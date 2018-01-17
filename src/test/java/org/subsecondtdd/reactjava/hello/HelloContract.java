package org.subsecondtdd.reactjava.hello;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public abstract class HelloContract {
    @Test
    public void says_hello() {
        Hello hello = makeHello();
        assertEquals("hello", hello.hello());
    }

    protected abstract Hello makeHello();
}
