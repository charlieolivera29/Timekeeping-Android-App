package com.example.timekeeping_beta.Fragments.UserEmployee.RequestAdjustment;

import android.os.Parcel;
import android.os.Parcelable;

public class AdjustmentRequestItem implements Parcelable {
    private String id;
    private String date;
    private String time_in;
    private String time_out;
    private String shift_in;
    private String shift_out;
    private String day_type;
    private String grace_period;
    private String reference;
    private String reason;

    //Karl
    private String status;
    private String requested_time_in;
    private String requested_time_out;
    private String requested_day_type;

    public AdjustmentRequestItem(){

    }

    protected AdjustmentRequestItem(Parcel in) {
        id = in.readString();
        date = in.readString();
        time_in = in.readString();
        time_out = in.readString();
        shift_in = in.readString();
        shift_out = in.readString();
        day_type = in.readString();
        setGrace_period(in.readString());
        setReference(in.readString());

        //Karl
        setReason(in.readString());
        setStatus(in.readString());
        setRequested_time_in(in.readString());
        setRequested_time_out(in.readString());
        setRequested_day_type(in.readString());
    }

    public static final Creator<AdjustmentRequestItem> CREATOR = new Creator<AdjustmentRequestItem>() {
        @Override
        public AdjustmentRequestItem createFromParcel(Parcel in) {
            return new AdjustmentRequestItem(in);
        }

        @Override
        public AdjustmentRequestItem[] newArray(int size) {
            return new AdjustmentRequestItem[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getDay_type() {
        return day_type;
    }

    public void setDay_type(String day_type) {
        this.day_type = day_type;
    }

    public String getGrace_period() {
        return grace_period;
    }

    public void setGrace_period(String grace_period) {
        this.grace_period = grace_period;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String s) {
        this.status = s;
    }

    public String getRequested_time_in() {
        return requested_time_in;
    }

    public void setRequested_time_in(String s) {
        this.requested_time_in = s;
    }

    public String getRequested_time_out() {
        return requested_time_out;
    }

    public void setRequested_time_out(String s) {
        this.requested_time_out = s;
    }

    public String getRequested_day_type() {
        return requested_day_type;
    }

    public void setRequested_day_type(String s) {
        this.requested_day_type = s;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(date);
        parcel.writeString(time_in);
        parcel.writeString(time_out);
        parcel.writeString(shift_in);
        parcel.writeString(shift_out);
        parcel.writeString(day_type);
        parcel.writeString(getGrace_period());
        parcel.writeString(getReference());
        parcel.writeString(getReason());
        parcel.writeString(getStatus());
        parcel.writeString(getRequested_time_in());
        parcel.writeString(getRequested_time_out());
        parcel.writeString(getRequested_day_type());
    }

}
