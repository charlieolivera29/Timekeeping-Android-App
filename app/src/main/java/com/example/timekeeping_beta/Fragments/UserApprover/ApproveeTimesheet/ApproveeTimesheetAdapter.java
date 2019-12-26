package com.example.timekeeping_beta.Fragments.UserApprover.ApproveeTimesheet;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.timekeeping_beta.Fragments.GlobalSettings.LeaveTypes.LeaveType;
import com.example.timekeeping_beta.Fragments.GlobalSettings.LeaveTypes.LeaveTypesViewModel;
import com.example.timekeeping_beta.Fragments.Timesheet.TimesheetItem;
import com.example.timekeeping_beta.Fragments.UserEmployee.RequestAdjustment.AdjusmentFragments.RequestFragment;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.R;

import java.util.List;

import static android.view.View.GONE;

public class ApproveeTimesheetAdapter extends RecyclerView.Adapter<ApproveeTimesheetAdapter.TimesheetViewHolder> {

    private Context context;
    private Helper helper;
    private List<TimesheetItem> timesheetList;
    private Dialog timeSheetDetails;
    private LinearLayout timesheetnavigattion;
    private LeaveTypesViewModel leaveTypesViewModel;
    private List<LeaveType> LeaveTypes;

    public ApproveeTimesheetAdapter(Context context, List<TimesheetItem> timesheetList) {
        this.context = context;
        this.helper = Helper.getInstance(context);

        timesheetnavigattion = ((FragmentActivity) context).findViewById(R.id.timesheetnavigattion);
        this.timesheetList = timesheetList;

        leaveTypesViewModel = ViewModelProviders.of((FragmentActivity) context).get(LeaveTypesViewModel.class);
    }

