package com.example.timekeeping_beta.Fragments.UserApprover.Leave;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.timekeeping_beta.Fragments.UserApprover.Approvee.Models.Profile;
import com.example.timekeeping_beta.Globals.CustomClasses.Flag;
import com.example.timekeeping_beta.Globals.Models.Pagination;
import com.example.timekeeping_beta.Globals.Models.queryStringBuilder;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.Globals.Models.User;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.Globals.StaticData.URLs_v2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaveUpdateViewModel extends AndroidViewModel {

    private Application application;

    final private MutableLiveData<List<Leave>> Leaves = new MutableLiveData<>();
    final private MutableLiveData<Pagination> pagination = new MutableLiveData<>();

    public LeaveUpdateViewModel(@NonNull Application application) {
        super(application);

        this.application = application;
    }

    public LiveData<List<Leave>> getLeaves() {
        return Leaves;
    }

    public MutableLiveData<Pagination> getPagination() {
        return pagination;
    }

    public boolean retrieveAllLeaves(String status, String search, Integer page, Integer show) {

        final URLs_v2 url = new URLs_v2();
        final User user = SharedPrefManager.getInstance(application.getBaseContext()).getUser();

        String GET_ADMIN_BACKEND_DATA = url.GET_ADMIN_BACKEND_DATA("leave-request/index/" + user.getUser_id(), new queryStringBuilder(user.getApi_token(), user.getLink(), page, search, show, status).build());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_ADMIN_BACKEND_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {
                        JSONArray leaves = response_obj.getJSONObject("msg").getJSONArray("data");

                        setApprovalsToArray(leaves);
                        setPagination(response_obj.getJSONObject("msg"));
                    } else {
                        Leaves.setValue(null);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Leaves.setValue(null);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Leaves.setValue(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return Helper.getInstance(application.getBaseContext()).headers();
            }
        };
        Helper.getInstance(application.getApplicationContext()).addToRequestQueue(stringRequest);

        return true;
    }

    private void setApprovalsToArray(JSONArray i_leaves) {

        Integer array_len = i_leaves.length();
        ArrayList<Leave> LeaveAdjustmentsArray = new ArrayList<>();

        if (array_len > 0) {

            for (int i = 0; i < array_len; i++) {

                try {
                    JSONObject adjustment = i_leaves.getJSONObject(i);

                    //Integer id = adjustment.getInt("id");
                    String employee_id = adjustment.getString("user_id");
                    String request_id = adjustment.getString("request_id");
                    String date_start = adjustment.getString("date_start");
                    String date_end = adjustment.getString("date_end");
                    String leave_type = adjustment.getString("leave_type");
                    String status = adjustment.getString("status");
                    String day_type = adjustment.getString("day_type");
                    String reason = adjustment.getString("reason");
                    String requested_at = adjustment.has("requested_at") ? adjustment.getString("requested_at") : "";

                    JSONObject employee_profile_object = adjustment.getJSONObject("profile");
                    String fname = employee_profile_object.getString("fname");
                    String lname = employee_profile_object.getString("lname");

                    String user_image_name = employee_profile_object.getString("image");

                    Profile employee_profile = new Profile(fname, lname);

                    Leave leave_object = new Leave(
                            employee_id,
                            request_id,
                            date_start,
                            date_end,
                            leave_type,
                            status,
                            day_type,
                            reason,
                            employee_profile,
                            requested_at,
                            user_image_name
                    );

                    LeaveAdjustmentsArray.add(leave_object);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Leaves.setValue(LeaveAdjustmentsArray);
        } else {
            Leaves.setValue(LeaveAdjustmentsArray);
        }
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
                        JSONArray leaves = response_obj.getJSONObject("msg").getJSONArray("data");

                        setApprovalsToArray(leaves);
                        setPagination(response_obj.getJSONObject("msg"));
                    } else {
                        Leaves.setValue(null);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Leaves.setValue(null);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Leaves.setValue(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return Helper.getInstance(application.getBaseContext()).headers();
            }
        };
        Helper.getInstance(application.getApplicationContext()).addToRequestQueue(stringRequest);
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
