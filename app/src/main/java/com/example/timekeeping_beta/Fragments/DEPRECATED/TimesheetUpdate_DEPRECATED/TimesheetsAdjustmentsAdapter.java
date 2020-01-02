package com.example.timekeeping_beta.Fragments.DEPRECATED.TimesheetUpdate_DEPRECATED;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timekeeping_beta.Fragments.UserApprover.Approvee.Models.Profile;
import com.example.timekeeping_beta.Fragments.UserApprover.EDTR.TimesheetAdjustment;
import com.example.timekeeping_beta.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static android.view.View.GONE;

public class TimesheetsAdjustmentsAdapter extends RecyclerView.Adapter<TimesheetsAdjustmentsAdapter.MyViewHolder> {

    private List<TimesheetAdjustment> mDataset;
    private List<TimesheetAdjustment> tempList;
    private List<TimesheetAdjustment> filteredList;

    private Context ctx;
    private Dialog timesheetApprovalDetails;

    // Provide a suitable constructor (depends on the kind of dataset)
    public TimesheetsAdjustmentsAdapter(List<TimesheetAdjustment> myDataset) {

        this.mDataset = myDataset;
        this.tempList = new ArrayList<>();
        tempList.addAll(mDataset);

        this.filteredList = new ArrayList<>();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name;
        public TextView schedule;
        public TextView time_in;
        public TextView time_out;
        public TextView date;
        public FrameLayout adjustment_status;
        public CardView timesheet_adjustment;

        public MyViewHolder(LinearLayout v) {
            super(v);
            name = v.findViewById(R.id.rvi_employee_name);
            schedule = v.findViewById(R.id.rvi_employee_schedule);
            time_in = v.findViewById(R.id.rvi_timesheet_adjustment_time_in);
            time_out = v.findViewById(R.id.rvi_timesheet_adjustment_time_out);
            date = v.findViewById(R.id.rvi_timesheet_adjustment_date);
            adjustment_status = v.findViewById(R.id.rvi_timesheet_adjustment_status);
            timesheet_adjustment = v.findViewById(R.id.rvi_timesheet_adjustment);
        }
    }

    @Override
    public TimesheetsAdjustmentsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(ctx).inflate(R.layout.recyclerview_approver_action_timesheet, parent, false);
        final TimesheetsAdjustmentsAdapter.MyViewHolder vh = new TimesheetsAdjustmentsAdapter.MyViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NotNull final TimesheetsAdjustmentsAdapter.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        TimesheetAdjustment adjustment = mDataset.get(position);

        Profile profile = adjustment.getProfile();
        final String user_id = adjustment.getUser_id();

        String fname = profile.getFname();
        String lname = profile.getLname();

        String shift_in = adjustment.getShift_in();
        String shift_out = adjustment.getShift_out();

        final String full_name = fname + " " + lname;
        String schedule = shift_in + "-" + shift_out;

        String time_in = "Time in: " + adjustment.getTime_in();
        String time_out = "Time out: " + adjustment.getTime_out();

        String date = adjustment.getDate_in();

        final String status = adjustment.getStatus();
        final String reason = !adjustment.getReason().equals("null") ? adjustment.getReason() : "No reason." ;

        holder.name.setText(full_name);
        holder.time_in.setText(time_in);
        holder.time_out.setText(time_out);
        holder.schedule.setText(schedule);
        holder.date.setText(date);

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

        holder.adjustment_status.setBackgroundColor(color);

        timesheetApprovalDetails = new Dialog(ctx,R.style.Dialog_Fullscreen_with_Animation);

        //int layout = status == "pending" ? R.layout.dialog_approver_action : R.layout.dialog_approver_read_only;

        timesheetApprovalDetails.setContentView(R.layout.dialog_approver_action);

        holder.timesheet_adjustment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LinearLayout time_adjustment_info = timesheetApprovalDetails.findViewById(R.id.time_adjustment_info);
                time_adjustment_info.setVisibility(View.VISIBLE);

                TextView modal_employee_name = timesheetApprovalDetails.findViewById(R.id.modal_employee_name);
                TextView modal_date = timesheetApprovalDetails.findViewById(R.id.modal_date);
                TextView modal_schedule	 = timesheetApprovalDetails.findViewById(R.id.modal_schedule);
                TextView otd_time_in = timesheetApprovalDetails.findViewById(R.id.otd_time_in);
                TextView otd_time_out = timesheetApprovalDetails.findViewById(R.id.otd_time_out);
                TextView otd_date_type = timesheetApprovalDetails.findViewById(R.id.otd_date_type);
                TextView rtd_time_in = timesheetApprovalDetails.findViewById(R.id.rtd_time_in);
                TextView rtd_time_out = timesheetApprovalDetails.findViewById(R.id.rtd_time_out);
                TextView rtd_date_type = timesheetApprovalDetails.findViewById(R.id.rtd_date_type);
                TextView modal_reason = timesheetApprovalDetails.findViewById(R.id.modal_reason);

                Button approve = timesheetApprovalDetails.findViewById(R.id.modal_approve_button);
                Button decline = timesheetApprovalDetails.findViewById(R.id.modal_decline_button);

                modal_employee_name.setText(full_name);
                modal_reason.setText(reason);

                if(status.equals("pending")){

                    approve.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        Toasty.success(ctx,"fsndjfsdjfns", Toast.LENGTH_SHORT).show();
                        }
                    });

                    decline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        Toasty.error(ctx,"dsndfkjdsbf", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    approve.setVisibility(GONE);
                    decline.setVisibility(GONE);
                }

                timesheetApprovalDetails.show();
            }
        });

        ImageButton close = timesheetApprovalDetails.findViewById(R.id.img_dialog_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timesheetApprovalDetails.dismiss();
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

        for (TimesheetAdjustment timesheetAdjustment : mDataset) {

            if (timesheetAdjustment.getStatus().equals(status)) {
                filteredList.add(timesheetAdjustment);
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
