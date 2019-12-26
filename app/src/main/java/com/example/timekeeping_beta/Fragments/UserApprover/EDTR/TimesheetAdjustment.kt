package com.example.timekeeping_beta.Fragments.UserApprover.EDTR

import com.example.timekeeping_beta.Fragments.UserApprover.Approvee.Models.Profile

class TimesheetAdjustment(
        var id: Int,
        var user_id: String,
        //        var edtr_id:Int,
        var date_in: String,

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
        //        var reason:String,
        //        var decline_reason:String,
        //        var checked_by:String,
        //        var checked_at:String,
        //        var created_at:String,
        //        var updated_at:String,
        var profile: Profile,

        //Inherits all adjustment properties from adjustment class

        //decline_reason: String = ""
        var reason: String,
        var user_image_name: String
)

//Kotlin inheritance
//): Adjustment(user_id,date_in,time_in,time_out,day_type,original_time_in,shift_out,reference,status,profile,reason)