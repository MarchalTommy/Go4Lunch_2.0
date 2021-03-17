
package com.aki.go4lunchv2.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Viewport implements Serializable, Parcelable
{

    @SerializedName("northeast")
    @Expose
    private Northeast northeast;
    @SerializedName("southwest")
    @Expose
    private com.aki.go4lunchv2.models.Southwest southwest;
    public final static Creator<Viewport> CREATOR = new Creator<Viewport>() {


        @SuppressWarnings({
            "unchecked"
        })
        public com.aki.go4lunchv2.models.Viewport createFromParcel(Parcel in) {
            return new com.aki.go4lunchv2.models.Viewport(in);
        }

        public com.aki.go4lunchv2.models.Viewport[] newArray(int size) {
            return (new com.aki.go4lunchv2.models.Viewport[size]);
        }

    }
    ;
    private final static long serialVersionUID = 9060506550665584306L;

    protected Viewport(Parcel in) {
        this.northeast = ((Northeast) in.readValue((Northeast.class.getClassLoader())));
        this.southwest = ((com.aki.go4lunchv2.models.Southwest) in.readValue((com.aki.go4lunchv2.models.Southwest.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public Viewport() {
    }

    /**
     * 
     * @param southwest
     * @param northeast
     */
    public Viewport(Northeast northeast, com.aki.go4lunchv2.models.Southwest southwest) {
        super();
        this.northeast = northeast;
        this.southwest = southwest;
    }

    public Northeast getNortheast() {
        return northeast;
    }

    public void setNortheast(Northeast northeast) {
        this.northeast = northeast;
    }

    public com.aki.go4lunchv2.models.Viewport withNortheast(Northeast northeast) {
        this.northeast = northeast;
        return this;
    }

    public com.aki.go4lunchv2.models.Southwest getSouthwest() {
        return southwest;
    }

    public void setSouthwest(com.aki.go4lunchv2.models.Southwest southwest) {
        this.southwest = southwest;
    }

    public com.aki.go4lunchv2.models.Viewport withSouthwest(com.aki.go4lunchv2.models.Southwest southwest) {
        this.southwest = southwest;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(northeast);
        dest.writeValue(southwest);
    }

    public int describeContents() {
        return  0;
    }

}
