package com.example.timekeeping_beta.Fragments.UserApprover.Adjustment;

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

public class AdjustmentsUpdateFragment extends Fragment {

    public BottomNavigationView nav;
    private List<Adjustment> AdjustmentsList;

    private Context context;

    private RecyclerView recyclerviewAllAdjustments;
    private AdjustmentsAdapter adjustmentsAdapter;

    private EditText search_bar;
    private TextView no_data;
    private ProgressDialog loadingScreenDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_adjustment_approval, container, false);
        context = v.getContext();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        AdjustmentsList = new ArrayList<>();

        recyclerviewAllAdjustments = v.findViewById(R.id.recyclerviewAllAdjusments);
        no_data = v.findViewById(R.id.no_data);

        recyclerviewAllAdjustments.setHasFixedSize(true);
        recyclerviewAllAdjustments.setLayoutManager(layoutManager);

        //Menu
        nav = v.findViewById(R.id.navigation_adjustments_approve);
        nav.setOnNavigationItemSelectedListener(navListener);

        getAdjustments();

        return v;
    }

    private void getAdjustments() {
        whenLoading();

        AdjustmentsUpdateViewModel AdjustmentsViewModel = ViewModelProviders.of(this)
                .get(AdjustmentsUpdateViewModel.class);

//        AdjustmentsViewModel.retrieveAllAdjustments();

        AdjustmentsViewModel.getAdjustments().observe(
                this, new Observer<List<Adjustment>>() {
                    @Override
                    public void onChanged(@Nullable List<Adjustment> adjustments) {
                        AdjustmentsList = adjustments;

                        if (AdjustmentsList != null) {

                            if (AdjustmentsList.size() == 0) {
                                whenNoResult();
                            } else {
                                Collections.reverse(AdjustmentsList);
                                adjustmentsAdapter = new AdjustmentsAdapter(AdjustmentsList);
                                recyclerviewAllAdjustments.setAdapter(adjustmentsAdapter);
                                resultChecker(adjustmentsAdapter.filterByStatus("pending"));
                                adjustmentsAdapter.notifyDataSetChanged();
                            }
                        } else {
                            whenError();
                        }
                    }
                }
        );
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            if (adjustmentsAdapter != null) {

                switch (menuItem.getItemId()) {
                    case R.id.nav_pending:
                        resultChecker(adjustmentsAdapter.filterByStatus("pending"));
                        break;
                    case R.id.nav_approve:
                        resultChecker(adjustmentsAdapter.filterByStatus("approved"));
                        break;
                    case R.id.nav_decline:
                        resultChecker(adjustmentsAdapter.filterByStatus("declined"));
                        break;
                    case R.id.nav_all:
                        resultChecker(adjustmentsAdapter.showAll());
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
        recyclerviewAllAdjustments.setVisibility(View.GONE);

        loadingScreenDialog = ProgressDialog.show(context, null, "Please Wait...");
    }

    public void whenNoResult() {
        loadingScreenDialog.dismiss();
        recyclerviewAllAdjustments.setVisibility(View.GONE);

        no_data.setVisibility(View.VISIBLE);
    }

    public void whenError() {
        loadingScreenDialog.dismiss();
        recyclerviewAllAdjustments.setVisibility(View.GONE);
        no_data.setVisibility(View.GONE);

        TryAgainFragment tryAgainFragment = new TryAgainFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("RETURN_TO", Flag.ADJUSTMENT_UPDATE_FRAGMENT);
        tryAgainFragment.setArguments(arguments);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        if (fragmentManager != null){
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, tryAgainFragment)
                    .commit();
        }
        else{
            Toasty.error(context,"Fragment manager is empty.",Toasty.LENGTH_LONG).show();
        }
    }

    public void whenSuccess() {
        loadingScreenDialog.dismiss();
        no_data.setVisibility(View.GONE);

        recyclerviewAllAdjustments.setVisibility(View.VISIBLE);
    }
}
