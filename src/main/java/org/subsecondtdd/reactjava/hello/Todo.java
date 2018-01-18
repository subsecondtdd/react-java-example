package org.subsecondtdd.reactjava.hello;

public class Todo {
    private final String text;

    public Todo(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Todo todo = (Todo) o;
        return text.equals(todo.text);
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }

    @Override
    public String toString() {
        return "Todo{" +
                "text='" + text + '\'' +
                '}';
    }
}
