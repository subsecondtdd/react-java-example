package org.subsecondtdd.j2v8;

import com.eclipsesource.v8.NodeJS;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.V8Value;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.subsecondtdd.reactjava.hello.Todo;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Java - JavaScript interop
 * <p>
 * JavaScript -> Java is pass by value
 */
public class JsProxy {
    private final NodeJS node;
    private final Gson gson = new Gson();

    public JsProxy() {
        this(NodeJS.createNodeJS());
    }

    public JsProxy(NodeJS node) {
        this.node = node;
    }

    public <T> T create(String path, Class<T> clazz, Object... ctorArgs) {
        V8Object JsConstructor = node.require(new File(path));
        V8Object Class = node.require(new File("src/main/javascript/class.js"));

        V8Array ctorArguments = new V8Array(node.getRuntime());
        ctorArguments.push(JsConstructor);
        for (Object ctorArg : ctorArgs) {
            ctorArguments.push(convert(ctorArg));
        }
        V8Object target = Class.executeObjectFunction("instantiate", ctorArguments);

        InvocationHandler handler = (proxy, method, methodArgs) -> {
            V8Array methodArguments = new V8Array(node.getRuntime());
            if (methodArgs != null) {
                for (Object methodArg : methodArgs) {
                    methodArguments.push(convert(methodArg));
                }
            }
            java.lang.Class<?> returnType = method.getReturnType();
            if (returnType == String.class) {
                return target.executeStringFunction(method.getName(), methodArguments);
            }
            if (returnType == Integer.class || returnType == Integer.TYPE) {
                return target.executeIntegerFunction(method.getName(), methodArguments);
            }
            if (returnType == Void.class || returnType == Void.TYPE) {
                target.executeFunction(method.getName(), methodArguments);
                return null;
            }
            if (returnType.isAssignableFrom(List.class)) {
                Object o = target.executeFunction(method.getName(), methodArguments);
                if (!(o instanceof V8Array))
                    throw new RuntimeException(String.format("Expected %s %s to return Array", path, method.getName()));
                V8Array result = (V8Array) o;
                List<Object> resultList = new ArrayList<>();
                for (int i = 0; i < result.length(); i++) {
                    V8Object listItem = result.getObject(i);
                    Map<String, Object> map = toMap(listItem);
                    String json = gson.toJson(map);
                    Object javaObject = gson.fromJson(json, Todo.class);
                    resultList.add(javaObject);
                }
                return resultList;
            }

            throw new RuntimeException("Don't yet support return type: " + returnType);
        };
        return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{clazz}, handler);
    }

    private Map<String, Object> toMap(V8Object v8Object) {
        Map<String, Object> map = new HashMap<>();
        for (String key : v8Object.getKeys()) {
            Object o = v8Object.get(key);
            map.put(key, o);
        }
        return map;
    }

    private Object convert(Object object) {
        if (object == null) return null;
        if (object instanceof String) return object;
        V8Object proxy = new V8Object(node.getRuntime());
        Method[] methods = object.getClass().getDeclaredMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();
            proxy.registerJavaMethod((receiver, parameters) -> {
                Object[] javaArgs = new Object[parameters.length()];
                for (int i = 0; i < parameters.length(); i++) {
                    Object jsObject = parameters.get(i);
                    if (parameterTypes[i].isAssignableFrom(jsObject.getClass())) {
                        // pass by reference
                        javaArgs[i] = jsObject;
                    } else {
                        // pass by value, after conversion via JSON
                        Object o = jsToJavaPrimitives(jsObject);
                        String json = gson.toJson(o);
                        javaArgs[i] = gson.fromJson(json, parameterTypes[i]);
                    }
                }
                try {
                    Object result = method.invoke(object, javaArgs);
                    return javaToJs(result);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e.getCause());
                }
            }, methodName);
        }
        return proxy;
    }

    private Object javaToJs(Object javaObject) {
        if (javaObject == null) return null;
        if (javaObject instanceof String) return javaObject;
        if (javaObject instanceof List) {
            V8Array v8Array = new V8Array(node.getRuntime());
            List list = (List) javaObject;
            for (Object object : list) {
                v8Array.push(javaToJs(object));
            }
            return v8Array;
        }
        return toV8Object(gson.toJsonTree(javaObject));
//
//        throw new RuntimeException(String.format("Can't yet convert Java %s (%s) to JavaScript", javaObject, javaObject.getClass()));
    }

    private Object toV8Object(JsonElement jsonElement) {
        if (jsonElement instanceof JsonObject) {
            V8Object v8Object = new V8Object(node.getRuntime());
            JsonObject jsonObject = (JsonObject) jsonElement;
            for (Map.Entry<String, JsonElement> keyValue : jsonObject.entrySet()) {
                Object o = toV8Object(keyValue.getValue());
                if (o instanceof Integer) {
                    v8Object.add(keyValue.getKey(), (Integer) o);
                } else if (o instanceof Boolean) {
                    v8Object.add(keyValue.getKey(), (Boolean) o);
                } else if (o instanceof Double) {
                    v8Object.add(keyValue.getKey(), (Double) o);
                } else if (o instanceof String) {
                    v8Object.add(keyValue.getKey(), (String) o);
                } else if (o instanceof V8Value) {
                    v8Object.add(keyValue.getKey(), (V8Value) o);
                } else {
                    throw new RuntimeException("Unexcpected type");
                }
            }
            return v8Object;
        } else if (jsonElement instanceof JsonArray) {
            V8Array v8Array = new V8Array(node.getRuntime());
            JsonArray jsonArray = (JsonArray) jsonElement;
            for (JsonElement element : jsonArray) {
                Object o = toV8Object(element);
                v8Array.push(o);
            }
            return v8Array;
        } else if (jsonElement instanceof JsonNull) {
            return null;
        } else if (jsonElement instanceof JsonPrimitive) {
            JsonPrimitive jsonPrimitive = (JsonPrimitive) jsonElement;
            if (jsonPrimitive.isBoolean()) {
                return jsonElement.getAsBoolean();
            } else if (jsonPrimitive.isNumber()) {
                return jsonElement.getAsInt();
            } else if (jsonPrimitive.isString()) {
                return jsonElement.getAsString();
            } else {
                throw new RuntimeException("Unexcpected type");
            }
        } else {
            throw new RuntimeException("Unexcpected type");
        }
    }

    private Object jsToJavaPrimitives(Object jsObject) {
        if (jsObject == null) return null;
        if (jsObject instanceof String) return jsObject;
        if (jsObject instanceof V8Object) {
            V8Object v8Object = (V8Object) jsObject;
            Map<String, Object> map = new HashMap<>();
            for (String key : v8Object.getKeys()) {
                Object o = v8Object.get(key);
                map.put(key, jsToJavaPrimitives(o));
            }
            return map;
        }
        throw new RuntimeException(String.format("Can't yet convert JavaScript %s (%s) to Java primitives", jsObject, jsObject.getClass()));
    }
}
