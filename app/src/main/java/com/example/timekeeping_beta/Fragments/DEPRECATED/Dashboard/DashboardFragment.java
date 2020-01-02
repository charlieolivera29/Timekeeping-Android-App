package com.example.timekeeping_beta.Fragments.DEPRECATED.Dashboard;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.timekeeping_beta.Fragments.DashBoard.Models.Dashboard;
import com.example.timekeeping_beta.Fragments.DashBoard.Models.DashboardCounts;
import com.example.timekeeping_beta.Fragments.DashBoard.DashboardViewModel;
import com.example.timekeeping_beta.Fragments.UserApprover.Adjustment.AdjustmentsUpdateFragment;
import com.example.timekeeping_beta.Fragments.UserApprover.Leave.LeaveUpdateFragment;
import com.example.timekeeping_beta.Fragments.UserApprover.Overtime.OvertimeUpdateFragment;
import com.example.timekeeping_beta.Fragments.Clock.ClockViewModel;
import com.example.timekeeping_beta.Fragments.Clock.EDTR;
import com.example.timekeeping_beta.Globals.Models.ApiResult;
import com.example.timekeeping_beta.Globals.Models.User;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.R;
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestAdjustment.AdjustmentFragment;
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestLeave.LeaveFragment;
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestOvertime.OvertimeFragment;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Locale;

import es.dmoral.toasty.Toasty;

import static android.view.View.GONE;

public class DashboardFragment extends Fragment {

    private View v;
    private Context ctx;

    private TextView lates_today;
    private TextView undertime_today;
    private TextView lates_this_month;
    private TextView accumulated_today;

    private TextView time_adjustment_quantity;
    private TextView overtime_request_quantity;
    private TextView leave_request_quantity;
    private TextView tv_toggle_request;
    private TextView tv_toggle_adjustments;

    private TextView time_adjustment_approvals_quantity;
    private TextView overtime_appprovals_quantity;
    private TextView leave_approval_quantity;
    private TextView approvals_quantity;

    private TextView tv_lates_today_minhours, tv_undertime_today_minhours,
            tv_lates_this_month_minhours, tv_accumulated_today_minhours;


    private LinearLayout pending_requests_container;
    private CardView pending_approvals_container;

    private DashboardViewModel dashboardViewModel;
    private ClockViewModel clockViewModel;

    private FrameLayout link_pending_time_adjustment, link_pending_overtime, link_pending_leaves,
            link_pending_approvals_overtime, link_pending_time_adjustment_approvals, link_pending_leaves_approvals,
            link_pending_time_approvals;

    private ProgressDialog loadingScreenDialog;
    private LiveData<Dashboard> dashboardData;

    private SwipeRefreshLayout try_again_layout;
    private RelativeLayout try_again_layout_v2;
    private ScrollView dashboard_layout;

    private TextView tv_time_in, tv_time_out;
    private Dialog clockDialog;
    private CardView cv_time_in, cv_time_out;
    private LinearLayout ll_daily_edtr_container;

    private void hideVisibleAppbarLayouts() {
        getActivity().findViewById(R.id.timesheetnavigattion).setVisibility(GONE);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

        //ViewModelProviders.of(this).get(UserDashboardViewModel.class).retrieveDashboardAttendance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_dashboard, container, false);

        ctx = v.getContext();
        initViews(v);
        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        Menu nav_menu = navigationView.getMenu();
        setListeners();
        validateUserAccess();

        return v;
    }

