package com.example.timekeeping_beta.Globals.CustomClasses.Interface;

import com.example.timekeeping_beta.Fragments.Profile.Models.Result;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UploadAPIs {

    @Multipart
    @POST("{fullUrl}")
    Call<Result> updateImage(
            @Path(value = "fullUrl", encoded = true) String fullUrl,
            @HeaderMap Map<String, String> headers,
            @Part("_method") RequestBody method,
            @Part("email") RequestBody email,
            @Part("fname") RequestBody fname,
            @Part("lname") RequestBody lname,
            @Part("emp_num") RequestBody emp_num,
            @Part("cell_num") RequestBody cell_num,
            @Part("api_token") RequestBody api_token,
            @Part("link") RequestBody link,
            @Part MultipartBody.Part image);


    @Multipart
    @POST("adminbackend/api/employee")
    Call<Result> createEmployee(
            @HeaderMap Map<String, String> headers,
            @Part("api_token") RequestBody api_token,
            @Part("link") RequestBody link,
            @Part("email") RequestBody email,
            @Part("fname") RequestBody fname,
            @Part("lname") RequestBody lname,
            @Part("emp_num") RequestBody emp_num,
            @Part("company") RequestBody company,
            @Part("project") RequestBody project,
            @Part("approver_id") RequestBody approver_id,
            @Part("role_id") RequestBody role_id,
            @Part("cell_num") RequestBody cell_num,
            @Part("bundee") RequestBody bundee,
            @Part("isExcluded") RequestBody isExcluded,
            @Part("isFlexible") RequestBody isFlexible,
            @Part("work_locations") RequestBody work_locations,
            @Part("branch_id") RequestBody branch_id,
            //@Part("sched_id") RequestBody sched_id,

            @Part("image") MultipartBody.Part image);


    //Always use query
    @Multipart
    @POST("clock/api/time-entry-using-app-kiosk")
        Call<Result> mobileTimeInApproval(
            @Query("api_token") String api_token,
            @Query("link") String link,
            @HeaderMap Map<String, String> headers,
            @Part("user_id") RequestBody user_id,
            @Part("time") RequestBody time,
            @Part("date") RequestBody date,
            @Part("reference") RequestBody reference,
            @Part MultipartBody.Part image);
}

