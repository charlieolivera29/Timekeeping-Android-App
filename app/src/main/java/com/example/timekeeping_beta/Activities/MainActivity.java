package com.example.timekeeping_beta.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.timekeeping_beta.Fragments.DashBoardVersion2.DashboardSliderFragment;
import com.example.timekeeping_beta.Fragments.DashBoardVersion2.UserDashboardFragment;
import com.example.timekeeping_beta.Fragments.Dashboard.ViewModel.DashboardViewModel;
import com.example.timekeeping_beta.Fragments.GlobalSettings.HolidayTypes.HolidayTypesFragment;
import com.example.timekeeping_beta.Fragments.GlobalSettings.HolidayManagement.HolidaysFragment;
import com.example.timekeeping_beta.Fragments.GlobalSettings.LeaveTypes.LeaveTypesFragment;
import com.example.timekeeping_beta.Fragments.GlobalSettings.LocationManagement.CompanyLocationsFragment;
import com.example.timekeeping_beta.Fragments.GlobalSettings.ScheduleManagement.SchedulesFragment;
import com.example.timekeeping_beta.Fragments.HRAdmin.Dashboard.HRDashboardEmployeeList;
import com.example.timekeeping_beta.Fragments.HRAdmin.Dashboard.HRDashboardFragment;
import com.example.timekeeping_beta.Fragments.HRAdmin.Employees.EmployeesFragment;
import com.example.timekeeping_beta.Fragments.MobileTimeEntry.MobileTimeEntryFragment;
import com.example.timekeeping_beta.Fragments.UserApprover.Adjustment.AdjustmentsUpdateFragmentv2;
import com.example.timekeeping_beta.Fragments.UserApprover.ApproveeDetails.ApproveeDetailFragment;
import com.example.timekeeping_beta.Fragments.UserApprover.EDTR.EDTRUpdateFragmentv2;
import com.example.timekeeping_beta.Fragments.UserApprover.Leave.LeaveUpdateFragmentv2;
import com.example.timekeeping_beta.Fragments.UserApprover.Overtime.OvertimeUpdateFragmentv2;
import com.example.timekeeping_beta.Fragments.UserApprover2.Approvees.ApproveesFragment;
import com.example.timekeeping_beta.Fragments.UserEmployee.RequestAdjustment.AdjusmentFragments.RequestFragment;
import com.example.timekeeping_beta.Fragments.UserEmployee.RequestEDTR.TimesheetEntryFragment;
import com.example.timekeeping_beta.Fragments.UserEmployee2.Adjustments.AdjustmentsFragment;
import com.example.timekeeping_beta.Fragments.UserEmployee2.Leaves.LeavesFragment;
import com.example.timekeeping_beta.Fragments.UserEmployee2.Overtimes.OvertimeFragment;
import com.example.timekeeping_beta.Globals.CustomClasses.BadgeDrawerArrowDrawable;
import com.example.timekeeping_beta.Globals.Static;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.R;
import com.example.timekeeping_beta.Fragments.Dashboard.DashboardFragment;
import com.example.timekeeping_beta.Fragments.Timesheet.TimesheetFragment;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.Globals.Models.User;
import com.example.timekeeping_beta.Fragments.Profile.UserProfileFragment;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import es.dmoral.toasty.Toasty;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.lang.Boolean.TRUE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        FragmentManager.OnBackStackChangedListener {

    private Context ctx;
    private Helper helper;
    //private User user = SharedPrefManager.getInstance(this).getUser();
    private User user;
    private com.example.timekeeping_beta.Activities.ViewModels.MainActivityViewModel mainActivityViewModel;
    private DashboardViewModel dashboardViewModel;
    final URLs url = new URLs();

    public NavigationView navigationView;
    public TextView name, email;
    private ImageView btnImage;

    private LinearLayout user_bio_layout;
    private Dialog about_us_dialog;
    private Integer PERMISSIONS = 69;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;


    private BadgeDrawerArrowDrawable badgeDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx = this;
        helper = Helper.getInstance(ctx);
        user = SharedPrefManager.getInstance(ctx).getUser();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        initViews();
        init();
        setListeners();
        mainAppBarManager();

        // sets the fragment to dashboard
        if (savedInstanceState == null) {

            //changeFragment("DASHBOARD_FRAGMENT", new UserDashboardFragment());
            //changeFragment("DASHBOARD_FRAGMENT", new UserDashboardFragment());

            DashboardSliderFragment dashboardSliderFragment = new DashboardSliderFragment();
            Bundle b = new Bundle();
            b.putInt("number_pages", Integer.valueOf(user.getRole_ID()) == 5241 ? 1 : 2);
            dashboardSliderFragment.setArguments(b);
            changeFragment("DashboardSliderFragment", dashboardSliderFragment);
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }
    }

    private void mainAppBarManager() {
        Menu nav_Approvals_Menu = navigationView.getMenu();

        //If approver
        if (user.getIsApprover().equals("1")) {
            //Shows approver menu
            nav_Approvals_Menu.findItem(R.id.nav_Approvals_Menu).setVisible(TRUE);
        }

        if (user.getRole_ID().equals("5242") || user.getRole_ID().equals("5243")) {
            //Shows approver menu
            nav_Approvals_Menu.findItem(R.id.nav_HR_ADMIN_Menu).setVisible(TRUE);
        }

        JSONArray bundeesArray = null;

        try {
            bundeesArray = new JSONArray(user.getUser_bundees());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException n) {
            sessionLogout();
        }


        if (bundeesArray != null) {
            if (bundeesArray.length() > 0) {
                for (int i = 0; i < bundeesArray.length(); i++) {

                    try {
                        if (bundeesArray.getInt(i) == 1001) {
                            nav_Approvals_Menu.findItem(R.id.nav_timesheet_request).setVisible(TRUE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void initViews() {
        // sets the header name and email of the login user
        navigationView = findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        name = headerView.findViewById(R.id.tv_name);
        email = headerView.findViewById(R.id.tv_email);
        btnImage = headerView.findViewById(R.id.btn_user_image);
        user_bio_layout = headerView.findViewById(R.id.user_bio_layout);
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
    }

    private void init() {
        about_us_dialog = new Dialog(this);
        about_us_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        mainActivityViewModel = ViewModelProviders.of(this)
                .get(com.example.timekeeping_beta.Activities.ViewModels.MainActivityViewModel.class);

        dashboardViewModel = ViewModelProviders.of(this)
                .get(DashboardViewModel.class);

        String full_name = user.getFname() + " " + user.getLname();
        name.setText(full_name);
        email.setText(user.getEmail());
    }

    private void setListeners() {

        dashboardViewModel.getTotalRACount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer i) {

                badgeDrawable = new BadgeDrawerArrowDrawable(getSupportActionBar().getThemedContext());

                if (i > 0) {
                    toggle.setDrawerArrowDrawable(badgeDrawable);
                    //if adding number
                    //badgeDrawable.setText("1");
                } else {
                    badgeDrawable.setEnabled(false);
                    toggle.setDrawerArrowDrawable(badgeDrawable);
                }
            }
        });


        // go to user profile
        user_bio_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changeFragment("USER_PROFILE_FRAGMENT", new UserProfileFragment());

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

            }
        });

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changeFragment("USER_PROFILE_FRAGMENT", new UserProfileFragment());

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

            }
        });
        // go to user profile

        getSupportFragmentManager().

                addOnBackStackChangedListener(this);

        navigationView.setNavigationItemSelectedListener(this);

        mainActivityViewModel.getUserFileName().

                observe(
                        this, new Observer<String>() {
                            @Override
                            public void onChanged(@Nullable String s) {

                                String user_image = URLs.url_image(user.getLink(), s);

                                Glide.with(ctx)
                                        .load(user_image)
                                        .error(R.drawable.ic_person_white_24dp)
                                        .thumbnail(
                                                Glide.with(ctx)
                                                        .load(Helper.getInstance(ctx).getCircleAnimation()))
                                        .apply(RequestOptions.circleCropTransform())
//                                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                                .skipMemoryCache(true)
                                        .override(500, 500)
                                        .into(btnImage);
                            }

                        }
                );
    }

    @Override
    protected void onResume() {
        super.onResume();

        appbarManager(getSupportFragmentManager().findFragmentById(R.id.fragment_container));

        //setUserImageOnCreate();
        checkRequiredPermissions();
        validateRequiredSettings();
        mainActivityViewModel.retrieveUserProfile();
        ViewModelProviders.of(this)
                .get(DashboardViewModel.class).retrieveUserDashboard();

    }

    private void appbarManager(Fragment fragment) {
        boolean is_timesheet_fragment = fragment instanceof TimesheetFragment;
        boolean is_edtr_fragment = fragment instanceof TimesheetEntryFragment;
        boolean is_user_profile_fragment = fragment instanceof UserProfileFragment;
        boolean is_approvee_fragment = fragment instanceof ApproveeDetailFragment;
        boolean is_dashboard_fragment = fragment instanceof DashboardSliderFragment;

        boolean is_list_fragment = fragment instanceof OvertimeFragment || fragment instanceof AdjustmentsFragment ||
                fragment instanceof LeavesFragment || fragment instanceof AdjustmentsUpdateFragmentv2 ||
                fragment instanceof EDTRUpdateFragmentv2 || fragment instanceof OvertimeUpdateFragmentv2 ||
                fragment instanceof LeaveUpdateFragmentv2 || fragment instanceof EmployeesFragment;

        //Old
        //boolean not_any = !is_timesheet_fragment && !is_edtr_fragment && !is_user_profile_fragment && !is_approvee_fragment && !is_dashboard_fragment;

        boolean not_any = !is_timesheet_fragment && !is_edtr_fragment && !is_user_profile_fragment && !is_dashboard_fragment && !is_list_fragment && !is_approvee_fragment;

        if (is_timesheet_fragment || is_edtr_fragment) {
            showTimesheetAppbar();
        } else if (is_user_profile_fragment) {
            showUserProfileAppbar();
        } else if (is_approvee_fragment) {
            showApproveeAppbar();
        } else if (is_dashboard_fragment) {
            showDashboardAppbar();
        } else if (is_list_fragment) {
            showListNavigator();
        }

//        else if (not_any) {
//            resetAppBar();
//        }
        else {
            resetAppBar();
        }
    }

    private void showListNavigator() {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.tl_dashboard).setVisibility(GONE);
                findViewById(R.id.timesheetnavigattion).setVisibility(GONE);
                findViewById(R.id.user_detail_container).setVisibility(GONE);
                findViewById(R.id.user_bio_appbar).setVisibility(GONE);
                findViewById(R.id.rl_list_navigator).setVisibility(VISIBLE);
            }
        });
    }

    private void showDashboardAppbar() {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.rl_list_navigator).setVisibility(GONE);
                findViewById(R.id.timesheetnavigattion).setVisibility(GONE);
                findViewById(R.id.user_detail_container).setVisibility(GONE);
                findViewById(R.id.user_bio_appbar).setVisibility(GONE);
                findViewById(R.id.tl_dashboard).setVisibility(VISIBLE);
            }
        });

    }

    private void showTimesheetAppbar() {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.rl_list_navigator).setVisibility(GONE);
                findViewById(R.id.tl_dashboard).setVisibility(GONE);
                findViewById(R.id.user_detail_container).setVisibility(GONE);
                findViewById(R.id.user_bio_appbar).setVisibility(GONE);
                findViewById(R.id.timesheetnavigattion).setVisibility(VISIBLE);
            }
        });
    }

    private void showUserProfileAppbar() {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.rl_list_navigator).setVisibility(GONE);
                findViewById(R.id.tl_dashboard).setVisibility(GONE);
                findViewById(R.id.user_detail_container).setVisibility(GONE);
                findViewById(R.id.timesheetnavigattion).setVisibility(GONE);
                findViewById(R.id.user_bio_appbar).setVisibility(VISIBLE);
            }
        });
    }

    private void showApproveeAppbar() {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.rl_list_navigator).setVisibility(GONE);
                findViewById(R.id.tl_dashboard).setVisibility(GONE);
                findViewById(R.id.timesheetnavigattion).setVisibility(GONE);
                findViewById(R.id.user_bio_appbar).setVisibility(GONE);
                findViewById(R.id.user_detail_container).setVisibility(VISIBLE);
            }
        });

    }

    private void resetAppBar() {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.rl_list_navigator).setVisibility(GONE);
                findViewById(R.id.tl_dashboard).setVisibility(GONE);
                findViewById(R.id.timesheetnavigattion).setVisibility(GONE);
                findViewById(R.id.user_bio_appbar).setVisibility(GONE);
                findViewById(R.id.user_detail_container).setVisibility(GONE);
            }
        });

    }

    private void changeFragment(String fragment_name, Fragment fragment) {
        //appbarManager(fragment);

        Fragment visibleFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        String replacement_tag = fragment_name;
        String visible_tag = "NO_FRAGMENT";

        if (visibleFragment != null) {
            visible_tag = visibleFragment.getTag() == null ? "NO_FRAGMENT" : visibleFragment.getTag();
        }

        //If replacing fragment with the same fragment
        //Do nothing
        if (replacement_tag.equals(visible_tag)) {

            String test = "Paul X Yuri";
        } else {

            Fragment f = getSupportFragmentManager().findFragmentByTag(fragment_name);

            //If fragment exists in backstack
            if (f != null) {
                if (f.isAdded()) {
                    getSupportFragmentManager().popBackStackImmediate(fragment_name, 0);
                } else {
                    addFragmentToContainer(fragment_name, fragment);
                }
            } else {
                addFragmentToContainer(fragment_name, fragment);
            }
        }
    }

    public void addFragmentToContainer(String fragment_name, Fragment fragment) {
        //When fragment is not Dashboard fragment
        if (getSupportFragmentManager().getBackStackEntryCount() > 0 && fragment instanceof DashboardSliderFragment) {

            getSupportFragmentManager().popBackStack();
        }

        //Adds to backstack
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                //.setCustomAnimations(R.anim.slide_up, R.anim.slide_bottom)
                .add(R.id.fragment_container, fragment, fragment_name)
                .addToBackStack(fragment_name)
                .commit();
    }

    public void checkReturnToFragment() {

        appbarManager(getSupportFragmentManager().findFragmentById(R.id.fragment_container));

        //Replaces with DashboardSliderFragment
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {

            DashboardSliderFragment dashboardSliderFragment = new DashboardSliderFragment();
            Bundle b = new Bundle();
            b.putInt("number_pages", Integer.valueOf(user.getRole_ID()) == 5241 ? 1 : 2);
            dashboardSliderFragment.setArguments(b);
            changeFragment("DashboardSliderFragment", dashboardSliderFragment);

            getSupportFragmentManager()
                    .beginTransaction()


                    //.replace(R.id.fragment_container, new UserDashboardFragment(), "DASHBOARD_FRAGMENT")
                    .replace(R.id.fragment_container, dashboardSliderFragment, "DashboardSliderFragment")


                    .commit();
        }
        //CLoses app when on Dashboard Fragment
        else if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        }
