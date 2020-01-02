package com.example.timekeeping_beta.Fragments.UserApprover.ApproveeDetails;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.timekeeping_beta.Fragments.UserApprover.ApproveeTimesheet.ApproveeTimesheetFragment;
import com.example.timekeeping_beta.Fragments.DEPRECATED.ApproversEmployeesFragment;
import com.example.timekeeping_beta.Fragments.UserApprover.Approvee.ApproveeProfileViewModel;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.R;
import com.example.timekeeping_beta.Globals.Models.UserProfileAll;

import es.dmoral.toasty.Toasty;

public class ApproveeDetailFragment extends Fragment {

    private View v;
    private String user_id;

    private ImageView appbar_approvee_image;
    private TextView approvee_name;
    private TextView approvee_email;


    private LinearLayout approvee_bio, gen_info_container;

    private ProgressDialog loadingScreenDialog;

    private ApproveeProfileViewModel ApproveeProfileViewModel;

    private UserProfileAll LiveDataApprovee;

    private static final int NUM_PAGES = 1;
    private ViewPager mPager;
    private PagerAdapter pagerAdapter;

    private TextView approvee_cell_number, approvee_company_name, approvee_schedule, approvee_position, approvee_id, toggle_more_info;
    private LinearLayout user_detail_container;

    @Override
    public void onResume() {
        super.onResume();

        LinearLayout user_detail_container = getActivity().findViewById(R.id.user_detail_container);
        user_detail_container.setVisibility(View.VISIBLE);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_approvee, container, false);
        user_id = getArguments().getString("user_id");
        initViews();
        setListeners();

//        mPager = v.findViewById(R.id.pager);
//        pagerAdapter = new ScreenSlidePagerAdapter(getActivity().getSupportFragmentManager());
//        mPager.setAdapter(pagerAdapter);
//        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
//
//        TabLayout tabLayout = v.findViewById(R.id.tabDots);
//        tabLayout.setupWithViewPager(mPager, true);

        ApproveeProfileViewModel = ViewModelProviders.of(this)
                .get(ApproveeProfileViewModel.class);

        getApprovees();

        ApproveeProfileViewModel.getLiveApprovee().observe(this, new Observer<UserProfileAll>() {
            @Override
            public void onChanged(@Nullable UserProfileAll userProfileAll) {

                if (userProfileAll != null) {
                    LiveDataApprovee = userProfileAll;
                    setData(userProfileAll);
                    whenSuccess();
                } else {
                    whenError();
                }
            }
        });

        return v;
    }

    private void setListeners() {
        user_detail_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (gen_info_container.getVisibility() == View.GONE) {
                    toggle_more_info.setText("Hide");
                    gen_info_container.setVisibility(View.VISIBLE);
                } else {
                    toggle_more_info.setText("See More...");
                    gen_info_container.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initViews() {

        FragmentActivity v = getActivity();

        appbar_approvee_image = v.findViewById(R.id.appbar_approvee_image);
        approvee_name = v.findViewById(R.id.approvee_name);
        approvee_email = v.findViewById(R.id.approvee_email);
        approvee_position = v.findViewById(R.id.approvee_position);
        approvee_cell_number = v.findViewById(R.id.approvee_cell_number);
        approvee_company_name = v.findViewById(R.id.approvee_company_name);
        approvee_schedule = v.findViewById(R.id.approvee_schedule);
        approvee_id = v.findViewById(R.id.approvee_id);
        toggle_more_info = v.findViewById(R.id.toggle_more_info);
        gen_info_container = v.findViewById(R.id.gen_info_container);
        user_detail_container = v.findViewById(R.id.user_detail_container);
    }

    public void getApprovees() {
        whenLoading();
        ApproveeProfileViewModel.retrieveUserDashboard(user_id);
    }

    public void setData(UserProfileAll a) {
        approvee_name.setText(a.getFname() + " " + a.getLname());
        approvee_email.setText(a.getEmail());

        approvee_cell_number.setText(a.getContact_numner());
        approvee_company_name.setText(a.getCompany());
        approvee_schedule.setText(a.getShift_in() + " to " + a.getShift_out());
        approvee_position.setText(a.getRole_name());
        approvee_id.setText(user_id);


        Glide.with(getContext())
                .load(a.getImage_file_name())
                .error(R.drawable.ic_person_white_24dp)
                .thumbnail(
                        Glide.with(getContext())
                                .load(Helper.getInstance(getContext()).getCircleAnimation()))
                .apply(RequestOptions.circleCropTransform())
                .override(500, 500)
                .into(appbar_approvee_image);


        Bundle bundle = new Bundle();
        bundle.putString("user_id", user_id);

        ApproveeTimesheetFragment approveeTimesheetFragment = new ApproveeTimesheetFragment();
        approveeTimesheetFragment.setArguments(bundle);

        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.approvee_timesheet_container, approveeTimesheetFragment, approveeTimesheetFragment.getClass().getName())
                .commit();
    }

    public void whenLoading() {
        loadingScreenDialog = ProgressDialog.show(getContext(), null, "Please Wait...");
    }

    public void whenError() {
        loadingScreenDialog.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setTitle("Server Error!");
        builder.setMessage("Could not retrieve employee's data. Please Retry.");

        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getApprovees();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                if (fragmentManager != null) {
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.approvee_timesheet_container, new ApproversEmployeesFragment())
                            .commit();
                }
            }
        });
        AlertDialog dialog = builder.create(); // calling builder.create after adding buttons
        dialog.show();
        Toasty.error(getContext(), "Network Unavailable!", Toasty.LENGTH_LONG).show();
    }

    public void whenSuccess() {
        loadingScreenDialog.dismiss();
    }

}
