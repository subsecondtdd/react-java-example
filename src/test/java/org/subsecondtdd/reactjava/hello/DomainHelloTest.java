package org.subsecondtdd.reactjava.hello;

public class DomainHelloTest extends HelloContract {
    @Override
    protected Hello makeHello() {
        return new DomainHello();
    }
}
