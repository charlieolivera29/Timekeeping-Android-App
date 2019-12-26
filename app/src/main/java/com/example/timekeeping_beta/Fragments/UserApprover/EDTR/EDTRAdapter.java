package com.example.timekeeping_beta.Fragments.UserApprover.EDTR;

import android.app.Dialog;
import android.app.ProgressDialog;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.timekeeping_beta.Fragments.UserApprover.Approvee.Models.Profile;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.Globals.Models.User;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static android.view.View.GONE;

public class EDTRAdapter extends RecyclerView.Adapter<EDTRAdapter.MyViewHolder> {

    private List<EDTRAdjustment> mDataset;
    private List<EDTRAdjustment> tempList;
    private List<EDTRAdjustment> filteredList;

    private Context ctx;

    private Dialog AdjustmentDetailsDialog;
    private Helper helper;
    private URLs url;
    private User user;
    private ProgressDialog loadingScreenDialog;

    // Provide a suitable constructor (depends on the kind of dataset)
    public EDTRAdapter(List<EDTRAdjustment> myDataset) {

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
        public CardView rvi_adjustment;
        public TextView name;
        public TextView original_time_in;
        public TextView original_time_out;
        public TextView time_in;
        public TextView time_out;
        public TextView date;
        public TextView adjustment_request_day_type;

        public ImageView adjustment_avatar;
        public LinearLayout approver_pending_options;
        public CardView adjustment_status;
        public MaterialCardView approve_button;
        public MaterialCardView decline_button;
        public MaterialCardView back_button;
        public EditText decline_reason;
        public TextView adjustment_text_status;
        public TextView original_time_title_container;
        public LinearLayout original_time_container;

        public MyViewHolder(LinearLayout v) {
            super(v);
            name = v.findViewById(R.id.rvi_employee_name);
            original_time_in = v.findViewById(R.id.rvi_original_time_in);
            original_time_out = v.findViewById(R.id.rvi_original_time_out);
            time_in = v.findViewById(R.id.rvi_adjustment_time_in);
            time_out = v.findViewById(R.id.rvi_adjustment_time_out);
            date = v.findViewById(R.id.rvi_adjustment_date);
            adjustment_status = v.findViewById(R.id.rvi_adjustment_status);
            rvi_adjustment = v.findViewById(R.id.rvi_adjustment);
            adjustment_request_day_type = v.findViewById(R.id.adjustment_request_day_type);

            adjustment_avatar = v.findViewById(R.id.rvi_adjustment_avatar);
            approver_pending_options = v.findViewById(R.id.rvi_approver_pending_options);
            approve_button = v.findViewById(R.id.rvi_approve_button);
            decline_button = v.findViewById(R.id.rvi_decline_button);
            back_button = v.findViewById(R.id.rvi_back_button);
            decline_reason = v.findViewById(R.id.rvi_decline_reason);
            adjustment_text_status = v.findViewById(R.id.rvi_adjustment_text_status);

            original_time_container = v.findViewById(R.id.original_time_container);
            original_time_title_container = v.findViewById(R.id.original_time_title_container);
        }
    }

    // Create new views (invoked by the layout manager)
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ctx = parent.getContext();
        helper = Helper.getInstance(ctx);
        url = new URLs();
        user = SharedPrefManager.getInstance(ctx).getUser();
        AdjustmentDetailsDialog = new Dialog(ctx, R.style.Dialog_Fullscreen_with_Animation);

        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(ctx)
                .inflate(R.layout.recyclerview_approver_action_adjusment, parent, false);

