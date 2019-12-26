package com.example.timekeeping_beta.Fragments.HRAdmin.Employees

import org.json.JSONArray

data class Employee(
        var id: Int,
        var user_id: String,
        var company_id: String,
        var email: String,
        var type: Int,
        var password: String,
        var pin: String,
        var isActive: Int,
        var last_seen: String,
        var api_token: String,
        var remember_token: String,
        var fname: String,
        var lname: String,
        var image: String,
        var role_id: String,
        var role_name: String,
        var location_id: String,
        var branch_name: String,
        var timetrack: JSONArray,
        var reports_to: JSONArray,
        var online: Boolean
)