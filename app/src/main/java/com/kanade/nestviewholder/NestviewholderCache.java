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
    private LinkedList<Nestitemview<T>> bindingViews;

    public NestviewholderCache(Context context) {
        this(context, 7, 3);
    }

    public NestviewholderCache(Context context, int cacheSize, int initSize) {
        this.cacheSize = cacheSize;
        this.initSize = initSize;
        this.context = context;
        this.cache = new SparseArray<>();
        this.factorySparseArray = new SparseArray<>();
        this.bindingViews = new LinkedList<>();
    }

    public void registerFactory(int type, NestitemviewFactory<T> factory) {
        factorySparseArray.put(type, factory);
        LinkedList<Nestitemview<T>> linkedList = new LinkedList<>();
        for (int i = 0; i < initSize; i++) {
            linkedList.add(factory.create(context));
        }
        cache.put(type, linkedList);
    }

    public Nestitemview<T> getItemView(int type, int position) {
        LinkedList<Nestitemview<T>> list = cache.get(type);
        Nestitemview<T> itemView;
        if (list.isEmpty()) {
            NestitemviewFactory<T> factory = factorySparseArray.get(type);
            itemView = factory.create(context);
        } else {
            if (list.peekFirst().getItemView().getParent() == null) {
                itemView = list.pollFirst();
            } else if (list.peekLast().getItemView().getParent() == null) {
                itemView = list.pollLast();
            } else {
                NestitemviewFactory<T> factory = factorySparseArray.get(type);
                itemView = factory.create(context);
            }
        }
        itemView.setPosition(position);
        Nestitemview<T> lastItem = bindingViews.peekLast();
        if (lastItem == null || itemView.getPosition() >= lastItem.getPosition()) {
            bindingViews.addLast(itemView);
        } else {
            bindingViews.addFirst(itemView);
        }
        return itemView;
    }

    public void detachView(int position) {
        Nestitemview<T> lastItem = bindingViews.peekLast();
        if (position >= lastItem.getPosition()) {
            lastItem = bindingViews.pollLast();
        } else {
            lastItem = bindingViews.pollFirst();
        }
        lastItem.release();
        List<Nestitemview<T>> list = cache.get(lastItem.getType());
        if (list.size() < cacheSize) {
            cache.get(lastItem.getType()).add(lastItem);
        }
    }
}
