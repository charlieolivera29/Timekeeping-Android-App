package com.example.timekeeping_beta.Fragments.Timesheet;

import org.json.JSONObject;

public class TimesheetItem {
    private String date_in;
    private String readable_month;
    private String readable_day_of_month;
    private String numeric_date;
    private String time_in;
    private String time_out;
    private String shift_in;
    private String shift_out;
    private String day_type;
    private String reference;
    private Integer late;
    private Integer undertime;
    private Integer adjusted;

    public TimesheetOvetime getOvertime() {
        return overtime;
    }

    public void setOvertime(TimesheetOvetime overtime) {
        this.overtime = overtime;
    }

    private TimesheetOvetime overtime;


    public TimesheetItem() { }

    public String getReadable_month() {
        return readable_month;
    }

    public String getReadable_day_of_month() {
        return readable_day_of_month;
    }

    public String getDate_in() {
        return date_in;
    }

    public String getNumeric_date() {
        return numeric_date;
    }

    public String getTime_in() {
        return time_in;
    }

    public String getTime_out() {
        return time_out;
    }

    public String getShiftIn() {
        return shift_in;
    }

    public String getShiftOut() {
        return shift_out;
    }

    public String getDay_type() {
        return day_type;
    }

    public String getReference() {
        return reference;
    }

    public Integer getLate() {
        return late;
    }

    public Integer getUndertime() {
        return undertime;
    }

    public Integer getAdjusted() {
        return adjusted;
    }

    public void setDate_in(String date_in) {
        this.date_in = date_in;
    }

    public void setNumeric_date(String i_numeric_date) {
        this.numeric_date = i_numeric_date;
    }

    public void setTime_in(String time_in) {
        this.time_in = time_in;
    }

    public void setTime_out(String time_out) {
        this.time_out = time_out;
    }

    public void setShift_in(String shift_in) {
        this.shift_in = shift_in;
    }

    public void setShift_out(String shift_out) {
        this.shift_out = shift_out;
    }

    public void setDay_type(String day_type) {
        this.day_type = day_type;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setLate(Integer late) {
        this.late = late;
    }

    public void setUndertime(Integer undertime) {
        this.undertime = undertime;
    }

    public void setAdjusted(Integer adjusted) {
        this.adjusted = adjusted;
    }

    public void setReadable_day_of_month(String i) {
        this.readable_day_of_month = i;
    }

    public void setReadable_month(String i) { this.readable_month = i; }
}
