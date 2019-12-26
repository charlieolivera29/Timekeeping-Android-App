package com.example.timekeeping_beta.Fragments.UserApprover.Overtime;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OvertimesUpdateViewModel extends AndroidViewModel {

    private Application application;
    final private MutableLiveData<List<Overtime>> Overtimes = new MutableLiveData<>();

    public MutableLiveData<Pagination> getPagination() {
        return pagination;
    }

    final private MutableLiveData<Pagination> pagination = new MutableLiveData<>();


    public OvertimesUpdateViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public LiveData<List<Overtime>> getOvertimes() {
        return Overtimes;
    }

    public void retrieveOvertimes(String status, String search, Integer page, Integer show) {

        final URLs_v2 url = new URLs_v2();
        final User user = SharedPrefManager.getInstance(application.getBaseContext()).getUser();

        String GET_ADMIN_BACKEND_DATA = url.GET_ADMIN_BACKEND_DATA("overtime/approver-index/" + user.getUser_id(), new queryStringBuilder(user.getApi_token(), user.getLink(), page, search, show, status).build());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_ADMIN_BACKEND_DATA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {

                        JSONArray OTs = response_obj.getJSONObject("msg").getJSONArray("data");

                        setOverTimesToArray(OTs);
                        setPagination(response_obj.getJSONObject("msg"));
                        //if (OTs.length() > 0) {
                        //    setOverTimesToArray(OTs);
                        //} else {
                        //    Adjustments.setValue(OTs);
                        //}
                    } else {
                        Overtimes.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Overtimes.setValue(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Overtimes.setValue(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                return Helper.getInstance(application.getBaseContext()).headers();
            }
        };
        Helper.getInstance(application.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void setOverTimesToArray(JSONArray i_OTs) {

        int array_len = i_OTs.length();
        ArrayList<Overtime> OvertimeArray = new ArrayList<>();

        if (array_len > 0) {

            for (int i = 0; i < array_len; i++) {

                try {
                    JSONObject adjustment = i_OTs.getJSONObject(i);

                    Integer id = adjustment.getInt("id");
                    String employee_id = adjustment.getString("user_id");
                    String date = adjustment.getString("date");
                    String start_time = adjustment.getString("start_time");
                    String end_time = adjustment.getString("end_time");
                    String status = adjustment.getString("status");
                    String reason = adjustment.getString("reason");
                    String checked_by = adjustment.getString("checked_by");
                    String checked_at = adjustment.getString("checked_at");


                    String requested_at = adjustment.has("requested_at") ? adjustment.getString("requested_at") : "";
                    String fname = adjustment.getJSONObject("profile").getString("fname");
                    String lname = adjustment.getJSONObject("profile").getString("lname");
                    String image_file_name = adjustment.getJSONObject("profile").getString("image");

                    String updated_at = adjustment.has("updated_at") ? adjustment.getString("updated_at") : "";
                    String created_at = adjustment.has("created_at") ? adjustment.getString("created_at") : "";


                    Overtime overtime = new Overtime(id, employee_id, date, start_time, end_time, status, reason, checked_by, checked_at, requested_at, fname, lname, image_file_name, updated_at, created_at);
                    OvertimeArray.add(overtime);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Overtimes.setValue(OvertimeArray);
        } else {
            Overtimes.setValue(OvertimeArray);
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

                        JSONArray OTs = response_obj.getJSONObject("msg").getJSONArray("data");

                        setOverTimesToArray(OTs);
                        setPagination(response_obj.getJSONObject("msg"));
                        //if (OTs.length() > 0) {
                        //    setOverTimesToArray(OTs);
                        //} else {
                        //    Adjustments.setValue(OTs);
                        //}
                    } else {
                        Overtimes.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Overtimes.setValue(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Overtimes.setValue(null);
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
