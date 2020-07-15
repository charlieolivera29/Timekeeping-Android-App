package com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestEDTR;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.timekeeping_beta.Globals.CustomClasses.Flag;
import com.example.timekeeping_beta.Globals.CustomClasses.Interface.RecyclerViewClickListener;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

import static android.app.ProgressDialog.show;

public class TimesheetEntryFragment extends Fragment implements RecyclerViewClickListener {

    private Context context;
    private View v;

    private List<TimesheetEntry> TimesheetEntryList;
    private TimesheetEntryDates TimesheetEntryDates;

    private RecyclerView recyclerViewTimesheetEntries;
    private EditText search_bar;

    private WeeklyTimesheetEntryAdapter mAdapter;
    private ProgressDialog loadingScreenDialog;

    private TextView no_data;

    private LinearLayout timesheetnavigattion;
    private ImageButton button_color_legend;
    private TextView date_start, tv_dash, date_end, tv_month;
    private ImageView button_previous, button_next;

    private TimesheetEntryViewModel TimesheetEntryViewModel;

    private Dialog EDTRAdjustmentDialog, updateEDTRAdjustmentDialog;
    String start_time = "", end_time = "";
    private FloatingActionButton fab_save_button;

    @Override
    public void onResume() {
        super.onResume();

        //timesheetnavigattion.setVisibility(View.VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_timesheet_entry, container, false);
        context = v.getContext();

        initViews();
        init();
        setListeners();

        return v;
    }

    private void initViews() {


        recyclerViewTimesheetEntries = v.findViewById(R.id.recyclerViewTimesheetEntries);
        no_data = v.findViewById(R.id.no_data);
        fab_save_button = v.findViewById(R.id.fab_save_button);
        fab_save_button.hide();

        timesheetnavigattion = getActivity().findViewById(R.id.timesheetnavigattion);
        tv_month = getActivity().findViewById(R.id.tv_month);
        date_start = getActivity().findViewById(R.id.tv_date_start);
        tv_dash = getActivity().findViewById(R.id.tv_dash);
        date_end = getActivity().findViewById(R.id.tv_date_end);
        button_previous = getActivity().findViewById(R.id.iv_prev_month);
        button_next = getActivity().findViewById(R.id.iv_next_month);
        button_color_legend = getActivity().findViewById(R.id.button_color_legend);
    }

    private void init() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        TimesheetEntryList = new ArrayList<>();
        recyclerViewTimesheetEntries.setHasFixedSize(true);
        recyclerViewTimesheetEntries.setLayoutManager(layoutManager);
        mAdapter = new WeeklyTimesheetEntryAdapter(context, TimesheetEntryList, this);
        recyclerViewTimesheetEntries.setAdapter(mAdapter);
        //button_color_legend.setVisibility(View.GONE);

        // R.style.AppTheme_NoActionBar
        EDTRAdjustmentDialog = new Dialog(context);
        updateEDTRAdjustmentDialog = new Dialog(context, R.style.AppTheme_NoActionBar);


        EDTRAdjustmentDialog.setContentView(R.layout.dialog_create_edtr_adjustment);
        updateEDTRAdjustmentDialog.setContentView(R.layout.dialog_create_edtr_adjustment);
    }

    private void setListeners() {
        TimesheetEntryViewModel = ViewModelProviders.of(this)
                .get(TimesheetEntryViewModel.class);

        TimesheetEntryViewModel.retrieveEDTR();

        whenLoading("Loading Timesheets. \nPlease wait...");
        TimesheetEntryViewModel.getLiveTimesheetEntries().observe(this, new Observer<ArrayList<TimesheetEntry>>() {
            @Override
            public void onChanged(@Nullable ArrayList<TimesheetEntry> timesheetEntries) {
                if (timesheetEntries != null) {
                    TimesheetEntryList.clear();
                    TimesheetEntryList.addAll(timesheetEntries);
                    recyclerViewTimesheetEntries.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();

                    fab_save_button.show();
                    fab_save_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TimesheetEntryViewModel.sendWeeklyTimeSheets();
                        }
                    });
                }
                whenSuccess();
            }
        });


        final TimesheetEntryFragment that = this;
        TimesheetEntryViewModel.getLiveTimesheetEntriesDates().observe(this, new Observer<TimesheetEntryDates>() {

            @Override
            public void onChanged(@Nullable TimesheetEntryDates timesheetEntryDates) {
                if (timesheetEntryDates != null) {

                    TimesheetEntryDates = timesheetEntryDates;
                    date_start.setText(timesheetEntryDates.getStart());
                    tv_dash.setVisibility(View.VISIBLE);
                    date_end.setText(timesheetEntryDates.getEnd());


                    if (Helper.getInstance(context).dateHasPassedOrToday(TimesheetEntryDates.getNext())) {
                        button_next.setVisibility(View.VISIBLE);

                        button_next.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                TimesheetEntryViewModel.retrieveEDTR(TimesheetEntryDates.getNext());
                            }
                        });

                        button_previous.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                TimesheetEntryViewModel.retrieveEDTR(TimesheetEntryDates.getPrevious());
                            }
                        });
                    } else {
                        button_next.setVisibility(View.INVISIBLE);

                        button_next.setOnClickListener(null);

                        button_previous.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                TimesheetEntryViewModel.retrieveEDTR(TimesheetEntryDates.getPrevious());
                            }
                        });
                    }
                }
            }
        });

        TimesheetEntryViewModel.getCreateResult().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                whenSuccess();

                if (aBoolean) {
                    Toasty.success(context, "EDTR saved!", Toasty.LENGTH_LONG).show();
                } else {
                    Toasty.error(context, "Could not create your adjustment.", Toasty.LENGTH_LONG).show();
                }
            }
        });

        TimesheetEntryViewModel.getEditResult().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                whenSuccess();

                if (aBoolean) {
                    Toasty.success(context, "EDTR saved!", Toasty.LENGTH_LONG).show();
                } else {
                    Toasty.error(context, "Could not save your adjustment.", Toasty.LENGTH_LONG).show();
                }
            }
        });

        TimesheetEntryViewModel.getDeleteResult().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                whenSuccess();

                if (aBoolean) {
                    Toasty.success(context, "EDTR deleted!", Toasty.LENGTH_LONG).show();
                } else {
                    Toasty.error(context, "Could not delete your adjustment.", Toasty.LENGTH_LONG).show();
                }
            }
        });


