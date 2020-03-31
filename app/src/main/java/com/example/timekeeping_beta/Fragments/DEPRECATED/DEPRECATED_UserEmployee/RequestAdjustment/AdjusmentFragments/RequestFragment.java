package com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestAdjustment.AdjusmentFragments;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.R;
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestAdjustment.AdjusmentFragments.DialogFragment.DatePickerFragment;
import com.example.timekeeping_beta.Fragments.UserEmployee.Adjustments.Models.TimeAdjustment;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.Globals.Models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;


public class RequestFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private Context ctx;
    public View v;

    private final User user = SharedPrefManager.getInstance(getActivity()).getUser();
    private final TimeAdjustment timeAdjustment = new TimeAdjustment();
    private final URLs url = new URLs();
    public Spinner spinnerDayType;

    private String converted_date = "";
    private String date_from_timesheet = "";
    private RequestQueue queue;

    private TextView txtShiftIn;
    private TextView txtShiftOut;
    private TextView txtTimeIn;
    private TextView txtTimeOut;
    private TextView txtReference;
    private TextView txtDayType;

    private LinearLayout timesheetInfoLayout;

    private String shift_in,
            shift_out,
            reference,
            time_in,
            time_out,
            day_type,
    // Edit Schedule Update
    // March 31 ,2020
    //Added params
    old_time_in,
            old_time_out,
            old_day_type;
    private Boolean edit_sched = true,isBroken = true;

    private DatePickerFragment datePicker;
    private LinearLayout timesheetnavigattion;

    public TextView txtCalendar, txt_date_from_timesheet;

    TextView txtAdjustedTimeIn, txtAdjustedTimeOut;

    //private String specific_date = "";
    private String readable_specific_date = "";
    private Button btnSendRequest;

    private Boolean isFromTimesheets = false;

    private ImageButton btnTimeIn, btnTimeOut, btnCalendar;
    private String blank_date = "", blank_time = "";
    private EditText txtAdjustedReason;

    private Map<String, String> Headers;

    private Integer shift = 0;
    private TableRow tr_shift_container;
    private Spinner spinner_shift;


    @Override
    public void onResume() {
        super.onResume();
        if (timesheetnavigattion != null) {
            timesheetnavigattion.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (timesheetnavigattion != null) {
            timesheetnavigattion.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (timesheetnavigattion != null) {
            timesheetnavigattion.setVisibility(View.GONE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_request, container, false);
        ctx = v.getContext();

        Headers = Helper.getInstance(ctx).headers();
        datePicker = new DatePickerFragment();
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        blank_date = getActivity().getResources().getString(R.string.blank_date);
        blank_time = getActivity().getResources().getString(R.string.blank_time);

        initViews();
        setListeners();

        Bundle bundle = getArguments();

        if (bundle != null) {

            isFromTimesheets = true;

            try {

                date_from_timesheet = bundle.containsKey("specific_date") ? bundle.getString("specific_date") : "";
                readable_specific_date = bundle.containsKey("readable_specific_date") ? bundle.getString("readable_specific_date") : "";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!readable_specific_date.isEmpty() && !date_from_timesheet.isEmpty()) {
            txt_date_from_timesheet.setVisibility(View.VISIBLE);
            txt_date_from_timesheet.setText(readable_specific_date);
            timekeepingUpdate();
        }


        return v;
    }

    private void initViews() {

        timesheetnavigattion = getActivity().findViewById(R.id.timesheetnavigattion);

        txtShiftIn = v.findViewById(R.id.tv_shift_in_request_adjust);
        txtShiftOut = v.findViewById(R.id.tv_shift_out_request_adjust);
        txtTimeIn = v.findViewById(R.id.tv_time_in_request_adjust_original);
        txtTimeOut = v.findViewById(R.id.tv_time_out_request_adjust_original);
        txtReference = v.findViewById(R.id.tv_reference_request_adjust);
        txtDayType = v.findViewById(R.id.tv_day_type_request_adjust);
        txtAdjustedReason = v.findViewById(R.id.edtxt_adjustment_reason);

        txtCalendar = v.findViewById(R.id.txt_date_calendar);
        txtCalendar.addTextChangedListener(txtCalendarWatcher);
        timesheetInfoLayout = v.findViewById(R.id.layout_date_info);
        spinnerDayType = v.findViewById(R.id.spinner_day_type);
        txt_date_from_timesheet = v.findViewById(R.id.txt_date_from_timesheet);

        btnCalendar = v.findViewById(R.id.btn_adjustment_request_calendar);
        btnTimeIn = v.findViewById(R.id.btn_time_in);
        btnTimeOut = v.findViewById(R.id.btn_time_out);


        txtAdjustedTimeIn = v.findViewById(R.id.txt_time_in);
        txtAdjustedTimeOut = v.findViewById(R.id.txt_time_out);
        btnSendRequest = v.findViewById(R.id.btn_send_adjustment_request);


        tr_shift_container = v.findViewById(R.id.tr_shift_container);
        spinner_shift = v.findViewById(R.id.spinner_shift);
    }

    private void setListeners() {
        // Calendar button
        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Calendar", "Calendar Clicked!");
                Log.d("DatePicker", datePicker.toString());
                datePicker.show(getChildFragmentManager(), null);
            }
        });

        // Spinner for day type
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.day_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDayType.setAdapter(adapter);
        spinnerDayType.setOnItemSelectedListener(this);

        // Time in button
        btnTimeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DialogFragment timePicker = new TimeInPickerFragment();
                //timePicker.show(getChildFragmentManager(), null);

                Calendar mcurrentTime = Calendar.getInstance(Locale.getDefault());
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ctx, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        String sh = selectedHour < 10 ? "0" + selectedHour : String.valueOf(selectedHour);
                        String st = selectedMinute < 10 ? "0" + selectedMinute : String.valueOf(selectedMinute);

                        String string_start_time = sh + ":" + st;

                        txtAdjustedTimeIn.setText(Helper.getInstance(ctx).convertToReadableTime(string_start_time));
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("");
                mTimePicker.show();
            }
        });

        // Time out button
        btnTimeOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DialogFragment timePicker = new TimeOutPickerFragment();
                //timePicker.show(getChildFragmentManager(), null);

                Calendar mcurrentTime = Calendar.getInstance(Locale.getDefault());
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ctx, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        String sh = selectedHour < 10 ? "0" + selectedHour : String.valueOf(selectedHour);
                        String st = selectedMinute < 10 ? "0" + selectedMinute : String.valueOf(selectedMinute);

                        String string_start_time = sh + ":" + st;

                        txtAdjustedTimeOut.setText(Helper.getInstance(ctx).convertToReadableTime(string_start_time));
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("");
                mTimePicker.show();
            }
        });

        // Send Request Adjustment
        btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateRequest();
            }
        });

        //Show form when timesheet found
        //Has animation if version KITKAT and above
    }

    // spinner method
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String dayType = adapterView.getItemAtPosition(i).toString();

        if (dayType.equals("ABSENT")) {

            txtAdjustedTimeIn.setText(blank_time);
            txtAdjustedTimeOut.setText(blank_time);

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

    // On Calendar text change
    private TextWatcher txtCalendarWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            txt_date_from_timesheet.setVisibility(View.GONE);
            txtCalendar.setVisibility(View.VISIBLE);

            converted_date = datePicker.getDate();
            timekeepingUpdate();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    // convert string time format
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

    // Gets adjustment from date
    public void timekeepingUpdate() {
        String url_show_timesheet = url.url_show_timesheet(user.getApi_token(), user.getLink());
        final String USER_ID = user.getUser_id();

        queue = Volley.newRequestQueue(ctx);
        final String date_in = !converted_date.isEmpty() ? converted_date : date_from_timesheet;

        StringRequest requestTimesheet = new StringRequest(Request.Method.POST, url_show_timesheet, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("ShowTimesheet", response);

                try {
                    JSONObject obj = new JSONObject(response);
                    JSONObject msg = obj.getJSONObject("msg");

                    JSONObject edtr_obj = msg.getJSONObject("edtr");
                    JSONObject schedule_object = msg.getJSONObject("schedule");

                    if (msg.has("isbroken") && msg.getBoolean("isbroken")) {

                        isBroken = msg.getBoolean("isbroken");
                        tr_shift_container.setVisibility(View.VISIBLE);

                        ArrayList<String> shiftOptions = new ArrayList<>();
                        shiftOptions.add("1");
                        shiftOptions.add("2");

                        ArrayAdapter adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, shiftOptions);
                        spinner_shift.setAdapter(adapter);
                        //Sets default value
                        //spnnr_user_types.setSelection(adapter.getPosition(myItem))
                        spinner_shift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                shift = Integer.valueOf(adapterView.getSelectedItem().toString());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                    }


                    // Edit Schedule Update
                    // March 31 ,2020
                    //                    if (edtr_obj.has("reference") && !edtr_obj.getString("reference").equals("null")) {
//                        reference = edtr_obj.getString("reference");
//                        shift_in = edtr_obj.getString("shift_in");
//                        shift_out = edtr_obj.getString("shift_out");
//                        time_in = edtr_obj.getString("time_in");
//                        time_out = edtr_obj.getString("time_out");
//                        day_type = edtr_obj.getString("day_type");
//
//                    } else {
//                        reference = getResources().getString(R.string.api_reference);
//                        shift_in = "";
//                        shift_out = "";
//                        time_in = "";
//                        time_out = "";
//                        day_type = "";
//                    }

                    reference = edtr_obj.has("reference") && !edtr_obj.getString("reference").equals("null") ?
                            edtr_obj.getString("reference") :
                            getResources().getString(R.string.api_reference);
                    shift_in = schedule_object.getString("shift_in");
                    shift_out = schedule_object.getString("shift_out");
                    time_in = edtr_obj.getString("time_in");
                    time_out = edtr_obj.getString("time_out");
                    day_type = edtr_obj.getString("day_type");

                    // Edit Schedule Update
                    // March 31 ,2020
                    old_time_in = edtr_obj.has("old_time_in") ? edtr_obj.getString("old_time_in") : "";
                    old_time_out = edtr_obj.has("old_time_out") ? edtr_obj.getString("old_time_out") : "";
                    old_day_type = edtr_obj.has("old_day_type") ? edtr_obj.getString("old_day_type") : "";
                    edit_sched = edtr_obj.has("edit_sched") ? edtr_obj.getBoolean("edit_sched") : false;


                    timeAdjustment.setShift_in(shift_in);
                    timeAdjustment.setShift_out(shift_out);
                    timeAdjustment.setTime_in(time_in);
                    timeAdjustment.setTime_out(time_out);
                    timeAdjustment.setDay_type(day_type);
                    timeAdjustment.setReference(reference);


                    txtReference.setText(timeAdjustment.getReference());
                    txtShiftIn.setText((timeAdjustment.getShift_in().isEmpty()) ? "--:-- --" : timeConvert(timeAdjustment.getShift_in()));
                    txtShiftOut.setText((timeAdjustment.getShift_out().isEmpty()) ? "--:-- --" : timeConvert(timeAdjustment.getShift_out()));
                    txtTimeIn.setText((timeAdjustment.getTime_in().isEmpty()) ? "--:-- --" : timeConvert(timeAdjustment.getTime_in()));
                    txtTimeOut.setText((timeAdjustment.getTime_out().isEmpty()) ? "--:-- --" : timeConvert(timeAdjustment.getTime_out()));
                    txtDayType.setText((timeAdjustment.getDay_type().isEmpty()) ? "- - -" : timeAdjustment.getDay_type());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        TransitionManager.beginDelayedTransition(timesheetInfoLayout);
                        if (timesheetInfoLayout.getVisibility() == View.GONE) {
                            timesheetInfoLayout.setVisibility(View.VISIBLE);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("ShowTimesheet", e.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.getMessage() != null) {
                    Toasty.error(ctx, error.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toasty.error(ctx, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                return Headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", USER_ID);
                params.put("date_in", date_in);
                params.put("shift", "0");

                return params;
            }
        };

        queue.add(requestTimesheet);
    }

    public void validateRequest() {

        final String timeIn = reConvertTime(txtAdjustedTimeIn.getText().toString());
        final String timeOut = reConvertTime(txtAdjustedTimeOut.getText().toString());
        final String reason = txtAdjustedReason.getText().toString();
        final String day_type = spinnerDayType.getSelectedItem().toString();
        final String date_in = !converted_date.isEmpty() ? converted_date : date_from_timesheet;

        if (reason.length() == 0 || day_type.length() == 0 || date_in.length() == 0) {
            Toasty.error(ctx, "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else {
            if (!day_type.equals("ABSENT")) {

                if (timeIn.length() == 0 || timeOut.length() == 0) {
                    Toasty.error(ctx, "Time in and time out is required.", Toast.LENGTH_SHORT).show();
                } else {
                    sendRequest(reason, date_in, timeIn, timeOut, day_type);
                }
            } else {
                sendRequest(reason, date_in, timeIn, timeOut, day_type);
            }
        }
    }

    private void sendRequest(final String reason, final String date_in, final String timeIn, final String timeOut, final String i_day_type) {

        final ProgressDialog loadingScreenDialog = ProgressDialog.show(ctx, null, "Sending Request...");
        final String url_adjustment_request = url.url_timesheet_adjustment(user.getApi_token(), user.getLink());

        Log.d("ShowTimesheet", url_adjustment_request);

        StringRequest requestAdjustment = new StringRequest(Request.Method.POST, url_adjustment_request, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingScreenDialog.dismiss();

                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("status").equals("success")) {

                        Toasty.success(ctx, "Request sent", Toast.LENGTH_SHORT).show();

                        clearForm();
                    } else {
                        Toasty.error(ctx, obj.getString("msg"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("ShowTimesheet", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(ctx, error.getMessage(), Toast.LENGTH_SHORT).show();

                Log.d("ShowTimesheet", error.toString());

                loadingScreenDialog.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                return Headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {

                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("user_id", user.getUser_id());
                    jsonBody.put("date_in", date_in);
                    jsonBody.put("day_type", i_day_type);
                    jsonBody.put("reason", reason);
                    jsonBody.put("reference", timeAdjustment.getReference());
                    jsonBody.put("shift", shift);
                    jsonBody.put("time_in", timeIn);
                    jsonBody.put("time_out", timeOut);

                    // Edit Schedule Update
                    // March 31 ,2020
                    //Added params
                    jsonBody.put("old_time_in", old_time_in);
                    jsonBody.put("old_time_out", old_time_out);
                    jsonBody.put("old_day_type", !old_day_type.isEmpty() ? old_day_type.isEmpty() : "ABSENT" );
                    jsonBody.put("shift_in", shift_in);
                    jsonBody.put("shift_out", shift_out);
                    jsonBody.put("edit_sched", edit_sched);
                    jsonBody.put("isBroken", isBroken.toString());

                    Log.d("ShowTimesheet", jsonBody.toString());

                } catch (JSONException e) {
                    e.printStackTrace();

                    Log.d("ShowTimesheet", e.toString());
                }
                return jsonBody.toString().getBytes(Charset.forName("UTF-8"));
            }
        };

        requestAdjustment.setRetryPolicy(new DefaultRetryPolicy(60 * 1000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(requestAdjustment);
    }


    private void clearForm() {
        TextView txtAdjustedDate = v.findViewById(R.id.txt_date_calendar);
        TextView txtAdjustedTimeIn = v.findViewById(R.id.txt_time_in);
        TextView txtAdjustedTimeOut = v.findViewById(R.id.txt_time_out);
        EditText txtAdjustedReason = v.findViewById(R.id.edtxt_adjustment_reason);

        txtAdjustedDate.setText("");
        txtAdjustedTimeIn.setText(blank_time);
        txtAdjustedTimeOut.setText(blank_time);
        txtAdjustedReason.setText("");

        converted_date = "";
        date_from_timesheet = "";

        spinnerDayType.setSelection(0);
    }
}