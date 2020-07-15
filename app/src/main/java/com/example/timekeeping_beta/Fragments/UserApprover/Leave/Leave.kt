package com.example.timekeeping_beta.Fragments.UserApprover.Leave

import com.example.timekeeping_beta.Fragments.UserApprover.Approvee.Models.Profile

class Leave(
        var user_id:String,
        var request_id:String,
        var date_start:String,
        var date_end:String,
        var leave_type:String,
        var status:String,
        var day_type: String,
        var reason: String,
        var profile: Profile,
        var requested_at: String,
        var user_image_file_name: String
        )