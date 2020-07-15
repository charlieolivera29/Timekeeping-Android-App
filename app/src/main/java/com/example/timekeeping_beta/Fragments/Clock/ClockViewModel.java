package com.example.timekeeping_beta.Fragments.Clock;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.timekeeping_beta.Fragments.Profile.Models.Result;
import com.example.timekeeping_beta.Globals.CustomClasses.Interface.UploadAPIs;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.Globals.Models.ApiResult;
import com.example.timekeeping_beta.Globals.Models.User;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClockViewModel extends AndroidViewModel {

    private MutableLiveData<EDTR> LiveEDTR;
    private MutableLiveData<ApiResult> timeInOutResult;
    private MutableLiveData<Integer> pinInput1, pinInput2, pinInput3, pinInput4;

    private Application app;
    private Helper helper;

    private RequestQueue queue;
    private String DATABASE, TABLE;
    private String url_check_clocked_in, url_time_in_out, url_show_timesheet, url_check_entry, url_get_coordinates;
    private User user;
    public JSONArray coordinates;
    public Double latitude, longitude;

    public ClockViewModel(@NonNull Application application) {
        super(application);

        app = application;
        URLs url = new URLs();
        user = SharedPrefManager.getInstance(app.getBaseContext()).getUser();
        queue = Volley.newRequestQueue(app.getBaseContext());
        helper = Helper.getInstance(app.getBaseContext());

        DATABASE = user.getC1();
        TABLE = user.getC2();
        url_check_clocked_in = url.url_check_clocked_in(user.getUser_id(), user.getApi_token(), user.getLink());
        url_time_in_out = url.url_time_in_out(user.getApi_token(), user.getLink());
        url_show_timesheet = url.url_show_timesheet(user.getApi_token(), user.getLink());
        url_check_entry = url.url_check_entry(user.getUser_id(), user.getApi_token(), user.getLink());
        url_get_coordinates = url.get_coordinates(user.getUser_id(), user.getApi_token(), user.getLink());

        pinInput1 = new MutableLiveData<>();
        pinInput2 = new MutableLiveData<>();
        pinInput3 = new MutableLiveData<>();
        pinInput4 = new MutableLiveData<>();
        LiveEDTR = new MutableLiveData<>();
        timeInOutResult = new MutableLiveData<>();
    }

    public MutableLiveData<ApiResult> getTimeInOutResult() {

        return this.timeInOutResult;
    }

    public MutableLiveData<EDTR> getUserEDTR() {

        return this.LiveEDTR;
    }

    public void retrieveUserTimesheet() {
        StringRequest requestTimesheet = new StringRequest(Request.Method.POST, url_show_timesheet, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jo_response = new JSONObject(response);

                    if (jo_response.get("status").equals("success")) {

                        JSONObject jo_msg = jo_response.getJSONObject("msg");
                        String time_in = helper.convertToReadableTime(jo_msg.getString("time_in"));
                        String time_out = helper.convertToReadableTime(jo_msg.getString("time_out"));
                        int shift = jo_msg.getInt("shift");

                        LiveEDTR.setValue(new EDTR(time_in, time_out, shift, ""));
                    } else {
                        LiveEDTR.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    LiveEDTR.setValue(null);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                LiveEDTR.setValue(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return Helper.getInstance(app.getBaseContext()).headers();
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user.getUser_id());
                params.put("date_in", helper.today());
                return params;
            }
        };

        queue.add(requestTimesheet);
    }

    public void retrieveUserTimeViaCheckTimedIn() {

        StringRequest requestTimesheet = new StringRequest(Request.Method.POST, url_check_clocked_in, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jo_response = new JSONObject(response);

                    if (jo_response.get("status").equals("success")) {

                        JSONObject jo_msg = jo_response.getJSONObject("msg");
                        String time_in = helper.convertToReadableTime(jo_msg.getString("time_in"));
                        String time_out = helper.convertToReadableTime(jo_msg.getString("time_out"));
                        int shift = jo_msg.getInt("shift");


                        LiveEDTR.setValue(new EDTR(time_in, time_out, shift, ""));
                    } else {
                        LiveEDTR.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    LiveEDTR.setValue(null);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                LiveEDTR.setValue(null);
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
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("date", helper.today());
                return params;
            }
        };

        queue.add(requestTimesheet);
    }

    public void check_edtr_entry() {
        String url = url_check_entry;
        StringRequest requestTimesheet = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    Log.d("stringResponse", response);
                    JSONObject jo_response = new JSONObject(response);

                    if (jo_response.get("status").equals("success")) {

                        Object jo_msg = jo_response.get("msg");

                        String date_in = app.getResources().getString(R.string.blank_date);
                        String time_in = app.getResources().getString(R.string.blank_time);
                        String time_out = app.getResources().getString(R.string.blank_time);
                        int shift = 0;

                        if (jo_msg instanceof JSONObject) {
                            JSONObject jo_msg2 = jo_response.getJSONObject("msg");

                            date_in = helper.convertToReadableDate(jo_msg2.getString("date_in"));
                            time_in = helper.convertToReadableTime(jo_msg2.getString("time_in"));
                            time_out = helper.convertToReadableTime(jo_msg2.getString("time_out"));
                            shift = jo_msg2.getInt("shift");
                        }

                        LiveEDTR.setValue(new EDTR(time_in, time_out, shift, date_in));
                    } else {
                        LiveEDTR.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    LiveEDTR.setValue(null);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                LiveEDTR.setValue(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = Helper.getInstance(app.getBaseContext()).headers();

                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("date", helper.today());
                return params;
            }
        };

        queue.add(requestTimesheet);
    }

    public void sendTimeInOut(final Double latitude, final Double longitude) {

        StringRequest requestTimeInOut = new StringRequest(Request.Method.POST, url_time_in_out, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getString("status").equals("success")) {

                        timeInOutResult.setValue(new ApiResult(true, "Success!"));
                        check_edtr_entry();
                    } else {
                        timeInOutResult.setValue(new ApiResult(false, obj.getString("msg")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    timeInOutResult.setValue(new ApiResult(false, e.toString()));
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                timeInOutResult.setValue(new ApiResult(false, "Error! Please try again"));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = Helper.getInstance(app.getBaseContext()).headers();
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", user.getUser_id());
                params.put("time", helper.now());
                params.put("date", helper.today());
                params.put("reference", app.getBaseContext().getResources().getString(R.string.api_reference));
                params.put("latitude", latitude.toString());
                params.put("longitude", longitude.toString());
                return params;
            }
        };
        queue.add(requestTimeInOut);
    }

    public void sendTimeInOutWithImage(MediaType fileType, final File originalFile) {

        RequestBody user_id = RequestBody.create(MultipartBody.FORM, user.getUser_id());
        RequestBody time = RequestBody.create(MultipartBody.FORM, helper.now());
        RequestBody date = RequestBody.create(MultipartBody.FORM, helper.today());
        RequestBody reference = RequestBody.create(MultipartBody.FORM, app.getBaseContext().getResources().getString(R.string.api_reference));


        RequestBody filePart = RequestBody.create(
                fileType,
                originalFile
        );

        final MultipartBody.Part file = MultipartBody.Part.createFormData(
                "image",
                originalFile.getName(),
                filePart
        );

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(URLs.ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        UploadAPIs client = retrofit.create(UploadAPIs.class);

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("d", user.getC1());
        headers.put("t", user.getC2());
        headers.put("token", user.getToken());

        final Context ctx = app.getBaseContext();

        Call<Result> call = client.mobileTimeInApproval(user.getApi_token(), user.getLink(),
                headers, user_id, time, date, reference, file);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {

                if (response.body() != null) {
                    if (response.body().getStatus().equals("success")) {
                        timeInOutResult.setValue(new ApiResult(true, "Success!"));
                        check_edtr_entry();
                    } else {
                        timeInOutResult.setValue(new ApiResult(false, response.body().getMsg()));
                    }

                    originalFile.delete();
                } else {
                    timeInOutResult.setValue(new ApiResult(false, ctx.getResources().getString(R.string.api_request_failed)));
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

                timeInOutResult.setValue(new ApiResult(false, app.getBaseContext().getString(R.string.api_request_error)));
            }
        });
    }

    public void get_coordinates() {

        StringRequest requestTimeInOut = new StringRequest(Request.Method.GET, url_get_coordinates, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);

                    if (obj.getString("status").equals("success")) {
                        coordinates = new JSONArray(obj.getString("msg"));
                        Log.d("coordinates", coordinates.toString());
                    } else {
                        Log.d("get_coordinates", obj.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("get_coordinates", e.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("get_coordinates", error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = Helper.getInstance(app.getBaseContext()).headers();
                return params;
            }
        };
        queue.add(requestTimeInOut);
    }
}
