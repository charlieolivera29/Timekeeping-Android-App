package com.example.timekeeping_beta.Fragments.MobileTimeEntry

import android.app.AlertDialog
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.timekeeping_beta.Fragments.Clock.ClockViewModel
import com.example.timekeeping_beta.Fragments.Clock.EDTR
import com.example.timekeeping_beta.Globals.Models.ApiResult
import com.example.timekeeping_beta.R
import es.dmoral.toasty.Toasty
import org.json.JSONArray
import org.json.JSONObject

class MobileTimeEntryFragment : Fragment() {

    private lateinit var v: View

    private lateinit var clockViewModel: ClockViewModel
    private lateinit var LocationViewModel: LocationViewModel

    private lateinit var toggleClockDialog: CardView
    private lateinit var tv_clock_in_out: TextView
    private lateinit var tv_time_in: TextView
    private lateinit var tv_time_out: TextView
    private lateinit var tv_date: TextView
    private lateinit var location_status: ImageView
    private lateinit var tv_location_message: TextView
    private lateinit var tv_shift: TextView

    private lateinit var edtr: EDTR
    private lateinit var location: JSONArray
    private lateinit var dialog: Dialog


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_mobile_time_entry, container, false)

        clockViewModel = ViewModelProviders.of(this).get(ClockViewModel::class.java)
        LocationViewModel = ViewModelProviders.of(activity!!).get(com.example.timekeeping_beta.Fragments.MobileTimeEntry.LocationViewModel::class.java)
        dialog = Dialog(v.context)

        return v
    }

    override fun onResume() {
        super.onResume()

        checkPermission()
    }

    private fun loadData() {

        loading()
        validateUserEDTR()
        getLocation()
    }

    private fun validateUserEDTR() {

        toggleClockDialog = v.findViewById<CardView>(R.id.cv_clock_in_out)
        tv_clock_in_out = v.findViewById<TextView>(R.id.tv_clock_in_out)
        tv_time_in = v.findViewById<TextView>(R.id.tv_time_in)
        tv_time_out = v.findViewById<TextView>(R.id.tv_time_out)
        tv_date = v.findViewById<TextView>(R.id.tv_date)
        tv_shift = v.findViewById<TextView>(R.id.tv_shift)

        clockViewModel.check_edtr_entry()

        clockViewModel.userEDTR.observe(this, Observer<EDTR?> {

            if (it != null) {

                edtr = it

                tv_time_in.text = edtr.time_in
                tv_time_out.text = edtr.time_out
                tv_date.text = edtr.date_in
                tv_shift.text = edtr.shift.toString()

                eDTRDialog()
                success()
            } else {
                error()
            }
        })

        clockViewModel.timeInOutResult.observe(this, Observer
        {
            if (it != null) {

                val apiResult: ApiResult = it

                if (it.status) {
                    Toasty.success(context!!, apiResult.message, Toasty.LENGTH_LONG).show()

                } else {
                    Toasty.error(context!!, apiResult.message, Toasty.LENGTH_LONG).show()
                }
            }
        })
    }

    private var locationManager: LocationManager? = null

    fun checkPermission() {

        locationManager = activity?.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager?
        val location_not_enabled = !locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)


        if (location_not_enabled) {
            showGPSDisabledAlertToUser()
        } else {
            loadData()
        }
    }

    private var userIsNearLocation = false

    private fun getLocation() {

        location_status = v.findViewById(R.id.location_status)
        tv_location_message = v.findViewById(R.id.tv_location_message)
        LocationViewModel.startLocationListener()

        LocationViewModel.currentLocation.observe(activity!!, Observer {

            location = it!!

            userIsNearLocation = userIsNearLocation(it!!)

            if (userIsNearLocation) {

                setLocationImage(ContextCompat.getDrawable(v.context, R.drawable.ic_my_location_valid_24dp),
                        "Location valid!",
                        ContextCompat.getColor(v.context, R.color.colorSuccess))


            } else {

                setLocationImage(ContextCompat.getDrawable(v.context, R.drawable.ic_my_location_invalid_24dp),
                        "Location Invalid!",
                        ContextCompat.getColor(v.context, R.color.colorError))
            }
        })
    }

    private fun setLocationImage(d: Drawable?, message: String, textcolor: Int) {

        activity?.runOnUiThread {

            kotlin.run {

                location_status.setImageDrawable(null)
                location_status.setImageDrawable(d!!)

                tv_location_message.text = message
                tv_location_message.setTextColor(textcolor)

                eDTRDialog()
            }
        }
    }


    fun eDTRDialog() {


        if (::edtr.isInitialized && userIsNearLocation) {
            val clockDialog = Dialog(context!!)
            clockDialog.setContentView(R.layout.dialog_clock_in_out)

            val tv_confirm_question = clockDialog.findViewById<TextView>(R.id.tv_confirm_question)
            val tv_clocked_in = clockDialog.findViewById<TextView>(R.id.tv_clocked_in)
            val tv_send_text = clockDialog.findViewById<TextView>(R.id.tv_send_text)
            val cv_send = clockDialog.findViewById<CardView>(R.id.cv_send)
            val tv_daily_edtr_title = v.findViewById<TextView>(R.id.anchr_title)

            val blank_time = context!!.getString(R.string.blank_time)

            val hasTimeinNoTimeout = edtr.time_in != blank_time && edtr.time_out == blank_time
            val noTimeinNoTimeout = edtr.time_in == blank_time && edtr.time_out == blank_time
            val hasBoth = edtr.time_in != blank_time && edtr.time_out != blank_time

            if (edtr.shift == 0) {

                if (hasTimeinNoTimeout) {

                    tv_daily_edtr_title.text = "Clock out"
                    tv_confirm_question.text = "Do you want to clock out now?"
                    tv_clocked_in.text = "Clocked in at: " + edtr.time_in
                    tv_send_text.text = "Time out"
                    cv_send.setCardBackgroundColor(resources.getColor(R.color.colorPending))
                    toggleClockDialog.setCardBackgroundColor(resources.getColor(R.color.colorPending))
                    tv_clock_in_out.text = "Time out"

                    toggleClockDialog.visibility = View.VISIBLE
                    toggleClockDialog.setOnClickListener { clockDialog.show() }
                } else if (noTimeinNoTimeout) {

                    tv_daily_edtr_title.text = "Clock in"
                    tv_confirm_question.text = "Do you want to clock in now?"
                    tv_clocked_in.text = "Not yet clocked in"
                    tv_send_text.text = "Time in"
                    cv_send.setCardBackgroundColor(resources.getColor(R.color.colorSuccess))
                    toggleClockDialog.setCardBackgroundColor(resources.getColor(R.color.colorSuccess))
                    tv_clock_in_out.text = "Time in"

                    toggleClockDialog.visibility = VISIBLE
                    toggleClockDialog.setOnClickListener { clockDialog.show() }
                } else {
                    tv_daily_edtr_title.text = "EDTR"
                    toggleClockDialog.visibility = View.GONE
                }
            }

            if (edtr.shift == 1) {

                if (hasTimeinNoTimeout) {

                    tv_daily_edtr_title.text = "Clock out"
                    tv_confirm_question.text = "Do you want to clock out now?"
                    tv_clocked_in.text = "Clocked in at: " + edtr.time_in
                    tv_send_text.text = "Time out"
                    cv_send.setCardBackgroundColor(resources.getColor(R.color.colorPending))
                    toggleClockDialog.setCardBackgroundColor(resources.getColor(R.color.colorPending))
                    tv_clock_in_out.text = "Time out"

                    toggleClockDialog.visibility = View.VISIBLE
                    toggleClockDialog.setOnClickListener { clockDialog.show() }
                } else if (hasBoth) {

                    tv_daily_edtr_title.text = "Clock in"
                    tv_confirm_question.text = "Do you want to clock in now?"
                    tv_clocked_in.text = "Not yet clocked in"
                    tv_send_text.text = "Time in"
                    cv_send.setCardBackgroundColor(resources.getColor(R.color.colorSuccess))
                    toggleClockDialog.setCardBackgroundColor(resources.getColor(R.color.colorSuccess))
                    tv_clock_in_out.text = "Time in"

                    toggleClockDialog.visibility = VISIBLE
                    toggleClockDialog.setOnClickListener { clockDialog.show() }
                }
            }

            if (edtr.shift == 2) {
                if (hasTimeinNoTimeout) {

                    tv_daily_edtr_title.text = "Clock out"
                    tv_confirm_question.text = "Do you want to clock out now?"
                    tv_clocked_in.text = "Clocked in at: " + edtr.time_in
                    tv_send_text.text = "Time out"
                    cv_send.setCardBackgroundColor(resources.getColor(R.color.colorPending))
                    toggleClockDialog.setCardBackgroundColor(resources.getColor(R.color.colorPending))
                    tv_clock_in_out.text = "Time out"

                    toggleClockDialog.visibility = View.VISIBLE
                    toggleClockDialog.setOnClickListener { clockDialog.show() }
                } else {
                    tv_daily_edtr_title.text = "EDTR"
                    toggleClockDialog.visibility = View.GONE
                }
            }



            cv_send.setOnClickListener {
                loading()
                clockDialog.dismiss()
                clockViewModel.sendTimeInOut()
            }
        }

    }


    fun showGPSDisabledAlertToUser() {


        val alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setMessage("GPS is disabled in your device.\nPlease enable it and select High accuracy.")
                .setCancelable(false)
                .setPositiveButton("Go to Settings Page"
                ) { dialog, id ->
                    val callGPSSettingIntent = Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS
                    )
                    startActivity(callGPSSettingIntent)
                }
        //alertDialogBuilder.setNegativeButton("Cancel",
        //{ dialog, id -> dialog.cancel() })
        val alert = alertDialogBuilder.create()
        alert.show()
    }

    private fun userIsNearLocation(loc: JSONArray): Boolean {

        val json_array_location = loc
        val json_object_location = json_array_location.get(0) as JSONObject

        val lat = json_object_location.get("latitude").toString()
        val long = json_object_location.get("longitude").toString()


        val company_location = Location(LocationManager.NETWORK_PROVIDER)

        //Pasig
        //company_location.latitude = 14.567721
        //company_location.longitude = 121.066256

        //Bahay
        //company_location.latitude = 14.5686977
        //company_location.longitude = 121.0147036

        //Zolvere
        company_location.latitude = 14.5708555
        company_location.longitude = 121.0160013

        val current_location = Location(LocationManager.NETWORK_PROVIDER)
        current_location.latitude = lat.toDouble()
        current_location.longitude = long.toDouble()

        val difference = company_location.distanceTo(current_location)

        val allowedDistance = 50

        //Uncomment to check distance
        //return difference < allowedDistance
        return true
        //Uncomment to check distance
    }

    override fun onDestroyView() {
        super.onDestroyView()

        LocationViewModel.stopUpdates()
    }

    fun loading() {

        if (dialog.isShowing) dialog.dismiss()
        dialog.setContentView(R.layout.dialog_loading_screen);
        dialog.setCancelable(false)
        dialog.show()
    }

    fun error() {

        if (dialog.isShowing) dialog.dismiss()
        dialog.setContentView(R.layout.dialog_try_again);
        dialog.setCancelable(false)

        dialog.findViewById<TextView>(R.id.link_try_again).setOnClickListener { loadData() }

        dialog.show()
    }

    fun success() {
        if (dialog.isShowing) dialog.dismiss()
    }
}