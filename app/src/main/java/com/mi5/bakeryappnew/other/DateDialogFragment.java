package com.mi5.bakeryappnew.other;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.mi5.bakeryappnew.R;
import com.mi5.bakeryappnew.utility.UrlStrings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by User on 20-08-2018.
 */

public class DateDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

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

        //datepickerdialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        return datepickerdialog;
    }
    String datePattern;
    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        txtDate = (TextView) getActivity().findViewById(R.id.txtSellDate);

        if((month + 1)<10)
        {
            datePattern = day + "/0" + (month + 1) + "/" + year;
        }
        else
        {
            datePattern = day + "/" + (month + 1) + "/" + year;
        }
        //}
        txtDate.setText(datePattern);
        txtDate.setError(null);
    }
}