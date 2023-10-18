package com.project.evcharz.Model;

public class HostModel {

    String id;
    String station_name;
    String mobile_number;
    String station_id;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStation_name() {
        return station_name;
    }

    public void setStation_name(String station_name) {
        this.station_name = station_name;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }

    public String getStation_id() {
        return station_id;
    }

    public void setStation_id(String station_id) {
        this.station_id = station_id;
    }

    public HostModel(String id, String station_name, String mobile_number, String station_id) {
        this.id = id;
        this.station_name = station_name;
        this.mobile_number = mobile_number;
        this.station_id = station_id;
    }
    public HostModel(){}


}
