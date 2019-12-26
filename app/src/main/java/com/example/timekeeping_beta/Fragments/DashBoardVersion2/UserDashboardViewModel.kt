package com.example.timekeeping_beta.Fragments.DashBoardVersion2

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.timekeeping_beta.Fragments.DashBoardVersion2.Models.DashboardAttendance
import com.example.timekeeping_beta.Fragments.DashBoardVersion2.Models.DashboardCount
import com.example.timekeeping_beta.Fragments.Dashboard.Models.DashboardCounts
import com.example.timekeeping_beta.Fragments.HRAdmin.Dashboard.Models.NameNumberPair
import com.example.timekeeping_beta.Globals.CustomClasses.Flag
import com.example.timekeeping_beta.Globals.StaticData.URLs
import com.example.timekeeping_beta.Globals.Helper
import com.example.timekeeping_beta.Globals.Models.dashboardOption
import com.example.timekeeping_beta.Globals.Models.listOption
import com.example.timekeeping_beta.Globals.SharedPrefManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class UserDashboardViewModel(application: Application) : AndroidViewModel(application) {

    //Data {

    var dashboardAttendance = MutableLiveData<DashboardAttendance>()
    var dashboardOvertimeCounts = MutableLiveData<DashboardCount>()
    var dashboardOvertimeNames = MutableLiveData<ArrayList<NameNumberPair>>()
    var dashboardLatesCounts = MutableLiveData<DashboardCount>()
    var dashboardLatesNames = MutableLiveData<ArrayList<NameNumberPair>>()
    var invalidToken = MutableLiveData<Boolean>()

    // }


    private val app = application
    private val url = URLs()
    private val user = SharedPrefManager.getInstance(application.baseContext).user
    private val helper = Helper.getInstance(application.baseContext)


    private val url_attendance_percentage = url.url_attendance_percentage(user.api_token, user.link)
    private val url_post_overtime_counts = url.post_overtime_counts(user.api_token, user.link)
    private val url_post_late_counts = url.post_late_counts(user.api_token, user.link)

    fun retrieveDashboardAttendance(date_start: String, date_end: String, day_type: String, user_type: String) {

        val stringRequest = object : StringRequest(Request.Method.POST, url_attendance_percentage,
                Response.Listener { response ->
                    try {
                        val response_obj = JSONObject(response)

                        val status = response_obj.getString("status")

                        if (status == "success") {
                            setToDashboardAttendance(response_obj.getJSONObject("msg"))

                        } else if (status == "error") {

                            if (response_obj.get("msg") is String) {
                                val invalidTokenMessage = "Access Denied: invalid token!"
                                if (response_obj.getString("msg") == invalidTokenMessage) {
                                    invalidToken.value = true
                                }
                            }

                            dashboardAttendance.value = null
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        dashboardAttendance.setValue(null)
                    }
                }, Response.ErrorListener {
            dashboardAttendance.setValue(null)
        }) {
            override fun getParams(): HashMap<String, String> {

                val params = HashMap<String, String>()

                params.put("date_start", date_start)
                params.put("date_end", date_end)
                params.put("date_type", day_type)
                params.put("user_id", user.user_id)
                params.put("user_type", user_type)

                return params
            }

            override fun getHeaders(): Map<String, String> {
                return helper.headers()
            }
        }
        stringRequest.retryPolicy = helper.volleyRetryPolicy()

        Helper.getInstance(app.baseContext).addToRequestQueue(stringRequest)
    }

    private fun setToDashboardAttendance(msg: JSONObject) {

        try {
            dashboardAttendance.value = DashboardAttendance(
                    msg.getInt("total_absent"),
                    msg.getInt("working_days"),
                    msg.getInt("present"),
                    msg.getDouble("percentage")
            )
        } catch (e: JSONException) {
            e.printStackTrace()
            dashboardAttendance.value = null
        }
    }

    fun retrieveOvertimeCounts(date_start: String, date_end: String, day_type: String, user_type: String) {

        val stringRequest = object : StringRequest(Request.Method.POST, url_post_overtime_counts,
                Response.Listener { response ->
                    try {
                        val response_obj = JSONObject(response)
                        val status = response_obj.getString("status")

                        if (status == "success") {

                            try {

                                val msg = response_obj.getJSONObject("msg")

                                dashboardOvertimeCounts.value =
                                        DashboardCount(
                                                msg.getDouble("total_hrs"),
                                                msg.getJSONArray("names"))

                                setToOvertimeNames(msg.getJSONArray("names"))
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                dashboardOvertimeCounts.value = null
                            }
                        } else if (status == "error") {

                            if (response_obj.get("msg") is String) {
                                val invalidTokenMessage = "Access Denied: invalid token!"
                                if (response_obj.getString("msg") == invalidTokenMessage) {
                                    invalidToken.value = true
                                }
                            }

                            dashboardOvertimeCounts.value = null
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        dashboardOvertimeCounts.setValue(null)
                    }
                }, Response.ErrorListener {
            dashboardOvertimeCounts.setValue(null)
        }) {
            override fun getHeaders(): Map<String, String> {

                return helper.headers()
            }

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()

                params.put("date_start", date_start)
                params.put("date_end", date_end)
                params.put("date_type", day_type)
                params.put("user_id", user.user_id)
                params.put("user_type", user_type)

                return params
            }
        }
        stringRequest.retryPolicy = helper.volleyRetryPolicy()

        Helper.getInstance(app.baseContext).addToRequestQueue(stringRequest)
    }

    private fun setToOvertimeNames(ja: JSONArray) {

        val innerList = ArrayList<NameNumberPair>()

        if (ja.length() > 0) {

            for (i in 0 until ja.length()) {

                val jo = ja.getJSONObject(i)
                innerList.add(NameNumberPair(
                        jo.getString("fname"),
                        jo.getString("lname"),
                        "",
                        jo.getDouble("total")))
            }
        }
        dashboardOvertimeNames.value = innerList
    }

    fun retrieveLateCounts(date_start: String, date_end: String, day_type: String, user_type: String) {

        val stringRequest = object : StringRequest(Request.Method.POST, url_post_late_counts,
                Response.Listener { response ->
                    try {
                        val response_obj = JSONObject(response)
                        val status = response_obj.getString("status")

                        if (status == "success") {

                            try {
                                val msg = response_obj.getJSONObject("msg")

                                dashboardLatesCounts.value =
                                        DashboardCount(
                                                msg.getDouble("total_hrs"),
                                                msg.getJSONArray("names"))

                                setToUndertimeNames(msg.getJSONArray("names"))
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                dashboardLatesCounts.value = null
                            }
                        } else if (status == "error") {

                            if (response_obj.get("msg") is String) {
                                val invalidTokenMessage = "Access Denied: invalid token!"
                                if (response_obj.getString("msg") == invalidTokenMessage) {
                                    invalidToken.value = true
                                }
                            }

                            dashboardLatesCounts.value = null
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        dashboardLatesCounts.setValue(null)
                    }
                }, Response.ErrorListener {
            dashboardLatesCounts.setValue(null)
        }) {
            override fun getHeaders(): Map<String, String> {

                return helper.headers()
            }

            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()

                params.put("date_start", date_start)
                params.put("date_end", date_end)
                params.put("date_type", day_type)
                params.put("user_id", user.user_id)
                params.put("user_type", user_type)

                return params
            }
        }
        stringRequest.retryPolicy = helper.volleyRetryPolicy()

        Helper.getInstance(app.baseContext).addToRequestQueue(stringRequest)
    }

    private fun setToUndertimeNames(ja: JSONArray) {

        val innerList = ArrayList<NameNumberPair>()

        if (ja.length() > 0) {

            for (i in 0 until ja.length()) {

                val jo = ja.getJSONObject(i)
                innerList.add(NameNumberPair(
                        jo.getString("fname"),
                        jo.getString("lname"),
                        "",
                        jo.getDouble("total")))
            }
        }
        dashboardLatesNames.value = innerList
    }

    fun getDashboardOptionsUserTypes(): ArrayList<String> {

        val FLAG = getUserRole()

        val listOptions: ArrayList<String> = ArrayList()

        if (FLAG == Flag.USER_IS_EMPLOYEE) {
            listOptions.add("Personal")
        } else if (FLAG == Flag.USER_IS_HR_ADMIN || FLAG == Flag.USER_IS_ADMIN || FLAG == Flag.USER_IS_TIMEKEEPER) {
            listOptions.add("Personal")
            listOptions.add("Company")
        } else if (user.isApprover == "1") {
            listOptions.add("Personal")
            listOptions.add("Company")
            listOptions.add("Team")
        }


        return listOptions
    }

    fun getUserRole(): Int {
        if (user.role_ID.toInt() == 5241) {

            return Flag.USER_IS_EMPLOYEE
        } else if (user.role_ID.toInt() == 5242) {

            return Flag.USER_IS_HR_ADMIN
        } else if (user.role_ID.toInt() == 5243) {

            return Flag.USER_IS_ADMIN
        } else if (user.role_ID.toInt() == 5244) {

            return Flag.USER_IS_TIMEKEEPER
        } else {
            return 6912
        }
    }

    fun getDashboardOptionsDayTypes(): ArrayList<String> {

        val listOptions: ArrayList<String> = ArrayList()
        listOptions.add("Today")
        listOptions.add("Weekly")

        return listOptions
    }

}