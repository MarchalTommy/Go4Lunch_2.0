
package com.aki.go4lunchv2.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Geometry implements Serializable, Parcelable
{

    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("viewport")
    @Expose
    private com.aki.go4lunchv2.models.Viewport viewport;
    public final static Creator<Geometry> CREATOR = new Creator<Geometry>() {


        @SuppressWarnings({
            "unchecked"
        })
        public com.aki.go4lunchv2.models.Geometry createFromParcel(Parcel in) {
            return new com.aki.go4lunchv2.models.Geometry(in);
        }

        public com.aki.go4lunchv2.models.Geometry[] newArray(int size) {
            return (new com.aki.go4lunchv2.models.Geometry[size]);
        }

    }
    ;
    private final static long serialVersionUID = -2724964726432730401L;

    protected Geometry(Parcel in) {
        this.location = ((Location) in.readValue((Location.class.getClassLoader())));
        this.viewport = ((com.aki.go4lunchv2.models.Viewport) in.readValue((com.aki.go4lunchv2.models.Viewport.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public Geometry() {
    }

    /**
     * 
     * @param viewport
     * @param location
     */
    public Geometry(Location location, com.aki.go4lunchv2.models.Viewport viewport) {
        super();
        this.location = location;
        this.viewport = viewport;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public com.aki.go4lunchv2.models.Geometry withLocation(Location location) {
        this.location = location;
        return this;
    }

    public com.aki.go4lunchv2.models.Viewport getViewport() {
        return viewport;
    }

    public void setViewport(com.aki.go4lunchv2.models.Viewport viewport) {
        this.viewport = viewport;
    }

    public com.aki.go4lunchv2.models.Geometry withViewport(com.aki.go4lunchv2.models.Viewport viewport) {
        this.viewport = viewport;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(location);
        dest.writeValue(viewport);
    }

    public int describeContents() {
        return  0;
    }

}
