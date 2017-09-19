package com.kanade.demo.message_view;

import android.view.View;
import android.widget.TextView;

import com.kanade.demo.MessageItem;
import com.kanade.demo.MessageType;
import com.kanade.nestviewholder.Nestitemview;


public class TextItemView extends Nestitemview<MessageItem> {
    private TextView textView;

    public TextItemView(View itemView) {
        super(itemView);
        textView = (TextView) itemView;
    }

    @Override
    public void dispatch(View itemView, MessageItem item) {
        textView.setText(item.getContent());
    }

    @Override
    public void release() {
        textView.setText("");
    }

    @Override
    public int getType() {
        return MessageType.TEXT;
    }
}
