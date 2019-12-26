package com.example.timekeeping_beta.Fragments.Retry;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.timekeeping_beta.Globals.Helper;
import com.example.timekeeping_beta.R;

import es.dmoral.toasty.Toasty;

public class TryAgainFromNestedFrament extends Fragment{

    private Context ctx;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.fragment_try_again, container, false);
        ctx = v.getContext();

        SwipeRefreshLayout retry = v.findViewById(R.id.retry);
        TextView link_try_again = v.findViewById(R.id.link_try_again);

        retry.setRefreshing(false);

        Bundle arguments = getArguments();
        final Integer return_to = arguments.getInt("RETURN_TO");
        final Integer container_to = arguments.getInt("CONTAINER");

        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        retry.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                if (fragmentManager != null) {

                    fragmentManager
                            .beginTransaction()
                            .replace(container_to, Helper.getInstance(ctx).getFragment(return_to))
                            .commit();

                } else {
                    Toasty.error(v.getContext(), "Fragment manager is empty.", Toasty.LENGTH_LONG).show();
                }
            }
        });


        link_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fragmentManager != null) {

                    fragmentManager
                            .beginTransaction()
                            .replace(container_to, Helper.getInstance(ctx).getFragment(return_to))
                            .commit();

                } else {
                    Toasty.error(v.getContext(), "Fragment manager is empty.", Toasty.LENGTH_LONG).show();
                }
            }
        });

        return v;
    }
}