package com.example.timekeeping_beta.Fragments.GlobalSettings.HolidayManagement;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.timekeeping_beta.Fragments.GlobalSettings.HolidayTypes.HolidayType;
import com.example.timekeeping_beta.Fragments.GlobalSettings.HolidayTypes.HolidayTypeViewModel;
import com.example.timekeeping_beta.Fragments.GlobalSettings.LocationManagement.CompanyLocation;
import com.example.timekeeping_beta.Fragments.GlobalSettings.LocationManagement.CompanyLocationsViewModel;
import com.example.timekeeping_beta.Fragments.Retry.TryAgainFragment;
import com.example.timekeeping_beta.Globals.CustomClasses.Flag;
import com.example.timekeeping_beta.Globals.CustomClasses.MultiSelectionSpinner;
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
import java.util.Collections;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class HolidaysFragment extends Fragment implements RecyclerViewClickListener {

    //Static
    private View v;
    private Context ctx;
    private Helper helper;
    private User user;

    private HolidaysViewModel HolidaysViewModel;
    private HolidayTypeViewModel HolidayTypeViewModel;
    private CompanyLocationsViewModel CompanyLocationsViewModel;

    private List<Holiday> HolidaysList;
    private List<HolidayType> HolidayTypesList;
    private List<CompanyLocation> CompanyLocationList;

    private FloatingActionButton fab_create_holiday_type;
    private RecyclerView recyclerview_holidays;
    private TextView no_data;
    private ProgressDialog loadingScreenDialog;
    private HolidaysAdapter holidaysAdapter;

    private Calendar holiday_date;
    private String string_create_holiday_date;
    private String string_edit_holiday_date;

    //Modal Show
    private Dialog ShowHDialog;
    private TextView modal_show_holiday_name;
    private TextView modal_show_holiday_date;
    private TextView modal_show_holiday_type;
    private TextView modal_show_holiday_locations;
    private TextView modal_show_holiday_desc;
    private ImageButton close_show_dialog;

    //Modal Create
    private Dialog CreateHDialog;
    private TextView modal_holiday_date;
    private RelativeLayout modal_holiday_date_picker;
    private Button btn_h_create;
    private EditText modal_holiday_name;
    private Spinner modal_spinner_holiday_type;
    private MultiSelectionSpinner modal_multiple_select_spinner_location;
    private TableRow modal_row_locations_affected;
    private Switch modal_specific_location_switch;
    private EditText modal_holiday_description;
    private ImageButton close_create_dialog;
    //Modal Create

    //Modal Edit
    private Dialog EditHTDialog;
    private Button btn_h_edit;

    private TextView modal_edit_title;
    private TextView modal_edit_holiday_date;
    private RelativeLayout modal_edit_holiday_date_picker;
    private EditText modal_edit_holiday_name;
    private Spinner modal_edit_spinner_holiday_type;
    private MultiSelectionSpinner modal_edit_multiple_select_spinner_location;
    private TableRow modal_edit_row_locations_affected;
    private Switch modal_edit_specific_location_switch;
    private EditText modal_edit_holiday_description;

    private ImageButton close_edit_dialog;
    //Modal Edit

    //Messages
    private String error_message;
    private String success_message;
    //Messages

    //Events

    @Override
    public void onItemClick(int position, int flag) {

        Holiday H = HolidaysList.get(position);

        for (HolidayType ht : HolidayTypesList) {
            if (ht.getHoliday_type_code().equals(H.getHoliday_type())) {
                modal_edit_spinner_holiday_type.setSelection(HolidayTypesList.indexOf(ht));
            }
        }

        ArrayList<Integer> location_pos_array = new ArrayList<>();

        for (CompanyLocation cl : CompanyLocationList) {

            for (int i = 0; i < H.getLocation_id().length(); i++) {
                try {
                    String jo_loc = H.getLocation_id().getString(i);

                    if (cl.getBranch_id().equals(jo_loc)) {
                        location_pos_array.add(i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        int[] Location_positions = new int[location_pos_array.size()];

        for (int lp : location_pos_array) {
            Location_positions[location_pos_array.indexOf(lp)] = lp;
        }
        //Edit

        //Create
        JSONArray ja = H.getLocation();
        String locations = "";
        if (ja.length() > 0) {

            try {
                for (int i = 0; i < ja.length(); i++) {

                    if (ja.get(i) instanceof JSONObject) {

                        JSONObject jo = ja.getJSONObject(i);

                        locations = locations + jo.getString("branch_name");
                        if (i != (ja.length() - 1)) {
                            locations = locations + ",";
                        }
                    } else {
                        locations = "All locations";
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                locations = "Error";
            }
        } else {
            locations = "All locations";
        }

        modal_show_holiday_name.setText(H.getHoliday_name());
        modal_show_holiday_date.setText(helper.convertToReadableDate(H.getHoliday_date()));
        modal_show_holiday_type.setText(H.getHoliday_type());
        modal_show_holiday_locations.setText(locations);
        modal_show_holiday_desc.setText(H.getHoliday_remarks());

        modal_edit_title.setText("Edit this Holiday.");
        modal_edit_holiday_date.setText(H.getHoliday_date());
        modal_edit_holiday_name.setText(H.getHoliday_name());
        modal_edit_multiple_select_spinner_location.setSelection(Location_positions);
        Boolean allLocations = H.getLocation_id().length() == CompanyLocationList.size() || locations.equals("All locations");
        modal_edit_specific_location_switch.setChecked(!allLocations);
        modal_edit_holiday_description.setText(H.getHoliday_remarks());

        if (flag == Flag.CALLBACK_SHOW) {
            ShowHDialog.show();
        } else if (flag == Flag.CALLBACK_EDIT) {
            EditHTDialog.show();
        }
    }

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

        retrieveData();
    }

    //UI States

    public void whenLoading() {
        no_data.setVisibility(View.GONE);
        recyclerview_holidays.setVisibility(View.GONE);

        loadingScreenDialog = ProgressDialog.show(ctx, null, "Please Wait...");
    }

    public void whenNoResult() {
        loadingScreenDialog.dismiss();
        recyclerview_holidays.setVisibility(View.GONE);

        no_data.setVisibility(View.VISIBLE);
    }

    public void whenError() {
        loadingScreenDialog.dismiss();
        recyclerview_holidays.setVisibility(View.GONE);
        no_data.setVisibility(View.GONE);

        TryAgainFragment tryAgainFragment = new TryAgainFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("RETURN_TO", Flag.HOlIDAYS_FRAGMENT);
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

        recyclerview_holidays.setVisibility(View.VISIBLE);
    }


    //Methods

    private void retrieveData() {
        HolidaysViewModel.retrieveAllHolidays();
        HolidayTypeViewModel.retrieveAllHolidayTypes();
        CompanyLocationsViewModel.retrieveAllLocations();
        whenLoading();
    }

    private void init() {

        helper = Helper.getInstance(ctx);
        user = SharedPrefManager.getInstance(ctx).getUser();

        error_message = getResources().getString(R.string.api_request_failed);
        success_message = getResources().getString(R.string.api_request_success);

        ShowHDialog = new Dialog(ctx, R.style.AppTheme_NoActionBar);
        EditHTDialog = new Dialog(ctx, R.style.AppTheme_NoActionBar);
        CreateHDialog = new Dialog(ctx, R.style.AppTheme_NoActionBar);

        holiday_date = Calendar.getInstance();

        HolidaysViewModel = ViewModelProviders.of(this)
                .get(HolidaysViewModel.class);

        HolidayTypeViewModel = ViewModelProviders.of(this)
                .get(HolidayTypeViewModel.class);

        CompanyLocationsViewModel = ViewModelProviders.of(this)
                .get(CompanyLocationsViewModel.class);
    }

    private void initViews() {
        fab_create_holiday_type = v.findViewById(R.id.fab_create_item);
        recyclerview_holidays = v.findViewById(R.id.recyclerview_global_settings_list);
        no_data = v.findViewById(R.id.no_data);

        ShowHDialog.setContentView(R.layout.dialog_holiday_show);
        CreateHDialog.setContentView(R.layout.dialog_holiday_create_edit);
        EditHTDialog.setContentView(R.layout.dialog_holiday_create_edit);

        modal_show_holiday_name = ShowHDialog.findViewById(R.id.modal_show_holiday_name);
        modal_show_holiday_date = ShowHDialog.findViewById(R.id.modal_show_holiday_date);
        modal_show_holiday_type = ShowHDialog.findViewById(R.id.modal_show_holiday_type);
        modal_show_holiday_locations = ShowHDialog.findViewById(R.id.modal_show_holiday_locations);
        modal_show_holiday_desc = ShowHDialog.findViewById(R.id.modal_show_holiday_desc);
        close_show_dialog = ShowHDialog.findViewById(R.id.close_show_dialog);

        btn_h_create = CreateHDialog.findViewById(R.id.btn_h_type_create);
        close_create_dialog = CreateHDialog.findViewById(R.id.img_fullscreen_dialog_close);

        modal_holiday_name = CreateHDialog.findViewById(R.id.modal_holiday_name);
        modal_holiday_date = CreateHDialog.findViewById(R.id.modal_holiday_date);
        modal_holiday_date_picker = CreateHDialog.findViewById(R.id.modal_holiday_date_picker);
        modal_row_locations_affected = CreateHDialog.findViewById(R.id.modal_row_locations_affected);
        modal_spinner_holiday_type = CreateHDialog.findViewById(R.id.modal_spinner_holiday_type);
        modal_multiple_select_spinner_location = CreateHDialog.findViewById(R.id.modal_multi_selection_spinner);
        modal_specific_location_switch = CreateHDialog.findViewById(R.id.modal_specific_location_switch);
        modal_holiday_description = CreateHDialog.findViewById(R.id.modal_holiday_description);

        btn_h_edit = EditHTDialog.findViewById(R.id.btn_h_type_create);
        close_edit_dialog = EditHTDialog.findViewById(R.id.img_fullscreen_dialog_close);

        modal_edit_title = EditHTDialog.findViewById(R.id.modal_title);
        modal_edit_holiday_date_picker = EditHTDialog.findViewById(R.id.modal_holiday_date_picker);
        modal_edit_holiday_date = EditHTDialog.findViewById(R.id.modal_holiday_date);
        modal_edit_holiday_name = EditHTDialog.findViewById(R.id.modal_holiday_name);
        modal_edit_spinner_holiday_type = EditHTDialog.findViewById(R.id.modal_spinner_holiday_type);
        modal_edit_multiple_select_spinner_location = EditHTDialog.findViewById(R.id.modal_multi_selection_spinner);
        modal_edit_row_locations_affected = EditHTDialog.findViewById(R.id.modal_row_locations_affected);
        modal_edit_specific_location_switch = EditHTDialog.findViewById(R.id.modal_specific_location_switch);
        modal_edit_holiday_description = EditHTDialog.findViewById(R.id.modal_holiday_description);
    }

    private void setListeners() {

        final HolidaysFragment that = this;

        //Observers
        HolidaysViewModel.getCreateHolidayResult().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

                loadingScreenDialog.dismiss();

                if (s != null) {

                    if (s.equals("success")) {
                        Toasty.success(ctx, success_message, Toasty.LENGTH_SHORT).show();
                        CreateHDialog.dismiss();
                        retrieveData();
                    } else {
                        Toasty.error(ctx, s, Toasty.LENGTH_SHORT).show();
                        whenSuccess();
                    }
                } else {
                    Toasty.error(ctx, error_message, Toasty.LENGTH_SHORT).show();
                }
            }
        });

        HolidaysViewModel.getEditHolidayResult().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

                loadingScreenDialog.dismiss();

                if (s != null) {

                    if (s.equals("success")) {
                        Toasty.success(ctx, success_message, Toasty.LENGTH_SHORT).show();
                        EditHTDialog.dismiss();
                        retrieveData();
                    } else {
                        Toasty.error(ctx, s, Toasty.LENGTH_SHORT).show();
                    }
                } else {
                    Toasty.error(ctx, error_message, Toasty.LENGTH_SHORT).show();
                }
            }
        });

        HolidaysViewModel.getHolidaysTypes().observe(this, new Observer<List<Holiday>>() {
            @Override
            public void onChanged(@Nullable List<Holiday> Holiday) {
                HolidaysList = Holiday;

                if (HolidaysList != null) {

                    if (HolidaysList.size() == 0) {
                        whenNoResult();
                    } else {
                        Collections.reverse(HolidaysList);
                        holidaysAdapter = new HolidaysAdapter(HolidaysList, that);
                        recyclerview_holidays.setAdapter(holidaysAdapter);
                        holidaysAdapter.notifyDataSetChanged();
                        whenSuccess();

                        setValues();
                    }
                } else {
                    whenError();
                }
            }
        });

        HolidayTypeViewModel.getHolidaysTypes().observe(this, new Observer<List<HolidayType>>() {
            @Override
            public void onChanged(@Nullable List<HolidayType> holidayTypes) {
                if (holidayTypes != null) {
                    HolidayTypesList = holidayTypes;
                    setHolidayTypesSpinner(holidayTypes);
                }
            }
        });

        CompanyLocationsViewModel.getLocations().observe(this, new Observer<List<CompanyLocation>>() {
            @Override
            public void onChanged(@Nullable List<CompanyLocation> companyLocations) {
                if (companyLocations != null) {
                    CompanyLocationList = companyLocations;
                    setLocationSpinner(companyLocations);
                }
            }
        });

        final DatePickerDialog.OnDateSetListener create_date_listener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                holiday_date.set(Calendar.YEAR, year);
                holiday_date.set(Calendar.MONTH, monthOfYear);
                holiday_date.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                string_create_holiday_date = helper.createStringDate(year, monthOfYear, dayOfMonth);

                modal_holiday_date.setText(helper.convertToReadableDate(string_create_holiday_date));
            }
        };

        final DatePickerDialog.OnDateSetListener edit_date_listener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                holiday_date.set(Calendar.YEAR, year);
                holiday_date.set(Calendar.MONTH, monthOfYear);
                holiday_date.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                string_edit_holiday_date = helper.createStringDate(year, monthOfYear, dayOfMonth);

                modal_edit_holiday_date.setText(helper.convertToReadableDate(string_edit_holiday_date));
            }
        };
        //Observers

        //Buttons
        modal_specific_location_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    modal_row_locations_affected.setVisibility(View.VISIBLE);
                } else {
                    modal_row_locations_affected.setVisibility(View.GONE);
                }
            }
        });

        modal_edit_specific_location_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    modal_edit_row_locations_affected.setVisibility(View.VISIBLE);
                } else {
                    modal_edit_row_locations_affected.setVisibility(View.GONE);
                }
            }
        });

        modal_holiday_date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DatePickerDialog(ctx, create_date_listener, holiday_date
                        .get(Calendar.YEAR), holiday_date.get(Calendar.MONTH),
                        holiday_date.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        modal_edit_holiday_date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DatePickerDialog(ctx, edit_date_listener, holiday_date
                        .get(Calendar.YEAR), holiday_date.get(Calendar.MONTH),
                        holiday_date.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        fab_create_holiday_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateHDialog.show();
            }
        });

        btn_h_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createHoliday();
            }
        });

