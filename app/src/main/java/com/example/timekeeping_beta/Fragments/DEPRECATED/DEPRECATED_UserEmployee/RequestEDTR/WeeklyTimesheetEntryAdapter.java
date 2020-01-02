package com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestEDTR;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.timekeeping_beta.Globals.CustomClasses.Flag;
import com.example.timekeeping_beta.Globals.CustomClasses.Interface.RecyclerViewClickListener;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.R;

import java.util.List;

public class WeeklyTimesheetEntryAdapter extends RecyclerView.Adapter<WeeklyTimesheetEntryAdapter.TimesheetViewHolder> {

    private Context context;
    private Helper helper;
    private static List<TimesheetEntry> timesheetEntryList;
    private RecyclerViewClickListener onClickListener;

    public WeeklyTimesheetEntryAdapter(Context context, List<TimesheetEntry> timesheetList, RecyclerViewClickListener onClickListener) {
        this.context = context;
        this.helper = Helper.getInstance(context);
        timesheetEntryList = timesheetList;
        this.onClickListener = onClickListener;
    }

    public static class TimesheetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RecyclerViewClickListener onItemClickListener;

        private CardView timesheet_details;
        private FrameLayout timesheet_status;
        private TextView date, timein, timeout, day_type, tv_month_output, tv_day_of_week_output;
        private LinearLayout legendLayout;
        private AppCompatImageView ib_button_edit, ib_button_delete;

        public TimesheetViewHolder(View itemView, final RecyclerViewClickListener onItemClickListener) {
            super(itemView);
            timesheet_details = itemView.findViewById(R.id.rv_timesheet);
            timesheet_status = itemView.findViewById(R.id.timesheet_status);
            date = itemView.findViewById(R.id.tv_date_output);
            timein = itemView.findViewById(R.id.tv_time_in_output);
            timeout = itemView.findViewById(R.id.tv_time_out_output);
            day_type = itemView.findViewById(R.id.tv_day_type);
            tv_month_output = itemView.findViewById(R.id.tv_month_output);
            tv_day_of_week_output = itemView.findViewById(R.id.tv_day_of_week_output);
            legendLayout = itemView.findViewById(R.id.legendLayout);
            ib_button_edit = itemView.findViewById(R.id.ib_button_edit);
            ib_button_delete = itemView.findViewById(R.id.ib_button_delete);

            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public void onClick(View view) {
            //onItemClickListener.onItemClick(getAdapterPosition(), Flag.CALLBACK_SHOW);
        }
    }

    @NonNull
    @Override
    public WeeklyTimesheetEntryAdapter.TimesheetViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_timesheet_entry, viewGroup, false);
        final WeeklyTimesheetEntryAdapter.TimesheetViewHolder timesheetView = new WeeklyTimesheetEntryAdapter.TimesheetViewHolder(v, onClickListener);

        return timesheetView;
    }

    @Override
    public void onBindViewHolder(@NonNull final WeeklyTimesheetEntryAdapter.TimesheetViewHolder timesheetViewHolder, final int i) {

        final TimesheetEntry te = timesheetEntryList.get(i);
        timesheetViewHolder.timesheet_details.setTag(i);


        timesheetViewHolder.date.setText(helper.convertToFormattedTime(te.getDate_in(), "yyyy-MM-dd", "dd"));
        timesheetViewHolder.tv_day_of_week_output.setText(helper.convertToFormattedTime(te.getDate_in(), "yyyy-MM-dd", "EEE"));

        timesheetViewHolder.timein.setText(helper.convertToReadableTime(te.getTime_in()));
        timesheetViewHolder.timeout.setText(helper.convertToReadableTime(te.getTime_out()));
        timesheetViewHolder.day_type.setText(te.getDay_type());

        timesheetViewHolder.timesheet_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onItemClick(i, Flag.CALLBACK_SHOW);
            }
        });

        if (helper.dateHasPassedOrToday(te.getDate_in())) {

            if (te.getStatus().equals("pending")) {

                timesheetViewHolder.timesheet_status.setBackgroundColor(context.getResources().getColor(R.color.colorPending));

                timesheetViewHolder.ib_button_edit.setVisibility(View.VISIBLE);
                timesheetViewHolder.ib_button_delete.setVisibility(View.VISIBLE);

                timesheetViewHolder.ib_button_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickListener.onItemClick(i, Flag.CALLBACK_DELETE);
                    }
                });

                timesheetViewHolder.ib_button_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (te.getTime_in().equals("null") ||
                                te.getTime_out().equals("null")) {
                            onClickListener.onItemClick(i, Flag.CALLBACK_CREATE);
                        } else {
                            onClickListener.onItemClick(i, Flag.CALLBACK_EDIT);
                        }

                    }
                });

            } else if (te.getStatus().equals("approved")) {

                timesheetViewHolder.timesheet_status.setBackgroundColor(context.getResources().getColor(R.color.colorSuccess));


                timesheetViewHolder.ib_button_edit.setVisibility(View.GONE);
                timesheetViewHolder.ib_button_delete.setVisibility(View.GONE);
            } else if (te.getStatus().equals("null")) {
                timesheetViewHolder.timesheet_status.setBackgroundColor(context.getResources().getColor(R.color.colorError));

                timesheetViewHolder.ib_button_edit.setVisibility(View.VISIBLE);
                timesheetViewHolder.ib_button_delete.setVisibility(View.GONE);

                timesheetViewHolder.ib_button_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (te.getTime_in().equals("null") ||
                                te.getTime_out().equals("null")) {
                            onClickListener.onItemClick(i, Flag.CALLBACK_CREATE);
                        } else {
                            onClickListener.onItemClick(i, Flag.CALLBACK_EDIT);
                        }

                    }
                });
            }
        } else {

            timesheetViewHolder.timesheet_status.setBackgroundColor(context.getResources().getColor(R.color.colorGray));

            timesheetViewHolder.ib_button_edit.setVisibility(View.GONE);
            timesheetViewHolder.ib_button_delete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return timesheetEntryList.size();
    }
}
