package com.kanade.demo;

import java.util.Random;

public class MessageType {
    public static int TEXT = 100;
    public static int IMG = 200;
    public static int AUDIO = 300;

    public static int randomType() {
        Random random = new Random();
        switch (random.nextInt(2)) {
            case 0:
                return TEXT;
            case 1:
                return IMG;
            case 2:
                return AUDIO;
            default:
                return TEXT;
        }
    }
}
