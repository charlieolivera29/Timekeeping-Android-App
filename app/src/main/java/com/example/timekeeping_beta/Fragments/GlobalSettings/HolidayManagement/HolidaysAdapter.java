package com.example.timekeeping_beta.Fragments.GlobalSettings.HolidayManagement;

import android.content.Context;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.List;

public class HolidaysAdapter extends RecyclerView.Adapter<HolidaysAdapter.MyViewHolder> {

    private List<Holiday> mDataset;
    private List<Holiday> tempList;
    private List<Holiday> filteredList;

    private Context ctx;

    private Helper helper;
    private URLs url;
    private User user;

    private RecyclerViewClickListener onItemClickListener;

    public HolidaysAdapter(List<Holiday> myDataset, RecyclerViewClickListener onItemClickListener) {

        this.mDataset = myDataset;
        this.tempList = new ArrayList<>();
        tempList.addAll(mDataset);

        this.filteredList = new ArrayList<>();
        this.onItemClickListener = onItemClickListener;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

        private RecyclerViewClickListener onItemClickListener;

        private FrameLayout rvi_holiday_container;
        private TextView rvi_holiday_id;
        private TextView rvi_holiday_name;
        private TextView rvi_holiday_date;
        private TextView rvi_holiday_description;
        private ImageView rvi_edit;

        public MyViewHolder(@NonNull View v, final RecyclerViewClickListener onItemClickListener) {
            super(v);
            rvi_holiday_container = v.findViewById(R.id.rvi_holiday_container);
            rvi_holiday_id = v.findViewById(R.id.rvi_holiday_id);
            rvi_holiday_name = v.findViewById(R.id.rvi_holiday_name);
            rvi_holiday_date = v.findViewById(R.id.rvi_holiday_date);
            rvi_holiday_description = v.findViewById(R.id.rvi_holiday_description);
            rvi_edit = v.findViewById(R.id.rvi_edit);

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
        FrameLayout v = (FrameLayout) LayoutInflater.from(ctx)
                .inflate(R.layout.recyclerview_global_settings_holiday, parent, false);

        final MyViewHolder vh = new MyViewHolder(v,onItemClickListener);

        return vh;
    }

    @Override
    public void onBindViewHolder(final HolidaysAdapter.MyViewHolder holder, final int position) {

        final Holiday holiday = mDataset.get(position);

        holder.rvi_holiday_id.setText(holiday.getHoliday_type());
        holder.rvi_holiday_name.setText(holiday.getHoliday_name());
        holder.rvi_holiday_date.setText(holiday.getHoliday_date());
        holder.rvi_holiday_description.setText(holiday.getHoliday_remarks());
    }

    @Override
    public int getItemCount() { return mDataset.size();  }
}
