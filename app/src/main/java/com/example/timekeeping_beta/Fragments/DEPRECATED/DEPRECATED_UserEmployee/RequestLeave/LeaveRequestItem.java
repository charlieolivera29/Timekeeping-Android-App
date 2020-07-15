package com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestLeave;

import android.os.Parcel;
import android.os.Parcelable;


public class LeaveRequestItem implements Parcelable {
    private String request_id;
    private String date_start;
    private String date_end;
    private String leave_type;
    private String status;
    private String time_start;
    private String time_end;
    private String day_type;
    private String reason;

    public LeaveRequestItem() {

    }

    protected LeaveRequestItem(Parcel in) {
        request_id = in.readString();
        date_start = in.readString();
        date_end = in.readString();
        leave_type = in.readString();
        status = in.readString();
        time_start = in.readString();
        time_end = in.readString();
        day_type = in.readString();
        reason = in.readString();
    }

    public static final Creator<LeaveRequestItem> CREATOR = new Creator<LeaveRequestItem>() {
        @Override
        public LeaveRequestItem createFromParcel(Parcel parcel) {
            return new LeaveRequestItem(parcel);
        }

        @Override
        public LeaveRequestItem[] newArray(int i) {
            return new LeaveRequestItem[i];
        }
    };

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public String getDate_start() {
        return date_start;
    }

    public void setDate_start(String date_start) {
        this.date_start = date_start;
    }

    public String getDate_end() {
        return date_end;
    }

    public void setDate_end(String date_end) {
        this.date_end = date_end;
    }

    public String getLeave_type() {
        return leave_type;
    }

    public void setLeave_type(String leave_type) {
        this.leave_type = leave_type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime_start() {
        return time_start;
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;
    }

    public String getTime_end() {
        return time_end;
    }

    public void setTime_end(String time_end) {
        this.time_end = time_end;
    }

    public String getDay_type() {
        return day_type;
    }

    public void setDay_type(String day_type) {
        this.day_type = day_type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(request_id);
        parcel.writeString(date_start);
        parcel.writeString(date_end);
        parcel.writeString(leave_type);
        parcel.writeString(status);
        parcel.writeString(time_start);
        parcel.writeString(time_end);
        parcel.writeString(day_type);
        parcel.writeString(reason);
    }
}
