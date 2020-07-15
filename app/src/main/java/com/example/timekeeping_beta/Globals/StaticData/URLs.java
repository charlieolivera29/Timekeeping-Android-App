package com.example.timekeeping_beta.Globals.StaticData;

import com.example.timekeeping_beta.Globals.Static;

public class URLs {

    public static final String ROOT_URL = Static.ROOT_URL;

    //Login
    public static final String URL_LOGIN = ROOT_URL + "login/api/login";

    // image path
    private static final String URL_USER_IMAGE = ROOT_URL + "adminbackend/public/assets/";

    // timesheet api
    private static final String TIMESHEET_ROOT_URL = ROOT_URL + "clock/api/";
    private static final String URL_FINAL_EDTR = TIMESHEET_ROOT_URL + "time/final/edtr/";
    private static final String URL_FINAL_EDTR_V2 = TIMESHEET_ROOT_URL + "time/";

    // user profile api
    private static final String USER_PROFILE_URL = ROOT_URL + "adminbackend/api/employee/";
    public static final String USER_PROFILE_UPDATE = ROOT_URL + "adminbackend/api/profile/";
    private static final String USER_CHANGE_PASS = ROOT_URL + "adminbackend/api/employee/changepassword/";
    private static final String USER_CHANGE_PIN = ROOT_URL + "adminbackend/api/employee/changepin/";

    // user adjustment request api
    private static final String SHOW_TIMESHEET_URL = ROOT_URL + "adminbackend/api/timeadjustment/showtime-edtr";
    private static final String TIMESHEET_ADJUSTMENT_URL = ROOT_URL + "adminbackend/api/timeadjustment";
    private static final String MYTIMEADJUSTMENT = ROOT_URL + "adminbackend/api/mytimeadjustment/";
    private static final String SHOW_PENDING_ADJUSTMENT = ROOT_URL + "adminbackend/api/timeadjustment/showtime-edtr";
    private static final String UPDATE_PENDING_ADJUSTMENT = ROOT_URL + "adminbackend/api/timeadjustment/update-request/";
    private static final String DELETE_PENDING_ADJUSTMENT = ROOT_URL + "adminbackend/api/timeadjustment/delete-request/";

    // user leave request api
    private static final String SHOW_LEAVE_URL = ROOT_URL + "adminbackend/api/leave-request/";

    private static final String SHOW_LEAVE_MANAGEMENT = ROOT_URL + "adminbackend/api/leave";

    private static final String CREATE_LEAVE_URL = ROOT_URL + "adminbackend/api/leave-request";
    private static final String DELETE_LEAVE_URL = ROOT_URL + "adminbackend/api/leave-request/delete";
    private static final String CANCEL_LEAVE_URL = ROOT_URL + "adminbackend/api/leave-cancel-request";

    public static final String REQUEST_PASSWORD_CHANGE = ROOT_URL + "login/api/support/request/password-reset";
    public static final String SEND_EMAIL_URL = ROOT_URL + "mailq/api/send_resetpass?";
    //Karl

    //REQUESTOR PAGE
    private static final String GET_USER_OVERTIMES = ROOT_URL + "adminbackend/api/overtime/";
    private static final String CREATE_OVERTIME_URL = ROOT_URL + "adminbackend/api/overtime";

    //APPROVER PAGE
    private static final String GET_APPROVEES = ROOT_URL + "adminbackend/api/user/approver/";
    private static final String GET_ALL_ADJUSTMENTS = ROOT_URL + "adminbackend/api/timeadjustment/";
    //private static final String GET_USER_DASHBOARD = ROOT_URL + "adminbackend/api/dashboard-daterange/";
    private static final String GET_USER_DASHBOARD = ROOT_URL + "adminbackend/api/dashboard/";
    private static final String GET_OT_APPROVAL = ROOT_URL + "adminbackend/api/overtime/approver-index/";
    private static final String GET_TIMESHEETS_ADJUSTMENTS = ROOT_URL + "clock/api/timeapproval/index/";
    private static final String GET_ALL_LEAVES = ROOT_URL + "adminbackend/api/leave-request/index/";

    private static final String APPROVE_ADJUSTMENT = ROOT_URL + "adminbackend/api/timeadjustment/update/";
    private static final String APPROVE_LEAVE = ROOT_URL + "adminbackend/api/leave-request/approval";

    private static final String UPDATE_OVERTIME = ROOT_URL + "adminbackend/api/overtime-approval/";
    private static final String DELETE_OVERTIME_REQUEST = ROOT_URL + "adminbackend/api/overtime-request/delete";
    private static final String UPDATE_OVERTIME_REQUEST = ROOT_URL + "adminbackend/api/overtime/";
    //APPROVER PAGE

