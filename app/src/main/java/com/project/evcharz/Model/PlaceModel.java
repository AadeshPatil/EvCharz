package com.project.evcharz.Model;

import java.io.Serializable;

public class PlaceModel implements Serializable {


    String station_id;
    Double latitude;
    Double longitude;
    String place_name;
    String unit_rate;
    String rating;
    String photoId;
    String address;

    public String getStation_id() {
        return station_id;
    }

    public void setStation_id(String station_id) {
        this.station_id = station_id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getUnit_rate() {
        return unit_rate;
    }

    public void setUnit_rate(String unit_rate) {
        this.unit_rate = unit_rate;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public PlaceModel(String station_id, Double latitude, Double longitude, String place_name, String unit_rate, String rating, String photoId, String address) {
        this.station_id = station_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.place_name = place_name;
        this.unit_rate = unit_rate;
        this.rating = rating;
        this.photoId = photoId;
        this.address = address;
    }

    public PlaceModel(){

    }
}
