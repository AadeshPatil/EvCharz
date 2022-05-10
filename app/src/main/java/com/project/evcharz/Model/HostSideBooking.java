package com.project.evcharz.Model;

public class HostSideBooking {

    String id;
    String total_amount;
    String mobile_no;
    String total_unit;
    String last_amount_withdraw;
    String total_withdraw;

    public HostSideBooking(String id, String total_amount, String mobile_no, String total_unit, String last_amount_withdraw, String total_withdraw) {
        this.id = id;
        this.total_amount = total_amount;
        this.mobile_no = mobile_no;
        this.total_unit = total_unit;
        this.last_amount_withdraw = last_amount_withdraw;
        this.total_withdraw = total_withdraw;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getTotal_unit() {
        return total_unit;
    }

    public void setTotal_unit(String total_unit) {
        this.total_unit = total_unit;
    }

    public String getLast_amount_withdraw() {
        return last_amount_withdraw;
    }

    public void setLast_amount_withdraw(String last_amount_withdraw) {
        this.last_amount_withdraw = last_amount_withdraw;
    }

    public String getTotal_withdraw() {
        return total_withdraw;
    }

    public void setTotal_withdraw(String total_withdraw) {
        this.total_withdraw = total_withdraw;
    }

    public HostSideBooking() {
    }
}
