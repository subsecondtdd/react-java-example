package org.subsecondtdd.j2v8;

import com.eclipsesource.v8.NodeJS;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.google.gson.Gson;
import org.subsecondtdd.reactjava.hello.Todo;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                    Map<String,Object> map = toMap(listItem);
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

    private Map<String,Object> toMap(V8Object v8Object) {
        Map<String,Object> map = new HashMap<>();
        for(String key : v8Object.getKeys()) {
            Object o = v8Object.get(key);
            map.put(key, o);
        }
        return map;
    }

    private Object convert(Object object) {
        if (object instanceof String) return object;
        V8Object proxy = new V8Object(node.getRuntime());
        Method[] methods = object.getClass().getDeclaredMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();
            proxy.registerJavaMethod(object, methodName, methodName, parameterTypes);
        }
        return proxy;
    }
}
