package com.example.timekeeping_beta.Fragments.GlobalSettings.ScheduleManagement

import org.json.JSONArray

data class Schedule(
        var id: Int,
        var sched_id: String,
        var sched_name: String,
        var shift_in: String,
        var shift_out: String,
        var day: JSONArray,
        var remarks: String,
        var grace_period: String,
        var late_threshold: String,
        var undertime_threshold: String,
        var sched_break: String,
        var date_start: String,
        var default_sched: String
)
