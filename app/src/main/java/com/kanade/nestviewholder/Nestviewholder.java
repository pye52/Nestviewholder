package com.kanade.nestviewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

public abstract class Nestviewholder<T> extends RecyclerView.ViewHolder {
    protected Nestitemview<T> nestitemview;
    protected FrameLayout root;

    public Nestviewholder(View root) {
        super(root);
    }

    public void addChild(Nestitemview<T> nestitemview, T item) {
        this.nestitemview = nestitemview;
        nestitemview.dispatch(root, item);
        root.addView(nestitemview.getItemView());
    }

    public Nestitemview<T> removeChild() {
        root.removeView(nestitemview.getItemView());
        return nestitemview;
    }
}
