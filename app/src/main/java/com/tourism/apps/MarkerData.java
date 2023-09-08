package com.tourism.apps;

import java.util.List;

public class MarkerData {
    private String photoReference, address;
    private boolean openNow;
    private List<String> typesList;
    private double rating;
    private int userRatingsTotal;

    public MarkerData(String photoReference, boolean openNow, List<String> typesList, double rating, int userRatingsTotal, String address) {
        this.photoReference = photoReference;
        this.openNow = openNow;
        this.typesList = typesList;
        this.rating = rating;
        this.userRatingsTotal = userRatingsTotal;
        this.address = address;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }

    public void setTypesList(List<String> typesList) {
        this.typesList = typesList;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setUserRatingsTotal(int userRatingsTotal) {
        this.userRatingsTotal = userRatingsTotal;
    }

    public boolean isOpenNow() {
        return openNow;
    }

    public List<String> getTypesList() {
        return typesList;
    }

    public double getRating() {
        return rating;
    }

    public int getUserRatingsTotal() {
        return userRatingsTotal;
    }
}
