package com.yinjiee.ausers.event;

public class GameTzEvent {
    private String message ;

    public GameTzEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
