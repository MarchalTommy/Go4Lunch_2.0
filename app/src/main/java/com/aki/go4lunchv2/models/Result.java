package com.aki.go4lunchv2.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Result implements Serializable, Parcelable {

    public final static Creator<Result> CREATOR = new Creator<Result>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        public Result[] newArray(int size) {
            return (new Result[size]);
        }

    };
    private final static long serialVersionUID = 8176040751922654786L;
    @SerializedName("business_status")
    @Expose
    private String businessStatus;
    @SerializedName("geometry")
    @Expose
    private Geometry geometry;
    @SerializedName("icon")
    @Expose
    private String icon;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("opening_hours")
    @Expose
    private OpeningHours openingHours;
    @SerializedName("photos")
    @Expose
    private List<Photo> photos = null;
    @SerializedName("place_id")
    @Expose
    private String placeId;
    @SerializedName("plus_code")
    @Expose
    private PlusCode plusCode;
    @SerializedName("price_level")
    @Expose
    private int priceLevel;
    @SerializedName("rating")
    @Expose
    private double rating;
    @SerializedName("reference")
    @Expose
    private String reference;
    @SerializedName("scope")
    @Expose
    private String scope;
    @SerializedName("types")
    @Expose
    private List<String> types = null;
    @SerializedName("user_ratings_total")
    @Expose
    private int userRatingsTotal;
    @SerializedName("vicinity")
    @Expose
    private String vicinity;
    @SerializedName("permanently_closed")
    @Expose
    private boolean permanentlyClosed;

    protected Result(Parcel in) {
        this.businessStatus = ((String) in.readValue((String.class.getClassLoader())));
        this.geometry = ((Geometry) in.readValue((Geometry.class.getClassLoader())));
        this.icon = ((String) in.readValue((String.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.openingHours = ((OpeningHours) in.readValue((OpeningHours.class.getClassLoader())));
        in.readList(this.photos, (Photo.class.getClassLoader()));
        this.placeId = ((String) in.readValue((String.class.getClassLoader())));
        this.plusCode = ((PlusCode) in.readValue((PlusCode.class.getClassLoader())));
        this.priceLevel = ((int) in.readValue((int.class.getClassLoader())));
        this.rating = ((double) in.readValue((double.class.getClassLoader())));
        this.reference = ((String) in.readValue((String.class.getClassLoader())));
        this.scope = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.types, (String.class.getClassLoader()));
        this.userRatingsTotal = ((int) in.readValue((int.class.getClassLoader())));
        this.vicinity = ((String) in.readValue((String.class.getClassLoader())));
        this.permanentlyClosed = ((boolean) in.readValue((boolean.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     */
    public Result() {
    }

    /**
     * @param types
     * @param plusCode
     * @param icon
     * @param placeId
     * @param rating
     * @param userRatingsTotal
     * @param businessStatus
     * @param priceLevel
     * @param photos
     * @param reference
     * @param permanentlyClosed
     * @param scope
     * @param name
     * @param geometry
     * @param openingHours
     * @param vicinity
     */
    public Result(String businessStatus, Geometry geometry, String icon, String name, OpeningHours openingHours, List<Photo> photos, String placeId, PlusCode plusCode, int priceLevel, double rating, String reference, String scope, List<String> types, int userRatingsTotal, String vicinity, boolean permanentlyClosed) {
        super();
        this.businessStatus = businessStatus;
        this.geometry = geometry;
        this.icon = icon;
        this.name = name;
        this.openingHours = openingHours;
        this.photos = photos;
        this.placeId = placeId;
        this.plusCode = plusCode;
        this.priceLevel = priceLevel;
        this.rating = rating;
        this.reference = reference;
        this.scope = scope;
        this.types = types;
        this.userRatingsTotal = userRatingsTotal;
        this.vicinity = vicinity;
        this.permanentlyClosed = permanentlyClosed;
    }

    public String getBusinessStatus() {
        return businessStatus;
    }

    public void setBusinessStatus(String businessStatus) {
        this.businessStatus = businessStatus;
    }

    public Result withBusinessStatus(String businessStatus) {
        this.businessStatus = businessStatus;
        return this;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Result withGeometry(Geometry geometry) {
        this.geometry = geometry;
        return this;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Result withIcon(String icon) {
        this.icon = icon;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Result withName(String name) {
        this.name = name;
        return this;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(OpeningHours openingHours) {
        this.openingHours = openingHours;
    }

    public Result withOpeningHours(OpeningHours openingHours) {
        this.openingHours = openingHours;
        return this;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public Result withPhotos(List<Photo> photos) {
        this.photos = photos;
        return this;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public Result withPlaceId(String placeId) {
        this.placeId = placeId;
        return this;
    }

    public PlusCode getPlusCode() {
        return plusCode;
    }

    public void setPlusCode(PlusCode plusCode) {
        this.plusCode = plusCode;
    }

    public Result withPlusCode(PlusCode plusCode) {
        this.plusCode = plusCode;
        return this;
    }

    public int getPriceLevel() {
        return priceLevel;
    }

    public void setPriceLevel(int priceLevel) {
        this.priceLevel = priceLevel;
    }

    public Result withPriceLevel(int priceLevel) {
        this.priceLevel = priceLevel;
        return this;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Result withRating(double rating) {
        this.rating = rating;
        return this;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Result withReference(String reference) {
        this.reference = reference;
        return this;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Result withScope(String scope) {
        this.scope = scope;
        return this;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public Result withTypes(List<String> types) {
        this.types = types;
        return this;
    }

    public int getUserRatingsTotal() {
        return userRatingsTotal;
    }

    public void setUserRatingsTotal(int userRatingsTotal) {
        this.userRatingsTotal = userRatingsTotal;
    }

    public Result withUserRatingsTotal(int userRatingsTotal) {
        this.userRatingsTotal = userRatingsTotal;
        return this;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public Result withVicinity(String vicinity) {
        this.vicinity = vicinity;
        return this;
    }

    public boolean isPermanentlyClosed() {
        return permanentlyClosed;
    }

    public void setPermanentlyClosed(boolean permanentlyClosed) {
        this.permanentlyClosed = permanentlyClosed;
    }

    public Result withPermanentlyClosed(boolean permanentlyClosed) {
        this.permanentlyClosed = permanentlyClosed;
        return this;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(businessStatus);
        dest.writeValue(geometry);
        dest.writeValue(icon);
        dest.writeValue(name);
        dest.writeValue(openingHours);
        dest.writeList(photos);
        dest.writeValue(placeId);
        dest.writeValue(plusCode);
        dest.writeValue(priceLevel);
        dest.writeValue(rating);
        dest.writeValue(reference);
        dest.writeValue(scope);
        dest.writeList(types);
        dest.writeValue(userRatingsTotal);
        dest.writeValue(vicinity);
        dest.writeValue(permanentlyClosed);
    }

    public int describeContents() {
        return 0;
    }

}
