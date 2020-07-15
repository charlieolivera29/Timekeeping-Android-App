package com.example.timekeeping_beta.Fragments.UserApprover.Leave;

import android.app.Dialog;
import android.content.Context;
import android.support.design.card.MaterialCardView;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.timekeeping_beta.Fragments.UserApprover.Approvee.Models.Profile;
import com.example.timekeeping_beta.R;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.Globals.Models.User;
import com.example.timekeeping_beta.Globals.Helper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static android.view.View.GONE;

public class LeaveAdjustmentAdapter
        extends RecyclerView.Adapter<LeaveAdjustmentAdapter.MyViewHolder> {

    private List<Leave> mDataset;
    private List<Leave> tempList;
    private List<Leave> filteredList;

    private Context ctx;
    private Helper helper;
    private Dialog LeaveDetailsDialog;

    // Provide a suitable constructor (depends on the kind of dataset)
    public LeaveAdjustmentAdapter(List<Leave> myDataset) {

        this.mDataset = myDataset;
        this.tempList = new ArrayList<>();
        tempList.addAll(mDataset);

        this.filteredList = new ArrayList<>();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView rvi_leave;
        public TextView name;
        public TextView type;
        public TextView start;
        public TextView end;
        public CardView leave_status;
        public TextView leave_text_status;
        public ImageView adjustment_avatar;
        public LinearLayout approver_pending_options;
        public MaterialCardView approve_button;
        public MaterialCardView decline_button;
        public MaterialCardView back_button;
        public EditText decline_reason;

        public MyViewHolder(LinearLayout v) {
            super(v);
            name = v.findViewById(R.id.rvi_employee_name);
            type = v.findViewById(R.id.rvi_leave_type);
            start = v.findViewById(R.id.rvi_leave_start);
            end = v.findViewById(R.id.rvi_leave_end);
            leave_status = v.findViewById(R.id.rvi_leave_status);
            rvi_leave = v.findViewById(R.id.rvi_leave);

            leave_text_status = v.findViewById(R.id.rvi_leave_text_status);
            adjustment_avatar = v.findViewById(R.id.rvi_adjustment_avatar);
            approver_pending_options = v.findViewById(R.id.rvi_approver_pending_options);
            approve_button = v.findViewById(R.id.rvi_approve_button);
            decline_button = v.findViewById(R.id.rvi_decline_button);
            back_button = v.findViewById(R.id.rvi_back_button);
            decline_reason = v.findViewById(R.id.rvi_decline_reason);

        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public LeaveAdjustmentAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        ctx = parent.getContext();
        helper = Helper.getInstance(ctx);

        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(ctx)
                .inflate(R.layout.recyclerview_approver_action_leave, parent, false);
        LeaveAdjustmentAdapter.MyViewHolder vh = new LeaveAdjustmentAdapter.MyViewHolder(v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NotNull final LeaveAdjustmentAdapter.MyViewHolder holder, final int pos) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final int position = holder.getAdapterPosition();

        final Leave leave = mDataset.get(position);

        Profile profile = leave.getProfile();
        final String user_id = leave.getUser_id();

        String fname = profile.getFname();
        String lname = profile.getLname();

        final String start_date = leave.getDate_start();
        final String end_date = leave.getDate_end();

        final String full_name = fname + " " + lname;
        final String leave_type = leave.getDay_type();

        final String status = leave.getStatus();
        final String text_status = status.substring(0, 1).toUpperCase() + status.substring(1);
        String requested_at = leave.getRequested_at();

        holder.name.setText(full_name);
        holder.start.setText(helper.convertToReadableDate(start_date));
        holder.end.setText(helper.convertToReadableDate(end_date));
        holder.type.setText("(" + leave_type + ")");


        final String url_user_image = URLs.url_image(SharedPrefManager.getInstance(ctx).getUser().getCompany(), leave.getUser_image_file_name());

        Glide.with(ctx)
                .load(url_user_image)
                .placeholder(R.drawable.ic_person_black_24dp)
                .error(R.drawable.ic_person_black_24dp)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.adjustment_avatar);


        final int color;

        switch (status) {

            case "approved":
                color = ContextCompat.getColor(ctx, R.color.colorSuccess);
                holder.approver_pending_options.setVisibility(View.GONE);
                break;

            case "declined":
                color = ContextCompat.getColor(ctx, R.color.colorError);
                holder.approver_pending_options.setVisibility(View.GONE);
                break;

            case "pending":
                color = ContextCompat.getColor(ctx, R.color.colorPending);
                holder.approver_pending_options.setVisibility(View.VISIBLE);
                break;

            default:
                color = ContextCompat.getColor(ctx, R.color.colorWhiteSmoke);
                holder.approver_pending_options.setVisibility(View.GONE);
        }

        holder.leave_status.setCardBackgroundColor(color);
        holder.leave_text_status.setText(text_status);


        LeaveDetailsDialog = new Dialog(ctx, R.style.Dialog_Fullscreen_with_Animation);
        LeaveDetailsDialog.setContentView(R.layout.dialog_approver_action);

        ImageView dialog_image = LeaveDetailsDialog.findViewById(R.id.appbar_approvee_image);


        Glide.with(ctx)
                .load(url_user_image)
                .placeholder(R.drawable.ic_person_black_24dp)
                .error(R.drawable.ic_person_black_24dp)
                .apply(RequestOptions.circleCropTransform())
                .into(dialog_image);

        TextView title =  LeaveDetailsDialog.findViewById(R.id.dialog_title);
        title.setText("Leave Request Approval");


        holder.approve_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateLeaveAdjustment(position, user_id, leave.getRequest_id(), "approved", "");
            }
        });

        holder.decline_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.decline_reason.getVisibility() == GONE) {
                    holder.decline_reason.setVisibility(View.VISIBLE);
                    holder.back_button.setVisibility(View.VISIBLE);
                    holder.approve_button.setVisibility(GONE);
                } else {
                    if (!holder.decline_reason.getText().toString().isEmpty()) {
                        updateLeaveAdjustment(position, user_id, leave.getRequest_id(), "declined", holder.decline_reason.getText().toString());
                    } else {
                        Toasty.error(ctx, "Decline reason is required!", Toasty.LENGTH_LONG).show();
                    }
                }
            }
        });

        holder.back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.decline_reason.setVisibility(View.GONE);
                holder.back_button.setVisibility(View.GONE);
                holder.approve_button.setVisibility(View.VISIBLE);
            }
        });

        holder.rvi_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //CardView modal_app_bar = LeaveDetailsDialog.findViewById(R.id.modal_app_bar);
                //modal_app_bar.setCardBackgroundColor(color);

                LinearLayout time_leave_info = LeaveDetailsDialog.findViewById(R.id.time_leave_info);
                time_leave_info.setVisibility(View.VISIBLE);

                //Reusable
                TextView modal_employee_name = LeaveDetailsDialog.findViewById(R.id.modal_employee_name);
                TextView modal_reason = LeaveDetailsDialog.findViewById(R.id.modal_leave_reason);
                //Reusable

                TextView modal_leave_type = LeaveDetailsDialog.findViewById(R.id.modal_leave_type);
                TextView modal_leave_date_start = LeaveDetailsDialog.findViewById(R.id.modal_leave_date_start);
                TextView modal_leave_date_end = LeaveDetailsDialog.findViewById(R.id.modal_leave_date_end);
                TextView modal_convert_time = LeaveDetailsDialog.findViewById(R.id.modal_convert_time);
                TextView modal_requested_at = LeaveDetailsDialog.findViewById(R.id.modal_requested_at);

                final LinearLayout modal_decline_reason_parent = LeaveDetailsDialog.findViewById(R.id.modal_decline_reason_parent);
                final EditText modal_edittext_decline_reason = LeaveDetailsDialog.findViewById(R.id.modal_edittext_decline_reason);


                modal_employee_name.setText(full_name);

                modal_leave_type.setText(leave_type);
                modal_leave_date_start.setText(helper.convertToReadableDate(start_date));
                modal_leave_date_end.setText(helper.convertToReadableDate(end_date));
                modal_convert_time.setText("Wala pa");
                modal_requested_at.setText(helper.convertToReadableDate(leave.getRequested_at()));
                modal_reason.setText(leave.getReason());

                final MaterialCardView approve = LeaveDetailsDialog.findViewById(R.id.modal_approve_button);
                final MaterialCardView decline = LeaveDetailsDialog.findViewById(R.id.modal_decline_button);
                final MaterialCardView cancel = LeaveDetailsDialog.findViewById(R.id.modal_cancel_button);

                if (status.equals("pending")) {

                    modal_decline_reason_parent.setVisibility(View.GONE);
                    approve.setVisibility(View.VISIBLE);
                    decline.setVisibility(View.VISIBLE);

                    approve.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            updateLeaveAdjustment(position, user_id, leave.getRequest_id(), "approved", "");
                        }
                    });

                    decline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            if (modal_decline_reason_parent.getVisibility() == GONE) {
                                modal_decline_reason_parent.setVisibility(View.VISIBLE);
                                cancel.setVisibility(View.VISIBLE);
                                approve.setVisibility(GONE);
                            } else {
                                if (!modal_edittext_decline_reason.getText().toString().isEmpty()) {

                                    updateLeaveAdjustment(position, user_id, leave.getRequest_id(), "declined", modal_edittext_decline_reason.getText().toString());
                                } else {
                                    Toasty.error(ctx, "Decline reason is required!", Toasty.LENGTH_LONG).show();
                                }
                            }

                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            modal_decline_reason_parent.setVisibility(View.GONE);
                            cancel.setVisibility(View.GONE);
                            approve.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    modal_decline_reason_parent.setVisibility(View.GONE);
                    approve.setVisibility(GONE);
                    decline.setVisibility(GONE);
                }

                ImageButton close = LeaveDetailsDialog.findViewById(R.id.img_dialog_close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LeaveDetailsDialog.dismiss();
                    }
                });

                LeaveDetailsDialog.show();
            }
        });


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void resetAdapter() {
        mDataset.clear();
        filteredList.clear();
        mDataset.addAll(tempList);
    }

    public boolean filterByStatus(String status) {
        resetAdapter();

        for (Leave leave : mDataset) {

            if (leave.getStatus().equals(status)) {
                filteredList.add(leave);
            }
        }

        mDataset.clear();
        if (filteredList.size() > 0) {
            mDataset.addAll(filteredList);
            notifyDataSetChanged();
            return true;
        } else {
            notifyDataSetChanged();
            return false;
        }
    }

    public Boolean showAll() {
        resetAdapter();
        notifyDataSetChanged();

        return mDataset.size() > 0;
    }

    public void updateLeaveAdjustment(final Integer i_dataSet_position, final String i_user_id, final String i_request_id, final String i_status, final String i_decline_reason) {

        final URLs url = new URLs();
        final User user = SharedPrefManager.getInstance(ctx).getUser();
        final String DATABASE = user.getC1();
        final String TABLE = user.getC2();

        String url_approve_leave = url.url_approve_leave();

        StringRequest leaveUpdateRequest = new StringRequest(Request.Method.POST, url_approve_leave, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {
                        Toasty.success(ctx, status, Toast.LENGTH_SHORT).show();
                    }
                    updateLeaveStatus(i_dataSet_position, i_status);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toasty.error(ctx, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                LeaveDetailsDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(ctx, "Server Error", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return helper.headers();
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", i_user_id);
                params.put("request_id", i_request_id);
                params.put("status", i_status);
                params.put("decline_reason", i_decline_reason);
                params.put("checked_by", user.getEmail());
                params.put("company_id", user.getCompany_ID());
                params.put("api_token", user.getApi_token());
                params.put("link", user.getLink());

                return params;
            }
        };

        leaveUpdateRequest.setRetryPolicy(new DefaultRetryPolicy(60 * 1000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Helper.getInstance(ctx).addToRequestQueue(leaveUpdateRequest);
    }

    public void updateLeaveStatus(Integer i_position, String i_status) {
        Leave leave = mDataset.get(i_position);
        leave.setStatus(i_status);
        notifyItemChanged(i_position);
        notifyDataSetChanged();
        filterByStatus("pending");
    }
}


