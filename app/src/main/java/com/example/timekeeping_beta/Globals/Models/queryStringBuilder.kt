package com.example.timekeeping_beta.Globals.Models

data class queryStringBuilder(
        var api_token: String = "",
        var link: String = "",
        var page: Int,
        var search: String = "",
        var show: Int,
        var status: String = ""
) {

    fun build(): String {
        return "?page=$page&search=$search&status=$status&show=$show&api_token=$api_token&link=$link"
    }

    fun buildLegacy(): String {
        return "?api_token=$api_token&link=$link"
    }

    fun buildNoPage(): String {
        return "&search=$search&status=$status&show=$show&api_token=$api_token&link=$link"
    }
}


