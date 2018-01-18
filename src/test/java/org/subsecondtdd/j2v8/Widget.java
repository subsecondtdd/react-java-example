package org.subsecondtdd.j2v8;

public interface Widget {
    String description(String name);
    String id();
    int wobble();
    int takeWobble(Wobble wobble);
}
