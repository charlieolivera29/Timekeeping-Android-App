package com.example.timekeeping_beta.Fragments.GlobalSettings.LocationManagement;

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

public class CompanyLocationsAdapter extends RecyclerView.Adapter<CompanyLocationsAdapter.MyViewHolder> {

    private Context ctx;
    private Helper helper;
    private URLs url;
    private User user;

    private List<CompanyLocation> mDataset;
    private List<CompanyLocation> tempList;
    private List<CompanyLocation> filteredList;

    private CompanyLocationsAdapter.OnItemClickListener onItemClickListener;

    public CompanyLocationsAdapter(List<CompanyLocation> myDataset, CompanyLocationsAdapter.OnItemClickListener onItemClickListener) {

        this.mDataset = myDataset;
        this.tempList = new ArrayList<>();
        tempList.addAll(mDataset);

        this.filteredList = new ArrayList<>();
        this.onItemClickListener = onItemClickListener;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private OnItemClickListener onItemClickListener;
        private FrameLayout rvi_holiday_type_container;
        private TextView rvi_location_name;
        private TextView rvi_bundee;
        private TextView rvi_schedule;
        private ImageView rvi_iv_edit;

        public MyViewHolder(@NonNull View v, CompanyLocationsAdapter.OnItemClickListener onItemClickListener) {
            super(v);
            rvi_location_name = v.findViewById(R.id.rvi_location_name);
            rvi_bundee = v.findViewById(R.id.rvi_bundee);
            rvi_schedule = v.findViewById(R.id.rvi_schedule);
            rvi_iv_edit = v.findViewById(R.id.rvi_iv_edit);

            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(getAdapterPosition());
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
                .inflate(R.layout.recyclerview_global_settings_location, parent, false);

        final MyViewHolder vh = new MyViewHolder(v, onItemClickListener);

        return vh;
    }

    @Override
    public void onBindViewHolder(final CompanyLocationsAdapter.MyViewHolder holder, final int position) {

        final CompanyLocation cl = mDataset.get(position);

        holder.rvi_location_name.setText(cl.getBranch_name());

        JSONArray ja = cl.getTimetrack();
        String bundees = "";
        if (ja.length() > 0) {

            for (int i = 0; i < ja.length(); i++) {

                try {
                    JSONObject jo = ja.getJSONObject(i);
                    bundees = bundees + jo.getString("bundee") + ",";
                } catch (JSONException e) {
                    e.printStackTrace();
                    bundees = "Error";
                }
            }
        } else {
            bundees = "No Bundee";
        }

        holder.rvi_bundee.setText(bundees);

        JSONArray ja_sched = cl.getSchedule();
        String scheds = "";
        if (ja_sched.length() > 0) {

            for (int i = 0; i < ja_sched.length(); i++) {

                try {
                    JSONObject jo = ja_sched.getJSONObject(i);
                    scheds = scheds + jo.getString("schedule_name") + ",";
                } catch (JSONException e) {
                    e.printStackTrace();
                    scheds = "Error";
                }
            }
        } else {
            scheds = "No Schedule";
        }
        holder.rvi_schedule.setText(scheds);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