    private void validateUserAccess() {

        User user = SharedPrefManager.getInstance(ctx).getUser();

        if (user.getIsApprover().equals("1")) {
            pending_approvals_container.setVisibility(View.VISIBLE);
        }

        try {
            JSONArray ja_user_bundees = new JSONArray(user.getUser_bundees());

            if (ja_user_bundees.length() > 0) {
                for (int i = 0; i < ja_user_bundees.length(); i++) {

                    if (ja_user_bundees.getInt(i) == 1003) {
                        ll_daily_edtr_container.setVisibility(View.VISIBLE);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        hideVisibleAppbarLayouts();

        dashboardViewModel.retrievePendingApprovals();
        dashboardViewModel.retrievePendingRequests();
    }

    private void initViews(View v) {
        lates_today = v.findViewById(R.id.lates_today);
        lates_this_month = v.findViewById(R.id.lates_this_month);
        undertime_today = v.findViewById(R.id.undertime_today);
        accumulated_today = v.findViewById(R.id.accumulated_today);

        time_adjustment_quantity = v.findViewById(R.id.time_adjustment_quantity);
        overtime_request_quantity = v.findViewById(R.id.overtime_request_quantity);
        leave_request_quantity = v.findViewById(R.id.leave_request_quantity);

        pending_requests_container = v.findViewById(R.id.pending_requests_container);
        pending_approvals_container = v.findViewById(R.id.pending_approvals_container);

        tv_toggle_request = v.findViewById(R.id.tv_toggle_request);
        tv_toggle_adjustments = v.findViewById(R.id.tv_toggle_adjustments);

        time_adjustment_approvals_quantity = v.findViewById(R.id.time_adjustment_approvals_quantity);
        overtime_appprovals_quantity = v.findViewById(R.id.overtime_appprovals_quantity);
        leave_approval_quantity = v.findViewById(R.id.leave_approval_quantity);
        approvals_quantity = v.findViewById(R.id.approvals_quantity);

        link_pending_time_adjustment_approvals = v.findViewById(R.id.link_pending_time_adjustment_approvals);
        link_pending_approvals_overtime = v.findViewById(R.id.link_pending_approvals_overtime);
        link_pending_leaves_approvals = v.findViewById(R.id.link_pending_leaves_approvals);
        link_pending_time_approvals = v.findViewById(R.id.link_pending_edtr_approvals);

        link_pending_time_adjustment = v.findViewById(R.id.link_pending_time_adjustment);
        link_pending_overtime = v.findViewById(R.id.link_pending_overtime);
        link_pending_leaves = v.findViewById(R.id.link_pending_leaves);


        tv_lates_today_minhours = v.findViewById(R.id.tv_lates_today_minhours);
        tv_undertime_today_minhours = v.findViewById(R.id.tv_undertime_today_minhours);
        tv_lates_this_month_minhours = v.findViewById(R.id.tv_lates_this_month_minhours);
        tv_accumulated_today_minhours = v.findViewById(R.id.tv_accumulated_today_minhours);

        try_again_layout = v.findViewById(R.id.swipeRefresh);
        try_again_layout_v2 = v.findViewById(R.id.try_again_layout_v2);
        dashboard_layout = v.findViewById(R.id.dashboard_layout);
        tv_time_in = v.findViewById(R.id.tv_time_in);
        tv_time_out = v.findViewById(R.id.tv_time_out);

        cv_time_in = v.findViewById(R.id.cv_time_in);
        cv_time_out = v.findViewById(R.id.cv_time_out);

        ll_daily_edtr_container = v.findViewById(R.id.ll_daily_edtr_container);
        clockDialog.setContentView(R.layout.dialog_clock_in_out);
    }

    private void init() {
        dashboardViewModel = ViewModelProviders.of(this)
                .get(DashboardViewModel.class);

        dashboardData = dashboardViewModel.getUserDashboard();

        clockViewModel = ViewModelProviders.of(this).
                get(ClockViewModel.class);

        clockViewModel.retrieveUserTimesheet();

        clockDialog = new Dialog(getActivity());
    }

    private void setValues(Dashboard dashboard) {

        int todays_late = dashboard.getToday_late();
        if (todays_late >= 60) {
            Double ml = ((double) todays_late) / 60;
            String sml = String.format(Locale.getDefault(), "%.1f", ml);

            lates_today.setText(sml);
            tv_lates_today_minhours.setText(ml <= 1.00 ? "hour" : "hours");
        } else {
            lates_today.setText(String.valueOf(todays_late));
            tv_lates_today_minhours.setText(todays_late == 1 ? "min" : "mins");
        }

        int months_late = dashboard.getMonthly_late();
        if (months_late >= 60) {
            Double ml = ((double) months_late) / 60;
            String sml = String.format(Locale.getDefault(), "%.1f", ml);

            lates_this_month.setText(sml);
            tv_lates_this_month_minhours.setText(ml <= 1.00 ? "hour" : "hours");
        } else {
            lates_this_month.setText(String.valueOf(months_late));
            tv_lates_this_month_minhours.setText(months_late == 1 ? "min" : "mins");
        }

        int undertime_mins = dashboard.getToday_undertime();
        if (undertime_mins >= 60) {
            Double ml = ((double) undertime_mins) / 60;
            String sml = String.format(Locale.getDefault(), "%.1f", ml);

            undertime_today.setText(sml);
            tv_undertime_today_minhours.setText(ml <= 1.00 ? "hour" : "hours");
        } else {
            undertime_today.setText(String.valueOf(undertime_mins));
            tv_undertime_today_minhours.setText(undertime_mins == 1 ? "min" : "mins");
        }


        int accumulated_mins = dashboard.getToday_accumulated();
        if (accumulated_mins >= 60) {
            Double ml = ((double) accumulated_mins) / 60;
            String sml = String.format(Locale.getDefault(), "%.1f", ml);

            accumulated_today.setText(sml);
            tv_accumulated_today_minhours.setText(ml <= 1.00 ? "hour" : "hours");
        } else {
            accumulated_today.setText(String.valueOf(accumulated_mins));
            tv_accumulated_today_minhours.setText(accumulated_mins == 1 ? "min" : "mins");
        }

        whenSuccess();
    }

    private void setListeners() {

        if (dashboardData.getValue() == null) {

            retrieveDashboard();
        } else if (dashboardData.getValue() != null) {
            setValues(dashboardData.getValue());
        }

        dashboardViewModel.getUserDashboard().observe(
                this, new Observer<Dashboard>() {

                    @Override
                    public void onChanged(@Nullable Dashboard dashboard) {

                        //if (loadingScreenDialog != null && loadingScreenDialog.isShowing()) {
                        //loadingScreenDialog.dismiss();
                        //}

//                        if (try_again_layout.isRefreshing()) {
//                            try_again_layout.setRefreshing(false);
//                        }

                        if (dashboard != null) {
                            setValues(dashboard);
                        } else {
                            whenError();
                        }
                    }
                }
        );

        dashboardViewModel.getInvalidToken().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
//                if (aBoolean != null) {
//
//                    if (aBoolean) {
//                        Toasty.error(getContext(), "Invalid token! You will be logged out.", Toasty.LENGTH_LONG).show();
//                        SharedPrefManager.getInstance(getContext()).logout();
//                        getActivity().finish();
//                    }
//                }
            }
        });

        dashboardViewModel.getPendingApprovals().observe(this, new Observer<DashboardCounts>() {
            @Override
            public void onChanged(@Nullable DashboardCounts dashboardCounts) {
                if (dashboardCounts != null) {

                    String adj_count = String.valueOf(dashboardCounts.getAdjustment());
                    String ot_count = String.valueOf(dashboardCounts.getOvertime());
                    String lv_count = String.valueOf(dashboardCounts.getLeave());

                    String ts_count = String.valueOf(dashboardCounts.getTime_approval());

                    time_adjustment_approvals_quantity.setText(adj_count);
                    overtime_appprovals_quantity.setText(ot_count);
                    leave_approval_quantity.setText(lv_count);
                    //approvals_quantity.setText(String.valueOf(dashboardCounts.getTime_approval()));

                    initializeApprovalCountDrawer(adj_count, ot_count, lv_count, ts_count);
                }
            }
        });

        dashboardViewModel.getPendingRequests().observe(this, new Observer<DashboardCounts>() {
            @Override
            public void onChanged(@Nullable DashboardCounts dashboardCounts) {

                if (dashboardCounts != null) {

                    String adj_count = String.valueOf(dashboardCounts.getAdjustment());
                    String ot_count = String.valueOf(dashboardCounts.getOvertime());
                    String lv_count = String.valueOf(dashboardCounts.getLeave());
                    String ts_count = String.valueOf(dashboardCounts.getTime_approval());

                    time_adjustment_quantity.setText(adj_count);
                    overtime_request_quantity.setText(ot_count);
                    leave_request_quantity.setText(lv_count);

                    initializeRequestCountDrawer(adj_count, ot_count, lv_count, ts_count);
                }
            }
        });

        clockViewModel.getUserEDTR().observe(this, new Observer<EDTR>() {
            @Override
            public void onChanged(@Nullable EDTR edtr) {
                if (edtr != null) {
                    tv_time_in.setText(edtr.getTime_in());
                    tv_time_out.setText(edtr.getTime_out());


                    TextView tv_confirm_question = clockDialog.findViewById(R.id.tv_confirm_question);
                    TextView tv_clocked_in = clockDialog.findViewById(R.id.tv_clocked_in);
                    TextView tv_send_text = clockDialog.findViewById(R.id.tv_send_text);
                    CardView cv_send = clockDialog.findViewById(R.id.cv_send);
                    TextView tv_daily_edtr_title = v.findViewById(R.id.tv_daily_edtr_title);

                    String blank_time = getContext().getString(R.string.blank_time);

                    if (!edtr.getTime_in().equals(blank_time) && edtr.getTime_out().equals(blank_time)) {

                        tv_daily_edtr_title.setText("Clock out");
                        tv_confirm_question.setText("Do you want to clock out now?");
                        tv_clocked_in.setText("Clocked in at: " + edtr.getTime_in());
                        tv_send_text.setText("Time out");
                        cv_send.setCardBackgroundColor(getResources().getColor(R.color.colorPending));

                        cv_time_in.setOnClickListener(null);
                        cv_time_out.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                clockDialog.show();
                            }
                        });
                    } else if (edtr.getTime_in().equals(blank_time) && edtr.getTime_out().equals(blank_time)) {
                        tv_daily_edtr_title.setText("Clock in");
                        tv_confirm_question.setText("Do you want to clock in now?");
                        tv_clocked_in.setText("Not yet clocked in");
                        tv_send_text.setText("Time in");
                        cv_send.setCardBackgroundColor(getResources().getColor(R.color.colorSuccess));

                        cv_time_out.setOnClickListener(null);
                        cv_time_in.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                clockDialog.show();
                            }
                        });
                    } else {
                        tv_daily_edtr_title.setText("EDTR");

                        cv_time_in.setOnClickListener(null);
                        cv_time_out.setOnClickListener(null);
                    }

