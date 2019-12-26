package com.example.timekeeping_beta.Fragments.HRAdmin.Dashboard;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.timekeeping_beta.Fragments.HRAdmin.Dashboard.Models.BundeeEmployee;
import com.example.timekeeping_beta.Fragments.HRAdmin.Dashboard.Models.EmployeeTopLates;
import com.example.timekeeping_beta.Fragments.UserApprover.Approvee.Models.Approvee;
import com.example.timekeeping_beta.Globals.Models.queryStringBuilder;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.Globals.Models.User;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.Globals.StaticData.URLs_v2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class HRDashboardViewModel extends AndroidViewModel {

    private Application application;
    private Map<String, String> headers;
    private RetryPolicy retryPolicy;

    final private MutableLiveData<DashboardDailyAttendance> DashboardDailyAttendance = new MutableLiveData<>();
    final private MutableLiveData<DashboardBundeeCount> DashboardBundeeCount = new MutableLiveData<>();

    final private MutableLiveData<ArrayList<Approvee>> onTimeEmployees = new MutableLiveData<>();
    final private MutableLiveData<ArrayList<Approvee>> LateEmployees = new MutableLiveData<>();
    final private MutableLiveData<ArrayList<Approvee>> onLeaveEmployees = new MutableLiveData<>();
    final private MutableLiveData<ArrayList<Approvee>> AbsentEmployees = new MutableLiveData<>();

    final private MutableLiveData<ArrayList<BundeeEmployee>> BundeeEmployees = new MutableLiveData<>();

    public HRDashboardViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        headers = Helper.getInstance(application.getBaseContext()).headers();
        retryPolicy = Helper.getInstance(application.getBaseContext()).volleyRetryPolicy();
    }

    public MutableLiveData<DashboardDailyAttendance> getDashboardDailyAttendance() {
        return this.DashboardDailyAttendance;
    }

    public MutableLiveData<DashboardBundeeCount> getDashboardBundeeCount() {
        return this.DashboardBundeeCount;
    }

    public MutableLiveData<ArrayList<Approvee>> getOnTimeEmployees() {
        return onTimeEmployees;
    }

    public MutableLiveData<ArrayList<Approvee>> getLateEmployees() {
        return LateEmployees;
    }

    public MutableLiveData<ArrayList<Approvee>> getOnLeaveEmployees() {
        return onLeaveEmployees;
    }

    public MutableLiveData<ArrayList<Approvee>> getAbsentEmployees() {
        return AbsentEmployees;
    }

    public MutableLiveData<ArrayList<BundeeEmployee>> getBundeeEmployees() {
        return BundeeEmployees;
    }

    public void retrieveHRDashboard() {

        final URLs url = new URLs();
        final User user = SharedPrefManager.getInstance(application.getApplicationContext()).getUser();

        String url_hr_dashboard = url.url_hr_dashboard(user.getApi_token(), user.getLink());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_hr_dashboard, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {

                        setToHRDashBoard(response_obj);
                    } else if (status.equals("error")) {

                        DashboardDailyAttendance.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    DashboardDailyAttendance.setValue(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DashboardDailyAttendance.setValue(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };
        stringRequest.setRetryPolicy(
                retryPolicy
        );

        Helper.getInstance(application).addToRequestQueue(stringRequest);
    }

    public void retriveBundeeCount() {

        final URLs url = new URLs();
        final User user = SharedPrefManager.getInstance(application.getApplicationContext()).getUser();

        String url_bundee_count = url.url_bundee_count(user.getApi_token(), user.getLink());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_bundee_count, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {

                        setToDashboardBundeeCount(response_obj.getJSONObject("msg"));
                    } else if (status.equals("error")) {

                        DashboardBundeeCount.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    DashboardBundeeCount.setValue(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DashboardBundeeCount.setValue(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };
        stringRequest.setRetryPolicy(
                retryPolicy
        );

        Helper.getInstance(application).addToRequestQueue(stringRequest);
    }

    public void retriveBundeeEmployees() {

        final URLs url = new URLs();
        final User user = SharedPrefManager.getInstance(application.getApplicationContext()).getUser();

        String url_bundee_employees = url.url_bundee_employees(user.getApi_token(), user.getLink());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_bundee_employees, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {

                        setToDashboardBundeeEmployees(response_obj.getJSONArray("msg"));
                    } else if (status.equals("error")) {

                        BundeeEmployees.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    BundeeEmployees.setValue(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                BundeeEmployees.setValue(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };
        stringRequest.setRetryPolicy(
                retryPolicy
        );

        Helper.getInstance(application).addToRequestQueue(stringRequest);
    }

    private void setToHRDashBoard(JSONObject response) {

        int onTimeEmployeeCount = 0;
        JSONArray onTimeEmployees = null;

        int lateEmployeesCount = 0;
        JSONArray lateEmployees = null;

        int onLeaveCount = 0;
        JSONArray onLeaveEmployees = null;

        int absentCount = 0;
        JSONArray absentEmployees = null;

        try {
            onTimeEmployeeCount = response.getInt("on_time");
            onTimeEmployees = response.getJSONArray("on_time_employees");

            lateEmployeesCount = response.getInt("late");
            lateEmployees = response.getJSONArray("late_employees");

            onLeaveCount = response.getInt("leave");
            onLeaveEmployees = response.getJSONArray("on_leave_employees");

            absentCount = response.getInt("absent");
            absentEmployees = response.getJSONArray("absent_employees");


        } catch (JSONException e) {
            e.printStackTrace();
        }


        DashboardDailyAttendance dashboardDailyAttendance = new DashboardDailyAttendance(
                onTimeEmployeeCount,
                onTimeEmployees,
                lateEmployeesCount,
                lateEmployees,
                onLeaveCount,
                onLeaveEmployees,
                absentCount,
                absentEmployees);

        DashboardDailyAttendance.setValue(dashboardDailyAttendance);

        setToOnTimeEmployeeLists();
        setToLateEmployeeLists();
        setToOnLeaveEmployeeLists();
        setToAbsentEmployeeLists();
    }

    private void setToOnTimeEmployeeLists() {

        ArrayList<Approvee> innerList = new ArrayList<>();
        JSONArray jaOnTimeEmployees = DashboardDailyAttendance.getValue().getOnTimeEmployees();

        for (int i = 0; i < jaOnTimeEmployees.length(); i++) {
            try {
                JSONObject joOnTimeEmployee = jaOnTimeEmployees.getJSONObject(i);

                innerList.add(new Approvee(
                        joOnTimeEmployee.getString("user_id"),

                        joOnTimeEmployee.getString("fname"),
                        joOnTimeEmployee.getString("lname"),
                        joOnTimeEmployee.getString("cell_num"),
                        "",
                        "",
                        "",
                        "",
                        joOnTimeEmployee.getString("image")
                ));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        onTimeEmployees.setValue(innerList);
    }

    private void setToLateEmployeeLists() {

        ArrayList<Approvee> innerList = new ArrayList<>();
        JSONArray jaLateEmployees = DashboardDailyAttendance.getValue().getLateEmployees();

        for (int i = 0; i < jaLateEmployees.length(); i++) {
            try {
                JSONObject joOnTimeEmployee = jaLateEmployees.getJSONObject(i);

                innerList.add(new Approvee(
                        joOnTimeEmployee.getString("user_id"),

                        joOnTimeEmployee.getString("fname"),
                        joOnTimeEmployee.getString("lname"),
                        joOnTimeEmployee.getString("cell_num"),
                        "",
                        "",
                        "",
                        "",
                        joOnTimeEmployee.getString("image")
                ));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        LateEmployees.setValue(innerList);
    }

    private void setToOnLeaveEmployeeLists() {

        ArrayList<Approvee> innerList = new ArrayList<>();
        JSONArray jaLateEmployees = DashboardDailyAttendance.getValue().getOnLeaveEmployees();

        for (int i = 0; i < jaLateEmployees.length(); i++) {
            try {
                JSONObject joOnTimeEmployee = jaLateEmployees.getJSONObject(i);

                innerList.add(new Approvee(
                        joOnTimeEmployee.getString("user_id"),

                        joOnTimeEmployee.getString("fname"),
                        joOnTimeEmployee.getString("lname"),
                        joOnTimeEmployee.getString("cell_num"),
                        "",
                        "",
                        "",
                        "",
                        joOnTimeEmployee.getString("image")
                ));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        onLeaveEmployees.setValue(innerList);
    }

    private void setToAbsentEmployeeLists() {

        ArrayList<Approvee> innerList = new ArrayList<>();
        JSONArray jaLateEmployees = DashboardDailyAttendance.getValue().getAbsentEmployees();

        for (int i = 0; i < jaLateEmployees.length(); i++) {
            try {
                JSONObject joOnTimeEmployee = jaLateEmployees.getJSONObject(i);

                innerList.add(new Approvee(
                        joOnTimeEmployee.getString("user_id"),

                        joOnTimeEmployee.getString("fname"),
                        joOnTimeEmployee.getString("lname"),
                        joOnTimeEmployee.getString("cell_num"),
                        "",
                        "",
                        "",
                        "",
                        joOnTimeEmployee.getString("image")
                ));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        AbsentEmployees.setValue(innerList);
    }


    private void setToDashboardBundeeCount(JSONObject response) {

        JSONArray count = null;
        int total_in = 0;
        int total_out = 0;
        Double percentage = 0.0;
        int total = 0;

        try {
            count = response.getJSONArray("count");
            total_in = response.getInt("total_in");
            total_out = response.getInt("total_out");
            percentage = response.getDouble("percentage");
            total = response.getInt("total");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        DashboardBundeeCount dashboardBundeeCount = new DashboardBundeeCount(
                count,
                total_in,
                total_out,
                percentage,
                total);

        DashboardBundeeCount.setValue(dashboardBundeeCount);
    }

    private void setToDashboardBundeeEmployees(JSONArray response) {

        ArrayList<BundeeEmployee> innerList = new ArrayList<>();

        for (int i = 0; i < response.length(); i++) {

            try {
                JSONObject jo_be = response.getJSONObject(i);

                BundeeEmployee be = new BundeeEmployee(
                        jo_be.getString("fname"),
                        jo_be.getString("lname"),
                        jo_be.getString("time_in"),
                        jo_be.getString("time_out"),
                        jo_be.getString("shift_in"),
                        jo_be.getString("shift_out"));

                innerList.add(be);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        BundeeEmployees.setValue(innerList);
    }


    final private MutableLiveData<ArrayList<EmployeeTopLates>> TopLateEmployees = new MutableLiveData<>();

    public MutableLiveData<ArrayList<EmployeeTopLates>> getTopLateEmployees() {
        return this.TopLateEmployees;
    }

    public void retriveTop10Lates(String range) {

        final URLs_v2 url = new URLs_v2();
        final User user = SharedPrefManager.getInstance(application.getApplicationContext()).getUser();

        String url_bundee_count = url.GET_ADMIN_BACKEND_DATA("dashboard-topTenlate-" + range.toLowerCase(), new queryStringBuilder(user.getApi_token(), user.getLink(), 0, "", 0, "").buildLegacy());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_bundee_count, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {

                        settoTopLateEmployees(response_obj.getJSONObject("msg").getJSONObject("late"));
                    } else if (status.equals("error")) {

                        TopLateEmployees.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    TopLateEmployees.setValue(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                TopLateEmployees.setValue(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };
        stringRequest.setRetryPolicy(
                retryPolicy
        );

        Helper.getInstance(application).addToRequestQueue(stringRequest);
    }

    private void settoTopLateEmployees(JSONObject response) {

        ArrayList<EmployeeTopLates> innerList = new ArrayList<>();

        int i = 0;
        for (Iterator<String> iter = response.keys(); iter.hasNext(); ) {


            String key = iter.next();
            JSONObject jo_be;
            try {
                jo_be = response.getJSONObject(key);

                EmployeeTopLates be = new EmployeeTopLates(
                        jo_be.getString("fname"),
                        jo_be.getString("lname"),
                        jo_be.getInt("late"));
                innerList.add(be);
                i++;
            } catch (JSONException e) {
                e.printStackTrace();
            }



        }


        TopLateEmployees.setValue(innerList);
    }


}