//        btn_h_edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });

        close_create_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CreateHDialog.isShowing()) {
                    CreateHDialog.dismiss();
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
        //Buttons

    }

    private void setValues() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ctx);
        recyclerview_holidays.setHasFixedSize(true);
        recyclerview_holidays.setLayoutManager(layoutManager);
    }

    private void setHolidayTypesSpinner(List<HolidayType> i_holidayTypes) {
        List<String> spinnerArray = new ArrayList<>();

        spinnerArray.add("-----");
        for (int i = 0; i < i_holidayTypes.size(); i++) {

            HolidayType ht = i_holidayTypes.get(i);
            spinnerArray.add(ht.getHoliday_type_name());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                ctx, android.R.layout.simple_spinner_dropdown_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modal_spinner_holiday_type.setAdapter(adapter);
        modal_edit_spinner_holiday_type.setAdapter(adapter);
    }

    private void setLocationSpinner(List<CompanyLocation> i_locations) {
        List<String> spinnerArray = new ArrayList<>();

        for (int i = 0; i < i_locations.size(); i++) {

            CompanyLocation ct = i_locations.get(i);
            spinnerArray.add(ct.getBranch_name());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                ctx, android.R.layout.simple_spinner_dropdown_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modal_multiple_select_spinner_location.setItems(spinnerArray);
        modal_edit_multiple_select_spinner_location.setItems(spinnerArray);
    }

    private void createHoliday() {

        boolean holiday_name_set = modal_holiday_date.getText().toString().length() > 0;
        boolean holiday_date_set = string_create_holiday_date != null && string_create_holiday_date.length() > 0;
        boolean holiday_type_set = modal_spinner_holiday_type.getSelectedItemPosition() != 0;
        boolean holiday_locations_set = modal_multiple_select_spinner_location.getSelectedIndicies().size() > 0;
        boolean holiday_description_set = modal_holiday_description.getText().toString().length() > 0;

        if (holiday_name_set && holiday_date_set && holiday_type_set && holiday_description_set) {

            String added_by = user.getUser_id();
            String holiday_name = modal_holiday_name.getText().toString();
            String holiday_description = modal_holiday_description.getText().toString();

            int htp = modal_spinner_holiday_type.getSelectedItemPosition() - 1;
            HolidayType holiday_type = HolidayTypesList.get(htp);

            JSONObject holiday_locations = new JSONObject();
            List<Integer> li = modal_multiple_select_spinner_location.getSelectedIndicies();

            for (int i : li) {

                CompanyLocation company_location_id = CompanyLocationList.get(i);

                try {
                    holiday_locations.put(String.valueOf(i), company_location_id.getBranch_id());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            HolidaysViewModel.createHoliday(added_by, string_create_holiday_date, holiday_name, holiday_description, holiday_type.getHoliday_type_code(), holiday_locations);

        } else {
            Toasty.error(ctx, "Please fill all fields", Toasty.LENGTH_LONG).show();
        }

    }
}
