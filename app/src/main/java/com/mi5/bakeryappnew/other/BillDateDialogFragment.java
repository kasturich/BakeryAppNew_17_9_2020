package com.mi5.bakeryappnew.other;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;

import com.mi5.bakeryappnew.R;

import java.util.Calendar;

/**
 * Created by User on 20-08-2018.
 */

public class BillDateDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    TextView txtDate;
    DatePickerDialog datepickerdialog;
    Calendar calendarStartDate;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        calendarStartDate = Calendar.getInstance();

        int year = calendarStartDate.get(Calendar.YEAR);
        int month = calendarStartDate.get(Calendar.MONTH);
        int day = calendarStartDate.get(Calendar.DAY_OF_MONTH);

        datepickerdialog = new DatePickerDialog(getActivity(),
                R.style.datepicker,this,year,month,day);

        datepickerdialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);

        return datepickerdialog;
    }
    String datePattern, dayPattern, monthPattern;
    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        txtDate = (TextView) getActivity().findViewById(R.id.txtDate);

        if(day <10)
        {
            dayPattern = "0" + day;
            //datePattern = day + "/0" + (month + 1) + "/" + year;
        }
        else
        {
            dayPattern = String.valueOf(day);
            //datePattern = day + "/" + (month + 1) + "/" + year;
        }

        if((month + 1)<10)
        {
            monthPattern = "0" + (month + 1);
            //datePattern = day + "/0" + (month + 1) + "/" + year;
        }
        else
        {
            monthPattern = String.valueOf(month + 1);
            //datePattern = day + "/" + (month + 1) + "/" + year;
        }

        datePattern = dayPattern + "/" + monthPattern + "/" + year;
        //}
        txtDate.setText(datePattern);
        txtDate.setError(null);
    }
}