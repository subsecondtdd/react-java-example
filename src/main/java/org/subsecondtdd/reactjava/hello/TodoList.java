package org.subsecondtdd.reactjava.hello;

import java.util.List;

public interface TodoList {
    void addTodo(Todo todo);
    List<Todo> getTodos();
}
