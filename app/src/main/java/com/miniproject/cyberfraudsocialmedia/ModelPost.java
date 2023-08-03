package com.miniproject.cyberfraudsocialmedia;

import android.graphics.Bitmap;

import java.io.File;

public class ModelPost {

    final private String name,time,text,id,docId,imageId;
    final private int dp;
    final private Bitmap image;

    public ModelPost(String name,String id,String docId,String imageId,String time, String text, Bitmap image, int dp) {
        this.name = name;
        this.id = id;
        this.docId = docId;
        this.imageId = imageId;
        this.time = time;
        this.text = text;
        this.image = image;
        this.dp = dp;
    }

    public String getDocId() {
        return docId;
    }

    public String getId() {
        return id;
    }

    public String getImageId() {
        return imageId;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getText() {
        return text;
    }

    public Bitmap getImage() {
        return image;
    }

    public int getDp() {
        return dp;
    }
}

