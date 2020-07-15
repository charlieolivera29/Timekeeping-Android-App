package com.example.timekeeping_beta.Fragments.HRAdmin.Employees;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.timekeeping_beta.Fragments.GlobalSettings.LocationManagement.CompanyLocation;
import com.example.timekeeping_beta.Fragments.GlobalSettings.LocationManagement.CompanyLocationsViewModel;
import com.example.timekeeping_beta.Fragments.Retry.TryAgainFragment;
import com.example.timekeeping_beta.Fragments.UserApprover.ApproveeDetails.ApproveeDetailFragment;
import com.example.timekeeping_beta.Fragments.UserApprover.Approvee.Models.Role;
import com.example.timekeeping_beta.Globals.CustomClasses.Flag;
import com.example.timekeeping_beta.Globals.CustomClasses.ImageFilePath;
import com.example.timekeeping_beta.Globals.CustomClasses.MultiSelectionSpinner;
import com.example.timekeeping_beta.Globals.Models.Bundee;
import com.example.timekeeping_beta.Globals.Models.Pagination;
import com.example.timekeeping_beta.Globals.Static;
import com.example.timekeeping_beta.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;

public class EmployeesFragment extends Fragment implements EmployeesAdapter.OnItemClickListener {

    private View v;
    private Context ctx;

    private EmployeesViewModel EmployeesViewModel;
    private CompanyLocationsViewModel CompanyLocationsViewModel;

    private List<Employee> EmployeeList;
    private List<Role> RoleList;
    private List<CompanyLocation> CompanyLocationList;
    private List<Bundee> BundeeList;

    private Pagination pagination;
    private String search = "";
    private Integer show = 10;


    private FloatingActionButton fab_create_leave_type;
    private RecyclerView recyclerview_holiday_types;
    private TextView no_data;

    private ProgressDialog loadingScreenDialog;
    private com.example.timekeeping_beta.Fragments.HRAdmin.Employees.EmployeesAdapter EmployeesAdapter;
    private Dialog CreateEmployeeDialog;
    private Dialog EditEmployeeDialog;

    private Button btn_h_type_create;
    private ImageView modal_iv_employee_image;
    private EditText modal_first_name;
    private EditText modal_last_name;
    private EditText modal_contact_number;
    private EditText modal_employee_number;
    private EditText modal_company;
    private EditText modal_project;
    private EditText modal_email;
    private Spinner modal_spinner_roles;
    private Switch modal_switch_toggle_password_pin_field;
    private TableRow modal_tr_password_container;
    private EditText modal_password;
    private TableRow modal_tr_pin_container;
    private EditText modal_pin;
    private Switch modal_switch_employee_excluded_from_timekeeping;
    private Switch modal_switch_employee_is_flexible;
    private Spinner modal_spinner_approver;
    private Spinner modal_spinner_employee_location;
    private Switch modal_switch_toggle_customize_bundee_field;
    private TableRow modal_tr_bundee_container;
    private MultiSelectionSpinner modal_bundee;
    private ImageButton close_create_dialog;

    private Button btn_h_type_edit;
    private EditText modal_edit_holiday_type_id;
    private EditText modal_edit_holiday_type_name;
    private ImageButton close_edit_dialog;

    private String error_message;
    private String success_message;

    private int selected_ht_position;

    //Image
    private int GALLERY_INTENT_ACTIVITY_CODE = 69;
    private File originalFile;
    private MediaType fileType;

    private EditText tv_search;

    private Spinner spnnr_user_types;
    private TextView tv_filter;

