package com.example.timekeeping_beta.Fragments.UserEmployee.RequestOvertime

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.example.timekeeping_beta.Globals.Helper
import com.example.timekeeping_beta.Globals.SharedPrefManager
import com.example.timekeeping_beta.Globals.StaticData.URLs
import com.example.timekeeping_beta.R
import es.dmoral.toasty.Toasty
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class RequestOvertimeFragment : Fragment() {

    private lateinit var v: View
    private lateinit var txt_overtime_time_start: TextView
    private lateinit var txt_overtime_time_end: TextView
    private lateinit var txt_overtime_date: TextView
    private lateinit var txt_overtime_reason: TextView
    private lateinit var btn_overtime_request: Button

    private lateinit var button_startTime: ImageButton
    private lateinit var button_endTime: ImageButton
    private lateinit var btn_calendar_overtime_date: ImageButton

    private lateinit var DatePickerDialog: Dialog
    private lateinit var TimePickerDialog: Dialog

    private lateinit var date: DatePicker
    private lateinit var start_time: TimePicker
    private lateinit var end_time: TimePicker
    private var str_start_time: String = ""
    private var str_end_time: String = ""
    private val str_date: String = ""

    private lateinit var helper: Helper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_request_overtime, container, false)
        initViews(v)
        setListeners()

        DatePickerDialog = Dialog(context)
        TimePickerDialog = Dialog(context)
        helper = Helper.getInstance(context)

        return v
    }

    fun initViews(v: View) {
        txt_overtime_time_start = v.findViewById<TextView>(R.id.txt_overtime_time_start)
        txt_overtime_time_end = v.findViewById<TextView>(R.id.txt_overtime_time_end)
        txt_overtime_date = v.findViewById<TextView>(R.id.txt_overtime_date)
        txt_overtime_reason = v.findViewById<EditText>(R.id.txt_overtime_reason)
        btn_overtime_request = v.findViewById<Button>(R.id.btn_overtime_request)

        btn_calendar_overtime_date = v.findViewById<ImageButton>(R.id.btn_calendar_overtime_date)
        button_startTime = v.findViewById<ImageButton>(R.id.btn_overtime_start_time)
        button_endTime = v.findViewById<ImageButton>(R.id.btn_overtime_end_time)

    }

    fun setListeners() {

        btn_calendar_overtime_date.setOnClickListener {
            DatePickerDialog.setContentView(R.layout.dialog_date_picker)
            date = DatePickerDialog.findViewById(R.id.date_picker)
            DatePickerDialog.setTitle("Set Date")
            val button_set_date = DatePickerDialog.findViewById<Button>(R.id.button_set_date)
            val button_cancel_date_picker = DatePickerDialog.findViewById<Button>(R.id.button_cancel_date_picker)

            button_set_date.setOnClickListener {
                val nonZeroIndexedMonth = date.month + 1

                val m = if (nonZeroIndexedMonth < 10) "0" + nonZeroIndexedMonth else nonZeroIndexedMonth.toString()
                val d = if (date.dayOfMonth < 10) "0" + date.dayOfMonth else date.dayOfMonth.toString()
                val y = if (date.year < 10) "0" + date.year else date.year.toString()

                val string_date = "$y-$m-$d"
                val human_readable_date = helper.convertToReadableDate(string_date)

                txt_overtime_date.text = human_readable_date
                DatePickerDialog.dismiss()
            }

            button_cancel_date_picker.setOnClickListener(View.OnClickListener { DatePickerDialog.dismiss() })

            DatePickerDialog.show()
        }

        button_startTime.setOnClickListener {
            TimePickerDialog.setContentView(R.layout.dialog_time_picker)
            TimePickerDialog.setTitle("Set Time in")
            start_time = TimePickerDialog.findViewById(R.id.time_picker)
            start_time.is24HourView
            val button_set_date = TimePickerDialog.findViewById<Button>(R.id.button_set_date)
            val button_cancel_date_picker = TimePickerDialog.findViewById<Button>(R.id.button_cancel_date_picker)

            button_set_date.setOnClickListener(View.OnClickListener {
                val h = if (start_time.currentHour < 10) "0" + start_time.currentHour.toString() else start_time.currentHour.toString()
                val t = if (start_time.currentMinute < 10) "0" + start_time.currentMinute.toString() else start_time.currentMinute.toString()
                val string_start_time = "$h:$t"

                str_start_time = string_start_time

                val int_hr_hour = if (start_time.currentHour > 12) start_time.currentHour - 12 else start_time.currentHour
                val hr_hour = if (int_hr_hour < 10) "0$int_hr_hour" else int_hr_hour.toString()
                val AM_PM = if (start_time.currentHour > 12) "PM" else "AM"

                val human_readable_start_time = "$hr_hour:$t $AM_PM"

                txt_overtime_time_start.text = human_readable_start_time

                TimePickerDialog.dismiss()
            })

            button_cancel_date_picker.setOnClickListener(View.OnClickListener { TimePickerDialog.dismiss() })

            TimePickerDialog.show()
        }


        button_endTime.setOnClickListener {
            TimePickerDialog.setContentView(R.layout.dialog_time_picker)
            TimePickerDialog.setTitle("Set Time in")
            end_time = TimePickerDialog.findViewById(R.id.time_picker)
            end_time.is24HourView
            val button_set_date = TimePickerDialog.findViewById<Button>(R.id.button_set_date)
            val button_cancel_date_picker = TimePickerDialog.findViewById<Button>(R.id.button_cancel_date_picker)

            button_set_date.setOnClickListener {
                val h = if (end_time.currentHour < 10) "0" + end_time.currentHour.toString() else end_time.currentHour.toString()
                val t = if (end_time.currentMinute < 10) "0" + end_time.currentMinute.toString() else end_time.currentMinute.toString()
                val string_end_time = "$h:$t"

                str_end_time = string_end_time

                val int_hr_hour = if (end_time.currentHour > 12) end_time.currentHour - 12 else end_time.currentHour
                val hr_hour = if (int_hr_hour < 10) "0$int_hr_hour" else int_hr_hour.toString()
                val AM_PM = if (end_time.currentHour > 12) "PM" else "AM"
                val human_readable_end_time = "$hr_hour:$t $AM_PM"

                txt_overtime_time_end.text = human_readable_end_time
                TimePickerDialog.dismiss()
            }

            button_cancel_date_picker.setOnClickListener { TimePickerDialog.dismiss() }

            TimePickerDialog.show()
        }

        btn_overtime_request.setOnClickListener {
            val day = if (txt_overtime_date.text != null) txt_overtime_date.text.toString() else ""
            paramsChecker(day, str_start_time, str_end_time, txt_overtime_reason.text.toString())
        }

    }


    private fun paramsChecker(i_date: String, i_start_time: String, i_end_time: String, i_reason: String): Boolean {

        if (i_date.length > 0 && i_start_time.length > 0 && i_end_time.length > 0 && i_reason.length > 0) {

            sendOvertimeRequest(i_date, i_start_time, i_end_time, i_reason)
            return true
        } else {

            Toasty.error(context!!, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    private fun sendOvertimeRequest(i_date: String, i_start_time: String, i_end_time: String, i_reason: String) {
        val requestLoadingScreenDialog = ProgressDialog.show(context, null, "Sending Request...")

        val url = URLs()
        val user = SharedPrefManager.getInstance(context).user

        val url_create_overtime = url.url_create_overtime()

        val requestOvertime = object : StringRequest(Request.Method.POST, url_create_overtime, Response.Listener { response ->
            requestLoadingScreenDialog.dismiss()

            try {
                val response_obj = JSONObject(response)
                val status = response_obj.getString("status")

                if (status == "success") {
                    Toasty.success(context!!, "Success", Toast.LENGTH_SHORT).show()

                    activity!!.onBackPressed()
                    //resultChecker(mAdapter.filterByStatus("pending"));

                } else {
                    Toasty.error(context!!, response_obj.getString("msg"), Toast.LENGTH_SHORT).show()
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { error ->
            requestLoadingScreenDialog.dismiss()
            Toasty.error(context!!, error.message!!, Toast.LENGTH_SHORT).show()
        }) {
            override fun getHeaders(): Map<String, String> {
                return helper.headers()
            }

            override fun getParams(): Map<String, String> {

                val params = HashMap<String, String>()
                params["api_token"] = user.api_token
                params["link"] = user.link
                params["user_id"] = user.user_id
                params["date"] = i_date
                params["start_time"] = i_start_time
                params["end_time"] = i_end_time
                params["reason"] = i_reason
                return params
            }
        }

        requestOvertime.retryPolicy = DefaultRetryPolicy(60 * 1000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        helper.requestQueue.add(requestOvertime)
    }

}