package com.mi5.bakeryappnew.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mi5.bakeryappnew.R;
import com.mi5.bakeryappnew.database.MyDBHandler;
import com.mi5.bakeryappnew.model.CategoryDetails;
import com.mi5.bakeryappnew.model.ItemDetails;
import com.mi5.bakeryappnew.model.SchemeDetails;
import com.mi5.bakeryappnew.model.UnitDetails;
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

public class DownloadDataActivity extends AppCompatActivity {

    int status = 0;
    Handler handler = new Handler();
    Dialog dialog;
    ProgressBar downloadProgressBar;
    TextView txtDownloadCounter;
    MyDBHandler db;
    String companyId, outletId, jsonSharedPreference;
    SoapObject responseUnit, responseCategory, responseItem, responseScheme;

    UnitDetails unitDetails;
    List<UnitDetails> unitDetailsList;
    CategoryDetails categoryDetails;
    List<CategoryDetails> categoryDetailsList;
    ItemDetails itemDetails;
    List<ItemDetails> itemDetailsList;
    SchemeDetails schemeDetails;
    List<SchemeDetails> schemeDetailsList;

    boolean serverConnection=false;
    SharedPreferences sharedpreferences;
    boolean hasLoggedIn, downloadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_data);

        downloadProgressBar = findViewById(R.id.downloadProgressBar);
        txtDownloadCounter = findViewById(R.id.txtDownloadCounter);

        dialog = new Dialog(DownloadDataActivity.this);

        db =new MyDBHandler(DownloadDataActivity.this);

        sharedpreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);

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

        unitDetailsList = new ArrayList<UnitDetails>();
        categoryDetailsList = new ArrayList<CategoryDetails>();
        itemDetailsList = new ArrayList<ItemDetails>();
        schemeDetailsList = new ArrayList<SchemeDetails>();

        if(isNetworkAvailable()) {
            showDialog(DownloadDataActivity.this, "Downloading Files 1/3");
            DownloadUnitsAsync downloadItemsAsync = new DownloadUnitsAsync();
            downloadItemsAsync.execute();
        }
        else
        {
            Toast.makeText(this,
                    "Please check internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm =(ConnectivityManager)getSystemService
                (Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        System.out.println("connection " +isConnected);
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public void showDialog(Activity activity, String msg) {

        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.setTitle(msg);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_download_data);

        downloadProgressBar = (ProgressBar) dialog.findViewById(R.id.downloadProgressBar);
        txtDownloadCounter = dialog.findViewById(R.id.txtDownloadCounter);

        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (status < 100) {

                    status += 1;

                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            downloadProgressBar.setProgress(status);
                            txtDownloadCounter.setText(String.valueOf(status));

                            if (status == 100) {
                                status = 0;
                            }
                        }
                    });
                }
            }
        }).start();
    }

    class DownloadUnitsAsync extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //showDialog(DownloadDataActivity.this, "Downloading Files 1/3");
        }

        @Override
        protected String doInBackground(String... strings) {
            downloadUnits();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                if(serverConnection)
                {
                    Toast.makeText(DownloadDataActivity.this,
                            "Server or Internet is down. Please try after some time!", Toast.LENGTH_SHORT).show();
                }
                else {
                    serverConnection = false;
                    String strUnitResponse = responseUnit.getProperty("Unit_DetailsResult").toString();

                    JSONArray jarray = new JSONArray(strUnitResponse);

                    for (int i=0; i<jarray.length(); i++)
                    {
                        unitDetails = new UnitDetails(
                                jarray.getJSONObject(i).getString("UnitId"),
                                jarray.getJSONObject(i).getString("Unit"),
                                jarray.getJSONObject(i).getString("CompanyId"),
                                jarray.getJSONObject(i).getString("msg"));
                        unitDetailsList.add(unitDetails);
                    }
                    boolean isInserted = false;

                    System.out.println("Download Activity unit list size = "
                            + unitDetailsList.size());

                    for(int j=0; j<unitDetailsList.size(); j++)
                    {
                        if (! unitDetailsList.get(j).getMsg().equals("1")) {
                            Toast.makeText(DownloadDataActivity.this,
                                    jarray.getJSONObject(j).getString("msg"),
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            isInserted = db.insertUnitData(unitDetailsList.get(j).getUnitId(),
                                    unitDetailsList.get(j).getUnit(),
                                    unitDetailsList.get(j).getCompanyId());
                        }
                    }

                    if(isInserted)
                    {
                        dialog.dismiss();
                        DownloadCategoriesAsync downloadCategoriesAsync = new
                                DownloadCategoriesAsync();
                        downloadCategoriesAsync.execute();

                        Toast.makeText(DownloadDataActivity.this,
                                "Units are inserted into DB", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(DownloadDataActivity.this,
                                "Units are not inserted into DB", Toast.LENGTH_SHORT).show();
                    }
                }
                dialog.dismiss();
            }
            catch (Exception e)
            {
                e.getMessage();
            }
        }
    }

    public void downloadUnits()
    {
        String METHOD_NAME = "Unit_Details";
        String SOAP_ACTION = UrlStrings.NAMESPACE+"Unit_Details";

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
                responseUnit = (SoapObject) envelope.bodyIn;
                //Log.d("response property" , response.getProperty("LoginResult").toString());
                Log.d("response property" , responseUnit.getProperty("Unit_DetailsResult").toString());
                Log.d("WS", responseUnit.toString());
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

    class DownloadCategoriesAsync extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            showDialog(DownloadDataActivity.this, "Downloading Files 2/3");
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
                    Toast.makeText(DownloadDataActivity.this,
                            "Server or Internet is down. Please try after some time!", Toast.LENGTH_SHORT).show();
                }
                else {
                    serverConnection = false;
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

                    for(int j=0; j<categoryDetailsList.size(); j++)
                    {
                        if (! categoryDetailsList.get(j).getMsg().equals("1")) {
                            Toast.makeText(DownloadDataActivity.this,
                                    jarray.getJSONObject(j).getString("msg"),
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            isInserted = db.insertCategoryData(
                                    categoryDetailsList.get(j).getCategoryId(),
                                    categoryDetailsList.get(j).getCategory(),
                                    categoryDetailsList.get(j).getCompanyId());
                        }
                    }

                    if(isInserted)
                    {
                        DownloadSchemeAsync downloadSchemeAsync = new DownloadSchemeAsync();
                        downloadSchemeAsync.execute();

                        Toast.makeText(DownloadDataActivity.this,
                                "Categories are inserted into DB", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(DownloadDataActivity.this,
                                "Categories are not inserted into DB", Toast.LENGTH_SHORT).show();
                    }
                }
                dialog.dismiss();
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

    class DownloadSchemeAsync extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            showDialog(DownloadDataActivity.this, "Downloading Files 2/3");
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
                    Toast.makeText(DownloadDataActivity.this,
                            "Server or Internet is down. Please try after some time!", Toast.LENGTH_SHORT).show();
                }
                else {
                    serverConnection = false;
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
                    boolean isInserted = false;

                    System.out.println("Download Activity scheme list size = "
                            + schemeDetailsList.size());

                    for(int j=0; j<schemeDetailsList.size(); j++)
                    {
                        if (schemeDetailsList.get(j).getMsg().equals("1")) {

                            isInserted = db.insertSchemeData(
                                schemeDetailsList.get(j).getCompanyId(),
                                schemeDetailsList.get(j).getSchemeId(),
                                schemeDetailsList.get(j).getCategoryId(),
                                schemeDetailsList.get(j).getCategory(),
                                schemeDetailsList.get(j).getOutletId(),
                                schemeDetailsList.get(j).getOutletName(),
                                schemeDetailsList.get(j).getDiscount(),
                                schemeDetailsList.get(j).getFromDate(),
                                schemeDetailsList.get(j).getToDate());
                        }
                        else
                        {
                            Toast.makeText(DownloadDataActivity.this,
                                    jarray.getJSONObject(j).getString("msg"),
                                    Toast.LENGTH_SHORT).show();

                            DownloadItemsAsync downloadItemsAsync = new DownloadItemsAsync();
                            downloadItemsAsync.execute();
                        }
                    }

                    if(isInserted)
                    {
                        DownloadItemsAsync downloadItemsAsync = new DownloadItemsAsync();
                        downloadItemsAsync.execute();

                        Toast.makeText(DownloadDataActivity.this,
                                "Schemes are inserted into DB", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(DownloadDataActivity.this,
                                "Schemes are not inserted into DB", Toast.LENGTH_SHORT).show();
                    }
                }
                dialog.dismiss();
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

    class DownloadItemsAsync extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            showDialog(DownloadDataActivity.this, "Downloading Files 3/3");
        }

        @Override
        protected String doInBackground(String... strings) {
            downloadItems();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                if(serverConnection)
                {
                    Toast.makeText(DownloadDataActivity.this,
                            "Server or Internet is down. Please try after some time!", Toast.LENGTH_SHORT).show();
                }
                else {
                    serverConnection = false;
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

                    for(int j=0; j<itemDetailsList.size(); j++)
                    {

                        System.out.println("itemDetailsList.get(j).getMsg() "
                                + itemDetailsList.get(j).getMsg() );
                        System.out.println("itemDetailsList.get(j).getItemId() "
                                + itemDetailsList.get(j).getItemId() );
                        System.out.println("itemDetailsList.get(j).getItemName() "
                                + itemDetailsList.get(j).getItemName() );
                        System.out.println("itemDetailsList.get(j).getAmount() "
                                + itemDetailsList.get(j).getAmount() );
                        System.out.println("itemDetailsList.get(j).getCategoryId() "
                                + itemDetailsList.get(j).getCategoryId() );
                        System.out.println("itemDetailsList.get(j).getCategory() "
                                + itemDetailsList.get(j).getCategory() );
                        System.out.println("itemDetailsList.get(j).getUnitId() "
                                + itemDetailsList.get(j).getUnitId() );
                        System.out.println("itemDetailsList.get(j).getUnit() "
                                + itemDetailsList.get(j).getUnit() );

                        if (itemDetailsList.get(j).getMsg().equals("1")) {
                            isInserted = db.insertItemData(
                                    itemDetailsList.get(j).getItemId(),
                                    itemDetailsList.get(j).getItemName(),
                                    itemDetailsList.get(j).getAmount(),
                                    itemDetailsList.get(j).getCategory(),
                                    itemDetailsList.get(j).getCategoryId(),
                                    itemDetailsList.get(j).getUnitId(),
                                    itemDetailsList.get(j).getUnit(),
                                    itemDetailsList.get(j).getCompanyId());
                        }
                        else
                        {
                            Toast.makeText(DownloadDataActivity.this,
                                    jarray.getJSONObject(j).getString("msg"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    if(isInserted)
                    {
                        sharedpreferences = getApplicationContext().getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();

                        editor.putBoolean("downloadData", true);
                        editor.apply();
                        editor.commit();

                        Intent intent = new Intent(DownloadDataActivity.this,
                                NavigationDrawerActivity.class);
                        startActivity(intent);

                        Toast.makeText(DownloadDataActivity.this,
                                "Items are inserted into DB", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(DownloadDataActivity.this,
                                "Items are not inserted into DB", Toast.LENGTH_SHORT).show();
                    }
                }
                dialog.dismiss();
            }
            catch (Exception e)
            {
                e.getMessage();
            }
        }
    }

    public void downloadItems()
    {
        String METHOD_NAME = "Item_Details";
        String SOAP_ACTION = UrlStrings.NAMESPACE+"Item_Details";

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
}
