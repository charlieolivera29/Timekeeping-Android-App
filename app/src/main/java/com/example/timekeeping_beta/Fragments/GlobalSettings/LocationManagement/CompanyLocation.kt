package com.example.timekeeping_beta.Fragments.GlobalSettings.LocationManagement

import org.json.JSONArray

data class CompanyLocation (
        var id: Integer,
        var branch_id: String,
        var branch_name: String,
        var address: String,
        var location: String,
        var description: String,
        var date_start: String,
        var timetrack: JSONArray,
        var change_queue: String,
        var schedule: JSONArray,
        var holidays: JSONArray
)