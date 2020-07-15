package com.example.timekeeping_beta.Globals.Models

data class UserProfileAll(
        var
        fname: String,
        var lname: String,
        var email: String,
        var contact_numner: String,
        var company: String,
        var location_id: String,
        var location_branch: String,
        var assigned_kiosks: String,
        var bundee_id: String,
        var bundee_name: String,
        var employee_number : String,
        var approvers : Array<String>,
        var role_id: String,
        var role_name: String,
        var shift_in : String,
        var shift_out   : String,
        var image_file_name : String
)