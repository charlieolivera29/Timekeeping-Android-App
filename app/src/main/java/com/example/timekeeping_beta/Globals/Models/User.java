package com.example.timekeeping_beta.Globals.Models;

public class User {

    private String user_id;
    private String emp_num;
    private String c1;
    private String c2;
    private String link;
    private String email;
    private String fname;
    private String lname;
    private String api_token;
    private String company;

    //Karl
    private String company_ID;
    private String role_ID;
    private String role_name;
    private String schedule_shift_in;
    private String schedule_shift_out;
    private String is_approver;
    private String file_name;
    private String user_bundees;

    private String token;
    private String broken_shift;

    public User(
            String user_id, String emp_num, String c1, String c2, String link, String email, String fname, String lname, String api_token, String company, String i_company_ID,

            //Karl
            String i_role_ID,
            String i_role_name,
            String i_shift_in,
            String i_shift_out,
            String i_is_approver,
            String i_file_name,
            String i_user_bundee,
            String i_token,
            String i_broken_shift
    ) {
        this.user_id = user_id;
        this.emp_num = emp_num;
        this.c1 = c1;
        this.c2 = c2;
        this.link = link;
        this.email = email;
        this.fname = fname;
        this.lname = lname;
        this.api_token = api_token;
        this.company = company;

        //Karl
        this.company_ID = i_company_ID;
        this.role_ID = i_role_ID;
        this.role_name = i_role_name;
        this.schedule_shift_in = i_shift_in;
        this.schedule_shift_out = i_shift_out;
        this.is_approver = i_is_approver;
        this.file_name = i_file_name;
        this.user_bundees = i_user_bundee;
        this.token = i_token;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getEmp_num() {
        return emp_num;
    }

    public String getC1() {
        return c1;
    }

    public String getC2() {
        return c2;
    }

    public String getLink() {
        return link;
    }

    public String getEmail() {
        return email;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getApi_token() {
        return api_token;
    }

    public String getCompany() {
        return company;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public void setFile_name(String i_file_name) {
        this.file_name = i_file_name;
    }

    public void setUser_bundees(String i_user_bundee) { this.user_bundees = i_user_bundee; }


    //Karl
    public String getCompany_ID() {
        return company_ID;
    }

    public String getRole_ID() {
        return role_ID;
    }

    public String getRole_name() {
        return role_name;
    }

    public String getSchedule_shift_in() {
        return schedule_shift_in;
    }

    public String getSchedule_shift_out() {
        return schedule_shift_out;
    }

    public String getIsApprover() {
        return is_approver;
    }

    public String getImageFileName() {
        return file_name;
    }

    public String getUser_bundees() { return this.user_bundees; }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setBroken_shift(String broken) {
        this.broken_shift = broken;
    }

    public String getBroken_shift() {
        return broken_shift;
    }

}
