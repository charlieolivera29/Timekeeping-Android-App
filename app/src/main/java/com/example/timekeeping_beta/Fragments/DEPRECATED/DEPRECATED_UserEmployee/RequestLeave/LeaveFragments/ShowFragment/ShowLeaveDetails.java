package com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestLeave.LeaveFragments.ShowFragment;

import android.support.design.card.MaterialCardView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.timekeeping_beta.R;
import com.example.timekeeping_beta.Fragments.Dialogs.DialogDeleteRequestFragment;
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestLeave.LeaveRequestItem;

public class ShowLeaveDetails extends AppCompatActivity {

    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_leave_request_details);

        LinearLayout leave_details_view = findViewById(R.id.leave_details_view);

        TextView title_bar_text = findViewById(R.id.title_bar_text);
        TextView textViewDateStart = findViewById(R.id.pending_leave_date_start);
        TextView textViewDateEnd = findViewById(R.id.pending_leave_date_end);
        TextView textViewCode = findViewById(R.id.pending_leave_code);
        TextView textViewStatus = findViewById(R.id.pending_leave_status);
        TextView textViewShiftIn = findViewById(R.id.pending_leave_shift_in);
        TextView textViewShiftOut = findViewById(R.id.pending_leave_shift_out);
        TextView textViewReason = findViewById(R.id.pending_reason);
        MaterialCardView btnDelete = findViewById(R.id.btn_delete_leave);

        title_bar_text.setText("Leave Request Details");

        if (getIntent().hasExtra("leave_pending")) {
            LeaveRequestItem item = getIntent().getParcelableExtra("leave_pending");

            final Integer request_id = Integer.parseInt(item.getRequest_id());
            textViewDateStart.setText(item.getDate_start());
            textViewDateEnd.setText(item.getDate_end());
            textViewCode.setText(item.getDay_type());
            textViewStatus.setText(item.getStatus());
            textViewShiftIn.setText(item.getTime_start());
            textViewShiftOut.setText(item.getTime_end());
            textViewReason.setText(item.getReason());

            if (!item.getStatus().equals("pending")) {
                btnDelete.setVisibility(View.GONE);
            } else {

                btnDelete.setVisibility(View.VISIBLE);

                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openConfirmDialog(request_id);
                    }
                });
            }


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
    }

    // Dialog
    public void openConfirmDialog(Integer i_id) {

        Bundle args = new Bundle();
        args.putInt("id", i_id);
        args.putString("flag_request", "LV");

        DialogDeleteRequestFragment deleteDialogFragment = new DialogDeleteRequestFragment();
        deleteDialogFragment.setArguments(args);
        deleteDialogFragment.show(getSupportFragmentManager(), "Delete Confirm Dialog");
    }
}
