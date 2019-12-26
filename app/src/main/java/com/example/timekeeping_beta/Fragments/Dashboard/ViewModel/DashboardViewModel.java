package com.example.timekeeping_beta.Fragments.Dashboard.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.timekeeping_beta.Fragments.Dashboard.Models.Dashboard;
import com.example.timekeeping_beta.Fragments.Dashboard.Models.DashboardCounts;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.Globals.Models.User;
import com.example.timekeeping_beta.Globals.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DashboardViewModel extends AndroidViewModel {

    final private MutableLiveData<Dashboard> UserDashboard = new MutableLiveData<>();
    final private MutableLiveData<DashboardCounts> PendingApprovals = new MutableLiveData<>();
    final private MutableLiveData<DashboardCounts> PendingRequests = new MutableLiveData<>();
    private MutableLiveData<Boolean> invalidToken = new MutableLiveData<>();

    private Integer totalRequestCount = 0;
    private Integer totalApprovalCount = 0;
    private MutableLiveData<Integer> totalRACount = new MutableLiveData<>();

    private Application application;
    private Helper helper;
    private Map<String,String> headers;
    private RetryPolicy retryPolicy;

    public DashboardViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        invalidToken.setValue(false);
        helper = Helper.getInstance(application.getBaseContext());
        headers = helper.headers();
        retryPolicy = helper.volleyRetryPolicy();
    }

    public MutableLiveData<Integer> getTotalRACount() {
        return this.totalRACount;
    }

    public MutableLiveData<Boolean> getInvalidToken() {
        return invalidToken;
    }

    public LiveData<Dashboard> getUserDashboard() {
        return UserDashboard;
    }

    public LiveData<DashboardCounts> getPendingApprovals() {
        return PendingApprovals;
    }

    public LiveData<DashboardCounts> getPendingRequests() {
        return PendingRequests;
    }

    public void retrieveUserDashboard() {

        final URLs url = new URLs();
        final User user = SharedPrefManager.getInstance(application.getApplicationContext()).getUser();
        final String DATABASE = user.getC1();
        final String TABLE = user.getC2();

        String url_get_user_dashboard = url.url_get_user_dashboard(user.getUser_id(), user.getApi_token(), user.getLink());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_get_user_dashboard, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {
                        JSONObject msg = response_obj.getJSONObject("msg");

                        setToUserDashBoard(msg);
                    } else if (status.equals("error")) {

                        if (response_obj.get("msg") instanceof String) {
                            String invalidTokenMessage = "Access Denied: invalid token!";
                            if (response_obj.getString("msg").equals(invalidTokenMessage)) {
                                invalidToken.setValue(true);
                            }
                        }

                        UserDashboard.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    UserDashboard.setValue(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                UserDashboard.setValue(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };
        stringRequest.setRetryPolicy(retryPolicy);

        Helper.getInstance(application).addToRequestQueue(stringRequest);
    }

    private void setToUserDashBoard(JSONObject response) {

        Integer today_late = 0;
        Integer today_undertime = 0;
        Integer today_accumulated = 0;
        Integer monthly_late = 0;
        Integer monthly_undertime = 0;
        Integer monthly_accumulated = 0;
        Integer yearly_late = 0;
        Integer yearly_undertime = 0;
        Integer yearly_accumulated = 0;

        try {
            today_late = response.getInt("today_late");
            today_undertime = response.getInt("today_undertime");
            today_accumulated = response.getInt("today_accumulated");
            monthly_late = response.getInt("monthly_late");
            monthly_undertime = response.getInt("monthly_undertime");
            monthly_accumulated = response.getInt("monthly_accumulated");
            yearly_late = response.getInt("yearly_late");
            yearly_undertime = response.getInt("yearly_undertime");
            yearly_accumulated = response.getInt("yearly_accumulated");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Dashboard userDashboard = new Dashboard(today_late,
                today_undertime,
                today_accumulated,
                monthly_late,
                monthly_undertime,
                monthly_accumulated,
                yearly_late,
                yearly_undertime,
                yearly_accumulated);

        UserDashboard.setValue(userDashboard);
    }

    public void retrievePendingRequests() {
        final URLs url = new URLs();
        final User user = SharedPrefManager.getInstance(application).getUser();
        final String DATABASE = user.getC1();
        final String TABLE = user.getC2();

        String url_resource_pending_reuests = url.url_resource_pending_reuests(user.getApi_token(), user.getLink());

        StringRequest pending_requests_stringRequest = new StringRequest(Request.Method.POST, url_resource_pending_reuests, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {
                        JSONObject msg = response_obj.getJSONObject("msg");

                        DashboardCounts pa = new DashboardCounts(
                                msg.getInt("leave"),
                                msg.getInt("overtime"),
                                msg.getInt("adjustment"),
                                msg.getInt("time_approval")
                        );

                        PendingRequests.setValue(pa);
                        totalRequestCount = pa.getAdjustment() + pa.getOvertime() + pa.getLeave();
                        recomputeTotalCount();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    PendingRequests.setValue(null);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                PendingRequests.setValue(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user.getUser_id());
                return params;
            }
        };

        Helper.getInstance(application.getApplicationContext()).addToRequestQueue(pending_requests_stringRequest);
    }

    public void retrievePendingApprovals() {
        final URLs url = new URLs();
        final User user = SharedPrefManager.getInstance(application).getUser();
        final String DATABASE = user.getC1();
        final String TABLE = user.getC2();

        String url_resource_pending_approvals = url.url_resource_pending_approvals(user.getApi_token(), user.getLink());

        StringRequest pending_approvals_stringRequest = new StringRequest(Request.Method.POST, url_resource_pending_approvals, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {
                        JSONObject msg = response_obj.getJSONObject("msg");

                        DashboardCounts pa = new DashboardCounts(
                                msg.getInt("leave"),
                                msg.getInt("overtime"),
                                msg.getInt("adjustment"),
                                msg.getInt("time_approval")
                        );

                        PendingApprovals.setValue(pa);
                        totalApprovalCount = pa.getAdjustment() + pa.getOvertime() + pa.getLeave();
                        recomputeTotalCount();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    PendingApprovals.setValue(null);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                PendingApprovals.setValue(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user.getUser_id());
                return params;
            }
        };

        Helper.getInstance(application.getApplicationContext()).addToRequestQueue(pending_approvals_stringRequest);
    }

    private void recomputeTotalCount () {

        totalRACount.setValue(totalRequestCount + totalApprovalCount);
    }
}
