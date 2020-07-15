package com.example.timekeeping_beta.Fragments.GlobalSettings.LocationManagement;

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

public class CompanyLocationsViewModel extends AndroidViewModel {

    final private MutableLiveData<List<CompanyLocation>> Locations = new MutableLiveData<>();

    final private MutableLiveData<String> create_holiday_result = new MutableLiveData<>();
    final private MutableLiveData<String> edit_holiday_result = new MutableLiveData<>();

    private Context ctx;
    private URLs url;
    private User user;
    private String DATABASE;
    private String TABLE;
    private String url_resource_locations;

    public CompanyLocationsViewModel(@NonNull Application application) {
        super(application);

        ctx = getApplication().getApplicationContext();

        url = new URLs();
        user = SharedPrefManager.getInstance(ctx).getUser();
        DATABASE = user.getC1();
        TABLE = user.getC2();
        url_resource_locations = url.url_resource_locations(user.getApi_token(), user.getLink());
    }

    public LiveData<List<CompanyLocation>> getLocations() {
        return Locations;
    }

    public LiveData<String> getCreateLocationResult() {
        return create_holiday_result;
    }

    public LiveData<String> getEditLocationsResult() {
        return edit_holiday_result;
    }

    public void retrieveAllLocations() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_resource_locations, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {
                        JSONArray holidays = response_obj.getJSONArray("msg");

                        setLocationsToArray(holidays);
                    } else {
                        Locations.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Locations.setValue(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Locations.setValue(null);
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

    private void setLocationsToArray(JSONArray i_locations) {

        int array_len = i_locations.length();
        ArrayList<CompanyLocation> companyLocationArray = new ArrayList<>();

        if (array_len > 0) {

            for (int i = 0; i < array_len; i++) {

                try {
                    JSONObject location_j_object = i_locations.getJSONObject(i);

                    Integer id = location_j_object.getInt("id");
                    String branch_id = location_j_object.getString("branch_id");
                    String branch_name = location_j_object.getString("branch_name");
                    String address = location_j_object.getString("address");
                    String c_location = location_j_object.getString("location");
                    String description = location_j_object.getString("description");
                    String date_start = location_j_object.getString("date_start");
                    JSONArray timetrack = location_j_object.getJSONArray("timetrack");
                    String change_queue = location_j_object.getString("change_queue");
                    JSONArray schedule = location_j_object.getJSONArray("schedule");
                    JSONArray holidays = location_j_object.getJSONArray("holidays");

                    CompanyLocation holiday_object = new CompanyLocation(
                            id,
                            branch_id,
                            branch_name,
                            address,
                            c_location,
                            description,
                            date_start,
                            timetrack,
                            change_queue,
                            schedule,
                            holidays
                    );

                    companyLocationArray.add(holiday_object);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Locations.setValue(companyLocationArray);
        } else {
            Locations.setValue(companyLocationArray);
        }
    }

    public void createHolidayType(final String ht_code, final String ht_name) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_resource_locations, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {
                        create_holiday_result.setValue(status);
                    } else {
                        create_holiday_result.setValue(response_obj.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    create_holiday_result.setValue(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                create_holiday_result.setValue(null);
            }
        }) {
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
                params.put("holiday_type_code", ht_code);
                params.put("holiday_type_name", ht_name);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60 * 1000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Helper.getInstance(ctx).addToRequestQueue(stringRequest);
    }

    public void updateHolidayType(final int ht_id, final String ht_code, final String ht_name) {

        String update_holiday_type = url.url_update_holiday_types(String.valueOf(ht_id), user.getApi_token(), user.getLink());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, update_holiday_type, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {
                        edit_holiday_result.setValue(status);
                    } else {
                        edit_holiday_result.setValue(response_obj.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    edit_holiday_result.setValue(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                edit_holiday_result.setValue(null);
            }
        }) {
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
                params.put("holiday_type_code", ht_code);
                params.put("holiday_type_name", ht_name);
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
