package com.example.timekeeping_beta.Globals.Models;

public class UserProfileItem {
    private String fname;
    private String lname;
    private String email;
    private String cell_num;
    private String user_image;
    private String emp_num;

    public UserProfileItem() {

    }


    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCell_num() {
        return cell_num;
    }

    public void setCell_num(String cell_num) {
        this.cell_num = cell_num;
    }

    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }

    public String getEmp_num() {
        return emp_num;
    }

    public void setEmp_num(String emp_num) {
        this.emp_num = emp_num;
    }
}
