package com.example.cotuong.network;

@FunctionalInterface
public interface PentaConsumer<T, U, V, W, X> {
    void accept(T t, U u, V v, W w, X x);
}