package com.tourism.apps;

import android.os.Parcel;
import android.os.Parcelable;

public class Place implements Parcelable{
    private String placeName;
    private String rating;
    private String imageUrl;
    private String eTiketLink;
    private double latitude;
    private double longitude;
    private String officialWebsite;
    private String placeAddress;

    public Place() {
        // Default constructor required for Firebase
    }

    public Place(String placeName, String rating, String imageUrl, String eTiketLink,
                 double latitude, double longitude, String officialWebsite, String placeAddress) {
        this.placeName = placeName;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.eTiketLink = eTiketLink;
        this.latitude = latitude;
        this.longitude = longitude;
        this.officialWebsite = officialWebsite;
        this.placeAddress = placeAddress;
    }
    // Parcelable implementation
    protected Place(Parcel in) {
        placeName = in.readString();
        rating = in.readString();
        officialWebsite = in.readString();
        eTiketLink = in.readString();
        placeAddress = in.readString();
        if (in.readByte() == 0) {
            latitude = 0.0;
        } else {
            latitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            longitude = 0.0;
        } else {
            longitude = in.readDouble();
        }
        imageUrl = in.readString();
    }

    public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel in) {
            return new Place(in);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(placeName);
        dest.writeString(rating);
        dest.writeString(officialWebsite);
        dest.writeString(eTiketLink);
        dest.writeString(placeAddress);
        if (latitude != 0.0) {
            dest.writeByte((byte) 1);
            dest.writeDouble(latitude);
        } else {
            dest.writeByte((byte) 0);
        }
        if (longitude != 0.0) {
            dest.writeByte((byte) 1);
            dest.writeDouble(longitude);
        } else {
            dest.writeByte((byte) 0);
        }
        dest.writeString(imageUrl);
    }
    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getETiketLink() {
        return eTiketLink;
    }

    public void setETiketLink(String eTiketLink) {
        this.eTiketLink = eTiketLink;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getOfficialWebsite() {
        return officialWebsite;
    }

    public void setOfficialWebsite(String officialWebsite) {
        this.officialWebsite = officialWebsite;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }
}
