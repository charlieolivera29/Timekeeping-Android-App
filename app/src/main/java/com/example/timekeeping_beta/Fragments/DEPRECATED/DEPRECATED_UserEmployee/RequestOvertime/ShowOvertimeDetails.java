package com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestOvertime;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.card.MaterialCardView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.timekeeping_beta.Fragments.Dialogs.DialogDeleteRequestFragment;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.Globals.Models.User;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ShowOvertimeDetails extends AppCompatActivity {

    private Context ctx;
    private Helper helper;
    private Integer overtime_id;

    private User user;
    private String DATABASE;
    private String TABLE;
    private RequestQueue request;

    //Main
    private LinearLayout overtime_details_view;
    private TextView title_bar_text;
    private TextView pending_overtime_date;
    private TextView pending_overtime_shift_in;
    private TextView pending_overtime_shift_out;
    private TextView pending_overtime_status;
    private TextView textViewReason;
    private MaterialCardView btnDelete;
    private TextView btnEdit;
    private FrameLayout edit_pending_overtime;
    private ImageButton btnClose;


    //Overtime update
    private TextView txt_overtime_date;
    private TextView txt_overtime_time_start;
    private TextView txt_overtime_time_end;
    private ImageButton btn_calendar_overtime_date;
    private ImageButton btn_overtime_start_time;
    private ImageButton btn_overtime_end_time;
    private EditText txt_overtime_reason;

    private DatePicker date;
    private TimePicker start_time;
    private TimePicker end_time;

    private Dialog DatePickerDialog;
    private Dialog TimePickerDialog;

    private String str_start_time;
    private String str_end_time;
    private String str_date;
    private Button btn_overtime_request;

    private String request_id;
    private String user_id;
    private String status;
    private String checked_by;
    private String checked_at;
    private String updated_at;
    private String created_at;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_overtime_request_details);

        init();
        initViews();
        setListeners();
        //title_bar_text.setText("Overtime Request Details");

        //Gets Overtime object passed from intent
        if (getIntent().hasExtra("overtime_request")) {
            String item_s = getIntent().getStringExtra("overtime_request");
            JSONObject item = null;

            try {
                item = new JSONObject(item_s);

                String date = item.has("date") ? item.getString("date") : "--- --, ----";
                String start_time = item.has("start_time") ? item.getString("start_time") : "--:--:-- --";
                String end_time = item.has("end_time") ? item.getString("end_time") : "--:--:-- --";

                String reason = item.has("reason") ? item.getString("reason") : "";
                String requested_at = item.has("requested_at") ? item.getString("requested_at") : "";
                String fname = item.has("fname") ? item.getString("fname") : "";
                String lname = item.has("lname") ? item.getString("lname") : "";

                overtime_id = item.has("overtime_id") ? item.getInt("overtime_id") : null;
                request_id = String.valueOf(overtime_id);

                status = item.has("status") ? item.getString("status") : "";
                checked_by = item.has("checked_by") ? item.getString("checked_by") : "";
                checked_at = item.has("checked_at") ? item.getString("checked_at") : "";
                user_id = item.has("user_id") ? item.getString("user_id") : "";
                updated_at = item.has("updated_at") ? item.getString("updated_at") : "";
                created_at = item.has("created_at") ? item.getString("created_at") : "";


                str_start_time = start_time;
                str_end_time = end_time;
                str_date = date;

                String readable_date = helper.convertToReadableDate(date);
                String readable_time_in = helper.convertToReadableTime(start_time);
                String readable_time_out = helper.convertToReadableTime(end_time);

                //Main
                pending_overtime_date.setText(readable_date);
                pending_overtime_shift_in.setText(readable_time_in);
                pending_overtime_shift_out.setText(readable_time_out);

                //Edit
                txt_overtime_date.setText(readable_date);
                txt_overtime_time_start.setText(readable_time_in);
                txt_overtime_time_end.setText(readable_time_out);
                txt_overtime_reason.setText(reason);

                if (status.equals("pending")) {
                    btnEdit.setVisibility(View.VISIBLE);
                    btnDelete.setVisibility(View.VISIBLE);
                }

                pending_overtime_status.setText(status.toUpperCase());
                textViewReason.setText(reason);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        getSupportActionBar().hide();
    }

    public void openConfirmDialog(Integer i_id) {

        Bundle args = new Bundle();
        args.putInt("id", i_id);
        args.putString("flag_request", "OT");

        DialogDeleteRequestFragment deleteDialogFragment = new DialogDeleteRequestFragment();
        deleteDialogFragment.setArguments(args);

        deleteDialogFragment.show(getSupportFragmentManager(), "Delete Confirm Dialog");
    }

    private void init() {
        ctx = this;
        helper = Helper.getInstance(ctx);
        DatePickerDialog = new Dialog(ctx);
        TimePickerDialog = new Dialog(ctx);

        request = Volley.newRequestQueue(ctx);
        user = SharedPrefManager.getInstance(ctx).getUser();
        DATABASE = user.getC1();
        TABLE = user.getC2();

    }

    private void initViews() {
        overtime_details_view = findViewById(R.id.overtime_details_view);
        title_bar_text = findViewById(R.id.title_bar_text);

        pending_overtime_date = findViewById(R.id.pending_overtime_date);
        pending_overtime_shift_in = findViewById(R.id.pending_overtime_shift_in);
        pending_overtime_shift_out = findViewById(R.id.pending_overtime_shift_out);
        pending_overtime_status = findViewById(R.id.pending_overtime_status);
        textViewReason = findViewById(R.id.pending_reason);
        btnDelete = findViewById(R.id.btn_delete_leave);
        btnEdit = findViewById(R.id.enable_edit_pending_overtime);
        edit_pending_overtime = findViewById(R.id.edit_pending_overtime);

        txt_overtime_date = findViewById(R.id.txt_overtime_date);
        txt_overtime_time_start = findViewById(R.id.txt_overtime_time_start);
        txt_overtime_time_end = findViewById(R.id.txt_overtime_time_end);
        btn_calendar_overtime_date = findViewById(R.id.btn_calendar_overtime_date);
        btn_overtime_start_time = findViewById(R.id.btn_overtime_start_time);
        btn_overtime_end_time = findViewById(R.id.btn_overtime_end_time);
        txt_overtime_reason = findViewById(R.id.txt_overtime_reason);
        btn_overtime_request = findViewById(R.id.btn_overtime_request);
        btnClose = findViewById(R.id.img_fullscreen_dialog_close);
    }

    private void setListeners() {

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edit_pending_overtime.getVisibility() == View.GONE) {
                    edit_pending_overtime.setVisibility(View.VISIBLE);
                } else {
                    edit_pending_overtime.setVisibility(View.GONE);
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openConfirmDialog(overtime_id);
            }
        });

