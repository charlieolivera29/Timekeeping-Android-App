package com.example.timekeeping_beta.Fragments.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.timekeeping_beta.R;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.Globals.Models.User;
import com.example.timekeeping_beta.Globals.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class DialogDeleteRequestFragment extends DialogFragment {

    private Context ctx;
    private User user;
    private final URLs url = new URLs();
    private RequestQueue queue;

    private String flag_request;
    private Integer request_id;
    private String user_id;

    private Map<String, String> headers;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        ctx = getActivity().getApplicationContext();
        user = SharedPrefManager.getInstance(ctx).getUser();

        headers = Helper.getInstance(ctx).headers();

        try {
            request_id = getArguments().getInt("id");
            flag_request = getArguments().getString("flag_request");
            user_id = user.getUser_id();
        } catch (Exception e) {
            System.out.print("e");
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_confirmation, null);

        builder.setView(view).setTitle("Delete?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (flag_request.equals("LV")) {
                            deleteLeaveRequest(request_id.toString(), user_id);
                            getActivity().onBackPressed();
                        } else if (flag_request.equals("OT")) {
                            deleteOvertimeRequest(request_id.toString(), user_id);
                            getActivity().onBackPressed();
                        } else {
                            Toasty.error(ctx, "Flag Error!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return builder.create();
    }

    public void deleteLeaveRequest(final String i_id, final String i_user_id) {
        final String DATABASE = user.getC1();
        final String TABLE = user.getC2();

        String url_delete_leave_params = url.url_delete_leave(user.getApi_token(), user.getLink());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_delete_leave_params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);

                    if (response_obj.getString("status").equals("success")) {
                        Toasty.success(ctx, response_obj.getString("msg"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toasty.error(ctx, response_obj.getString("msg"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toasty.error(ctx, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(ctx, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", i_user_id);
                params.put("request_id", i_id);
                params.put("link", user.getLink());
                params.put("api_token", user.getApi_token());

                return params;
            }
        };
        Helper.getInstance(ctx).addToRequestQueue(stringRequest);
    }

    //Dev Karl
    public void deleteOvertimeRequest(final String i_id, final String i_user_id) {

        final String DATABASE = user.getC1();
        final String TABLE = user.getC2();

        String url_delete_overtime = url.url_delete_overtime();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_delete_overtime, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);

                    if (response_obj.getString("status").equals("success")) {
                        Toasty.success(ctx, response_obj.getString("msg"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toasty.error(ctx, response_obj.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toasty.error(ctx, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(ctx, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", i_user_id);
                params.put("id", i_id);
                params.put("link", user.getLink());
                params.put("api_token", user.getApi_token());

                return params;
            }
        };
        Helper.getInstance(ctx).addToRequestQueue(stringRequest);
    }
}
