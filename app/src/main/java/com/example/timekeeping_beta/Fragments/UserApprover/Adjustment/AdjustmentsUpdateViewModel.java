package com.example.timekeeping_beta.Fragments.UserApprover.Adjustment;

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

public class AdjustmentsUpdateViewModel extends AndroidViewModel {

    final private MutableLiveData<List<Adjustment>> Adjustments = new MutableLiveData<>();

    public MutableLiveData<Pagination> getPagination() {
        return pagination;
    }

    final private MutableLiveData<Pagination> pagination = new MutableLiveData<>();

    private final Application application;

    public AdjustmentsUpdateViewModel(@NonNull Application application) {
        super(application);

        this.application = application;
    }

    public LiveData<List<Adjustment>> getAdjustments() {
        return Adjustments;
    }

    public void retrieveAllAdjustments(String status, String search, Integer page, Integer show) {

        final URLs_v2 url = new URLs_v2();
        final User user = SharedPrefManager.getInstance(application.getApplicationContext()).getUser();
        final String DATABASE = user.getC1();
        final String TABLE = user.getC2();

        String url_get_approvees = url.GET_ADMIN_BACKEND_DATA("timeadjustment/" + user.getUser_id(), new queryStringBuilder(user.getApi_token(), user.getLink(), page, search, show, status).build());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_get_approvees, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {
                        JSONObject msg = response_obj.getJSONObject("msg");
                        JSONArray adjusments = msg.getJSONObject("adjustments").getJSONArray("data");

                        setApprovalsToArray(adjusments);
                        setPagination(msg.getJSONObject("adjustments"));
                    } else {
                        Adjustments.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Adjustments.setValue(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Adjustments.setValue(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return Helper.getInstance(application.getBaseContext()).headers();
            }
        };
        Helper.getInstance(application.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void setApprovalsToArray(JSONArray i_adjusments) {

        int array_len = i_adjusments.length();
        ArrayList<Adjustment> AdjustmentsArray = new ArrayList<>();

        if (array_len > 0) {

            for (int i = 0; i < array_len; i++) {

                try {
                    JSONObject adjustment = i_adjusments.getJSONObject(i);

                    Integer id = adjustment.getInt("id");
                    String employee_id = adjustment.getString("user_id");
                    String employee_time_in = adjustment.getString("time_in");
                    String employee_time_out = adjustment.getString("time_out");
                    String employee_day_type = adjustment.getString("day_type");

                    String employee_shift_in = adjustment.getString("shift_in");
                    String employee_shift_out = adjustment.getString("shift_out");
                    String employee_date_in = adjustment.getString("date_in");
                    String employee_adjustment_status = adjustment.getString("status");
                    String employee_adjustment_reference = adjustment.getString("reference");

                    String employee_adjustment_reason = adjustment.getString("reason");


                    String employee_old_time_in = adjustment.has("old_time_in") ? adjustment.getString("old_time_in") : "";
                    String employee_old_time_out = adjustment.has("old_time_out") ? adjustment.getString("old_time_out") : "";
                    String employee_old_day_type = adjustment.has("old_day_type") ? adjustment.getString("old_day_type") : "";

                    JSONObject employee_profile_object = adjustment.getJSONObject("profile");
                    String fname = employee_profile_object.getString("fname");
                    String lname = employee_profile_object.getString("lname");
                    String image_file_name = employee_profile_object.getString("image");

                    String requestee_email = "";
                    //employee_profile_object.getString("image");

                    Profile employee_profile = new Profile(fname, lname);

                    Adjustment adjustment_object = new Adjustment(
                            id,
                            employee_id,
                            employee_date_in,
                            employee_old_time_in,
                            employee_old_time_out,
                            employee_old_day_type,
                            employee_time_in,
                            employee_time_out,
                            employee_day_type,
                            employee_shift_in,
                            employee_shift_out,
                            employee_adjustment_reference,
                            employee_adjustment_status,
                            employee_profile,
                            employee_adjustment_reason,
                            image_file_name
                    );

                    AdjustmentsArray.add(adjustment_object);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Adjustments.setValue(AdjustmentsArray);
        } else {
            Adjustments.setValue(AdjustmentsArray);
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
                        JSONObject msg = response_obj.getJSONObject("msg");
                        JSONArray adjusments = msg.getJSONObject("adjustments").getJSONArray("data");

                        setApprovalsToArray(adjusments);
                        setPagination(msg.getJSONObject("adjustments"));
                    } else {
                        Adjustments.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Adjustments.setValue(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Adjustments.setValue(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return Helper.getInstance(application.getBaseContext()).headers();
            }
        };

        stringRequest.setRetryPolicy(Helper.getInstance(application.getBaseContext()).volleyRetryPolicy());
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