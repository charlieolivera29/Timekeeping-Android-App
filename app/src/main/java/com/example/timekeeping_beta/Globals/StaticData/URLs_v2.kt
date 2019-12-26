package com.example.timekeeping_beta.Globals.StaticData

import com.example.timekeeping_beta.Globals.SharedPrefManager
import com.example.timekeeping_beta.Globals.Static

//Version 2 11-28-2019
class URLs_v2 {

    companion object {

        const val ROOT_URL = Static.ROOT_URL
        const val VERSION_ROOT_URL = Static.VERSION_ROOT_URL

        const val BACKEND_ADMIN_PATH = ROOT_URL.plus("adminbackend/api/")
        const val BACKEND_CLOCK_PATH = ROOT_URL.plus("clock/api/")
    }

    fun GET_USER_ADJUSTMENTS(route: String, queryStrings: String): String {

        return BACKEND_ADMIN_PATH.plus(route).plus(queryStrings)
    }

    fun GET_ADMIN_BACKEND_DATA(route: String, queryStrings: String): String {

        return BACKEND_ADMIN_PATH.plus(route).plus(queryStrings)
    }

    fun GET_CLOCK_BACKEND_DATA(route: String, queryStrings: String): String {

        return BACKEND_CLOCK_PATH.plus(route).plus(queryStrings)
    }

    fun GET_VERSION_CHECKER(): String {

        return VERSION_ROOT_URL.plus("/getversion?app_code=").plus(Static.APP_CODE).plus("&installed_version=").plus(Static.APP_VERSION);
    }
}