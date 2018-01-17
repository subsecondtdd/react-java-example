package org.subsecondtdd.reactjava.hello;

public class DomHelloTest extends HelloContract {
    @Override
    protected Hello makeHello() {
        return new DomHello(new DomainHello());
    }
}
