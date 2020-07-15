package com.example.timekeeping_beta.Fragments.UserApprover.Approvee.Models;

public class Role {

    private String role_id;

    public String getRole_id() {
        return role_id;
    }

    public String getRole_name() {
        return role_name;
    }

    private String role_name;

    public Role(String role_id, String role_name) {

        this.role_id = role_id;
        this.role_name = role_name;
    }
}


