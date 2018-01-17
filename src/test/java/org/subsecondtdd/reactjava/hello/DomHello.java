package org.subsecondtdd.reactjava.hello;

import com.eclipsesource.v8.NodeJS;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;

import java.io.File;

// TODO: Generate this class from Hello.java and DomHello.js - it's only an adapter.
// Alternatively, create a superclass that does the heavy lifting.
public class DomHello implements Hello {
    private final NodeJS node = NodeJS.createNodeJS();
    private final V8Object target;

    public DomHello(Hello hello) {
        V8Object DomHello = node.require(new File("src/test/java/org/subsecondtdd/reactjava/hello/DomHello.js"));

        V8Object object = node.getRuntime().getObject("Object");
        V8Array arguments = new V8Array(node.getRuntime());
        arguments.push(DomHello);

        V8Object proxy = new V8Object(node.getRuntime());
        proxy.registerJavaMethod(hello, "hello", "hello", new Class[0]);
        arguments.push(proxy);

        target = object.executeObjectFunction("spawn", arguments);
    }

    @Override
    public String hello() {
        return target.executeStringFunction("hello", new V8Array(node.getRuntime()));
    }
}
