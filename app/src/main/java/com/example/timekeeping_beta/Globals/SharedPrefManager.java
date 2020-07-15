package com.example.timekeeping_beta.Globals;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.timekeeping_beta.Activities.LoginActivity;
import com.example.timekeeping_beta.Globals.Models.User;

import org.json.JSONObject;

public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "mysharedpref";
    private static final String KEY_USER_ID = "keyuserid";
    private static final String KEY_EMP_NUM = "keyempnum";
    private static final String KEY_C1 = "c1";
    private static final String KEY_C2 = "c2";
    private static final String KEY_LINK = "link";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_FNAME = "fname";
    private static final String KEY_LNAME = "lname";
    private static final String KEY_API_TOKEN = "api_token";
    private static final String KEY_COMPANY = "company";

    private static final String KEY_COMPANY_ID = "company_id";
    private static final String KEY_ROLE_ID = "role_id";
    private static final String KEY_ROLE_NAME = "role_name";
    private static final String KEY_SCHEDULE_SHIFT_IN = "original_time_in";
    private static final String KEY_SCHEDULE_SHIFT_OUT = "shift_out";
    private static final String KEY_IS_APPROVER = "is_approver";
    private static final String KEY_USER_IMAGE_FILE_NAME = "image_file_name_path";
    private static final String KEY_USER_BUNDEES = "user_bundees";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_BROKEN_SHIFT = "broken_shift";

    private static SharedPrefManager mInstance;
    private Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_USER_ID, user.getUser_id());
        editor.putString(KEY_EMP_NUM, user.getEmp_num());
        editor.putString(KEY_C1, user.getC1());
        editor.putString(KEY_C2, user.getC2());
        editor.putString(KEY_LINK, user.getLink());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_FNAME, user.getFname());
        editor.putString(KEY_LNAME, user.getLname());
        editor.putString(KEY_API_TOKEN, user.getApi_token());
        editor.putString(KEY_COMPANY, user.getCompany());

        editor.putString(KEY_COMPANY_ID, user.getCompany_ID());
        editor.putString(KEY_ROLE_ID, user.getRole_ID());
        editor.putString(KEY_ROLE_NAME, user.getRole_name());
        editor.putString(KEY_SCHEDULE_SHIFT_IN, user.getSchedule_shift_in());
        editor.putString(KEY_SCHEDULE_SHIFT_OUT, user.getSchedule_shift_out());
        editor.putString(KEY_IS_APPROVER, user.getIsApprover());
        editor.putString(KEY_USER_IMAGE_FILE_NAME, user.getImageFileName());
        editor.putString(KEY_USER_BUNDEES, user.getUser_bundees());
        editor.putString(KEY_TOKEN, user.getToken());
        editor.apply();
    }

    public void setFname(String fname) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_FNAME, fname);
        editor.apply();
    }

    public void setLname(String lname) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_LNAME, lname);
        editor.apply();
    }

    public void setEmail(String email) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public void setKeyBrokenShift(boolean b) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_BROKEN_SHIFT, "1");
        editor.apply();
    }

    // check if the user is logged in
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMP_NUM, null) != null;
    }

    //
    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getString(KEY_USER_ID, null),
                sharedPreferences.getString(KEY_EMP_NUM, null),
                sharedPreferences.getString(KEY_C1, null),
                sharedPreferences.getString(KEY_C2, null),
                sharedPreferences.getString(KEY_LINK, null),
                sharedPreferences.getString(KEY_EMAIL, null),
                sharedPreferences.getString(KEY_FNAME, null),
                sharedPreferences.getString(KEY_LNAME, null),
                sharedPreferences.getString(KEY_API_TOKEN, null),
                sharedPreferences.getString(KEY_COMPANY, null),
                sharedPreferences.getString(KEY_COMPANY_ID, null),
                sharedPreferences.getString(KEY_ROLE_ID, null),
                sharedPreferences.getString(KEY_ROLE_NAME, null),
                sharedPreferences.getString(KEY_SCHEDULE_SHIFT_IN, null),
                sharedPreferences.getString(KEY_SCHEDULE_SHIFT_OUT, null),
                sharedPreferences.getString(KEY_IS_APPROVER, null),
                sharedPreferences.getString(KEY_USER_IMAGE_FILE_NAME, null),
                sharedPreferences.getString(KEY_USER_BUNDEES, null),
                sharedPreferences.getString(KEY_TOKEN, null),
                sharedPreferences.getString(KEY_BROKEN_SHIFT, null)
        );
    }

    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        mCtx.startActivity(new Intent(mCtx, LoginActivity.class));
    }

    public void clearSharedPrefs() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

    }
}
