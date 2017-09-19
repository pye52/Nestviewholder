package com.kanade.nestviewholder;

import android.content.Context;
import android.util.SparseArray;

import java.util.LinkedList;
import java.util.List;

public class NestviewholderCache<T> {
    private int cacheSize;
    private int initSize;

    private Context context;
    private SparseArray<NestitemviewFactory<T>> factorySparseArray;
    private SparseArray<LinkedList<Nestitemview<T>>> cache;
    private SparseArray<Nestitemview<T>> bindingViews;

    public NestviewholderCache(Context context) {
        this(context, 7, 3);
    }

    public NestviewholderCache(Context context, int cacheSize, int initSize) {
        this.cacheSize = cacheSize;
        this.initSize = initSize;
        this.context = context;
        this.cache = new SparseArray<>();
        this.factorySparseArray = new SparseArray<>();
        this.bindingViews = new SparseArray<>();
    }

    public void registerFactory(int type, NestitemviewFactory<T> factory) {
        factorySparseArray.put(type, factory);
        LinkedList<Nestitemview<T>> linkedList = new LinkedList<>();
        for (int i = 0; i < initSize; i++) {
            linkedList.add(factory.create(context));
        }
        cache.put(type, linkedList);
    }

    public Nestitemview<T> getItemView(int type, Nestviewholder<T> holder) {
        LinkedList<Nestitemview<T>> list = cache.get(type);
        Nestitemview<T> nestitemview;
        if (list.isEmpty()) {
            NestitemviewFactory<T> factory = factorySparseArray.get(type);
            nestitemview = factory.create(context);
        } else {
            if (list.peekFirst().getItemView().getParent() == null) {
                nestitemview = list.pollFirst();
            } else if (list.peekLast().getItemView().getParent() == null) {
                nestitemview = list.pollLast();
            } else {
                NestitemviewFactory<T> factory = factorySparseArray.get(type);
                nestitemview = factory.create(context);
            }
        }
        bindingViews.put(holder.getAdapterPosition(), nestitemview);
        return nestitemview;
    }

    public void detachView(Nestviewholder<T> holder) {
        holder.removeChild();
        Nestitemview<T> nestitemview = bindingViews.get(holder.getAdapterPosition());
        if (nestitemview != null) {
            List<Nestitemview<T>> list = cache.get(nestitemview.getType());
            if (list.size() < cacheSize) {
                cache.get(nestitemview.getType()).add(nestitemview);
            }
        }
    }
}
