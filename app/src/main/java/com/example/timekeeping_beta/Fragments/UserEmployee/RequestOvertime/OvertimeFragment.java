package com.example.timekeeping_beta.Fragments.UserEmployee.RequestOvertime;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.timekeeping_beta.Fragments.Retry.TryAgainFragment;
import com.example.timekeeping_beta.Fragments.UserApprover.Overtime.Overtime;
import com.example.timekeeping_beta.Globals.CustomClasses.Flag;
import com.example.timekeeping_beta.Globals.Models.queryStringBuilder;
import com.example.timekeeping_beta.Globals.StaticData.URLs_v2;
import com.example.timekeeping_beta.R;
import com.example.timekeeping_beta.Fragments.UserEmployee.RequestOvertime.Adapter.RequestedOvertimesAdapter;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.Globals.Models.User;
import com.example.timekeeping_beta.Globals.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class OvertimeFragment extends Fragment {

    private Context context;

    private View v;
    public BottomNavigationView nav;

    private FrameLayout frame_overtime_list;
    private FrameLayout frame_request_overtime;

    private RecyclerView recyclerviewAllRequestedOvertimes;
    private RequestedOvertimesAdapter mAdapter;

    private EditText search_bar;
    private TextView no_data;

    private Dialog DatePickerDialog;
    private Dialog TimePickerDialog;
    ProgressDialog loadingScreenDialog;
    private ImageButton button_startTime;
    private ImageButton button_endTime;
    private ImageButton btn_calendar_overtime_date;

    private DatePicker date;
    private TimePicker start_time;
    private TimePicker end_time;

    private TextView txt_overtime_date;
    private TextView txt_overtime_time_start;
    private TextView txt_overtime_time_end;

    private EditText txt_overtime_reason;

    private Button btn_overtime_request;

    public List<Overtime> Overtimes;

    private String str_start_time;
    private String str_end_time;
    private String str_date;
    private Helper helper;
    private RequestQueue queue;
    private FloatingActionButton fab_make_request;
    private CardView layout_request_overtime;

    @Override
    public void onResume() {
        super.onResume();
        getUserOvertimes();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_overtime_request, container, false);
        context = v.getContext();
        helper = Helper.getInstance(context);
        queue = Volley.newRequestQueue(context);

        initViews();
        setListeners();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        nav.setOnNavigationItemSelectedListener(navListener);
        recyclerviewAllRequestedOvertimes.setHasFixedSize(true);
        recyclerviewAllRequestedOvertimes.setLayoutManager(layoutManager);
        DatePickerDialog = new Dialog(context);
        TimePickerDialog = new Dialog(context);
        Overtimes = new ArrayList<>();

        //showPendingOvertimes();
        //getUserOvertimes();

        return v;
    }

    private void setListeners() {

        btn_calendar_overtime_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.setContentView(R.layout.dialog_date_picker);
                date = DatePickerDialog.findViewById(R.id.date_picker);
                DatePickerDialog.setTitle("Set Date");
                Button button_set_date = DatePickerDialog.findViewById(R.id.button_set_date);
                Button button_cancel_date_picker = DatePickerDialog.findViewById(R.id.button_cancel_date_picker);

                button_set_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Integer nonZeroIndexedMonth = date.getMonth() + 1;

                        String m = nonZeroIndexedMonth < 10 ? "0" + nonZeroIndexedMonth : String.valueOf(nonZeroIndexedMonth);
                        String d = date.getDayOfMonth() < 10 ? "0" + date.getDayOfMonth() : String.valueOf(date.getDayOfMonth());
                        String y = date.getYear() < 10 ? "0" + date.getYear() : String.valueOf(date.getYear());

                        String string_date = y + "-" + m + "-" + d;
                        String human_readable_date = helper.convertToReadableDate(string_date);

                        txt_overtime_date.setText(human_readable_date);
                        DatePickerDialog.dismiss();
                    }
                });

                button_cancel_date_picker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DatePickerDialog.dismiss();
                    }
                });

                DatePickerDialog.show();
            }
        });

        button_startTime.setOnClickListener(new View.OnClickListener() {
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

        button_endTime.setOnClickListener(new View.OnClickListener() {
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

                String day = txt_overtime_date.getText() != null ? txt_overtime_date.getText().toString() : "";
                paramsChecker(day, str_start_time, str_end_time, txt_overtime_reason.getText().toString());
            }
        });

        fab_make_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOvertimeRequest();
            }
        });

    }

    private void initViews() {
        layout_request_overtime = v.findViewById(R.id.layout_request_overtime);
        fab_make_request = v.findViewById(R.id.fab_make_request);
        recyclerviewAllRequestedOvertimes = v.findViewById(R.id.recyclerviewAllRequestedOvertimes);
        frame_overtime_list = v.findViewById(R.id.frame_overtime_list);
        frame_request_overtime = v.findViewById(R.id.frame_request_overtime);
        nav = v.findViewById(R.id.navigation_overtimes);
        btn_calendar_overtime_date = v.findViewById(R.id.btn_calendar_overtime_date);
        button_startTime = v.findViewById(R.id.btn_overtime_start_time);
        button_endTime = v.findViewById(R.id.btn_overtime_end_time);
        ProgressBar loading_screen = v.findViewById(R.id.loading_screen);
        no_data = v.findViewById(R.id.no_data);
        txt_overtime_time_start = v.findViewById(R.id.txt_overtime_time_start);
        txt_overtime_time_end = v.findViewById(R.id.txt_overtime_time_end);
        txt_overtime_date = v.findViewById(R.id.txt_overtime_date);
        txt_overtime_reason = v.findViewById(R.id.txt_overtime_reason);
        btn_overtime_request = v.findViewById(R.id.btn_overtime_request);
    }

    private void showPendingOvertimes() {
        mAdapter = new RequestedOvertimesAdapter(Overtimes);
        Collections.reverse(Overtimes);
        recyclerviewAllRequestedOvertimes.setAdapter(mAdapter);
        resultChecker(mAdapter.filterByStatus("pending"));
    }

    @Override
    public void onStart() {
        super.onStart();

    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            if (mAdapter != null) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_pending:
                        resultChecker(mAdapter.filterByStatus("pending"));
                        break;
                    case R.id.nav_approve:
                        resultChecker(mAdapter.filterByStatus("approved"));
                        break;
                    case R.id.nav_decline:
                        resultChecker(mAdapter.filterByStatus("declined"));
                        break;
                }
            }

            //fab_make_request.setVisibility(View.VISIBLE);
            fab_make_request.show();
            return true;
        }
    };

    private void showOvertimeRequest() {

        layout_request_overtime.setVisibility(View.VISIBLE);
        //fab_make_request.setVisibility(View.GONE);
        fab_make_request.hide();
        no_data.setVisibility(View.GONE);

        frame_overtime_list.setVisibility(View.GONE);

        frame_request_overtime.setVisibility(View.VISIBLE);
    }

    private void resultChecker(Boolean b) {
        frame_request_overtime.setVisibility(View.GONE);

        frame_overtime_list.setVisibility(View.VISIBLE);

        if (!b) {
            whenNoResult();
            //no_data.setVisibility(View.VISIBLE);
        } else {
            whenSuccess();
            //ano_data.setVisibility(View.GONE);
        }
    }

    public void whenLoading() {
        frame_overtime_list.setVisibility(View.VISIBLE);
        layout_request_overtime.setVisibility(View.GONE);
        recyclerviewAllRequestedOvertimes.setVisibility(View.GONE);
        no_data.setVisibility(View.GONE);
        loadingScreenDialog = ProgressDialog.show(context, null, "Please Wait...");
    }

    public void whenSuccess() {
        frame_overtime_list.setVisibility(View.VISIBLE);
        layout_request_overtime.setVisibility(View.GONE);
        no_data.setVisibility(View.GONE);
        loadingScreenDialog.dismiss();
        recyclerviewAllRequestedOvertimes.setVisibility(View.VISIBLE);
    }

    public void whenNoResult() {
        frame_overtime_list.setVisibility(View.VISIBLE);
        layout_request_overtime.setVisibility(View.GONE);
        recyclerviewAllRequestedOvertimes.setVisibility(View.GONE);
        loadingScreenDialog.dismiss();
        no_data.setVisibility(View.VISIBLE);
    }

    public void whenError() {
        recyclerviewAllRequestedOvertimes.setVisibility(View.GONE);
        no_data.setVisibility(View.GONE);
        loadingScreenDialog.dismiss();

        TryAgainFragment tryAgainFragment = new TryAgainFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("RETURN_TO", Flag.OVERTIME_FRAGMENT);
        tryAgainFragment.setArguments(arguments);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        if (fragmentManager != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, tryAgainFragment)
                    .commit();
        } else {
            Toasty.error(context, "Fragment manager is empty.", Toasty.LENGTH_LONG).show();
        }
    }

    private void getUserOvertimes() {

        whenLoading();

        final URLs_v2 url = new URLs_v2();
        final User user = SharedPrefManager.getInstance(context).getUser();

        String GET_USER_ADJUSTMENTS = url.GET_USER_ADJUSTMENTS("overtimes/" + user.getUser_id(), new queryStringBuilder(user.getApi_token(), user.getLink(), 1, "", 10, "").build());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_USER_ADJUSTMENTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");
                    String message = response_obj.getString("msg");

                    if (status.equals("success")) {

                        JSONArray jot_array = new JSONArray(message);

                        if (jot_array.length() > 0) {

                            Overtimes.clear();

                            for (int i = 0; i < jot_array.length(); i++) {
                                JSONObject jot = (JSONObject) jot_array.get(i);

                                Integer id = jot.getInt("id");
                                String employee_id = jot.getString("user_id");

                                String numeric_date = jot.getString("date");
                                String _24HOUR_start_time = jot.getString("start_time");
                                String _24HOUR_end_time = jot.getString("end_time");

                                String date = numeric_date;
                                String start_time = _24HOUR_start_time;
                                String end_time = _24HOUR_end_time;

                                String ot_status = jot.getString("status");
                                String reason = jot.getString("reason");
                                String checked_by = jot.getString("checked_by");
                                String checked_at = jot.getString("checked_at");

                                String updated_at = jot.getString("updated_at");
                                String created_at = jot.getString("created_at");

                                Overtime ot = new Overtime(id, employee_id, date, start_time, end_time, ot_status, reason, checked_by, checked_at, "", user.getFname(), user.getLname(), "", updated_at, created_at);
                                Overtimes.add(ot);
                            }

                            showPendingOvertimes();
                        } else {
                            whenNoResult();
                        }
                    } else {
                        whenError();
                    }
                } catch (JSONException e) {
                    Toasty.info(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                    whenError();
                }

                loadingScreenDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                whenError();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                return helper.headers();
            }
        };
        queue.add(stringRequest);
    }


    private boolean paramsChecker(final String i_date, final String i_start_time, final String i_end_time, final String i_reason) {

        if (i_date.length() > 0 && i_start_time.length() > 0 && i_end_time.length() > 0 && i_reason.length() > 0) {

            sendOvertimeRequest(i_date, i_start_time, i_end_time, i_reason);
            return true;
        } else {

            Toasty.error(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void sendOvertimeRequest(final String i_date, final String i_start_time, final String i_end_time, final String i_reason) {
        final ProgressDialog requestLoadingScreenDialog = ProgressDialog.show(getContext(), null, "Sending Request...");

        final URLs url = new URLs();
        final User user = SharedPrefManager.getInstance(context).getUser();
        final String DATABASE = user.getC1();
        final String TABLE = user.getC2();

        String url_create_overtime = url.url_create_overtime();

        StringRequest requestOvertime = new StringRequest(Request.Method.POST, url_create_overtime, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                requestLoadingScreenDialog.dismiss();

                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {
                        whenSuccess();
                        Toasty.success(context, "Success", Toast.LENGTH_SHORT).show();
                        getUserOvertimes();

                        //resultChecker(mAdapter.filterByStatus("pending"));

                    } else {
                        Toasty.error(context, response_obj.getString("msg"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestLoadingScreenDialog.dismiss();
                Toasty.error(context, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                params.put("api_token", user.getApi_token());
                params.put("link", user.getLink());
                params.put("user_id", user.getUser_id());
                params.put("date", i_date);
                params.put("start_time", i_start_time);
                params.put("end_time", i_end_time);
                params.put("reason", i_reason);
                return params;
            }
        };

        requestOvertime.setRetryPolicy(new DefaultRetryPolicy(60 * 1000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(requestOvertime);
    }

    private void clearForm() {

    }
}