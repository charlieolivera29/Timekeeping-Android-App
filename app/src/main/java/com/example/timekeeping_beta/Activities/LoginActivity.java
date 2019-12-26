package com.example.timekeeping_beta.Activities;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.Globals.Models.User;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.Globals.Static;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.StaticData.URLs_v2;
import com.example.timekeeping_beta.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity {

    public EditText editTextUser;
    public EditText editTextPass;
    public EditText editTextLink;
    public TextView link_forgotpassword;
    public Dialog ForgotPasswordDialog;
    private Context ctx;
    ProgressDialog dialog;
    private Dialog appVersionDialog;

    @Override
    protected void onResume() {
        super.onResume();

        //checkRequiredPermissions();
        if (SharedPrefManager.getInstance(ctx).isLoggedIn()) {
            redirectToMain();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ctx = this;
        SharedPrefManager.getInstance(this);


        editTextUser = findViewById(R.id.et_username);
        editTextPass = findViewById(R.id.et_password);
        editTextLink = findViewById(R.id.et_link);
        link_forgotpassword = findViewById(R.id.link_forgotpassword);

        appVersionDialog = new Dialog(this);

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
                //startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        });


        link_forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toasty.info(ctx, "This feature is currently disabled.", Toasty.LENGTH_LONG).show();
                showForgotPassword();
            }
        });

    }

    private void userLogin() {
        final String email = editTextUser.getText().toString();
        final String password = editTextPass.getText().toString();
        final String link = editTextLink.getText().toString();

        if (email.isEmpty() || password.isEmpty() || link.isEmpty()) {

            Toasty.error(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else {

            final ProgressDialog dialog = ProgressDialog.show(this, null, "Please Wait...");

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_LOGIN, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    dialog.dismiss();
                    try {
                        JSONObject user_obj = new JSONObject(response);

                        //get the msg from user object
                        if (user_obj.getString("status").equals("success")) {

                            Toasty.success(getApplicationContext(), getResources().getString(R.string.api_request_login_success), Toast.LENGTH_SHORT).show();
                            JSONObject userJson = user_obj.getJSONObject("msg");

                            JSONArray roles = userJson.getJSONArray("role");
                            JSONObject role_obj = roles.getJSONObject(0);

                            JSONObject schedule_obj = userJson.getJSONObject("schedule");

                            //create a new user object
                            User user = new User(
                                    userJson.getString("user_id"),
                                    userJson.getString("emp_num"),
                                    userJson.getString("c1"),
                                    userJson.getString("c2"),
                                    userJson.getString("link"),
                                    userJson.getString("email"),
                                    userJson.getString("fname"),
                                    userJson.getString("lname"),
                                    userJson.getString("api_token"),
                                    link,
                                    userJson.getString("company_id"),
                                    role_obj.getString("role_id"),
                                    role_obj.getString("role_name"),
                                    schedule_obj.getString("shift_in"),
                                    schedule_obj.getString("shift_out"),
                                    String.valueOf(userJson.getInt("is_approver")),
                                    "",
                                    userJson.getJSONArray("bundee").toString(),
                                    user_obj.getString("token"),
                                    "0"
                            );

                            //store the user data in shared preferences
                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                            //start the main activity
                            redirectToMain();
                        } else {
                            Toasty.error(getApplicationContext(), user_obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toasty.error(getApplication(), getResources().getString(R.string.api_request_unparsable_data), Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    dialog.dismiss();
                    Toasty.error(getApplication(), getResources().getString(R.string.api_request_error), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", email);
                    params.put("password", password);
                    params.put("link", link);
                    return params;
                }
            };

            Helper.getInstance(this).addToRequestQueue(stringRequest);
        }
    }

    private void showForgotPassword() {
        ForgotPasswordDialog = new Dialog(ctx);
        ForgotPasswordDialog.setContentView(R.layout.dialog_reset_password);

        final EditText forgot_password_username_field = ForgotPasswordDialog.findViewById(R.id.forgot_password_username_field);
        final EditText forgot_password_link_field = ForgotPasswordDialog.findViewById(R.id.forgot_password_link_field);
        Button button_send_resetPassword = ForgotPasswordDialog.findViewById(R.id.button_send_resetPassword);

        button_send_resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!forgot_password_username_field.getText().toString().isEmpty() && !forgot_password_link_field.getText().toString().isEmpty()) {

                    dialog = ProgressDialog.show(ctx, null, "Please Wait...");

                    sendForgotPassword(forgot_password_username_field.getText().toString(), forgot_password_link_field.getText().toString());
                } else {
                    Toasty.error(ctx, "Please fill all fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ForgotPasswordDialog.show();
    }

    private void sendForgotPassword(final String i_username, final String i_link) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.REQUEST_PASSWORD_CHANGE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();

                try {
                    JSONObject user_obj = new JSONObject(response);

                    if (user_obj.getString("status").equals("success")) {
                        Toasty.success(getApplicationContext(), user_obj.getString("msg"), Toast.LENGTH_SHORT).show();
                        sendEmail(i_username, i_link);
                    } else {
                        Toasty.error(getApplication(), user_obj.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Toasty.error(getApplication(), "Error", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", i_username);
                params.put("link", i_link);

                return params;
            }
        };

        Helper.getInstance(this).addToRequestQueue(stringRequest);
    }


    private void sendEmail(final String i_username, final String i_link) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.SEND_EMAIL_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject user_obj = new JSONObject(response);

                    if (user_obj.getString("status").equals("success")) {

                        Toasty.success(getApplicationContext(), user_obj.getString("msg"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toasty.error(getApplicationContext(), user_obj.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(getApplication(), "Error", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", i_username);
                params.put("link", i_link);

                return params;
            }
        };

        Helper.getInstance(this).addToRequestQueue(stringRequest);
    }


    private void checkVersion() {

        final ProgressDialog loadingScreen = ProgressDialog.show(this, null, "Checking app version \nPlease Wait...");
        String version_checker = new URLs_v2().GET_VERSION_CHECKER();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, version_checker, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingScreen.dismiss();
                try {
                    JSONObject jo = new JSONObject(response);

                    if (jo.getString("status").equals("success")) {

                        if (!jo.getBoolean("isUptoDate")) {
                            updateApp(jo.getString("message"));
                        } else {
                            if (SharedPrefManager.getInstance(ctx).isLoggedIn()) {
                                redirectToMain();
                            }
                        }
                    } else {

                        String message = jo.has("message") && jo.get("message") instanceof String ? jo.getString("message") : "Oops! Something went wrong";

                        Toasty.error(ctx, message, Toasty.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toasty.error(ctx, e.toString(), Toasty.LENGTH_LONG).show();
                    showErrorUpdateCheck();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingScreen.dismiss();
                //Toasty.error(ctx, ctx.getResources().getString(R.string.api_request_error), Toasty.LENGTH_LONG).show();
                showErrorUpdateCheck();
            }
        });

        Helper.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void showErrorUpdateCheck() {
        appVersionDialog.setContentView(R.layout.dialog_version_check_failed);
        CardView cv_retry = appVersionDialog.findViewById(R.id.cv_retry);
        CardView cv_close_app = appVersionDialog.findViewById(R.id.cv_close_app);

        cv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkVersion();
                appVersionDialog.dismiss();
            }
        });

        cv_close_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        appVersionDialog.setCancelable(false);
        appVersionDialog.show();
    }

    private void updateApp(String downloadPath) {

        appVersionDialog.setContentView(R.layout.dialog_app_is_updating);
        appVersionDialog.setCancelable(false);
        appVersionDialog.show();


        //get destination to update file and set Uri
        //TODO: First I wanted to store my update .apk file on internal storage for my app but apparently android does not allow you to open and install
        //aplication with existing package from there. So for me, alternative solution is Download directory in external storage. If there is better
        //solution, please inform us in comment
        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        String fileName = "installer.apk";
        destination += fileName;
        final Uri uri = Uri.parse("file://" + destination);

        //Delete update file if exists
        File file = new File(destination);
        if (file.exists())
            //file.delete() - test this, I think sometimes it doesnt work
            file.delete();


        final DownloadManager downloadmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        //Uri url = Uri.parse("http://timekeeping.caimitoapps.com:999/packages/timekeeping.apk");
        Uri url = Uri.parse(Static.VERSION_ROOT_URL + downloadPath);

        DownloadManager.Request request = new DownloadManager.Request(url);
        request.setTitle("Timekeeping App");
        request.setDescription("Downloading");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);
        request.setDestinationUri(uri);

        final long downloadId = downloadmanager.enqueue(request);


        BroadcastReceiver onCompletedReceiver = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {

                appVersionDialog.dismiss();

                //Clear Sessions before installing
                SharedPrefManager.getInstance(getApplicationContext()).clearSharedPrefs();


                Uri path = uri;
                String mt = downloadmanager.getMimeTypeForDownloadedFile(downloadId);

                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                install.setDataAndType(uri,
                        downloadmanager.getMimeTypeForDownloadedFile(downloadId));
                startActivity(install);
                unregisterReceiver(this);
                finish();
            }
        };

        registerReceiver(onCompletedReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void redirectToMain() {

        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }


    private Integer WRITE_EXTERNAL_STORAGE = 69;


    private void checkRequiredPermissions() {
        if (ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        } else {
            checkVersion();
        }
    }
}
