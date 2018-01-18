package org.subsecondtdd.reactjava.hello;

public class DomainTodoListTest extends TodoListContract {
    @Override
    protected TodoList makeTodoList() {
        return new DomainTodoList();
    }
}
