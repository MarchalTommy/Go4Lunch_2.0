
package com.aki.go4lunchv2.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OpeningHours implements Serializable, Parcelable
{

    @SerializedName("open_now")
    @Expose
    private boolean openNow;
    public final static Creator<OpeningHours> CREATOR = new Creator<OpeningHours>() {


        @SuppressWarnings({
            "unchecked"
        })
        public com.aki.go4lunchv2.models.OpeningHours createFromParcel(Parcel in) {
            return new com.aki.go4lunchv2.models.OpeningHours(in);
        }

        public com.aki.go4lunchv2.models.OpeningHours[] newArray(int size) {
            return (new com.aki.go4lunchv2.models.OpeningHours[size]);
        }

    }
    ;
    private final static long serialVersionUID = -598349803154801758L;

    protected OpeningHours(Parcel in) {
        this.openNow = ((boolean) in.readValue((boolean.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public OpeningHours() {
    }

    /**
     * 
     * @param openNow
     */
    public OpeningHours(boolean openNow) {
        super();
        this.openNow = openNow;
    }

    public boolean isOpenNow() {
        return openNow;
    }

    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }

    public com.aki.go4lunchv2.models.OpeningHours withOpenNow(boolean openNow) {
        this.openNow = openNow;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(openNow);
    }

    public int describeContents() {
        return  0;
    }

    public String toString() {
        return (openNow ? "Open" : "Closed");
    }

}
