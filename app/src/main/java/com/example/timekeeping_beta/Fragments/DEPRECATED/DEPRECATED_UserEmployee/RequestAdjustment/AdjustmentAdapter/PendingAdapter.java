package com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestAdjustment.AdjustmentAdapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.R;
import com.example.timekeeping_beta.Fragments.UserEmployee.Adjustments.Models.AdjustmentRequestItem;

import java.util.List;

public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.AdjustmentRequestViewHolder> {


    private Context ctx;
    private OnItemClickListener onItemClickListener;
    private List<AdjustmentRequestItem> adjustmentRequestItemList;

    public PendingAdapter(List<AdjustmentRequestItem> adjustmentRequestItemList, OnItemClickListener onItemClickListener) {
        this.adjustmentRequestItemList = adjustmentRequestItemList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public AdjustmentRequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_request_adjustment, viewGroup, false);

        ctx = viewGroup.getContext();
        return new AdjustmentRequestViewHolder(v, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AdjustmentRequestViewHolder adjustmentRequestViewHolder, int position) {

        Helper helper = Helper.getInstance(ctx);

        AdjustmentRequestItem currentItem = adjustmentRequestItemList.get(position);
        adjustmentRequestViewHolder.date.setText(currentItem.getDate());
        adjustmentRequestViewHolder.timein.setText(helper.convertToReadableTime(currentItem.getRequested_time_in()));
        adjustmentRequestViewHolder.timeout.setText(helper.convertToReadableTime(currentItem.getRequested_time_out()));
        adjustmentRequestViewHolder.shiftIn.setText(currentItem.getShift_in());
        adjustmentRequestViewHolder.shiftOut.setText(currentItem.getShift_out());
        adjustmentRequestViewHolder.day_type.setText(currentItem.getDay_type());


        //adjustmentRequestViewHolder.rvi_status.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPending));

        if (currentItem.getStatus().equals("approved")) {
            adjustmentRequestViewHolder.rvi_status.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorSuccess));
        } else if (currentItem.getStatus().equals("declined")) {
            adjustmentRequestViewHolder.rvi_status.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorError));
        } else {
            adjustmentRequestViewHolder.rvi_status.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorPending));
        }
    }

    @Override
    public int getItemCount() {
        return adjustmentRequestItemList.size();
    }


    public class AdjustmentRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public OnItemClickListener onItemClickListener;
        private TextView date;
        private TextView timein;
        private TextView timeout;
        private TextView shiftIn;
        private TextView shiftOut;
        private TextView day_type;
        private FrameLayout rvi_status;

        public AdjustmentRequestViewHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            date = itemView.findViewById(R.id.adjustment_request_date);
            timein = itemView.findViewById(R.id.adjustment_request_timein);
            timeout = itemView.findViewById(R.id.adjustment_request_timeout);
            shiftIn = itemView.findViewById(R.id.adjustment_shift_in_schedule);
            shiftOut = itemView.findViewById(R.id.adjustment_shift_out_schedule);
            day_type = itemView.findViewById(R.id.adjustment_request_day_type);
            rvi_status = itemView.findViewById(R.id.rvi_status);

            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}
