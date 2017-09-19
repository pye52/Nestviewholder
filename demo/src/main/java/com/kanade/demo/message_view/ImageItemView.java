package com.kanade.demo.message_view;

import android.view.View;
import android.widget.ImageView;

import com.kanade.demo.MessageItem;
import com.kanade.demo.MessageType;
import com.kanade.demo.R;
import com.kanade.nestviewholder.Nestitemview;

public class ImageItemView extends Nestitemview<MessageItem> {
    private ImageView imageView;

    public ImageItemView(View itemView) {
        super(itemView);
        this.imageView = (ImageView) itemView;
    }

    @Override
    public int getType() {
        return MessageType.IMG;
    }

    @Override
    public void dispatch(View itemView, MessageItem item) {
        imageView.setImageResource(R.drawable.img);
    }

    @Override
    public void release() {
        imageView.setImageResource(0);
    }
}
