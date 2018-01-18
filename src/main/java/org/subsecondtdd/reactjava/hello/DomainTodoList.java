package org.subsecondtdd.reactjava.hello;

import java.util.ArrayList;
import java.util.List;

public class DomainTodoList implements TodoList {
    private List<Todo> todos = new ArrayList<>();

    @Override
    public void addTodo(Todo todo) {
        todos.add(todo);
    }

    @Override
    public List<Todo> getTodos() {
        return todos;
    }
}
