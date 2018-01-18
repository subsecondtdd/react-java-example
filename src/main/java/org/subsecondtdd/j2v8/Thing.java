package org.subsecondtdd.j2v8;

import java.util.Objects;

public class Thing {
    private final String name;

    public Thing(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Thing{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Thing thing = (Thing) o;
        return Objects.equals(name, thing.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
