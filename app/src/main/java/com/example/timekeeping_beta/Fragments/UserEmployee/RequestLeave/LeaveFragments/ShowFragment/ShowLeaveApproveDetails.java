package com.example.timekeeping_beta.Fragments.UserEmployee.RequestLeave.LeaveFragments.ShowFragment;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.timekeeping_beta.R;
import com.example.timekeeping_beta.Fragments.UserEmployee.RequestLeave.DialogFragment.CancelDialogFragment;
import com.example.timekeeping_beta.Fragments.UserEmployee.RequestLeave.LeaveRequestItem;

public class ShowLeaveApproveDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_leave_approve_details);

        TextView textViewDateStart = findViewById(R.id.approve_leave_date_start);
        TextView textViewDateEnd = findViewById(R.id.approve_leave_date_end);
        TextView textViewCode = findViewById(R.id.approve_leave_code);
        TextView textViewStatus = findViewById(R.id.approve_leave_status);
        TextView textViewShiftIn = findViewById(R.id.approve_leave_shift_in);
        TextView textViewShiftOut = findViewById(R.id.approve_leave_shift_out);
        TextView textViewReason = findViewById(R.id.approve_leave_reason);

        LinearLayout btnCancel = findViewById(R.id.btn_cancel_leave);

        if(getIntent().hasExtra("leave_approve")){
            LeaveRequestItem item = getIntent().getParcelableExtra("leave_approve");

            String request_id = item.getRequest_id();
            textViewDateStart.setText(item.getDate_start());
            textViewDateEnd.setText(item.getDate_end());
            textViewCode.setText(item.getDay_type());
            textViewStatus.setText(item.getStatus());
            textViewShiftIn.setText(item.getTime_start());
            textViewShiftOut.setText(item.getTime_end());
            textViewReason.setText(item.getReason());

            SharedPreferences.Editor editor = getSharedPreferences("Leave_Data", MODE_PRIVATE).edit();
            editor.putString("request_id", request_id);
            editor.apply();
        }


        // hide titlebar
        getSupportActionBar().hide();

        // Close Button
        ImageButton btnClose = findViewById(R.id.img_fullscreen_dialog_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

        // Delete Button
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openConfirmDialog();
            }
        });
    }

    // Dialog
    public void openConfirmDialog(){
        CancelDialogFragment cancelDialogFragment = new CancelDialogFragment();
        cancelDialogFragment.show(getSupportFragmentManager(), "Cancel Confirm Dialog");
    }
}
