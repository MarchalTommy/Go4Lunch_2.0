package com.aki.go4lunchv2.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Period implements Serializable {

    @SerializedName("close")
    @Expose
    private Close close;
    @SerializedName("open")
    @Expose
    private Open open;

    public Close getClose() {
        return close;
    }

    public void setClose(Close close) {
        this.close = close;
    }

    public Period withClose(Close close) {
        this.close = close;
        return this;
    }

    public Open getOpen() {
        return open;
    }

    public void setOpen(Open open) {
        this.open = open;
    }

    public Period withOpen(Open open) {
        this.open = open;
        return this;
    }

}