    private ImageView iv_toggle_options;
    private LinearLayout ll_options_container;
    private CardView search_bar_container;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_employees_list, container, false);
        ctx = v.getContext();

        init();
        initViews();
        setListeners();
        setValues();

        retrieveEmployees();

        return v;
    }

    private void retrieveEmployees() {
        EmployeesViewModel.retrieveEmployees("all", search, show);
        EmployeesViewModel.retrieveRoles();
        EmployeesViewModel.retrieveBundees();
        CompanyLocationsViewModel.retrieveAllLocations();
        whenLoading();
    }

    private void init() {
        error_message = getResources().getString(R.string.api_request_failed);
        success_message = getResources().getString(R.string.api_request_success);

        EditEmployeeDialog = new Dialog(ctx, R.style.AppTheme_NoActionBar);
        CreateEmployeeDialog = new Dialog(ctx, R.style.AppTheme_NoActionBar);

        EmployeesViewModel = ViewModelProviders.of(this)
                .get(EmployeesViewModel.class);

        CompanyLocationsViewModel = ViewModelProviders.of(this)
                .get(CompanyLocationsViewModel.class);
    }

    private void initViews() {
        fab_create_leave_type = v.findViewById(R.id.fab_create_item);
        recyclerview_holiday_types = v.findViewById(R.id.recyclerview_global_settings_list);
        no_data = v.findViewById(R.id.no_data);
        tv_search = v.findViewById(R.id.tv_search);
        spnnr_user_types = v.findViewById(R.id.spnnr_user_types);
        tv_filter = v.findViewById(R.id.tv_filter);
        iv_toggle_options = v.findViewById(R.id.iv_toggle_options);
        search_bar_container = v.findViewById(R.id.search_bar_container);
        ll_options_container = v.findViewById(R.id.ll_options_container);

        CreateEmployeeDialog.setContentView(R.layout.dialog_employee);
        EditEmployeeDialog.setContentView(R.layout.dialog_employee);

        btn_h_type_create = CreateEmployeeDialog.findViewById(R.id.btn_h_type_create);
        modal_iv_employee_image = CreateEmployeeDialog.findViewById(R.id.modal_iv_employee_image);
        modal_first_name = CreateEmployeeDialog.findViewById(R.id.modal_first_name);
        modal_last_name = CreateEmployeeDialog.findViewById(R.id.modal_last_name);
        modal_contact_number = CreateEmployeeDialog.findViewById(R.id.modal_contact_number);
        modal_employee_number = CreateEmployeeDialog.findViewById(R.id.modal_employee_number);
        modal_company = CreateEmployeeDialog.findViewById(R.id.modal_company);
        modal_project = CreateEmployeeDialog.findViewById(R.id.modal_project);
        modal_email = CreateEmployeeDialog.findViewById(R.id.modal_email);
        modal_spinner_roles = CreateEmployeeDialog.findViewById(R.id.modal_spinner_roles);
        modal_switch_toggle_password_pin_field = CreateEmployeeDialog.findViewById(R.id.modal_switch_toggle_password_pin_field);
        modal_tr_password_container = CreateEmployeeDialog.findViewById(R.id.modal_tr_password_container);
        modal_password = CreateEmployeeDialog.findViewById(R.id.modal_password);
        modal_tr_pin_container = CreateEmployeeDialog.findViewById(R.id.modal_tr_pin_container);
        modal_pin = CreateEmployeeDialog.findViewById(R.id.modal_pin);
        modal_switch_employee_excluded_from_timekeeping = CreateEmployeeDialog.findViewById(R.id.modal_switch_employee_excluded_from_timekeeping);
        modal_switch_employee_is_flexible = CreateEmployeeDialog.findViewById(R.id.modal_switch_employee_is_flexible);
        modal_spinner_approver = CreateEmployeeDialog.findViewById(R.id.modal_spinner_approver);
        modal_spinner_employee_location = CreateEmployeeDialog.findViewById(R.id.modal_spinner_employee_location);
        modal_switch_toggle_customize_bundee_field = CreateEmployeeDialog.findViewById(R.id.modal_switch_toggle_customize_bundee_field);
        modal_tr_bundee_container = CreateEmployeeDialog.findViewById(R.id.modal_tr_bundee_container);
        modal_bundee = CreateEmployeeDialog.findViewById(R.id.modal_bundee);
        close_create_dialog = CreateEmployeeDialog.findViewById(R.id.img_fullscreen_dialog_close);


        btn_h_type_edit = EditEmployeeDialog.findViewById(R.id.btn_h_type_create);
        close_edit_dialog = EditEmployeeDialog.findViewById(R.id.img_fullscreen_dialog_close);
    }

    private void replaceToggleImage(int i) {

        if (Static.INSTANCE.getOS_VERSION() < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            //iv_toggle_options.setBackgroundDrawable(ContextCompat.getDrawable(ctx, i));
        } else {
            //iv_toggle_options.setBackground(ContextCompat.getDrawable(ctx, i));
        }
    }

    private void setListeners() {

        search_bar_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ll_options_container.getVisibility() == GONE) {
                    ll_options_container.setVisibility(View.VISIBLE);
                    replaceToggleImage(R.drawable.ic_arrow_drop_up_black_24dp);
                } else {
                    ll_options_container.setVisibility(View.GONE);
                    replaceToggleImage(R.drawable.ic_arrow_drop_down_black_24dp);
                }
            }
        });


        tv_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EmployeesViewModel.retrieveEmployees("all", search, show);
            }
        });

        ArrayList<Integer> listOptions = new ArrayList();
        listOptions.add(10);
        listOptions.add(20);
        listOptions.add(50);
        listOptions.add(100);

        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(getContext(), android.R.layout.simple_spinner_dropdown_item, listOptions);
        spnnr_user_types.setAdapter(adapter);
        //Sets default value
        spnnr_user_types.setSelection(0, false);
        spnnr_user_types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView != null) {
                    Integer selected = (Integer) adapterView.getSelectedItem();

                    show = selected;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        EmployeesViewModel.getPagination().observe(this, new Observer<Pagination>() {
            @Override
            public void onChanged(@Nullable Pagination l_pagination) {
                ImageView prev_button = getActivity().findViewById(R.id.iv_next_page);
                ImageView next_button = getActivity().findViewById(R.id.iv_prev_page);

                if (l_pagination != null) {

                    if (l_pagination.getPrev_page_url() != "null" && l_pagination.getNext_page_url() != "null") {
                        prev_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                EmployeesViewModel.retrievePaginated(Flag.PREV_PAGE, "all", search, show);
                            }
                        });

                        next_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                EmployeesViewModel.retrievePaginated(Flag.NEXT_PAGE, "all", search, show);
                            }
                        });
                    } else if (l_pagination.getPrev_page_url() == "null" && l_pagination.getNext_page_url() != "null") {
                        prev_button.setOnClickListener(null);
                        next_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                EmployeesViewModel.retrievePaginated(Flag.NEXT_PAGE, "all", search, show);
                            }
                        });
                    } else if (l_pagination.getPrev_page_url() != "null" && l_pagination.getNext_page_url() == "null") {
                        prev_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                EmployeesViewModel.retrievePaginated(Flag.PREV_PAGE, "all", search, show);
                            }
                        });

                        next_button.setOnClickListener(null);
                    } else {
                        prev_button.setOnClickListener(null);
                        next_button.setOnClickListener(null);
                    }
                } else {
                    prev_button.setOnClickListener(null);
                    next_button.setOnClickListener(null);
                }
            }
        });

        tv_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                search = tv_search.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        EmployeesViewModel.getCreateLeaveTypeResult().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

                loadingScreenDialog.dismiss();

                if (s != null) {

                    if (s.equals("success")) {
                        Toasty.success(ctx, success_message, Toasty.LENGTH_SHORT).show();
                        CreateEmployeeDialog.dismiss();
                        retrieveEmployees();
                    } else {
                        Toasty.error(ctx, s, Toasty.LENGTH_SHORT).show();
                        whenSuccess();
                    }
                } else {
                    Toasty.error(ctx, error_message, Toasty.LENGTH_SHORT).show();
                }
            }
        });

        EmployeesViewModel.getEditLeaveTypeResult().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

                loadingScreenDialog.dismiss();

                if (s != null) {

                    if (s.equals("success")) {
                        Toasty.success(ctx, success_message, Toasty.LENGTH_SHORT).show();
                        EditEmployeeDialog.dismiss();
                        retrieveEmployees();
                    } else {
                        Toasty.error(ctx, s, Toasty.LENGTH_SHORT).show();
                    }
                } else {
                    Toasty.error(ctx, error_message, Toasty.LENGTH_SHORT).show();
                }
            }
        });

        final EmployeesFragment that = this;

        EmployeesViewModel.getEmployees().observe(this, new Observer<List<Employee>>() {
            @Override
            public void onChanged(@Nullable List<Employee> employees) {
                EmployeeList = employees;

                if (EmployeeList != null) {

                    if (EmployeeList.size() == 0) {
                        whenNoResult();
                    } else {
                        setApproversSpinner(employees);
                        EmployeesAdapter = new EmployeesAdapter(EmployeeList, that);
                        recyclerview_holiday_types.setAdapter(EmployeesAdapter);
                        EmployeesAdapter.notifyDataSetChanged();
                        whenSuccess();
                    }
                } else {
                    whenError();
                }
            }
        });


        EmployeesViewModel.getRoles().observe(this, new Observer<List<Role>>() {
            @Override
            public void onChanged(@Nullable List<Role> roles) {
                RoleList = roles;
                setRolesSpinner(roles);
            }
        });

        EmployeesViewModel.getBundees().observe(this, new Observer<List<Bundee>>() {
            @Override
            public void onChanged(@Nullable List<Bundee> bundees) {
                BundeeList = bundees;
                setBundeeSpinner(bundees);
            }
        });

        CompanyLocationsViewModel.getLocations().observe(this, new Observer<List<CompanyLocation>>() {
            @Override
            public void onChanged(@Nullable List<CompanyLocation> companyLocations) {
                CompanyLocationList = companyLocations;

                setEmployeeLocationsSpinner(companyLocations);
            }
        });


        fab_create_leave_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateEmployeeDialog.show();
            }
        });

        modal_iv_employee_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                int GALLERY_INTENT_ACTIVITY_CODE = 69;
                startActivityForResult(intent, GALLERY_INTENT_ACTIVITY_CODE);
            }
        });

        btn_h_type_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                validateEmployee();
