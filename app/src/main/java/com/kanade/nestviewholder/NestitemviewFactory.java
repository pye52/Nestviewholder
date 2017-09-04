package com.kanade.nestviewholder;

import android.content.Context;

public interface NestitemviewFactory<T> {
    Nestitemview<T> create(Context context);
}
