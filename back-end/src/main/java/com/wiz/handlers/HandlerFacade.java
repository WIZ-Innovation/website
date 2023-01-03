package com.wiz.handlers;

public class HandlerFacade {
    private static volatile HandlerFacade instance;

    public static HandlerFacade getInstance() {
        if (instance == null) {
            synchronized (HandlerFacade.class) {
                if (instance == null) {
                    instance = new HandlerFacade();
                }
            }
        }

        return instance;
    }

    private HandlerFacade() {};
}
