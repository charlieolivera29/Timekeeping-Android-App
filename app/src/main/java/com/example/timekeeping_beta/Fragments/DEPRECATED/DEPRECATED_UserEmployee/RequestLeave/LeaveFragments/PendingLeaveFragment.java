package com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestLeave.LeaveFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.timekeeping_beta.Fragments.Retry.TryAgainFromNestedFrament;
import com.example.timekeeping_beta.Globals.CustomClasses.Flag;
import com.example.timekeeping_beta.R;
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestLeave.LeaveAdapter.LeavePendingAdapter;
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestLeave.LeaveFragments.ShowFragment.ShowLeaveDetails;
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestLeave.LeaveRequestItem;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.Globals.Models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class PendingLeaveFragment extends Fragment implements LeavePendingAdapter.OnItemClickListener {

    private final URLs url = new URLs();
    private final User user = SharedPrefManager.getInstance(getActivity()).getUser();
    private final String DATABASE = user.getC1();
    private final String TABLE = user.getC2();

    private RecyclerView recyclerView;
    private LeavePendingAdapter leavePendingAdapter;
    public List<LeaveRequestItem> leaveRequestList;

    private com.example.timekeeping_beta.Globals.Helper Helper;

    private TextView no_data;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leave_pending, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewLeavePending);
        no_data = view.findViewById(R.id.no_data);

        Helper = com.example.timekeeping_beta.Globals.Helper.getInstance(view.getContext());

        leaveRequestList = new ArrayList<>();
        leavePendingAdapter = new LeavePendingAdapter(leaveRequestList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(leavePendingAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        loadLeaveRequest();
    }

    public void loadLeaveRequest() {
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), null, "Please Wait...");
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        String url_show_leave = url.url_show_leave(user.getUser_id(), user.getApi_token(), user.getLink());

        JsonObjectRequest requestLeave = new JsonObjectRequest(Request.Method.GET, url_show_leave, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                try {
                    JSONArray obj_array = response.getJSONArray("msg");

                    leaveRequestList.clear();

                    for (int oa = 0; oa < obj_array.length(); oa++) {
                        JSONObject data = obj_array.getJSONObject(oa);
                        if (data.getString("status").equals("pending")) {
                            LeaveRequestItem leaveRequestItem = new LeaveRequestItem();
                            leaveRequestItem.setRequest_id(data.getString("request_id"));
                            leaveRequestItem.setLeave_type(data.getString("leave_type"));
                            leaveRequestItem.setDate_start(dateConvert(data.getString("date_start")));
                            leaveRequestItem.setDate_end(dateConvert(data.getString("date_end")));
                            leaveRequestItem.setTime_start(Helper.convertToReadableTime(data.getString("time_start")));
                            leaveRequestItem.setTime_end(Helper.convertToReadableTime(data.getString("time_end")));
                            leaveRequestItem.setStatus(data.getString("status"));
                            leaveRequestItem.setDay_type(data.getString("day_type"));
                            leaveRequestItem.setReason(data.getString("reason"));
                            leaveRequestList.add(leaveRequestItem);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                setuprecyclerView();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();
                whenError();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("d", DATABASE);
                headers.put("t", TABLE);
                return headers;
            }
        };

        queue.add(requestLeave);
    }

    private void setuprecyclerView() {
        if(leaveRequestList.size() > 0){
            Collections.reverse(leaveRequestList);
            leavePendingAdapter.notifyDataSetChanged();
            whenSuccess();
        } else {
            whenEmpty();
        }
    }

    private void whenSuccess() {
        no_data.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void whenEmpty() {
        no_data.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void whenError () {
        no_data.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

        TryAgainFromNestedFrament tryAgainFragment = new TryAgainFromNestedFrament();
        Bundle arguments = new Bundle();
        arguments.putInt("RETURN_TO", Flag.PENDDING_LEAVE_FRAGMENT);
        arguments.putInt("CONTAINER", R.id.fragment_leave_container);
        tryAgainFragment.setArguments(arguments);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        if (fragmentManager != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_leave_container, tryAgainFragment)
                    .commit();
        } else {
            Toasty.error(getContext(), "Fragment manager is empty.", Toasty.LENGTH_LONG).show();
        }
    }

    public String dateConvert(String date) {

        String string_date = date;
        String converted_date = "";
        SimpleDateFormat raw_date = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat date_day = new SimpleDateFormat("MMM dd, yyyy");

        try {
            Date raw_dateDt = raw_date.parse(string_date);
            converted_date = date_day.format(raw_dateDt);
            Log.d("@date_with_day", converted_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return converted_date;
    }


    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), ShowLeaveDetails.class);
        intent.putExtra("leave_pending", leaveRequestList.get(position));
        startActivity(intent);
    }
}
