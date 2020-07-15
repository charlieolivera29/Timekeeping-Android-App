package com.example.timekeeping_beta.Fragments.HRAdmin.Employees;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.Globals.Models.User;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.R;

import java.util.ArrayList;
import java.util.List;

public class EmployeesAdapter extends RecyclerView.Adapter<EmployeesAdapter.MyViewHolder> {

    private List<Employee> mDataset;
    private List<Employee> tempList;
    private List<Employee> filteredList;

    private Context ctx;

    private Helper helper;
    private URLs url;
    private User user;

    private EmployeesAdapter.OnItemClickListener onItemClickListener;

    public EmployeesAdapter(List<Employee> myDataset, EmployeesAdapter.OnItemClickListener onItemClickListener) {

        this.mDataset = new ArrayList<>();
        this.tempList = new ArrayList<>();
        this.filteredList = new ArrayList<>();

        this.mDataset.addAll(myDataset);
        this.tempList.addAll(myDataset);

        this.onItemClickListener = onItemClickListener;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private OnItemClickListener onItemClickListener;

        private CardView rvi_approvee_details;
        private ImageButton rvi_edit;
        private TextView rvi_employee_name;
        private TextView rvi_email;
        private TextView rvi_role;
        private ConstraintLayout cv_inactive_badge;
        private LinearLayout ll_status_layout;

        public MyViewHolder(@NonNull View v, EmployeesAdapter.OnItemClickListener onItemClickListener) {
            super(v);
            rvi_approvee_details = v.findViewById(R.id.rvi_approvee_details);
            rvi_edit = v.findViewById(R.id.rvi_edit);
            rvi_employee_name = v.findViewById(R.id.rvi_employee_name);
            rvi_email = v.findViewById(R.id.rvi_email);
            rvi_role = v.findViewById(R.id.rvi_role);
            cv_inactive_badge = v.findViewById(R.id.cv_inactive_badge);
            ll_status_layout = v.findViewById(R.id.ll_status_layout);

            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        ctx = parent.getContext();
        helper = Helper.getInstance(ctx);
        url = new URLs();
        user = SharedPrefManager.getInstance(ctx).getUser();

        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(ctx)
                .inflate(R.layout.recyclerview_details_approvee, parent, false);

        final MyViewHolder vh = new MyViewHolder(v, onItemClickListener);

        return vh;
    }

    @Override
    public void onBindViewHolder(final EmployeesAdapter.MyViewHolder holder, final int position) {

        final Employee e = mDataset.get(position);

        holder.rvi_employee_name.setText(e.getFname() + " " + e.getLname());
        holder.rvi_email.setText(e.getEmail());
        holder.rvi_role.setText(e.getRole_name());

        if (e.isActive() == 0) {
            holder.cv_inactive_badge.setVisibility(View.VISIBLE);
        } else {
            holder.cv_inactive_badge.setVisibility(View.GONE);
        }

    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public List<Employee> filterByName(String filter) {

        filteredList.clear();
        mDataset.clear();

        for (Employee e : tempList) {
            if ((e.getFname() + " " + e.getLname()).contains(filter)) {
                filteredList.add(e);
            }
        }

        if (filteredList.size() > 0) {
            mDataset.addAll(filteredList);
        } else {
            mDataset.addAll(tempList);
        }

        notifyDataSetChanged();

        return filteredList;
    }
}
