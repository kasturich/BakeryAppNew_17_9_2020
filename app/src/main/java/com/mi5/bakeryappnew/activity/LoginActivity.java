package com.mi5.bakeryappnew.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mi5.bakeryappnew.R;
import com.mi5.bakeryappnew.model.LoginDetails;
import com.mi5.bakeryappnew.utility.UrlStrings;

import org.json.JSONArray;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    EditText edtUserName, edtPassword;
    TextView txtLoginButton, txtForgotPassword;

    boolean serverConnection=false;
    SoapObject response;

    SharedPreferences sharedPreferences;
    boolean hasLoggedIn;

    LoginDetails loginDetails;
    List<LoginDetails> loginDetailsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUserName = findViewById(R.id.edtUserName);
        edtPassword = findViewById(R.id.edtPassword);
        txtLoginButton = findViewById(R.id.txtLoginButton);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);

        loginDetailsList = new ArrayList<LoginDetails>();

        txtLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isNetworkAvailable()) {
                    validateLogin();
                }
                else
                {
                    Toast.makeText(LoginActivity.this,
                            "Please check your internet", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void validateLogin()
    {
        if(edtUserName.length() == 0)
        {
            edtUserName.setError("Enter username");
            edtUserName.requestFocus();
        }
        else if(edtPassword.length() == 0)
        {
            edtPassword.setError("Enter password");
            edtPassword.requestFocus();
        }
        else
        {
            LoginAsync loginAsync = new LoginAsync(edtUserName.getText().toString(),
                    edtPassword.getText().toString());
            loginAsync.execute();
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

    class LoginAsync extends AsyncTask<String, Void, String>
    {
        String userName, userPassword;
        public LoginAsync(String userName, String userPassword)
        {
            this.userName = userName;
            this.userPassword = userPassword;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            callLogin(userName, userPassword);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try
            {
                if(serverConnection)
                {
                    Toast.makeText(LoginActivity.this, "Server or Internet is down. Please try after some time!", Toast.LENGTH_SHORT).show();
                }
                else {
                    serverConnection = false;
                    String strResponse = response.getProperty("Outlet_LoginResult").toString();

                    JSONArray jarray = new JSONArray(strResponse);
                    if (jarray.getJSONObject(0).getString("msg").equals("1")) {

                        for(int i=0; i<jarray.length(); i++)
                        {
                            loginDetails = new LoginDetails(
                                    jarray.getJSONObject(i).getString("UserName"),
                                    jarray.getJSONObject(i).getString("Password"),
                                    jarray.getJSONObject(i).getString("OutletId"),
                                    jarray.getJSONObject(i).getString("OutletName"),
                                    jarray.getJSONObject(i).getString("Address"),
                                    jarray.getJSONObject(i).getString("MobileNo"),
                                    jarray.getJSONObject(i).getString("CompanyId"),
                                    jarray.getJSONObject(i).getString("msg")
                            );

                            loginDetailsList.add(loginDetails);
                        }

                        sharedPreferences = getApplicationContext().getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString("JSON", strResponse);
                        editor.putBoolean("hasLoggedIn", true);
                        editor.putBoolean("downloadData", false);
                        editor.apply();
                        editor.commit();

                        /*Intent intent = new Intent(LoginActivity.this, TabMainActivity.class);
                        startActivity(intent);*/

                        Intent intent = new Intent(LoginActivity.this,
                                NavigationDrawerActivity.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Please check username and password.", Toast.LENGTH_SHORT).show();

                    }
                }
            }
            catch (Exception e)
            {
                e.getMessage();
            }
        }
    }

    public void callLogin(String userName, String userPassword)
    {
        String METHOD_NAME = "Outlet_Login";
        String SOAP_ACTION = UrlStrings.NAMESPACE+"Outlet_Login";

        System.out.println("Soap action = " + SOAP_ACTION);
        System.out.println("url = " + UrlStrings.URL);

        SoapObject request = new SoapObject(UrlStrings.NAMESPACE, METHOD_NAME);
        // Property which holds input parameters
        PropertyInfo unamePI = new PropertyInfo();
        PropertyInfo passPI = new PropertyInfo();

        unamePI.setName("UserName");
        passPI.setName("Password");

        unamePI.setValue(userName);
        passPI.setValue(userPassword);

        unamePI.setType(String.class);
        passPI.setType(String.class);

        request.addProperty(unamePI);
        request.addProperty(passPI);

        System.out.println("request - " + unamePI + " = " + passPI + " = ");

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
                Log.d("response property" , response.getProperty("Outlet_LoginResult").toString());
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
