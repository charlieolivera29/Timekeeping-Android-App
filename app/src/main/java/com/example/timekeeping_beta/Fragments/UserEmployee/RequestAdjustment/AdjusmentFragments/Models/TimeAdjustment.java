package com.example.timekeeping_beta.Fragments.UserEmployee.RequestAdjustment.AdjusmentFragments.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class TimeAdjustment implements Parcelable {
    private String id;
    private String time_in;
    private String time_out;
    private String shift_in;
    private String shift_out;
    private String reference;
    private String day_type;
    private String date;

    public TimeAdjustment (){

    }

    protected TimeAdjustment(Parcel in) {
        id = in.readString();
        time_in = in.readString();
        time_out = in.readString();
        shift_in = in.readString();
        shift_out = in.readString();
        reference = in.readString();
        day_type = in.readString();
        date = in.readString();
    }

    public static final Creator<TimeAdjustment> CREATOR = new Creator<TimeAdjustment>() {
        @Override
        public TimeAdjustment createFromParcel(Parcel in) {
            return new TimeAdjustment(in);
        }

        @Override
        public TimeAdjustment[] newArray(int size) {
            return new TimeAdjustment[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime_in() {
        return time_in;
    }

    public void setTime_in(String time_in) {
        this.time_in = time_in;
    }

    public String getTime_out() {
        return time_out;
    }

    public void setTime_out(String time_out) {
        this.time_out = time_out;
    }

    public String getShift_in() {
        return shift_in;
    }

    public void setShift_in(String shift_in) {
        this.shift_in = shift_in;
    }

    public String getShift_out() {
        return shift_out;
    }

    public void setShift_out(String shift_out) {
        this.shift_out = shift_out;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDay_type() {
        return day_type;
    }

    public void setDay_type(String day_type) {
        this.day_type = day_type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(time_in);
        parcel.writeString(time_out);
        parcel.writeString(shift_in);
        parcel.writeString(shift_out);
        parcel.writeString(reference);
        parcel.writeString(day_type);
        parcel.writeString(date);
    }
}
