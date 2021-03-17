
package com.aki.go4lunchv2.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PlusCode implements Serializable, Parcelable
{

    @SerializedName("compound_code")
    @Expose
    private String compoundCode;
    @SerializedName("global_code")
    @Expose
    private String globalCode;
    public final static Creator<PlusCode> CREATOR = new Creator<PlusCode>() {


        @SuppressWarnings({
            "unchecked"
        })
        public com.aki.go4lunchv2.models.PlusCode createFromParcel(Parcel in) {
            return new com.aki.go4lunchv2.models.PlusCode(in);
        }

        public com.aki.go4lunchv2.models.PlusCode[] newArray(int size) {
            return (new com.aki.go4lunchv2.models.PlusCode[size]);
        }

    }
    ;
    private final static long serialVersionUID = 2590436807104392248L;

    protected PlusCode(Parcel in) {
        this.compoundCode = ((String) in.readValue((String.class.getClassLoader())));
        this.globalCode = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public PlusCode() {
    }

    /**
     * 
     * @param globalCode
     * @param compoundCode
     */
    public PlusCode(String compoundCode, String globalCode) {
        super();
        this.compoundCode = compoundCode;
        this.globalCode = globalCode;
    }

    public String getCompoundCode() {
        return compoundCode;
    }

    public void setCompoundCode(String compoundCode) {
        this.compoundCode = compoundCode;
    }

    public com.aki.go4lunchv2.models.PlusCode withCompoundCode(String compoundCode) {
        this.compoundCode = compoundCode;
        return this;
    }

    public String getGlobalCode() {
        return globalCode;
    }

    public void setGlobalCode(String globalCode) {
        this.globalCode = globalCode;
    }

    public com.aki.go4lunchv2.models.PlusCode withGlobalCode(String globalCode) {
        this.globalCode = globalCode;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(compoundCode);
        dest.writeValue(globalCode);
    }

    public int describeContents() {
        return  0;
    }

}
