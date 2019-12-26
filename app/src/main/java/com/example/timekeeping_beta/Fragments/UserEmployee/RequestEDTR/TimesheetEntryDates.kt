package com.example.timekeeping_beta.Fragments.UserEmployee.RequestEDTR

import org.json.JSONObject

//data class TimesheetEntryDates(
//        var current: JSONObject,
//        var start: String,
//        var end: String,
//        var prev_start: String,
//        var prev_end: String,
//        var next_start: String,
//        var next_end: String
//)

data class TimesheetEntryDates(
        var start: String,
        var end: String,
        var next: String,
        var previous: String
)