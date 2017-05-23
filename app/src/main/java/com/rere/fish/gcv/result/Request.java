package com.rere.fish.gcv.result;

/**
 * Created by Android dev on 5/23/17.
 */

public class Request {
    String image;
    String request;

    Request(String image, String request) {
        this.image = image;
        this.request = request;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }
}
