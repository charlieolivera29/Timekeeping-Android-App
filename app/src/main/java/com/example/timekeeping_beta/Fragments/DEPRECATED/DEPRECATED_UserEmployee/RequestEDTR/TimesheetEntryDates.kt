package com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestEDTR

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