//        recyclerViewTimesheetEntries.setOnTouchListener(new OnSwipeTouchListener(context) {
//
//            public void onSwipeRight() {
//                TimesheetEntryViewModel.retrieveEDTR(TimesheetEntryDates.getPrev_start());
//            }
//
//            @Override
//            public void onSwipeLeft() {
//                TimesheetEntryViewModel.retrieveEDTR(TimesheetEntryDates.getNext_start());
//            }
//
//            @Override
//            public void onItemTouch(float x, float y) {
//                View view = recyclerViewTimesheetEntries.findChildViewUnder(x, y);
//                if (view != null && view.getTag() != null) {
//
//                    TimesheetEntry te = TimesheetEntryList.get(Integer.parseInt(view.getTag().toString()));
//
//                    adjustEDTR(te);
//                }
//            }
//
//        });

        button_color_legend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog help = new Dialog(context);
                help.setContentView(R.layout.dialog_edtr_request_help);
                help.show();
            }
        });

    }

    public void whenLoading(String idleMessage) {
        loadingScreenDialog = show(getContext(), null, idleMessage);
    }


    public void whenSuccess() {
        loadingScreenDialog.dismiss();
        if (EDTRAdjustmentDialog.isShowing()) {
            EDTRAdjustmentDialog.dismiss();
        }
    }

    @Override
    public void onItemClick(int position, int flag) {

        if (flag == Flag.CALLBACK_CREATE) {
            //adjustEDTR(position, Flag.CALLBACK_CREATE);
            adjustEDTR(position, Flag.CALLBACK_EDIT);
        } else if (flag == Flag.CALLBACK_EDIT) {
            adjustEDTR(position, Flag.CALLBACK_EDIT);
        } else if (flag == Flag.CALLBACK_DELETE) {
            showConfirmDialog(position);
        } else if (flag == Flag.CALLBACK_SHOW) {
            adjustEDTR(position, Flag.CALLBACK_SHOW);
        }
    }

    private void showConfirmDialog(final int position) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //TimesheetEntryViewModel.deleteEDTRAdjustment(edtr_id);
                        TimesheetEntryViewModel.updateWeeklyAdjustments(position, "", "", "", Flag.CALLBACK_DELETE);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to delete this request?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void adjustEDTR(final int position, final int flag) {

        final TimesheetEntry te = TimesheetEntryList.get(position);
        RelativeLayout modal_app_bar = EDTRAdjustmentDialog.findViewById(R.id.modal_app_bar);

//        ImageButton img_fullscreen_dialog_close = modal_app_bar.findViewById(R.id.img_fullscreen_dialog_close);
//        TextView title_bar_text = modal_app_bar.findViewById(R.id.title_bar_text);

        final TextView edtr_date = EDTRAdjustmentDialog.findViewById(R.id.edtr_date);
        final TextView edtr_day_type = EDTRAdjustmentDialog.findViewById(R.id.edtr_day_type);
        final TextView txt_time_in = EDTRAdjustmentDialog.findViewById(R.id.txt_time_in);
        final TextView txt_time_out = EDTRAdjustmentDialog.findViewById(R.id.txt_time_out);
        ImageButton btn_time_in = EDTRAdjustmentDialog.findViewById(R.id.btn_time_in);
        ImageButton btn_time_out = EDTRAdjustmentDialog.findViewById(R.id.btn_time_out);
        final EditText edtxt_adjustment_reason = EDTRAdjustmentDialog.findViewById(R.id.edtxt_adjustment_reason);
        Button btn_send = EDTRAdjustmentDialog.findViewById(R.id.btn_send_adjustment_request);

//        title_bar_text.setText("EDTR Request");
        edtr_date.setText(Helper.getInstance(context).convertToReadableDate(te.getDate_in()));
        edtr_day_type.setText(te.getDay_type().toUpperCase());
        edtxt_adjustment_reason.setText(!te.getRemarks().equals("null") ? te.getRemarks() : "");


        if (flag == Flag.CALLBACK_CREATE) {
            start_time = "";
            end_time = "";

            txt_time_in.setText(context.getResources().getString(R.string.blank_time));
            txt_time_out.setText(context.getResources().getString(R.string.blank_time));

        } else if (flag == Flag.CALLBACK_EDIT) {


            start_time = te.getTime_in();
            end_time = te.getTime_out();

            txt_time_in.setText(Helper.getInstance(context).convertToReadableTime(te.getTime_in()));
            txt_time_out.setText(Helper.getInstance(context).convertToReadableTime(te.getTime_out()));

            btn_send.setVisibility(View.VISIBLE);
            edtxt_adjustment_reason.setEnabled(true);
            btn_send.setEnabled(true);

            //if (te.getDay_type().equals("REG")) {
            btn_time_in.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Calendar mcurrentTime = Calendar.getInstance(Locale.getDefault());
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                            String sh = selectedHour < 10 ? "0" + selectedHour : String.valueOf(selectedHour);
                            String st = selectedMinute < 10 ? "0" + selectedMinute : String.valueOf(selectedMinute);

                            String string_start_time = sh + ":" + st;

                            start_time = string_start_time;
                            txt_time_in.setText(Helper.getInstance(context).convertToReadableTime(string_start_time));
                        }
                    }, hour, minute, false);//Yes 24 hour time
                    mTimePicker.setTitle("");
                    mTimePicker.show();
                }
            });

            btn_time_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Calendar mcurrentTime = Calendar.getInstance(Locale.getDefault());
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                            String sh = selectedHour < 10 ? "0" + selectedHour : String.valueOf(selectedHour);
                            String st = selectedMinute < 10 ? "0" + selectedMinute : String.valueOf(selectedMinute);
                            String string_start_time = sh + ":" + st;

                            end_time = string_start_time;
                            txt_time_out.setText(Helper.getInstance(context).convertToReadableTime(string_start_time));
                        }
                    }, hour, minute, false);//Yes 24 hour time
                    mTimePicker.setTitle("");
                    mTimePicker.show();
                }
            });
            //}

            btn_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String reason = edtxt_adjustment_reason.getText().toString();

                    if (reason.length() == 0) {

                        Toasty.error(context, "Please fill all fields", Toasty.LENGTH_LONG).show();
                    } else {

                        if (!te.getDay_type().equals("RD")) {

                            if (start_time.length() == 0 || edtr_date.length() == 0) {
                                Toasty.error(context, "Time in and time out is required.", Toasty.LENGTH_SHORT).show();
                            } else {
                                if (flag == Flag.CALLBACK_CREATE) {
                                    whenLoading("Adding EDTR \nPlease wait...");
                                    TimesheetEntryViewModel.createEDTRAdjustment(te, start_time, end_time, reason);
                                } else if (flag == Flag.CALLBACK_EDIT) {
                                    whenLoading("Saving EDTR \nPlease wait...");
                                    //TimesheetEntryViewModel.updateEDTRAdjustment(te, start_time, end_time, reason);
                                    TimesheetEntryViewModel.updateWeeklyAdjustments(position, start_time, end_time, reason, Flag.CALLBACK_EDIT);
                                }
                            }
                        } else {
                            if (flag == Flag.CALLBACK_CREATE) {
                                whenLoading("Adding EDTR \nPlease wait...");
                                TimesheetEntryViewModel.createEDTRAdjustment(te, start_time, end_time, reason);
                            } else if (flag == Flag.CALLBACK_EDIT) {
                                whenLoading("Saving EDTR \nPlease wait...");
                                TimesheetEntryViewModel.updateWeeklyAdjustments(position, start_time, end_time, reason, Flag.CALLBACK_EDIT);
                            }
                        }
                    }
                }
            });

        } else if (flag == Flag.CALLBACK_SHOW) {
            start_time = te.getTime_in();
            end_time = te.getTime_out();

            txt_time_in.setText(Helper.getInstance(context).convertToReadableTime(te.getTime_in()));
            txt_time_out.setText(Helper.getInstance(context).convertToReadableTime(te.getTime_out()));

            btn_time_in.setOnClickListener(null);
            btn_time_out.setOnClickListener(null);
            btn_send.setOnClickListener(null);


            edtxt_adjustment_reason.setEnabled(false);
            btn_send.setEnabled(false);
            btn_send.setVisibility(View.GONE);
        }


        EDTRAdjustmentDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        button_next.setVisibility(View.VISIBLE);
    }
}