//        }
    }

    private void setActivityTitle() {

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        appbarManager(currentFragment);

        if (currentFragment instanceof DashboardFragment || currentFragment instanceof UserDashboardFragment || currentFragment instanceof DashboardSliderFragment) {
            this.setTitle(getResources().getString(R.string.app_name));
        } else if (currentFragment instanceof AdjustmentsUpdateFragmentv2) {
            this.setTitle(getResources().getString(R.string.title_fragment_approver_adjustments));
        } else if (currentFragment instanceof OvertimeUpdateFragmentv2) {
            this.setTitle(getResources().getString(R.string.title_fragment_approver_overtimes));
        } else if (currentFragment instanceof LeaveUpdateFragmentv2) {
            this.setTitle(getResources().getString(R.string.title_fragment_approver_leaves));
        } else if (currentFragment instanceof ApproveesFragment) {
            this.setTitle(getResources().getString(R.string.title_fragment_approver_employees));
        } else if (currentFragment instanceof AdjustmentsFragment) {
            this.setTitle(getResources().getString(R.string.title_fragment_adjustments_requests));
        } else if (currentFragment instanceof com.example.timekeeping_beta.Fragments.UserEmployee2.Overtimes.OvertimeFragment) {
            this.setTitle(getResources().getString(R.string.title_fragment_overtime_requests));
        } else if (currentFragment instanceof LeavesFragment) {
            this.setTitle(getResources().getString(R.string.title_fragment_leave_requests));
        } else if (currentFragment instanceof TimesheetFragment) {
            this.setTitle(getResources().getString(R.string.title_fragment_timesheet));
        } else if (currentFragment instanceof UserProfileFragment) {
            this.setTitle(getResources().getString(R.string.title_fragment_my_profile));
        } else if (currentFragment instanceof EmployeesFragment) {
            this.setTitle(getResources().getString(R.string.title_fragment_employees));
        } else if (currentFragment instanceof SchedulesFragment) {
            this.setTitle(getResources().getString(R.string.title_fragment_schedules));
        } else if (currentFragment instanceof HolidaysFragment) {
            this.setTitle(getResources().getString(R.string.title_fragment_holidays));
        } else if (currentFragment instanceof HolidayTypesFragment) {
            this.setTitle(getResources().getString(R.string.title_fragment_holiday_types));
        } else if (currentFragment instanceof LeaveTypesFragment) {
            this.setTitle(getResources().getString(R.string.title_fragment_leave_types));
        } else if (currentFragment instanceof RequestFragment) {
            this.setTitle(getResources().getString(R.string.title_fragment_request_adjustment));
        } else if (currentFragment instanceof HRDashboardFragment) {
            this.setTitle(getResources().getString(R.string.title_fragment_HR_Dashboard));
        } else if (currentFragment instanceof ApproveeDetailFragment) {
            this.setTitle(getResources().getString(R.string.title_fragment_approvee_timesheet));
        } else if (currentFragment instanceof TimesheetEntryFragment) {
            this.setTitle(getResources().getString(R.string.title_fragment_EDTR));
        } else if (currentFragment instanceof EDTRUpdateFragmentv2) {
            this.setTitle(getResources().getString(R.string.title_fragment_approver_edtr));
        } else if (currentFragment instanceof MobileTimeEntryFragment) {
            this.setTitle(getResources().getString(R.string.title_fragment_mobile_edtr));
        }else if (currentFragment instanceof HRDashboardEmployeeList) {
            this.setTitle("Present Employees");
        } else {
            // From TryAgainFragment
            // this.setTitle(currentFragment.getTag() != null ? currentFragment.getTag() : "Error");
        }
    }

    private void checkRequiredPermissions() {
        if (ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED

                || ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED

                || ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSIONS
            );

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(Gravity.LEFT); //CLOSE Nav Drawer!
        }

        checkReturnToFragment();
    }

    @Override
    public void onBackStackChanged() {
        setActivityTitle();
    }


    //@SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NotNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {

            //changeFragment("DASHBOARD_FRAGMENT", new UserDashboardFragment());
            changeFragment("DashboardSliderFragment", new DashboardSliderFragment());
        } else if (id == R.id.nav_timesheet) {

            changeFragment("TimesheetFragment", new TimesheetFragment());
        } else if (id == R.id.nav_overtime_request) {

            //changeFragment("OvertimeFragment", new OvertimeFragment());
            changeFragment("OvertimeFragment", new com.example.timekeeping_beta.Fragments.UserEmployee2.Overtimes.OvertimeFragment());
        } else if (id == R.id.nav_timesheet_request) {

            changeFragment("TimesheetEntryFragment", new TimesheetEntryFragment());
        } else if (id == R.id.nav_adjustment_request) {

            //changeFragment("AdjustmentFragment", new AdjustmentFragment());
            changeFragment("AdjustmentsFragment", new AdjustmentsFragment());
        } else if (id == R.id.nav_leave_request) {

            //changeFragment("LeaveFragment", new LeaveFragment());
            changeFragment("LeavesFragment", new LeavesFragment());
        } else if (id == R.id.nav_approver_employee) {

            //changeFragment("ApproversEmployeesFragment", new ApproversEmployeesFragment());
            changeFragment("ApproveesFragment", new ApproveesFragment());
        } else if (id == R.id.nav_time_approvals) {

            //changeFragment("AdjustmentsUpdateFragment", new AdjustmentsUpdateFragment());
            changeFragment("AdjustmentsUpdateFragmentv2", new AdjustmentsUpdateFragmentv2());
        } else if (id == R.id.nav_timesheet_Approvals) {

            //changeFragment("TimesheetUpdateFragment", new TimesheetUpdateFragment());
            //changeFragment("EDTRUpdateFragment", new EDTRUpdateFragment());
            changeFragment("EDTRUpdateFragmentv2", new EDTRUpdateFragmentv2());
        } else if (id == R.id.nav_overtime_approvals) {

            //changeFragment("OvertimeUpdateFragment", new OvertimeUpdateFragment());
            changeFragment("OvertimeUpdateFragmentv2", new OvertimeUpdateFragmentv2());
        } else if (id == R.id.nav_leave_approvals) {

            changeFragment("LeaveUpdateFragmentv2", new LeaveUpdateFragmentv2());
        }
        //Approver

        //Global Settings
        else if (id == R.id.nav_holiday_types) {

            changeFragment("HolidayTypesFragment", new HolidayTypesFragment());
        } else if (id == R.id.nav_leave_types) {

            changeFragment("LeaveTypesFragment", new LeaveTypesFragment());
        } else if (id == R.id.nav_holiday_management) {

            changeFragment("HolidaysFragment", new HolidaysFragment());
        } else if (id == R.id.nav_schedule_management) {

            changeFragment("SchedulesFragment", new SchedulesFragment());
        } else if (id == R.id.nav_location_management) {

            changeFragment("CompanyLocationsFragment", new CompanyLocationsFragment());
        }
        //Global Settings


        //HR Admin
        else if (id == R.id.nav_employees) {

            changeFragment("EmployeesFragment", new EmployeesFragment());
        } else if (id == R.id.nav_hr_dashboard) {

            changeFragment("HRDashboardFragment", new HRDashboardFragment());
        }
        //HR Admin


        //Others
        else if (id == R.id.btnAboutUs) {
            about_us_dialog.setContentView(R.layout.dialog_about_us);
            TextView tv_version_id = about_us_dialog.findViewById(R.id.tv_version_id);
            String version = "version " + Static.APP_VERSION;
            tv_version_id.setText(version);
            about_us_dialog.show();
        } else if (id == R.id.btnLogout) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            sessionLogout();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setMessage("Are you sure you want to logout?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
        //Others

        //Karl


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sessionLogout() {
        SharedPrefManager.getInstance(getApplicationContext()).logout();
        finish();
        Toasty.success(this, getResources().getString(R.string.api_request_logout_success), Toasty.LENGTH_LONG).show();
    }

    public void moveBack() {

        //Number of fragments stacks is 2 or above
        // 1 Dashboard
        // 2 Other fragments
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {

            //Empties fragments stack
            getSupportFragmentManager().popBackStack();
            //Goes to Dashboad
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, new UserDashboardFragment())
                    .commit();
        }
        //If Screen is on Dashboard
        else {
            //Closes app
            finish();
        }
    }

    public void setUserImageOnCreate() {

        if (SharedPrefManager.getInstance(this).getUser().getImageFileName().length() > 0) {

            String user_image = URLs.url_image(user.getLink(), SharedPrefManager.getInstance(this).getUser().getImageFileName());

            Glide.with(ctx)
                    .load(user_image)
                    .error(R.drawable.ic_person_white_24dp)
                    .thumbnail(
                            Glide.with(ctx)
                                    .load(Helper.getInstance(ctx).getCircleAnimation()))
                    .apply(RequestOptions.circleCropTransform())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .override(500, 500)
                    .into(btnImage);
        }
    }

    private void validateRequiredSettings() {
        if (!timeZoneAutomatic()) {
            showDateTimeDisabledAlertToUser();
        }
    }

    private Boolean timeZoneAutomatic() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.Global.getInt(this.getContentResolver(), Settings.Global.AUTO_TIME, 0) == 1 && Settings.Global.getInt(this.getContentResolver(), Settings.Global.AUTO_TIME_ZONE, 0) == 1;
        } else {
            return android.provider.Settings.System.getInt(
                    this.getContentResolver(),
                    Settings.System.AUTO_TIME,
                    0
            ) == 1 && android.provider.Settings.System.getInt(
                    this.getContentResolver(),
                    Settings.System.AUTO_TIME_ZONE,
                    0
            ) == 1;
        }
    }

    private void showDateTimeDisabledAlertToUser() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder
                .setMessage("Automatic time or timezone is disabled in your device.\nPlease enable it to use the app.")
                .setCancelable(false)
                .setPositiveButton("Go to Settings Page", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_DATE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                    }
                });

        Dialog alert = alertDialogBuilder.create();
        alert.show();
    }
}
