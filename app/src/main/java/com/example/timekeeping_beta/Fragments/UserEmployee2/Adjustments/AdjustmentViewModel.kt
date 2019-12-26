package com.example.timekeeping_beta.Fragments.UserEmployee2.Adjustments

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.timekeeping_beta.Fragments.UserEmployee.RequestAdjustment.AdjustmentRequestItem
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

class AdjustmentViewModel(application: Application) : AndroidViewModel(application) {

    var Adjustments = MutableLiveData<ArrayList<AdjustmentRequestItem>>()
    var pagination = MutableLiveData<Pagination>()


    private val app = application
    private val url = URLs_v2()
    private val user = SharedPrefManager.getInstance(application.baseContext).user
    private val helper = Helper.getInstance(application.baseContext)

    fun retrieveData(status: String, search: String, page: Int, show: Int) {

        val GET_USER_ADJUSTMENTS_URL = url.GET_USER_ADJUSTMENTS("mytimeadjustment/".plus(user.user_id), queryStringBuilder(user.api_token, user.link, page, search, show, status).build())

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

                            Adjustments.value = null
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Adjustments.setValue(null)
                    }
                }, Response.ErrorListener {
            Adjustments.setValue(null)
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

        val scopedAdjustments = ArrayList<AdjustmentRequestItem>()

        if (jot_array.length() > 0) {


            for (i in 0 until jot_array.length()) {
                val data = jot_array.get(i) as JSONObject

                val adjustmentRequestItem = AdjustmentRequestItem()
                adjustmentRequestItem.id = data.getString("id")
                adjustmentRequestItem.date = helper.convertToReadableDate(data.getString("date_in"))
                adjustmentRequestItem.day_type = data.getString("day_type")
                adjustmentRequestItem.time_in = helper.convertToReadableTime(data.getString("old_time_in"))
                adjustmentRequestItem.time_out = helper.convertToReadableTime(data.getString("old_time_out"))
                adjustmentRequestItem.shift_in = helper.convertToReadableTime(data.getString("shift_in"))
                adjustmentRequestItem.shift_out = helper.convertToReadableTime(data.getString("shift_out"))
                adjustmentRequestItem.grace_period = data.getString("grace_period")
                adjustmentRequestItem.reference = data.getString("reference")

                adjustmentRequestItem.requested_time_in = data.getString("time_in")
                adjustmentRequestItem.requested_time_out = data.getString("time_out")
                adjustmentRequestItem.requested_day_type = data.getString("day_type")

                adjustmentRequestItem.reason = if (data.getString("reason") == "null") "" else data.getString("reason")
                adjustmentRequestItem.status = if (data.getString("status") == "null") "" else data.getString("status")

                scopedAdjustments.add(adjustmentRequestItem)
            }

        }

        Adjustments.value = scopedAdjustments
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

                            Adjustments.value = null
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Adjustments.setValue(null)
                    }
                }, Response.ErrorListener {
            Adjustments.setValue(null)
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