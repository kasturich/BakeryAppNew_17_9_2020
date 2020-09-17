package com.mi5.bakeryappnew.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mi5.bakeryappnew.R;
import com.mi5.bakeryappnew.activity.NavigationDrawerActivity;
import com.mi5.bakeryappnew.adapter.SellReportItemAdapter;
import com.mi5.bakeryappnew.model.SellReportDetails;
import com.mi5.bakeryappnew.model.SellReportItemDetails;
import com.mi5.bakeryappnew.other.DateDialogFragment;
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
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewTodaySellFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewTodaySellFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView txtSellDate, txtDateTitle, txtSearchButton;
    TextView txtTotalAmount, txtTotalCashAmount, txtTotalCardAmount,
            txtDailyBillCount, txtTotalOnlinePaymentAmount;
    ListView sellReportListView;
    ProgressBar reportProgressBar;
    Bundle bundle = new Bundle();

    boolean serverConnection=false;
    SharedPreferences sharedpreferences;
    boolean hasLoggedIn, downloadData;

    Double totalAmount = 0.0, totalCashAmount =0.0,
            totalCardAmount =0.0, totalOnlinePayment=0.0;

    String companyId, outletId, jsonSharedPreference, strSellDate="", strBundleSellDate="";
    SoapObject responseTotalAmount, responseBillDetails;
    String colored = "*";
    SpannableStringBuilder builderDate;

    SellReportItemDetails sellReportItemDetails;
    List<SellReportItemDetails> sellReportItemDetailsList;
    SellReportItemAdapter sellReportItemAdapter;
    SellReportDetails sellReportDetails;
    List<SellReportDetails> sellReportDetailsList;

    private SchemeDetailsFragment.OnFragmentInteractionListener mListener;

    public ViewTodaySellFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewTodaySellFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewTodaySellFragment newInstance(String param1, String param2) {
        ViewTodaySellFragment fragment = new ViewTodaySellFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_today_sell, container, false);

        ((NavigationDrawerActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getString(R.string.sell_title));

        txtSellDate = view.findViewById(R.id.txtSellDate);
        txtDateTitle = view.findViewById(R.id.txtDateTitle);
        txtSearchButton = view.findViewById(R.id.txtSearchButton);
        sellReportListView = view.findViewById(R.id.sellReportListView);
        reportProgressBar = view.findViewById(R.id.reportProgressBar);
        txtDailyBillCount = view.findViewById(R.id.txtDailyBillCount);
        txtTotalAmount = view.findViewById(R.id.txtTotalAmount);
        txtTotalCashAmount = view.findViewById(R.id.txtTotalCashAmount);
        txtTotalCardAmount = view.findViewById(R.id.txtTotalCardAmount);
        txtTotalOnlinePaymentAmount = view.findViewById(R.id.txtTotalOnlinePaymentAmount);

        txtDailyBillCount.setText("0");
        txtTotalAmount.setText("0.0");
        txtTotalCashAmount.setText("0.0");
        txtTotalCardAmount.setText("0.0");
        txtTotalOnlinePaymentAmount.setText("0.0");

        sharedpreferences = getActivity().getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);

        hasLoggedIn = sharedpreferences.getBoolean("hasLoggedIn", false);
        downloadData = sharedpreferences.getBoolean("downloadData", false);

        jsonSharedPreference = sharedpreferences.getString("JSON", "");

        System.out.println("downloadData" + downloadData);

        if (jsonSharedPreference != null) {
            try {
                JSONArray jarray = new JSONArray(jsonSharedPreference);
                System.out.println("jarray length " + jarray.length());
                JSONObject json_obj = jarray.getJSONObject(0);
                for (int i=0; i<jarray.length(); i++)
                {
                    //System.out.println("personal info user_id " + user_id);
                    companyId = json_obj.getString("CompanyId");
                    outletId = json_obj.getString("OutletId");
                    System.out.println("company id " + companyId);
                }

            } catch (JSONException e) {

                System.out.println("sharedpreference error = " + e.getMessage());
            }
        }

        sellReportItemDetailsList = new ArrayList<SellReportItemDetails>();
        sellReportDetailsList = new ArrayList<SellReportDetails>();

        builderDate = new SpannableStringBuilder();

        builderDate.append(getResources().getString(R.string.sell_date));
        int start = builderDate.length();
        builderDate.append(colored);
        int end = builderDate.length();
        builderDate.setSpan(new ForegroundColorSpan(Color.RED), start, end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        txtDateTitle.setText(builderDate);

        txtSellDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DateDialogFragment dateDialogFragment = new DateDialogFragment();
                dateDialogFragment.show(getActivity().getFragmentManager(), "Date Picker");
            }
        });

        txtSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            try
            {
                if(txtSellDate.getText().toString().length() == 0)
                {
                    txtSellDate.setError("Select Date");
                    txtSellDate.requestFocus();
                }
                else
                {
                    sellReportItemDetailsList.clear();
                    sellReportDetailsList.clear();

                    String dArray[] = txtSellDate.getText().toString().split("/");
                    int d = Integer.parseInt(dArray[0]);
                    if( d < 10)
                    {
                        String a = "0"+d;
                        strSellDate = a + "/" + dArray[1] + "/" + dArray[2];
                    }
                    else
                    {
                        strSellDate = txtSellDate.getText().toString();
                    }

                    if(strBundleSellDate.length()>0)
                    {
                        bundle.clear();
                    }

                    //to show total amount in cash, card and online payment
                    DownloadTotalSellReportAsync downloadTotalSellReportAsync
                            = new DownloadTotalSellReportAsync(strSellDate);
                    downloadTotalSellReportAsync.execute();

                    //to show the bill details
                    DownloadSellReportAsync downloadSellReportAsync
                            = new DownloadSellReportAsync(strSellDate);
                    downloadSellReportAsync.execute();

                }
            }
            catch (Exception e)
            {
                e.getMessage();
            }
            }
        });

        sellReportListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String orderId = sellReportItemDetailsList.get(position).getOrderId();

                Bundle bundle = new Bundle();
                bundle.putString("orderId", orderId);

                System.out.println("strSellDate " + strSellDate);
                System.out.println("strBundleSellDate " + strBundleSellDate);

                if(strSellDate.length() != 0)
                {
                    bundle.remove("sellReportDate");
                    strBundleSellDate = "";
                }

                if (strBundleSellDate.length()>0)
                {
                    bundle.putString("sellReportDate", strBundleSellDate);
                }
                else
                {
                    bundle.putString("sellReportDate", strSellDate);
                }

                Fragment fragment = new ViewOrderDetailsFragment();
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .addToBackStack("orderDetails")
                        .commit();
                if (fragment != null) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                    fragmentTransaction.addToBackStack("orderDetails");
                    fragmentTransaction.commit();
                }
            }
        });

        txtSellDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(strBundleSellDate.length()>0)
                {
                    bundle.clear();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                if(strBundleSellDate.length()>0)
                {
                    bundle.clear();
                }
            }
        });

        bundle = getArguments();
        if(bundle != null)
        {
            strBundleSellDate = bundle.getString("sellReportDate", strSellDate);
        }

        try {
            if(strBundleSellDate.length() > 0)
            {
                txtSellDate.setText(strBundleSellDate);
                sellReportItemDetailsList.clear();
                DownloadSellReportAsync downloadSellReportAsync
                        = new DownloadSellReportAsync(strBundleSellDate);
                downloadSellReportAsync.execute();
                DownloadTotalSellReportAsync downloadTotalSellReportAsync
                        = new DownloadTotalSellReportAsync(strBundleSellDate);
                downloadTotalSellReportAsync.execute();
            }
        }
        catch (Exception e)
        {
            e.getMessage();
        }

        return view;
    }

    //to show the details of bill with amount and payment type
    class DownloadSellReportAsync extends AsyncTask<String, Void, String>
    {
        String selectedDate;

        public DownloadSellReportAsync (String selectedDate)
        {
            this.selectedDate = selectedDate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            reportProgressBar.setVisibility(View.VISIBLE);
            reportProgressBar.setProgress(5);
        }

        @Override
        protected String doInBackground(String... strings) {
            downloadSellReport(selectedDate);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                if(serverConnection)
                {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Server or Internet is down. Please try after some time!", Toast.LENGTH_SHORT).show();
                }
                else {
                    reportProgressBar.setVisibility(View.GONE);
                    serverConnection = false;
                    String strUnitResponse = responseBillDetails.getProperty("Sales_Details_NewResult").toString();

                    JSONArray jarray = new JSONArray(strUnitResponse);

                    for (int i=0; i<jarray.length(); i++)
                    {
                        sellReportItemDetails = new SellReportItemDetails(
                                jarray.getJSONObject(i).getString("OutletId"),
                                jarray.getJSONObject(i).getString("OrderId"),
                                jarray.getJSONObject(i).getString("FinalBillAmount"),
                                jarray.getJSONObject(i).getString("msg"),
                                jarray.getJSONObject(i).getString("PaymentType"));
                        sellReportItemDetailsList.add(sellReportItemDetails);
                    }

                    System.out.println("sell list size = "
                            + sellReportItemDetailsList.size());

                    if(strBundleSellDate.length()>0)
                    {
                        bundle.clear();
                    }

                    sellReportItemAdapter = new SellReportItemAdapter(getActivity().getApplicationContext(),
                            sellReportItemDetailsList);
                    sellReportItemAdapter.notifyDataSetChanged();
                    sellReportListView.setAdapter(sellReportItemAdapter);
                }
            }
            catch (Exception e)
            {
                e.getMessage();
            }
        }
    }
    public void downloadSellReport(String selectedDate)
    {
        String METHOD_NAME = "Sales_Details_New";
        String SOAP_ACTION = UrlStrings.NAMESPACE+"Sales_Details_New";

        System.out.println("Soap action = " + SOAP_ACTION);
        System.out.println("url = " + UrlStrings.URL);

        SoapObject request = new SoapObject(UrlStrings.NAMESPACE, METHOD_NAME);
        // Property which holds input parameters
        PropertyInfo companyIdPI = new PropertyInfo();
        PropertyInfo outletIdPI = new PropertyInfo();
        PropertyInfo datePI = new PropertyInfo();

        companyIdPI.setName("CompanyId");
        outletIdPI.setName("OutletId");
        datePI.setName("Date");

        companyIdPI.setValue(companyId);
        outletIdPI.setValue(outletId);
        datePI.setValue(selectedDate);

        companyIdPI.setType(String.class);
        outletIdPI.setType(String.class);
        datePI.setType(String.class);

        request.addProperty(companyIdPI);
        request.addProperty(outletIdPI);
        request.addProperty(datePI);

        System.out.println("request - " + companyIdPI + " = " + outletIdPI + " = ");

        System.out.println("requested parameters : " +request.toString());
        // Create envelope
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope( SoapEnvelope.VER11);
        envelope.dotNet = true;
        // Set output SOAP object
        envelope.setOutputSoapObject(request);
        // Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(UrlStrings.URL, 50000);

        try {
            // Invoke web service
            androidHttpTransport.call(SOAP_ACTION, envelope);
            // Get the response

            if (envelope.bodyIn instanceof SoapFault)
            {
                String str= ((SoapFault) envelope.bodyIn).faultstring;
                Log.i("Soapfault", str);
                // response = response.getProperty("LoginDetailsResult").toString();
                //Log.d("response property" , response.getProperty("LoginDetailsResult").toString());
            }
            else {
                //SoapObject result = (SoapObject)envelope.getResponse();
                //SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;
                serverConnection = false;
                responseBillDetails = (SoapObject) envelope.bodyIn;
                //Log.d("response property" , response.getProperty("LoginResult").toString());
                Log.d("response property" , responseBillDetails.getProperty("Sales_Details_NewResult").toString());
                Log.d("WS", responseBillDetails.toString());
            }
        }
        catch (SoapFault e)
        {
            System.out.println("in catch = " + e.getMessage());
        }
        catch (Exception e) {
            //Assign Error Status true in static variable 'errored'
            System.out.println("connection error checking");
            serverConnection = true;
            e.printStackTrace();
        }
    }

    //to show daily total amount for cash, card and online payment
    class DownloadTotalSellReportAsync extends AsyncTask<String, Void, String>
    {
        String selectedDate;

        public DownloadTotalSellReportAsync (String selectedDate)
        {
            this.selectedDate = selectedDate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            reportProgressBar.setVisibility(View.VISIBLE);
            reportProgressBar.setProgress(5);
        }

        @Override
        protected String doInBackground(String... strings) {
            downloadTotalSellReport(selectedDate);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                if(serverConnection)
                {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Server or Internet is down. Please try after some time!", Toast.LENGTH_SHORT).show();
                }
                else {
                    serverConnection = false;
                    reportProgressBar.setVisibility(View.GONE);
                    String strUnitResponse = responseTotalAmount.getProperty("DateWiseSales_DetailsResult").toString();

                    JSONArray jarray = new JSONArray(strUnitResponse);

                    for (int i=0; i<jarray.length(); i++)
                    {
                        sellReportDetails = new SellReportDetails(
                                jarray.getJSONObject(i).getString("OutletId"),
                                jarray.getJSONObject(i).getString("DailyBillAmount"),
                                jarray.getJSONObject(i).getString("FinalBillAmount"),
                                jarray.getJSONObject(i).getString("TotalDailyDiscount"),
                                jarray.getJSONObject(i).getString("TotalOnlinePayment"),
                                jarray.getJSONObject(i).getString("TotalCashPayment"),
                                jarray.getJSONObject(i).getString("TotalCardPayment"),
                                jarray.getJSONObject(i).getString("DailyBillCount"),
                                jarray.getJSONObject(i).getString("msg"),
                                "");
                        sellReportDetailsList.add(sellReportDetails);
                    }

                    System.out.println("sell list size = "
                            + sellReportDetailsList.size());

                    for(int j=0; j<sellReportDetailsList.size(); j++)
                    {
                        if (sellReportDetailsList.get(j).getMsg().equals("1")) {

                            txtDailyBillCount.setText(sellReportDetailsList.get(j).getDailyBillCount());

                            txtTotalAmount.setText(sellReportDetailsList.get(j).getFinalBillAmount()
                                    + " " + getResources().getString(R.string.rs));

                            txtTotalCashAmount.setText(sellReportDetailsList.get(j).getTotalCashPayment()
                                    + " " + getResources().getString(R.string.rs));

                            txtTotalCardAmount.setText(sellReportDetailsList.get(j).getTotalCardPayment()
                                    + " " + getResources().getString(R.string.rs));

                            txtTotalOnlinePaymentAmount.setText(sellReportDetailsList.get(j).getTotalOnlinePayment()
                                    + " " + getResources().getString(R.string.rs));

                        }
                        else
                        {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.getMessage();
            }
        }
    }
    public void downloadTotalSellReport(String selectedDate)
    {
        String METHOD_NAME = "DateWiseSales_Details";
        String SOAP_ACTION = UrlStrings.NAMESPACE+"DateWiseSales_Details";

        System.out.println("Soap action = " + SOAP_ACTION);
        System.out.println("url = " + UrlStrings.URL);

        SoapObject request = new SoapObject(UrlStrings.NAMESPACE, METHOD_NAME);
        // Property which holds input parameters
        PropertyInfo companyIdPI = new PropertyInfo();
        PropertyInfo outletIdPI = new PropertyInfo();
        PropertyInfo datePI = new PropertyInfo();

        companyIdPI.setName("CompanyId");
        outletIdPI.setName("OutletId");
        datePI.setName("Date");

        companyIdPI.setValue(companyId);
        outletIdPI.setValue(outletId);
        datePI.setValue(selectedDate);

        companyIdPI.setType(String.class);
        outletIdPI.setType(String.class);
        datePI.setType(String.class);

        request.addProperty(companyIdPI);
        request.addProperty(outletIdPI);
        request.addProperty(datePI);

        System.out.println("request - " + companyIdPI + " = " + outletIdPI + " = ");

        System.out.println("requested parameters : " +request.toString());
        // Create envelope
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope( SoapEnvelope.VER11);
        envelope.dotNet = true;
        // Set output SOAP object
        envelope.setOutputSoapObject(request);
        // Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(UrlStrings.URL, 50000);

        try {
            // Invoke web service
            androidHttpTransport.call(SOAP_ACTION, envelope);
            // Get the response

            if (envelope.bodyIn instanceof SoapFault)
            {
                String str= ((SoapFault) envelope.bodyIn).faultstring;
                Log.i("Soapfault", str);
                // response = response.getProperty("LoginDetailsResult").toString();
                //Log.d("response property" , response.getProperty("LoginDetailsResult").toString());
            }
            else {
                //SoapObject result = (SoapObject)envelope.getResponse();
                //SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;
                serverConnection = false;
                responseTotalAmount = (SoapObject) envelope.bodyIn;
                //Log.d("response property" , response.getProperty("LoginResult").toString());
                Log.d("response property" , responseTotalAmount.getProperty("DateWiseSales_DetailsResult").toString());
                Log.d("WS", responseTotalAmount.toString());
            }
        }
        catch (SoapFault e)
        {
            System.out.println("in catch = " + e.getMessage());
        }
        catch (Exception e) {
            //Assign Error Status true in static variable 'errored'
            System.out.println("connection error checking");
            serverConnection = true;
            e.printStackTrace();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SchemeDetailsFragment.OnFragmentInteractionListener) {
            mListener = (SchemeDetailsFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}