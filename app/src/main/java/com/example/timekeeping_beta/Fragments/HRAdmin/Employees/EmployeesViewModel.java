package com.example.timekeeping_beta.Fragments.HRAdmin.Employees;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.timekeeping_beta.Fragments.Profile.Models.Result;
import com.example.timekeeping_beta.Fragments.UserApprover.Approvee.Models.Role;
import com.example.timekeeping_beta.Globals.CustomClasses.Flag;
import com.example.timekeeping_beta.Globals.Models.Pagination;
import com.example.timekeeping_beta.Globals.Models.queryStringBuilder;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.CustomClasses.Interface.UploadAPIs;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.Globals.Models.Bundee;
import com.example.timekeeping_beta.Globals.Models.User;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EmployeesViewModel extends AndroidViewModel {

    final private MutableLiveData<List<Employee>> Employees = new MutableLiveData<>();
    final private MutableLiveData<List<Role>> Roles = new MutableLiveData<>();

    final private MutableLiveData<List<Bundee>> Bundees = new MutableLiveData<>();

    final private MutableLiveData<String> create_employee_type_result = new MutableLiveData<>();
    final private MutableLiveData<String> edit_employeee_type_result = new MutableLiveData<>();

    final private MutableLiveData<Pagination> pagination = new MutableLiveData<>();

    private Context ctx;
    private URLs url;
    private User user;

    private String url_resource_employees;
    private String url_resource_roles;
    private String url_resource_bundees;

    private Helper helper;
    private Map<String, String> headers;
    private String DATABASE = "";
    private String TABLE = "";

    public EmployeesViewModel(@NonNull Application application) {
        super(application);

        ctx = getApplication().getApplicationContext();

        url = new URLs();
        user = SharedPrefManager.getInstance(ctx).getUser();

        helper = Helper.getInstance(ctx);
        headers = helper.headers();

        DATABASE = user.getC1();
        TABLE = user.getC2();

        url_resource_employees = url.url_resource_employees(user.getApi_token(), user.getLink());
        url_resource_roles = url.url_resource_roles(user.getApi_token(), user.getLink());
        url_resource_bundees = url.url_resource_bundees(user.getApi_token(), user.getLink());
    }

    public MutableLiveData<Pagination> getPagination() {
        return pagination;
    }

    public MutableLiveData<List<Employee>> getEmployees() {
        return Employees;
    }

    public MutableLiveData<List<Role>> getRoles() {
        return Roles;
    }

    public MutableLiveData<List<Bundee>> getBundees() {
        return Bundees;
    }

    public LiveData<String> getCreateLeaveTypeResult() {
        return create_employee_type_result;
    }

    public LiveData<String> getEditLeaveTypeResult() {
        return edit_employeee_type_result;
    }

    public void retrieveEmployees(String employee, String search, Integer show) {

        String url = url_resource_employees + "&employee=" + employee + "&show=" + show + "&search=" + search + "&api_token=" + user.getApi_token() + "&link=" + user.getLink();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {
                        JSONArray holidays = response_obj.getJSONObject("msg").getJSONArray("data");

                        setEmployeesToArray(holidays);
                        setPagination(response_obj.getJSONObject("msg"));
                    } else {
                        Employees.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Employees.setValue(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Employees.setValue(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };
        Helper.getInstance(ctx).addToRequestQueue(stringRequest);
    }

    public void retrievePaginated(Integer flag, String employee, String search, Integer show) {

        Pagination l_pagination = pagination.getValue();
        String url = "";

        if (flag == Flag.NEXT_PAGE) {
            url = l_pagination.getNext_page_url();
        } else if (flag == Flag.PREV_PAGE) {
            url = l_pagination.getPrev_page_url();
        }

        User user = SharedPrefManager.getInstance(getApplication().getBaseContext()).getUser();

        url = url + "&employee=" + employee + "&show=" + show + "&search=" + search + "&api_token=" + user.getApi_token() + "&link=" + user.getLink();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {
                        JSONArray holidays = response_obj.getJSONObject("msg").getJSONArray("data");

                        setEmployeesToArray(holidays);
                        setPagination(response_obj.getJSONObject("msg"));
                    } else {
                        Employees.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Employees.setValue(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Employees.setValue(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };
        Helper.getInstance(ctx).addToRequestQueue(stringRequest);
    }

    private void setPagination(JSONObject msg) {
        Pagination l_pagination = null;

        try {
            if (msg.getString("next_page_url") != "null" || msg.getString("prev_page_url") != "null") {
                l_pagination = new Pagination(
                        msg.getInt("current_page"),
                        msg.getString("next_page_url"),
                        msg.getString("prev_page_url"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        pagination.setValue(l_pagination);
    }

    public void retrieveRoles() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_resource_roles, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {
                        JSONArray holidays = response_obj.getJSONArray("msg");

                        setToRoles(holidays);
                    } else {
                        Employees.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Employees.setValue(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Employees.setValue(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }
        };
        Helper.getInstance(ctx).addToRequestQueue(stringRequest);
    }

    public void retrieveBundees() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_resource_bundees, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {
                        JSONArray holidays = response_obj.getJSONArray("msg");

                        setToBundees(holidays);
                    } else {
                        Employees.setValue(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Employees.setValue(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Employees.setValue(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;

            }
        };
        Helper.getInstance(ctx).addToRequestQueue(stringRequest);

    }

    private void setToBundees(JSONArray i_bundees) {

        int array_len = i_bundees.length();
        ArrayList<Bundee> BundeeArray = new ArrayList<>();

        if (array_len > 0) {

            for (int i = 0; i < array_len; i++) {

                try {
                    JSONObject employee = i_bundees.getJSONObject(i);

                    Integer id = employee.getInt("id");
                    String timetrack_id = employee.getString("timetrack_id");
                    String name = employee.getString("name");

                    Bundee bundee_object = new Bundee(
                            id,
                            timetrack_id,
                            name
                    );

                    BundeeArray.add(bundee_object);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Bundees.setValue(BundeeArray);
        } else {
            Bundees.setValue(BundeeArray);
        }
    }

    private void setToRoles(JSONArray i_employees) {

        int array_len = i_employees.length();
        ArrayList<Role> RoleArray = new ArrayList<>();

        if (array_len > 0) {

            for (int i = 0; i < array_len; i++) {

                try {
                    JSONObject employee = i_employees.getJSONObject(i);

                    String role_id = employee.getString("role_id");
                    String role_name = employee.getString("name");

                    Role role_object = new Role(
                            role_id,
                            role_name
                    );

                    RoleArray.add(role_object);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Roles.setValue(RoleArray);
        } else {
            Roles.setValue(RoleArray);
        }
    }


    private void setEmployeesToArray(JSONArray i_employees) {

        int array_len = i_employees.length();
        ArrayList<Employee> EmployeesArray = new ArrayList<>();

        if (array_len > 0) {

            for (int i = 0; i < array_len; i++) {

                try {
                    JSONObject employee = i_employees.getJSONObject(i);

                    Integer id = employee.getInt("id");
                    String user_id = employee.getString("user_id");
                    String company_id = employee.getString("company_id");
                    String email = employee.getString("email");
                    Integer type = employee.getInt("type");
                    String password = employee.getString("password");
                    String pin = employee.getString("pin");
                    Integer isActive = employee.getInt("isActive");
                    String last_seen = employee.getString("last_seen");
                    String api_token = employee.getString("api_token");
                    String remember_token = employee.getString("remember_token");

                    JSONObject users = employee.getJSONObject("users");
                    String fname = users.getString("fname");
                    String lname = users.getString("lname");
                    String image = users.getString("image");

                    JSONObject user_roles = employee.getJSONObject("user_roles");
                    String role_id = user_roles.getString("role_id");
                    String role_name = user_roles.has("role_name") ? user_roles.getString("role_name") : "No Role Name";

                    JSONObject work_location = employee.getJSONObject("work_location");
                    String location_id = work_location.getString("location_id");
                    String branch_name = work_location.getString("branch_name");
                    JSONArray timetrack = work_location.getJSONArray("timetrack");

                    JSONArray reports_to = employee.getJSONArray("reports_to");

                    //Removed
                    //Boolean online = employee.getBoolean("online");
                    Boolean online = false;


                    Employee employee_object = new Employee(
                            id,
                            user_id,
                            company_id,
                            email,
                            type,
                            password,
                            pin,
                            isActive,
                            last_seen,
                            api_token,
                            remember_token,
                            fname,
                            lname,
                            image,
                            role_id,
                            role_name,
                            location_id,
                            branch_name,
                            timetrack,
                            reports_to,
                            online
                    );

                    EmployeesArray.add(employee_object);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


            Collections.sort(EmployeesArray, new Comparator<Employee>() {
                @Override
                public int compare(Employee employee, Employee t1) {
                    return employee.getFname().compareToIgnoreCase(t1.getFname());
                }
            });

            Employees.setValue(EmployeesArray);
        } else {
            Employees.setValue(EmployeesArray);
        }
    }

    public void createHolidayType(final String ht_code, final String ht_name) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_resource_employees, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {
                        create_employee_type_result.setValue(status);
                    } else {
                        create_employee_type_result.setValue(response_obj.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    create_employee_type_result.setValue(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                create_employee_type_result.setValue(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("leave_code", ht_code);
                params.put("leave_name", ht_name);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60 * 1000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Helper.getInstance(ctx).addToRequestQueue(stringRequest);
    }

    public void updateHolidayType(final int lt_id, final String lt_code, final String lt_name) {

        String update_leave_type = url.url_update_leave_types(String.valueOf(lt_id), user.getApi_token(), user.getLink());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, update_leave_type, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {
                        edit_employeee_type_result.setValue(status);
                    } else {
                        edit_employeee_type_result.setValue(response_obj.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    edit_employeee_type_result.setValue(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                edit_employeee_type_result.setValue(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return headers;

            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("leave_code", lt_code);
                params.put("leave_name", lt_name);
                params.put("_method", "PUT");
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(60 * 1000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Helper.getInstance(ctx).addToRequestQueue(stringRequest);
    }

    public void createEmployee(
            String i_email,
            String i_fname,
            String i_lname,
            String i_emp_num,
            String i_company,
            String i_project,
            String i_approver_id,
            JSONObject i_role_id,
            String i_cell_num,
            JSONArray i_bundee,
            Integer i_isExcluded,
            Integer i_isFlexible,
            JSONArray i_work_locations,
            String i_branch_id,
            File originalFile,
            MediaType fileType
    ) {

        RequestBody api_token = RequestBody.create(MultipartBody.FORM, user.getApi_token());
        RequestBody link = RequestBody.create(MultipartBody.FORM, user.getLink());
        RequestBody email = RequestBody.create(MultipartBody.FORM, i_email);
        RequestBody fname = RequestBody.create(MultipartBody.FORM, i_fname);
        RequestBody lname = RequestBody.create(MultipartBody.FORM, i_lname);
        RequestBody emp_num = RequestBody.create(MultipartBody.FORM, i_emp_num);
        RequestBody company = RequestBody.create(MultipartBody.FORM, i_company);
        RequestBody project = RequestBody.create(MultipartBody.FORM, i_project);
        RequestBody approver_id = RequestBody.create(MultipartBody.FORM, i_approver_id);
        RequestBody role_id = RequestBody.create(MultipartBody.FORM, i_role_id.toString());
        RequestBody cell_num = RequestBody.create(MultipartBody.FORM, i_cell_num);
        RequestBody bundee = RequestBody.create(MultipartBody.FORM, i_bundee.toString());
        RequestBody isExcluded = RequestBody.create(MultipartBody.FORM, String.valueOf(i_isExcluded));
        RequestBody isFlexible = RequestBody.create(MultipartBody.FORM, String.valueOf(i_isFlexible));
        RequestBody work_locations = RequestBody.create(MultipartBody.FORM, i_work_locations.toString());
        RequestBody branch_id = RequestBody.create(MultipartBody.FORM, i_branch_id);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(URLs.ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        UploadAPIs client = retrofit.create(UploadAPIs.class);

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("d", DATABASE);
        headers.put("t", TABLE);

        RequestBody filePart = RequestBody.create(
                fileType,
                originalFile
        );

        MultipartBody.Part file = MultipartBody.Part.createFormData(
                "image",
                originalFile.getName(),
                filePart
        );

        Call<Result> call = client.createEmployee(
                headers,
                api_token,
                link,
                email,
                fname,
                lname,
                emp_num,
                company,
                project,
                approver_id,
                role_id,
                cell_num,
                bundee,
                isExcluded,
                isFlexible,
                work_locations,
                branch_id,
                file
        );
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {

                if (response.body() != null) {
                    if (response.body().getStatus().equals("success")) {
                        Toasty.success(ctx, ctx.getResources().getString(R.string.api_request_success), Toasty.LENGTH_LONG).show();
                    } else {
                        Toasty.error(ctx, ctx.getResources().getString(R.string.api_request_failed), Toasty.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

                Toasty.error(ctx, ctx.getResources().getString(R.string.api_request_error), Toasty.LENGTH_LONG).show();
            }
        });
    }


}
