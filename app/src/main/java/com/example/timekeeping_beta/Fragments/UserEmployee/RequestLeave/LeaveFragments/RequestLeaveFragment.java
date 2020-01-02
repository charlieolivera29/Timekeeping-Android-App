package com.example.timekeeping_beta.Fragments.UserEmployee.RequestLeave.LeaveFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.R;
import com.example.timekeeping_beta.Fragments.UserEmployee2.Adjustments.Models.TimeAdjustment;
import com.example.timekeeping_beta.Fragments.UserEmployee.RequestLeave.DialogFragment.DateEndPickerFragment;
import com.example.timekeeping_beta.Fragments.UserEmployee.RequestLeave.DialogFragment.DateStartPickerFragment;
import com.example.timekeeping_beta.Fragments.UserEmployee.RequestLeave.LeaveFragments.Models.Leave;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.Globals.Models.User;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class RequestLeaveFragment extends Fragment {

    private Context ctx;
    private final User user = SharedPrefManager.getInstance(getActivity()).getUser();
    private final TimeAdjustment timeAdjustment = new TimeAdjustment();
    private final URLs url = new URLs();

    private final String USER_ID = user.getUser_id();
    private final String DATABASE = user.getC1();
    private final String TABLE = user.getC2();

    private Spinner spinner;
    private View view;
    private AppCompatButton btnLeaveRequest;
    private RadioGroup radioGroupShift;
    private RadioButton radioButtonShift;

    private String leave_code;
    private String shift;
    private String leave_type;

    private RequestQueue queue;

    private TextView txtLeaveDateStart;
    private TextView txtLeaveDateEnd;
    private EditText txtLeaveReason;

    private DialogFragment datePicker;

    String start_date;
    String end_date;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_request_leave, container, false);

        ctx = view.getContext();

        radioGroupShift = view.findViewById(R.id.radio_group_shift);
        txtLeaveDateStart = view.findViewById(R.id.txt_leave_date_start);
        txtLeaveDateEnd = view.findViewById(R.id.txt_leave_date_end);
        txtLeaveReason = view.findViewById(R.id.txt_leave_reason);

        setListeners();

        checkButton();
        spinnerLeaveFunction();
        leaveBtnRequest();
        calendarStartBtn();
        calendarEndBtn();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setListeners() {
        txtLeaveDateStart.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                start_date = ((DateStartPickerFragment) datePicker).getStartDate();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        txtLeaveDateEnd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                end_date = ((DateEndPickerFragment) datePicker).getEndDate();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    // Spinner function
    public void spinnerLeaveFunction() {
        spinner = view.findViewById(R.id.spinner_leave_type);
        final List<Leave> leaveList = new ArrayList<>();

        String url_show_leave_management = url.url_show_leave_management(user.getApi_token(), user.getLink());
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), null, "Please Wait...");

        JsonObjectRequest requestLeaveManagement = new JsonObjectRequest(Request.Method.GET, url_show_leave_management, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();

                try {
                    JSONArray obj_array = response.getJSONArray("msg");
                    for (int oa = 0; oa < obj_array.length(); oa++) {
                        JSONObject data = obj_array.getJSONObject(oa);
                        final Leave leave = new Leave();
                        leave.setLeave_name(data.getString("leave_name"));
                        leave.setLeave_code(data.getString("leave_code"));
                        leaveList.add(leave);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ArrayAdapter<Leave> leave_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, leaveList);
                leave_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(leave_adapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return Helper.getInstance(getContext()).headers();
            }
        };

        queue.add(requestLeaveManagement);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Leave leave = (Leave) adapterView.getSelectedItem();
                leave_code = leave.getLeave_code();
                Log.d("Leave_Code", leave_code);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    // Radio Button
    public void checkButton() {
        radioGroupShift.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                radioButtonShift = view.findViewById(i);
                Log.d("id_value", String.valueOf(radioButtonShift.getText()));

                LinearLayout leaveDateStartEndLayout = view.findViewById(R.id.leave_request_date_start_end_layout);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(leaveDateStartEndLayout);
                }

                if (String.valueOf(radioButtonShift.getText()).equals("Whole")) {
                    shift = String.valueOf(radioButtonShift.getText());
                    Log.d("shift_value", shift);
                    leaveDateStartEndLayout.setVisibility(View.VISIBLE);
                } else {
                    shift = String.valueOf(radioButtonShift.getText());
                    Log.d("shift_value", shift);
                    leaveDateStartEndLayout.setVisibility(View.GONE);
                }

                if (shift.equals("1st shift")) {
                    leave_type = "first";
                } else if (shift.equals("2st shift")) {
                    leave_type = "second";
                } else {
                    leave_type = "whole";
                }
            }
        });
    }

    // Leave button
    public void leaveBtnRequest() {
        btnLeaveRequest = view.findViewById(R.id.btn_leave_request);

        btnLeaveRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Id_radio", String.valueOf(radioGroupShift.getCheckedRadioButtonId()));
                if (radioGroupShift.getCheckedRadioButtonId() == -1) {
                    Toasty.error(getContext(), "Please Select Shift", Toast.LENGTH_SHORT).show();
                } else {
                    sendLeaveRequest();
                }
            }
        });
    }


    public void calendarStartBtn() {
        Button btnCalendarStart = view.findViewById(R.id.btn_calendar_leave_start);
        btnCalendarStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker = new DateStartPickerFragment();
                datePicker.show(getChildFragmentManager(), null);

            }
        });
    }

    public void calendarEndBtn() {
        Button btnCalendarStart = view.findViewById(R.id.btn_calendar_leave_end);
        btnCalendarStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker = new DateEndPickerFragment();
                datePicker.show(getChildFragmentManager(), null);
            }
        });
    }


    public void sendLeaveRequest() {

        if (start_date != null &&
                leave_type != null &&
                leave_code != null &&
                txtLeaveReason.getText().toString().length() > 0) {

            final ProgressDialog loadingScreenDialog = ProgressDialog.show(getContext(), null, "Sending Request...");

            final String reason = txtLeaveReason.getText().toString();

            String url_create_leave = url.url_create_leave(user.getApi_token(), user.getLink());

            StringRequest requestLeave = new StringRequest(Request.Method.POST, url_create_leave, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    loadingScreenDialog.dismiss();
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("status").equals("success")) {
                            Toasty.success(getContext(), "Request sent", Toast.LENGTH_SHORT).show();

                            //Affects text listener
                            //Line: 123
                            //clearForm();

                            getActivity().onBackPressed();

//                            Redirect to Pending Adjustments
//                            getFragmentManager()
//                                    .beginTransaction()
//                                    .replace(R.id.fragment_leave_container, new LeavesFragment()d)
//                                    .commit();
                        } else {
                            Toasty.error(getContext(), obj.getString("msg"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toasty.error(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    loadingScreenDialog.dismiss();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    return Helper.getInstance(getContext()).headers();
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("user_id", USER_ID);
                    params.put("date_start", start_date);
                    params.put("date_end", end_date == null ? end_date = "" : end_date);
                    params.put("leave_type", leave_type);
                    params.put("day_type", leave_code);
                    params.put("reason", reason);
                    return params;
                }
            };

            requestLeave.setRetryPolicy(new DefaultRetryPolicy(60 * 1000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(requestLeave);
        } else {
            Toasty.error(ctx, "Please fill all fields", Toasty.LENGTH_LONG).show();
        }

    }

    //Affects text listener
    //Line: 123
    private void clearForm() {

        start_date = null;
        end_date = null;
        leave_type = null;
        leave_code = null;

        radioGroupShift.setSelected(false);
        txtLeaveDateStart.setText(ctx.getResources().getString(R.string.blank_date));
        txtLeaveDateEnd.setText(ctx.getResources().getString(R.string.blank_date));
        txtLeaveReason.setText("");
    }
}
