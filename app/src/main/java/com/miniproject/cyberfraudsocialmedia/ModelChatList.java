package com.miniproject.cyberfraudsocialmedia;

public class ModelChatList {

    private final String name,lm,id;

    public ModelChatList(String name, String lm,String id) {
        this.name = name;
        this.lm = lm;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getLm() {
        return lm;
    }

    public String getId() {
        return id;
    }
}
