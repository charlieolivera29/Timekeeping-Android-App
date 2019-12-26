package com.example.timekeeping_beta.Fragments.UserEmployee2.Leaves

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.timekeeping_beta.Fragments.UserEmployee.RequestAdjustment.AdjustmentRequestItem
import com.example.timekeeping_beta.Fragments.UserEmployee.RequestLeave.LeaveFragments.Models.Leave
import com.example.timekeeping_beta.Fragments.UserEmployee.RequestLeave.LeaveRequestItem
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

class LeaveViewModel(application: Application) : AndroidViewModel(application) {

    var Leaves = MutableLiveData<ArrayList<LeaveRequestItem>>()
    var pagination = MutableLiveData<Pagination>()

    private val app = application
    private val url = URLs_v2()
    private val user = SharedPrefManager.getInstance(application.baseContext).user
    private val helper = Helper.getInstance(application.baseContext)

    fun retrieveData(status: String, search: String, page: Int, show: Int) {

        val GET_USER_ADJUSTMENTS_URL = url.GET_USER_ADJUSTMENTS("leave-request/".plus(user.user_id), queryStringBuilder(user.api_token, user.link, page, search, show, status).build())

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

                            Leaves.value = null
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Leaves.setValue(null)
                    }
                }, Response.ErrorListener {
            Leaves.setValue(null)
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

        val scopedAdjustments = ArrayList<LeaveRequestItem>()

        if (jot_array.length() > 0) {


            for (i in 0 until jot_array.length()) {
                val data = jot_array.get(i) as JSONObject

                val leaveRequestItem = LeaveRequestItem()
                leaveRequestItem.request_id = data.getString("request_id")
                leaveRequestItem.leave_type = data.getString("leave_type")
                leaveRequestItem.date_start = helper.convertToReadableDate(data.getString("date_start"))
                leaveRequestItem.date_end = helper.convertToReadableDate(data.getString("date_end"))
                leaveRequestItem.time_start = helper.convertToReadableTime(data.getString("time_start"))
                leaveRequestItem.time_end = helper.convertToReadableTime(data.getString("time_end"))
                leaveRequestItem.status = data.getString("status")
                leaveRequestItem.day_type = data.getString("day_type")
                leaveRequestItem.reason = data.getString("reason")

                scopedAdjustments.add(leaveRequestItem)
            }

        }

        Leaves.value = scopedAdjustments
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

                            Leaves.value = null
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Leaves.setValue(null)
                    }
                }, Response.ErrorListener {
            Leaves.setValue(null)
        }) {
            override fun getHeaders(): MutableMap<String, String> {

                val h = helper.headers()
                return h
            }
        }

        stringRequest.retryPolicy = helper.volleyRetryPolicy()

        Helper.getInstance(app.applicationContext).addToRequestQueue(stringRequest)
    }


    fun setPagination(msg: JSONObject) {
        var l_pagination:Pagination? = null

        if (msg.getString("next_page_url") != "null" || msg.getString("prev_page_url") != "null" ){
            l_pagination = Pagination(
                    msg.getInt("current_page"),
                    msg.getString("next_page_url"),
                    msg.getString("prev_page_url"))

        }

        pagination.value = l_pagination
    }
}