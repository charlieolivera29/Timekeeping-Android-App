package com.example.timekeeping_beta.Fragments.HRAdmin.Dashboard;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.timekeeping_beta.Fragments.DashBoard.UserDashboardViewModel;
import com.example.timekeeping_beta.Fragments.HRAdmin.Dashboard.Models.BundeeEmployee;
import com.example.timekeeping_beta.Fragments.HRAdmin.Dashboard.Models.NameNumberPair;
import com.example.timekeeping_beta.Fragments.UserApprover.Approvee.Models.Approvee;
import com.example.timekeeping_beta.Globals.CustomClasses.Flag;
import com.example.timekeeping_beta.Globals.CustomClasses.Interface.RecyclerViewClickListener;
import com.example.timekeeping_beta.R;

import java.util.ArrayList;

public class HRDashboardEmployeeList extends Fragment implements RecyclerViewClickListener {

    private HRDashboardViewModel hrDashboardViewModel;
    private UserDashboardViewModel userDashboardViewModel;
    ArrayList<Approvee> HR_employee_list;
    MutableLiveData<ArrayList<BundeeEmployee>> HR_bundee_employee_list;
    ArrayList<NameNumberPair> HR_name_number_pair_employee_list;
    private int employeeListFlag;
    private String parent_fragment_flag;

    private TextView tv_hrd_employeeList_title, tv_when_empty_message, tv_when_empty_secondary_message;
    private LinearLayout no_result;

    private RecyclerView rv_hrd_employeeList;
    private HRDashboardAdapter mAdapter;
    private BundeeEmployeeAdapter mAdapterBundee;
    private NameNumberPairAdapter nameNumberPairAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hrDashboardViewModel = ViewModelProviders.of(getActivity())
                .get(HRDashboardViewModel.class);

        userDashboardViewModel = ViewModelProviders.of(getActivity())
                .get(UserDashboardViewModel.class);

//        hrDashboardViewModel.retrieveHRDashboard();
//        hrDashboardViewModel.retriveBundeeCount();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard_employee_list, container, false);

        employeeListFlag = getArguments().getInt("employees_list_flag");
        parent_fragment_flag = getArguments().getString("parent_tag");
        rv_hrd_employeeList = v.findViewById(R.id.rv_hrd_employeeList);
        tv_hrd_employeeList_title = v.findViewById(R.id.tv_hrd_employeeList_title);
        tv_when_empty_message = v.findViewById(R.id.tv_when_empty_message);
        tv_when_empty_secondary_message = v.findViewById(R.id.tv_when_empty_secondary_message);
        no_result = v.findViewById(R.id.no_result);

        rv_hrd_employeeList.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(v.getContext());
        rv_hrd_employeeList.setLayoutManager(layoutManager);

        setListeners();

