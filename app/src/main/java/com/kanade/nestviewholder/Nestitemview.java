package com.kanade.nestviewholder;

import android.view.View;

public abstract class Nestitemview<T> {
    private View itemView;

    public Nestitemview(View itemView) {
        this.itemView = itemView;
    }

    public abstract int getType();

    public abstract void dispatch(View root, T item);

    public final View getItemView() {
        return itemView;
    }
}
