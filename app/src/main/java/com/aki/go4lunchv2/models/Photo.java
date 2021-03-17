
package com.aki.go4lunchv2.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Photo implements Serializable, Parcelable
{

    @SerializedName("height")
    @Expose
    private int height;
    @SerializedName("html_attributions")
    @Expose
    private List<String> htmlAttributions = null;
    @SerializedName("photo_reference")
    @Expose
    private String photoReference;
    @SerializedName("width")
    @Expose
    private int width;
    public final static Creator<Photo> CREATOR = new Creator<Photo>() {


        @SuppressWarnings({
            "unchecked"
        })
        public com.aki.go4lunchv2.models.Photo createFromParcel(Parcel in) {
            return new com.aki.go4lunchv2.models.Photo(in);
        }

        public com.aki.go4lunchv2.models.Photo[] newArray(int size) {
            return (new com.aki.go4lunchv2.models.Photo[size]);
        }

    }
    ;
    private final static long serialVersionUID = -1686983422509613046L;

    protected Photo(Parcel in) {
        this.height = ((int) in.readValue((int.class.getClassLoader())));
        in.readList(this.htmlAttributions, (String.class.getClassLoader()));
        this.photoReference = ((String) in.readValue((String.class.getClassLoader())));
        this.width = ((int) in.readValue((int.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public Photo() {
    }

    /**
     * 
     * @param htmlAttributions
     * @param photoReference
     * @param width
     * @param height
     */
    public Photo(int height, List<String> htmlAttributions, String photoReference, int width) {
        super();
        this.height = height;
        this.htmlAttributions = htmlAttributions;
        this.photoReference = photoReference;
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public com.aki.go4lunchv2.models.Photo withHeight(int height) {
        this.height = height;
        return this;
    }

    public List<String> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<String> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public com.aki.go4lunchv2.models.Photo withHtmlAttributions(List<String> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
        return this;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    public com.aki.go4lunchv2.models.Photo withPhotoReference(String photoReference) {
        this.photoReference = photoReference;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public com.aki.go4lunchv2.models.Photo withWidth(int width) {
        this.width = width;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(height);
        dest.writeList(htmlAttributions);
        dest.writeValue(photoReference);
        dest.writeValue(width);
    }

    public int describeContents() {
        return  0;
    }

}
