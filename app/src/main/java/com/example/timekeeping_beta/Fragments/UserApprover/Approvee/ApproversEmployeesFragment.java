package com.example.timekeeping_beta.Fragments.UserApprover.Approvee;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.timekeeping_beta.Fragments.Retry.TryAgainFragment;
import com.example.timekeeping_beta.Fragments.UserApprover.ApproveeDetails.ApproveeDetailFragment;
import com.example.timekeeping_beta.Fragments.UserApprover.Approvee.Models.Approvee;
import com.example.timekeeping_beta.Globals.CustomClasses.Flag;
import com.example.timekeeping_beta.Globals.CustomClasses.Interface.RecyclerViewClickListener;
import com.example.timekeeping_beta.R;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.Globals.Models.User;
import com.example.timekeeping_beta.Globals.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static android.app.ProgressDialog.*;

public class ApproversEmployeesFragment<Array> extends Fragment implements RecyclerViewClickListener {

    public View v;
    private RecyclerView recyclerViewApproversEmployees;
    private RecyclerView.Adapter mAdapter;
    private Context context;

    private TextView no_data;
    private ProgressDialog loadingScreenDialog;

    private Approvee[] ApproveeList;

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_approvees, container, false);
        context = v.getContext();


        no_data = v.findViewById(R.id.no_data);

        recyclerViewApproversEmployees = v.findViewById(R.id.recyclerViewApproversEmployees);
        recyclerViewApproversEmployees.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerViewApproversEmployees.setLayoutManager(layoutManager);

        getApprovees();


        return v;
    }

    private void getApprovees() {

        whenLoading();
        final URLs url = new URLs();
        final User user = SharedPrefManager.getInstance(getActivity()).getUser();
        final String DATABASE = user.getC1();
        final String TABLE = user.getC2();

        String url_get_approvees = url.url_get_approvees(user.getUser_id(), user.getApi_token(), user.getLink());

        final RecyclerViewClickListener that = this;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_get_approvees, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    JSONArray approvees_jarray = response_obj.getJSONArray("msg");
                    Integer array_len = approvees_jarray.length();

                    if (array_len > 0) {

                        ApproveeList = new Approvee[array_len];

                        for (int i = 0; i < array_len; i++) {
                            JSONObject approvee = approvees_jarray.getJSONObject(i);
                            String id = approvee.getString("user_id");
                            String f_name = approvee.getString("fname");
                            String l_name = approvee.getString("lname");
                            String cell_num = approvee.getString("cell_num");
                            String email = approvee.getString("email");
                            String location = approvee.getString("location");
                            String role_id = "";
                            String role_name = "";

                            //Wala sinesend
                            //String image_file_name =

                            if (approvee.has("user_roles")) {
                                JSONObject user_roles = new JSONObject(approvee.getString("user_roles"));
                                role_id = user_roles.getString("role_id");
                                role_name = user_roles.getString("role_name");
                            }
                            if (approvee.has("roles")) {
                                JSONArray user_roles = new JSONArray(approvee.getString("roles"));
                                role_id = user_roles.getString(i);
                            }

                            Approvee a = new Approvee(id, f_name, l_name, cell_num, email, location, role_id, role_name, "");
                            ApproveeList[i] = a;
                        }

                        mAdapter = new ApproveesAdapter(ApproveeList, that);
                        recyclerViewApproversEmployees.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                        whenSuccess();
                    }

                } catch (JSONException e) {
                    whenError();
                    Toasty.info(context, e.getMessage(), Toasty.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                whenError();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("d", DATABASE);
                headers.put("t", TABLE);
                return headers;
            }
        };
        Helper.getInstance(context).addToRequestQueue(stringRequest);
    }

    public void whenLoading() {
        no_data.setVisibility(View.GONE);
        loadingScreenDialog = show(getContext(), null, "Please Wait...");
    }

    public void whenSuccess() {
        no_data.setVisibility(View.GONE);
        loadingScreenDialog.dismiss();
    }

    public void whenError() {
        no_data.setVisibility(View.GONE);
        loadingScreenDialog.dismiss();

        TryAgainFragment tryAgainFragment = new TryAgainFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("RETURN_TO", Flag.APPROVERS_EMPLOYEE_FRAGMENT);
        tryAgainFragment.setArguments(arguments);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        if (fragmentManager != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, tryAgainFragment)
                    .commit();
        } else {
            Toasty.error(context, "Fragment manager is empty.", Toasty.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemClick(int position, int flag) {
        Bundle bundle = new Bundle();
        bundle.putString("user_id", ApproveeList[position].getApproveeId());

        ApproveeDetailFragment ApproveeDetailFragment = new ApproveeDetailFragment();
        ApproveeDetailFragment.setArguments(bundle);

        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .add(R.id.fragment_container, ApproveeDetailFragment, "ApproveeDetailFragmentFromApproveesFragment")
                .addToBackStack("ApproveeDetailFragmentFromApproveesFragment")
                .commit();
    }
}