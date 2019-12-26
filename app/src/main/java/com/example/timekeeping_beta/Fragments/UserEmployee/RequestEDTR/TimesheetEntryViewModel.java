package com.example.timekeeping_beta.Fragments.UserEmployee.RequestEDTR;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.timekeeping_beta.Globals.CustomClasses.Flag;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.Globals.Models.User;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.Globals.StaticData.URLs_v2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TimesheetEntryViewModel extends AndroidViewModel {

    private final Application application;

    final private MutableLiveData<ArrayList<TimesheetEntry>> LiveTimesheetEntries = new MutableLiveData<>();
    final private MutableLiveData<TimesheetEntryDates> LiveTimesheetEntriesDates = new MutableLiveData<>();

    private MutableLiveData<Boolean> createResult = new MutableLiveData<>(), editResult = new MutableLiveData<>(), deleteResult = new MutableLiveData<>();

    private String selectedDateRangeStart;
    private Map<String, String> Headers;

    public TimesheetEntryViewModel(@NonNull Application application) {
        super(application);
        this.application = application;

        Headers = Helper.getInstance(application.getApplicationContext()).headers();
    }

    public MutableLiveData<Boolean> getCreateResult() {
        return this.createResult;
    }

    public MutableLiveData<Boolean> getEditResult() {
        return this.editResult;
    }

    public MutableLiveData<Boolean> getDeleteResult() {
        return this.deleteResult;
    }

    public MutableLiveData<ArrayList<TimesheetEntry>> getLiveTimesheetEntries() {
        return this.LiveTimesheetEntries;
    }

    public MutableLiveData<TimesheetEntryDates> getLiveTimesheetEntriesDates() {
        return this.LiveTimesheetEntriesDates;
    }

    public void retrieveEDTR() {

        final URLs url = new URLs();
        final User user = SharedPrefManager.getInstance(application.getApplicationContext()).getUser();

        String url_edtr = url.url_edtr(user.getUser_id(), user.getApi_token(), user.getLink());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_edtr, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {

                        JSONObject TimesheetEntries = response_obj.getJSONObject("msg");

                        setToTimesheetEntries(TimesheetEntries);
                        setToLiveTimesheetEntriesDates(TimesheetEntries);
                    } else {
                        LiveTimesheetEntries.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    LiveTimesheetEntries.setValue(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LiveTimesheetEntries.setValue(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                return Headers;
            }
        };
        Helper.getInstance(application.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public void retrieveEDTR(final String date_range) {

        selectedDateRangeStart = date_range;

        final URLs url = new URLs();
        final User user = SharedPrefManager.getInstance(application.getApplicationContext()).getUser();
        final String DATABASE = user.getC1();
        final String TABLE = user.getC2();

        String url_edtr_range = url.url_edtr_range(user.getUser_id(), date_range, user.getApi_token(), user.getLink());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_edtr_range, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {

                        JSONObject TimesheetEntries = response_obj.getJSONObject("msg");

                        setToTimesheetEntries(TimesheetEntries);
                        setToLiveTimesheetEntriesDates(TimesheetEntries);
                    } else {
                        LiveTimesheetEntries.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    LiveTimesheetEntries.setValue(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LiveTimesheetEntries.setValue(null);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("date", date_range);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {

                return Headers;
            }
        };
        Helper.getInstance(application.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void setToLiveTimesheetEntriesDates(JSONObject jsonObjectTimesheetEntries) {

        try {
            JSONObject jsonObjectDates = jsonObjectTimesheetEntries.getJSONObject("dates");

            LiveTimesheetEntriesDates.setValue(new TimesheetEntryDates(
                    jsonObjectDates.getString("start"),
                    jsonObjectDates.getString("end"),
                    jsonObjectDates.getString("next"),
                    jsonObjectDates.getString("previous")
            ));

        } catch (JSONException e) {
            LiveTimesheetEntriesDates.setValue(null);
        }
    }

    private void setToTimesheetEntries(JSONObject jsonObjectTimesheetEntries) {

        ArrayList<TimesheetEntry> timesheetEntries = new ArrayList<>();

        try {
            JSONArray jsonArrayEDTR = jsonObjectTimesheetEntries.getJSONArray("edtr");

            for (int i = 0; i < jsonArrayEDTR.length(); i++) {

                JSONObject jsonObjectEDTR = jsonArrayEDTR.getJSONObject(i);

                timesheetEntries.add(new TimesheetEntry(

                        jsonObjectEDTR.has("id") ? jsonObjectEDTR.getString("id") : "null",
                        jsonObjectEDTR.getString("date_in"),
                        jsonObjectEDTR.getString("time_in"),
                        jsonObjectEDTR.getString("time_out"),
                        jsonObjectEDTR.getString("remarks"),
                        jsonObjectEDTR.getString("status"),
                        jsonObjectEDTR.getString("checked_by"),
                        jsonObjectEDTR.getString("checked_at"),
                        jsonObjectEDTR.getString("reference"),
                        jsonObjectEDTR.getString("reason"),
                        jsonObjectEDTR.getString("day_type"),


                        jsonObjectEDTR.getInt("shift"),
                        jsonObjectEDTR.getString("shift_in"),
                        jsonObjectEDTR.getString("shift_out"),
                        jsonObjectEDTR.getBoolean("isBroken"),
                        jsonObjectEDTR.getJSONObject("overtime"),
                        jsonObjectEDTR.getString("late"),
                        jsonObjectEDTR.getString("undertime"),
                        jsonObjectEDTR.getString("isadjusted"),
                        jsonObjectEDTR.getString("date")
                ));
            }

            LiveTimesheetEntries.setValue(timesheetEntries);

        } catch (JSONException e) {
            LiveTimesheetEntries.setValue(timesheetEntries);
        }
    }

    public void createEDTRAdjustment(final TimesheetEntry selected_edtr, final String time_in, final String time_out, final String remarks) {

        final URLs url = new URLs();
        final User user = SharedPrefManager.getInstance(application.getApplicationContext()).getUser();
        final String DATABASE = user.getC1();
        final String TABLE = user.getC2();

        String url_edtr_create = url.url_edtr_create(user.getApi_token(), user.getLink());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_edtr_create, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {

                        createResult.setValue(true);
                        resetTimesheetEntries();

                    } else {
                        LiveTimesheetEntries.setValue(null);
                        createResult.setValue(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    LiveTimesheetEntries.setValue(null);
                    createResult.setValue(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LiveTimesheetEntries.setValue(null);
                createResult.setValue(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user.getUser_id());
                params.put("date_in", selected_edtr.getDate_in());
                params.put("time_in", time_in);
                params.put("time_out", time_out);
                params.put("day_type", selected_edtr.getDay_type());
                params.put("reference", "edtr");
                params.put("status", "pending");
                params.put("remarks", remarks);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                return Headers;
            }
        };
        Helper.getInstance(application.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public void updateEDTRAdjustment(final TimesheetEntry selected_edtr, final String time_in, final String time_out, final String remarks) {

        final URLs url = new URLs();
        final User user = SharedPrefManager.getInstance(application.getApplicationContext()).getUser();
        final String DATABASE = user.getC1();
        final String TABLE = user.getC2();

        String url_edtr_update = url.url_edtr_edit(user.getApi_token(), user.getLink());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_edtr_update, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {

                        resetTimesheetEntries();
                        editResult.setValue(true);
                    } else {
                        LiveTimesheetEntries.setValue(null);
                        editResult.setValue(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    LiveTimesheetEntries.setValue(null);
                    editResult.setValue(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LiveTimesheetEntries.setValue(null);
                editResult.setValue(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", selected_edtr.getId());
                params.put("user_id", user.getUser_id());
                params.put("date_in", selected_edtr.getDate_in());
                params.put("time_in", time_in);
                params.put("time_out", time_out);
                params.put("day_type", selected_edtr.getDay_type());
                params.put("reference", selected_edtr.getReference());
                params.put("status", "pending");
                params.put("remarks", remarks);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                return Headers;
            }
        };
        Helper.getInstance(application.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public void deleteEDTRAdjustment(final String edtr_id) {

        final URLs url = new URLs();
        final User user = SharedPrefManager.getInstance(application.getApplicationContext()).getUser();
        final String DATABASE = user.getC1();
        final String TABLE = user.getC2();

        String url_edtr_delete = url.url_edtr_delete(user.getApi_token(), user.getLink());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_edtr_delete, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {

                        resetTimesheetEntries();
                        deleteResult.setValue(true);

                    } else {
                        LiveTimesheetEntries.setValue(null);
                        deleteResult.setValue(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    LiveTimesheetEntries.setValue(null);
                    deleteResult.setValue(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LiveTimesheetEntries.setValue(null);
                deleteResult.setValue(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", edtr_id);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                return Headers;
            }
        };
        Helper.getInstance(application.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void resetTimesheetEntries() {
        if (selectedDateRangeStart != null) {
            retrieveEDTR(selectedDateRangeStart);
        } else {
            retrieveEDTR();
        }
    }

    public void updateWeeklyAdjustments(final int pos, final String time_in, final String time_out, final String remarks, final int flag) {

        ArrayList<TimesheetEntry> tes = LiveTimesheetEntries.getValue();

        if (tes != null && !tes.isEmpty()) {

            TimesheetEntry te = tes.get(pos);

            if (flag == Flag.CALLBACK_EDIT) {
                te.setTime_in(time_in);
                te.setTime_out(time_out);
                te.setRemarks(remarks);
                te.setStatus("pending");
            } else {
                te.setTime_in("");
                te.setTime_out("");
                te.setRemarks("");
                te.setStatus("null");
            }

        }

        LiveTimesheetEntries.setValue(tes);
    }

    private JSONObject jsonBuilder() {

        JSONArray approvals = new JSONArray();

        if (LiveTimesheetEntries.getValue() != null) {

            int i = 0;
            for (TimesheetEntry te : LiveTimesheetEntries.getValue()) {

                JSONObject jo = new JSONObject();

                try {
                    jo.put("time_in", te.getTime_in());
                    jo.put("time_out", te.getTime_out());
                    jo.put("remarks", te.getRemarks());
                    jo.put("status", te.getStatus());
                    jo.put("checked_at", te.getChecked_at());
                    jo.put("checked_by", te.getChecked_by());
                    jo.put("date", te.getDate_in());
                    jo.put("date_in", te.getDate_in());
                    jo.put("day_type", te.getDay_type());


                    jo.put("shift_in", te.getShift_in());
                    jo.put("shift_out", te.getShift_out());
                    jo.put("isBroken", te.isBroken());
                    jo.put("shift", te.getShift());
                    jo.put("overtime", te.getOvertime());
                    jo.put("late", te.getLate());
                    jo.put("undertime", te.getUndertime());
                    jo.put("isadjusted", te.isAdjusted());

                    //approvals.put(String.valueOf(i),jo);
                    approvals.put(jo);

                    i++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        JSONObject apps = new JSONObject();
        try {
            apps.put("approvals", approvals);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return apps;
    }

    public void sendWeeklyTimeSheets() {
        final URLs_v2 url = new URLs_v2();
        final User user = SharedPrefManager.getInstance(application.getApplicationContext()).getUser();

        String url_edtr_create = url.GET_CLOCK_BACKEND_DATA("timeapproval/create/" + user.getUser_id(), "?api_token=" + user.getApi_token() + "&link=" + user.getLink());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_edtr_create, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {

                        createResult.setValue(true);
                        resetTimesheetEntries();

                    } else {
                        LiveTimesheetEntries.setValue(null);
                        createResult.setValue(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    LiveTimesheetEntries.setValue(null);
                    createResult.setValue(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LiveTimesheetEntries.setValue(null);
                createResult.setValue(false);
            }
        }) {


            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                final String requestBody = jsonBuilder().toString();
                return requestBody.getBytes(Charset.forName("UTF-8"));
            }

            @Override
            public Map<String, String> getHeaders() {
                return Headers;
            }
        };
        Helper.getInstance(application.getApplicationContext()).addToRequestQueue(stringRequest);
    }

}
