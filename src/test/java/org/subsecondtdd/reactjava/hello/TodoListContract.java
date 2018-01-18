package org.subsecondtdd.reactjava.hello;

import org.junit.Test;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

public abstract class TodoListContract {
    @Test
    public void adds_todos() {
        TodoList todoList = makeTodoList();
        todoList.addTodo(new Todo("Get milk"));
        assertEquals(singletonList(new Todo("Get milk")), todoList.getTodos());
    }

    protected abstract TodoList makeTodoList();
}