                    cv_send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            clockDialog.dismiss();
                            clockViewModel.sendTimeInOut();
                        }
                    });
                }
            }
        });

        clockViewModel.getTimeInOutResult().observe(this, new Observer<ApiResult>() {
            @Override
            public void onChanged(@Nullable ApiResult apiResult) {

                if (apiResult != null) {
                    if (apiResult.getStatus()) {
                        Toasty.success(getContext(), apiResult.getMessage(), Toasty.LENGTH_LONG).show();

                    } else {
                        Toasty.error(getContext(), apiResult.getMessage(), Toasty.LENGTH_LONG).show();
                    }
                } else {
                    Toasty.error(getContext(), apiResult.getMessage(), Toasty.LENGTH_LONG).show();
                }
            }
        });

        link_pending_time_adjustment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                assert getFragmentManager() != null;
                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, new AdjustmentFragment(), "AdjustmentFragment")
                        .addToBackStack("AdjustmentFragment")
                        .commit();
            }
        });

        link_pending_overtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                assert getFragmentManager() != null;
                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, new OvertimeFragment(), "LeaveFragment")
                        .addToBackStack("LeaveFragment")
                        .commit();
            }
        });

        link_pending_leaves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                assert getFragmentManager() != null;
                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, new LeaveFragment(), "LeaveFragment")
                        .addToBackStack("LeaveFragment")
                        .commit();
            }
        });


        link_pending_time_adjustment_approvals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                assert getFragmentManager() != null;
                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, new AdjustmentsUpdateFragment(), "AdjustmentsUpdateFragment")
                        .addToBackStack("AdjustmentsUpdateFragment")
                        .commit();
            }
        });

        link_pending_approvals_overtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                assert getFragmentManager() != null;
                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, new OvertimeUpdateFragment(), "OvertimeUpdateFragment")
                        .addToBackStack("OvertimeUpdateFragment")
                        .commit();
            }
        });

        link_pending_leaves_approvals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                assert getFragmentManager() != null;
                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, new LeaveUpdateFragment(), "LeaveUpdateFragment")
                        .addToBackStack("LeaveUpdateFragment")
                        .commit();
            }
        });

        try_again_layout_v2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retrieveDashboard();

            }
        });

