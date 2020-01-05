package com.example.timekeeping_beta.Fragments.Timesheet;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.timekeeping_beta.Fragments.GlobalSettings.LeaveTypes.LeaveTypesViewModel;
import com.example.timekeeping_beta.Globals.CustomClasses.OnSwipeTouchListener;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.R;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.Globals.Models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class TimesheetFragment extends Fragment {

    private User user;
    private Context ctx;

    // objects
    private View v;
    private List<TimesheetItem> timesheetList;
    private RecyclerView recyclerView;
    private TextView date_start;
    private TextView tv_dash;
    private TextView date_end;
    private TextView tv_month;
    private SwipeRefreshLayout button_try_again;
    private ImageView button_previous;
    private ImageView button_next;

    // local variable
    private String date = "";
    private String prev_start;
    private String next_start;
    private String date_begin;
    private String date_finish;

    private LinearLayout timesheetnavigattion;
    private TimesheetAdapter timesheetAdapter;
    private ProgressDialog dialog;
    private ImageButton button_color_legend;

    private Dialog legendDialog;

    private FrameLayout timesheetsSwipeContainer;

    @Override
    public void onPause() {
        super.onPause();
        //timesheetnavigattion.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();

        //timesheetnavigattion.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        //timesheetnavigattion.setVisibility(View.VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_timesheet, container, false);
        ctx = v.getContext();

        init();
        setListeners();
        loadMyTimesheet(date);

        if (getActivity() != null) {
            getActivity().setTitle(getResources().getString(R.string.title_fragment_timesheet));
        }


        return v;
    }

    private void init() {

        user = SharedPrefManager.getInstance(getActivity()).getUser();
        //These views are inside the Main Activity layout not in fragment layout
        timesheetnavigattion = getActivity().findViewById(R.id.timesheetnavigattion);
        tv_month = getActivity().findViewById(R.id.tv_month);
        date_start = getActivity().findViewById(R.id.tv_date_start);
        tv_dash = getActivity().findViewById(R.id.tv_dash);
        date_end = getActivity().findViewById(R.id.tv_date_end);
        button_previous = getActivity().findViewById(R.id.iv_prev_month);
        button_next = getActivity().findViewById(R.id.iv_next_month);
        button_color_legend = getActivity().findViewById(R.id.button_color_legend);
        //These views are inside the Main Activity layout not in fragment layout

        //Views inside fragment
        recyclerView = v.findViewById(R.id.recyclerViewTimesheet);
        button_try_again = v.findViewById(R.id.retry);

        timesheetsSwipeContainer = v.findViewById(R.id.timesheetsSwipeContainer);
        //Views inside fragment

        timesheetList = new ArrayList<>();
        button_color_legend.setBackgroundResource(0);
        button_color_legend.setVisibility(View.VISIBLE);
        legendDialog = new Dialog(v.getContext());
        legendDialog.setContentView(R.layout.dialog_timesheet_legend);
    }

    private void setListeners() {

        //Improve on next version
        recyclerView.setOnTouchListener(new OnSwipeTouchListener(getContext()) {

            public void onSwipeRight() {
                previousTimesheet();
            }

            @Override
            public void onSwipeLeft() {
                nextTimesheet();
            }

            @Override
            public void onItemTouch(float x, float y) {
                View view = recyclerView.findChildViewUnder(x, y);
                if (view != null && view.getTag() != null) {

                    TimesheetItem ti = timesheetList.get(Integer.parseInt(view.getTag().toString()));

                    timesheetAdapter.redirectToEditPage(ti);
                }
            }

        });

        button_try_again.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMyTimesheet(date);
            }
        });

        button_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousTimesheet();
            }
        });

        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextTimesheet();
            }
        });

        button_color_legend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                legendDialog.show();
            }
        });
    }

    public void loadMyTimesheet(String date) {

        whenLoading();
        RequestQueue queue = Volley.newRequestQueue(ctx);

        URLs url = new URLs();

        String edtr_url = url.url_edtr(user.getUser_id(), user.getApi_token(), user.getLink(), date);
        Log.d("@edtr_url", edtr_url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, edtr_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    if (response.getString("status").equals("success")) {

                        timesheetList.clear();

                        // this will get the json object request
                        String stringMsg = response.getString("msg");

                        // access the json object request
                        //get the object of edtr
                        JSONArray edtr_count = response.getJSONObject("msg").getJSONArray("edtr");//get the object dates

                        date_begin = response.getJSONObject("msg").getJSONObject("dates").getString("start");
                        date_finish = response.getJSONObject("msg").getJSONObject("dates").getString("end");

                        date_start.setText(date_begin);
                        tv_dash.setVisibility(View.VISIBLE);
                        date_end.setText(date_finish);

                        // get the value of prev_start
                        prev_start = response.getJSONObject("msg").getJSONObject("dates").getString("prev_start");

                        tv_month.setText(Helper.getInstance(getContext()).convertToFormattedTime(prev_start, "yyyy-MM-dd", "MMMM"));

                        // get the value of next_start
                        next_start = response.getJSONObject("msg").getJSONObject("dates").getString("next_start");

                        for (int ed = 0; ed < edtr_count.length(); ed++) {
                            JSONObject edtr_data = edtr_count.getJSONObject(ed);
                            // debug data
                            Log.d("@data_date_in", edtr_data.getString("date_in"));
                            Log.d("@date_time_in", edtr_data.getString("time_in"));

                            String time_in_convertion = timeConvert(edtr_data.getString("time_in"));
                            String time_out_convertion = timeConvert(edtr_data.getString("time_out"));

                            //Removed in update 11-29-2019
                            //String shift_in_convertion = timeConvert(edtr_data.has("shift_in") ? edtr_data.getString("shift_in") : getResources().getString(R.string.blank_time));
                            //String shift_out_convertion = timeConvert(edtr_data.has("shift_out") ? edtr_data.getString("shift_out") : getResources().getString(R.string.blank_time));

                            String shift_in_convertion = user.getSchedule_shift_in();
                            String shift_out_convertion = user.getSchedule_shift_out();

                            String time_in;
                            String time_out;
                            String shift_in = user.getSchedule_shift_in();
                            String shift_out = user.getSchedule_shift_out();

                            if (!edtr_data.getString("day_type").equalsIgnoreCase("HOLIDAY")) {
                                time_in = (edtr_data.getString("time_in").equals("null")) || edtr_data.getString("time_in").isEmpty() ? "--:-- --" : time_in_convertion;
                                time_out = (edtr_data.getString("time_out").equals("null")) || edtr_data.getString("time_out").isEmpty() ? "--:-- --" : time_out_convertion;
                                //shift_in = (edtr_data.getString("shift_in").equals("null")) ? "--:-- --" : shift_in_convertion;
                                //shift_out = (edtr_data.getString("shift_out").equals("null")) ? "--:-- --" : shift_out_convertion;
                            } else {
                                time_in = "--:-- --";
                                time_out = "--:-- --";
                                //shift_in = "--:-- --";
                                //shift_out = "--:-- --";
                            }

                            if (edtr_data.getString("day_type").equalsIgnoreCase("RD")) {
                                time_in = "--:-- --";
                                time_out = "--:-- --";

                                shift_in = "--:-- --";
                                shift_out = "--:-- --";
                            }

                            Log.d("@time_in", time_in);

                            TimesheetItem timesheetItem = new TimesheetItem();

                            timesheetItem.setDate_in(dateConvert(edtr_data.getString("date_in")));
                            timesheetItem.setNumeric_date(edtr_data.getString("date_in"));

                            timesheetItem.setReadable_day_of_month(getDate(edtr_data.getString("date_in")));
                            timesheetItem.setReadable_month(getMonth(edtr_data.getString("date_in")));

                            timesheetItem.setTime_in(time_in);
                            timesheetItem.setTime_out(time_out);
                            timesheetItem.setShift_in(shift_in);
                            timesheetItem.setShift_out(shift_out);
                            timesheetItem.setDay_type(edtr_data.getString("day_type"));
                            timesheetItem.setReference(edtr_data.getString("reference"));
                            timesheetItem.setLate((edtr_data.has("late") && !edtr_data.getString("late").equals("null")) ? edtr_data.getInt("late") : 0);
                            timesheetItem.setUndertime((edtr_data.has("undertime") && !edtr_data.getString("undertime").equals("null")) ? edtr_data.getInt("undertime") : 0);

                            int isAdjusted = 0;

                            if (edtr_data.has("isadjusted")) {
                                //if (!edtr_data.getString("isadjusted").equals("null")) {
                                if (edtr_data.getString("isadjusted").equals("1")) {
                                    isAdjusted = 1;
                                }
                            }


                            if (edtr_data.has("overtime")) {
                                timesheetItem.setOvertime(new TimesheetOvetime(
                                        edtr_data.getJSONObject("overtime").getString("start_time"),
                                        edtr_data.getJSONObject("overtime").getString("end_time")
                                ));
                            } else if (!edtr_data.has("overtime")) {
                                timesheetItem.setOvertime(new TimesheetOvetime(
                                        "null",
                                        "null"
                                ));
                            }


                            timesheetItem.setAdjusted(isAdjusted);
                            timesheetList.add(timesheetItem);

                            setuprecyclerView(timesheetList);
                        }
                        whenSuccess();
                    } else {
                        Toasty.info(getContext(), response.getString("msg"), Toast.LENGTH_SHORT).show();
                        whenError();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    whenError();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                whenError();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                return Helper.getInstance(getContext()).headers();
            }
        };

        queue.add(request);
    }

    private void whenLoading() {
        button_try_again.setVisibility(View.GONE);
        timesheetsSwipeContainer.setVisibility(View.GONE);
        //recyclerView.setVisibility(View.GONE);
        button_try_again.setRefreshing(false);

        if (getActivity() != null) {
            dialog = ProgressDialog.show(getActivity(), null, "Please Wait...");
        }
    }

    private void whenError() {
        dialog.dismiss();
        button_try_again.setRefreshing(false);
        //recyclerView.setVisibility(View.GONE);
        timesheetsSwipeContainer.setVisibility(View.GONE);

        button_try_again.setVisibility(View.VISIBLE);
    }

    private void whenSuccess() {
        dialog.dismiss();
        button_try_again.setRefreshing(false);
        button_try_again.setVisibility(View.GONE);

        //recyclerView.setVisibility(View.VISIBLE);
        timesheetsSwipeContainer.setVisibility(View.VISIBLE);
    }

    private void setuprecyclerView(List<TimesheetItem> timesheetList) {
        timesheetAdapter = new TimesheetAdapter(getActivity(), timesheetList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(timesheetAdapter);
        timesheetAdapter.notifyDataSetChanged();
    }

    public void previousTimesheet() {
        String next_date = prev_start;
        loadMyTimesheet(next_date);
    }

    public void nextTimesheet() {
        String next_date = next_start;
        loadMyTimesheet(next_date);
    }

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

        if (converted_time.isEmpty()) {
            converted_time = "--:-- --";
        }

        return converted_time;

    }

    public String dateConvert(String date) {

        String string_date = date;
        String converted_date = "";
        SimpleDateFormat raw_date = new SimpleDateFormat("yyyy-MM-dd");


        SimpleDateFormat date_day = new SimpleDateFormat("MMM dd, yyyy (EEE)");

        try {
            Date raw_dateDt = raw_date.parse(string_date);

            converted_date = date_day.format(raw_dateDt);

            Log.d("@date_with_day", converted_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return converted_date;
    }

    private String getDate(String date) {

        String string_date = date;
        String converted_date = "";
        SimpleDateFormat raw_date = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat date_day = new SimpleDateFormat("dd");

        try {
            Date raw_dateDt = raw_date.parse(string_date);

            converted_date = date_day.format(raw_dateDt);

            Log.d("@date_with_day", converted_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return converted_date;
    }

    private String getMonth(String date) {

        String string_date = date;
        String converted_date = "";
        SimpleDateFormat raw_date = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat date_day = new SimpleDateFormat("MMM");

        try {
            Date raw_dateDt = raw_date.parse(string_date);

            converted_date = date_day.format(raw_dateDt);

            Log.d("@date_with_day", converted_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return converted_date;
    }

}