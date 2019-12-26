package com.example.timekeeping_beta.Fragments.GlobalSettings.ScheduleManagement;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.timekeeping_beta.Globals.CustomClasses.Flag;
import com.example.timekeeping_beta.Globals.CustomClasses.Interface.RecyclerViewClickListener;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.Globals.Models.User;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.MyViewHolder> {

    private List<Schedule> mDataset;
    private List<Schedule> tempList;
    private List<Schedule> filteredList;

    private Context ctx;

    private Helper helper;
    private URLs url;
    private User user;

    private RecyclerViewClickListener onItemClickListener;

    public ScheduleAdapter(List<Schedule> myDataset, RecyclerViewClickListener onItemClickListener) {

        this.mDataset = myDataset;
        this.tempList = new ArrayList<>();
        tempList.addAll(mDataset);

        this.filteredList = new ArrayList<>();
        this.onItemClickListener = onItemClickListener;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RecyclerViewClickListener onItemClickListener;

        private FrameLayout rvi_holiday_type_container;
        private TextView rvi_sched_name;
        private TextView rvi_sched_days;
        private TextView rvi_sched_shift_in;
        private TextView rvi_sched_shift_out;
        private ImageView rvi_edit;

        public MyViewHolder(@NonNull View v, final RecyclerViewClickListener onItemClickListener) {
            super(v);
            rvi_edit = v.findViewById(R.id.rvi_edit);
            rvi_sched_name = v.findViewById(R.id.rvi_sched_name);
            rvi_sched_days = v.findViewById(R.id.rvi_sched_days);
            rvi_sched_shift_in = v.findViewById(R.id.rvi_sched_shift_in);
            rvi_sched_shift_out = v.findViewById(R.id.rvi_sched_shift_out);

            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
            rvi_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(getAdapterPosition(), Flag.CALLBACK_EDIT);
                }
            });
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(getAdapterPosition(), Flag.CALLBACK_SHOW);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        ctx = parent.getContext();
        helper = Helper.getInstance(ctx);
        url = new URLs();
        user = SharedPrefManager.getInstance(ctx).getUser();

        // create a new view
        CardView v = (CardView) LayoutInflater.from(ctx)
                .inflate(R.layout.recyclerview_global_settings_schedule, parent, false);

        final MyViewHolder vh = new MyViewHolder(v, onItemClickListener);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ScheduleAdapter.MyViewHolder holder, final int position) {

        final Schedule schedule = mDataset.get(position);

        String days = "";
        JSONArray ja_days = schedule.getDay();

        if (ja_days.length() > 0) {
            for (int i = 0; i < ja_days.length(); i++) {
                try {
                    JSONObject jo = ja_days.getJSONObject(i);
                    int jo_type = jo.getInt("type");
                    if(jo_type == 1){
                        days = days  + jo.getString("day") + ",";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }else {
            days = "No Schedule";
        }


        holder.rvi_sched_name.setText(schedule.getSched_name());
        holder.rvi_sched_days.setText(days.substring(0, days.length() - 1));
        holder.rvi_sched_shift_in.setText(helper.convertToReadableTime(schedule.getShift_in()));
        holder.rvi_sched_shift_out.setText(helper.convertToReadableTime(schedule.getShift_out()));

    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
