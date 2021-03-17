
package com.aki.go4lunchv2.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Restaurant implements Serializable, Parcelable
{

    @SerializedName("html_attributions")
    @Expose
    private List<Object> htmlAttributions = null;
    @SerializedName("next_page_token")
    @Expose
    private String nextPageToken;
    @SerializedName("results")
    @Expose
    private List<com.aki.go4lunchv2.models.Result> results = null;
    @SerializedName("status")
    @Expose
    private String status;
    public final static Creator<Restaurant> CREATOR = new Creator<Restaurant>() {


        @SuppressWarnings({
            "unchecked"
        })
        public com.aki.go4lunchv2.models.Restaurant createFromParcel(Parcel in) {
            return new com.aki.go4lunchv2.models.Restaurant(in);
        }

        public com.aki.go4lunchv2.models.Restaurant[] newArray(int size) {
            return (new com.aki.go4lunchv2.models.Restaurant[size]);
        }

    }
    ;
    private final static long serialVersionUID = -2166576751259338167L;

    protected Restaurant(Parcel in) {
        in.readList(this.htmlAttributions, (Object.class.getClassLoader()));
        this.nextPageToken = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.results, (com.aki.go4lunchv2.models.Result.class.getClassLoader()));
        this.status = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public Restaurant() {
    }

    /**
     * 
     * @param htmlAttributions
     * @param nextPageToken
     * @param results
     * @param status
     */
    public Restaurant(List<Object> htmlAttributions, String nextPageToken, List<com.aki.go4lunchv2.models.Result> results, String status) {
        super();
        this.htmlAttributions = htmlAttributions;
        this.nextPageToken = nextPageToken;
        this.results = results;
        this.status = status;
    }

    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public com.aki.go4lunchv2.models.Restaurant withHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
        return this;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public com.aki.go4lunchv2.models.Restaurant withNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
        return this;
    }

    public List<com.aki.go4lunchv2.models.Result> getResults() {
        return results;
    }

    public void setResults(List<com.aki.go4lunchv2.models.Result> results) {
        this.results = results;
    }

    public com.aki.go4lunchv2.models.Restaurant withResults(List<com.aki.go4lunchv2.models.Result> results) {
        this.results = results;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public com.aki.go4lunchv2.models.Restaurant withStatus(String status) {
        this.status = status;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(htmlAttributions);
        dest.writeValue(nextPageToken);
        dest.writeList(results);
        dest.writeValue(status);
    }

    public int describeContents() {
        return  0;
    }

}
