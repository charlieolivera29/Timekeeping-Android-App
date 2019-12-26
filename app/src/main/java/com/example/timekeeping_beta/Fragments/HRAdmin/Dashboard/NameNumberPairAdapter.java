package com.example.timekeeping_beta.Fragments.HRAdmin.Dashboard;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.timekeeping_beta.Fragments.HRAdmin.Dashboard.Models.BundeeEmployee;
import com.example.timekeeping_beta.Fragments.HRAdmin.Dashboard.Models.NameNumberPair;
import com.example.timekeeping_beta.Globals.CustomClasses.Flag;
import com.example.timekeeping_beta.Globals.CustomClasses.Interface.RecyclerViewClickListener;
import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.R;

import java.util.ArrayList;

public class NameNumberPairAdapter extends RecyclerView.Adapter<NameNumberPairAdapter.MyViewHolder> {

    private ArrayList<NameNumberPair> mDataset;
    private Context ctx;
    private RecyclerViewClickListener ApproveeClickListener;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public RecyclerViewClickListener ApproveeClickListener;
        public TextView tv_employee_name, tv_number;

        public MyViewHolder(View v, RecyclerViewClickListener onItemClickListener) {
            super(v);
            tv_employee_name = v.findViewById(R.id.tv_employee_name);
            tv_number = v.findViewById(R.id.tv_number);

            this.ApproveeClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            ApproveeClickListener.onItemClick(getAdapterPosition(), Flag.SHOW);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public NameNumberPairAdapter(ArrayList<NameNumberPair> myDataset, Context context, RecyclerViewClickListener approveeClickListener) {
        this.ctx = context;
        this.mDataset = myDataset;
        this.ApproveeClickListener = approveeClickListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NameNumberPairAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_name_number_pair_employee, parent, false);
        NameNumberPairAdapter.MyViewHolder vh = new NameNumberPairAdapter.MyViewHolder(v, ApproveeClickListener);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final NameNumberPairAdapter.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        NameNumberPair nameNumberPair = mDataset.get(position);

        Helper helper = Helper.getInstance(ctx);

        holder.tv_employee_name.setText(nameNumberPair.getFname());
        holder.tv_number.setText(String.valueOf(nameNumberPair.getF()));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}