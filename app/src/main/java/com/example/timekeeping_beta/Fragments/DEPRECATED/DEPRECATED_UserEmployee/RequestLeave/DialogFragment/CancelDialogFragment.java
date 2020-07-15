package com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestLeave.DialogFragment;

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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.timekeeping_beta.R;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.Globals.Models.User;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class CancelDialogFragment extends DialogFragment {

    private final User user = SharedPrefManager.getInstance(getActivity()).getUser();
    private final URLs url = new URLs();

    private final String DATABASE = user.getC1();
    private final String TABLE = user.getC2();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_confirmation, null);

        TextView body = view.findViewById(R.id.text_confirm);
        body.setText("Are you sure you want to \nCancel this Request?");

        builder.setView(view).setTitle("Cancel?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toasty.success(getContext(), "Successful in cancelling Leave", Toast.LENGTH_SHORT).show();
                        cancelLeaveRequest();
                        getActivity().onBackPressed();
                    }
                });

        return builder.create();
    }

    public void cancelLeaveRequest() {

        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        SharedPreferences pref = getActivity().getSharedPreferences("Leave_Data", Context.MODE_PRIVATE);
        final String request_id = pref.getString("request_id","request id is empty");
        String url_cancel_leave = url.url_cancel_leave(user.getApi_token(), user.getLink());

        StringRequest requestCancel = new StringRequest(Request.Method.POST, url_cancel_leave, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("cancel_status", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
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
                params.put("request_id", request_id);
                return params;
            }
        };

        queue.add(requestCancel);
    }
}
