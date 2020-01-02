package com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestOvertime.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.timekeeping_beta.Fragments.UserApprover.Overtime.Overtime;
import com.example.timekeeping_beta.R;
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestOvertime.ShowOvertimeDetails;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.Globals.Models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RequestedOvertimesAdapter extends RecyclerView.Adapter<RequestedOvertimesAdapter.MyViewHolder> {

    private Context ctx;
    private Helper helper;

    private List<Overtime> mDataset;
    private List<Overtime> tempList;
    private List<Overtime> filteredList;

    private String employee_schedule_start;
    private String employee_schedule_end;

    //Constructor
    public RequestedOvertimesAdapter(List<Overtime> myDataset) {

        this.mDataset = myDataset;
        this.tempList = new ArrayList<>();
        tempList.addAll(mDataset);

        this.filteredList = new ArrayList<>();
    }

    //ViewHolder
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView requested_at;
        public TextView rvi_schedule_from;
        public TextView rvi_schedule_to;
        public TextView start_time;
        public TextView end_time;
        public TextView date;
        public FrameLayout overtime_status;
        public CardView rvi_overtime_request_details;

        public MyViewHolder(CardView v) {
            super(v);
            requested_at = v.findViewById(R.id.rvi_overtime_requested_at);
            start_time = v.findViewById(R.id.rvi_overtime_start_time);
            end_time = v.findViewById(R.id.rvi_overtime_end_time);
            date = v.findViewById(R.id.rvi_overtime_date);
            overtime_status = v.findViewById(R.id.rvi_overtime_status);
            rvi_overtime_request_details = v.findViewById(R.id.rvi_overtime_request_details);
            rvi_schedule_from = v.findViewById(R.id.rvi_schedule_from);
            rvi_schedule_to = v.findViewById(R.id.rvi_schedule_to);
        }
    }

    @Override
    public RequestedOvertimesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
        ctx = parent.getContext();
        helper = Helper.getInstance(ctx);

        User user = SharedPrefManager.getInstance(ctx).getUser();
        employee_schedule_start = Helper.getInstance(ctx).convertToReadableTime(user.getSchedule_shift_in());
        employee_schedule_end = Helper.getInstance(ctx).convertToReadableTime(user.getSchedule_shift_out());

        // create a new view
        CardView v = (CardView) LayoutInflater.from(ctx)
                .inflate(R.layout.recyclerview_request_overtime, parent, false);
        RequestedOvertimesAdapter.MyViewHolder vh = new RequestedOvertimesAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final RequestedOvertimesAdapter.MyViewHolder holder, int i) {

        final Overtime overtime = mDataset.get(i);

        String employee_id = overtime.getUser_id();
        String date = overtime.getDate();
        String start_time = overtime.getStart_time();
        String end_time = overtime.getEnd_time();
        String status = overtime.getStatus();
        String reason = overtime.getReason();

        String requested_at = overtime.getRequested_at();
        String fname = overtime.getFname();
        String lname = overtime.getLname();
        String name = fname + " " + lname;

        //Requested at
        //holder.requested_at.setText(status);

        holder.rvi_schedule_from.setText(employee_schedule_start);
        holder.rvi_schedule_to.setText(employee_schedule_end);

        holder.start_time.setText(helper.convertToReadableTime(start_time));
        holder.end_time.setText(helper.convertToReadableTime(end_time));
        holder.date.setText(helper.convertToReadableDate(date));

        int color;

        switch (status) {

            case "approved":
                color = ContextCompat.getColor(ctx, R.color.colorSuccess);
                break;

            case "declined":
                color = ContextCompat.getColor(ctx, R.color.colorError);
                break;

            case "pending":
                color = ContextCompat.getColor(ctx, R.color.colorPending);
                break;

            default:
                color = ContextCompat.getColor(ctx, R.color.colorWhiteSmoke);
        }

        holder.overtime_status.setBackgroundColor(color);

        holder.rvi_overtime_request_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Change Show
                Intent intent = new Intent(ctx, ShowOvertimeDetails.class);

                JSONObject overtime_request_json = new JSONObject();
                try {
                    overtime_request_json.put("overtime_id", overtime.getId());
                    overtime_request_json.put("user_id", overtime.getUser_id());
                    overtime_request_json.put("date", overtime.getDate());
                    overtime_request_json.put("start_time", overtime.getStart_time());
                    overtime_request_json.put("end_time", overtime.getEnd_time());
                    overtime_request_json.put("status", overtime.getStatus());
                    overtime_request_json.put("reason", overtime.getReason());
                    overtime_request_json.put("checked_by", overtime.getChecked_by());
                    overtime_request_json.put("checked_at", overtime.getChecked_at());
                    overtime_request_json.put("fname", overtime.getFname());
                    overtime_request_json.put("lname", overtime.getLname());
                    overtime_request_json.put("updated_at", overtime.getUpdated_at());
                    overtime_request_json.put("created_at", overtime.getChecked_by());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                intent.putExtra("overtime_request", overtime_request_json.toString());
                ctx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void resetAdapter() {
        mDataset.clear();
        filteredList.clear();
        mDataset.addAll(tempList);
    }

    public Boolean filterByStatus(String status) {
        resetAdapter();

        for (Overtime overtime : mDataset) {

            if (overtime.getStatus().equals(status)) {
                filteredList.add(overtime);
            }
        }

        mDataset.clear();
        if (filteredList.size() > 0) {
            mDataset.addAll(filteredList);
            notifyDataSetChanged();
            return true;
        } else {
            notifyDataSetChanged();
            return false;
        }
    }

    public Boolean showAll() {
        resetAdapter();
        notifyDataSetChanged();

        return mDataset.size() > 0;
    }

}


