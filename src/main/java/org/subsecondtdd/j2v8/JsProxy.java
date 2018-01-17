package org.subsecondtdd.j2v8;

import com.eclipsesource.v8.NodeJS;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JsProxy {
    private final NodeJS node;

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
            if (method.getReturnType() == String.class) {
                return target.executeStringFunction(method.getName(), methodArguments);
            }
            if (method.getReturnType() == Integer.class || method.getReturnType() == Integer.TYPE) {
                return target.executeIntegerFunction(method.getName(), methodArguments);
            }
            throw new RuntimeException("Don't yet support return type: " + method.getReturnType());
        };
        return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{clazz}, handler);
    }

    private Object convert(Object object) {
        if (object instanceof String) return object;
        V8Object proxy = new V8Object(node.getRuntime());
        Method[] methods = object.getClass().getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            proxy.registerJavaMethod(object, methodName, methodName, method.getParameterTypes());
        }
        return proxy;
    }
}
