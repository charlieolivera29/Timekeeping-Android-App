package com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestAdjustment.AdjustmentAdapter;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.timekeeping_beta.R;
import com.example.timekeeping_beta.Fragments.UserEmployee.Adjustments.Models.AdjustmentRequestItem;
import com.example.timekeeping_beta.Globals.Helper;

import java.util.List;

public class AdjustmentAdapter extends RecyclerView.Adapter<AdjustmentAdapter.AdjustmentRequestViewHolder> {

    private Context context;
    private List<AdjustmentRequestItem> adjustmentRequestItemList;
    private Dialog timeSheetAdjustment;

    private Helper helper;

    public AdjustmentAdapter(Context i_context, List<AdjustmentRequestItem> i_adjustmentRequestItemList) {
        context = i_context;
        adjustmentRequestItemList = i_adjustmentRequestItemList;

        helper = Helper.getInstance(context);

        timeSheetAdjustment = new Dialog(context, R.style.AppTheme_NoActionBar);
        timeSheetAdjustment.setContentView(R.layout.dialog_timesheet_adjustment);
    }

    @NonNull
    @Override
    public AdjustmentRequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_request_adjustment, viewGroup, false);
        final AdjustmentRequestViewHolder adjustmentView = new AdjustmentRequestViewHolder(v);

        return new AdjustmentRequestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdjustmentRequestViewHolder adjustmentRequestViewHolder, int i) {
        final AdjustmentRequestItem currentItem = adjustmentRequestItemList.get(i);

        //adjustmentRequestViewHolder.date.setText(helper.convertToReadableDate(currentItem.getDate()));
        adjustmentRequestViewHolder.date.setText(currentItem.getDate());

        adjustmentRequestViewHolder.timein.setText(helper.convertToReadableTime(currentItem.getRequested_time_in()));
        adjustmentRequestViewHolder.timeout.setText(helper.convertToReadableTime(currentItem.getRequested_time_out()));
        adjustmentRequestViewHolder.shiftIn.setText(helper.convertToReadableTime(currentItem.getShift_in()));
        adjustmentRequestViewHolder.shiftOut.setText(helper.convertToReadableTime(currentItem.getShift_out()));
        adjustmentRequestViewHolder.day_type.setText(currentItem.getRequested_day_type());

        if (currentItem.getStatus().equals("approved")) {
            adjustmentRequestViewHolder.rvi_status.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSuccess));
        } else if (currentItem.getStatus().equals("declined")) {
            adjustmentRequestViewHolder.rvi_status.setBackgroundColor(ContextCompat.getColor(context, R.color.colorError));
        } else {
            adjustmentRequestViewHolder.rvi_status.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPending));
        }

        // RecyclerViewButton
        adjustmentRequestViewHolder.timesheet_adjustment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TextView adjusment_date = timeSheetAdjustment.findViewById(R.id.adjusment_date);
                TextView user_grace_period = timeSheetAdjustment.findViewById(R.id.user_grace_period);
                TextView user_shift_in = timeSheetAdjustment.findViewById(R.id.user_shift_in);
                TextView user_shift_out = timeSheetAdjustment.findViewById(R.id.user_shift_out);
                TextView adjustment_reference = timeSheetAdjustment.findViewById(R.id.adjustment_reference);
                TextView adjusted_original_time_in = timeSheetAdjustment.findViewById(R.id.adjusted_original_time_in);
                TextView adjusted_original_time_out = timeSheetAdjustment.findViewById(R.id.adjusted_original_time_out);
                TextView adjusted_original_day_type = timeSheetAdjustment.findViewById(R.id.adjusted_original_day_type);
                TextView adjusted_requested_time_in = timeSheetAdjustment.findViewById(R.id.adjusted_requested_time_in);
                TextView adjusted_requested_time_out = timeSheetAdjustment.findViewById(R.id.adjusted_requested_time_out);
                TextView adjusted_requested_day_type = timeSheetAdjustment.findViewById(R.id.adjusted_requested_day_type);
                TextView adjustment_reason = timeSheetAdjustment.findViewById(R.id.adjustment_reason);


                adjusment_date.setText(helper.convertToReadableDate(currentItem.getDate()));
                //user_grace_period.setText();
                user_shift_in.setText(helper.convertToReadableTime(currentItem.getShift_in()));
                user_shift_out.setText(helper.convertToReadableTime(currentItem.getShift_out()));
                user_grace_period.setText(currentItem.getGrace_period());
                adjustment_reference.setText(currentItem.getReference());

                adjusted_original_time_in.setText(helper.convertToReadableTime(currentItem.getTime_in()));
                adjusted_original_time_out.setText(helper.convertToReadableTime(currentItem.getTime_out()));
                adjusted_original_day_type.setText(currentItem.getDay_type());

                adjusted_requested_time_in.setText(helper.convertToReadableTime(currentItem.getRequested_time_in()));
                adjusted_requested_time_out.setText(helper.convertToReadableTime(currentItem.getRequested_time_out()));
                adjusted_requested_day_type.setText(currentItem.getRequested_day_type());

                adjustment_reason.setText(currentItem.getReason());

                timeSheetAdjustment.show();
            }
        });

        // CloseButton
        ImageButton close = timeSheetAdjustment.findViewById(R.id.img_fullscreen_dialog_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeSheetAdjustment.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return adjustmentRequestItemList.size();
    }

    public static class AdjustmentRequestViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout timesheet_adjustment;
        private TextView date;
        private TextView timein;
        private TextView timeout;
        private TextView shiftIn;
        private TextView shiftOut;
        private TextView day_type;
        private FrameLayout rvi_status;

        public AdjustmentRequestViewHolder(View itemView) {
            super(itemView);
            timesheet_adjustment = itemView.findViewById(R.id.rv_timesheet_adjustment);
            date = itemView.findViewById(R.id.adjustment_request_date);
            timein = itemView.findViewById(R.id.adjustment_request_timein);
            timeout = itemView.findViewById(R.id.adjustment_request_timeout);
            shiftIn = itemView.findViewById(R.id.adjustment_shift_in_schedule);
            shiftOut = itemView.findViewById(R.id.adjustment_shift_out_schedule);
            day_type = itemView.findViewById(R.id.adjustment_request_day_type);
            rvi_status = itemView.findViewById(R.id.rvi_status);
        }
    }
}