//        try_again_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//
//                retrieveDashboard();
//            }
//        });
    }

    private void whenError() {
        //dashboard_layout.setVisibility(GONE);

        try_again_layout_v2.setEnabled(true);
        try_again_layout_v2.setVisibility(View.VISIBLE);
        //try_again_layout.setVisibility(View.VISIBLE);
        try_again_layout.setRefreshing(false);
        try_again_layout.setEnabled(false);
    }

    private void whenSuccess() {
        //dashboard_layout.setVisibility(View.VISIBLE);

        try_again_layout_v2.setEnabled(false);
        try_again_layout_v2.setVisibility(View.GONE);
        //try_again_layout.setVisibility(View.GONE);
        try_again_layout.setRefreshing(false);
        try_again_layout.setEnabled(false);
    }

    private void retrieveDashboard() {
        dashboardViewModel.retrieveUserDashboard();


        dashboardViewModel.retrievePendingApprovals();
        dashboardViewModel.retrievePendingRequests();
        clockViewModel.retrieveUserTimesheet();

        //loadingScreenDialog = ProgressDialog.show(getActivity(), "Please Wait", "We are currently retrieving your dashboard data.");
        //try_again_layout.setRefreshing(false);
        try_again_layout_v2.setVisibility(GONE);
        try_again_layout_v2.setEnabled(false);
        try_again_layout.setEnabled(false);
        try_again_layout.setRefreshing(true);
    }

    private void initializeRequestCountDrawer(String adjustment_request_count, String overtime_request_count, String leave_request_count, String edtr_request_count) {

        NavigationView navigation = getActivity().findViewById(R.id.nav_view);
        Menu nav_Approvals_Menu = navigation.getMenu();

        if (!adjustment_request_count.equals("0")) {
            View itemActionView = nav_Approvals_Menu.findItem(R.id.nav_adjustment_request).getActionView();
            View v = View.inflate(getActivity(), R.layout.badge, null);

            if (itemActionView == null) {
                TextView tvAdjCount = v.findViewById(R.id.count);
                tvAdjCount.setText(adjustment_request_count);
                nav_Approvals_Menu.findItem(R.id.nav_adjustment_request).setActionView(v);
            } else {
                TextView tv = itemActionView.findViewById(R.id.count);
                tv.setText(adjustment_request_count);
            }
        }

        if (!edtr_request_count.equals("0")) {
            View itemActionView = nav_Approvals_Menu.findItem(R.id.nav_timesheet_request).getActionView();
            View v = View.inflate(getActivity(), R.layout.badge, null);

            if (itemActionView == null) {
                TextView tvAdjCount = v.findViewById(R.id.count);
                tvAdjCount.setText(edtr_request_count);
                nav_Approvals_Menu.findItem(R.id.nav_timesheet_request).setActionView(v);
            } else {
                TextView tv = itemActionView.findViewById(R.id.count);
                tv.setText(edtr_request_count);
            }
        }

        if (adjustment_request_count.equals("0")) {

            MenuItem menuItem = nav_Approvals_Menu.findItem(R.id.nav_adjustment_request);

            if (menuItem.getActionView() != null) {
                menuItem.setActionView(null);
            }
        }

        if (!overtime_request_count.equals("0")) {
            View itemActionView = nav_Approvals_Menu.findItem(R.id.nav_overtime_request).getActionView();
            View v = View.inflate(getActivity(), R.layout.badge, null);

            if (itemActionView == null) {
                TextView tvAdjCount = v.findViewById(R.id.count);
                tvAdjCount.setText(overtime_request_count);
                nav_Approvals_Menu.findItem(R.id.nav_overtime_request).setActionView(v);
            } else {
                TextView tv = itemActionView.findViewById(R.id.count);
                tv.setText(overtime_request_count);
            }
        }
        if (overtime_request_count.equals("0")) {
            MenuItem menuItem = nav_Approvals_Menu.findItem(R.id.nav_overtime_request);

            if (menuItem.getActionView() != null) {
                menuItem.setActionView(null);
            }
        }

        if (!leave_request_count.equals("0")) {
            View itemActionView = nav_Approvals_Menu.findItem(R.id.nav_leave_request).getActionView();
            View v = View.inflate(getActivity(), R.layout.badge, null);

            if (itemActionView == null) {
                TextView tvAdjCount = v.findViewById(R.id.count);
                tvAdjCount.setText(leave_request_count);
                nav_Approvals_Menu.findItem(R.id.nav_leave_request).setActionView(v);
            } else {
                TextView tv = itemActionView.findViewById(R.id.count);
                tv.setText(leave_request_count);
            }
        }
        if (leave_request_count.equals("0")) {
            MenuItem menuItem = nav_Approvals_Menu.findItem(R.id.nav_leave_request);

            if (menuItem.getActionView() != null) {
                menuItem.setActionView(null);
            }
        }
    }

    private void initializeApprovalCountDrawer(String adjustment_approval_count, String overtime_appoval_count, String leave_approval_count, String timesheet_approval_count) {

        NavigationView navigation = getActivity().findViewById(R.id.nav_view);
        Menu nav_Approvals_Menu = navigation.getMenu();

        if (!adjustment_approval_count.equals("0")) {
            View itemActionView = nav_Approvals_Menu.findItem(R.id.nav_time_approvals).getActionView();

            if (itemActionView == null) {
                View v = View.inflate(getActivity(), R.layout.badge, null);
                TextView tvAdjCount = v.findViewById(R.id.count);
                tvAdjCount.setText(adjustment_approval_count);

                nav_Approvals_Menu.findItem(R.id.nav_time_approvals).setActionView(v);
            } else {
                TextView tv = itemActionView.findViewById(R.id.count);
                tv.setText(adjustment_approval_count);
            }
        }

        if (!timesheet_approval_count.equals("0")) {
            View itemActionView = nav_Approvals_Menu.findItem(R.id.nav_timesheet_Approvals).getActionView();

            if (itemActionView == null) {
                View v = View.inflate(getActivity(), R.layout.badge, null);
                TextView tvAdjCount = v.findViewById(R.id.count);
                tvAdjCount.setText(timesheet_approval_count);

                nav_Approvals_Menu.findItem(R.id.nav_timesheet_Approvals).setActionView(v);
            } else {
                TextView tv = itemActionView.findViewById(R.id.count);
                tv.setText(timesheet_approval_count);
            }
        }

        if (adjustment_approval_count.equals("0")) {
            MenuItem menuItem = nav_Approvals_Menu.findItem(R.id.nav_time_approvals);

            if (menuItem.getActionView() != null) {
                menuItem.setActionView(null);
            }
        }

        if (!overtime_appoval_count.equals("0")) {
            View itemActionView = nav_Approvals_Menu.findItem(R.id.nav_overtime_approvals).getActionView();

            if (itemActionView == null) {
                View v = View.inflate(getActivity(), R.layout.badge, null);
                TextView tvAdjCount = v.findViewById(R.id.count);
                tvAdjCount.setText(overtime_appoval_count);

                nav_Approvals_Menu.findItem(R.id.nav_overtime_approvals).setActionView(v);
            } else {
                TextView tv = itemActionView.findViewById(R.id.count);
                tv.setText(overtime_appoval_count);
            }
        }

        if (overtime_appoval_count.equals("0")) {
            MenuItem menuItem = nav_Approvals_Menu.findItem(R.id.nav_overtime_approvals);

            if (menuItem.getActionView() != null) {
                menuItem.setActionView(null);
            }
        }


        if (!leave_approval_count.equals("0")) {
            //R
            View itemActionView = nav_Approvals_Menu.findItem(R.id.nav_leave_approvals).getActionView();
            View v = View.inflate(getActivity(), R.layout.badge, null);

            if (itemActionView == null) {
                TextView tvAdjCount = v.findViewById(R.id.count);

                //R
                tvAdjCount.setText(leave_approval_count);

                //R
                nav_Approvals_Menu.findItem(R.id.nav_leave_approvals).setActionView(v);
            } else {
                TextView tv = itemActionView.findViewById(R.id.count);
                //R
                tv.setText(leave_approval_count);
            }
        }
        //R
        if (leave_approval_count.equals("0")) {

            //R
            MenuItem menuItem = nav_Approvals_Menu.findItem(R.id.nav_leave_approvals);

            if (menuItem.getActionView() != null) {
                menuItem.setActionView(null);
            }
        }
    }
}
