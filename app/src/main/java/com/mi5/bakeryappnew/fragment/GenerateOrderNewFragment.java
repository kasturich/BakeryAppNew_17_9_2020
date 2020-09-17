package com.mi5.bakeryappnew.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mi5.bakeryappnew.R;
import com.mi5.bakeryappnew.activity.NavigationDrawerActivity;
import com.mi5.bakeryappnew.adapter.ItemDatabaseDetailsAdapter;
import com.mi5.bakeryappnew.adapter.ItemDetailsAdapter;
import com.mi5.bakeryappnew.adapter.SimpleSpinnerArrayAdapter;
import com.mi5.bakeryappnew.database.MyDBHandler;
import com.mi5.bakeryappnew.model.AddItemsForOrder;
import com.mi5.bakeryappnew.model.CategoryDatabaseDetails;
import com.mi5.bakeryappnew.model.CategoryDetails;
import com.mi5.bakeryappnew.model.ItemDatabaseDetails;
import com.mi5.bakeryappnew.model.ItemDetails;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GenerateOrderNewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GenerateOrderNewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GenerateOrderNewFragment extends Fragment
        implements AdapterView.OnItemSelectedListener{
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
    String companyId, jsonSharedPreference;
    String catPrimaryId, itemPrimaryId;

    SwipeRefreshLayout swipeToRefresh;
    Spinner categorySpinner;
    ListView itemListView;
    ProgressBar itemProgressBar;
    FloatingActionButton floatingActionButton;

    CategoryDetails categoryDetails;
    List<CategoryDetails> categoryDetailsList;

    ItemDetails itemDetails;
    List<ItemDetails> itemDetailsList;
    ItemDetailsAdapter itemDetailsAdapter;

    SimpleSpinnerArrayAdapter simpleSpinnerArrayAdapter;

    SoapObject responseCategory, responseItem;

    String itemName, strTotalItemQuantity, keyItem, valueItem, orderItemJSONString;
    int itemCount=0, totalQuantity=0, showItemCount=0;

    AddItemsForOrder addItemsForOrder, addItemsForOrderParcable, addOrderBackItem;
    List<AddItemsForOrder> addItemsForOrderList;
    List<AddItemsForOrder> showItemsCountList;

    ArrayList<AddItemsForOrder> addItemsForOrdersParcelableList
            = new ArrayList<AddItemsForOrder>();

    ArrayList<AddItemsForOrder> addItemsForBackOrderList
            = new ArrayList<AddItemsForOrder>();

    Bundle bundle;

    String valueItemSeparator[];

    Map<String, Integer> map = new TreeMap<String, Integer>();
    Set keys;
    Iterator ii;

    private OnFragmentInteractionListener mListener;

    public GenerateOrderNewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GenerateOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GenerateOrderNewFragment newInstance(String param1, String param2) {
        GenerateOrderNewFragment fragment = new GenerateOrderNewFragment();
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
        View view = inflater.inflate(R.layout.fragment_generate_order, container, false);

        ((NavigationDrawerActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getString(R.string.nav_header_generate_order));

        //swipeToRefresh = view.findViewById(R.id.swipeToRefresh);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        itemListView = view.findViewById(R.id.itemListView);
        itemProgressBar = view.findViewById(R.id.itemProgressBar);
        floatingActionButton = view.findViewById(R.id.floatingActionButton);

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
                    System.out.println("company id " + companyId);
                }

            } catch (JSONException e) {

                System.out.println("sharedpreference error = " + e.getMessage());
            }
        }

        itemDetailsList = new ArrayList<ItemDetails>();
        addItemsForOrderList = new ArrayList<AddItemsForOrder>();
        showItemsCountList = new ArrayList<AddItemsForOrder>();
        categoryDetailsList = new ArrayList<CategoryDetails>();

        addItemsForBackOrderList = new ArrayList<AddItemsForOrder>();

        bundle = this.getArguments();
        if (bundle != null) {
            addOrderBackItem = (AddItemsForOrder) bundle.getParcelable("addOrderBackItem");
            addItemsForBackOrderList = bundle.getParcelableArrayList(
                    "addBackOrderList");
        }
        System.out.println("addItemsForBackOrderList " + addItemsForBackOrderList.size());

        categorySpinner.setOnItemSelectedListener(this);

        //insertCategoriesIntoList();

        DownloadCategoriesAsync downloadCategoriesAsync = new DownloadCategoriesAsync();
        downloadCategoriesAsync.execute();

        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Item is selected", Toast.LENGTH_SHORT).show();
                    System.out.println("add button item id " + itemDetailsList.get(i).getItemId());

                    if (addItemsForBackOrderList.size() > 0) {
                        System.out.println("addItemsForBackOrderList is > 0"
                                + addItemsForBackOrderList.size());
                        System.out.println("add button back item itemCount " + itemCount);
                        //for (int j=0; j<addItemsForBackOrderList.size();j++)
                        //{
                        addOrderBackItem = new AddItemsForOrder(
                                itemDetailsList.get(i).getItemId(),
                                itemDetailsList.get(i).getItemName(),
                                String.valueOf(itemCount),
                                itemDetailsList.get(i).getAmount(),
                                itemDetailsList.get(i).getCategoryId()
                        );

                        addItemsForBackOrderList.add(addOrderBackItem);
                        showItemsCountList.add(addOrderBackItem);

                        System.out.println("addItemsForBackOrderList " + addItemsForBackOrderList.size());
                        System.out.println("showItemsCountList " + showItemsCountList.size());
                        System.out.println("addItemsForBackOrderList item quantity "
                                + addItemsForBackOrderList.get(i).getItemQuantity());

                        int count = 1;
                        try {
                            count = map.get(itemDetailsList.get(i).getItemId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        map.put(itemDetailsList.get(i).getItemId(), (count + 1));
                        /*Toast.makeText(getActivity().getApplicationContext(),
                                String.valueOf(count), Toast.LENGTH_LONG).show();*/

                        view = itemListView.getChildAt(i);
                        LinearLayout mainLinear = view.findViewById(R.id.mainLinear);
                        TextView txtCount = view.findViewById(R.id.txtCount);
                        //itemCount = Integer.parseInt(addItemsForOrderList.get(i).getItemQuantity());
                        txtCount.setText(String.valueOf(count));
                        mainLinear.setBackgroundColor(getResources().getColor(R.color.light_grey));

                    } else {
                        System.out.println("addItemsForBackOrderList is < 0"
                                + addItemsForBackOrderList.size());
                        System.out.println("add button item itemCount " + itemCount);
                        showItemCount = 0;

                        addItemsForOrder = new AddItemsForOrder(
                                itemDetailsList.get(i).getItemId(),
                                itemDetailsList.get(i).getItemName(),
                                String.valueOf(itemCount),
                                itemDetailsList.get(i).getAmount(),
                                itemDetailsList.get(i).getCategoryId()
                        );

                        addItemsForOrderList.add(addItemsForOrder);
                        showItemsCountList.add(addItemsForOrder);

                        System.out.println("showItemsCount List.get(i).getItemId() "
                                + itemDetailsList.get(i).getItemId());
                        /*showItemCount = countItemOccurance(showItemsCountList,
                                showItemsCountList.size(),
                                Integer.parseInt(showItemsCountList.get(i).getItemId()));*/

                        /*System.out.println("showItemCount "
                                + showItemsCountList.get(i).getItemId() + " " + showItemCount);

                        view = itemListView.getChildAt(i);
                        TextView txtCount = view.findViewById(R.id.txtCount);
                        txtCount.setText(String.valueOf(showItemCount));*/

                        int count = 1;
                        try {
                            count = map.get(itemDetailsList.get(i).getItemId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        map.put(itemDetailsList.get(i).getItemId(), (count + 1));
                        /*Toast.makeText(getActivity().getApplicationContext(),
                                String.valueOf(count), Toast.LENGTH_LONG).show();*/

                        view = itemListView.getChildAt(i);
                        LinearLayout mainLinear = view.findViewById(R.id.mainLinear);
                        TextView txtCount = view.findViewById(R.id.txtCount);
                        //itemCount = Integer.parseInt(addItemsForOrderList.get(i).getItemQuantity());
                        txtCount.setText(String.valueOf(count));
                        mainLinear.setBackgroundColor(getResources().getColor(R.color.light_grey));
                    }

                    System.out.println("itemCount " + itemCount);
                    System.out.println("showItemCount " + i + " " + showItemCount);
                    System.out.println("addItemsForOrderList size " + addItemsForOrderList.size());
                }
                catch (Exception e)
                {
                    e.getMessage();
                }
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(addItemsForBackOrderList.size()>0)
                {
                    for (int i = 0; i < addItemsForBackOrderList.size(); i++) {
                        System.out.println("i " + i);

                        if(Integer.parseInt(addItemsForBackOrderList.get(i).getItemQuantity())
                                >1)
                        {
                            itemCount = Integer.parseInt(addItemsForBackOrderList.get(i).getItemQuantity());
                        }
                        else {
                            itemCount = countOccurance(addItemsForBackOrderList,
                                    addItemsForBackOrderList.size(),
                                    Integer.parseInt(addItemsForBackOrderList.get(i).getItemId()));
                        }

                        System.out.println("addItemsForBackOrderList itemCount " + itemCount);
                        System.out.println("addItemsForBackOrderList itemCount "
                                + addItemsForBackOrderList.get(i).getItemQuantity());

                        addItemsForOrderParcable = new AddItemsForOrder(
                                addItemsForBackOrderList.get(i).getItemId(),
                                addItemsForBackOrderList.get(i).getItemName(),
                                String.valueOf(itemCount),
                                addItemsForBackOrderList.get(i).getItemPrice(),
                                addItemsForBackOrderList.get(i).getCatId()
                        );
                        addItemsForOrdersParcelableList.add(addItemsForOrderParcable);

                        System.out.println("addItemsForOrdersParcelableList id "
                                + addItemsForOrdersParcelableList.get(i).getItemId());

                        System.out.println("addItemsForOrdersParcelableList quantity "
                                + addItemsForOrdersParcelableList.get(i).getItemQuantity());
                    }
                }
                else
                {
                    for (int i = 0; i < addItemsForOrderList.size(); i++) {
                        System.out.println("i " + i);

                        itemCount = countOccurance(addItemsForOrderList,
                                addItemsForOrderList.size(),
                                Integer.parseInt(addItemsForOrderList.get(i).getItemId()));

                        addItemsForOrderParcable = new AddItemsForOrder(
                                addItemsForOrderList.get(i).getItemId(),
                                addItemsForOrderList.get(i).getItemName(),
                                String.valueOf(itemCount),
                                addItemsForOrderList.get(i).getItemPrice(),
                                addItemsForOrderList.get(i).getCatId()
                        );
                        addItemsForOrdersParcelableList.add(addItemsForOrderParcable);

                        System.out.println("addItemsForOrdersParcelableList id "
                                + addItemsForOrdersParcelableList.get(i).getItemId());

                        System.out.println("addItemsForOrdersParcelableList quantity "
                                + addItemsForOrdersParcelableList.get(i).getItemQuantity());
                    }
                }

                Bundle bundle = new Bundle();
                bundle.putParcelable("addOrderItem", addItemsForOrderParcable);
                bundle.putParcelableArrayList("addItemsForOrdersParcelableList", addItemsForOrdersParcelableList);
                //bundle.putParcelableArrayList("addOrderItemList", addItemsForOrderList);
                //args.putString(ARG_PARAM2, param2);
                //Navigation.findNavController(view).navigate(R.id.nav_confirm_order, bundle);

                Fragment fragment = new ConfirmOrderNewFragment();
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .commit();
                if (fragment != null) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, fragment, "Confirmation");
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                    // set the toolbar title
                    // getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                    //toolbar_title.setText(getResources().getString(R.string.app_name));
                }
            }
        });

        return view;
    }

    public int countOccurance(List<AddItemsForOrder> addItemsForOrdersParcelableList,
                              int listSize, int searchId)
    {
        int res =0;
        for(int i=0; i<listSize; i++)
        {
            if(searchId == Integer.parseInt(
                    addItemsForOrdersParcelableList.get(i).getItemId()))
            {
                res++;
            }
        }
        return res;
    }

    public int countItemOccurance(List<AddItemsForOrder> addItemsForOrdersParcelableList,
                              int listSize, int searchId)
    {
        int res =0;
        for(int i=0; i<listSize; i++)
        {
            if(searchId == Integer.parseInt(
                    addItemsForOrdersParcelableList.get(i).getItemId()))
            {
                res++;
            }
        }
        return res;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        try {
            //categoryDetailsList.clear();
            itemDetailsList.clear();
            catPrimaryId =categoryDetailsList.get(i).getCategoryId();
            Log.e("Category Database", catPrimaryId);
            //insertCategoryItemIntoList(catPrimaryId);

            DownloadItemsAsync downloadItemsAsync = new DownloadItemsAsync(catPrimaryId);
            downloadItemsAsync.execute();
        }
        catch (Exception e)
        {
            e.getMessage();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    class DownloadCategoriesAsync extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            itemProgressBar.setVisibility(View.VISIBLE);
            itemProgressBar.setProgress(5);
        }

        @Override
        protected String doInBackground(String... strings) {
            downloadCategories();
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
                    itemProgressBar.setVisibility(View.GONE);
                    String strUnitResponse = responseCategory.getProperty("Category_DetailsResult").toString();

                    JSONArray jarray = new JSONArray(strUnitResponse);

                    for (int i=0; i<jarray.length(); i++)
                    {
                        categoryDetails = new CategoryDetails(
                                jarray.getJSONObject(i).getString("CategoryId"),
                                jarray.getJSONObject(i).getString("Category"),
                                jarray.getJSONObject(i).getString("CompanyId"),
                                jarray.getJSONObject(i).getString("msg"));
                        categoryDetailsList.add(categoryDetails);
                    }
                    boolean isInserted = false;

                    System.out.println("Download Activity unit list size = "
                            + categoryDetailsList.size());

                    simpleSpinnerArrayAdapter = new SimpleSpinnerArrayAdapter
                            (getActivity().getApplicationContext(), categoryDetailsList);
                    simpleSpinnerArrayAdapter.notifyDataSetChanged();
                    categorySpinner.setAdapter(simpleSpinnerArrayAdapter);
                }
            }
            catch (Exception e)
            {
                e.getMessage();
            }
        }
    }

    public void downloadCategories()
    {
        String METHOD_NAME = "Category_Details";
        String SOAP_ACTION = UrlStrings.NAMESPACE+"Category_Details";

        System.out.println("Soap action = " + SOAP_ACTION);
        System.out.println("url = " + UrlStrings.URL);

        SoapObject request = new SoapObject(UrlStrings.NAMESPACE, METHOD_NAME);
        // Property which holds input parameters
        PropertyInfo companyIdPI = new PropertyInfo();
        PropertyInfo passPI = new PropertyInfo();

        companyIdPI.setName("CompanyId");

        companyIdPI.setValue(companyId);

        companyIdPI.setType(String.class);

        request.addProperty(companyIdPI);

        System.out.println("request - " + companyIdPI + " = " + passPI + " = ");

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
                responseCategory = (SoapObject) envelope.bodyIn;
                //Log.d("response property" , response.getProperty("LoginResult").toString());
                Log.d("response property" , responseCategory.getProperty("Category_DetailsResult").toString());
                Log.d("WS", responseCategory.toString());
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

    class DownloadItemsAsync extends AsyncTask<String, Void, String>
    {
        String catId;

        public DownloadItemsAsync(String catId)
        {
            this.catId = catId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            itemProgressBar.setVisibility(View.VISIBLE);
            itemProgressBar.setProgress(5);
        }

        @Override
        protected String doInBackground(String... strings) {
            downloadItems(catId);
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

                    itemProgressBar.setVisibility(View.GONE);

                    String strUnitResponse = responseItem.getProperty("Item_DetailsResult").toString();

                    JSONArray jarray = new JSONArray(strUnitResponse);

                    for (int i=0; i<jarray.length(); i++)
                    {
                        itemDetails = new ItemDetails(
                                jarray.getJSONObject(i).getString("ItemId"),
                                jarray.getJSONObject(i).getString("ItemName"),
                                jarray.getJSONObject(i).getString("CategoryId"),
                                jarray.getJSONObject(i).getString("Category"),
                                jarray.getJSONObject(i).getString("UnitId"),
                                jarray.getJSONObject(i).getString("Unit"),
                                jarray.getJSONObject(i).getString("Amount"),
                                jarray.getJSONObject(i).getString("CompanyId"),
                                jarray.getJSONObject(i).getString("msg"));
                        itemDetailsList.add(itemDetails);
                    }
                    boolean isInserted = false;

                    System.out.println("Download Activity item list size = "
                            + itemDetailsList.size());

                    itemDetailsAdapter = new ItemDetailsAdapter
                            (getActivity().getApplicationContext(), itemDetailsList);
                    itemDetailsAdapter.notifyDataSetChanged();
                    itemListView.setAdapter(itemDetailsAdapter);
                }
            }
            catch (Exception e)
            {
                e.getMessage();
            }
        }
    }

    public void downloadItems(String catId)
    {
        String METHOD_NAME = "Item_Details";
        String SOAP_ACTION = UrlStrings.NAMESPACE+"Item_Details";

        System.out.println("Soap action = " + SOAP_ACTION);
        System.out.println("url = " + UrlStrings.URL);

        SoapObject request = new SoapObject(UrlStrings.NAMESPACE, METHOD_NAME);
        // Property which holds input parameters
        PropertyInfo companyIdPI = new PropertyInfo();
        PropertyInfo categoryIdPI = new PropertyInfo();

        companyIdPI.setName("CompanyId");
        categoryIdPI.setName("CategoryId");

        companyIdPI.setValue(companyId);
        categoryIdPI.setValue(catId);

        companyIdPI.setType(String.class);
        categoryIdPI.setType(String.class);

        request.addProperty(companyIdPI);
        request.addProperty(categoryIdPI);

        System.out.println("request - " + companyIdPI + " = " + categoryIdPI + " = ");

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
                responseItem = (SoapObject) envelope.bodyIn;
                //Log.d("response property" , response.getProperty("LoginResult").toString());
                Log.d("response property" , responseItem.getProperty("Item_DetailsResult").toString());
                Log.d("WS", responseItem.toString());
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
