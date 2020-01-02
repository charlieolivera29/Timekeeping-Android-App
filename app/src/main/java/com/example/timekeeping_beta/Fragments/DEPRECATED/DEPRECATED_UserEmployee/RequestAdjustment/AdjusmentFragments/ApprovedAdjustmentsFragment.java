package com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestAdjustment.AdjusmentFragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestAdjustment.AdjustmentAdapter.AdjustmentAdapter;
import com.example.timekeeping_beta.Fragments.UserEmployee.Adjustments.Models.AdjustmentRequestItem;
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

public class ApprovedAdjustmentsFragment extends Fragment {

    private final URLs url = new URLs();
    private User user;

    private RecyclerView recyclerView;
    public List<AdjustmentRequestItem> adjustmentRequestList;


    private TextView no_data;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_approve, container, false);

        recyclerView = v.findViewById(R.id.recyclerViewAdjustmentApprove);
        no_data = v.findViewById(R.id.no_data);

        user = SharedPrefManager.getInstance(v.getContext()).getUser();

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustmentRequestList = new ArrayList<>();
        loadMyApproveRequest();
    }

    private void setuprecyclerView(List<AdjustmentRequestItem> adjustmentRequestList) {
        if (adjustmentRequestList.size() > 0) {
            AdjustmentAdapter adjustmentAdapter = new AdjustmentAdapter(getContext(), adjustmentRequestList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adjustmentAdapter);
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
        arguments.putInt("RETURN_TO", Flag.APPROVED_ADJUSTMENTS_FRAGMENT);
        arguments.putInt("CONTAINER", R.id.fragment_adjustment_container);
        tryAgainFragment.setArguments(arguments);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        if (fragmentManager != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_adjustment_container, tryAgainFragment)
                    .commit();
        } else {
            Toasty.error(getContext(), "Fragment manager is empty.", Toasty.LENGTH_LONG).show();
        }
    }

    public void loadMyApproveRequest() {
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), null, "Please Wait...");
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        String url_mytimeadjustment = url.url_mytimeadjustment(user.getUser_id(), user.getApi_token(), user.getLink());

        JsonObjectRequest requestAdjustment = new JsonObjectRequest(Request.Method.GET, url_mytimeadjustment, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();

                try {
                    JSONArray obj_array = response.getJSONArray("msg");

                    for (int oa = 0; oa < obj_array.length(); oa++) {
                        JSONObject data = obj_array.getJSONObject(oa);
                        if (data.getString("status").equals("approved")) {
                            AdjustmentRequestItem adjustmentRequestItem = new AdjustmentRequestItem();
                            adjustmentRequestItem.setDate(data.getString("date_in"));
                            adjustmentRequestItem.setDay_type(data.getString("old_day_type"));
                            adjustmentRequestItem.setTime_in(data.getString("old_time_in"));
                            adjustmentRequestItem.setTime_out(data.getString("old_time_out"));
                            adjustmentRequestItem.setShift_in(data.getString("shift_in"));
                            adjustmentRequestItem.setShift_out(data.getString("shift_out"));
                            adjustmentRequestItem.setGrace_period(data.getString("grace_period"));
                            adjustmentRequestItem.setReference(data.getString("reference"));
                            adjustmentRequestItem.setReason(data.getString("reason"));
                            adjustmentRequestItem.setStatus(data.getString("status"));
                            adjustmentRequestItem.setRequested_time_in(data.getString("time_in"));
                            adjustmentRequestItem.setRequested_time_out(data.getString("time_out"));
                            adjustmentRequestItem.setRequested_day_type(data.getString("day_type"));
                            adjustmentRequestList.add(adjustmentRequestItem);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                setuprecyclerView(adjustmentRequestList);
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
                headers.put("d", user.getC1());
                headers.put("t", user.getC2());
                return headers;
            }
        };

        queue.add(requestAdjustment);
    }

}
