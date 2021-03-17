
package com.aki.go4lunchv2.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Northeast implements Serializable, Parcelable
{

    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("lng")
    @Expose
    private double lng;
    public final static Creator<Northeast> CREATOR = new Creator<Northeast>() {


        @SuppressWarnings({
            "unchecked"
        })
        public com.aki.go4lunchv2.models.Northeast createFromParcel(Parcel in) {
            return new com.aki.go4lunchv2.models.Northeast(in);
        }

        public com.aki.go4lunchv2.models.Northeast[] newArray(int size) {
            return (new com.aki.go4lunchv2.models.Northeast[size]);
        }

    }
    ;
    private final static long serialVersionUID = -3272461646940721968L;

    protected Northeast(Parcel in) {
        this.lat = ((double) in.readValue((double.class.getClassLoader())));
        this.lng = ((double) in.readValue((double.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public Northeast() {
    }

    /**
     * 
     * @param lng
     * @param lat
     */
    public Northeast(double lat, double lng) {
        super();
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public com.aki.go4lunchv2.models.Northeast withLat(double lat) {
        this.lat = lat;
        return this;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public com.aki.go4lunchv2.models.Northeast withLng(double lng) {
        this.lng = lng;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(lat);
        dest.writeValue(lng);
    }

    public int describeContents() {
        return  0;
    }

}