    //GLOBAL SETTINGS
    private static final String GET_LOCATIONS = ROOT_URL + "adminbackend/api/location";
    private static final String GET_HOLIDAYS = ROOT_URL + "adminbackend/api/holiday";
    private static final String GET_HOLIDAY_TYPES = ROOT_URL + "adminbackend/api/holiday-type";
    private static final String GET_LEAVE_TYPES = ROOT_URL + "adminbackend/api/leave";
    //GLOBAL SETTINGS

    private static final String GET_EPLOYEESS = ROOT_URL + "adminbackend/api/employee";
    private static final String GET_ROLES = ROOT_URL + "adminbackend/api/role";
    private static final String GET_BUNDEES = ROOT_URL + "adminbackend/api/bundee";

    private static final String GET_PENDING_REQUESTS = ROOT_URL + "adminbackend/api/dashboard-pending-request";
    private static final String GET_PENDING_APPROVALS = ROOT_URL + "adminbackend/api/dashboard-pending-approvals";
    private static final String GET_SCHEDULE = ROOT_URL + "adminbackend/api/schedule";

    private static final String GET_HR_DASHBOARD = ROOT_URL + "adminbackend/api/dashboard-attendance-daily";
    private static final String GET_BUNDEE_COUNT = ROOT_URL + "adminbackend/api/dashboard-bundeeCount?date=";
    private static final String GET_BUNDEE_EMPLOYEES = ROOT_URL + "adminbackend/api/dashboard-employeeTimeEntry?date=";

    private static final String GET_TIMESHEET_EDTR = ROOT_URL + "clock/api/time/";
    private static final String GET_WEEKLY_EDTR = ROOT_URL + "clock/api/timesheet-entry-weekly/";
    private static final String POST_CREATE_EDTR = ROOT_URL + "clock/api/timeapproval/create";
    private static final String POST_DELETE_EDTR = ROOT_URL + "clock/api/timeapproval/remove-request";
    private static final String POST_EDIT_EDTR = ROOT_URL + "clock/api/timeapproval/updatedtr";

    private static final String POST_UPDATE_EDTR = ROOT_URL + "clock/api/timeapproval/update";
    private static final String POST_UPDATE_EDTR_2 = ROOT_URL + "clock/api/timeapproval/storeEdtr";


    private static final String POST_TIME_IN_OUT = ROOT_URL + "clock/api/time-entry-using-app-kiosk";
    private static final String POST_CHECK_TIMED_IN = ROOT_URL + "clock/api/checkclockedin";
    private static final String POST_ATTENDANCE_PERCENTAGE = ROOT_URL + "adminbackend/api/dashboard-test";

    private static final String POST_LATE_COUNTS = ROOT_URL + "adminbackend/api/dashboard-late-count";
    private static final String POST_OVERTIME_COUNTS = ROOT_URL + "adminbackend/api/dashboard-ot-count";

    private static final String POST_CHECK_EDTR = ROOT_URL + "clock/api/check-entry/";

    // Charlie

    private static final String GET_COORDINATES = ROOT_URL + "adminbackend/api/employee-bundee-location/";


    // Karl

    public String url_timesheets(String user_id, String api_token, String link, String date) {
        return URL_FINAL_EDTR_V2 + user_id + "?" + "api_token=" + api_token + "&" + "link=" + link + "&" + "date=" + date;
    }

    public String url_edtr(String user_id, String api_token, String link, String date) {
        return URL_FINAL_EDTR + user_id + "?" + "api_token=" + api_token + "&" + "link=" + link + "&" + "date=" + date;
    }

    public String url_user_profile(String user_id, String api_token, String link) {
        return USER_PROFILE_URL + user_id + "?" + "api_token=" + api_token + "&" + "link=" + link;
    }

    public static String url_image(String company, String user_img) {

        if (user_img.equals("Photo.png")) {
            return URL_USER_IMAGE + user_img;
        } else {
            return URL_USER_IMAGE + company + "/images/users/" + user_img;
        }
    }

    public String url_profile_update(String user_id, String api_token, String link) {
        return USER_PROFILE_UPDATE + user_id + "?" + "api_token=" + api_token + "&" + "link=" + link;
    }

    public String url_change_pass(String user_id, String api_token, String link) {
        return USER_CHANGE_PASS + user_id + "?" + "api_token=" + api_token + "&" + "link=" + link;
    }

    public String url_change_pin(String user_id, String api_token, String link) {
        return USER_CHANGE_PIN + user_id + "?" + "api_token=" + api_token + "&" + "link=" + link;
    }

    public String url_show_timesheet(String api_token, String link) {
        return SHOW_TIMESHEET_URL + "?" + "api_token=" + api_token + "&" + "link=" + link;
    }

