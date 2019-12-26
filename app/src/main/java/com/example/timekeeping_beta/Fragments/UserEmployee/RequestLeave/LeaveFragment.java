package com.example.timekeeping_beta.Fragments.UserEmployee.RequestLeave;

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

import com.example.timekeeping_beta.Fragments.UserEmployee.RequestAdjustment.AdjusmentFragments.RequestFragment;
import com.example.timekeeping_beta.R;
import com.example.timekeeping_beta.Fragments.UserEmployee.RequestLeave.LeaveFragments.ApprovedLeaveFragment;
import com.example.timekeeping_beta.Fragments.UserEmployee.RequestLeave.LeaveFragments.DeclinedLeaveFragment;
import com.example.timekeeping_beta.Fragments.UserEmployee.RequestLeave.LeaveFragments.PendingLeaveFragment;
import com.example.timekeeping_beta.Fragments.UserEmployee.RequestLeave.LeaveFragments.RequestLeaveFragment;

public class LeaveFragment extends Fragment {

    public BottomNavigationView nav;
    private FloatingActionButton fab_make_request;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leave_request, container, false);
        nav = view.findViewById(R.id.navigation_leave_request);
        nav.setOnNavigationItemSelectedListener(navListener);
        fab_make_request = view.findViewById(R.id.fab_make_request);

        fab_make_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //fab_make_request.setVisibility(View.GONE);
                fab_make_request.hide();
                getFragmentManager().beginTransaction().replace(R.id.fragment_leave_container, new RequestLeaveFragment()).commit();
            }
        });


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(R.id.fragment_leave_container, new PendingLeaveFragment()).commit();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;

            switch (menuItem.getItemId()){
                case R.id.nav_pending:
                    selectedFragment = new PendingLeaveFragment();
                    break;
                case R.id.nav_approve:
                    selectedFragment = new ApprovedLeaveFragment();
                    break;
                case R.id.nav_decline:
                    selectedFragment = new DeclinedLeaveFragment();
                    break;
            }

            //fab_make_request.setVisibility(View.VISIBLE);
            fab_make_request.show();
            getFragmentManager().beginTransaction().replace(R.id.fragment_leave_container, selectedFragment).commit();

            return true;
        }
    };
}
