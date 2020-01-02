package com.example.timekeeping_beta.Fragments.UserApprover.Approvee

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.timekeeping_beta.Fragments.UserApprover.Approvee.Models.Approvee
import com.example.timekeeping_beta.Globals.Helper
import com.example.timekeeping_beta.Globals.Models.queryStringBuilder
import com.example.timekeeping_beta.Globals.SharedPrefManager
import com.example.timekeeping_beta.Globals.StaticData.URLs_v2
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ApproveeViewModel(application: Application) : AndroidViewModel(application) {

    var Approvees = MutableLiveData<ArrayList<Approvee>>()

    private val app = application
    private val url = URLs_v2()
    private val user = SharedPrefManager.getInstance(application.baseContext).user
    private val helper = Helper.getInstance(application.baseContext)

    fun retrieveData() {

        val GET_USER_ADJUSTMENTS_URL = url.GET_USER_ADJUSTMENTS("user/approver/".plus(user.user_id), queryStringBuilder(user.api_token, user.link, 0, "", 0, "").buildLegacy())

        val stringRequest = object : StringRequest(Request.Method.GET, GET_USER_ADJUSTMENTS_URL,
                Response.Listener { response ->

                    try {
                        val response_obj = JSONObject(response)
                        val approvees_jarray = response_obj.getJSONArray("msg")
                        val array_len = approvees_jarray.length()

                        if (array_len > 0) {

                            var localApproveeList = ArrayList<Approvee>()

                            for (i in 0 until array_len) {
                                val approvee = approvees_jarray.getJSONObject(i)
                                val id = approvee.getString("user_id")
                                val f_name = approvee.getString("fname")
                                val l_name = approvee.getString("lname")
                                val cell_num = approvee.getString("cell_num")
                                val email = approvee.getString("email")
                                val location = approvee.getString("location")
                                var role_id = ""
                                var role_name = ""

                                //Wala sinesend
                                //String image_file_name =

                                if (approvee.has("user_roles")) {
                                    val user_roles = JSONObject(approvee.getString("user_roles"))
                                    role_id = user_roles.getString("role_id")
                                    role_name = user_roles.getString("role_name")
                                }
                                if (approvee.has("roles")) {
                                    val user_roles = JSONArray(approvee.getString("roles"))
                                    role_id = user_roles.getString(i)
                                }

                                val a = Approvee(id, f_name, l_name, cell_num, email, location, role_id, role_name, "")
                                localApproveeList.add(a)
                            }

                            Approvees.value = localApproveeList

                        }

                    } catch (e: JSONException) {
                        Approvees.setValue(null)
                    }

                }, Response.ErrorListener {
            Approvees.setValue(null)
        }) {
            override fun getHeaders(): MutableMap<String, String> {

                val h = helper.headers()
                return h
            }
        }

        stringRequest.retryPolicy = helper.volleyRetryPolicy()

        Helper.getInstance(app.applicationContext).addToRequestQueue(stringRequest)
    }
}