//                String ht_code = modal_holiday_type_id.getText().toString();
//                String ht_name = modal_holiday_type_name.getText().toString();
//
//                if (ht_code.length() > 0 && ht_name.length() > 0) {
//                    loadingScreenDialog = ProgressDialog.show(ctx, null, "Please Wait...");
//                    EmployeesViewModel.createHolidayType(ht_code, ht_name);
//                } else {
//                    Toasty.error(ctx, getResources().getString(R.string.required_field_empty), Toasty.LENGTH_SHORT).show();
//                }
            }
        });

        btn_h_type_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String ht_code = modal_edit_holiday_type_id.getText().toString();
//                String ht_name = modal_edit_holiday_type_name.getText().toString();
//
//                if (ht_code.length() > 0 && ht_name.length() > 0) {
//                    loadingScreenDialog = ProgressDialog.show(ctx, null, "Please Wait...");
//                    EmployeesViewModel.updateHolidayType(selected_ht_position, ht_code, ht_name);
//                } else {
//                    Toasty.error(ctx, getResources().getString(R.string.required_field_empty), Toasty.LENGTH_SHORT).show();
//                }
            }
        });


        modal_switch_toggle_password_pin_field.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (modal_tr_password_container.getVisibility() == GONE) {
                    modal_tr_password_container.setVisibility(View.VISIBLE);
                    modal_tr_pin_container.setVisibility(View.VISIBLE);
                } else {
                    modal_tr_password_container.setVisibility(View.GONE);
                    modal_tr_pin_container.setVisibility(View.GONE);
                }
            }
        });

        modal_switch_toggle_customize_bundee_field.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (modal_tr_bundee_container.getVisibility() == GONE) {
                    modal_tr_bundee_container.setVisibility(View.VISIBLE);
                } else {
                    modal_tr_bundee_container.setVisibility(View.GONE);
                }
            }
        });

        close_create_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CreateEmployeeDialog.isShowing()) {
                    CreateEmployeeDialog.dismiss();
                }
            }
        });

        close_edit_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EditEmployeeDialog.isShowing()) {
                    EditEmployeeDialog.dismiss();
                }
            }
        });
    }

    private void setValues() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ctx);

        recyclerview_holiday_types.setHasFixedSize(true);
        recyclerview_holiday_types.setLayoutManager(layoutManager);

        Glide.with(ctx)
                .load(R.drawable.ic_person_black_24dp)
                .apply(RequestOptions.circleCropTransform())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .override(500, 500)
                .into(modal_iv_employee_image);
    }

    public void whenLoading() {
        no_data.setVisibility(GONE);
        recyclerview_holiday_types.setVisibility(GONE);

        loadingScreenDialog = ProgressDialog.show(ctx, null, "Please Wait...");
    }

    public void whenNoResult() {
        loadingScreenDialog.dismiss();
        recyclerview_holiday_types.setVisibility(GONE);

        no_data.setVisibility(View.VISIBLE);
    }

    public void whenError() {
        loadingScreenDialog.dismiss();
        recyclerview_holiday_types.setVisibility(GONE);
        no_data.setVisibility(GONE);

        TryAgainFragment tryAgainFragment = new TryAgainFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("RETURN_TO", Flag.EMPLOYEES_FRAGMENT);
        tryAgainFragment.setArguments(arguments);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        if (fragmentManager != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, tryAgainFragment)
                    .commit();
        } else {
            Toasty.error(ctx, "Fragment manager is empty.", Toasty.LENGTH_LONG).show();
        }
    }

    public void whenSuccess() {
        loadingScreenDialog.dismiss();
        no_data.setVisibility(GONE);

        recyclerview_holiday_types.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(int position) {
        //EditEmployeeDialog.show();

        Employee e = EmployeeList.get(position);
        selected_ht_position = e.getId();

        Bundle bundle = new Bundle();
        bundle.putString("user_id", e.getUser_id());

        Fragment ApproveeDetailFragment = new ApproveeDetailFragment();
        ApproveeDetailFragment.setArguments(bundle);

        ((FragmentActivity) v.getContext())
                .getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, ApproveeDetailFragment, "ApproveeDetailFragmentFromEmployeesFragment")
                .addToBackStack("ApproveeDetailFragmentFromEmployeesFragment")
                .commit();

        //modal_edit_holiday_type_id.setText(LT.getLeave_type_code());
        //modal_edit_holiday_type_name.setText(LT.getLeave_type_name());
    }

    private void setRolesSpinner(List<Role> i_role) {
        List<String> spinnerArray = new ArrayList<>();

        spinnerArray.add("-----");
        for (int i = 0; i < i_role.size(); i++) {

            Role r = i_role.get(i);
            spinnerArray.add(r.getRole_name());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                ctx, android.R.layout.simple_spinner_dropdown_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modal_spinner_roles.setAdapter(adapter);
    }

    private void setApproversSpinner(List<Employee> i_employees) {
        List<String> spinnerArray = new ArrayList<>();

        spinnerArray.add("-----");
        for (int i = 0; i < i_employees.size(); i++) {

            Employee e = i_employees.get(i);
            spinnerArray.add(e.getFname() + " " + e.getLname());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                ctx, android.R.layout.simple_spinner_dropdown_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modal_spinner_approver.setAdapter(adapter);
    }

    private void setEmployeeLocationsSpinner(List<CompanyLocation> i_employee_locations) {
        List<String> spinnerArray = new ArrayList<>();

        if (i_employee_locations != null) {
            spinnerArray.add("-----");
            for (int i = 0; i < i_employee_locations.size(); i++) {

                CompanyLocation cl = i_employee_locations.get(i);
                spinnerArray.add(cl.getBranch_name());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    ctx, android.R.layout.simple_spinner_dropdown_item, spinnerArray);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            modal_spinner_employee_location.setAdapter(adapter);
        }
    }

    private void setBundeeSpinner(List<Bundee> i_bundees) {
        List<String> spinnerArray = new ArrayList<>();

        for (int i = 0; i < i_bundees.size(); i++) {

            Bundee b = i_bundees.get(i);
            spinnerArray.add(b.getBundee_name());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                ctx, android.R.layout.simple_spinner_dropdown_item, spinnerArray);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modal_bundee.setItems(spinnerArray);
    }

    private void validateEmployee() {
        if (
                modal_first_name.getText().toString().length() > 0 &&
                        modal_last_name.getText().toString().length() > 0 &&
                        modal_contact_number.getText().toString().length() > 0 &&
                        modal_employee_number.getText().toString().length() > 0 &&
                        modal_company.getText().toString().length() > 0 &&
                        modal_project.getText().toString().length() > 0 &&
                        modal_email.getText().toString().length() > 0 &&
                        modal_spinner_roles.getSelectedItemPosition() != 0 &&
                        !modal_spinner_approver.getSelectedItem().equals(0) &&
                        !modal_spinner_employee_location.getSelectedItem().equals(0)) {

            Employee approver = EmployeeList.get(modal_spinner_approver.getSelectedItemPosition() - 1);
            Role role = RoleList.get(modal_spinner_roles.getSelectedItemPosition() - 1);
            JSONObject jo_role = new JSONObject();
            try {
                jo_role.put("role_id", role.getRole_id());
                jo_role.put("role_name", role.getRole_name());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONArray ja_bundee = new JSONArray();

            CompanyLocation companyLocation = CompanyLocationList.get(modal_spinner_employee_location.getSelectedItemPosition() - 1);
            JSONArray ja_work_locations = new JSONArray();

            int isExcluded = modal_switch_employee_excluded_from_timekeeping.isChecked() ? 1 : 0;
            int isFlexible = modal_switch_employee_is_flexible.isChecked() ? 1 : 0;

            EmployeesViewModel.createEmployee(
                    modal_email.getText().toString(),
                    modal_first_name.getText().toString(),
                    modal_last_name.getText().toString(),
                    modal_employee_number.getText().toString(),
                    modal_company.getText().toString(),
                    modal_project.getText().toString(),
                    approver.getUser_id(),
                    jo_role,
                    modal_contact_number.getText().toString(),
                    ja_bundee,
                    isExcluded,
                    isFlexible,
                    ja_work_locations,
                    companyLocation.getBranch_id(),
                    originalFile,
                    fileType
            );
        } else {
            Toasty.error(ctx, "Please fill all required fields").show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            //Image uri (incomplete path)
            Uri targetUri = data.getData();

            //Image uri (complete path)
            String realPath = ImageFilePath.getPath(ctx, targetUri);

            //Get file type
            fileType = MediaType.parse(ctx.getContentResolver().getType(targetUri));
            originalFile = new File(realPath);

            Bitmap bitmap;

            try {
                bitmap = BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(targetUri));
                //main_user_img.setImageBitmap(bitmap);
                //Sets preview
                Glide.with(ctx)
                        .load(bitmap)
                        .apply(RequestOptions.circleCropTransform())
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .override(500, 500)
                        .into(modal_iv_employee_image);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //sendImageUpdateRequest();
        }
    }


}
