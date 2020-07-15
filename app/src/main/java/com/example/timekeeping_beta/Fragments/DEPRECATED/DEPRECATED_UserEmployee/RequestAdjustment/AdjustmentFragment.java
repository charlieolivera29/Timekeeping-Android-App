package com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestAdjustment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.timekeeping_beta.R;
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestAdjustment.AdjusmentFragments.ApprovedAdjustmentsFragment;
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestAdjustment.AdjusmentFragments.DeclinedAdjustmentsFragment;
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestAdjustment.AdjusmentFragments.PendingAdjustmentsFragment;
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestAdjustment.AdjusmentFragments.RequestFragment;


public class AdjustmentFragment extends Fragment {

    public BottomNavigationView nav;
    private FloatingActionButton fab_make_request;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_adjustment_request, container, false);
        nav = v.findViewById(R.id.navigation_adjustment_request);
        fab_make_request = v.findViewById(R.id.fab_make_request);
        nav.setOnNavigationItemSelectedListener(navListener);

        fab_make_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //fab_make_request.setVisibility(View.GONE);
                fab_make_request.hide();
                getFragmentManager().beginTransaction().replace(R.id.fragment_adjustment_container, new RequestFragment()).commit();
            }
        });

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_adjustment_container, new PendingAdjustmentsFragment())
                .commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;

            switch (menuItem.getItemId()) {
                case R.id.nav_pending:
                    selectedFragment = new PendingAdjustmentsFragment();
                    break;
                case R.id.nav_approve:
                    selectedFragment = new ApprovedAdjustmentsFragment();
                    break;
                case R.id.nav_decline:
                    selectedFragment = new DeclinedAdjustmentsFragment();
                    break;
            }

            //fab_make_request.setVisibility(View.VISIBLE);
            fab_make_request.show();
            getFragmentManager().beginTransaction().replace(R.id.fragment_adjustment_container, selectedFragment).commit();

            return true;
        }
    };
}
