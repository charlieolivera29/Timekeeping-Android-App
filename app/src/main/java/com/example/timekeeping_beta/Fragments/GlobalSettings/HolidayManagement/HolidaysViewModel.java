package com.example.timekeeping_beta.Fragments.GlobalSettings.HolidayManagement;

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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HolidaysViewModel extends AndroidViewModel {

    final private MutableLiveData<List<Holiday>> Holidays = new MutableLiveData<>();

    final private MutableLiveData<String> create_holiday_result = new MutableLiveData<>();
    final private MutableLiveData<String> edit_holiday_result = new MutableLiveData<>();

    private Context ctx;
    private URLs url;
    private User user;
    private String DATABASE;
    private String TABLE;
    private String url_resource_holidays;

    public HolidaysViewModel(@NonNull Application application) {
        super(application);

        ctx = getApplication().getApplicationContext();

        url = new URLs();
        user = SharedPrefManager.getInstance(ctx).getUser();
        DATABASE = user.getC1();
        TABLE = user.getC2();
        url_resource_holidays = url.url_resource_holidays(user.getApi_token(), user.getLink());
    }

    public LiveData<List<Holiday>> getHolidaysTypes() {
        return Holidays;
    }

    public LiveData<String> getCreateHolidayResult() {
        return create_holiday_result;
    }

    public LiveData<String> getEditHolidayResult() {
        return edit_holiday_result;
    }

    public void retrieveAllHolidays() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_resource_holidays, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {
                        JSONArray holidays = response_obj.getJSONArray("msg");

                        setHolidaysToArray(holidays);
                    } else {
                        Holidays.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Holidays.setValue(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Holidays.setValue(null);
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

    private void setHolidaysToArray(JSONArray i_holiday) {

        int array_len = i_holiday.length();
        ArrayList<Holiday> HolidayArray = new ArrayList<>();

        if (array_len > 0) {

            for (int i = 0; i < array_len; i++) {

                try {
                    JSONObject holiday = i_holiday.getJSONObject(i);

                    Integer id = holiday.getInt("id");
                    String holiday_name = holiday.getString("holiday_name");
                    String holiday_code = holiday.getString("holiday_code");
                    String holiday_date = holiday.getString("holiday_date");
                    String holiday_type = holiday.getString("holiday_type");
                    String holiday_remarks = holiday.getString("holiday_remarks");
                    String added_by = holiday.getString("added_by");
                    JSONArray location = holiday.getJSONArray("location");
                    JSONArray location_id = holiday.getJSONArray("location_id");

                    Holiday holiday_object = new Holiday(
                            id,
                            holiday_name,
                            holiday_code,
                            holiday_date,
                            holiday_type,
                            holiday_remarks,
                            added_by,
                            location,
                            location_id
                    );

                    HolidayArray.add(holiday_object);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Holidays.setValue(HolidayArray);
        } else {
            Holidays.setValue(HolidayArray);
        }
    }

    public void createHoliday(final String ih_added_by,final String ih_date,final String ih_holiday_name,final String ih_remarks,final String ih_type,final JSONObject ih_location_ids) {

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("added_by", ih_added_by);
            jsonBody.put("holiday_date", ih_date);
            jsonBody.put("holiday_name", ih_holiday_name);
            jsonBody.put("holiday_remarks", ih_remarks);
            jsonBody.put("holiday_type", ih_type);
            jsonBody.put("location_id", ih_location_ids);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = jsonBody.toString();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_resource_holidays, new Response.Listener<String>() {
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
                create_holiday_result.setValue(error.getLocalizedMessage());
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
                return requestBody.getBytes(Charset.forName("UTF-8"));
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
