package com.example.timekeeping_beta.Fragments.Dashboard.Models

class Dashboard(
        var today_late: Int,
        var today_undertime: Int,
        var today_accumulated: Int,
        var monthly_late: Int,
        var monthly_undertime: Int,
        var monthly_accumulated: Int,
        var yearly_late: Int,
        var yearly_undertime: Int,
        var yearly_accumulated: Int
)