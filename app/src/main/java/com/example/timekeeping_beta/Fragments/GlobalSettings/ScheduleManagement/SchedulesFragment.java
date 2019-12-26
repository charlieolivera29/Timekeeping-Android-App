package com.example.timekeeping_beta.Fragments.GlobalSettings.ScheduleManagement;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.timekeeping_beta.Fragments.Retry.TryAgainFragment;
import com.example.timekeeping_beta.Globals.CustomClasses.Flag;
import com.example.timekeeping_beta.Globals.CustomClasses.Interface.RecyclerViewClickListener;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.Globals.Models.User;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class SchedulesFragment extends Fragment implements RecyclerViewClickListener {

    private View v;
    private Context ctx;
    private Helper helper;
    private SchedulesViewModel SchedulesViewModel;
    private List<Schedule> SchedulesList;

    private FloatingActionButton fab_create_leave_type;
    private RecyclerView recyclerview_holiday_types;
    private TextView no_data;

    private ProgressDialog loadingScreenDialog;
    private ScheduleAdapter schedulesAdapter;
    private Dialog CreateHTDialog;
    private Dialog ShowHTDialog;
    private Dialog EditHTDialog;


    private TextView modal_show_sched_name;
    private TextView modal_show_days;
    private TextView modal_show_shift_in;
    private TextView modal_show_shift_out;
    private TextView modal_show_grace_period;
    private TextView modal_show_remarks;

    private Button btn_ht_type_create;
    private EditText modal_sched_name;
    private RelativeLayout modal_modal_sched_shift_in_picker;
    private TextView modal_sched_shift_in;
    private RelativeLayout modal_modal_sched_shift_out_picker;
    private TextView modal_sched_shift_out;
    private EditText modal_grace_period;
    private EditText modal_remarks;
    private Spinner modal_spinner_sunday_dt;
    private Spinner modal_spinner_monday_dt;
    private Spinner modal_spinner_tuesday_dt;
    private Spinner modal_spinner_wednesday_dt;
    private Spinner modal_spinner_thursday_dt;
    private Spinner modal_spinner_friday_dt;
    private Spinner modal_spinner_saturday_dt;
    private ImageButton close_create_dialog;

    private Button btn_ht_type_edit;
    private TextView title;
    private EditText modal_edit_sched_name;
    private RelativeLayout modal_edit_modal_sched_shift_in_picker;
    private TextView modal_edit_sched_shift_in;
    private RelativeLayout modal_edit_sched_shift_out_picker;
    private TextView modal_edit_sched_shift_out;
    private EditText modal_edit_grace_period;
    private EditText modal_edit_remarks;
    private Spinner modal_edit_spinner_sunday_dt;
    private Spinner modal_edit_spinner_monday_dt;
    private Spinner modal_edit_spinner_tuesday_dt;
    private Spinner modal_edit_spinner_wednesday_dt;
    private Spinner modal_edit_spinner_thursday_dt;
    private Spinner modal_edit_spinner_friday_dt;
    private Spinner modal_edit_spinner_saturday_dt;

    private LinearLayout modal_effective_date_picker;
    private TextView modal_effective_date;

    private ImageButton close_edit_dialog;

    private String error_message;
    private String success_message;

    private int selected_sched_pos;
    private String string_shift_in_time;
    private String string_shift_out_time;
    private String string_edit_shift_in_time;
    private String string_edit_shift_out_time;
    private String string_effective_date;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_global_settings_container, container, false);
        ctx = v.getContext();

        init();
        initViews();
        setListeners();
        setValues();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        retrieveHolidaTypes();
    }

    private void retrieveHolidaTypes() {
        SchedulesViewModel.retrieveAllSchedules();
        whenLoading();
    }

    private void init() {
        error_message = getResources().getString(R.string.api_request_failed);
        success_message = getResources().getString(R.string.api_request_success);

        helper = Helper.getInstance(ctx);

        EditHTDialog = new Dialog(ctx, R.style.AppTheme_NoActionBar);
        CreateHTDialog = new Dialog(ctx, R.style.AppTheme_NoActionBar);
        ShowHTDialog = new Dialog(ctx, R.style.AppTheme_NoActionBar);

        SchedulesViewModel = ViewModelProviders.of(this)
                .get(SchedulesViewModel.class);

    }

    private void initViews() {
        fab_create_leave_type = v.findViewById(R.id.fab_create_item);
        recyclerview_holiday_types = v.findViewById(R.id.recyclerview_global_settings_list);
        no_data = v.findViewById(R.id.no_data);

        ShowHTDialog.setContentView(R.layout.dialog_schedule_show);
        CreateHTDialog.setContentView(R.layout.dialog_schedule_create_edit);
        EditHTDialog.setContentView(R.layout.dialog_schedule_create_edit);


        modal_show_sched_name = ShowHTDialog.findViewById(R.id.modal_show_sched_name);
        modal_show_days = ShowHTDialog.findViewById(R.id.modal_show_days);
        modal_show_shift_in = ShowHTDialog.findViewById(R.id.modal_show_shift_in);
        modal_show_shift_out = ShowHTDialog.findViewById(R.id.modal_show_shift_out);
        modal_show_grace_period = ShowHTDialog.findViewById(R.id.modal_show_grace_period);
        modal_show_remarks = ShowHTDialog.findViewById(R.id.modal_show_remarks);


        btn_ht_type_create = CreateHTDialog.findViewById(R.id.btn_ht_type_create);
        modal_sched_name = CreateHTDialog.findViewById(R.id.modal_sched_name);
        modal_modal_sched_shift_in_picker = CreateHTDialog.findViewById(R.id.modal_modal_sched_shift_in_picker);
        modal_sched_shift_in = CreateHTDialog.findViewById(R.id.modal_sched_shift_in);
        modal_modal_sched_shift_out_picker = CreateHTDialog.findViewById(R.id.modal_modal_sched_shift_out_picker);
        modal_sched_shift_out = CreateHTDialog.findViewById(R.id.modal_sched_shift_out);
        modal_grace_period = CreateHTDialog.findViewById(R.id.modal_grace_period);
        modal_remarks = CreateHTDialog.findViewById(R.id.modal_remarks);
        modal_spinner_sunday_dt = CreateHTDialog.findViewById(R.id.modal_spinner_sunday_dt);
        modal_spinner_monday_dt = CreateHTDialog.findViewById(R.id.modal_spinner_monday_dt);
        modal_spinner_tuesday_dt = CreateHTDialog.findViewById(R.id.modal_spinner_tuesday_dt);
        modal_spinner_wednesday_dt = CreateHTDialog.findViewById(R.id.modal_spinner_wednesday_dt);
        modal_spinner_thursday_dt = CreateHTDialog.findViewById(R.id.modal_spinner_thursday_dt);
        modal_spinner_friday_dt = CreateHTDialog.findViewById(R.id.modal_spinner_friday_dt);
        modal_spinner_saturday_dt = CreateHTDialog.findViewById(R.id.modal_spinner_saturday_dt);
        close_create_dialog = CreateHTDialog.findViewById(R.id.img_fullscreen_dialog_close);

        btn_ht_type_edit = EditHTDialog.findViewById(R.id.btn_ht_type_create);


        title = EditHTDialog.findViewById(R.id.title);
        modal_edit_sched_name = EditHTDialog.findViewById(R.id.modal_sched_name);
        modal_edit_modal_sched_shift_in_picker = EditHTDialog.findViewById(R.id.modal_modal_sched_shift_in_picker);
        modal_edit_sched_shift_in = EditHTDialog.findViewById(R.id.modal_sched_shift_in);
        modal_edit_sched_shift_out_picker = EditHTDialog.findViewById(R.id.modal_modal_sched_shift_out_picker);
        modal_edit_sched_shift_out = EditHTDialog.findViewById(R.id.modal_sched_shift_out);
        modal_edit_grace_period = EditHTDialog.findViewById(R.id.modal_grace_period);
        modal_edit_remarks = EditHTDialog.findViewById(R.id.modal_remarks);
        modal_edit_spinner_sunday_dt = EditHTDialog.findViewById(R.id.modal_spinner_sunday_dt);
        modal_edit_spinner_monday_dt = EditHTDialog.findViewById(R.id.modal_spinner_monday_dt);
        modal_edit_spinner_tuesday_dt = EditHTDialog.findViewById(R.id.modal_spinner_tuesday_dt);
        modal_edit_spinner_wednesday_dt = EditHTDialog.findViewById(R.id.modal_spinner_wednesday_dt);
        modal_edit_spinner_thursday_dt = EditHTDialog.findViewById(R.id.modal_spinner_thursday_dt);
        modal_edit_spinner_friday_dt = EditHTDialog.findViewById(R.id.modal_spinner_friday_dt);
        modal_edit_spinner_saturday_dt = EditHTDialog.findViewById(R.id.modal_spinner_saturday_dt);

        modal_effective_date_picker = EditHTDialog.findViewById(R.id.modal_effective_date_picker);
        modal_effective_date = EditHTDialog.findViewById(R.id.modal_effective_date);

        close_edit_dialog = EditHTDialog.findViewById(R.id.img_fullscreen_dialog_close);
    }

    private void setListeners() {

        final SchedulesFragment that = this;

        SchedulesViewModel.getCreateLeaveTypeResult().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

                loadingScreenDialog.dismiss();

                if (s != null) {

                    if (s.equals("success")) {
                        Toasty.success(ctx, success_message, Toasty.LENGTH_SHORT).show();
                        CreateHTDialog.dismiss();
                        retrieveHolidaTypes();
                    } else {
                        Toasty.error(ctx, s, Toasty.LENGTH_SHORT).show();
                        whenSuccess();
                    }
                } else {
                    Toasty.error(ctx, error_message, Toasty.LENGTH_SHORT).show();
                }
            }
        });

        SchedulesViewModel.getEditLeaveTypeResult().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

                loadingScreenDialog.dismiss();

                if (s != null) {

                    if (s.equals("success")) {
                        Toasty.success(ctx, success_message, Toasty.LENGTH_SHORT).show();
                        EditHTDialog.dismiss();
                        retrieveHolidaTypes();
                    } else {
                        Toasty.error(ctx, s, Toasty.LENGTH_SHORT).show();
                    }
                } else {
                    Toasty.error(ctx, error_message, Toasty.LENGTH_SHORT).show();
                }
            }
        });

        SchedulesViewModel.getSchedules().observe(this, new Observer<List<Schedule>>() {
            @Override
            public void onChanged(@Nullable List<Schedule> schedules) {
                SchedulesList = schedules;

                if (SchedulesList != null) {

                    if (SchedulesList.size() == 0) {
                        whenNoResult();
                    } else {
                        schedulesAdapter = new ScheduleAdapter(SchedulesList, that);
                        recyclerview_holiday_types.setAdapter(schedulesAdapter);
                        schedulesAdapter.notifyDataSetChanged();
                        whenSuccess();
                    }
                } else {
                    whenError();
                }
            }
        });

        fab_create_leave_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateHTDialog.show();
            }
        });

        btn_ht_type_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingScreenDialog = ProgressDialog.show(ctx, null, "Please Wait...");

                try {
                    validateSchedule(
                            Flag.CREATE,
                            modal_sched_name.getText().toString(),
                            string_shift_in_time,
                            string_shift_out_time,
                            modal_grace_period.getText().toString(),
                            modal_remarks.getText().toString(),
                            new JSONArray()
                                    .put(new JSONObject().put("day", "Sun").put("type", modal_spinner_sunday_dt.getSelectedItem().equals("REG") ? 1 : 0))
                                    .put(new JSONObject().put("day", "Mon").put("type", modal_spinner_monday_dt.getSelectedItem().equals("REG") ? 1 : 0))
                                    .put(new JSONObject().put("day", "Tue").put("type", modal_spinner_tuesday_dt.getSelectedItem().equals("REG") ? 1 : 0))
                                    .put(new JSONObject().put("day", "Wed").put("type", modal_spinner_wednesday_dt.getSelectedItem().equals("REG") ? 1 : 0))
                                    .put(new JSONObject().put("day", "Thu").put("type", modal_spinner_thursday_dt.getSelectedItem().equals("REG") ? 1 : 0))
                                    .put(new JSONObject().put("day", "Fri").put("type", modal_spinner_friday_dt.getSelectedItem().equals("REG") ? 1 : 0))
                                    .put(new JSONObject().put("day", "Sat").put("type", modal_spinner_saturday_dt.getSelectedItem().equals("REG") ? 1 : 0))
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        close_create_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CreateHTDialog.isShowing()) {
                    CreateHTDialog.dismiss();
                }
            }
        });

        close_edit_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EditHTDialog.isShowing()) {
                    EditHTDialog.dismiss();
                }
            }
        });

        modal_modal_sched_shift_in_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ctx, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        string_shift_in_time = selectedHour + ":" + selectedMinute;
                        modal_sched_shift_in.setText(helper.convertToReadableTime(string_shift_in_time));
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");

                mTimePicker.show();
            }
        });

        modal_edit_modal_sched_shift_in_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ctx, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        string_edit_shift_in_time = selectedHour + ":" + selectedMinute;
                        modal_edit_sched_shift_in.setText(helper.convertToReadableTime(string_edit_shift_in_time));
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");

                mTimePicker.show();
            }
        });


        modal_modal_sched_shift_out_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ctx, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        string_shift_out_time = selectedHour + ":" + selectedMinute;
                        modal_sched_shift_out.setText(helper.convertToReadableTime(string_shift_out_time));
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");

                mTimePicker.show();
            }
        });

        modal_edit_sched_shift_out_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ctx, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        string_edit_shift_out_time = selectedHour + ":" + selectedMinute;
                        modal_edit_sched_shift_out.setText(helper.convertToReadableTime(string_edit_shift_out_time));
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");

                mTimePicker.show();
            }
        });

        modal_effective_date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar effective_date = Calendar.getInstance();

                DatePickerDialog.OnDateSetListener create_date_listener = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub

                        effective_date.set(Calendar.YEAR, year);
                        effective_date.set(Calendar.MONTH, monthOfYear);
                        effective_date.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        string_effective_date = helper.createStringDate(year, monthOfYear, dayOfMonth);

                        modal_effective_date.setText(helper.convertToReadableDate(string_effective_date));
                    }
                };

                new DatePickerDialog(ctx, create_date_listener, effective_date
                        .get(Calendar.YEAR), effective_date.get(Calendar.MONTH),
                        effective_date.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void setValues() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ctx);

        recyclerview_holiday_types.setHasFixedSize(true);
        recyclerview_holiday_types.setLayoutManager(layoutManager);
        setAdapters();
        btn_ht_type_create.setText("CREATE");
        btn_ht_type_edit.setText("SAVE");
    }

    public void whenLoading() {
        no_data.setVisibility(View.GONE);
        recyclerview_holiday_types.setVisibility(View.GONE);

        loadingScreenDialog = ProgressDialog.show(ctx, null, "Please Wait...");
    }

    public void whenNoResult() {
        loadingScreenDialog.dismiss();
        recyclerview_holiday_types.setVisibility(View.GONE);

        no_data.setVisibility(View.VISIBLE);
    }

    public void whenError() {
        loadingScreenDialog.dismiss();
        recyclerview_holiday_types.setVisibility(View.GONE);
        no_data.setVisibility(View.GONE);

        TryAgainFragment tryAgainFragment = new TryAgainFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("RETURN_TO", Flag.SCHEDULE_FRAGMENT);
        tryAgainFragment.setArguments(arguments);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        if (fragmentManager != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, tryAgainFragment)
                    .commit();
        } else {
            Toasty.error(ctx, "Fragment manager is empty.", Toasty.LENGTH_LONG).show();
        }
    }

    public void whenSuccess() {
        loadingScreenDialog.dismiss();
        no_data.setVisibility(View.GONE);

        recyclerview_holiday_types.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(int position, int f) {

        Schedule s = SchedulesList.get(position);
        selected_sched_pos = position;

        JSONArray ja = s.getDay();
        String days = "";
        if (ja.length() > 0) {

            for (int i = 0; i < ja.length(); i++) {

                try {
                    JSONObject jo = ja.getJSONObject(i);

                    if (jo.getInt("type") == 1) {
                        days = days + jo.getString("day") + ",";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    days = "Error";
                }
            }
        } else {
            days = "No days";
        }

        modal_show_sched_name.setText(s.getSched_name());
        modal_show_days.setText(days.substring(0, days.length() - 1));
        modal_show_shift_in.setText(helper.convertToReadableTime(s.getShift_in()));
        modal_show_shift_out.setText(helper.convertToReadableTime(s.getShift_out()));
        modal_show_grace_period.setText(s.getGrace_period());
        modal_show_remarks.setText(s.getRemarks());

        if (f == Flag.CALLBACK_SHOW) {
            ShowHTDialog.show();

        } else if (f == Flag.CALLBACK_EDIT) {
            showEditSchedule(s);
        }
    }

    private void showEditSchedule(Schedule s) {

        title.setText("Edit Schedule");
        modal_edit_sched_name.setText(s.getSched_name());
        modal_edit_sched_shift_in.setText(helper.convertToReadableTime(s.getShift_in()));
        string_edit_shift_in_time = s.getShift_in();
        modal_edit_sched_shift_out.setText(helper.convertToReadableTime(s.getShift_out()));
        string_edit_shift_out_time = s.getShift_out();
        modal_edit_grace_period.setText(s.getGrace_period());
        modal_edit_remarks.setText(s.getRemarks());
        modal_edit_modal_sched_shift_in_picker.setVisibility(View.VISIBLE);
        modal_edit_sched_shift_out_picker.setVisibility(View.VISIBLE);
        modal_effective_date_picker.setVisibility(View.VISIBLE);

        final JSONArray ja_days = s.getDay();

        if (ja_days.length() > 0) {
            for (int i = 0; i < ja_days.length(); i++) {
                try {
                    JSONObject jo = ja_days.getJSONObject(i);

                    String jo_day_name = jo.getString("day");
                    int jo_type = jo.getInt("type");

                    switch (jo_day_name) {

                        case "Sun":
                            modal_edit_spinner_sunday_dt.setSelection(jo_type == 0 ? 0 : 1);
                            break;

                        case "Mon":
                            modal_edit_spinner_monday_dt.setSelection(jo_type == 0 ? 0 : 1);
                            break;

                        case "Tue":
                            modal_edit_spinner_tuesday_dt.setSelection(jo_type == 0 ? 0 : 1);
                            break;

                        case "Wed":
                            modal_edit_spinner_wednesday_dt.setSelection(jo_type == 0 ? 0 : 1);
                            break;

                        case "Thu":
                            modal_edit_spinner_thursday_dt.setSelection(jo_type == 0 ? 0 : 1);
                            break;

                        case "Fri":
                            modal_edit_spinner_friday_dt.setSelection(jo_type == 0 ? 0 : 1);
                            break;

                        case "Sat":
                            modal_edit_spinner_saturday_dt.setSelection(jo_type == 0 ? 0 : 1);
                            break;

                        default:
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        btn_ht_type_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingScreenDialog = ProgressDialog.show(ctx, null, "Please Wait...");

                try {
                    JSONArray ja_days = new JSONArray()
                            .put(new JSONObject().put("day", "Sun").put("type", modal_edit_spinner_sunday_dt.getSelectedItem().equals("REG") ? 1 : 0))
                            .put(new JSONObject().put("day", "Mon").put("type", modal_edit_spinner_monday_dt.getSelectedItem().equals("REG") ? 1 : 0))
                            .put(new JSONObject().put("day", "Tue").put("type", modal_edit_spinner_tuesday_dt.getSelectedItem().equals("REG") ? 1 : 0))
                            .put(new JSONObject().put("day", "Wed").put("type", modal_edit_spinner_wednesday_dt.getSelectedItem().equals("REG") ? 1 : 0))
                            .put(new JSONObject().put("day", "Thu").put("type", modal_edit_spinner_thursday_dt.getSelectedItem().equals("REG") ? 1 : 0))
                            .put(new JSONObject().put("day", "Fri").put("type", modal_edit_spinner_friday_dt.getSelectedItem().equals("REG") ? 1 : 0))
                            .put(new JSONObject().put("day", "Sat").put("type", modal_edit_spinner_saturday_dt.getSelectedItem().equals("REG") ? 1 : 0));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                validateSchedule(
                        Flag.EDIT,
                        modal_edit_sched_name.getText().toString(),
                        string_edit_shift_in_time,
                        string_edit_shift_out_time,
                        modal_edit_grace_period.getText().toString(),
                        modal_edit_remarks.getText().toString(),
                        ja_days
                );
            }
        });

        EditHTDialog.show();
    }

    public void setAdapters() {
        List<String> spinnerArray = new ArrayList<>();
        spinnerArray.add("REG");
        spinnerArray.add("RD");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                ctx, android.R.layout.simple_spinner_dropdown_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        modal_spinner_sunday_dt.setAdapter(adapter);
        modal_spinner_monday_dt.setAdapter(adapter);
        modal_spinner_tuesday_dt.setAdapter(adapter);
        modal_spinner_wednesday_dt.setAdapter(adapter);
        modal_spinner_thursday_dt.setAdapter(adapter);
        modal_spinner_friday_dt.setAdapter(adapter);
        modal_spinner_saturday_dt.setAdapter(adapter);

        modal_edit_spinner_sunday_dt.setAdapter(adapter);
        modal_edit_spinner_monday_dt.setAdapter(adapter);
        modal_edit_spinner_tuesday_dt.setAdapter(adapter);
        modal_edit_spinner_wednesday_dt.setAdapter(adapter);
        modal_edit_spinner_thursday_dt.setAdapter(adapter);
        modal_edit_spinner_friday_dt.setAdapter(adapter);
        modal_edit_spinner_saturday_dt.setAdapter(adapter);
    }

    private void validateSchedule(
            int flag,
            String sched_name,
            String shift_in_time,
            String shift_out_time,
            String grace_period,
            String remarks,
            JSONArray days) {


        if (sched_name.length() > 0 &&
                shift_in_time != null &&
                shift_out_time != null &&
                grace_period.length() > 0 &&
                remarks.length() > 0) {

            User user = SharedPrefManager.getInstance(ctx).getUser();

            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("added_by", user.getUser_id());
                jsonBody.put("days", days);
                jsonBody.put("grace_period", Integer.valueOf(grace_period));
                jsonBody.put("isBroken", false);
                jsonBody.put("remarks", remarks);
                jsonBody.put("sched_name", sched_name);
                jsonBody.put("shift_in", string_shift_in_time);
                jsonBody.put("shift_out", string_shift_out_time);

                if (flag == Flag.CREATE) {

                    SchedulesViewModel.createSchedule(jsonBody);
                } else if (flag == Flag.EDIT) {

                    Schedule schedule = SchedulesList.get(selected_sched_pos);

                    jsonBody.put("date_sched_queue", string_effective_date != null ? string_effective_date : "");
                    jsonBody.put("id", schedule.getId());
                    jsonBody.put("sched_id", schedule.getSched_id());
                    jsonBody.put("break", null);
                    jsonBody.put("default_sched", 0);
                    jsonBody.put("undertime_treshold", null);

                    SchedulesViewModel.updateSchedule(schedule.getSched_id(), jsonBody);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toasty.error(ctx, "Please fill all fields", Toasty.LENGTH_LONG).show();
        }
    }
}
