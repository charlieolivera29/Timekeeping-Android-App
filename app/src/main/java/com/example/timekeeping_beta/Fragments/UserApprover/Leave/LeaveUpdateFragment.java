package com.example.timekeeping_beta.Fragments.UserApprover.Leave;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.timekeeping_beta.Fragments.Retry.TryAgainFragment;
import com.example.timekeeping_beta.Globals.CustomClasses.Flag;
import com.example.timekeeping_beta.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class LeaveUpdateFragment extends Fragment {

    private Context context;

    public BottomNavigationView nav;
    private List<Leave> LeaveAdjustmentsList;

    private RecyclerView recyclerviewAllLeaves;
    private LeaveAdjustmentAdapter mAdapter;

    private EditText search_bar;
    private TextView no_data;

    private ProgressDialog loadingScreenDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_timesheet_approval, container, false);
        context = v.getContext();


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        LeaveAdjustmentsList = new ArrayList<>();

        recyclerviewAllLeaves = v.findViewById(R.id.recyclerviewAllTimesheetsAdjusments);
        no_data = v.findViewById(R.id.no_data);
        whenLoading();

        LeaveUpdateViewModel leaveUpdateViewModel = ViewModelProviders.of(this)
                .get(LeaveUpdateViewModel.class);

        //leaveUpdateViewModel.retrieveAllLeaves();

        leaveUpdateViewModel.getLeaves().observe(this
                , new Observer<List<Leave>>() {
                    @Override
                    public void onChanged(@Nullable List<Leave> leaveAdjustments) {
                        LeaveAdjustmentsList = leaveAdjustments;

                        if (LeaveAdjustmentsList != null) {
                            if (LeaveAdjustmentsList.size() == 0) {
                                whenNoResult();
                            } else {
                                Collections.reverse(LeaveAdjustmentsList);
                                mAdapter = new LeaveAdjustmentAdapter(LeaveAdjustmentsList);
                                recyclerviewAllLeaves.setAdapter(mAdapter);
                                resultChecker(mAdapter.filterByStatus("pending"));
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            whenError();
                        }

                    }
                });

        recyclerviewAllLeaves.setHasFixedSize(true);
        recyclerviewAllLeaves.setLayoutManager(layoutManager);

        nav = v.findViewById(R.id.navigation_timesheet_adjustments_approve);
        nav.setOnNavigationItemSelectedListener(navListener);

        return v;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            //Add if empty checker
            if (mAdapter != null) {

                switch (menuItem.getItemId()) {
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
            }
            return true;
        }
    };

    private void resultChecker(Boolean b) {

        if (!b) {
            whenNoResult();
            //no_data.setVisibility(View.VISIBLE);
        } else {
            whenSuccess();
            //no_data.setVisibility(View.GONE);
        }
    }

    public void whenLoading() {
        no_data.setVisibility(View.GONE);
        recyclerviewAllLeaves.setVisibility(View.GONE);
        loadingScreenDialog = ProgressDialog.show(context, null, "Please Wait...");
    }

    public void whenSuccess() {
        loadingScreenDialog.dismiss();
        no_data.setVisibility(View.GONE);
        recyclerviewAllLeaves.setVisibility(View.VISIBLE);
    }

    public void whenNoResult() {
        loadingScreenDialog.dismiss();
        recyclerviewAllLeaves.setVisibility(View.GONE);
        no_data.setVisibility(View.VISIBLE);
    }

    public void whenError() {

        loadingScreenDialog.dismiss();
        recyclerviewAllLeaves.setVisibility(View.GONE);
        no_data.setVisibility(View.GONE);

        TryAgainFragment tryAgainFragment = new TryAgainFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("RETURN_TO", Flag.APPROVERS_LEAVE_FRAGMENT);
        tryAgainFragment.setArguments(arguments);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        if (fragmentManager != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, tryAgainFragment)
                    .commit();
        } else {
            Toasty.error(context, "Fragment manager is empty.", Toasty.LENGTH_LONG).show();
        }
    }

}

