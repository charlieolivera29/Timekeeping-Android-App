package com.example.timekeeping_beta.Fragments.UserApprover.Overtime;

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

public class OvertimeUpdateFragment extends Fragment {

    private Context context;

    private List<Overtime> OvertimeList;

    private RecyclerView recyclerviewAllOvertime;
    private EditText search_bar;
    public BottomNavigationView nav;

    private OvertimesAdapter mAdapter;
    private ProgressDialog loadingScreenDialog;

    private TextView no_data;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_overtime_approvals, container, false);
        context = v.getContext();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        OvertimeList = new ArrayList<>();

        recyclerviewAllOvertime = v.findViewById(R.id.recyclerviewAllOvertime);
        no_data = v.findViewById(R.id.no_data);
        whenLoading();

        recyclerviewAllOvertime.setHasFixedSize(true);
        recyclerviewAllOvertime.setLayoutManager(layoutManager);

        nav = v.findViewById(R.id.navigation_overtime_approve);
        nav.setOnNavigationItemSelectedListener(navListener);

        mAdapter = new OvertimesAdapter(OvertimeList);
        Collections.reverse(OvertimeList);
        recyclerviewAllOvertime.setAdapter(mAdapter);
        //resultChecker(mAdapter.filterByStatus("pending"));

        OvertimesUpdateViewModel OvertimesUpdateViewModel = ViewModelProviders.of(this)
                .get(OvertimesUpdateViewModel.class);

        //OvertimesUpdateViewModel.retrieveOvertimes();

        OvertimesUpdateViewModel.getOvertimes().observe(
                this, new Observer<List<Overtime>>() {

                    @Override
                    public void onChanged(@Nullable List<Overtime> overtimes) {
                        OvertimeList = overtimes;

                        if (OvertimeList != null) {
                            if (OvertimeList.size() == 0){
                                whenNoResult();
                            }else{
                                mAdapter = new OvertimesAdapter(OvertimeList);
                                Collections.reverse(OvertimeList);
                                recyclerviewAllOvertime.setAdapter(mAdapter);
                                resultChecker(mAdapter.filterByStatus("pending"));
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            whenError();
                        }
                    }
                });


        return v;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

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
        recyclerviewAllOvertime.setVisibility(View.GONE);
        loadingScreenDialog = ProgressDialog.show(context, null, "Please Wait...");
    }

    public void whenSuccess() {
        loadingScreenDialog.dismiss();
        no_data.setVisibility(View.GONE);
        recyclerviewAllOvertime.setVisibility(View.VISIBLE);
    }

    public void whenNoResult() {
        loadingScreenDialog.dismiss();
        recyclerviewAllOvertime.setVisibility(View.GONE);
        no_data.setVisibility(View.VISIBLE);
    }

    public void whenError() {

        loadingScreenDialog.dismiss();
        recyclerviewAllOvertime.setVisibility(View.GONE);
        no_data.setVisibility(View.GONE);

        TryAgainFragment tryAgainFragment = new TryAgainFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("RETURN_TO", Flag.APPROVERS_OVERTIME_FRAGMENT);
        tryAgainFragment.setArguments(arguments);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        if (fragmentManager != null) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, tryAgainFragment)
                    .commit();
        } else {
            Toasty.error(context, "Fragment manager is empty.", Toasty.LENGTH_LONG).show();
        }
    }
}