    public String url_timesheet_adjustment(String api_token, String link) {
        return TIMESHEET_ADJUSTMENT_URL + "?" + "api_token=" + api_token + "&" + "link=" + link;
    }

    public String url_mytimeadjustment(String user_id, String api_token, String link) {
        return MYTIMEADJUSTMENT + user_id + "?" + "api_token=" + api_token + "&" + "link=" + link;
    }

    public String url_show_adjustment(String api_token, String link) {
        return SHOW_PENDING_ADJUSTMENT + "?" + "api_token=" + api_token + "&" + "link=" + link;
    }

    public String url_update_adjustment(String id, String api_token, String link) {
        return UPDATE_PENDING_ADJUSTMENT + id + "?" + "api_token=" + api_token + "&" + "link=" + link;
    }

    public String url_delete_adjustment(String id, String api_token, String link) {
        return DELETE_PENDING_ADJUSTMENT + id + "?" + "api_token=" + api_token + "&" + "link=" + link;
    }

    public String url_show_leave(String user_id, String api_token, String link) {
        return SHOW_LEAVE_URL + user_id + "?" + "api_token=" + api_token + "&" + "link=" + link;
    }

    public String url_show_leave_management(String api_token, String link) {
        return SHOW_LEAVE_MANAGEMENT + "?data=all&" + "api_token=" + api_token + "&" + "link=" + link;
    }

    public String url_create_leave(String api_token, String link) {
        return CREATE_LEAVE_URL + "?" + "api_token=" + api_token + "&" + "link=" + link;
    }

    public String url_delete_leave(String api_token, String link) {
        return DELETE_LEAVE_URL + "?" + "api_token=" + api_token + "&" + "link=" + link;
    }

    public String url_cancel_leave(String api_token, String link) {
        return CANCEL_LEAVE_URL + "?" + "api_token=" + api_token + "&" + "link=" + link;
    }

    //Karl

    //APPROVER PAGE
    public String url_get_approvees(String user_id, String api_token, String link) {
        return GET_APPROVEES + user_id + "?" + "api_token=" + api_token + "&" + "link=" + link;
    }

    public String url_get_all_adjustments(String user_id, String api_token, String link) {
        return GET_ALL_ADJUSTMENTS + user_id + "/show-all?" + "api_token=" + api_token + "&" + "link=" + link;
    }

    public String url_get_user_dashboard(String user_id, String api_token, String link) {
        return GET_USER_DASHBOARD + user_id + "?" + "api_token=" + api_token + "&" + "link=" + link;
    }

    public String url_get_overtimes(String user_id, String api_token, String link) {
        return GET_OT_APPROVAL + user_id + "?" + "api_token=" + api_token + "&" + "link=" + link;
    }

    public String url_get_all_timesheets(String user_id, String api_token, String link) {
        return GET_TIMESHEETS_ADJUSTMENTS + user_id + "?" + "api_token=" + api_token + "&" + "link=" + link;
    }

    public String url_get_all_leaves(String user_id, String api_token, String link) {
        return GET_ALL_LEAVES + user_id + "?" + "api_token=" + api_token + "&" + "link=" + link;
    }

    public String url_approve_adjustment(String adjustment_id, String api_token, String link) {
        return APPROVE_ADJUSTMENT + adjustment_id + "?" + "api_token=" + api_token + "&" + "link=" + link;
    }

    public String url_approve_leave() {
        return APPROVE_LEAVE;
    }

    public String url_create_overtime() {
        return CREATE_OVERTIME_URL;
    }

    public String url_get_user_overtimes(String user_id, String api_token, String link) {
        return GET_USER_OVERTIMES + user_id + "?" + "api_token=" + api_token + "&" + "link=" + link;
    }

    public String url_approve_overtime(String user_id) {
        return UPDATE_OVERTIME + user_id;
    }

    public String url_delete_overtime() {
        return DELETE_OVERTIME_REQUEST;
    }

    public String url_update_overtime_request(String user_id) {
        return UPDATE_OVERTIME_REQUEST + user_id;
    }


    public String url_resource_holidays(String api_token, String link) {
        return GET_HOLIDAYS + "?api_token=" + api_token + "&link=" + link;
    }

    public String url_update_holiday(String ht_id, String api_token, String link) {
        return GET_HOLIDAYS + "/" + ht_id + "?api_token=" + api_token + "&link=" + link;
    }

    public String url_resource_holiday_types(String api_token, String link) {
        return GET_HOLIDAY_TYPES + "?api_token=" + api_token + "&link=" + link;
    }

