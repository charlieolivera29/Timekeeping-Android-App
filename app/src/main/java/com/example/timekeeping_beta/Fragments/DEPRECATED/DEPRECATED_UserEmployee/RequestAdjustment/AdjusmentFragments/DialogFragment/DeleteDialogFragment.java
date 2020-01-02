package com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestAdjustment.AdjusmentFragments.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.R;
import com.example.timekeeping_beta.Fragments.UserEmployee.Adjustments.Models.TimeAdjustment;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.Globals.Models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class DeleteDialogFragment extends DialogFragment {

    private final User user = SharedPrefManager.getInstance(getActivity()).getUser();
    private final TimeAdjustment timeAdjustment = new TimeAdjustment();
    private final URLs url = new URLs();
    private Helper helper;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_confirmation, null);

        helper = Helper.getInstance(getActivity());

        builder.setView(view).setTitle("Delete?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toasty.success(getContext(), "Successful in deleting adjustment", Toast.LENGTH_SHORT).show();
                        deleteAdjustmentRequest();
                        getActivity().onBackPressed();
                    }
                });

        return builder.create();
    }

    public void deleteAdjustmentRequest() {

        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        SharedPreferences pref = getActivity().getSharedPreferences("Adjustment_Data", Context.MODE_PRIVATE);
        String id = pref.getString("id", "id is empty");
        Log.d("id", id);
        String url_delete_adjustment = url.url_delete_adjustment(id, user.getApi_token(), user.getLink());
        Log.d("url_delete", url_delete_adjustment);

        StringRequest requestDelete = new StringRequest(Request.Method.POST, url_delete_adjustment, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response", response);
                try {
                    JSONObject obj = new JSONObject(response);
                    Log.d("status", obj.getString("status"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.getMessage() != null){
                    Toasty.error(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return helper.headers();
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("session_id", user.getUser_id());
                return params;
            }
        };

        queue.add(requestDelete);
    }
}
