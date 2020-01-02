package com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestAdjustment.AdjusmentFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestAdjustment.AdjusmentFragments.ShowFragment.ShowPendingDetails;
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestAdjustment.AdjustmentAdapter.PendingAdapter;
import com.example.timekeeping_beta.Fragments.UserEmployee.Adjustments.Models.AdjustmentRequestItem;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.Helper;
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

public class PendingAdjustmentsFragment extends Fragment implements PendingAdapter.OnItemClickListener{

    private View v;
    private Context ctx;
    private final URLs url = new URLs();
    private final User user = SharedPrefManager.getInstance(getActivity()).getUser();

    private RecyclerView recyclerView;
    public List<AdjustmentRequestItem> adjustmentRequestList;
    private Helper helper;

    private TextView no_data;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_pending, container, false);
        ctx = v.getContext();

        recyclerView = v.findViewById(R.id.recyclerViewAdjustmentRequest);
        no_data = v.findViewById(R.id.no_data);
        helper = Helper.getInstance(v.getContext());

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        adjustmentRequestList = new ArrayList<>();
        loadMyPedingRequest();
    }

    private void setuprecyclerView(List<AdjustmentRequestItem> adjustmentRequestList) {
        if(adjustmentRequestList.size() > 0){
            PendingAdapter pendingAdapter = new PendingAdapter(adjustmentRequestList, this);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(pendingAdapter);
            whenSuccess();
        }else{
            whenEmpty();
        }
    }

    private void whenSuccess () {
        no_data.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void whenEmpty () {
        no_data.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void whenError () {
        no_data.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

        TryAgainFromNestedFrament tryAgainFragment = new TryAgainFromNestedFrament();
        Bundle arguments = new Bundle();
        arguments.putInt("RETURN_TO", Flag.PENDING_ADJUJSTMENTS_FRAGMENT);
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

    public void loadMyPedingRequest(){
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), null, "Please Wait...");
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        String url_mytimeadjustment = url.url_mytimeadjustment(user.getUser_id(), user.getApi_token(), user.getLink());

        JsonObjectRequest requestAdjustment = new JsonObjectRequest(Request.Method.GET, url_mytimeadjustment, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();

                try {
                    JSONArray obj_array = response.getJSONArray("msg");
                    Integer obj_array_length = obj_array.length();
                    for(int oa = 0; oa < obj_array_length; oa++){
                        JSONObject data = obj_array.getJSONObject(oa);
                        if(data.getString("status").equals("pending")){
                            AdjustmentRequestItem adjustmentRequestItem = new AdjustmentRequestItem();
                            adjustmentRequestItem.setId(data.getString("id"));
                            adjustmentRequestItem.setDate(helper.convertToReadableDate(data.getString("date_in")));
                            adjustmentRequestItem.setDay_type(data.getString("day_type"));
                            adjustmentRequestItem.setTime_in(helper.convertToReadableTime(data.getString("old_time_in")));
                            adjustmentRequestItem.setTime_out(helper.convertToReadableTime(data.getString("old_time_out")));
                            adjustmentRequestItem.setShift_in(helper.convertToReadableTime(data.getString("shift_in")));
                            adjustmentRequestItem.setShift_out(helper.convertToReadableTime(data.getString("shift_out")));
                            adjustmentRequestItem.setGrace_period(data.getString("grace_period"));
                            adjustmentRequestItem.setReference(data.getString("reference"));

                            adjustmentRequestItem.setRequested_time_in(data.getString("time_in"));
                            adjustmentRequestItem.setRequested_time_out(data.getString("time_out"));
                            adjustmentRequestItem.setRequested_day_type(data.getString("day_type"));

                            adjustmentRequestItem.setReason((data.getString("reason").equals("null")) ? "" : data.getString("reason"));
                            adjustmentRequestItem.setStatus((data.getString("status").equals("null")) ? "" : data.getString("status"));
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

    // click event for recyclerview
    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), ShowPendingDetails.class);
        intent.putExtra("adjustment_pending", adjustmentRequestList.get(position));
        startActivity(intent);
    }



}