    public String url_update_holiday_types(String ht_id, String api_token, String link) {
        return GET_HOLIDAY_TYPES + "/" + ht_id + "?api_token=" + api_token + "&link=" + link;
    }

    public String url_resource_leave_types(String api_token, String link) {
        return GET_LEAVE_TYPES + "?api_token=" + api_token + "&link=" + link;
    }

    public String url_update_leave_types(String ht_id, String api_token, String link) {
        return GET_LEAVE_TYPES + "/" + ht_id + "?api_token=" + api_token + "&link=" + link;
    }

    public String url_resource_locations(String api_token, String link) {
        return GET_LOCATIONS + "?api_token=" + api_token + "&link=" + link;
    }

    public String url_resource_employees(String api_token, String link) {
        return GET_EPLOYEESS + "?api_token=" + api_token + "&link=" + link;
    }

    public String url_resource_roles(String api_token, String link) {
        return GET_ROLES + "?api_token=" + api_token + "&link=" + link;
    }

    public String url_resource_bundees(String api_token, String link) {
        return GET_BUNDEES + "?api_token=" + api_token + "&link=" + link;
    }

    public String url_resource_pending_reuests(String api_token, String link) {
        return GET_PENDING_REQUESTS + "?api_token=" + api_token + "&link=" + link;
    }

    public String url_resource_pending_approvals(String api_token, String link) {
        return GET_PENDING_APPROVALS + "?api_token=" + api_token + "&link=" + link;
    }

    public String url_resource_schedules(String api_token, String link) {
        return GET_SCHEDULE + "?api_token=" + api_token + "&link=" + link;
    }

    public String url_update_schedule(String sched_id, String api_token, String link) {
        return GET_SCHEDULE + "/" + sched_id + "?api_token=" + api_token + "&link=" + link;
    }

    public String url_hr_dashboard(String api_token, String link) {
        return GET_HR_DASHBOARD + "?api_token=" + api_token + "&link=" + link;
    }

    public String url_bundee_count(String api_token, String link) {

        return GET_BUNDEE_COUNT + "&api_token=" + api_token + "&link=" + link;
    }

    public String url_bundee_employees(String api_token, String link) {

        return GET_BUNDEE_EMPLOYEES + "&api_token=" + api_token + "&link=" + link;
    }


    //EDTRs new
    public String url_edtr(String userId, String api_token, String link) {

        return GET_WEEKLY_EDTR + userId + "?api_token=" + api_token + "&link=" + link;
    }

    //EDTRs new
    public String url_edtr_range(String userId, String date_range, String api_token, String link) {

        return GET_WEEKLY_EDTR + userId + "?date=" + date_range + "&api_token=" + api_token + "&link=" + link;
    }

    public String url_edtr_create(String api_token, String link) {

        return POST_CREATE_EDTR + "?api_token=" + api_token + "&link=" + link;
    }

    public String url_edtr_delete(String api_token, String link) {

        return POST_DELETE_EDTR + "?api_token=" + api_token + "&link=" + link;
    }

    public String url_edtr_edit(String api_token, String link) {

        return POST_EDIT_EDTR + "?api_token=" + api_token + "&link=" + link;
    }


    //Both is called when approving/declining EDTR
    public String url_edtr_update(String api_token, String link) {

        return POST_UPDATE_EDTR + "?api_token=" + api_token + "&link=" + link;
    }

    public String url_edtr_update2(String api_token, String link) {

        return POST_UPDATE_EDTR_2 + "?api_token=" + api_token + "&link=" + link;
    }
    //Both is called when approving/declining EDTR


    public String url_time_in_out(String api_token, String link) {

        return POST_TIME_IN_OUT + "?api_token=" + api_token + "&link=" + link;
    }

    public String url_check_clocked_in(String userId, String api_token, String link) {

        return POST_CHECK_TIMED_IN + "/" + userId + "?api_token=" + api_token + "&link=" + link;
    }

    public String url_attendance_percentage(String api_token, String link) {

        return POST_ATTENDANCE_PERCENTAGE + "?api_token=" + api_token + "&link=" + link;
    }

    public String post_overtime_counts(String api_token, String link) {

        return POST_OVERTIME_COUNTS + "?api_token=" + api_token + "&link=" + link;
    }

    public String post_late_counts(String api_token, String link) {

        return POST_LATE_COUNTS + "?api_token=" + api_token + "&link=" + link;
    }

    public String url_check_entry(String user, String api_token, String link) {

        return POST_CHECK_EDTR + user + "?api_token=" + api_token + "&link=" + link;
    }
    //Karl

    public String get_coordinates(String user_id, String api_token, String link) {
        return GET_COORDINATES + user_id + "?" + "api_token=" + api_token + "&" + "link=" + link;
    }
}
