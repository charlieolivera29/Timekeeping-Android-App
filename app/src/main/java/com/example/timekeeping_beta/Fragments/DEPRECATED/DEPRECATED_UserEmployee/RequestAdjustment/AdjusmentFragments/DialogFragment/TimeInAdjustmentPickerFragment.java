package com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestAdjustment.AdjusmentFragments.DialogFragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.timekeeping_beta.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeInAdjustmentPickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private String converted_time;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    // Time picker
    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {

        TextView txtTimeIn = getActivity().findViewById(R.id.txt_adjusted_time_in);

        String clockString = hourOfDay + ":" + minute;
        SimpleDateFormat _24Hour = new SimpleDateFormat("HH:mm");
        SimpleDateFormat _12Hour = new SimpleDateFormat("hh:mm a");

        try {
            Date _24HourDt = _24Hour.parse(clockString);
            String api_24Hour = _24Hour.format(_24HourDt);
            converted_time = _12Hour.format(_24HourDt);
            Log.d("@12Hr_format", converted_time);
            Log.d("@24Hr_format", api_24Hour);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        txtTimeIn.setText(converted_time);
    }
}
