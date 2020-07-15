package com.example.timekeeping_beta.Fragments.DEPRECATED.DEPRECATED_UserEmployee.RequestLeave.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.timekeeping_beta.R;

import java.text.DateFormat;
import java.util.Calendar;


public class DateEndPickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    String date;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

        String s_month = (month + 1) < 10 ? "0" + (month + 1) : String.valueOf(month + 1);
        String s_day = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);

        date = year + "-" + s_month + "-" + s_day;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance().format(calendar.getTime());

        Log.d("Date", currentDateString);
        TextView txtViewCalendar = getActivity().findViewById(R.id.txt_leave_date_end);
        txtViewCalendar.setText(currentDateString);

    }

    public String getEndDate() {
        return date;
    }
}
