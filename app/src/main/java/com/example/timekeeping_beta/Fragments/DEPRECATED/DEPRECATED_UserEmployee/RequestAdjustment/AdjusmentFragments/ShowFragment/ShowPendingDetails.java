package com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestAdjustment.AdjusmentFragments.ShowFragment;


import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.card.MaterialCardView;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.R;
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestAdjustment.AdjusmentFragments.DialogFragment.DeleteDialogFragment;
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestAdjustment.AdjusmentFragments.DialogFragment.TimeInAdjustmentPickerFragment;
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestAdjustment.AdjusmentFragments.DialogFragment.TimeOutAdjustmentPickerFragment;
import com.example.timekeeping_beta.Fragments.UserEmployee.Adjustments.Models.TimeAdjustment;
import com.example.timekeeping_beta.Fragments.UserEmployee.Adjustments.Models.AdjustmentRequestItem;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.Globals.Models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class ShowPendingDetails extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private final URLs url = new URLs();
    private final User user = SharedPrefManager.getInstance(this).getUser();
    private final TimeAdjustment timeAdjustment = new TimeAdjustment();

    private RequestQueue request;
    private LinearLayout adjustmentLayout;
    private LinearLayout adjusetmentReasonLayout;
    private MaterialCardView btnUpdate;
    public Spinner spinnerDayType;

    private TextView textViewReason;
    private TextView textViewTimeIn;
    private TextView textViewTimeOut;
    private TextView textOriginTimeIn;
    private TextView textOriginTimeOut;
    private TextView textOriginDayType;

    private String id;
    private String date;
    private String time_in;
    private String time_out;
    private String shift_in;
    private String shift_out;
    private String day_type;
    private String reference;

    private ImageButton btnTimeOut;
    private ImageButton btnTimeIn;

    private String blank_date = "", blank_time = "";

    private Helper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_pending_request_details);

        helper = Helper.getInstance(this);

        TextView textViewDate = findViewById(R.id.tad_date);
        TextView textViewGracePeriod = findViewById(R.id.tad_grace_period);
        TextView textViewShiftIn = findViewById(R.id.tad_shift_in);
        TextView textViewShiftOut = findViewById(R.id.tad_shift_out);
        TextView textViewReference = findViewById(R.id.tad_reference);
        textViewReason = findViewById(R.id.tad_reason);
        textViewTimeIn = findViewById(R.id.txt_adjusted_time_in);
        textViewTimeOut = findViewById(R.id.txt_adjusted_time_out);

        if (getIntent().hasExtra("adjustment_pending")) {
            AdjustmentRequestItem item = getIntent().getParcelableExtra("adjustment_pending");
            String adjustment_id = item.getId();
            textViewDate.setText(item.getDate());
            textViewGracePeriod.setText(item.getGrace_period());
            textViewShiftIn.setText(item.getShift_in());
            textViewShiftOut.setText(item.getShift_out());
            textViewReference.setText(item.getReference());
            textViewReason.setText(item.getReason());
            textViewTimeIn.setText(helper.convertToReadableTime(item.getRequested_time_in()));
            textViewTimeOut.setText(helper.convertToReadableTime(item.getRequested_time_out()));


            SharedPreferences.Editor editor = getSharedPreferences("Adjustment_Data", MODE_PRIVATE).edit();
            editor.putString("id", adjustment_id);
            editor.apply();
        }

        // hide titlebar
        getSupportActionBar().hide();

        MaterialCardView btnDelete = findViewById(R.id.button_delete);
        btnDelete.setVisibility(View.VISIBLE);

        TextView btnEdit = findViewById(R.id.enable_edit_pending);
        btnEdit.setVisibility(View.VISIBLE);

        adjustmentLayout = findViewById(R.id.adjusted_time);
        adjusetmentReasonLayout = findViewById(R.id.adjustment_reason);
        btnUpdate = findViewById(R.id.btn_update_time_adjustment);

        // Show adjustment
        showAdjustment();

        // Edit Button
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(adjustmentLayout);
                }

                //if (adjustmentLayout.getVisibility() == View.GONE) {
                //adjustmentLayout.setVisibility(View.VISIBLE);
                //} else {
                //adjustmentLayout.setVisibility(View.GONE);
                //}

                if (textViewReason.isEnabled() == false) {
                    btnTimeOut.setVisibility(View.VISIBLE);
                    btnTimeIn.setVisibility(View.VISIBLE);
                    btnUpdate.setVisibility(View.VISIBLE);
                    spinnerDayType.setEnabled(TRUE);
                    textViewReason.setEnabled(TRUE);
                } else {

                    btnTimeOut.setVisibility(View.INVISIBLE);
                    btnTimeIn.setVisibility(View.INVISIBLE);

                    btnUpdate.setVisibility(View.GONE);
                    spinnerDayType.setEnabled(FALSE);
                    textViewReason.setEnabled(FALSE);
                }
            }
        });

        // Update Button
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateRequest();
            }
        });

        // Delete Button
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openConfirmDialog();
            }
        });

        // Close Button
        ImageButton btnClose = findViewById(R.id.img_fullscreen_dialog_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        // Time in button
        btnTimeIn = findViewById(R.id.adjusted_time_in_edit);
        btnTimeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimeInAdjustmentPickerFragment();
                timePicker.show(getSupportFragmentManager(), null);
            }
        });

        // Time in button
        btnTimeOut = findViewById(R.id.adjusted_time_out_edit);
        btnTimeOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimeOutAdjustmentPickerFragment();
                timePicker.show(getSupportFragmentManager(), null);
            }
        });

        // Spinner for day type
        spinnerDayType = findViewById(R.id.adjustment_spinner_day_type);

        spinnerDayType.setEnabled(FALSE);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.day_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDayType.setAdapter(adapter);
        spinnerDayType.setOnItemSelectedListener(this);
        blank_date = this.getResources().getString(R.string.blank_date);
        blank_time = this.getResources().getString(R.string.blank_time);
    }

    // api request show details
    public void showAdjustment() {
        String url_show_adjustment = url.url_show_adjustment(user.getApi_token(), user.getLink());
        request = Volley.newRequestQueue(this.getApplicationContext());

        StringRequest requestAdjustmentShow = new StringRequest(Request.Method.POST, url_show_adjustment, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response", response);
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject msg_obj = obj.getJSONObject("msg");

                    if (msg_obj.has("reference") && !msg_obj.getString("reference").equals("null")) {
                        id = msg_obj.getString("id");
                        date = msg_obj.getString("date_in");
                        shift_in = msg_obj.getString("original_time_in");
                        shift_out = msg_obj.getString("shift_out");
                        time_in = msg_obj.getString("time_in");
                        time_out = msg_obj.getString("time_out");
                        day_type = msg_obj.getString("day_type");
                        reference = msg_obj.getString("reference");
                    } else {
                        id = "";
                        date = "";
                        shift_in = "";
                        shift_out = "";
                        time_in = "";
                        time_out = "";
                        day_type = "";
                        reference = getResources().getString(R.string.api_reference);
                    }

                    Log.d("id", id);

                    timeAdjustment.setDate(date);
                    timeAdjustment.setShift_in(shift_in);
                    timeAdjustment.setShift_out(shift_out);
                    timeAdjustment.setTime_in(time_in);
                    timeAdjustment.setTime_out(time_out);
                    timeAdjustment.setDay_type(day_type);
                    timeAdjustment.setReference(reference);

                    textOriginTimeIn = findViewById(R.id.tad_time_in);
                    textOriginTimeOut = findViewById(R.id.tad_time_out);
                    textOriginDayType = findViewById(R.id.tad_day_type);

                    String convert_time_in = (time_in).equals("null") || time_in.length() < 1 ? "--:-- --" : timeConvert(time_in);
                    String convert_time_out = (time_out.equals("null")) || time_out.length() < 1 ? "--:-- --" : timeConvert(time_out);

                    textOriginTimeIn.setText(convert_time_in);
                    textOriginTimeOut.setText(convert_time_out);
                    textOriginDayType.setText(day_type);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                return helper.headers();
            }

            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                AdjustmentRequestItem item = getIntent().getParcelableExtra("adjustment_pending");
                params.put("user_id", user.getUser_id());
                params.put("date_in", dateConvert(item.getDate()));
                return params;
            }
        };

        request.add(requestAdjustmentShow);
    }

    public void validateRequest() {

        String timeInUpdate = reConvertTime(textViewTimeIn.getText().toString());
        String timeOutUpdate = reConvertTime(textViewTimeOut.getText().toString());
        String reasonUpdate = textViewReason.getText().toString();

        if (reasonUpdate.length() == 0) {
            Toasty.error(getBaseContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else {
            if (!spinnerDayType.getSelectedItem().toString().equals("ABSENT")) {
                if (timeInUpdate.length() == 0 || timeOutUpdate.length() == 0) {
                    Toasty.error(getBaseContext(), "Time in and time out is required.", Toast.LENGTH_SHORT).show();
                } else {
                    updateAdjustmentRequest();
                }
            } else {
                updateAdjustmentRequest();
            }
        }
    }


    // update function
    public void updateAdjustmentRequest() {
        AdjustmentRequestItem item = getIntent().getParcelableExtra("adjustment_pending");
        String url_update_adjustment = url.url_update_adjustment(item.getId(), user.getApi_token(), user.getLink());

        final String timeIn = textViewTimeIn.getText().toString();
        final String timeOut = textViewTimeOut.getText().toString();
        final String reason = textViewReason.getText().toString();
        final String user_id = user.getUser_id();
        final String day_type = spinnerDayType.getSelectedItem().toString();

        StringRequest requestUpdateAdjustment = new StringRequest(Request.Method.POST, url_update_adjustment, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("update_status", response);
                Toasty.success(getApplicationContext(), "Update Success!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(getApplicationContext(), "Update Failed", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return helper.headers();
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("session_id", user_id);
                params.put("day_type", day_type);
                params.put("reason", reason);
                params.put("time_in", timeIn);
                params.put("time_out", timeOut);
                return params;
            }
        };

        requestUpdateAdjustment.setRetryPolicy(new DefaultRetryPolicy(60 * 1000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        request.add(requestUpdateAdjustment);
    }

    // Dialog
    public void openConfirmDialog() {
        DeleteDialogFragment deleteDialogFragment = new DeleteDialogFragment();
        deleteDialogFragment.show(getSupportFragmentManager(), "Delete Confirm Dialog");
    }

    // date convert
    public String dateConvert(String date) {

        String string_date = date;
        String converted_date = "";
        SimpleDateFormat raw_date = new SimpleDateFormat("MMM dd, yyyy EEE");
        SimpleDateFormat date_day = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date raw_dateDt = raw_date.parse(string_date);
            converted_date = date_day.format(raw_dateDt);
            Log.d("@date_with_day", converted_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return converted_date;
    }

    // time convert
    public String timeConvert(String time) {
        String string_time = time;
        String converted_time = "";
        SimpleDateFormat _24Hour = new SimpleDateFormat("HH:mm");
        SimpleDateFormat _12Hour = new SimpleDateFormat("hh:mm a");

        if (!time.isEmpty()) {
            try {
                Date _24HourDt = _24Hour.parse(string_time);
                converted_time = _12Hour.format(_24HourDt);
                Log.d("@12Hr_format", converted_time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return converted_time;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String dayType = adapterView.getItemAtPosition(i).toString();

        if (dayType.equals("ABSENT")) {

            textViewTimeIn.setText(blank_time);
            textViewTimeOut.setText(blank_time);

            btnTimeIn.setEnabled(false);
            btnTimeOut.setEnabled(false);
        } else {

            if (!btnTimeIn.isEnabled()) {
                btnTimeIn.setEnabled(true);
            }
            if (!btnTimeOut.isEnabled()) {
                btnTimeOut.setEnabled(true);
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public String reConvertTime(String time) {
        String string_time = time;
        String converted_time = "";
        SimpleDateFormat _24Hour = new SimpleDateFormat("HH:mm");
        SimpleDateFormat _12Hour = new SimpleDateFormat("hh:mm a");

        if (!time.isEmpty()) {
            try {
                Date _12HourDt = _12Hour.parse(string_time);
                converted_time = _24Hour.format(_12HourDt);
                Log.d("@24Hr_format", converted_time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return converted_time;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
