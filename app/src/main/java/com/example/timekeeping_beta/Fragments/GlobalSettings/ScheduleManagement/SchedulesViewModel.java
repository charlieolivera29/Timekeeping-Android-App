package com.example.timekeeping_beta.Fragments.GlobalSettings.ScheduleManagement;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.Globals.Models.User;
import com.example.timekeeping_beta.Globals.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchedulesViewModel extends AndroidViewModel {

    final private MutableLiveData<List<Schedule>> Schedules = new MutableLiveData<>();

    final private MutableLiveData<String> create_leave_type_result = new MutableLiveData<>();
    final private MutableLiveData<String> edit_leave_type_result = new MutableLiveData<>();

    private Context ctx;
    private URLs url;
    private User user;
    private String DATABASE;
    private String TABLE;
    private String url_resource_schedule;

    public SchedulesViewModel(@NonNull Application application) {
        super(application);

        ctx = getApplication().getApplicationContext();

        url = new URLs();
        user = SharedPrefManager.getInstance(ctx).getUser();
        DATABASE = user.getC1();
        TABLE = user.getC2();
        url_resource_schedule = url.url_resource_schedules(user.getApi_token(), user.getLink());
    }

    public MutableLiveData<List<Schedule>> getSchedules() {
        return Schedules;
    }

    public LiveData<String> getCreateLeaveTypeResult() {
        return create_leave_type_result;
    }

    public LiveData<String> getEditLeaveTypeResult() {
        return edit_leave_type_result;
    }

    public void retrieveAllSchedules() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_resource_schedule, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {
                        JSONArray holidays = response_obj.getJSONArray("msg");

                        setSchedulsToArray(holidays);
                    } else {
                        Schedules.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Schedules.setValue(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Schedules.setValue(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("d", DATABASE);
                headers.put("t", TABLE);
                return headers;
            }
        };
        Helper.getInstance(ctx).addToRequestQueue(stringRequest);
    }

    private void setSchedulsToArray(JSONArray i_holiday_types) {

        int array_len = i_holiday_types.length();
        ArrayList<Schedule> LeaveTypeArray = new ArrayList<>();

        if (array_len > 0) {

            for (int i = 0; i < array_len; i++) {

                try {
                    JSONObject sched = i_holiday_types.getJSONObject(i);

                    int id = sched.getInt("id");
                    String sched_id = sched.getString("sched_id");
                    String sched_name = sched.getString("sched_name");
                    String shift_in = sched.getString("shift_in");
                    String shift_out = sched.getString("shift_out");
                    JSONArray day = sched.getJSONArray("day");
                    String remarks = sched.getString("remarks");
                    String grace_period = sched.getString("grace_period");
                    String late_threshold = sched.getString("late_threshold");
                    String undertime_threshold = sched.getString("undertime_threshold");
                    String sched_break = sched.getString("break");
                    String date_start = sched.getString("date_start");
                    String default_sched = sched.getString("default_sched");

                    Schedule leave_type_object = new Schedule(
                            id,
                            sched_id,
                            sched_name,
                            shift_in,
                            shift_out,
                            day,
                            remarks,
                            grace_period,
                            late_threshold,
                            undertime_threshold,
                            sched_break,
                            date_start,
                            default_sched
                    );

                    LeaveTypeArray.add(leave_type_object);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Schedules.setValue(LeaveTypeArray);
        } else {
            Schedules.setValue(LeaveTypeArray);
        }
    }

    public void createSchedule(final JSONObject jsonBody) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_resource_schedule, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {
                        create_leave_type_result.setValue(status);
                    } else {
                        create_leave_type_result.setValue(response_obj.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    create_leave_type_result.setValue(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                create_leave_type_result.setValue(null);
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("d", DATABASE);
                headers.put("t", TABLE);
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return jsonBody.toString().getBytes();
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60 * 1000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Helper.getInstance(ctx).addToRequestQueue(stringRequest);
    }

    public void updateSchedule(final String id,final JSONObject jsonBody) {

        String url_update_schedule = new URLs().url_update_schedule(id,user.getApi_token(),user.getLink());

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url_update_schedule   , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {
                        create_leave_type_result.setValue(status);
                    } else {
                        create_leave_type_result.setValue(response_obj.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    create_leave_type_result.setValue(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                create_leave_type_result.setValue(null);
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return jsonBody.toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("d", DATABASE);
                headers.put("t", TABLE);
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60 * 1000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Helper.getInstance(ctx).addToRequestQueue(stringRequest);
    }
}
