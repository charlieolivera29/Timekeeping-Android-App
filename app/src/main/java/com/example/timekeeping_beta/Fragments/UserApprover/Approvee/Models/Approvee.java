package com.example.timekeeping_beta.Fragments.UserApprover.Approvee.Models;

public class Approvee {
    private String id;
    private String f_name;
    private String l_name;
    private String cell_num;
    private String email;
    private String location;
    private String role_id;
    private String role_name;
    private String image_file_name;

    public Approvee(
            String i_id,
            String i_f_name,
            String i_l_name,
            String i_cell_num,
            String i_email,
            String i_location,
            String i_role_id,
            String i_role_name,
            String i_image_file_name
    ) {

        id = i_id;
        f_name = i_f_name;
        l_name = i_l_name;
        cell_num = i_cell_num;
        email = i_email;
        location = i_location;
        role_id = i_role_id;
        role_name = i_role_name;
        image_file_name = i_image_file_name;
    }

    public String getApproveeId() {
        return id;
    }

    public String getFirst_name() {
        return f_name;
    }

    public String getLast_name() {
        return l_name;
    }

    public String getCell_Number() {
        return cell_num;
    }

    public String getEmail() {
        return email;
    }

    public String getLocation() {
        return location;
    }

    public String getRole_ID() {
        return role_id;
    }

    public String getRole_name() {
        return role_name;
    }

    public String getImage_file_name() { return image_file_name; }

}
