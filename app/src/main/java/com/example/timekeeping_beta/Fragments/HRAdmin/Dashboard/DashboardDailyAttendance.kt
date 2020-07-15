package com.example.timekeeping_beta.Fragments.HRAdmin.Dashboard

import org.json.JSONArray

data class DashboardDailyAttendance (
        var onTimeEmployeeCount : Int,
        var onTimeEmployees : JSONArray?,

        var lateEmployeesCount : Int,
        var lateEmployees : JSONArray?,

        var onLeaveCount : Int,
        var onLeaveEmployees : JSONArray?,

        var absentCount : Int,
        var absentEmployees : JSONArray?
)