package com.mi5.bakeryappnew.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mi5.bakeryappnew.R;
import com.mi5.bakeryappnew.activity.NavigationDrawerActivity;
import com.mi5.bakeryappnew.adapter.OrderItemDetailsAdapter;
import com.mi5.bakeryappnew.adapter.SellReportItemAdapter;
import com.mi5.bakeryappnew.model.AddItemsForOrder;
import com.mi5.bakeryappnew.model.OrderItemDetails;
import com.mi5.bakeryappnew.model.SellReportItemDetails;
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
 * Use the {@link ViewOrderDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewOrderDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ListView sellReportDetailsListView;
    ProgressBar reportProgressBar;
    ImageView imgBack;

    OrderItemDetails orderItemDetails;
    List<OrderItemDetails> orderItemDetailsList;
    OrderItemDetailsAdapter orderItemDetailsAdapter;

    boolean serverConnection=false;
    SharedPreferences sharedpreferences;
    boolean hasLoggedIn, downloadData;

    String companyId, outletId, jsonSharedPreference, orderId, sellReportDate;
    SoapObject response;
    Bundle bundle = new Bundle();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ViewOrderDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewOrderDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewOrderDetailsFragment newInstance(String param1, String param2) {
        ViewOrderDetailsFragment fragment = new ViewOrderDetailsFragment();
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
        View view = inflater.inflate(R.layout.fragment_view_order_details, container, false);

        ((NavigationDrawerActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getString(R.string.sell_title));

        sellReportDetailsListView = view.findViewById(R.id.sellReportDetailsListView);
        reportProgressBar = view.findViewById(R.id.reportProgressBar);
        imgBack = view.findViewById(R.id.imgBack);

        bundle = getArguments();
        if (bundle != null) {
            orderId = bundle.getString("orderId", "0");
            sellReportDate = bundle.getString("sellReportDate", "0");
        }
        System.out.println("orderId from bundle " + orderId);
        System.out.println("sellReportDate from bundle " + sellReportDate);

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

        orderItemDetailsList = new ArrayList<OrderItemDetails>();

        DownloadSellReportAsync downloadSellReportAsync = new DownloadSellReportAsync();
        downloadSellReportAsync.execute();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("sellReportDate", sellReportDate);
                Fragment fragment = new ViewTodaySellFragment();
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

        return view;
    }

    class DownloadSellReportAsync extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            reportProgressBar.setVisibility(View.VISIBLE);
            reportProgressBar.setProgress(5);
        }

        @Override
        protected String doInBackground(String... strings) {
            getDetails();
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
                    String strUnitResponse = response.getProperty("ItemSales_DetailResult").toString();

                    JSONArray jarray = new JSONArray(strUnitResponse);

                    for (int i=0; i<jarray.length(); i++)
                    {
                        orderItemDetails = new OrderItemDetails(
                                jarray.getJSONObject(i).getString("ItemId"),
                                jarray.getJSONObject(i).getString("ItemName"),
                                jarray.getJSONObject(i).getString("Quantity"),
                                jarray.getJSONObject(i).getString("TotalAmount"),
                                jarray.getJSONObject(i).getString("msg"));
                        orderItemDetailsList.add(orderItemDetails);
                    }

                    System.out.println("list size = "
                            + orderItemDetailsList.size());

                    orderItemDetailsAdapter = new OrderItemDetailsAdapter(getActivity().getApplicationContext(),
                            orderItemDetailsList);
                    orderItemDetailsAdapter.notifyDataSetChanged();
                    sellReportDetailsListView.setAdapter(orderItemDetailsAdapter);
                }
            }
            catch (Exception e)
            {
                e.getMessage();
            }
        }
    }

    public void getDetails()
    {
        String METHOD_NAME = "ItemSales_Detail";
        String SOAP_ACTION = UrlStrings.NAMESPACE+"ItemSales_Detail";

        System.out.println("Soap action = " + SOAP_ACTION);
        System.out.println("url = " + UrlStrings.URL);

        SoapObject request = new SoapObject(UrlStrings.NAMESPACE, METHOD_NAME);
        // Property which holds input parameters
        PropertyInfo companyIdPI = new PropertyInfo();
        PropertyInfo outletIdPI = new PropertyInfo();
        PropertyInfo datePI = new PropertyInfo();

        companyIdPI.setName("CompanyId");
        outletIdPI.setName("OutletId");
        datePI.setName("OrderId");

        companyIdPI.setValue(companyId);
        outletIdPI.setValue(outletId);
        datePI.setValue(orderId);

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
                response = (SoapObject) envelope.bodyIn;
                //Log.d("response property" , response.getProperty("LoginResult").toString());
                Log.d("response property" , response.getProperty("ItemSales_DetailResult").toString());
                Log.d("WS", response.toString());
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
}