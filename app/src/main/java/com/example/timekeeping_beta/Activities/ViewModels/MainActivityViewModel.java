package com.example.timekeeping_beta.Activities.ViewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.Globals.Models.User;
import com.example.timekeeping_beta.Globals.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivityViewModel extends AndroidViewModel {

    private MutableLiveData<String> user_image_file_name = new MutableLiveData<>();
    private Application application;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);

        this.application = getApplication();
    }

    public void retrieveUserProfile(){

        final URLs url = new URLs();
        final User user = SharedPrefManager.getInstance(application.getApplicationContext()).getUser();
        final String DATABASE = user.getC1();
        final String TABLE = user.getC2();

        String url_user_profile = url.url_user_profile(user.getUser_id(),user.getApi_token(), user.getLink());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_user_profile, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")){
                        JSONArray msg_array = response_obj.getJSONArray("msg");
                        JSONObject msg = msg_array.getJSONObject(0);

                        JSONObject user = msg.getJSONObject("users");

                        User u = SharedPrefManager.getInstance(application.getApplicationContext()).getUser();
                        u.setFile_name(user.getString("image"));
                        SharedPrefManager.getInstance(application.getApplicationContext()).userLogin(u);
                        user_image_file_name.setValue(user.getString("image"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() {
                return Helper.getInstance(application.getBaseContext()).headers();
            }
        };
        Helper.getInstance(application.getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public MutableLiveData<String> getUserFileName(){
        return user_image_file_name;
    }
}
