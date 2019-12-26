package com.example.timekeeping_beta.Fragments.GlobalSettings.HolidayManagement

import org.json.JSONArray

data class Holiday(
        var id: Int,
        var holiday_name: String,
        var holiday_code: String,
        var holiday_date: String,
        var holiday_type: String,
        var holiday_remarks: String,
        var added_by: String,
        var location: JSONArray,
        var location_id: JSONArray
)