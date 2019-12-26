package com.example.timekeeping_beta.Fragments.GlobalSettings.LeaveTypes;

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

public class LeaveTypesViewModel extends AndroidViewModel {

    final private MutableLiveData<List<LeaveType>> LeaveTypes = new MutableLiveData<>();

    final private MutableLiveData<String> create_leave_type_result = new MutableLiveData<>();
    final private MutableLiveData<String> edit_leave_type_result = new MutableLiveData<>();

    private Context ctx;
    private URLs url;
    private User user;
    private String DATABASE;
    private String TABLE;
    private String url_resource_leave_types;

    public LeaveTypesViewModel(@NonNull Application application) {
        super(application);

        ctx = getApplication().getApplicationContext();

        url = new URLs();
        user = SharedPrefManager.getInstance(ctx).getUser();
        DATABASE = user.getC1();
        TABLE = user.getC2();
        url_resource_leave_types = url.url_resource_leave_types(user.getApi_token(), user.getLink());
    }

    public MutableLiveData<List<LeaveType>> getLeaveTypes() {
        return LeaveTypes;
    }
    public LiveData<String> getCreateLeaveTypeResult() { return create_leave_type_result; }
    public LiveData<String> getEditLeaveTypeResult() { return edit_leave_type_result; }

    public void retrieveAllLeaveTypes(){

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_resource_leave_types, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")){
                        JSONArray holidays = response_obj.getJSONArray("msg");

                        setLeaveTypesToArray(holidays);
                    }else{
                        LeaveTypes.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    LeaveTypes.setValue(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LeaveTypes.setValue(null);
            }
        })
        {
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

    private void setLeaveTypesToArray(JSONArray i_holiday_types){

        int array_len = i_holiday_types.length();
        ArrayList<LeaveType> LeaveTypeArray = new ArrayList<>();

        if (array_len > 0){

            for (int i = 0;i < array_len;i++) {

                try {
                    JSONObject holiday_type = i_holiday_types.getJSONObject(i);

                    Integer id = holiday_type.getInt("id");
                    String name = holiday_type.getString("leave_name");
                    String code = holiday_type.getString("leave_code");

                    LeaveType leave_type_object = new LeaveType(
                            id,
                            code,
                            name
                    );

                    LeaveTypeArray.add(leave_type_object);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            LeaveTypes.setValue(LeaveTypeArray);
        }
        else
        {
            LeaveTypes.setValue(LeaveTypeArray);
        }
    }

    public void createHolidayType(final String ht_code, final String ht_name) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_resource_leave_types, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")){
                        create_leave_type_result.setValue(status);
                    }else{
                        create_leave_type_result.setValue(response_obj.getString("msg")   );
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
        })
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("d", DATABASE);
                headers.put("t", TABLE);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("leave_code", ht_code);
                params.put("leave_name", ht_name);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60 * 1000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Helper.getInstance(ctx).addToRequestQueue(stringRequest);
    }

    public void updateHolidayType(final int lt_id,final String lt_code, final String lt_name) {

        String update_leave_type = url.url_update_leave_types(String.valueOf(lt_id),user.getApi_token(),user.getLink());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, update_leave_type, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")){
                        edit_leave_type_result.setValue(status);
                    }else{
                        edit_leave_type_result.setValue(response_obj.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    edit_leave_type_result.setValue(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                edit_leave_type_result.setValue(null);
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("d", DATABASE);
                headers.put("t", TABLE);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("leave_code", lt_code);
                params.put("leave_name", lt_name);
                params.put("_method", "PUT");
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60 * 1000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Helper.getInstance(ctx).addToRequestQueue(stringRequest);
    }
}
