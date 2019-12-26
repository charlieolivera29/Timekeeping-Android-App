package com.example.timekeeping_beta.Fragments.UserEmployee.RequestEDTR

import org.json.JSONArray
import org.json.JSONObject

data class TimesheetEntry (
        //Has id
        var id: String,
        var date_in: String,
        var time_in: String,
        var time_out: String,
        var remarks: String,
        var status: String,
        var checked_by: String,
        var checked_at: String,
        var reference: String,
        var reason: String,
        var day_type: String,

        var shift: Int,
        var shift_in: String,
        var shift_out: String,
        var isBroken: Boolean,
        var overtime: JSONObject,
        var late: String,
        var undertime: String,
        var isAdjusted: String,
        var date: String
)