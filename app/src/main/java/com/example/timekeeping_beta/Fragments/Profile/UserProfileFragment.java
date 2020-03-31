package com.example.timekeeping_beta.Fragments.Profile;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.timekeeping_beta.Fragments.Profile.Models.Result;
import com.example.timekeeping_beta.Fragments.Retry.TryAgainFragment;
import com.example.timekeeping_beta.Globals.CustomClasses.Flag;
import com.example.timekeeping_beta.Globals.CustomClasses.ImageFilePath;
import com.example.timekeeping_beta.Globals.CustomClasses.Interface.UploadAPIs;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.Globals.Models.UserProfileItem;
import com.example.timekeeping_beta.R;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.Globals.Models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;


public class UserProfileFragment extends Fragment implements DialogInterface.OnDismissListener {

    //Static
    private Context ctx;
    private File originalFile;
    private MediaType fileType;
    private boolean changesWereMade = false;
    private Map<String, String> Headers;

    // Objects
    private User user;
    private UserProfileItem userProfileItem;

    // Variable
    private View v;
    private RequestQueue queue;
    private URLs url = new URLs();
    private Dialog userProfileDialog;

    //Main
    private TextView name;
    private TextView email;
    private TextView num;
    private TextView bundee;
    private TextView location;
    private TextView assign_loc;
    private TextView approver;
    private TextView days;
    private TextView sched;
    private TextView btn_change_picture;
    private CardView profile_layout;
    private CardView pass_layout;
    private CardView pin_layout;
    private ImageView main_user_img;
    private FloatingActionButton toggle_edit_profile;

    //Dialog
    public ImageView dialog_user_img;
    public ImageView img_dialog_close;
    public EditText fname;
    public EditText lname;
    public EditText dialog_email;
    public EditText cell_num;
    protected EditText old_pass;
    protected EditText new_pass;
    protected EditText confirm_pass;
    protected EditText old_pin;
    protected EditText new_pin;
    protected EditText confirm_pin;

    private LinearLayout user_bio_appbar;
    private BottomNavigationView nav;
    private ImageButton save;
    private ImageButton close;

    private Button button_send_profile_change;
    private Button button_send_change_password;
    private Button button_send_change_pin;

    private TextView nav_name;
    private TextView nav_email;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ctx = v.getContext();

        init();
        initViews();
        initDialogViews();
        setListeners();

