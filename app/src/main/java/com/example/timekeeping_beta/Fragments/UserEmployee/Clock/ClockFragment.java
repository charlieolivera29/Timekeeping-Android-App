package com.example.timekeeping_beta.Fragments.UserEmployee.Clock;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.timekeeping_beta.R;


public class ClockFragment extends Fragment {

    private EDTR UserEDTR;
    private TextView tv_time_in, tv_time_out;
    private Button numpad_1, numpad_2, numpad_3, numpad_4, numpad_5, numpad_6, numpad_7, numpad_8, numpad_9, numpad_0;
    private LinearLayout numpad_clear, numpad_send;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_clock, container, false);

        initViews(v);
        setListeners();

        return v;
    }

    private void initViews(View v) {

        tv_time_in = v.findViewById(R.id.tv_time_in);
        tv_time_out = v.findViewById(R.id.tv_time_out);

        numpad_0 = v.findViewById(R.id.numpad_0);
        numpad_1 = v.findViewById(R.id.numpad_1);
        numpad_2 = v.findViewById(R.id.numpad_2);
        numpad_3 = v.findViewById(R.id.numpad_3);
        numpad_4 = v.findViewById(R.id.numpad_4);
        numpad_5 = v.findViewById(R.id.numpad_5);
        numpad_6 = v.findViewById(R.id.numpad_6);
        numpad_7 = v.findViewById(R.id.numpad_7);
        numpad_8 = v.findViewById(R.id.numpad_8);
        numpad_9 = v.findViewById(R.id.numpad_9);
        numpad_clear = v.findViewById(R.id.numpad_clear);
        numpad_send = v.findViewById(R.id.numpad_send);
    }

    private void setListeners () {

        ClockViewModel clockViewModel = ViewModelProviders.of(this).get(ClockViewModel.class);

        clockViewModel.retrieveUserTimeViaCheckTimedIn();

        clockViewModel.getUserEDTR().observe(this, new Observer<EDTR>() {
            @Override
            public void onChanged(@Nullable EDTR edtr) {
                if (edtr != null){
                    UserEDTR = edtr;
                    tv_time_in.setText(edtr.getTime_in());
                    tv_time_out.setText(edtr.getTime_out());
                }
            }
        });
    }
}
