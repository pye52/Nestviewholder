package com.kanade.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import com.kanade.demo.message_view.ImageItemViewFactory;
import com.kanade.demo.message_view.TextItemViewFactory;
import com.kanade.nestviewholder.NestviewholderCache;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NestviewholderCache<MessageItem> cache = new NestviewholderCache<>(this);
        cache.registerFactory(MessageType.TEXT, new TextItemViewFactory());
        cache.registerFactory(MessageType.IMG, new ImageItemViewFactory());

        List<MessageItem> list = MessageBuilder.createMessageList();
        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MessageAdapter adapter = new MessageAdapter(this, list, cache);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(list.size() - 1);
    }
}