    @NonNull
    @Override
    public TimesheetViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_details_timesheet, viewGroup, false);
        final TimesheetViewHolder timesheetView = new TimesheetViewHolder(v);

        timeSheetDetails = new Dialog(context);
        timeSheetDetails.setContentView(R.layout.dialog_timesheet_details);

        leaveTypesViewModel.retrieveAllLeaveTypes();

        if (leaveTypesViewModel.getLeaveTypes().getValue() != null) {
            LeaveTypes = leaveTypesViewModel.getLeaveTypes().getValue();
        } else {
            leaveTypesViewModel.getLeaveTypes().observe((FragmentActivity) context, new Observer<List<LeaveType>>() {
                @Override
                public void onChanged(@Nullable List<LeaveType> i_leaveTypes) {
                    LeaveTypes = i_leaveTypes;
                }
            });
        }

        return timesheetView;
    }


    public static class TimesheetViewHolder extends RecyclerView.ViewHolder {

        private CardView timesheet_details;
        private FrameLayout timesheet_status;
        private TextView date;
        private TextView timein;
        private TextView timeout;
        private TextView day_type;
        private TextView tv_month_output;
        private TextView tv_day_of_week_output;
        private LinearLayout legendLayout;

        public TimesheetViewHolder(View itemView) {
            super(itemView);
            timesheet_details = itemView.findViewById(R.id.rv_timesheet);
            timesheet_status = itemView.findViewById(R.id.timesheet_status);
            date = itemView.findViewById(R.id.tv_date_output);
            timein = itemView.findViewById(R.id.tv_time_in_output);
            timeout = itemView.findViewById(R.id.tv_time_out_output);
            day_type = itemView.findViewById(R.id.tv_schedule_output);
            tv_month_output = itemView.findViewById(R.id.tv_month_output);
            tv_day_of_week_output = itemView.findViewById(R.id.tv_day_of_week_output);
            legendLayout = itemView.findViewById(R.id.legendLayout);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull TimesheetViewHolder timesheetViewHolder, int i) {

        timesheetViewHolder.timesheet_details.setTag(i);
        final TimesheetItem currentItem = timesheetList.get(i);

        if (!timesheetList.get(i).getTime_in().equals("--:-- --") && !timesheetList.get(i).getTime_out().equals("--:-- --")) {
            timesheetViewHolder.timesheet_status.setBackgroundColor(context.getResources().getColor(R.color.colorTimesheetComplete));
        } else if (!timesheetList.get(i).getTime_in().equals("--:-- --") && timesheetList.get(i).getTime_out().equals("--:-- --")) {
            timesheetViewHolder.timesheet_status.setBackgroundColor(context.getResources().getColor(R.color.colorTimesheetIncomplete));
        } else {
            timesheetViewHolder.timesheet_status.setBackgroundColor(context.getResources().getColor(R.color.colorTimesheetEmpty));
        }

        timesheetViewHolder.date.setText(currentItem.getReadable_day_of_month());
        timesheetViewHolder.tv_month_output.setText(currentItem.getReadable_month().toUpperCase());
        timesheetViewHolder.tv_day_of_week_output.setText(helper.convertToFormattedTime(currentItem.getNumeric_date(), "yyyy-MM-dd", "EEE"));
        timesheetViewHolder.timein.setText(currentItem.getTime_in().replace("a.m.", "AM").replace("p.m.", "PM"));
        timesheetViewHolder.timeout.setText(currentItem.getTime_out().replace("a.m.", "AM").replace("p.m.", "PM"));
        timesheetViewHolder.day_type.setText(currentItem.getDay_type());

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(3, 3, 3, 3);
        timesheetViewHolder.legendLayout.removeAllViews();

        Boolean dateHasPassedorToday = helper.dateHasPassedOrToday(currentItem.getNumeric_date());

        if (currentItem.getDay_type().equals("ABSENT") && dateHasPassedorToday) {
            ImageView iv_legend = new ImageView(context);
            iv_legend.setBackground(ContextCompat.getDrawable(context, R.drawable.legend_absent_oval));
            iv_legend.setLayoutParams(lp);

            timesheetViewHolder.legendLayout.addView(iv_legend);
        }

        if (currentItem.getDay_type().equals("RD") && dateHasPassedorToday) {
            ImageView iv_legend = new ImageView(context);
            iv_legend.setBackground(ContextCompat.getDrawable(context, R.drawable.legend_rest_day_oval));
            iv_legend.setLayoutParams(lp);

            timesheetViewHolder.legendLayout.addView(iv_legend);
        }

        if (currentItem.getAdjusted() == 1) {
            ImageView iv_legend = new ImageView(context);
            iv_legend.setBackground(ContextCompat.getDrawable(context, R.drawable.legend_adjusted_oval));
            iv_legend.setLayoutParams(lp);

            timesheetViewHolder.legendLayout.addView(iv_legend);
        }

        if (currentItem.getLate() > 0) {
            ImageView iv_legend = new ImageView(context);
            iv_legend.setBackground(ContextCompat.getDrawable(context, R.drawable.legend_late_oval));
            iv_legend.setLayoutParams(lp);

            timesheetViewHolder.legendLayout.addView(iv_legend);
        }

        if (currentItem.getUndertime() > 0) {
            ImageView iv_legend = new ImageView(context);
            iv_legend.setBackground(ContextCompat.getDrawable(context, R.drawable.legend_undertime_oval));
            iv_legend.setLayoutParams(lp);

            timesheetViewHolder.legendLayout.addView(iv_legend);
        }

        if (!currentItem.getOvertime().getStart_time().equals("null") && !currentItem.getOvertime().getEnd_time().equals("null")) {
            ImageView iv_legend = new ImageView(context);
            iv_legend.setBackground(ContextCompat.getDrawable(context, R.drawable.legend_overtime_oval));
            iv_legend.setLayoutParams(lp);

            timesheetViewHolder.legendLayout.addView(iv_legend);
        }

        if (LeaveTypes != null) {

            for (LeaveType leaveType : LeaveTypes) {
                if (currentItem.getDay_type().equals(leaveType.getLeave_type_code())) {
                    ImageView iv_legend = new ImageView(context);
                    iv_legend.setBackground(ContextCompat.getDrawable(context, R.drawable.legend_leave_oval));
                    iv_legend.setLayoutParams(lp);

                    timesheetViewHolder.legendLayout.addView(iv_legend);
                }
            }
        }

        //timesheetViewHolder.timesheet_details.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View view) {
                //Ryan
                //showDetails(currentItem);

                //Karl
        //        redirectToEditPage(currentItem);
        //    }
        //});
    }

    @Override
    public int getItemCount() {
        return timesheetList.size();
    }

    private void showDetails(final TimesheetItem currentItem) {
        ImageView btn_edit_timesheet = timeSheetDetails.findViewById(R.id.btn_edit_timesheet);

        TextView dialog_date_today = timeSheetDetails.findViewById(R.id.tv_date_today_details);
        TextView dialog_time_in = timeSheetDetails.findViewById(R.id.tv_time_in_details);
        TextView dialog_time_out = timeSheetDetails.findViewById(R.id.tv_time_out_details);
        TextView dialog_shift_in = timeSheetDetails.findViewById(R.id.tv_Shift_in_details);
        TextView dialog_shift_out = timeSheetDetails.findViewById(R.id.tv_Shift_out_details);
        TextView dialog_day_type = timeSheetDetails.findViewById(R.id.tv_day_type_details);
        TextView dialog_reference = timeSheetDetails.findViewById(R.id.tv_reference_details);

        TextView dialog_late = timeSheetDetails.findViewById(R.id.tv_timesheet_late);
        TextView dialog_undertime = timeSheetDetails.findViewById(R.id.tv_timesheet_undertime);
        TextView dialog_adjusted = timeSheetDetails.findViewById(R.id.tv_timesheet_adjusted);

        dialog_date_today.setText(currentItem.getDate_in());

        String time_in = !currentItem.getTime_in().isEmpty() ? currentItem.getTime_in() : "--:-- --";
        String time_out = !currentItem.getTime_out().isEmpty() ? currentItem.getTime_out() : "--:-- --";

        dialog_time_in.setText(time_in);
        dialog_time_out.setText(time_out);

        dialog_shift_in.setText(currentItem.getShiftIn());
        dialog_shift_out.setText(currentItem.getShiftOut());
        dialog_day_type.setText(currentItem.getDay_type());

        String reference = !currentItem.getReference().equals("null") ? currentItem.getReference() : "No reference";
        dialog_reference.setText(reference);

        dialog_late.setText(currentItem.getLate().toString());
        dialog_undertime.setText(currentItem.getUndertime().toString());
        dialog_adjusted.setText(currentItem.getAdjusted().toString());

        btn_edit_timesheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                timeSheetDetails.dismiss();
            }
        });

        timeSheetDetails.show();
    }

    public void redirectToEditPage(final TimesheetItem currentItem) {

        timesheetnavigattion.setVisibility(GONE);

        Bundle bundle = new Bundle();
        bundle.putString("specific_date", currentItem.getNumeric_date());
        bundle.putString("readable_specific_date", currentItem.getDate_in());
        RequestFragment rf = new RequestFragment();
        rf.setArguments(bundle);

        ((FragmentActivity) context)
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .add(R.id.approvee_timesheet_container, rf, "RequestFragment")
                .addToBackStack("RequestFragment")
                .commit();
    }

    //public void runPieChart(Integer late, Integer undertime, Integer adjusted){
//
//        Integer status[] = {late, undertime, adjusted};
//        String statusName[] = {"late","undertime","adjusted"};
//
//        List<PieEntry> pieEntries = new ArrayList<>();
//        for (int i = 0; i < status.length; i++){
//            pieEntries.add(new PieEntry(status[i], statusName[i]));
//        }
//
//        PieDataSet dataSet = new PieDataSet(pieEntries, "");
//        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
//        PieData data = new PieData(dataSet);
//
//        //get the chart
//        PieChart chart = timeSheetDetails.findViewById(R.id.daily_chart);
//        chart.setData(data);
//        chart.animateY(1000);
//        chart.invalidate();
//    }


}
