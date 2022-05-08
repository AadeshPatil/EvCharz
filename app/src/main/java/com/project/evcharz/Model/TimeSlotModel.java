package com.project.evcharz.Model;

public class TimeSlotModel {

    Long id;
    String station_id;
    String date;
    String start_time;
    String end_time;
    Boolean isAvailable;

    public TimeSlotModel(Long id, String station_id, String date, String start_time, String end_time, Boolean isAvailable) {
        this.id = id;
        this.station_id = station_id;
        this.date = date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.isAvailable = isAvailable;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStation_id() {
        return station_id;
    }

    public void setStation_id(String station_id) {
        this.station_id = station_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }
}
