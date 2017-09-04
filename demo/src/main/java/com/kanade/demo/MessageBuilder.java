package com.kanade.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MessageBuilder {
    public static List<MessageItem> createMessageList() {
        Random random = new Random();
        List<MessageItem> list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            int r = random.nextInt(2);
            int senderId = LoginHelper.loginUser;
            if (r == 1) {
                senderId = random.nextInt(senderId);
            }
            int type = MessageType.randomType();
            MessageItem item = createMessage(i, senderId, type, "测试" + i);
            list.add(item);
        }

        return list;
    }

    public static MessageItem createMessage(int id, int senderId, int itemType, String content) {
        MessageItem item = new MessageItem();
        item.setId(id);
        item.setSenderId(senderId);
        item.setItemType(itemType);
        item.setContent(content);
        return item;
    }
}
