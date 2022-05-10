package com.project.evcharz.Model;

import java.io.Serializable;

public class BookingModel implements Serializable {

    long booking_id;
    String user_mb_no;
    String date;
    String booking_time;
    String start_time;
    String end_time;
    String vehicle_type;
    String station_id;
    String station_name;
    String amount_paid;
    String transaction_mode;
    String status;
    String duration;
    String unit_consumption;

    public String getUser_mb_no() {
        return user_mb_no;
    }

    public void setUser_mb_no(String user_mb_no) {
        this.user_mb_no = user_mb_no;
    }

    public long getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(long booking_id) {
        this.booking_id = booking_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBooking_time() {
        return booking_time;
    }

    public void setBooking_time(String booking_time) {
        this.booking_time = booking_time;
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

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    public String getStation_id() {
        return station_id;
    }

    public void setStation_id(String station_id) {
        this.station_id = station_id;
    }

    public String getStation_name() {
        return station_name;
    }

    public void setStation_name(String station_name) {
        this.station_name = station_name;
    }

    public String getAmount_paid() {
        return amount_paid;
    }

    public void setAmount_paid(String amount_paid) {
        this.amount_paid = amount_paid;
    }

    public String getTransaction_mode() {
        return transaction_mode;
    }

    public void setTransaction_mode(String transaction_mode) {
        this.transaction_mode = transaction_mode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getUnit_consumption() {
        return unit_consumption;
    }

    public void setUnit_consumption(String unit_consumption) {
        this.unit_consumption = unit_consumption;
    }


    public BookingModel(long booking_id, String user_mb_no, String date, String booking_time, String start_time, String end_time, String vehicle_type, String station_id, String station_name, String amount_paid, String transaction_mode, String status, String duration, String unit_consumption) {
        this.booking_id = booking_id;
        this.user_mb_no = user_mb_no;
        this.date = date;
        this.booking_time = booking_time;
        this.start_time = start_time;
        this.end_time = end_time;
        this.vehicle_type = vehicle_type;
        this.station_id = station_id;
        this.station_name = station_name;
        this.amount_paid = amount_paid;
        this.transaction_mode = transaction_mode;
        this.status = status;
        this.duration = duration;
        this.unit_consumption = unit_consumption;
    }

    public BookingModel() {
    }
}
