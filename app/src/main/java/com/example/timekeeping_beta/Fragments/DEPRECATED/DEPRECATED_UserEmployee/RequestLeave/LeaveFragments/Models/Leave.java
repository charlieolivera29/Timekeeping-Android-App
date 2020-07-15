package com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestLeave.LeaveFragments.Models;

public class Leave {
    private String id;
    private String leave_name;
    private String leave_code;

    public Leave() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLeave_name() {
        return leave_name;
    }

    public void setLeave_name(String leave_name) {
        this.leave_name = leave_name;
    }

    public String getLeave_code() {
        return leave_code;
    }

    public void setLeave_code(String leave_code) {
        this.leave_code = leave_code;
    }

    @Override
    public String toString() {
        return leave_name;
    }
}
