package com.example.timekeeping_beta.Fragments.UserApprover.Adjustment

import com.example.timekeeping_beta.Fragments.UserApprover.Approvee.Models.Profile

open class Adjustment(
        var adjustment_id: Int,
        var user_id: String,
//        var edtr_id:Int,
        var date_in: String,

        var old_time_in: String,
        var old_time_out: String,
        var old_day_type: String,

        var time_in: String,
        var time_out: String,
        var day_type: String,

        var shift_in: String,
        var shift_out: String,
        var reference: String,
//        var grace_period:String,
//        var late_treshold:String,
//        var undertime_threshold:String,
//        var day_type:String,
        var status: String,
//        var decline_reason:String,
//        var checked_by:String,
//        var checked_at:String,
//        var created_at:String,
//        var updated_at:String,
        var profile: Profile,
        var reason: String,
        var image_file_name: String)