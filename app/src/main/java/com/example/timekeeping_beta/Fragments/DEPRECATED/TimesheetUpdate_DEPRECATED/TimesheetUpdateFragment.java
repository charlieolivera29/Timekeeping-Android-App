package com.example.timekeeping_beta.Fragments.DEPRECATED.TimesheetUpdate_DEPRECATED;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.timekeeping_beta.Fragments.UserApprover.EDTR.EDTRAdjustment;
import com.example.timekeeping_beta.Fragments.UserApprover.EDTR.TimesheetAdjustment;
import com.example.timekeeping_beta.Fragments.UserApprover.EDTR.TimesheetAdjustmentViewModel;
import com.example.timekeeping_beta.R;

import java.util.ArrayList;
import java.util.List;

public class TimesheetUpdateFragment extends Fragment {

    public BottomNavigationView nav;
    private List<TimesheetAdjustment> TimesheetAdjustmentsList;

    private RecyclerView recyclerviewAllTimesheetsAdjusments;
    private TimesheetsAdjustmentsAdapter mAdapter;

    private EditText search_bar;
    private TextView no_data;
    private ProgressBar loading_screen;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_timesheet_approval, container, false);
        Context context = v.getContext();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        TimesheetAdjustmentsList = new ArrayList<>();

        recyclerviewAllTimesheetsAdjusments = v.findViewById(R.id.recyclerviewAllTimesheetsAdjusments);
        //search_bar = v.findViewById(R.id.search_bar);
        loading_screen = v.findViewById(R.id.loading_screen);
        no_data = v.findViewById(R.id.no_data);
        //whenLoading();

        TimesheetAdjustmentViewModel timesheetAdjustmentViewModel = ViewModelProviders.of(this)
                .get(TimesheetAdjustmentViewModel.class);

        //timesheetAdjustmentViewModel.retrieveAllTimesheetAdjustments();

        timesheetAdjustmentViewModel.getTimesheetAdjustments().observe(this
                , new Observer<List<EDTRAdjustment>>() {
                    @Override
                    public void onChanged(@Nullable List<EDTRAdjustment> timesheetAdjustments) {
//                        TimesheetAdjustmentsList = timesheetAdjustments;
//
//                        if (TimesheetAdjustmentsList != null) {
//                            whenSuccess();
//                            Collections.reverse(TimesheetAdjustmentsList);
//                            mAdapter = new TimesheetsAdjustmentsAdapter(TimesheetAdjustmentsList);
//                            recyclerviewAllTimesheetsAdjusments.setAdapter(mAdapter);
//                            resultChecker(mAdapter.filterByStatus("pending"));
//                            mAdapter.notifyDataSetChanged();
//                            //setSearchListener();
//                        }

                    }
                });


        recyclerviewAllTimesheetsAdjusments.setHasFixedSize(true);
        recyclerviewAllTimesheetsAdjusments.setLayoutManager(layoutManager);

        nav = v.findViewById(R.id.navigation_timesheet_adjustments_approve);
        nav.setOnNavigationItemSelectedListener(navListener);

        return v;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            //Add if empty checker

            switch (menuItem.getItemId()){
                case R.id.nav_pending:
                    resultChecker(mAdapter.filterByStatus("pending"));
                    break;
                case R.id.nav_approve:
                    resultChecker(mAdapter.filterByStatus("approved"));
                    break;
                case R.id.nav_decline:
                    resultChecker(mAdapter.filterByStatus("declined"));
                    break;
                case R.id.nav_all:
                    resultChecker(mAdapter.showAll());
                    break;
            }

            return true;
        }
    };

    private void resultChecker(Boolean b){

        if (!b) {
            no_data.setVisibility(View.VISIBLE);
        } else {
            no_data.setVisibility(View.GONE);
        }
    }

    public void whenLoading(){
        //recyclerviewAllAdjustments.setVisibility(View.GONE);
        no_data.setVisibility(View.GONE);
        loading_screen.setVisibility(View.VISIBLE);
    }

    public void whenSuccess(){
        //recyclerviewAllAdjustments.setVisibility(View.GONE);
        no_data.setVisibility(View.GONE);
        loading_screen.setVisibility(View.GONE);
    }


}
