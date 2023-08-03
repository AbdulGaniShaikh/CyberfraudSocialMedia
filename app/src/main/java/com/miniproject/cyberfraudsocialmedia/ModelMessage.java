package com.miniproject.cyberfraudsocialmedia;

public class ModelMessage {

    final private String name,time,message;

    public ModelMessage(String name, String time, String message) {
        this.name = name;
        this.time = time;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }
}
