package com.example.timekeeping_beta.Globals.Models

data class Pagination (
        var current_page: Int,
        var next_page_url: String,
        var prev_page_url: String
)