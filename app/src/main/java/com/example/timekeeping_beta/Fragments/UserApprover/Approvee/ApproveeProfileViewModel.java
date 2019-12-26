package com.example.timekeeping_beta.Fragments.UserApprover.Approvee;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.Globals.Models.User;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.Globals.Models.UserProfileAll;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApproveeProfileViewModel extends AndroidViewModel {

    private final Application application;
    final private MutableLiveData<UserProfileAll> LiveApprovee = new MutableLiveData<>();

    public ApproveeProfileViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public MutableLiveData<UserProfileAll> getLiveApprovee() {
        return LiveApprovee;
    }

    public void retrieveUserDashboard(final String user_id) {

        final URLs url = new URLs();
        final User user = SharedPrefManager.getInstance(application.getApplicationContext()).getUser();
        final String DATABASE = user.getC1();
        final String TABLE = user.getC2();

        String user_profile_url = url.url_user_profile(user_id, user.getApi_token(), user.getLink());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, user_profile_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Helper helper = Helper.getInstance(application.getApplicationContext());

                UserProfileAll upa;

                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {
                        JSONArray msg_array = response_obj.getJSONArray("msg");
                        JSONObject msg = msg_array.getJSONObject(0);

                        String id = msg.getString("id");


                        JSONObject users = msg.getJSONObject("users");

                        JSONObject user_roles = msg.has("user_roles") ? msg.getJSONObject("user_roles") : msg.getJSONObject("user_role");

                        String role_id = user_roles.getString("role_id");
                        String role_name = user_roles.has("role_name") ? user_roles.getString("role_name") : "No Role Name";

                        JSONArray emp_sched_a = msg.getJSONArray("emp_sched");
                        JSONObject emp_sched_o = emp_sched_a.getJSONObject(0);
                        JSONObject work_location = msg.getJSONObject("work_location");
                        JSONArray timetrack_a = work_location.getJSONArray("timetrack");
                        JSONObject timetrack_o = timetrack_a.getJSONObject(0);

                        JSONArray reports_to = msg.getJSONArray("reports_to");
                        String[] approvers = new String[reports_to.length()];

                        if (reports_to.length() > 0) {
                            for (int i = 0; i < reports_to.length(); i++) {
                                JSONObject jo = reports_to.getJSONObject(i);

                                String approver_name = jo.getString("fname") + " " + jo.getString("lname");
                                approvers[i] = approver_name;
                            }
                        }

                        String user_image = URLs.url_image(user.getLink(), users.getString("image"));

                        UserProfileAll a = new UserProfileAll(
                                users.getString("fname"),
                                users.getString("lname"),
                                msg.getString("email"),
                                users.getString("cell_num"),
                                emp_sched_o.getString("company"),
                                work_location.getString("location_id"),
                                work_location.getString("branch_name"),
                                work_location.getString("branch_name"),
                                timetrack_o.getString("id"),
                                timetrack_o.getString("bundee"),
                                user_id,
                                approvers,
                                role_id,
                                role_name,
                                helper.convertToReadableTime(emp_sched_o.getString("shift_in")),
                                helper.convertToReadableTime(emp_sched_o.getString("shift_out")),
                                user_image
                        );

                        upa = a;
                    } else {
                        upa = null;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    upa = null;
                }

                LiveApprovee.setValue(upa);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LiveApprovee.setValue(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return Helper.getInstance(getApplication().getBaseContext()).headers();
            }
        };
        stringRequest.setRetryPolicy(

                Helper.getInstance(getApplication().getBaseContext()).volleyRetryPolicy()
        );
        Helper.getInstance(application.getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