        setEmployeeList();
        return v;
    }

    private void setListeners() {

//        hrDashboardViewModel.getAbsentEmployees().observe(this, new Observer<ArrayList<Approvee>>() {
//            @Override
//            public void onChanged(@Nullable ArrayList<Approvee> Approvees) {
//                setEmployeeList();
//            }
//        });
    }


    @Override
    public void onItemClick(int position, int flag) {

    }

    private void setEmployeeList() {

        //Fragment parentFragment = getActivity().getSupportFragmentManager().findFragmentByTag(parent_fragment_flag);
        Fragment parentFragment = this;

        //MutableLiveData<ArrayList<Approvee>> liveApproveeList = null;

//        if (employeeListFlag == Flag.ON_TIME_EMPLOYEES) {
//            tv_hrd_employeeList_title.setText("On Time");
//            no_result_message = getResources().getString(R.string.title_no_late);
//
//            liveApproveeList = hrDashboardViewModel.getOnTimeEmployees();
//        } else if (employeeListFlag == Flag.LATE_EMPLOYEES) {
//            tv_hrd_employeeList_title.setText("Late");
//            no_result_message = getResources().getString(R.string.title_no_late);
//
//            liveApproveeList = hrDashboardViewModel.getLateEmployees();
//        } else if (employeeListFlag == Flag.ABSENT_EMPLOYEES) {
//            tv_hrd_employeeList_title.setText("Absent");
//            no_result_message = getResources().getString(R.string.title_no_absent);
//
//            liveApproveeList = hrDashboardViewModel.getAbsentEmployees();
//        } else if (employeeListFlag == Flag.ON_LEAVE_EMPLOYEES) {
//            tv_hrd_employeeList_title.setText("On Leave List");
//            no_result_message = getResources().getString(R.string.title_no_leave);
//
//            liveApproveeList = hrDashboardViewModel.getOnLeaveEmployees();
//        }

        //        if (liveApproveeList != null && liveApproveeList.getValue() != null && employeeListFlag != Flag.BUNDEE_EMPLOYEES) {
//            HR_employee_list = liveApproveeList.getValue();
//
//            if (HR_employee_list != null && HR_employee_list.size() > 0) {
//
//
//                mAdapter = new HRDashboardAdapter(HR_employee_list, getContext(), this);
//                rv_hrd_employeeList.setAdapter(null);
//                rv_hrd_employeeList.setAdapter(mAdapter);
//                mAdapter.notifyDataSetChanged();
//                whenHasData();
//            } else {
//                whenNoResult(no_result_message);
//            }
//        } else


        //MutableLiveData<ArrayList<BundeeEmployee>> liveBundeeEmployees = null;
        //MutableLiveData<ArrayList<NameNumberPair>> liveDashboardEmployees = null;


        String no_result_message = "Empty!";
        String no_result_message2 = "";


        if (employeeListFlag == Flag.BUNDEE_EMPLOYEES) {

            getActivity().setTitle("Present Employees");
            no_result_message2 = "No one is present today.";

            HR_bundee_employee_list = hrDashboardViewModel.getBundeeEmployees();
        } else if (employeeListFlag == Flag.DASHBOARD_OVERTIME_EMPLOYEES) {

            getActivity().setTitle("Overtimes");
            no_result_message2 = "No employee to display.";

            HR_name_number_pair_employee_list = userDashboardViewModel.getDashboardOvertimeNames().getValue();
        } else if (employeeListFlag == Flag.DASHBOARD_LATE_EMPLOYEES) {

            getActivity().setTitle("Overtimes");
            no_result_message2 = "No employee to display.";

            HR_name_number_pair_employee_list = userDashboardViewModel.getDashboardLatesNames().getValue();
        }


        if (employeeListFlag == Flag.BUNDEE_EMPLOYEES) {

            if (HR_bundee_employee_list != null && !HR_bundee_employee_list.getValue().isEmpty()) {

                mAdapterBundee = new BundeeEmployeeAdapter(HR_bundee_employee_list.getValue(), getContext(), this);
                rv_hrd_employeeList.setAdapter(null);
                rv_hrd_employeeList.setAdapter(mAdapterBundee);
                mAdapterBundee.notifyDataSetChanged();
                whenHasData();
            } else {
                whenNoResult(no_result_message, no_result_message2);
            }
        } else if (employeeListFlag == Flag.DASHBOARD_OVERTIME_EMPLOYEES) {

            if (HR_name_number_pair_employee_list != null && HR_name_number_pair_employee_list.size() > 0) {

                nameNumberPairAdapter = new NameNumberPairAdapter(HR_name_number_pair_employee_list, getContext(), this);
                rv_hrd_employeeList.setAdapter(null);
                rv_hrd_employeeList.setAdapter(nameNumberPairAdapter);
                nameNumberPairAdapter.notifyDataSetChanged();

                whenHasData();
            } else {
                whenNoResult(no_result_message, no_result_message2);
            }
        } else if (employeeListFlag == Flag.DASHBOARD_LATE_EMPLOYEES) {

            if (HR_name_number_pair_employee_list != null && HR_name_number_pair_employee_list.size() > 0) {

                nameNumberPairAdapter = new NameNumberPairAdapter(HR_name_number_pair_employee_list, getContext(), this);
                rv_hrd_employeeList.setAdapter(null);
                rv_hrd_employeeList.setAdapter(nameNumberPairAdapter);
                nameNumberPairAdapter.notifyDataSetChanged();
                whenHasData();
            } else {
                whenNoResult(no_result_message, no_result_message2);
            }
        }
    }

    private void whenNoResult(String message, String message2) {
        tv_when_empty_message.setText(message);
        tv_when_empty_secondary_message.setText(message2);

        rv_hrd_employeeList.setVisibility(View.GONE);
        no_result.setVisibility(View.VISIBLE);
    }

    private void whenHasData() {
        no_result.setVisibility(View.GONE);
        rv_hrd_employeeList.setVisibility(View.VISIBLE);
    }

}


