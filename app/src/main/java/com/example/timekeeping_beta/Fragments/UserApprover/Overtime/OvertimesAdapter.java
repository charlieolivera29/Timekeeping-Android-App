package com.example.timekeeping_beta.Fragments.UserApprover.Overtime;

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
import com.example.timekeeping_beta.R;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.Globals.Models.User;
import com.example.timekeeping_beta.Globals.Helper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static android.view.View.GONE;

public class OvertimesAdapter extends RecyclerView.Adapter<OvertimesAdapter.MyViewHolder> {

    private Context ctx;

    private List<Overtime> mDataset;
    private List<Overtime> tempList;
    private List<Overtime> filteredList;

    private Dialog OvertimeDetailsDialog;
    private Helper helper;

    private URLs url;
    private User user;


    //ViewHolder
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView requested_at;
        public TextView start_time;
        public TextView end_time;
        public TextView date;
        public CardView rvi_overtime;

        public CardView overtime_status;
        public TextView overtime_text_status;
        public ImageView adjustment_avatar;
        public LinearLayout approver_pending_options;
        public MaterialCardView approve_button;
        public MaterialCardView decline_button;
        public MaterialCardView back_button;
        public EditText decline_reason;


        public MyViewHolder(CardView v) {
            super(v);
            name = v.findViewById(R.id.rvi_employee_name);
            requested_at = v.findViewById(R.id.rvi_overtime_requested_at);
            start_time = v.findViewById(R.id.rvi_overtime_start_time);
            end_time = v.findViewById(R.id.rvi_overtime_end_time);
            date = v.findViewById(R.id.rvi_overtime_date);
            overtime_status = v.findViewById(R.id.rvi_overtime_status);
            rvi_overtime = v.findViewById(R.id.rvi_overtime);

            overtime_text_status = v.findViewById(R.id.rvi_overtime_text_status);
            adjustment_avatar = v.findViewById(R.id.rvi_adjustment_avatar);
            approver_pending_options = v.findViewById(R.id.rvi_approver_pending_options);
            approve_button = v.findViewById(R.id.rvi_approve_button);
            decline_button = v.findViewById(R.id.rvi_decline_button);
            back_button = v.findViewById(R.id.rvi_back_button);
            decline_reason = v.findViewById(R.id.rvi_decline_reason);
        }
    }

    //Constructor
    public OvertimesAdapter(List<Overtime> myDataset) {

        this.mDataset = myDataset;
        this.tempList = new ArrayList<>();
        tempList.addAll(mDataset);

        this.filteredList = new ArrayList<>();
    }


    @Override
    public OvertimesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        ctx = parent.getContext();

        helper = Helper.getInstance(ctx);
        url = new URLs();
        user = SharedPrefManager.getInstance(ctx).getUser();
        // create a new view
        CardView v = (CardView) LayoutInflater.from(ctx)
                .inflate(R.layout.recyclerview_approver_action_overtime, parent, false);
        OvertimesAdapter.MyViewHolder vh = new OvertimesAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NotNull final MyViewHolder holder, final int ipoos) {

        final int position = holder.getAdapterPosition();

        Overtime overtime = mDataset.get(position);

        final int overtime_id = overtime.getId();
        final String employee_id = overtime.getUser_id();
        final String date = overtime.getDate();
        final String start_time = overtime.getStart_time();
        final String end_time = overtime.getEnd_time();
        final String status = overtime.getStatus();
        final String text_status = status.substring(0, 1).toUpperCase() + status.substring(1);

        Date time_from = helper.stringTimeToDate(overtime.getStart_time());
        Date time_to = helper.stringTimeToDate(overtime.getEnd_time());
        Long time_rendered_seconds = (time_to.getTime() - time_from.getTime()) / 1000;

        final String rendered_hours = time_rendered_seconds >= 3600 ? String.valueOf(time_rendered_seconds / 3600) : String.valueOf(time_rendered_seconds);

        final String reason = overtime.getReason();

        String requested_at = overtime.getRequested_at();
        String fname = overtime.getFname();
        String lname = overtime.getLname();
        final String name = fname + " " + lname;

        holder.name.setText(name);
        holder.requested_at.setText(requested_at);
        holder.start_time.setText(helper.convertToReadableTime(start_time));
        holder.end_time.setText(helper.convertToReadableTime(end_time));
        holder.date.setText(helper.convertToReadableDate(date));


        final String url_user_image = URLs.url_image(user.getCompany(), overtime.getUser_image_file_name());

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

        holder.overtime_status.setCardBackgroundColor(color);
        holder.overtime_text_status.setText(text_status);

        OvertimeDetailsDialog = new Dialog(ctx, R.style.Dialog_Fullscreen_with_Animation);
        OvertimeDetailsDialog.setContentView(R.layout.dialog_approver_action);

        ImageView dialog_image = OvertimeDetailsDialog.findViewById(R.id.appbar_approvee_image);

        Glide.with(ctx)
                .load(url_user_image)
                .placeholder(R.drawable.ic_person_black_24dp)
                .error(R.drawable.ic_person_black_24dp)
                .apply(RequestOptions.circleCropTransform())
                .into(dialog_image);

        TextView title =  OvertimeDetailsDialog.findViewById(R.id.dialog_title);
        title.setText("Overtime Request Approval");




        holder.approve_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateOvertime(position, String.valueOf(overtime_id), employee_id, "approved", "");
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
                        updateOvertime(position, String.valueOf(overtime_id), employee_id, "declined", holder.decline_reason.getText().toString());
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

        holder.rvi_overtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CardView modal_app_bar = OvertimeDetailsDialog.findViewById(R.id.modal_app_bar);
                //modal_app_bar.setCardBackgroundColor(color);

                LinearLayout time_overtime_info = OvertimeDetailsDialog.findViewById(R.id.time_overtime_info);
                time_overtime_info.setVisibility(View.VISIBLE);

                TextView modal_employee_name = OvertimeDetailsDialog.findViewById(R.id.modal_employee_name);

                TextView modal_overtime_status = OvertimeDetailsDialog.findViewById(R.id.modal_overtime_status);
                TextView modal_overtime_date = OvertimeDetailsDialog.findViewById(R.id.modal_overtime_date);
                TextView modal_overtime_time_start = OvertimeDetailsDialog.findViewById(R.id.modal_overtime_time_start);
                TextView modal_overtime_time_end = OvertimeDetailsDialog.findViewById(R.id.modal_overtime_time_end);
                TextView modal_overtime_hours_rendered = OvertimeDetailsDialog.findViewById(R.id.modal_overtime_hours_rendered);

                TextView modal_reason = OvertimeDetailsDialog.findViewById(R.id.modal_overtime_reason);
                final LinearLayout modal_decline_reason_parent = OvertimeDetailsDialog.findViewById(R.id.modal_decline_reason_parent);
                final EditText modal_edittext_decline_reason = OvertimeDetailsDialog.findViewById(R.id.modal_edittext_decline_reason);

                final MaterialCardView approve = OvertimeDetailsDialog.findViewById(R.id.modal_approve_button);
                final MaterialCardView decline = OvertimeDetailsDialog.findViewById(R.id.modal_decline_button);
                final MaterialCardView cancel = OvertimeDetailsDialog.findViewById(R.id.modal_cancel_button);

                modal_employee_name.setText(name);
                modal_reason.setText(reason);
                modal_overtime_status.setText(status);
                modal_overtime_date.setText(helper.convertToReadableDate(date));
                modal_overtime_time_start.setText(helper.convertToReadableTime(start_time));
                modal_overtime_time_end.setText(helper.convertToReadableTime(end_time));
                modal_overtime_hours_rendered.setText(rendered_hours);


                if (status.equals("pending")) {

                    modal_decline_reason_parent.setVisibility(View.GONE);
                    approve.setVisibility(View.VISIBLE);
                    decline.setVisibility(View.VISIBLE);

                    approve.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            updateOvertime(position, String.valueOf(overtime_id), employee_id, "approved", "");
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

                                    updateOvertime(position, String.valueOf(overtime_id), employee_id, "declined", modal_edittext_decline_reason.getText().toString());
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

                ImageButton close = OvertimeDetailsDialog.findViewById(R.id.img_dialog_close);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        modal_decline_reason_parent.setVisibility(GONE);
                        OvertimeDetailsDialog.dismiss();
                    }
                });

                OvertimeDetailsDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void resetAdapter() {
        mDataset.clear();
        filteredList.clear();
        mDataset.addAll(tempList);
    }

    public Boolean filterByStatus(String status) {
        resetAdapter();

        for (Overtime overtime : mDataset) {

            if (overtime.getStatus().equals(status)) {
                filteredList.add(overtime);
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

    public void updateOvertime(final Integer i_dataSet_position, final String i_overtime_id, final String i_user_id, final String i_status, final String i_decline_reason) {

        final URLs url = new URLs();
        final User user = SharedPrefManager.getInstance(ctx).getUser();
        final String DATABASE = user.getC1();
        final String TABLE = user.getC2();

        String url_approve_overtime = url.url_approve_overtime(user.getUser_id());

        StringRequest overtimeUpdateRequest = new StringRequest(Request.Method.POST, url_approve_overtime, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject response_obj = new JSONObject(response);
                    String status = response_obj.getString("status");

                    Toasty.success(ctx, status, Toast.LENGTH_SHORT).show();

                    if (status.equals("success")) {
                        updateOvertimeStatus(i_dataSet_position, i_status);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toasty.error(ctx, "Server Error", Toast.LENGTH_SHORT).show();
                }

                OvertimeDetailsDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                return helper.headers();
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", i_overtime_id);
                params.put("checked_by", user.getEmail());
                params.put("status", i_status);
                params.put("user_id", i_user_id);
                params.put("company_id", user.getCompany_ID());
                params.put("decline_reason", i_decline_reason);
                params.put("api_token", user.getApi_token());
                params.put("link", user.getLink());

                return params;
            }
        };

        overtimeUpdateRequest.setRetryPolicy(new DefaultRetryPolicy(60 * 1000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Helper.getInstance(ctx).addToRequestQueue(overtimeUpdateRequest);
    }

    public void updateOvertimeStatus(Integer i_position, String i_status) {
        Overtime overtime = mDataset.get(i_position);
        overtime.setStatus(i_status);
        notifyItemChanged(i_position);
        notifyDataSetChanged();
    }

}