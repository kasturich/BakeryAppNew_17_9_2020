package com.mi5.bakeryappnew.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mi5.bakeryappnew.R;
import com.mi5.bakeryappnew.activity.NavigationDrawerActivity;
import com.mi5.bakeryappnew.adapter.SchemeDetailsAdapter;
import com.mi5.bakeryappnew.database.MyDBHandler;
import com.mi5.bakeryappnew.model.SchemeDetails;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SchemeDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SchemeDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SchemeDetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    boolean serverConnection=false;
    SharedPreferences sharedpreferences;
    boolean hasLoggedIn, downloadData;
    SoapObject responseScheme;

    String companyId, jsonSharedPreference, outletId;

    SwipeRefreshLayout swipeToRefresh;
    ListView schemeListView;
    ProgressBar schemeProgressBar;

    SchemeDetails schemeDetails;
    List<SchemeDetails> schemeDetailsList;
    List<SchemeDetails> showSchemeList;
    SchemeDetailsAdapter schemeDetailsAdapter;

    private OnFragmentInteractionListener mListener;

    public SchemeDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SchemeDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SchemeDetailsFragment newInstance(String param1, String param2) {
        SchemeDetailsFragment fragment = new SchemeDetailsFragment();
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
        View view = inflater.inflate(R.layout.fragment_scheme_details, container, false);

        ((NavigationDrawerActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getString(R.string.scheme_title));

        //swipeToRefresh = view.findViewById(R.id.swipeToRefresh);
        schemeListView = view.findViewById(R.id.schemeListView);
        schemeProgressBar = view.findViewById(R.id.schemeProgressBar);

        //swipeToRefresh.setColorSchemeResources(R.color.colorAccent);

        sharedpreferences = getActivity().getSharedPreferences("LoginDetails",
                Context.MODE_PRIVATE);

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

        schemeDetailsList = new ArrayList<SchemeDetails>();
        showSchemeList = new ArrayList<SchemeDetails>();

        /*swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                schemeDetailsList.clear();
                insertIntoSchemeList();
                swipeToRefresh.setRefreshing(false);
            }
        });*/

        DownloadSchemeAsync downloadSchemeAsync = new DownloadSchemeAsync();
        downloadSchemeAsync.execute();

        return view;
    }

    class DownloadSchemeAsync extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            schemeProgressBar.setVisibility(View.VISIBLE);
            schemeProgressBar.setProgress(5);
        }

        @Override
        protected String doInBackground(String... strings) {
            downloadScheme();
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

                    schemeProgressBar.setVisibility(View.GONE);

                    String strUnitResponse = responseScheme.getProperty("AppliedScheme_DetailsResult").toString();

                    JSONArray jarray = new JSONArray(strUnitResponse);

                    for (int i=0; i<jarray.length(); i++)
                    {
                        schemeDetails = new SchemeDetails(
                                jarray.getJSONObject(i).getString("CompanyId"),
                                jarray.getJSONObject(i).getString("SchemeId"),
                                jarray.getJSONObject(i).getString("CategoryId"),
                                jarray.getJSONObject(i).getString("Category"),
                                jarray.getJSONObject(i).getString("OutletId"),
                                jarray.getJSONObject(i).getString("OutletName"),
                                jarray.getJSONObject(i).getString("Discount"),
                                jarray.getJSONObject(i).getString("FromDate"),
                                jarray.getJSONObject(i).getString("ToDate"),
                                jarray.getJSONObject(i).getString("msg"));
                        schemeDetailsList.add(schemeDetails);
                    }

                    for(int j=0; j<schemeDetailsList.size(); j++)
                    {
                        try {
                            String strEndDate = schemeDetailsList.get(j).getToDate();
                            System.out.println("strEndDate " + strEndDate);
                            SimpleDateFormat dateFormat1 = new SimpleDateFormat(
                                    "dd/MM/yyyy", Locale.US);
                            String currentDate = dateFormat1.format(new Date());
                            Date endDate = dateFormat1.parse(strEndDate);
                            System.out.println(dateFormat1.format(endDate));
                            if (dateFormat1.parse(currentDate).after
                                    (dateFormat1.parse(strEndDate))) {

                                System.out.println("currentDate in if " + currentDate);
                                System.out.println("strEndDate in if " + strEndDate);
                            }
                            else
                            {
                                schemeDetails = new SchemeDetails(
                                        schemeDetailsList.get(j).getCompanyId(),
                                        schemeDetailsList.get(j).getSchemeId(),
                                        schemeDetailsList.get(j).getCategoryId(),
                                        schemeDetailsList.get(j).getCategory(),
                                        schemeDetailsList.get(j).getOutletId(),
                                        schemeDetailsList.get(j).getOutletName(),
                                        schemeDetailsList.get(j).getDiscount(),
                                        schemeDetailsList.get(j).getFromDate(),
                                        schemeDetailsList.get(j).getToDate(),
                                        ""
                                );
                                showSchemeList.add(schemeDetails);
                            }

                            schemeDetailsAdapter = new SchemeDetailsAdapter(getActivity().getApplicationContext(),
                                    showSchemeList);
                            schemeDetailsAdapter.notifyDataSetChanged();
                            schemeListView.setAdapter(schemeDetailsAdapter);

                            System.out.println("scheme list size = "
                                    + showSchemeList.size());
                        }
                        catch (Exception e)
                        {
                            e.getMessage();
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

    public void downloadScheme()
    {
        String METHOD_NAME = "AppliedScheme_Details";
        String SOAP_ACTION = UrlStrings.NAMESPACE+"AppliedScheme_Details";

        System.out.println("Soap action = " + SOAP_ACTION);
        System.out.println("url = " + UrlStrings.URL);

        SoapObject request = new SoapObject(UrlStrings.NAMESPACE, METHOD_NAME);
        // Property which holds input parameters
        PropertyInfo companyIdPI = new PropertyInfo();
        PropertyInfo outletIdPI = new PropertyInfo();

        companyIdPI.setName("CompanyId");
        outletIdPI.setName("OutletId");

        companyIdPI.setValue(companyId);
        outletIdPI.setValue(outletId);

        companyIdPI.setType(String.class);
        outletIdPI.setType(String.class);

        request.addProperty(companyIdPI);
        request.addProperty(outletIdPI);

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
                responseScheme = (SoapObject) envelope.bodyIn;
                //Log.d("response property" , response.getProperty("LoginResult").toString());
                Log.d("response property" , responseScheme.getProperty("AppliedScheme_DetailsResult").toString());
                Log.d("WS", responseScheme.toString());
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
