package com.kanade.demo.message_view;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kanade.demo.MessageItem;
import com.kanade.nestviewholder.Nestitemview;
import com.kanade.nestviewholder.NestitemviewFactory;


public class TextItemViewFactory implements NestitemviewFactory<MessageItem> {
    @Override
    public Nestitemview<MessageItem> create(Context context) {
        TextView view = new TextView(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        view.setLayoutParams(params);
        return new TextItemView(view);
    }
}
