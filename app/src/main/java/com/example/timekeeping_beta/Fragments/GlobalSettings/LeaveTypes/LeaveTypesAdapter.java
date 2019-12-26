package com.example.timekeeping_beta.Fragments.GlobalSettings.LeaveTypes;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.timekeeping_beta.Fragments.GlobalSettings.HolidayTypes.HolidayType;
import com.example.timekeeping_beta.Globals.CustomClasses.Flag;
import com.example.timekeeping_beta.Globals.CustomClasses.Interface.RecyclerViewClickListener;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.Globals.Models.User;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.R;

import java.util.ArrayList;
import java.util.List;

public class LeaveTypesAdapter extends RecyclerView.Adapter<LeaveTypesAdapter.MyViewHolder> {

    private List<LeaveType> mDataset;
    private List<LeaveType> tempList;
    private List<HolidayType> filteredList;

    private Context ctx;

    private Helper helper;
    private URLs url;
    private User user;

    private RecyclerViewClickListener onItemClickListener;

    public LeaveTypesAdapter(List<LeaveType> myDataset, RecyclerViewClickListener onItemClickListener) {

        this.mDataset = myDataset;
        this.tempList = new ArrayList<>();
        tempList.addAll(mDataset);

        this.filteredList = new ArrayList<>();
        this.onItemClickListener = onItemClickListener;
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

        private RecyclerViewClickListener onItemClickListener;

        private FrameLayout rvi_holiday_type_container;
        private TextView rvi_holiday_type_id;
        private TextView rvi_holiday_type_name;
        private ImageView rvi_edit;

        public MyViewHolder(@NonNull View v, final RecyclerViewClickListener onItemClickListener) {
            super(v);
            rvi_holiday_type_container = v.findViewById(R.id.rvi_holiday_type_container);
            rvi_holiday_type_id = v.findViewById(R.id.rvi_holiday_type_id);
            rvi_holiday_type_name = v.findViewById(R.id.rvi_holiday_type_name);
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
                .inflate(R.layout.recyclerview_global_settings_type, parent, false);

        final MyViewHolder vh = new MyViewHolder(v,onItemClickListener);

        return vh;
    }

    @Override
    public void onBindViewHolder(final LeaveTypesAdapter.MyViewHolder holder, final int position) {

        final LeaveType leaveType = mDataset.get(position);

        holder.rvi_holiday_type_id.setText(leaveType.getLeave_type_code());
        holder.rvi_holiday_type_name.setText(leaveType.getLeave_type_name());
    }

    @Override
    public int getItemCount() { return mDataset.size();  }

}
