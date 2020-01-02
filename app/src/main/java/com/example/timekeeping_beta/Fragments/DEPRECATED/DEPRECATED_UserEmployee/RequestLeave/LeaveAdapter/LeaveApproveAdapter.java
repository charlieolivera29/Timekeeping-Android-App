package com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestLeave.LeaveAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.timekeeping_beta.R;
import com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestLeave.LeaveRequestItem;

import java.util.List;

public class LeaveApproveAdapter extends RecyclerView.Adapter<LeaveApproveAdapter.LeaveRequestViewHolder> {

    private Context ctx;
    private OnItemClickListener onItemClickListener;
    private List<LeaveRequestItem> leaveRequestItemList;

    public LeaveApproveAdapter(List<LeaveRequestItem> leaveRequestItemList, OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        this.leaveRequestItemList = leaveRequestItemList;
    }

    @NonNull
    @Override
    public LeaveRequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_request_leave, viewGroup, false);
        ctx = view.getContext();
        return new LeaveRequestViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaveRequestViewHolder leaveRequestViewHolder, int i) {
        final LeaveRequestItem currentItem = leaveRequestItemList.get(i);
        leaveRequestViewHolder.day_type.setText(currentItem.getDay_type());
        leaveRequestViewHolder.date_start.setText(currentItem.getDate_start());
        leaveRequestViewHolder.date_end.setText(currentItem.getDate_end());
        leaveRequestViewHolder.time_in.setText(currentItem.getTime_start());
        leaveRequestViewHolder.time_out.setText(currentItem.getTime_end());

        leaveRequestViewHolder.rvi_status.setBackgroundColor(ContextCompat.getColor(ctx, R.color.colorSuccess));
    }

    @Override
    public int getItemCount() {
        return leaveRequestItemList.size();
    }

    public static class LeaveRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public OnItemClickListener onItemClickListener;
        private TextView day_type;
        private TextView date_start;
        private TextView date_end;
        private TextView time_in;
        private TextView time_out;
        private FrameLayout rvi_status;

        public LeaveRequestViewHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            day_type = itemView.findViewById(R.id.leave_request_day_type);
            date_start = itemView.findViewById(R.id.leave_request_date_start);
            date_end = itemView.findViewById(R.id.leave_request_date_end);
            time_in = itemView.findViewById(R.id.leave_request_time_in);
            time_out = itemView.findViewById(R.id.leave_request_time_out);
            rvi_status = itemView.findViewById(R.id.rvi_status);

            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
