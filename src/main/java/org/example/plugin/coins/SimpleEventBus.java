package org.example.plugin.coins;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class SimpleEventBus implements EventBus {

    private final Map<Class<?>, List<Consumer<?>>> handlers = new ConcurrentHashMap<>();

    @Override
    public <E> void subscribe(Class<E> eventType, Consumer<E> handler) {
        handlers.computeIfAbsent(eventType, ignored -> new CopyOnWriteArrayList<>()).add(handler);
    }

    @Override
    public void publish(Object event) {
        if (event == null) {
            return;
        }

        List<Consumer<?>> list = handlers.get(event.getClass());
        if (list == null) {
            return;
        }

        for (Consumer<?> handler : list) {
            @SuppressWarnings("unchecked")
            Consumer<Object> cast = (Consumer<Object>) handler;
            cast.accept(event);
        }
    }
}
