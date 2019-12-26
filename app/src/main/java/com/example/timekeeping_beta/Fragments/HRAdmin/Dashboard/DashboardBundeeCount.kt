package com.example.timekeeping_beta.Fragments.HRAdmin.Dashboard

import org.json.JSONArray

data class DashboardBundeeCount(
        var count: JSONArray,
        var total_in: Int,
        var total_out: Int,
        var percentage: Double,
        var total: Int
)