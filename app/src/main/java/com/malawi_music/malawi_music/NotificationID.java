package com.malawi_music.malawi_music;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by OWEN KALUNGWE on 04/09/2020.
 */

public class NotificationID {
    private final static AtomicInteger c = new AtomicInteger(0);
    public static int getID() {
        return c.incrementAndGet();
    }
}