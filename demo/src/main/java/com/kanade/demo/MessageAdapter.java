package com.kanade.demo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import com.kanade.nestviewholder.NestviewholderCache;
import com.kanade.nestviewholder.Nestitemview;
import com.kanade.nestviewholder.INestviewholder;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.BaseViewHolder> {
    private static final String TAG = "MessageAdapter";

    private static final int SEND = 476;
    private static final int RECEIVE = 323;

    private Context context;
    private NestviewholderCache<MessageItem> cache;
    private List<MessageItem> list;

    public MessageAdapter(Context context, List<MessageItem> list, NestviewholderCache<MessageItem> cache) {
        this.context = context;
        this.list = list;
        this.cache = cache;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        if (viewType == SEND) {
            view = inflater.inflate(R.layout.rv_item_send, parent, false);
            return new MessageSendViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.rv_item_receive, parent, false);
            return new MessageReceiveViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        MessageItem item = list.get(position);
        holder.setView(item);
        Nestitemview<MessageItem> itemView = cache.getItemView(item.getItemType(), position);
        itemView.dispatch(holder.itemView, item);
        holder.addChild(itemView.getItemView());
    }

    @Override
    public void onViewRecycled(BaseViewHolder holder) {
        super.onViewRecycled(holder);
        holder.removeChild();
        cache.detachView(holder.getAdapterPosition());
    }

    @Override
    public int getItemViewType(int position) {
        MessageItem item = list.get(position);
        if (LoginHelper.isLoginUser(item.getSenderId())) {
            return SEND;
        } else {
            return RECEIVE;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder implements INestviewholder {
        private FrameLayout frameLayout;

        public BaseViewHolder(View itemView) {
            super(itemView);
            frameLayout = (FrameLayout) itemView.findViewById(R.id.message_fl);
        }

        public void setView(MessageItem item) {

        }

        @Override
        public void addChild(View view) {
            frameLayout.addView(view);
        }

        @Override
        public View removeChild() {
            View view = frameLayout.getChildAt(0);
            frameLayout.removeViewAt(0);
            return view;
        }
    }

    public class MessageSendViewHolder extends BaseViewHolder {
        private TextView time;
        private ImageView avatar;
        private ImageView state;

        public MessageSendViewHolder(View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.message_time);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            state = (ImageView) itemView.findViewById(R.id.message_state);
        }

        @Override
        public void setView(MessageItem item) {
            time.setText("2017-09-01");
            state.setImageResource(R.drawable.success);
        }
    }

    public class MessageReceiveViewHolder extends BaseViewHolder {
        private TextView time;
        private ImageView avatar;
        private TextView sendername;

        public MessageReceiveViewHolder(View itemView) {
            super(itemView);
            time = (TextView) itemView.findViewById(R.id.message_time);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            sendername = (TextView) itemView.findViewById(R.id.sendername);
        }

        @Override
        public void setView(MessageItem item) {
            time.setText("2017-09-01");
            sendername.setText("Test");
        }
    }
}
