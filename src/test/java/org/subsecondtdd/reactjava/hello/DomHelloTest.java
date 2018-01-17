package org.subsecondtdd.reactjava.hello;

import org.subsecondtdd.j2v8.JsProxy;

public class DomHelloTest extends HelloContract {
    @Override
    protected Hello makeHello() {
        return new JsProxy().create("js/test/DomHello.js", Hello.class, new DomainHello());
    }
}
