package com.example.timekeeping_beta.Fragments.GlobalSettings.HolidayTypes;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.timekeeping_beta.Fragments.Retry.TryAgainFragment;
import com.example.timekeeping_beta.Globals.CustomClasses.Flag;
import com.example.timekeeping_beta.Globals.CustomClasses.Interface.RecyclerViewClickListener;
import com.example.timekeeping_beta.R;

import java.util.Collections;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class HolidayTypesFragment extends Fragment implements RecyclerViewClickListener {

    private View v;
    private Context ctx;
    private HolidayTypeViewModel HolidayTypesViewModel;
    private List<HolidayType> HolidayTypesList;

    private FloatingActionButton fab_create_holiday_type;
    private RecyclerView recyclerview_holiday_types;
    private TextView no_data;

    private ProgressDialog loadingScreenDialog;
    private HolidayTypesAdapter holidayTypesAdapter;

    private Dialog CreateHTDialog;
    private Dialog EditHTDialog;
    private Dialog ShowHTDialog;

    private Button btn_ht_type_create;
    private EditText modal_holiday_type_id;
    private EditText modal_holiday_type_name;
    private ImageButton close_create_dialog;

    private Button btn_ht_type_edit;
    private EditText modal_edit_holiday_type_id;
    private EditText modal_edit_holiday_type_name;
    private ImageButton close_edit_dialog;

    private String error_message;
    private String success_message;

    private int selected_ht_position;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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
        HolidayTypesViewModel.retrieveAllHolidayTypes();
        whenLoading();
    }

    private void init() {
        error_message = getResources().getString(R.string.api_request_failed);
        success_message = getResources().getString(R.string.api_request_success);

        EditHTDialog = new Dialog(ctx, R.style.AppTheme_NoActionBar);
        CreateHTDialog = new Dialog(ctx, R.style.AppTheme_NoActionBar);
        ShowHTDialog = new Dialog(ctx, R.style.AppTheme_NoActionBar);

        HolidayTypesViewModel = ViewModelProviders.of(this)
                .get(HolidayTypeViewModel.class);
    }

    private void initViews() {
        fab_create_holiday_type = v.findViewById(R.id.fab_create_item);
        recyclerview_holiday_types = v.findViewById(R.id.recyclerview_global_settings_list);
        no_data = v.findViewById(R.id.no_data);

        CreateHTDialog.setContentView(R.layout.dialog_type_create);
        EditHTDialog.setContentView(R.layout.dialog_type_edit);
        ShowHTDialog.setContentView(R.layout.dialog_type_show);

        btn_ht_type_create = CreateHTDialog.findViewById(R.id.btn_ht_type_create);
        modal_holiday_type_id = CreateHTDialog.findViewById(R.id.modal_holiday_type_id);
        modal_holiday_type_name = CreateHTDialog.findViewById(R.id.modal_holiday_type_name);
        close_create_dialog = CreateHTDialog.findViewById(R.id.img_fullscreen_dialog_close);

        btn_ht_type_edit = EditHTDialog.findViewById(R.id.btn_ht_type_create);
        modal_edit_holiday_type_id = EditHTDialog.findViewById(R.id.modal_holiday_type_id);
        modal_edit_holiday_type_name = EditHTDialog.findViewById(R.id.modal_holiday_type_name);
        close_edit_dialog = EditHTDialog.findViewById(R.id.img_fullscreen_dialog_close);
    }

    private void setListeners() {

        final HolidayTypesFragment that = this;

        HolidayTypesViewModel.getCreateHolidayTypeResult().observe(this, new Observer<String>() {
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
                    Toasty.error(ctx, error_message,Toasty.LENGTH_SHORT).show();
                }
            }
        });

        HolidayTypesViewModel.getEditHolidayTypeResult().observe(this, new Observer<String>() {
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

        HolidayTypesViewModel.getHolidaysTypes().observe(this, new Observer<List<HolidayType>>() {
            @Override
            public void onChanged(@Nullable List<HolidayType> holidayTypes) {
                HolidayTypesList = holidayTypes;

                if (HolidayTypesList != null) {

                    if (HolidayTypesList.size() == 0) {
                        whenNoResult();
                    } else {
                        Collections.reverse(HolidayTypesList);
                        holidayTypesAdapter = new HolidayTypesAdapter(HolidayTypesList, that);
                        recyclerview_holiday_types.setAdapter(holidayTypesAdapter);
                        holidayTypesAdapter.notifyDataSetChanged();
                        whenSuccess();
                    }
                } else {
                    whenError();
                }
            }
        });

        fab_create_holiday_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateHTDialog.show();
            }
        });

        btn_ht_type_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ht_code = modal_holiday_type_id.getText().toString();
                String ht_name = modal_holiday_type_name.getText().toString();

                if (ht_code.length() > 0 && ht_name.length() > 0) {
                    loadingScreenDialog = ProgressDialog.show(ctx, null, "Please Wait...");
                    HolidayTypesViewModel.createHolidayType(ht_code, ht_name);
                } else {
                    Toasty.error(ctx, getResources().getString(R.string.required_field_empty), Toasty.LENGTH_SHORT).show();
                }
            }
        });

        btn_ht_type_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ht_code = modal_edit_holiday_type_id.getText().toString();
                String ht_name = modal_edit_holiday_type_name.getText().toString();

                if (ht_code.length() > 0 && ht_name.length() > 0) {
                    loadingScreenDialog = ProgressDialog.show(ctx, null, "Please Wait...");
                    HolidayTypesViewModel.updateHolidayType(selected_ht_position,ht_code, ht_name);
                } else {
                    Toasty.error(ctx, getResources().getString(R.string.required_field_empty), Toasty.LENGTH_SHORT).show();
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
    }

    private void setValues() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ctx);

        recyclerview_holiday_types.setHasFixedSize(true);
        recyclerview_holiday_types.setLayoutManager(layoutManager);
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
        arguments.putInt("RETURN_TO", Flag.HOLIDAY_TYPES_FRAGMENT);
        tryAgainFragment.setArguments(arguments);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        if (fragmentManager != null){
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, tryAgainFragment)
                    .commit();
        }
        else{
            Toasty.error(ctx,"Fragment manager is empty.",Toasty.LENGTH_LONG).show();
        }
    }

    public void whenSuccess() {
        loadingScreenDialog.dismiss();
        no_data.setVisibility(View.GONE);

        recyclerview_holiday_types.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(int position,int flag) {

        HolidayType HT = HolidayTypesList.get(position);
        selected_ht_position = HT.getId() ;

        modal_edit_holiday_type_id.setText(HT.getHoliday_type_code());
        modal_edit_holiday_type_name.setText(HT.getHoliday_type_name());

        if (flag == Flag.CALLBACK_EDIT) {
            EditHTDialog.show();
        } else if (flag == Flag.CALLBACK_SHOW) {
            showHolidayType(HT);
        }
    }


    private void showHolidayType(HolidayType HT) {
        TextView rvi_title = ShowHTDialog.findViewById(R.id.rvi_title);
        TextView modal_holiday_type_id = ShowHTDialog.findViewById(R.id.modal_holiday_type_id);
        TextView modal_holiday_type_name = ShowHTDialog.findViewById(R.id.modal_holiday_type_name);

        rvi_title.setText("Holiday Type:");
        modal_holiday_type_id.setText(HT.getHoliday_type_code());
        modal_holiday_type_name.setText(HT.getHoliday_type_name());
        ShowHTDialog.findViewById(R.id.img_fullscreen_dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowHTDialog.dismiss();
            }
        });

        ShowHTDialog.show();
    }
}