//        btn_calendar_overtime_date.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DatePickerDialog.setContentView(R.layout.dialog_date_picker);
//                date = DatePickerDialog.findViewById(R.id.date_picker);
//                DatePickerDialog.setTitle("Set Date");
//                Button button_set_date = DatePickerDialog.findViewById(R.id.button_set_date);
//                Button button_cancel_date_picker = DatePickerDialog.findViewById(R.id.button_cancel_date_picker);
//
//                button_set_date.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        Integer nonZeroIndexedMonth = date.getMonth() + 1;
//
//                        String m = nonZeroIndexedMonth < 10 ? "0" + nonZeroIndexedMonth : String.valueOf(nonZeroIndexedMonth);
//                        String d = date.getDayOfMonth() < 10 ? "0" + date.getDayOfMonth() : String.valueOf(date.getDayOfMonth());
//                        String y = date.getYear() < 10 ? "0" + date.getYear() : String.valueOf(date.getYear());
//
//                        String string_date = y + "-" + m + "-" + d;
//                        String human_readable_date = helper.convertToReadableDate(string_date);
//
//                        str_date = string_date;
//
//                        txt_overtime_date.setText(human_readable_date);
//                        DatePickerDialog.dismiss();
//                    }
//                });
//
//                button_cancel_date_picker.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        DatePickerDialog.dismiss();
//                    }
//                });
//
//                DatePickerDialog.show();
//            }
//        });

        btn_overtime_start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.setContentView(R.layout.dialog_time_picker);
                TimePickerDialog.setTitle("Set Time in");
                start_time = TimePickerDialog.findViewById(R.id.time_picker);
                start_time.is24HourView();
                Button button_set_date = TimePickerDialog.findViewById(R.id.button_set_date);
                Button button_cancel_date_picker = TimePickerDialog.findViewById(R.id.button_cancel_date_picker);

                button_set_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String h = start_time.getCurrentHour() < 10 ? "0" + start_time.getCurrentHour().toString() : start_time.getCurrentHour().toString();
                        String t = start_time.getCurrentMinute() < 10 ? "0" + start_time.getCurrentMinute().toString() : start_time.getCurrentMinute().toString();
                        String string_start_time = h + ":" + t;

                        str_start_time = string_start_time;

                        int int_hr_hour = start_time.getCurrentHour() > 12 ? start_time.getCurrentHour() - 12 : start_time.getCurrentHour();
                        String hr_hour = int_hr_hour < 10 ? "0" + int_hr_hour : String.valueOf(int_hr_hour);
                        String AM_PM = start_time.getCurrentHour() > 12 ? "PM" : "AM";

                        String human_readable_start_time = hr_hour + ":" + t + " " + AM_PM;

                        txt_overtime_time_start.setText(human_readable_start_time);

                        TimePickerDialog.dismiss();
                    }
                });

                button_cancel_date_picker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        TimePickerDialog.dismiss();
                    }
                });

                TimePickerDialog.show();
            }
        });

        btn_overtime_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.setContentView(R.layout.dialog_time_picker);
                TimePickerDialog.setTitle("Set Time in");
                end_time = TimePickerDialog.findViewById(R.id.time_picker);
                end_time.is24HourView();
                Button button_set_date = TimePickerDialog.findViewById(R.id.button_set_date);
                Button button_cancel_date_picker = TimePickerDialog.findViewById(R.id.button_cancel_date_picker);

                button_set_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String h = end_time.getCurrentHour() < 10 ? "0" + end_time.getCurrentHour().toString() : end_time.getCurrentHour().toString();
                        String t = end_time.getCurrentMinute() < 10 ? "0" + end_time.getCurrentMinute().toString() : end_time.getCurrentMinute().toString();
                        String string_end_time = h + ":" + t;

                        str_end_time = string_end_time;

                        int int_hr_hour = end_time.getCurrentHour() > 12 ? end_time.getCurrentHour() - 12 : end_time.getCurrentHour();
                        String hr_hour = int_hr_hour < 10 ? "0" + int_hr_hour : String.valueOf(int_hr_hour);
                        String AM_PM = end_time.getCurrentHour() > 12 ? "PM" : "AM";
                        String human_readable_end_time = hr_hour + ":" + t + " " + AM_PM;

                        txt_overtime_time_end.setText(human_readable_end_time);
                        TimePickerDialog.dismiss();
                    }
                });

                button_cancel_date_picker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        TimePickerDialog.dismiss();
                    }
                });

                TimePickerDialog.show();
            }
        });

        btn_overtime_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                paramsChecker(str_date, str_start_time, str_end_time, txt_overtime_reason.getText().toString());
            }
        });

    }

    private boolean paramsChecker(final String i_date, final String i_start_time, final String i_end_time, final String i_reason) {

        if (i_date.length() > 0 && i_start_time.length() > 0 && i_end_time.length() > 0 && i_reason.length() > 0) {

            updateOvertimeRequest(i_date, i_start_time, i_end_time, i_reason);
            return true;
        } else {
            Toasty.error(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void updateOvertimeRequest(
            final String i_date,
            final String i_start_time,
            final String i_end_time,
            final String i_reason
    ) {
        final URLs url = new URLs();

        String url_update_overtime_request = url.url_update_overtime_request(request_id);

        StringRequest requestUpdateAdjustment = new StringRequest(Request.Method.PUT, url_update_overtime_request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("update_status", response);
                Toasty.success(ctx, "Update Success!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(ctx, "Error! Please try again", Toast.LENGTH_SHORT).show();
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
                params.put("user_id", user_id);
                params.put("status", status);
                //params.put("updated_at", updated_at);
                params.put("updated_at", i_date);
                params.put("start_time", i_start_time);
                params.put("end_time", i_end_time);
                params.put("reason", i_reason);
                params.put("id", request_id);
                params.put("decline_reason", "");
                params.put("date", i_date);
                params.put("created_at", created_at);
                params.put("checked_by", checked_by);
                params.put("checked_at", checked_at);
                params.put("api_token", user.getApi_token());
                params.put("link", user.getLink());


                return params;
            }
        };

        request.add(requestUpdateAdjustment);
    }


}
