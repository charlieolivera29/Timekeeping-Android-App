package com.example.timekeeping_beta.Fragments.HRAdmin.Dashboard;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.timekeeping_beta.Fragments.UserApprover.Approvee.Models.Approvee;
import com.example.timekeeping_beta.Globals.CustomClasses.Flag;
import com.example.timekeeping_beta.Globals.CustomClasses.Interface.RecyclerViewClickListener;
import com.example.timekeeping_beta.Globals.StaticData.URLs;
import com.example.timekeeping_beta.Globals.SharedPrefManager;
import com.example.timekeeping_beta.R;

import java.util.ArrayList;

public class HRDashboardAdapter extends RecyclerView.Adapter<HRDashboardAdapter.MyViewHolder> {

    private ArrayList<Approvee> mDataset;
    private Context ctx;
    private RecyclerViewClickListener ApproveeClickListener;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public RecyclerViewClickListener ApproveeClickListener;
        public TextView name;
        public ImageView approvee_image;

        public MyViewHolder(LinearLayout v, RecyclerViewClickListener onItemClickListener) {
            super(v);
            name = v.findViewById(R.id.rvi_employee_name);
            approvee_image = v.findViewById(R.id.rvi_approvee_image);

            this.ApproveeClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            ApproveeClickListener.onItemClick(getAdapterPosition(), Flag.SHOW);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public HRDashboardAdapter(ArrayList<Approvee> myDataset, Context context, RecyclerViewClickListener approveeClickListener) {
        this.ctx = context;
        this.mDataset = myDataset;
        this.ApproveeClickListener = approveeClickListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_details_hrapprovee, parent, false);
        MyViewHolder vh = new MyViewHolder(v, ApproveeClickListener);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Approvee a = mDataset.get(position);
        final String id = a.getApproveeId();
        String fname = a.getFirst_name();
        String lname = a.getLast_name();
        String fullname = fname + " " + lname;
        String email = a.getEmail();
        String contact = a.getCell_Number();
        String location = a.getLocation();
        String rolename = a.getRole_name();

        holder.name.setText(fullname);
        holder.approvee_image.setVisibility(View.VISIBLE);

        Glide.with(ctx.getApplicationContext())
                .load(URLs.url_image(SharedPrefManager.getInstance(ctx).getUser().getCompany(), a.getImage_file_name()))
                .error(R.drawable.ic_person_black_24dp)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.approvee_image);


//        holder.approvee_details.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                Bundle bundle = new Bundle();
//                bundle.putString("user_id", id);
//
//                Fragment ApproveeDetailFragment = new ApproveeDetailFragment();
//                //Fragment ApproveeDetailFragment = new ApproveeDetailSliderFragment();
//                ApproveeDetailFragment.setArguments(bundle);
//
//                ((FragmentActivity) v.getContext())
//                        .getSupportFragmentManager()
//                        .beginTransaction()
//                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
//                        .add(R.id.fragment_container, ApproveeDetailFragment,ApproveeDetailFragment.getClass().getName())
//                        .addToBackStack(ApproveeDetailFragment.getClass().getName())
//                        .commit();
//            }
//        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
