package com.example.timekeeping_beta.Fragments.HRAdmin.Dashboard;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.timekeeping_beta.Fragments.HRAdmin.Dashboard.Models.EmployeeTopLates;
import com.example.timekeeping_beta.Globals.CustomClasses.Flag;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HRDashboardFragment extends Fragment implements OnChartValueSelectedListener {

    private TextView tv_on_time_count, tv_on_leave_count, tv_late_count, tv_absent_count;
    private ProgressBar tv_loading_data, tv_loading_bc_data;
    private CardView cv_on_time, cv_late, cv_on_leave, cv_absent;
    private SwipeRefreshLayout swipeRefresh;

    private PieChart pieChart;
    private BarChart barchart;

    private HRDashboardViewModel HRDashboardViewModel;
    private DashboardDailyAttendance DashboardDailyAttendance;
    private DashboardBundeeCount DashboardBundeeCount;
    private ArrayList<EmployeeTopLates> DashboardTopLateEmployees;

    private String tag;
    private String selectedRange = "today";
    private Spinner spnnr_top_lates_date_range;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hr_dashboard, container, false);

        if (getArguments() != null) {
            tag = getArguments().getString("fragment_tag");
        }

        swipeRefresh = v.findViewById(R.id.swipeRefresh);

        tv_on_time_count = v.findViewById(R.id.tv_on_time_count);
        tv_on_leave_count = v.findViewById(R.id.tv_on_leave_count);
        tv_late_count = v.findViewById(R.id.tv_late_count);
        tv_absent_count = v.findViewById(R.id.tv_absent_count);
        tv_loading_data = v.findViewById(R.id.tv_loading_data);
        tv_loading_bc_data = v.findViewById(R.id.tv_loading_bc_data);

        cv_on_time = v.findViewById(R.id.cv_on_time);
        cv_late = v.findViewById(R.id.cv_late);
        cv_on_leave = v.findViewById(R.id.cv_on_leave);
        cv_absent = v.findViewById(R.id.cv_absent);
        spnnr_top_lates_date_range = v.findViewById(R.id.spnnr_top_lates_date_range);


        pieChart = v.findViewById(R.id.pieChart);
        pieChart.setNoDataText("OOPS! Something went wrong");
        pieChart.setNoDataTextColor(R.color.colorGray);

        barchart = v.findViewById(R.id.barChart);
        barchart.setNoDataText("OOPS! Something went wrong");
        barchart.setNoDataTextColor(R.color.colorGray);

        init();

        setListeners();

        return v;
    }

    private void setListeners() {

        ArrayList listOptions = new ArrayList<String>();
        listOptions.add("Today");
        listOptions.add("Monthly");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item_layout, listOptions);
        spnnr_top_lates_date_range.setAdapter(adapter);
        spnnr_top_lates_date_range.setSelection(0, false);
        spnnr_top_lates_date_range.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent != null) {
                    selectedRange = parent.getSelectedItem().toString();
                    HRDashboardViewModel.retriveTop10Lates(selectedRange);

                    whenLoading(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //HRDashboardViewModel.retrieveHRDashboard();
                HRDashboardViewModel.retriveBundeeCount();
                HRDashboardViewModel.retriveBundeeEmployees();
                HRDashboardViewModel.retriveTop10Lates(selectedRange);
            }
        });

    }

    final Fragment goToFragment = new HRDashboardEmployeeList();

    private void setCardListeners() {

        cv_on_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Bundle b = new Bundle();
                b.putInt("employees_list_flag", Flag.ON_TIME_EMPLOYEES);
                b.putString("parent_tag", tag);
                goToFragment.setArguments(b);

                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .add(R.id.fragment_container, goToFragment, HRDashboardEmployeeList.class.getName())
                        .addToBackStack(HRDashboardEmployeeList.class.getName())
                        .commit();
            }
        });

        cv_late.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Bundle b = new Bundle();
                b.putInt("employees_list_flag", Flag.LATE_EMPLOYEES);
                b.putString("parent_tag", tag);
                goToFragment.setArguments(b);

                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .add(R.id.fragment_container, goToFragment, HRDashboardEmployeeList.class.getName())
                        .addToBackStack(HRDashboardEmployeeList.class.getName())
                        .commit();
            }
        });

        cv_on_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Bundle b = new Bundle();
                b.putInt("employees_list_flag", Flag.ON_LEAVE_EMPLOYEES);
                b.putString("parent_tag", tag);
                goToFragment.setArguments(b);

                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .add(R.id.fragment_container, goToFragment, HRDashboardEmployeeList.class.getName())
                        .addToBackStack(HRDashboardEmployeeList.class.getName())
                        .commit();
            }
        });
        cv_absent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Bundle b = new Bundle();
                b.putInt("employees_list_flag", Flag.ABSENT_EMPLOYEES);
                b.putString("parent_tag", tag);
                goToFragment.setArguments(b);

                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .add(R.id.fragment_container, goToFragment, HRDashboardEmployeeList.class.getName())
                        .addToBackStack(HRDashboardEmployeeList.class.getName())
                        .commit();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void init() {

        whenLoading(false);

        if (getActivity() != null) {
            HRDashboardViewModel = ViewModelProviders.of(getActivity()).get(HRDashboardViewModel.class);
        }


//        if (HRDashboardViewModel.getDashboardBundeeCount().getValue() != null) {
//            DashboardDailyAttendance = HRDashboardViewModel.getDashboardDailyAttendance().getValue();
//            setValues(DashboardDailyAttendance);
//            whenSuccess();
//        } else {
//            HRDashboardViewModel.retrieveHRDashboard();
//
//            HRDashboardViewModel.getDashboardDailyAttendance().observe(this, new Observer<com.example.timekeeping_beta.Fragments.HRAdmin.Dashboard.DashboardDailyAttendance>() {
//                @Override
//                public void onChanged(@Nullable DashboardDailyAttendance dashboardDailyAttendance) {
//                    if (dashboardDailyAttendance != null) {
//                        DashboardDailyAttendance = dashboardDailyAttendance;
//                        setValues(dashboardDailyAttendance);
//                    }
//                }
//            });
//        }


        if (HRDashboardViewModel.getDashboardDailyAttendance().getValue() != null) {
            DashboardBundeeCount = HRDashboardViewModel.getDashboardBundeeCount().getValue();
            setToChart(DashboardBundeeCount);
            whenSuccess();
        } else {
            HRDashboardViewModel.retriveBundeeCount();

            HRDashboardViewModel.getDashboardBundeeCount().observe(this, new Observer<DashboardBundeeCount>() {
                @Override
                public void onChanged(@Nullable DashboardBundeeCount dashboardBundeeCount) {
                    if (dashboardBundeeCount != null) {

                        DashboardBundeeCount = dashboardBundeeCount;
                        setToChart(dashboardBundeeCount);
                    }
                    whenSuccess();
                }
            });
        }

        if (HRDashboardViewModel.getBundeeEmployees().getValue() == null) {
            HRDashboardViewModel.retriveBundeeEmployees();
        }

        if (HRDashboardViewModel.getTopLateEmployees().getValue() != null) {
            DashboardTopLateEmployees = HRDashboardViewModel.getTopLateEmployees().getValue();
            setToBarChart();
            whenSuccess();
        } else {
            HRDashboardViewModel.retriveTop10Lates(selectedRange);

            HRDashboardViewModel.getTopLateEmployees().observe(this, new Observer<ArrayList<EmployeeTopLates>>() {
                @Override
                public void onChanged(@Nullable ArrayList<EmployeeTopLates> topLates) {
                    if (topLates != null) {

                        DashboardTopLateEmployees = topLates;
                        setToBarChart();
                    }
                    whenSuccess();
                }
            });
        }
    }


    private void setToBarChart() {

        //BarDataSet barDataSet = new BarDataSet(getData(), "Inducesmile");
        BarDataSet barDataSet = new BarDataSet(getData(), "");
        barchart.getDescription().setText("");
        barchart.getLegend().setWordWrapEnabled(true);
        barchart.getLegend().setEnabled(false);


        barDataSet.setBarBorderWidth(0.9f);
        barDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        BarData barData = new BarData(barDataSet);
        XAxis xAxis = barchart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        IndexAxisValueFormatter formatter = new IndexAxisValueFormatter(getLabels());
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);
        barchart.setData(barData);
        barchart.setFitBars(true);
        barchart.animateXY(5000, 5000);
        barchart.invalidate();
        barchart.setVisibility(View.VISIBLE);
    }

    private String[] getLabels() {
        String[] labels = new String[10];

        int index = 0;
        if (!DashboardTopLateEmployees.isEmpty()) {

            for (EmployeeTopLates etl : DashboardTopLateEmployees) {
                //labels[index] = DashboardTopLateEmployees.get(index).getFname() + " " + DashboardTopLateEmployees.get(index).getLname();
                labels[index] = DashboardTopLateEmployees.get(index).getFname();
                index++;
            }
        }
        return labels;
    }

    private ArrayList getData() {
        ArrayList<BarEntry> entries = new ArrayList<>();

        int index = 0;
        if (!DashboardTopLateEmployees.isEmpty()) {

            for (EmployeeTopLates etl : DashboardTopLateEmployees) {

                float lateInHours = etl.getMinutesLate() / 60f;
                entries.add(new BarEntry(index, lateInHours));
                index++;
            }
        }

        return entries;
    }

    private void setValues(DashboardDailyAttendance dashboardDailyAttendance) {

        String moreThan99 = "99+";

        int on_time_count = dashboardDailyAttendance.getOnTimeEmployeeCount();
        int on_leave_count = dashboardDailyAttendance.getOnLeaveCount();
        int late_count = dashboardDailyAttendance.getLateEmployeesCount();
        int absent_count = dashboardDailyAttendance.getAbsentCount();

        tv_on_time_count.setText(on_time_count > 99 ? moreThan99 : String.valueOf(on_time_count));
        tv_on_leave_count.setText(on_leave_count > 99 ? moreThan99 : String.valueOf(on_leave_count));
        tv_late_count.setText(late_count > 99 ? moreThan99 : String.valueOf(late_count));
        tv_absent_count.setText(absent_count > 99 ? moreThan99 : String.valueOf(absent_count));

        setCardListeners();
    }

    private ArrayList pieEntries, PieEntryLabels;
    private PieData pieData;
    private PieDataSet pieDataSet;

    private void setToChart(DashboardBundeeCount dashboardBundeeCount) {

        pieChart.setOnChartValueSelectedListener(this);
        pieEntries = new ArrayList<>();

        JSONArray counts = dashboardBundeeCount.getCount();

        if (counts.length() > 0) {
            for (int i = 0; i < counts.length(); i++) {
                try {
                    JSONObject jo = counts.getJSONObject(i);
                    String bundee = jo.getString("bundee");
                    int in = jo.getInt("in");

                    pieEntries.add(new PieEntry((float) in, bundee));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //pieDataSet = new PieDataSet(pieEntries, String.valueOf(dashboardBundeeCount.getTotal_in()));
        pieDataSet = new PieDataSet(pieEntries, "");

        pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);

        pieChart.getDescription().setText("");

        //Disable legend
        pieChart.getLegend().setWordWrapEnabled(true);
        pieChart.getLegend().setEnabled(false);

        pieChart.setCenterText("Total: " + (dashboardBundeeCount.getTotal_in()));
        pieChart.setCenterTextSize(Helper.getInstance(getActivity().getBaseContext()).integerToDP(10));
        pieChart.setCenterTextColor(Color.BLACK);

        pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        pieDataSet.setSliceSpace(2f);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(10f);
        pieDataSet.setSliceSpace(5f);

        pieChart.invalidate();
        pieChart.setVisibility(View.VISIBLE);
    }

    private void whenLoading(Boolean loadingFromFilter) {

        if (loadingFromFilter) {
            barchart.setVisibility(View.GONE);
            tv_loading_bc_data.setVisibility(View.VISIBLE);
        } else {

            swipeRefresh.setEnabled(false);
            swipeRefresh.setRefreshing(true);

            pieChart.setVisibility(View.GONE);
            barchart.setVisibility(View.GONE);
            tv_loading_data.setVisibility(View.VISIBLE);
            tv_loading_bc_data.setVisibility(View.VISIBLE);
        }

    }

    private void whenSuccess() {

        if (DashboardTopLateEmployees != null && DashboardBundeeCount != null) {

            swipeRefresh.setEnabled(true);
            swipeRefresh.setRefreshing(false);
        }

        tv_loading_data.setVisibility(View.GONE);
        tv_loading_bc_data.setVisibility(View.GONE);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {


        PieEntry pe = (PieEntry) e;
        String label = pe.getLabel();

        final Bundle b = new Bundle();
        b.putInt("employees_list_flag", Flag.BUNDEE_EMPLOYEES);
        b.putString("parent_tag", tag);
        b.putString("bundee", label);
        goToFragment.setArguments(b);

        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .add(R.id.fragment_container, goToFragment, HRDashboardEmployeeList.class.getName())
                .addToBackStack(HRDashboardEmployeeList.class.getName())
                .commit();
    }

    @Override
    public void onNothingSelected() {

    }
}
