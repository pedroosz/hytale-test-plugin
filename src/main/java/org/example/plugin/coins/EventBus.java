package org.example.plugin.coins;

import java.util.function.Consumer;

public interface EventBus {

    <E> void subscribe(Class<E> eventType, Consumer<E> handler);

    void publish(Object event);
}
