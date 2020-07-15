package com.example.timekeeping_beta.Fragments.UserEmployee.Overtimes

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.timekeeping_beta.Fragments.UserApprover.Overtime.Overtime
import com.example.timekeeping_beta.Globals.CustomClasses.Flag
import com.example.timekeeping_beta.Globals.Helper
import com.example.timekeeping_beta.Globals.Models.Pagination
import com.example.timekeeping_beta.Globals.Models.queryStringBuilder
import com.example.timekeeping_beta.Globals.SharedPrefManager
import com.example.timekeeping_beta.Globals.StaticData.URLs_v2
import es.dmoral.toasty.Toasty
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class OvertimeViewModel(application: Application) : AndroidViewModel(application) {

    var Overtimes = MutableLiveData<ArrayList<Overtime>>()
    var pagination = MutableLiveData<Pagination>()

    private val app = application
    private val url = URLs_v2()
    private val user = SharedPrefManager.getInstance(application.baseContext).user
    private val helper = Helper.getInstance(application.baseContext)

    fun retrieveData(status: String, search: String, page: Int, show: Int) {

        val GET_USER_ADJUSTMENTS_URL = url.GET_USER_ADJUSTMENTS("overtime/".plus(user.user_id), queryStringBuilder(user.api_token, user.link, page, search, show, status).build())

        val stringRequest = object : StringRequest(Request.Method.GET, GET_USER_ADJUSTMENTS_URL,
                Response.Listener { response ->
                    try {
                        val response_obj = JSONObject(response)
                        val status = response_obj.getString("status")

                        if (status == "success") {

                            setData(response_obj.getJSONObject("msg").getJSONArray("data"))
                            setPagination(response_obj.getJSONObject("msg"))

                        } else if (status == "error") {

                            if (response_obj.get("msg") is String) {
                                val invalidTokenMessage = "Access Denied: invalid token!"
                                if (response_obj.getString("msg") == invalidTokenMessage) {

                                    Toasty.error(app.baseContext, invalidTokenMessage, Toasty.LENGTH_LONG).show()
                                }
                            }

                            Overtimes.value = null
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Overtimes.setValue(null)
                    }
                }, Response.ErrorListener {
            Overtimes.setValue(null)
        }) {
            override fun getHeaders(): MutableMap<String, String> {

                val h = helper.headers()
                return h
            }
        }

        stringRequest.retryPolicy = helper.volleyRetryPolicy()

        Helper.getInstance(app.applicationContext).addToRequestQueue(stringRequest)
    }

    fun retrievePaginated(flag: Int,status: String, search: String,show: Int) {

        val l_pagination = pagination.value!!
        var url = ""

        if (flag == Flag.NEXT_PAGE) {
            url = l_pagination.next_page_url
        } else if (flag == Flag.PREV_PAGE) {
            url = l_pagination.prev_page_url
        }
        url = url.plus(queryStringBuilder(user.api_token, user.link, 0, search, show, status).buildNoPage())

        val stringRequest = object : StringRequest(Request.Method.GET, url,
                Response.Listener { response ->
                    try {
                        val response_obj = JSONObject(response)
                        val status = response_obj.getString("status")

                        if (status == "success") {

                            setData(response_obj.getJSONObject("msg").getJSONArray("data"))
                            setPagination(response_obj.getJSONObject("msg"))

                        } else if (status == "error") {

                            if (response_obj.get("msg") is String) {
                                val invalidTokenMessage = "Access Denied: invalid token!"
                                if (response_obj.getString("msg") == invalidTokenMessage) {

                                    Toasty.error(app.baseContext, invalidTokenMessage, Toasty.LENGTH_LONG).show()
                                }
                            }

                            Overtimes.value = null
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Overtimes.setValue(null)
                    }
                }, Response.ErrorListener {
            Overtimes.setValue(null)
        }) {
            override fun getHeaders(): MutableMap<String, String> {

                val h = helper.headers()
                return h
            }
        }

        stringRequest.retryPolicy = helper.volleyRetryPolicy()

        Helper.getInstance(app.applicationContext).addToRequestQueue(stringRequest)
    }

    fun setData(jot_array: JSONArray) {

        val scopedOvertime = ArrayList<Overtime>()

        if (jot_array.length() > 0) {

            for (i in 0 until jot_array.length()) {
                val jot = jot_array.get(i) as JSONObject

                val id = jot.getInt("id")
                val employee_id = jot.getString("user_id")

                val numeric_date = jot.getString("date")
                val _24HOUR_start_time = jot.getString("start_time")
                val _24HOUR_end_time = jot.getString("end_time")

                val ot_status = jot.getString("status")
                val reason = jot.getString("reason")
                val checked_by = jot.getString("checked_by")
                val checked_at = jot.getString("checked_at")

                val updated_at = jot.getString("updated_at")
                val created_at = jot.getString("created_at")

                val ot = Overtime(id, employee_id, numeric_date, _24HOUR_start_time, _24HOUR_end_time, ot_status, reason, checked_by, checked_at, "", user.fname, user.lname, "", updated_at, created_at)
                scopedOvertime.add(ot)
            }

        }

        Overtimes.value = scopedOvertime
    }


    fun setPagination(msg: JSONObject) {
        var l_pagination: Pagination? = null

        if (msg.getString("next_page_url") != "null" || msg.getString("prev_page_url") != "null" ){
            l_pagination = Pagination(
                    msg.getInt("current_page"),
                    msg.getString("next_page_url"),
                    msg.getString("prev_page_url"))

        }

        pagination.value = l_pagination
    }
}