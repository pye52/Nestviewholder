package com.kanade.nestviewholder;

public interface INestviewholder<T> {
    void addChild(Nestitemview<T> nestitemview, T item);

    Nestitemview<T> removeChild();
}
