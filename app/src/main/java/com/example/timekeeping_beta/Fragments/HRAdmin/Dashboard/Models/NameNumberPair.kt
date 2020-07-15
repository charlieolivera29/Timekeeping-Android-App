package com.example.timekeeping_beta.Fragments.HRAdmin.Dashboard.Models

class NameNumberPair(
        var fname: String,
        var lname: String,
        var fullName: String,
        var f: Double) {

    init {
        fullName = fname.plus(" ").plus(lname)
    }
}