        return v;
    }


    private void init() {
        user = SharedPrefManager.getInstance(ctx).getUser();
        url = new URLs();
        Headers = Helper.getInstance(ctx).headers();
    }

    private void initViews() {

        bundee = v.findViewById(R.id.tv_user_profile_bundee);
        location = v.findViewById(R.id.tv_user_profile_location);
        assign_loc = v.findViewById(R.id.tv_user_profile_assgin_location);
        approver = v.findViewById(R.id.tv_user_profile_approver);
        days = v.findViewById(R.id.tv_user_profile_day_sched);
        sched = v.findViewById(R.id.tv_user_profile_sched);

        // Inside MainActivity
        // Inside app_bar_main.xml
        // Is referenced within getActivity
        toggle_edit_profile = v.findViewById(R.id.img_edit_profile);
        user_bio_appbar = getActivity().findViewById(R.id.user_bio_appbar);
        main_user_img = getActivity().findViewById(R.id.img_user_profile);
        name = getActivity().findViewById(R.id.tv_user_profile_name);
        email = getActivity().findViewById(R.id.tv_user_profile_email);
        num = getActivity().findViewById(R.id.tv_user_profile_number);

        final NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        nav_name = headerView.findViewById(R.id.tv_name);
        nav_email = headerView.findViewById(R.id.tv_email);
    }

    private void initDialogViews() {

        userProfileDialog = new Dialog(ctx, R.style.Dialog_Fullscreen_with_Animation);
        userProfileDialog.setContentView(R.layout.dialog_user_profile_edit);
        userProfileDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        userProfileDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        userProfileDialog.setOnDismissListener(this);


        dialog_user_img = userProfileDialog.findViewById(R.id.img_profile_edit);
        btn_change_picture = userProfileDialog.findViewById(R.id.btn_change_picture);
        fname = userProfileDialog.findViewById(R.id.ed_profile_fname);
        lname = userProfileDialog.findViewById(R.id.ed_profile_lname);
        dialog_email = userProfileDialog.findViewById(R.id.ed_profile_email);
        cell_num = userProfileDialog.findViewById(R.id.ed_profile_cell_num);

        old_pass = userProfileDialog.findViewById(R.id.ed_profile_old_pass);
        new_pass = userProfileDialog.findViewById(R.id.ed_profile_new_pass);
        confirm_pass = userProfileDialog.findViewById(R.id.ed_profile_confirm_pass);

        old_pin = userProfileDialog.findViewById(R.id.ed_profile_old_pin);
        new_pin = userProfileDialog.findViewById(R.id.ed_profile_new_pin);
        confirm_pin = userProfileDialog.findViewById(R.id.ed_profile_confirm_pin);

        profile_layout = userProfileDialog.findViewById(R.id.profile_layout);
        pass_layout = userProfileDialog.findViewById(R.id.layout_change_pass);
        pin_layout = userProfileDialog.findViewById(R.id.layout_change_pin);
        nav = userProfileDialog.findViewById(R.id.navigation_edit_profile);


        button_send_profile_change = userProfileDialog.findViewById(R.id.button_send_profile_change);
        button_send_change_password = userProfileDialog.findViewById(R.id.button_send_change_password);
        button_send_change_pin = userProfileDialog.findViewById(R.id.button_send_change_pin);
        img_dialog_close = userProfileDialog.findViewById(R.id.img_dialog_close);

    }

    private void setListeners() {

        nav.setOnNavigationItemSelectedListener(navListener);

        toggle_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Hides Profile appbar
                user_bio_appbar.setVisibility(View.GONE);


                String url_user_img = URLs.url_image(user.getCompany(), userProfileItem.getUser_image());

                fname.setHint(userProfileItem.getFname());
                lname.setHint(userProfileItem.getLname());
                dialog_email.setHint(userProfileItem.getEmail());
                cell_num.setHint(userProfileItem.getCell_num());

                userProfileDialog.show();
            }
        });

        btn_change_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showGallery();
            }
        });

        button_send_profile_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fileType == null || originalFile == null) {
                    saveChangesRyan();
                } else {
                    saveChangesKarl();
                }

            }
        });

        button_send_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String oldpass = old_pass.getText().toString();
                final String newpass = new_pass.getText().toString();
                final String confirmpass = confirm_pass.getText().toString();

                if (oldpass.length() > 0 && newpass.length() > 0 && confirmpass.length() > 0) {
                    if (!oldpass.equals(confirmpass) && !oldpass.equals(newpass)) {
                        if (confirmpass.equals(newpass)) {
                            changePass();
                        } else {
                            Toasty.error(ctx, "New passwords should match", Toasty.LENGTH_LONG).show();
                        }
                    } else {
                        Toasty.error(ctx, "Old passwords should not be the same as new password", Toasty.LENGTH_LONG).show();
                    }
                } else {
                    Toasty.error(ctx, "Please fill all fields", Toasty.LENGTH_LONG).show();
                }
            }
        });

        button_send_change_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String oldpin = old_pin.getText().toString();
                final String newpin = new_pin.getText().toString();
                final String confirmpin = confirm_pin.getText().toString();

                if (oldpin.length() > 0 && newpin.length() > 0 && confirmpin.length() > 0) {
                    if (!oldpin.equals(confirmpin) && !oldpin.equals(newpin)) {
                        if (confirmpin.equals(newpin)) {
                            changePin();
                        } else {
                            Toasty.error(ctx, "New pins should match", Toasty.LENGTH_LONG).show();
                        }
                    } else {
                        Toasty.error(ctx, "Old pins should not be the same as new password", Toasty.LENGTH_LONG).show();
                    }
                } else {
                    Toasty.error(ctx, "Please fill all fields", Toasty.LENGTH_LONG).show();
                }
            }
        });

        img_dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userProfileDialog != null && userProfileDialog.isShowing()) {
                    userProfileDialog.dismiss();
                }
            }
        });
    }

    private void loadMyProfile() {
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), null, "Please Wait...");
        dialog.show();
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());

        String user_profile_url = url.url_user_profile(user.getUser_id(), user.getApi_token(), user.getLink());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, user_profile_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                try {
                    JSONObject usr_pro_obj = new JSONObject(response);
                    String stringMsg = usr_pro_obj.getString("msg");
                    Log.d("@profile", stringMsg);

                    JSONArray usr_pro_count = usr_pro_obj.getJSONArray("msg");

                    for (int i = 0; i < usr_pro_count.length(); i++) {
                        JSONObject profile_data = usr_pro_count.getJSONObject(i);

                        // class userProfileItem
                        userProfileItem = new UserProfileItem();

                        // Get the data from the json object
                        String img_name = profile_data.getJSONObject("users").getString("image");
                        userProfileItem.setUser_image(img_name);
                        String url_user_image = URLs.url_image(user.getCompany(), userProfileItem.getUser_image());

                        String full_name = profile_data.getJSONObject("users").getString("fname") + " " + profile_data.getJSONObject("users").getString("lname");
                        userProfileItem.setFname(profile_data.getJSONObject("users").getString("fname"));
                        userProfileItem.setLname(profile_data.getJSONObject("users").getString("lname"));


                        userProfileItem.setEmp_num(profile_data.getString("emp_num"));
                        userProfileItem.setEmail(profile_data.getString("email"));

                        String cell_num = profile_data.getJSONObject("users").getString("cell_num");
                        userProfileItem.setCell_num(profile_data.getJSONObject("users").getString("cell_num"));


                        String user_location = profile_data.getJSONObject("work_location").getString("branch_name");

                        setImageViaGlide(url_user_image);

                        name.setText(full_name);
                        email.setText(profile_data.getString("email"));
                        num.setText(cell_num);
                        location.setText(user_location);

                        nav_name.setText(full_name);
                        nav_email.setText(profile_data.getString("email"));

                        SharedPrefManager.getInstance(ctx).setFname(profile_data.getJSONObject("users").getString("fname"));
                        SharedPrefManager.getInstance(ctx).setLname(profile_data.getJSONObject("users").getString("lname"));
                        SharedPrefManager.getInstance(ctx).setEmail(profile_data.getString("email"));

                        // approver
                        JSONArray reports_to_array = profile_data.getJSONArray("reports_to");
                        String reports_to_string = "- - -";

                        if (reports_to_array.length() > 0) {
                            //approver.setText("");
                            reports_to_string = "";
                            for (int ra = 0; ra < reports_to_array.length(); ra++) {
                                JSONObject reports_to_object = reports_to_array.getJSONObject(ra);
                                //approver.append(reports_to_object.getString("fname") + " " + reports_to_object.getString("lname"));
                                reports_to_string = reports_to_string + reports_to_object.getString("fname") + " " + reports_to_object.getString("lname");
                                if (reports_to_array.length() != ra + 1) {
                                    //approver.append(", ");
                                    reports_to_string = reports_to_string + ", ";
                                }
                            }
                        }
                        approver.setText(reports_to_string);

                        // emp_sched

                        String assigned_location_string = assign_loc.getText().toString();
                        String days_string = days.getText().toString();
                        String sched_string = sched.getText().toString();

                        JSONArray emp_sched_array = profile_data.getJSONArray("emp_sched");

                        if (emp_sched_array.length() > 0) {

                            for (int a = 0; a < emp_sched_array.length(); a++) {

                                JSONObject assign_loc_data = emp_sched_array.getJSONObject(a);
                                //assign_loc.append(assign_loc_data.getString("branch_name"));


                                StringBuilder string_assigned_work_location = new StringBuilder("No Assigned Kiosk");
                                // location
                                if (assign_loc_data.get("work_locations") instanceof JSONArray) {

                                    //assign_loc.setText("");
                                    JSONArray work_loc_arr = assign_loc_data.has("work_locations") ? assign_loc_data.getJSONArray("work_locations") : new JSONArray();

                                    if (work_loc_arr.length() > 0){

                                        string_assigned_work_location = new StringBuilder();

                                        for (int wl = 0; wl < work_loc_arr.length(); wl++) {
                                            JSONObject branch_obj = work_loc_arr.getJSONObject(wl);

                                            //assign_loc.append(branch_obj.getString("branch_name"));
                                            string_assigned_work_location.append(branch_obj.getString("branch_name"));
                                            if (work_loc_arr.length() != wl + 1) {

                                                //assign_loc.append(", ");
                                                string_assigned_work_location.append(",");
                                            }
                                        }
                                    }
                                }
                                //else if (assign_loc_data.get("work_locations").equals("null")) {
                                    //assign_loc.setText("");
                                    //assign_loc.setText("All locations");
                                //}
                                assign_loc.setText(string_assigned_work_location.toString());


                                // day sched
                                JSONArray days_arr = assign_loc_data.getJSONArray("days");
                                days.setText("");

                                for (int da = 0; da < days_arr.length(); da++) {

                                    JSONObject days_obj = days_arr.getJSONObject(da);
                                    String day_type = (days_obj.getInt("type") == 0) ? "(RD)" : "(REG)";
                                    days.append(days_obj.getString("day") + day_type);
                                    if (days_arr.length() != da + 1) {
                                        days.append(", ");
                                    }
                                }

                                // shift
                                String shift_in = Helper.getInstance(ctx).convertToReadableTime(assign_loc_data.getString("shift_in"));
                                String shift_out = Helper.getInstance(ctx).convertToReadableTime(assign_loc_data.getString("shift_out"));
                                sched.setText("");
                                sched.append(
                                        assign_loc_data.getString("sched_name") + " " + "(" + shift_in + " - " + shift_out + ")");

                            }
                        }
                        // bundee
                        JSONArray bundee_count = profile_data.getJSONObject("work_location").getJSONArray("timetrack");

                        StringBuilder stringBundees = new StringBuilder("No Assigned bundee");
                        if (bundee_count.length() > 0) {
                            stringBundees = new StringBuilder();

                            for (int b = 0; b < bundee_count.length(); b++) {
                                JSONObject timetrack_data = bundee_count.getJSONObject(b);
                                stringBundees.append(timetrack_data.getString("bundee"));
                                if (bundee_count.length() != b + 1) {
                                    stringBundees.append(", ");
                                }
                            }
                        }
                        bundee.setText(stringBundees.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ViewModelProviders.of((FragmentActivity) ctx).get(com.example.timekeeping_beta.Activities.ViewModels.MainActivityViewModel.class);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                dialog.dismiss();
                whenError();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {

                return Headers;
            }
        };

        queue.add(stringRequest);
    }

    private void setImageViaGlide(String url_user_image) {


//        Glide.with(ctx)
//                .load(url_user_image)
//                //.placeholder(R.drawable.ic_person_white_24dp)
//                .error(R.drawable.ic_person_white_24dp)
//                .thumbnail(
//                        Glide.with(ctx)
//                                .load(Helper.getCircleAnimation()))
//                .dontAnimate()
//
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
//                .override(500, 500)
//                .apply(ro)
//                .into(btnImage);

        Glide.with(ctx)
                .load(url_user_image)
                .error(R.drawable.ic_person_white_24dp)
                .thumbnail(
                        Glide.with(ctx)
                                .load(Helper.getInstance(ctx).getCircleAnimation()))
                .apply(RequestOptions.circleCropTransform())
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                //.skipMemoryCache(true)
                .override(500, 500)
                .into(main_user_img);

        Glide.with(ctx)
                .load(url_user_image)
                .placeholder(R.drawable.ic_person_white_24dp)
                .error(R.drawable.ic_person_white_24dp)
                .apply(RequestOptions.circleCropTransform())
                //.diskCacheStrategy(DiskCacheStrategy.NONE)
                //.skipMemoryCache(true)
                .override(500, 500)
                .into(dialog_user_img);
    }

    public void showGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        int GALLERY_INTENT_ACTIVITY_CODE = 69;
        startActivityForResult(intent, GALLERY_INTENT_ACTIVITY_CODE);
    }

    public void changePass() {
        String url_change_pass = url.url_change_pass(user.getUser_id(), user.getApi_token(), user.getLink());

        final String oldpass = old_pass.getText().toString();
        final String newpass = new_pass.getText().toString();
        final String confirmpass = confirm_pass.getText().toString();

        StringRequest requestPass = new StringRequest(Request.Method.POST, url_change_pass, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("response_pass", response);
                try {
                    JSONObject pass_obj = new JSONObject(response);
                    String message = "";

                    if (pass_obj.getString("status").equals("success")) {
                        Toasty.success(ctx, "Password Changed!", Toasty.LENGTH_LONG).show();
                    } else {
                        if (pass_obj.get("msg") instanceof JSONObject) {

                            JSONObject msg = pass_obj.getJSONObject("msg");
                            JSONArray jsonArray = msg.getJSONArray("password");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                message = jsonArray.getString(i);
                            }


                        } else if (pass_obj.get("msg") instanceof String) {

                            message = pass_obj.getString("msg");

                        }
                        Toasty.error(ctx, message, Toasty.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(ctx, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return Headers;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("old_password", oldpass);
                params.put("password", newpass);
                params.put("password_confirmation", confirmpass);
                return params;
            }
        };

        requestPass.setRetryPolicy(new DefaultRetryPolicy(60 * 1000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(requestPass);
    }

    public void changePin() {
        String url_change_pin = url.url_change_pin(user.getUser_id(), user.getApi_token(), user.getLink());

        final String oldpin = old_pin.getText().toString();
        final String newpin = new_pin.getText().toString();
        final String confirmpin = confirm_pin.getText().toString();

        StringRequest requestPin = new StringRequest(Request.Method.POST, url_change_pin, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject pin_obj = new JSONObject(response);

                    String message = "";

                    if (pin_obj.getString("status").equals("success")) {
                        Toasty.success(ctx, "Pin Changed!", Toasty.LENGTH_LONG).show();
                    } else {
                        if (pin_obj.get("msg") instanceof JSONObject) {

                            JSONObject msg = pin_obj.getJSONObject("msg");
                            JSONArray jsonArray = msg.getJSONArray("pin");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                message = jsonArray.getString(i);
                            }


                        } else if (pin_obj.get("msg") instanceof String) {

                            message = pin_obj.getString("msg");
                        }
                        Toasty.error(ctx, message, Toasty.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(ctx, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return Headers;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("old_pin", oldpin);
                params.put("pin", newpin);
                params.put("pin_confirmation", confirmpin);
                return params;
            }
        };

        requestPin.setRetryPolicy(new DefaultRetryPolicy(60 * 1000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(requestPin);
    }

    private void saveChangesKarl() {

        final String edit_fname = fname.getText().toString();
        final String edit_lname = lname.getText().toString();
        final String edit_email = email.getText().toString();
        final String edit_cell_num = cell_num.getText().toString();

        String mfname = (!edit_fname.equals("")) ? edit_fname : userProfileItem.getFname();
        String mlname = (!edit_lname.equals("")) ? edit_lname : userProfileItem.getLname();
        String memail = (!edit_email.equals("")) ? edit_email : userProfileItem.getEmail();
        String mcell_num = (!edit_cell_num.equals("")) ? edit_cell_num : userProfileItem.getCell_num();

        RequestBody method = RequestBody.create(MultipartBody.FORM, "PUT");
        RequestBody fname = RequestBody.create(MultipartBody.FORM, mfname);
        RequestBody lname = RequestBody.create(MultipartBody.FORM, mlname);
        RequestBody email = RequestBody.create(MultipartBody.FORM, memail);
        RequestBody cell_num = RequestBody.create(MultipartBody.FORM, mcell_num);
        RequestBody emp_num = RequestBody.create(MultipartBody.FORM, user.getEmp_num());
        RequestBody api_token = RequestBody.create(MultipartBody.FORM, user.getApi_token());
        RequestBody link = RequestBody.create(MultipartBody.FORM, user.getLink());


        RequestBody filePart = RequestBody.create(
                fileType,
                originalFile
        );

        MultipartBody.Part file = MultipartBody.Part.createFormData(
                "image",
                originalFile.getName(),
                filePart
        );

        Retrofit.Builder builder = new Retrofit.Builder()
                        .baseUrl(URLs.ROOT_URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        UploadAPIs client = retrofit.create(UploadAPIs.class);

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("d", user.getC1());
        headers.put("t", user.getC2());
        headers.put("token", user.getToken());

        Call<Result> call = client.updateImage(URLs.USER_PROFILE_UPDATE + user.getUser_id(),
                headers, method, email, fname, lname,
                emp_num, cell_num, api_token, link, file);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {

                if (response.body() != null) {
                    if (response.body().getStatus().equals("success")) {
                        Toasty.success(ctx, getResources().getString(R.string.api_request_success), Toasty.LENGTH_LONG).show();
                        userProfileDialog.dismiss();
                        loadMyProfile();
                    } else {
                        Toasty.error(ctx, getResources().getString(R.string.api_request_failed), Toasty.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

                Toasty.error(ctx, getResources().getString(R.string.api_request_error), Toasty.LENGTH_LONG).show();
            }
        });
    }

    public void saveChangesRyan() {
        String url_update = url.url_profile_update(user.getUser_id(), user.getApi_token(), user.getLink());

        final ProgressDialog dialog = ProgressDialog.show(getActivity(), null, "Please Wait...");
        final String edit_fname = fname.getText().toString();
        final String edit_lname = lname.getText().toString();
        final String edit_email = email.getText().toString();
        final String edit_cell_num = cell_num.getText().toString();

        String mfname = (!edit_fname.equals("")) ? edit_fname : userProfileItem.getFname();
        String mlname = (!edit_lname.equals("")) ? edit_lname : userProfileItem.getLname();
        String memail = (!edit_email.equals("")) ? edit_email : userProfileItem.getEmail();
        String mcell_num = (!edit_cell_num.equals("")) ? edit_cell_num : userProfileItem.getCell_num();

        userProfileItem.setFname(mfname);
        userProfileItem.setLname(mlname);
        userProfileItem.setEmail(memail);
        userProfileItem.setCell_num(mcell_num);

        StringRequest requestUpdate = new StringRequest(Request.Method.PUT, url_update, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                Log.d("response_profile", response);
                try {
                    JSONObject profile_obj = new JSONObject(response);
                    // set the value
                    if (profile_obj.getString("status").equals("failed")) {
                        Toasty.error(ctx, profile_obj.getString("msg"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toasty.success(ctx, "Success!", Toast.LENGTH_SHORT).show();
                        userProfileDialog.dismiss();
                        loadMyProfile();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(ctx, error.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return Headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("fname", userProfileItem.getFname());
                params.put("lname", userProfileItem.getLname());
                params.put("email", userProfileItem.getEmail());
                params.put("cell_num", userProfileItem.getCell_num());
                params.put("emp_num", userProfileItem.getEmp_num());
                return params;
            }
        };

        requestUpdate.setRetryPolicy(new DefaultRetryPolicy(60 * 1000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(requestUpdate);
    }

    public void whenError() {
        TryAgainFragment tryAgainFragment = new TryAgainFragment();
        Bundle arguments = new Bundle();
        arguments.putInt("RETURN_TO", Flag.USER_PROFILE_FRAGMENT);
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

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.nav_profile:
                    showEditProfile();
                    break;
                case R.id.nav_password:
                    showEditPassword();
                    break;
                case R.id.nav_pin:
                    showEditPIN();
                    break;
            }
            return true;
        }
    };

    private void showEditProfile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(pass_layout);
        }

        pass_layout.setVisibility(View.GONE);
        pin_layout.setVisibility(View.GONE);

        profile_layout.setVisibility(View.VISIBLE);
    }

    private void showEditPassword() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(pass_layout);
        }

        profile_layout.setVisibility(View.GONE);
        pin_layout.setVisibility(View.GONE);

        pass_layout.setVisibility(View.VISIBLE);
    }

    private void showEditPIN() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(pin_layout);
        }

        profile_layout.setVisibility(View.GONE);
        pass_layout.setVisibility(View.GONE);

        pin_layout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        user_bio_appbar.setVisibility(View.VISIBLE);
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
                        .into(dialog_user_img);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //sendImageUpdateRequest();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadMyProfile();
    }
}