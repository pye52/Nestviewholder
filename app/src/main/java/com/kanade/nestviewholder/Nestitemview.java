package com.kanade.nestviewholder;

import android.view.View;

public abstract class Nestitemview<T> {
    private int position;
    private View itemView;

    public Nestitemview(View itemView) {
        this.itemView = itemView;
    }

    public abstract int getType();

    public abstract void dispatch(View root, T item);

    public abstract void release();

    public final View getItemView() {
        return itemView;
    }

    public final int getPosition() {
        return position;
    }

    public final void setPosition(int position) {
        this.position = position;
    }
}
