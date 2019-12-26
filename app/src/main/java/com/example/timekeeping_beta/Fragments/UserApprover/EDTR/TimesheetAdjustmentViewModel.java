package com.example.timekeeping_beta.Fragments.UserApprover.EDTR;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.timekeeping_beta.Fragments.UserApprover.Approvee.Models.Profile;
import com.example.timekeeping_beta.Fragments.UserApprover.EDTR.TimesheetAdjustment;
import com.example.timekeeping_beta.Globals.CustomClasses.Flag;
import com.example.timekeeping_beta.Globals.Models.Pagination;
import com.example.timekeeping_beta.Globals.Models.queryStringBuilder;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.Globals.Models.User;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.Globals.StaticData.URLs_v2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TimesheetAdjustmentViewModel extends AndroidViewModel {

    final private MutableLiveData<List<EDTRAdjustment>> TimesheetAdjustments = new MutableLiveData<>();
    final private MutableLiveData<Pagination> pagination = new MutableLiveData<>();

    private Context ctx;

    public TimesheetAdjustmentViewModel(@NonNull Application application) {
        super(application);

        ctx = getApplication().getApplicationContext();
    }

    public LiveData<List<EDTRAdjustment>> getTimesheetAdjustments() {
        return TimesheetAdjustments;
    }

    public MutableLiveData<Pagination> getPagination() {
        return pagination;
    }

    public void retrieveAllTimesheetAdjustments(String status, String search, Integer page, Integer show) {

        final URLs_v2 url = new URLs_v2();
        final User user = SharedPrefManager.getInstance(ctx).getUser();

        String GET_EDTR_ADJUSTMENTS = url.GET_CLOCK_BACKEND_DATA("timeapproval/index/" + user.getUser_id(), new queryStringBuilder(user.getApi_token(), user.getLink(), page, search, show, status).build());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_EDTR_ADJUSTMENTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {
                        JSONObject msg = response_obj.getJSONObject("msg");
                        JSONArray timesheet_adjustments = msg.getJSONObject("approvals").getJSONArray("data");

                        setApprovalsToArray(timesheet_adjustments);
                        setPagination(msg.getJSONObject("approvals"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    TimesheetAdjustments.setValue(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                TimesheetAdjustments.setValue(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                return Helper.getInstance(ctx).headers();
            }
        };
        Helper.getInstance(ctx).addToRequestQueue(stringRequest);
    }

    private void setApprovalsToArray(JSONArray i_timesheet_adjusments) {

        int array_len = i_timesheet_adjusments.length();
        ArrayList<EDTRAdjustment> TimesheetAdjustmentsArray = new ArrayList<>();

        if (array_len > 0) {

            for (int i = 0; i < array_len; i++) {

                try {
                    JSONObject adjustment = i_timesheet_adjusments.getJSONObject(i);

                    Integer id = adjustment.getInt("id");
                    String employee_id = adjustment.getString("user_id");
                    String edtr_id = adjustment.getString("edtr_id");
                    String employee_time_in = adjustment.getString("time_in");
                    String employee_time_out = adjustment.getString("time_out");
                    String employee_day_type = adjustment.getString("day_type");
                    String employee_shift_in = adjustment.getString("shift_in");
                    String employee_shift_out = adjustment.getString("shift_out");
                    String employee_date_in = adjustment.getString("date_in");
                    String employee_adjustment_status = adjustment.getString("status");
                    String employee_adjustment_reference = adjustment.getString("reference");
                    String employee_adjustment_reason = adjustment.getString("remarks");
                    JSONObject employee_profile_object = adjustment.getJSONObject("profile");
                    String fname = employee_profile_object.getString("fname");
                    String lname = employee_profile_object.getString("lname");
                    String image = employee_profile_object.getString("image");

                    Profile employee_profile = new Profile(fname, lname);

                    EDTRAdjustment timesheet_adjustment_object = new EDTRAdjustment(
                            adjustment.getInt("id"),
                            adjustment.getString("user_id"),
                            adjustment.getString("edtr_id"),
                            adjustment.getString("date_in"),
                            adjustment.getString("time_in"),
                            adjustment.getString("time_out"),
                            adjustment.getString("shift_in"),
                            adjustment.getString("shift_out"),
                            adjustment.getString("reference"),
                            adjustment.getString("day_type"),
                            adjustment.getInt("grace_period"),
                            adjustment.getString("late_threshold"),
                            adjustment.getString("undertime_threshold"),
                            adjustment.getString("remarks"),
                            adjustment.getString("reason"),
                            adjustment.getString("decline_reason"),
                            adjustment.getString("isBroken"),
                            adjustment.getInt("shift"),
                            adjustment.getString("checked_by"),
                            adjustment.getString("checked_at"),
                            adjustment.getString("status"),
                            adjustment.getString("created_at"),
                            adjustment.getString("updated_at"),
                            employee_profile,
                            image
                    );

                    TimesheetAdjustmentsArray.add(timesheet_adjustment_object);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //TimesheetAdjustments.setValue(TimesheetAdjustmentsArray);
        }
        TimesheetAdjustments.setValue(TimesheetAdjustmentsArray);
    }

    public void retrievePaginated(Integer flag, String status, String search, Integer show) {

        Pagination l_pagination = pagination.getValue();
        String url = "";

        if (flag == Flag.NEXT_PAGE) {
            url = l_pagination.getNext_page_url();
        } else if (flag == Flag.PREV_PAGE) {
            url = l_pagination.getPrev_page_url();
        }

        User user = SharedPrefManager.getInstance(getApplication().getBaseContext()).getUser();

        url = url + new queryStringBuilder(user.getApi_token(), user.getLink(), 0, search, show, status).buildNoPage();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {
                        JSONObject msg = response_obj.getJSONObject("msg");
                        JSONArray timesheet_adjustments = msg.getJSONObject("approvals").getJSONArray("data");

                        setApprovalsToArray(timesheet_adjustments);
                        setPagination(msg.getJSONObject("approvals"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                return Helper.getInstance(ctx).headers();
            }
        };

        stringRequest.setRetryPolicy(Helper.getInstance(ctx).volleyRetryPolicy());
        Helper.getInstance(ctx).addToRequestQueue(stringRequest);
    }

    private void setPagination(JSONObject msg) {
        Pagination l_pagination = null;

        try {
            if (msg.getString("next_page_url") != "null" || msg.getString("prev_page_url") != "null") {
                l_pagination = new Pagination(
                        msg.getInt("current_page"),
                        msg.getString("next_page_url"),
                        msg.getString("prev_page_url"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        pagination.setValue(l_pagination);
    }
}
