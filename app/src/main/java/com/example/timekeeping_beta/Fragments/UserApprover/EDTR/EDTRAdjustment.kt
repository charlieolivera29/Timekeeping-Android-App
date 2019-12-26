package com.example.timekeeping_beta.Fragments.UserApprover.EDTR

import com.example.timekeeping_beta.Fragments.UserApprover.Approvee.Models.Profile
import org.json.JSONObject

data class EDTRAdjustment (
        var id: Int,
        var user_id: String,
        var edtr_id: String,
        var date_in: String,
        var time_in: String,
        var time_out: String,
        var shift_in: String,
        var shift_out: String,
        var reference: String,
        var day_type: String,
        var grace_period: Int,
        var late_threshold: String,
        var undertime_thershold: String,
        var remarks: String,
        var reason: String,
        var decline_reason: String,
        var isBroken: String,
        var shift: Int,
        var checked_by: String,
        var checked_at: String,
        var status: String,
        var created_at: String,
        var updated_at: String,
        var profile: Profile,
        var user_image_name: String
)