        final MyViewHolder vh = new MyViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NotNull final MyViewHolder holder, final int pos) {

        final int position = holder.getAdapterPosition();

        final EDTRAdjustment adjustment = mDataset.get(position);

        Profile profile = adjustment.getProfile();
        final String user_id = adjustment.getUser_id();
        final String shift_in = adjustment.getShift_in();
        final String shift_out = adjustment.getShift_out();
        final String time_in = adjustment.getTime_in();
        final String time_out = adjustment.getTime_out();
        final String date = adjustment.getDate_in();
        final String status = adjustment.getStatus();
        final String text_status = status.substring(0, 1).toUpperCase() + status.substring(1);
        final String reason = adjustment.getReason();
        final String fname = profile.getFname();
        final String lname = profile.getLname();
        final String full_name = fname + " " + lname;

        holder.name.setText(full_name);
        holder.time_in.setText(helper.convertToReadableTime(adjustment.getTime_in()));
        holder.time_out.setText(helper.convertToReadableTime(adjustment.getTime_out()));

        holder.original_time_container.setVisibility(GONE);
        holder.original_time_title_container.setVisibility(GONE);

        holder.original_time_in.setText("XXX");
        holder.original_time_out.setText("XXX");
        holder.date.setText(helper.convertToReadableDate(date));
        holder.adjustment_request_day_type.setText(adjustment.getDay_type());


        final String url_user_image = URLs.url_image(SharedPrefManager.getInstance(ctx).getUser().getCompany(), adjustment.getUser_image_name());

        Glide.with(ctx)
                .load(url_user_image)
                .placeholder(R.drawable.ic_person_black_24dp)
                .error(R.drawable.ic_person_black_24dp)
                .apply(RequestOptions.circleCropTransform()).
                into(holder.adjustment_avatar);



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

        holder.adjustment_text_status.setText(text_status);
        holder.adjustment_status.setCardBackgroundColor(color);


        holder.approve_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JSONObject params = new JSONObject();
                try {
                    params.put("approver_user_id", user.getUser_id());
                    params.put("decline_reason", "");


                    params.put("checked_by", user.getEmail());
                    params.put("company_id", user.getCompany_ID());
                    params.put("date_in", adjustment.getDate_in());
                    params.put("day_type", adjustment.getDay_type());
                    params.put("id", adjustment.getId());
                    params.put("session_id", user.getUser_id());
                    params.put("status", "approved");
                    params.put("time_in", adjustment.getTime_in());
                    params.put("time_out", adjustment.getTime_out());
                    params.put("user_id", adjustment.getUser_id());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                updateEDTR(pos, params);
                updateEDTR2(pos, params);
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

                        JSONObject params = new JSONObject();
                        try {
                            params.put("approver_user_id", user.getUser_id());

                            params.put("checked_by", user.getEmail());
                            params.put("company_id", user.getCompany_ID());
                            params.put("date_in", adjustment.getDate_in());
                            params.put("day_type", adjustment.getDay_type());
                            params.put("id", adjustment.getId());
                            params.put("session_id", user.getUser_id());
                            params.put("decline_reason", holder.decline_reason.getText());
                            params.put("status", "declined");
                            params.put("time_in", adjustment.getTime_in());
                            params.put("time_out", adjustment.getTime_out());
                            params.put("user_id", adjustment.getUser_id());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        updateEDTR(pos, params);
                        updateEDTR2(pos, params);
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

        holder.rvi_adjustment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                showAdjustmentDetails(adjustment, full_name, reason, time_in, time_out, status, color, position, url_user_image);
            }
        });
    }

    private void showAdjustmentDetails(final EDTRAdjustment adjustment, final String full_name, final String reason, final String time_in, final String time_out, final String status, final Integer color, final Integer position, final String url_user_image) {
        AdjustmentDetailsDialog.setContentView(R.layout.dialog_approver_action);

        TextView title =  AdjustmentDetailsDialog.findViewById(R.id.dialog_title);
        title.setText("EDTR Request Approval");


        final LinearLayout modal_decline_reason_parent = AdjustmentDetailsDialog.findViewById(R.id.modal_decline_reason_parent);
        ImageButton close = AdjustmentDetailsDialog.findViewById(R.id.img_dialog_close);
        TextView otd_time_in = AdjustmentDetailsDialog.findViewById(R.id.otd_time_in);
        TextView otd_time_out = AdjustmentDetailsDialog.findViewById(R.id.otd_time_out);
        TextView otd_date_type = AdjustmentDetailsDialog.findViewById(R.id.otd_date_type);
        TextView rtd_time_in = AdjustmentDetailsDialog.findViewById(R.id.rtd_time_in);
        TextView rtd_time_out = AdjustmentDetailsDialog.findViewById(R.id.rtd_time_out);
        final TextView rtd_date_type = AdjustmentDetailsDialog.findViewById(R.id.rtd_date_type);
        //CardView modal_app_bar = AdjustmentDetailsDialog.findViewById(R.id.modal_app_bar);
        LinearLayout time_adjustment_info = AdjustmentDetailsDialog.findViewById(R.id.time_adjustment_info);
        TextView modal_employee_name = AdjustmentDetailsDialog.findViewById(R.id.modal_employee_name);
        TextView modal_date = AdjustmentDetailsDialog.findViewById(R.id.modal_date);
        TextView modal_requestee_email = AdjustmentDetailsDialog.findViewById(R.id.modal_requestee_email);
        TextView modal_reason = AdjustmentDetailsDialog.findViewById(R.id.modal_adjustment_reason);
        final EditText modal_edittext_decline_reason = AdjustmentDetailsDialog.findViewById(R.id.modal_edittext_decline_reason);
        final MaterialCardView approve = AdjustmentDetailsDialog.findViewById(R.id.modal_approve_button);
        final MaterialCardView decline = AdjustmentDetailsDialog.findViewById(R.id.modal_decline_button);
        final MaterialCardView cancel = AdjustmentDetailsDialog.findViewById(R.id.modal_cancel_button);

        final ImageView requestee_image = AdjustmentDetailsDialog.findViewById(R.id.appbar_approvee_image);
        final LinearLayout original_time_container = AdjustmentDetailsDialog.findViewById(R.id.original_time_container);

        //modal_app_bar.setCardBackgroundColor(color);
        time_adjustment_info.setVisibility(View.VISIBLE);
        original_time_container.setVisibility(GONE);

        modal_date.setText(helper.convertToReadableDate(adjustment.getDate_in()));

        String si = helper.convertToReadableTime(adjustment.getShift_in());
        String so = helper.convertToReadableTime(adjustment.getShift_out());
        modal_requestee_email.setText(si + " - " + so);

        modal_employee_name.setText(full_name);
        modal_reason.setText(reason);

        otd_time_in.setText("XXX");
        otd_time_out.setText("XXX");
        otd_date_type.setText("XXX");

        Glide.with(ctx)
                .load(url_user_image)
                .placeholder(R.drawable.ic_person_black_24dp)
                .error(R.drawable.ic_person_black_24dp)
                .apply(RequestOptions.circleCropTransform()).
                into(requestee_image);


        rtd_time_in.setText(helper.convertToReadableTime(time_in));
        rtd_time_out.setText(helper.convertToReadableTime(time_out));
        rtd_date_type.setText(adjustment.getDay_type());

        if (status.equals("pending")) {

            modal_decline_reason_parent.setVisibility(View.GONE);
            approve.setVisibility(View.VISIBLE);
            decline.setVisibility(View.VISIBLE);


            approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    JSONObject params = new JSONObject();
                    try {

                        params.put("approver_user_id", user.getUser_id());
                        params.put("decline_reason", "");

                        params.put("checked_by", user.getEmail());
                        params.put("company_id", user.getCompany_ID());
                        params.put("date_in", adjustment.getDate_in());
                        params.put("day_type", adjustment.getDay_type());
                        params.put("id", adjustment.getId());
                        params.put("session_id", user.getUser_id());
                        params.put("status", "approved");
                        params.put("time_in", adjustment.getTime_in());
                        params.put("time_out", adjustment.getTime_out());
                        params.put("user_id", adjustment.getUser_id());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    updateEDTR(position, params);
                    updateEDTR2(position, params);
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
                            JSONObject params = new JSONObject();
                            try {
                                params.put("approver_user_id", user.getUser_id());

                                params.put("checked_by", user.getEmail());
                                params.put("company_id", user.getCompany_ID());
                                params.put("date_in", adjustment.getDate_in());
                                params.put("day_type", adjustment.getDay_type());
                                params.put("id", adjustment.getId());
                                params.put("session_id", user.getUser_id());
                                params.put("decline_reason", modal_edittext_decline_reason.getText().toString());
                                params.put("status", "declined");
                                params.put("time_in", adjustment.getTime_in());
                                params.put("time_out", adjustment.getTime_out());
                                params.put("user_id", adjustment.getUser_id());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            updateEDTR(position, params);
                            updateEDTR2(position, params);
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

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modal_decline_reason_parent.setVisibility(GONE);
                AdjustmentDetailsDialog.dismiss();
            }
        });

        AdjustmentDetailsDialog.show();
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

        Integer i = mDataset.size();

        //Finds Object with input status
        for (EDTRAdjustment adjustment : mDataset) {

            if (adjustment.getStatus().equals(status)) {
                filteredList.add(adjustment);
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

    private void updateEDTR(final int pos, final JSONObject jsonBody) {

        final URLs url = new URLs();
        final User user = SharedPrefManager.getInstance(ctx).getUser();

        String url_edtr_update = url.url_edtr_update(user.getApi_token(), user.getLink());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_edtr_update, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {

                        updateStatus(pos, "success");
                        Toasty.success(ctx, "Success on updating of timeapproval!", Toasty.LENGTH_LONG).show();
                    } else {
                        Toasty.error(ctx, response_obj.getString("msg"), Toasty.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toasty.error(ctx, "Unable to update timeapproval!", Toasty.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(ctx, "Unable to update timeapproval!", Toasty.LENGTH_LONG).show();
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return jsonBody.toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() {
                return helper.headers();
            }
        };
        Helper.getInstance(ctx).addToRequestQueue(stringRequest);
    }

    private void updateEDTR2(final int pos, final JSONObject jsonBody) {

        final URLs url = new URLs();
        final User user = SharedPrefManager.getInstance(ctx).getUser();

        String url_edtr_update = url.url_edtr_update2(user.getApi_token(), user.getLink());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_edtr_update, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    if (status.equals("success")) {

//                        updateStatus(pos, "success");
//                        Toasty.success(ctx, "Success on updating of timeapproval!", Toasty.LENGTH_LONG).show();
                    } else {
//                        Toasty.error(ctx, response_obj.getString("msg"), Toasty.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Toasty.error(ctx, "Unable to update timeapproval!", Toasty.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toasty.error(ctx, "Unable to update timeapproval!", Toasty.LENGTH_LONG).show();
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return jsonBody.toString().getBytes();
            }

            @Override
            public Map<String, String> getHeaders() {
                return helper.headers();
            }
        };
        Helper.getInstance(ctx).addToRequestQueue(stringRequest);
    }

    public void updateStatus(Integer i_position, String i_status) {
        EDTRAdjustment adjustment = mDataset.get(i_position);
        adjustment.setStatus(i_status);
        notifyItemChanged(i_position);
        notifyDataSetChanged();
        filterByStatus("pending");
    }
}

