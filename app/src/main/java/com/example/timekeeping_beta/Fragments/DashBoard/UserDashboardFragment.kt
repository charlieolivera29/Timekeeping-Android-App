package com.example.timekeeping_beta.Fragments.DashBoard

import android.app.DatePickerDialog
import android.app.Dialog
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.AppCompatImageButton
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import com.example.timekeeping_beta.Fragments.Clock.ClockViewModel
import com.example.timekeeping_beta.Fragments.Clock.EDTR
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestEDTR.TimesheetEntryFragment
import com.example.timekeeping_beta.Fragments.DashBoard.Models.DashboardAttendance
import com.example.timekeeping_beta.Fragments.DashBoard.Models.DashboardCount
import com.example.timekeeping_beta.Fragments.HRAdmin.Dashboard.HRDashboardEmployeeList
import com.example.timekeeping_beta.Fragments.MobileTimeEntry.MobileTimeEntryFragment
import com.example.timekeeping_beta.Fragments.Timesheet.TimesheetFragment
import com.example.timekeeping_beta.Fragments.UserApprover.Adjustment.AdjustmentsUpdateFragmentv2
import com.example.timekeeping_beta.Fragments.UserApprover.EDTR.EDTRUpdateFragmentv2
import com.example.timekeeping_beta.Fragments.UserApprover.Leave.LeaveUpdateFragmentv2
import com.example.timekeeping_beta.Fragments.UserApprover.Overtime.OvertimeUpdateFragmentv2
import com.example.timekeeping_beta.Fragments.UserEmployee.Adjustments.AdjustmentsFragment
import com.example.timekeeping_beta.Fragments.UserEmployee.Leaves.LeavesFragment
import com.example.timekeeping_beta.Fragments.UserEmployee.Overtimes.OvertimeFragment
import com.example.timekeeping_beta.Globals.CustomClasses.Flag
import com.example.timekeeping_beta.Globals.Helper
import com.example.timekeeping_beta.Globals.Models.ApiResult
import com.example.timekeeping_beta.Globals.Models.User
import com.example.timekeeping_beta.Globals.SharedPrefManager
import com.example.timekeeping_beta.R
import es.dmoral.toasty.Toasty
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class UserDashboardFragment : Fragment() {

    private lateinit var ctx: Context;
    private lateinit var helper: Helper;
    //Dashboard Filter
    private lateinit var user: User

    private var date_type: String = "today"
    private var user_type: String = "user"
    //Dashboard Filter

    private lateinit var dashboardAttendance: LiveData<DashboardAttendance>

    private lateinit var v: View
    private lateinit var ll_daily_edtr_container: LinearLayout


    private lateinit var userDashboardViewModel: UserDashboardViewModel
    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var clockViewModel: ClockViewModel

    private lateinit var tv_attendance_percentage: TextView
    private lateinit var tv_total_absent: TextView
    private lateinit var tv_total_present: TextView
    private lateinit var tv_total_lates: TextView
    private lateinit var tv_total_overtime: TextView


    //Request
    private lateinit var time_adjustment_quantity: TextView
    private lateinit var overtime_request_quantity: TextView
    private lateinit var leave_request_quantity: TextView
    private lateinit var edtr_request_quantity: TextView

    //Approver
    private lateinit var pending_approvals_container: CardView
    private lateinit var time_adjustment_approvals_quantity: TextView
    private lateinit var overtime_appprovals_quantity: TextView
    private lateinit var leave_approval_quantity: TextView
    private lateinit var edtr_approvals_quantity: TextView


    private lateinit var filterDialog: Dialog
    private lateinit var iv_toggle_filter: ImageView
    private lateinit var ll_toggle_filter: LinearLayout
    private lateinit var userTypeOptions: ArrayList<String>
    private lateinit var dayTypeOptions: ArrayList<String>
    private lateinit var spnnr_user_types: Spinner
    private lateinit var spnnr_date_types: Spinner

    private lateinit var tv_cancel: TextView
    private lateinit var tv_filter: TextView
    private lateinit var srl_refresh: SwipeRefreshLayout
    private lateinit var try_again_layout: RelativeLayout

    //Links
    private lateinit var link_pending_edtr: FrameLayout
    private lateinit var link_pending_time_adjustment: FrameLayout
    private lateinit var link_pending_overtime: FrameLayout
    private lateinit var link_pending_leaves: FrameLayout
    private lateinit var link_pending_leaves_approvals: FrameLayout
    private lateinit var link_pending_approvals_overtime: FrameLayout
    private lateinit var link_pending_edtr_approvals: FrameLayout
    private lateinit var link_pending_time_adjustment_approvals: FrameLayout

    private lateinit var cv_attendance_percentage: CardView
    private lateinit var cv_total_lates: CardView
    private lateinit var cv_total_overtime: CardView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.fragment_dashboard_user, container, false)
        ctx = v.context;

        init()
        initViews()
        setListeners()
        validateUserAccess()
        loadDashboardData(range_from, range_to, date_type, user_type)
        setData()

        return v
    }

    private fun init() {
        helper = Helper.getInstance(ctx);

        userDashboardViewModel = ViewModelProviders.of(activity!!).get(UserDashboardViewModel::class.java)
        dashboardViewModel = ViewModelProviders.of(activity!!).get(DashboardViewModel::class.java)
        clockViewModel = ViewModelProviders.of(activity!!).get(ClockViewModel::class.java)

        user = SharedPrefManager.getInstance(context).user
        filterDialog = Dialog(activity!!)
    }

    private fun initViews() {

        filterDialog.setContentView(R.layout.dialog_filter_dashboard)

        ll_daily_edtr_container = v.findViewById(R.id.ll_daily_edtr_container)
        tv_attendance_percentage = v.findViewById(R.id.tv_attendance_percentage)
        tv_total_absent = v.findViewById(R.id.tv_total_absent)
        tv_total_present = v.findViewById(R.id.tv_total_present)
        tv_total_lates = v.findViewById(R.id.tv_total_lates)
        tv_total_overtime = v.findViewById(R.id.tv_total_overtime)

        time_adjustment_quantity = v.findViewById<TextView>(R.id.time_adjustment_quantity)
        overtime_request_quantity = v.findViewById<TextView>(R.id.overtime_request_quantity)
        leave_request_quantity = v.findViewById<TextView>(R.id.leave_request_quantity)

        link_pending_edtr = v.findViewById<FrameLayout>(R.id.link_pending_edtr)
        link_pending_time_adjustment = v.findViewById<FrameLayout>(R.id.link_pending_time_adjustment)
        link_pending_overtime = v.findViewById<FrameLayout>(R.id.link_pending_overtime)
        link_pending_leaves = v.findViewById<FrameLayout>(R.id.link_pending_leaves)

        link_pending_leaves_approvals = v.findViewById<FrameLayout>(R.id.link_pending_leaves_approvals)
        link_pending_approvals_overtime = v.findViewById<FrameLayout>(R.id.link_pending_approvals_overtime)
        link_pending_edtr_approvals = v.findViewById<FrameLayout>(R.id.link_pending_edtr_approvals)
        link_pending_time_adjustment_approvals = v.findViewById<FrameLayout>(R.id.link_pending_time_adjustment_approvals)


        edtr_request_quantity = v.findViewById<TextView>(R.id.edtr_request_quantity)

        pending_approvals_container = v.findViewById(R.id.pending_approvals_container)
        time_adjustment_approvals_quantity = v.findViewById<TextView>(R.id.time_adjustment_approvals_quantity)
        overtime_appprovals_quantity = v.findViewById<TextView>(R.id.overtime_appprovals_quantity)
        leave_approval_quantity = v.findViewById<TextView>(R.id.leave_approval_quantity)
        edtr_approvals_quantity = v.findViewById<TextView>(R.id.edtr_approvals_quantity)
        srl_refresh = v.findViewById<SwipeRefreshLayout>(R.id.srl_refresh)
        try_again_layout = v.findViewById<RelativeLayout>(R.id.try_again_layout)


        iv_toggle_filter = v.findViewById<ImageView>(R.id.iv_toggle_filter)
        ll_toggle_filter = v.findViewById(R.id.ll_toggle_filter)
        spnnr_user_types = filterDialog.findViewById(R.id.spnnr_user_types)
        spnnr_date_types = filterDialog.findViewById(R.id.spnnr_date_types)

        tv_cancel = filterDialog.findViewById(R.id.tv_cancel)
        tv_filter = filterDialog.findViewById(R.id.tv_filter)


        cv_attendance_percentage = v.findViewById(R.id.cv_attendance_percentage);
        cv_total_lates = v.findViewById(R.id.cv_total_lates);
        cv_total_overtime = v.findViewById(R.id.cv_total_overtime);
    }

    private fun validateUserAccess() {

        if (user.isApprover == "1") {
            pending_approvals_container.visibility = View.VISIBLE
        }

        try {
            val ja_user_bundees = JSONArray(user.user_bundees)

            if (ja_user_bundees.length() > 0) {
                for (i in 0 until ja_user_bundees.length()) {

                    val bundee = ja_user_bundees.getInt(i)

                    //Bundee has EDTR
                    if (bundee == 1001) {
                        link_pending_edtr.visibility = VISIBLE

                        val navigation = activity!!.findViewById<NavigationView>(R.id.nav_view)
                        val nav_Approvals_Menu = navigation.menu

                        nav_Approvals_Menu.findItem(R.id.nav_timesheet_request).setVisible(true)
                    }

                    //Bundee has Moblie
                    else if (bundee == 1003) {
                        ll_daily_edtr_container.visibility = VISIBLE
                        //validateUserEDTR()

                        ll_daily_edtr_container.setOnClickListener {

                            activity!!.supportFragmentManager
                                    .beginTransaction()
                                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out) //.setCustomAnimations(R.anim.slide_up, R.anim.slide_bottom)
                                    .add(R.id.fragment_container, MobileTimeEntryFragment(), "MobileTimeEntryFragment")
                                    .addToBackStack("MobileTimeEntryFragment")
                                    .commit()
                        }
                    }

                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }


    }

    private fun validateUserEDTR() {

        val toggleClockDialog = v.findViewById<CardView>(R.id.cv_clock_in_out)
        val tv_clock_in_out = v.findViewById<TextView>(R.id.tv_clock_in_out)
        val tv_time_in = v.findViewById<TextView>(R.id.tv_time_in)
        val tv_time_out = v.findViewById<TextView>(R.id.tv_time_out)

        clockViewModel.check_edtr_entry()
        clockViewModel.userEDTR.observe(this, Observer<EDTR?> {
            val edtr: EDTR? = it

            if (edtr != null) {

                tv_time_in.text = edtr.time_in
                tv_time_out.text = edtr.time_out

                val clockDialog = Dialog(context!!)
                clockDialog.setContentView(R.layout.dialog_clock_in_out)

                val tv_confirm_question = clockDialog.findViewById<TextView>(R.id.tv_confirm_question)
                val tv_clocked_in = clockDialog.findViewById<TextView>(R.id.tv_clocked_in)
                val tv_send_text = clockDialog.findViewById<TextView>(R.id.tv_send_text)
                val cv_send = clockDialog.findViewById<CardView>(R.id.cv_send)
                val tv_daily_edtr_title = v.findViewById<TextView>(R.id.tv_daily_edtr_title)

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

                        //toggleClockDialog.visibility = VISIBLE
                        //toggleClockDialog.setOnClickListener { clockDialog.show() }
                    } else if (noTimeinNoTimeout) {

                        tv_daily_edtr_title.text = "Clock in"
                        tv_confirm_question.text = "Do you want to clock in now?"
                        tv_clocked_in.text = "Not yet clocked in"
                        tv_send_text.text = "Time in"
                        cv_send.setCardBackgroundColor(resources.getColor(R.color.colorSuccess))
                        toggleClockDialog.setCardBackgroundColor(resources.getColor(R.color.colorSuccess))
                        tv_clock_in_out.text = "Time in"

                        //toggleClockDialog.visibility = VISIBLE
                        //toggleClockDialog.setOnClickListener { clockDialog.show() }
                    } else {
                        tv_daily_edtr_title.text = "EDTR"
                        toggleClockDialog.visibility = GONE
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

                        //toggleClockDialog.visibility = VISIBLE
                        //toggleClockDialog.setOnClickListener { clockDialog.show() }
                    } else if (hasBoth) {

                        tv_daily_edtr_title.text = "Clock in"
                        tv_confirm_question.text = "Do you want to clock in now?"
                        tv_clocked_in.text = "Not yet clocked in"
                        tv_send_text.text = "Time in"
                        cv_send.setCardBackgroundColor(resources.getColor(R.color.colorSuccess))
                        toggleClockDialog.setCardBackgroundColor(resources.getColor(R.color.colorSuccess))
                        tv_clock_in_out.text = "Time in"

                        //toggleClockDialog.visibility = VISIBLE
                        //toggleClockDialog.setOnClickListener { clockDialog.show() }
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

                        //toggleClockDialog.visibility = VISIBLE
                        //toggleClockDialog.setOnClickListener { clockDialog.show() }
                    } else {
                        tv_daily_edtr_title.text = "EDTR"
                        toggleClockDialog.visibility = GONE
                    }
                }



                cv_send.setOnClickListener {
                    clockDialog.dismiss()
                    clockViewModel.sendTimeInOut(clockViewModel.latitude, clockViewModel.longitude)
                }
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

    private fun setListeners() {

        userDashboardViewModel
                .dashboardAttendance
                .observe(this,
                        Observer<DashboardAttendance> {

                            if (it != null) {
                                val dashboardAttendance = it

                                tv_attendance_percentage.text = dashboardAttendance.percentage.toString()
                                tv_total_absent.text = dashboardAttendance.total_absent.toString()
                                tv_total_present.text = dashboardAttendance.present.toString()

                                success()
                            } else {
                                error()
                            }
                        }
                )

        userDashboardViewModel.dashboardLatesCounts.observe(this, Observer<DashboardCount> {

            val dashboardCount = it

            tv_total_lates.text = if (dashboardCount?.total_hrs.toString() != "null") dashboardCount?.total_hrs.toString() else "0"

            if (dashboardCount != null && dashboardCount.names.length() > 0) {
                tv_total_lates.setOnClickListener {

                    val goToFragment: Fragment = HRDashboardEmployeeList()

                    val b = Bundle()
                    b.putInt("employees_list_flag", Flag.DASHBOARD_LATE_EMPLOYEES)
                    goToFragment.arguments = b

                    activity!!
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                            .add(R.id.fragment_container, goToFragment, HRDashboardEmployeeList::class.java.name)
                            .addToBackStack(HRDashboardEmployeeList::class.java.name)
                            .commit()
                }
            } else {
                tv_total_lates.setOnClickListener {}
            }
        })
        userDashboardViewModel.dashboardOvertimeCounts.observe(this, Observer<DashboardCount> {

            val dashboardCount = it

            tv_total_overtime.text = if (dashboardCount?.total_hrs.toString() != "null") dashboardCount?.total_hrs.toString() else "0"
            if (dashboardCount != null && dashboardCount.names.length() > 0) {
                tv_total_overtime.setOnClickListener {

                    val goToFragment: Fragment = HRDashboardEmployeeList()

                    val b = Bundle()
                    b.putInt("employees_list_flag", Flag.DASHBOARD_OVERTIME_EMPLOYEES)
                    goToFragment.arguments = b

                    activity!!
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                            .add(R.id.fragment_container, goToFragment, HRDashboardEmployeeList::class.java.name)
                            .addToBackStack(HRDashboardEmployeeList::class.java.name)
                            .commit()
                }
            } else {
                tv_total_overtime.setOnClickListener {}
            }
        })


        dashboardViewModel.pendingRequests.observe(this, Observer { dashboardCounts ->
            if (dashboardCounts != null) {

                val adj_count = dashboardCounts.adjustment.toString()
                val ot_count = dashboardCounts.overtime.toString()
                val lv_count = dashboardCounts.leave.toString()
                val ts_count = dashboardCounts.time_approval.toString()

                time_adjustment_quantity.text = adj_count
                overtime_request_quantity.text = ot_count
                leave_request_quantity.text = lv_count
                edtr_request_quantity.text = ts_count

                initializeRequestCountDrawer(adj_count, ot_count, lv_count, ts_count)
            }
        })

        dashboardViewModel.pendingApprovals.observe(this, Observer { dashboardCounts ->
            if (dashboardCounts != null) {

                val adj_count = dashboardCounts.adjustment.toString()
                val ot_count = dashboardCounts.overtime.toString()
                val lv_count = dashboardCounts.leave.toString()
                val edtr_count = dashboardCounts.time_approval.toString()

                val ts_count = dashboardCounts.time_approval.toString()

                time_adjustment_approvals_quantity.text = adj_count
                overtime_appprovals_quantity.text = ot_count
                leave_approval_quantity.text = lv_count
                this.edtr_approvals_quantity.text = edtr_count

                initializeApprovalCountDrawer(adj_count, ot_count, lv_count, ts_count)
            }
        })

        ll_toggle_filter.setOnClickListener {
            filterDialog.show()
        }


        tv_cancel.setOnClickListener {
            filterDialog.dismiss()
        }

        tv_filter.setOnClickListener {
            filterDialog.dismiss()
            loadDashboardData(range_from, range_to, date_type, user_type)
        }

        srl_refresh.setOnRefreshListener {
            loadDashboardData(range_from, range_to, date_type, user_type)
        }


        filterDates()


        link_pending_time_adjustment.setOnClickListener { addFragmentToContainer("AdjustmentsFragment", AdjustmentsFragment()) }
        link_pending_overtime.setOnClickListener { addFragmentToContainer("OvertimeFragment", OvertimeFragment()) }
        link_pending_leaves.setOnClickListener { addFragmentToContainer("LeavesFragment", LeavesFragment()) }
        link_pending_edtr.setOnClickListener { addFragmentToContainer("TimesheetEntryFragment", TimesheetEntryFragment()) }

        link_pending_leaves_approvals.setOnClickListener { addFragmentToContainer("LeaveUpdateFragmentv2", LeaveUpdateFragmentv2()) }
        link_pending_approvals_overtime.setOnClickListener { addFragmentToContainer("OvertimeUpdateFragmentv2", OvertimeUpdateFragmentv2()) }
        link_pending_edtr_approvals.setOnClickListener { addFragmentToContainer("EDTRUpdateFragmentv2", EDTRUpdateFragmentv2()) }
        link_pending_time_adjustment_approvals.setOnClickListener { addFragmentToContainer("AdjustmentsUpdateFragmentv2", AdjustmentsUpdateFragmentv2()) }

        cv_attendance_percentage.setOnClickListener {
            //Toasty.info(ctx,"This card shows the percentage of your attendance. Results may vary depending on the filter you set",Toasty.LENGTH_LONG).show()

            //Adds to backstack
            val act = activity

            if (act != null) {
                act.getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out) //.setCustomAnimations(R.anim.slide_up, R.anim.slide_bottom)
                        .add(R.id.fragment_container, TimesheetFragment(), "TimesheetFragment")
                        .addToBackStack("TimesheetFragment")
                        .commit()
            }
        }
        cv_total_lates.setOnClickListener { Toasty.info(ctx, "This card shows your lates in hours. Results may vary depending on the filter you set", Toasty.LENGTH_LONG).show() }
        cv_total_overtime.setOnClickListener { Toasty.info(ctx, "This card shows your overtime in hours. Results may vary depending on the filter you set", Toasty.LENGTH_LONG).show() }
    }

    private fun addFragmentToContainer(fragment_name: String, fragment: Fragment) {
        //Adds to backstack
        activity!!
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out) //.setCustomAnimations(R.anim.slide_up, R.anim.slide_bottom)
                .add(R.id.fragment_container, fragment, fragment_name)
                .addToBackStack(fragment_name)
                .commit()
    }

    private var range_from: String = ""
    private var range_to: String = ""


    private lateinit var txt_range_from: TextView
    private lateinit var txt_range_to: TextView

    private lateinit var btn_range_from: AppCompatImageButton
    private lateinit var btn_range_to: AppCompatImageButton

    private fun filterDates() {

        txt_range_from = filterDialog.findViewById(R.id.txt_range_from)
        txt_range_to = filterDialog.findViewById(R.id.txt_range_to)

        btn_range_from = filterDialog.findViewById(R.id.btn_range_from)
        btn_range_to = filterDialog.findViewById(R.id.btn_range_to)

        btn_range_from.setOnClickListener {
            val newCalendar = Calendar.getInstance();
            val startDate = DatePickerDialog(
                    context,
                    DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                        // TODO Auto-generated method stub
                        newCalendar.set(Calendar.YEAR, year)
                        newCalendar.set(Calendar.MONTH, month)
                        newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        val stringRangeFrom = helper.createStringDateFromDatePickerDialog(year, month, dayOfMonth)

                        txt_range_from.setText(helper.convertToReadableDate(stringRangeFrom))
                        range_from = stringRangeFrom
                    }, newCalendar.get(Calendar.YEAR),
                    newCalendar.get(Calendar.MONTH),
                    newCalendar.get(Calendar.DAY_OF_MONTH))
            startDate.show()
        }

        btn_range_to.setOnClickListener {
            val newCalendar = Calendar.getInstance();
            val endDate = DatePickerDialog(
                    context,
                    DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                        // TODO Auto-generated method stub
                        newCalendar.set(Calendar.YEAR, year)
                        newCalendar.set(Calendar.MONTH, month)
                        newCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        val stringRangeFrom = helper.createStringDateFromDatePickerDialog(year, month, dayOfMonth)

                        txt_range_to.setText(helper.convertToReadableDate(stringRangeFrom))
                        range_to = stringRangeFrom
                    }, newCalendar.get(Calendar.YEAR),
                    newCalendar.get(Calendar.MONTH),
                    newCalendar.get(Calendar.DAY_OF_MONTH))
            endDate.show()
        }
    }

    private fun loadDashboardData(date_start: String, date_end: String, day_type: String, user_type: String) {

        val dt = if (date_start != "" && date_end != "" && day_type != "weekly") "date_range" else day_type

        loading()
        userDashboardViewModel.retrieveDashboardAttendance(date_start, date_end, dt, user_type)
        userDashboardViewModel.retrieveOvertimeCounts(date_start, date_end, dt, user_type)
        userDashboardViewModel.retrieveLateCounts(date_start, date_end, dt, user_type)

        dashboardViewModel.retrievePendingRequests()
        dashboardViewModel.retrievePendingApprovals()
    }

    private fun setData() {

        userTypeOptions = userDashboardViewModel.getDashboardOptionsUserTypes()
        dayTypeOptions = userDashboardViewModel.getDashboardOptionsDayTypes()

        //fill data in spinner
        val adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, userTypeOptions)
        spnnr_user_types.adapter = adapter
        //Sets default value
        spnnr_user_types.setSelection(0, false)
        spnnr_user_types.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                if (parent != null) {
                    val selected: String = parent.selectedItem as String

                    if (selected == "Personal") {
                        user_type = "user"
                    } else if (selected == "Company") {
                        user_type = "hradmin"
                    } else if (selected == "Team") {
                        user_type = "approver"
                    }
                }
            }
        }

        //fill data in spinner
        val adapter_date_type = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, dayTypeOptions)
        spnnr_date_types.adapter = adapter_date_type
        //Sets default value
        spnnr_date_types.setSelection(0, false)
        spnnr_date_types.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                if (parent != null) {
                    val selected: String = parent.selectedItem as String

                    if (selected == "Today") {
                        date_type = "daily"
                    } else if (selected == "Weekly") {
                        date_type = "weekly"
                    }
                }
            }
        }


    }


    // Sets badge inside request appbar
    private fun initializeRequestCountDrawer(adjustment_request_count: String, overtime_request_count: String, leave_request_count: String, edtr_request_count: String) {

        val navigation = activity!!.findViewById<NavigationView>(R.id.nav_view)
        val nav_Approvals_Menu = navigation.menu

        if (adjustment_request_count != "0") {
            val itemActionView = nav_Approvals_Menu.findItem(R.id.nav_adjustment_request).actionView
            val v = View.inflate(activity, R.layout.badge, null)

            if (itemActionView == null) {
                val tvAdjCount = v.findViewById<TextView>(R.id.count)
                tvAdjCount.text = adjustment_request_count
                nav_Approvals_Menu.findItem(R.id.nav_adjustment_request).actionView = v
            } else {
                val tv = itemActionView.findViewById<TextView>(R.id.count)
                tv.text = adjustment_request_count
            }
        }

        if (edtr_request_count != "0") {
            val itemActionView = nav_Approvals_Menu.findItem(R.id.nav_timesheet_request).actionView
            val v = View.inflate(activity, R.layout.badge, null)

            if (itemActionView == null) {
                val tvAdjCount = v.findViewById<TextView>(R.id.count)
                tvAdjCount.text = edtr_request_count
                nav_Approvals_Menu.findItem(R.id.nav_timesheet_request).actionView = v
            } else {
                val tv = itemActionView.findViewById<TextView>(R.id.count)
                tv.text = edtr_request_count
            }
        }

        if (adjustment_request_count == "0") {

            val menuItem = nav_Approvals_Menu.findItem(R.id.nav_adjustment_request)

            if (menuItem.actionView != null) {
                menuItem.actionView = null
            }
        }

        if (overtime_request_count != "0") {
            val itemActionView = nav_Approvals_Menu.findItem(R.id.nav_overtime_request).actionView
            val v = View.inflate(activity, R.layout.badge, null)

            if (itemActionView == null) {
                val tvAdjCount = v.findViewById<TextView>(R.id.count)
                tvAdjCount.text = overtime_request_count
                nav_Approvals_Menu.findItem(R.id.nav_overtime_request).actionView = v
            } else {
                val tv = itemActionView.findViewById<TextView>(R.id.count)
                tv.text = overtime_request_count
            }
        }
        if (overtime_request_count == "0") {
            val menuItem = nav_Approvals_Menu.findItem(R.id.nav_overtime_request)

            if (menuItem.actionView != null) {
                menuItem.actionView = null
            }
        }

        if (leave_request_count != "0") {
            val itemActionView = nav_Approvals_Menu.findItem(R.id.nav_leave_request).actionView
            val v = View.inflate(activity, R.layout.badge, null)

            if (itemActionView == null) {
                val tvAdjCount = v.findViewById<TextView>(R.id.count)
                tvAdjCount.text = leave_request_count
                nav_Approvals_Menu.findItem(R.id.nav_leave_request).actionView = v
            } else {
                val tv = itemActionView.findViewById<TextView>(R.id.count)
                tv.text = leave_request_count
            }
        }
        if (leave_request_count == "0") {
            val menuItem = nav_Approvals_Menu.findItem(R.id.nav_leave_request)

            if (menuItem.actionView != null) {
                menuItem.actionView = null
            }
        }
    }

    // Sets badge inside approver appbar
    private fun initializeApprovalCountDrawer(adjustment_approval_count: String, overtime_appoval_count: String, leave_approval_count: String, timesheet_approval_count: String) {

        val navigation = activity!!.findViewById<NavigationView>(R.id.nav_view)
        val nav_Approvals_Menu = navigation.menu

        if (adjustment_approval_count != "0") {
            val itemActionView = nav_Approvals_Menu.findItem(R.id.nav_time_approvals).actionView

            if (itemActionView == null) {
                val v = View.inflate(activity, R.layout.badge, null)
                val tvAdjCount = v.findViewById<TextView>(R.id.count)
                tvAdjCount.text = adjustment_approval_count

                nav_Approvals_Menu.findItem(R.id.nav_time_approvals).actionView = v
            } else {
                val tv = itemActionView.findViewById<TextView>(R.id.count)
                tv.text = adjustment_approval_count
            }
        }

        if (timesheet_approval_count != "0") {
            val itemActionView = nav_Approvals_Menu.findItem(R.id.nav_timesheet_Approvals).actionView

            if (itemActionView == null) {
                val v = View.inflate(activity, R.layout.badge, null)
                val tvAdjCount = v.findViewById<TextView>(R.id.count)
                tvAdjCount.text = timesheet_approval_count

                nav_Approvals_Menu.findItem(R.id.nav_timesheet_Approvals).actionView = v
            } else {
                val tv = itemActionView.findViewById<TextView>(R.id.count)
                tv.text = timesheet_approval_count
            }
        }

        if (adjustment_approval_count == "0") {
            val menuItem = nav_Approvals_Menu.findItem(R.id.nav_time_approvals)

            if (menuItem.actionView != null) {
                menuItem.actionView = null
            }
        }

        if (overtime_appoval_count != "0") {
            val itemActionView = nav_Approvals_Menu.findItem(R.id.nav_overtime_approvals).actionView

            if (itemActionView == null) {
                val v = View.inflate(activity, R.layout.badge, null)
                val tvAdjCount = v.findViewById<TextView>(R.id.count)
                tvAdjCount.text = overtime_appoval_count

                nav_Approvals_Menu.findItem(R.id.nav_overtime_approvals).actionView = v
            } else {
                val tv = itemActionView.findViewById<TextView>(R.id.count)
                tv.text = overtime_appoval_count
            }
        }

        if (overtime_appoval_count == "0") {
            val menuItem = nav_Approvals_Menu.findItem(R.id.nav_overtime_approvals)

            if (menuItem.actionView != null) {
                menuItem.actionView = null
            }
        }


        if (leave_approval_count != "0") {
            //R
            val itemActionView = nav_Approvals_Menu.findItem(R.id.nav_leave_approvals).actionView
            val v = View.inflate(activity, R.layout.badge, null)

            if (itemActionView == null) {
                val tvAdjCount = v.findViewById<TextView>(R.id.count)

                //R
                tvAdjCount.text = leave_approval_count

                //R
                nav_Approvals_Menu.findItem(R.id.nav_leave_approvals).actionView = v
            } else {
                val tv = itemActionView.findViewById<TextView>(R.id.count)
                //R
                tv.text = leave_approval_count
            }
        }
        //R
        if (leave_approval_count == "0") {

            //R
            val menuItem = nav_Approvals_Menu.findItem(R.id.nav_leave_approvals)

            if (menuItem.actionView != null) {
                menuItem.actionView = null
            }
        }
    }


    //States
    fun loading() {
        srl_refresh.isRefreshing = true
        try_again_layout.visibility = GONE

    }

    fun success() {
        srl_refresh.isRefreshing = false
        try_again_layout.visibility = GONE

    }

    fun error() {
        srl_refresh.isRefreshing = false
        try_again_layout.visibility = VISIBLE
    }
}