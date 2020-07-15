package com.example.timekeeping_beta.Globals;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CircularProgressDrawable;
import android.util.Log;
import android.util.TypedValue;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.timekeeping_beta.Fragments.DEPRECATED.Dashboard.DashboardFragment;
import com.example.timekeeping_beta.Fragments.GlobalSettings.HolidayTypes.HolidayTypesFragment;
import com.example.timekeeping_beta.Fragments.GlobalSettings.HolidayManagement.HolidaysFragment;
import com.example.timekeeping_beta.Fragments.GlobalSettings.LeaveTypes.LeaveTypesFragment;
import com.example.timekeeping_beta.Fragments.GlobalSettings.LocationManagement.CompanyLocationsFragment;
import com.example.timekeeping_beta.Fragments.GlobalSettings.ScheduleManagement.SchedulesFragment;
import com.example.timekeeping_beta.Fragments.HRAdmin.Employees.EmployeesFragment;
import com.example.timekeeping_beta.Fragments.Profile.UserProfileFragment;
import com.example.timekeeping_beta.Fragments.Timesheet.TimesheetFragment;
import com.example.timekeeping_beta.Fragments.UserApprover.Adjustment.AdjustmentsUpdateFragment;
import com.example.timekeeping_beta.Fragments.DEPRECATED.ApproversEmployeesFragment;
import com.example.timekeeping_beta.Fragments.UserApprover.EDTR.EDTRUpdateFragment;
import com.example.timekeeping_beta.Fragments.UserApprover.Leave.LeaveUpdateFragment;
import com.example.timekeeping_beta.Fragments.UserApprover.Overtime.OvertimeUpdateFragment;
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestAdjustment.AdjusmentFragments.ApprovedAdjustmentsFragment;
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestAdjustment.AdjusmentFragments.DeclinedAdjustmentsFragment;
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestAdjustment.AdjusmentFragments.PendingAdjustmentsFragment;
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestAdjustment.AdjustmentFragment;
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestLeave.LeaveFragment;
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestLeave.LeaveFragments.ApprovedLeaveFragment;
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestLeave.LeaveFragments.DeclinedLeaveFragment;
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestLeave.LeaveFragments.PendingLeaveFragment;
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestOvertime.OvertimeFragment;
import com.example.timekeeping_beta.Globals.Models.User;
import com.example.timekeeping_beta.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Helper {

    private static Helper mInstance;
    private RequestQueue mRequestQueue;
    private Context mCtx;

    private ArrayList<Fragment> fragmentsList;

    private Helper(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        //When adding new Fragment
        //Add index reference to Flag
        fragmentsList = new ArrayList<>();
        fragmentsList.add(0, new DashboardFragment());
        fragmentsList.add(1, new AdjustmentsUpdateFragment());
        fragmentsList.add(2, new ApproversEmployeesFragment());
        fragmentsList.add(3, new LeaveUpdateFragment());
        fragmentsList.add(4, new OvertimeUpdateFragment());
        fragmentsList.add(5, new TimesheetFragment());
        fragmentsList.add(6, new ApprovedAdjustmentsFragment());
        fragmentsList.add(7, new DeclinedAdjustmentsFragment());
        fragmentsList.add(8, new PendingAdjustmentsFragment());
        fragmentsList.add(9, new AdjustmentFragment());
        fragmentsList.add(10, new ApprovedLeaveFragment());
        fragmentsList.add(11, new DeclinedLeaveFragment());
        fragmentsList.add(12, new PendingLeaveFragment());
        fragmentsList.add(13, new LeaveFragment());
        fragmentsList.add(14, new OvertimeFragment());
        fragmentsList.add(15, new UserProfileFragment());
        fragmentsList.add(16, new EDTRUpdateFragment());
        fragmentsList.add(17, new EmployeesFragment());
        fragmentsList.add(18, new SchedulesFragment());
        fragmentsList.add(19, new HolidaysFragment());
        fragmentsList.add(20, new HolidayTypesFragment());
        fragmentsList.add(21, new LeaveTypesFragment());
        fragmentsList.add(22, new CompanyLocationsFragment());
    }

    public static synchronized Helper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Helper(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }

    //Dev Karl
    public String convertToReadableTime(String time) {

        String string_time = time;
        String converted_time = "--:--:-- --";
        SimpleDateFormat _24Hour = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat _readableFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        if (!time.isEmpty()) {
            try {
                Date _24HourDt = _24Hour.parse(string_time);
                converted_time = _readableFormat.format(_24HourDt);
                Log.d("@12Hr_format", converted_time);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        }

        return converted_time.replace("a.m.", "AM").replace("p.m.", "PM");
    }

    public String convertToFormattedTime(String time, String input_time_pattern, String pattern) {

        String string_time = time;
        String converted_time = "--:--:-- --";
        SimpleDateFormat _24Hour = new SimpleDateFormat(input_time_pattern, Locale.getDefault());
        SimpleDateFormat _readableFormat = new SimpleDateFormat(pattern, Locale.getDefault());

        if (!time.isEmpty()) {
            try {
                Date _24HourDt = _24Hour.parse(string_time);
                converted_time = _readableFormat.format(_24HourDt);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        }

        return converted_time.replace("a.m.", "AM").replace("p.m.", "PM");
    }

    public String createStringDate(int year, int monthOfYear, int dayOfMonth) {

        String s_month = monthOfYear + 1 < 10 ? "0" + monthOfYear + 1 : String.valueOf(monthOfYear + 1);
        String s_year = year < 10 ? "0" + year : String.valueOf(year);
        String s_date = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);

        return s_year + "-" + s_month + "-" + s_date;
    }

    public String convertToReadableDate(String date) {

        String string_date = date;
        String converted_date = "--- --, ----";
        SimpleDateFormat raw_date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat date_day = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

        try {
            Date raw_dateDt = raw_date.parse(string_date);
            converted_date = date_day.format(raw_dateDt);
            Log.d("@date_with_day", converted_date);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return converted_date;
    }

    public Fragment getFragment(Integer fragment_reference) {
        return fragmentsList.get(fragment_reference);
    }

    public Date stringTimeToDate(String _24hour_time) {

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        Date date;
        try {
            date = formatter.parse(_24hour_time);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            date = null;
        }

        return date;
    }

    public String createStringDateFromDatePickerDialog(int year, int month, int dayOfMonth) {
        int nonZeroIndexedMonth = month + 1;

        String m = nonZeroIndexedMonth < 10 ? "0" + nonZeroIndexedMonth : String.valueOf(nonZeroIndexedMonth);
        String d = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
        String y = year < 10 ? "0" + year : String.valueOf(year);

        return y + "-" + m + "-" + d;
    }

    public CircularProgressDrawable getCircleAnimation() {

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mCtx);
        int[] cs = new int[1];
        cs[0] = mCtx.getResources().getColor(R.color.colorWhite);
        circularProgressDrawable.setColorSchemeColors(cs);
        circularProgressDrawable.setStrokeWidth(10f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        return circularProgressDrawable;
    }


    public float integerToDP(int i) {

        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, mCtx.getResources().getDisplayMetrics());
    }

    public boolean dateHasPassedOrToday(String date) {
        try {
            return (new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date).before(new Date())
                    || new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date).equals(new Date()));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    //Returns current date
    public String today() {

        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    //Returns current time
    public String now() {

        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
    }
    //Dev Karl

    public Map<String, String> headers() {
        Map<String, String> headers = new HashMap<>();

        User user = SharedPrefManager.getInstance(mCtx).getUser();

        headers.put("d", user.getC1());
        headers.put("t", user.getC2());
        headers.put("token", user.getToken());

        return headers;
    }

    public RetryPolicy volleyRetryPolicy() {

        return new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 1000 * 60;
            }

            @Override
            public int getCurrentRetryCount() {
                return -1;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        };
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mCtx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
