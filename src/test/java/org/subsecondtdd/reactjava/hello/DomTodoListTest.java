package org.subsecondtdd.reactjava.hello;

import org.subsecondtdd.j2v8.JsProxy;

public class DomTodoListTest extends TodoListContract {
    @Override
    protected TodoList makeTodoList() {
        return new JsProxy().create("js/test/DomTodoList.js", TodoList.class, new DomainTodoList());
    }
}
