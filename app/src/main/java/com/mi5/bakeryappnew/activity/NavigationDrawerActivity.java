package com.mi5.bakeryappnew.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.mi5.bakeryappnew.R;
import com.mi5.bakeryappnew.database.MyDBHandler;
import com.mi5.bakeryappnew.fragment.ConfirmOrderFragment;
import com.mi5.bakeryappnew.fragment.ConfirmOrderNewFragment;
import com.mi5.bakeryappnew.fragment.ContactInformationFragment;
import com.mi5.bakeryappnew.fragment.GenerateOrderFragment;
import com.mi5.bakeryappnew.fragment.GenerateOrderNewFragment;
import com.mi5.bakeryappnew.fragment.HomeFragment;
import com.mi5.bakeryappnew.fragment.OrderDoneFragment;
import com.mi5.bakeryappnew.fragment.SchemeDetailsFragment;
import com.mi5.bakeryappnew.fragment.SellReportFragment;
import com.mi5.bakeryappnew.fragment.ViewTodaySellFragment;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NavigationDrawerActivity extends AppCompatActivity
        implements
        ContactInformationFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener,
        GenerateOrderFragment.OnFragmentInteractionListener,
        ConfirmOrderFragment.OnFragmentInteractionListener,
        SchemeDetailsFragment.OnFragmentInteractionListener,
        SellReportFragment.OnFragmentInteractionListener,
        GenerateOrderNewFragment.OnFragmentInteractionListener,
        ConfirmOrderNewFragment.OnFragmentInteractionListener,
        OrderDoneFragment.OnFragmentInteractionListener,
        ViewTodaySellFragment.OnFragmentInteractionListener {

    private AppBarConfiguration mAppBarConfiguration;

    TextView txtOutletName;
    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;
    View navHeader;
    Fragment fragment = null;
    SharedPreferences sharedpreferences;
    String outletId, outletName, jsonSharedPreference;
    boolean hasLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle(getResources().getString(R.string.nav_header_home));

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Navigation view header
        navHeader = navigationView.getHeaderView(0);

        txtOutletName = navHeader.findViewById(R.id.txtOutletName);

        sharedpreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        /*user_id = sharedpreferences.getString("UserId", "");
        user_name = sharedpreferences.getString("UserName", "");*/
        jsonSharedPreference = sharedpreferences.getString("JSON", "");

        hasLoggedIn = sharedpreferences.getBoolean("hasLoggedIn", false);

        System.out.println("hasLoggedIn " + hasLoggedIn);

        if (jsonSharedPreference != null) {
            try {
                JSONArray jarray = new JSONArray(jsonSharedPreference);
                System.out.println("jarray length " + jarray.length());
                JSONObject json_obj = jarray.getJSONObject(0);
                for (int i=0; i<jarray.length(); i++)
                {
                    outletId = json_obj.getString("OutletId");
                    outletName = json_obj.getString("OutletName");
                    System.out.println("navigation outlet id " + outletId + " " + outletName);
                }

            } catch (JSONException e) {

                System.out.println("sharedpreference error = " + e.getMessage());
            }
        }

        txtOutletName.setText(outletName);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_generate_order, R.id.nav_confirm_order)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController,
                mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_home :
                Fragment fragment = new HomeFragment();
                getFragmentManager().beginTransaction()
                        .addToBackStack("home")
                        .commit();
                if (fragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                    fragmentTransaction.addToBackStack("home");
                    fragmentTransaction.commit();
                }
        }
        return false;
    }

    @Override
    public void onBackPressed() {

        System.out.println("Fragment " + getSupportFragmentManager().getFragments().get(0));
        //System.out.println("Fragment " + getSupportFragmentManager().getFragments().last);
        //Fragment f = getSupportFragmentManager().findFragmentById(R.id.nav_confirm_order);
        Fragment f = getSupportFragmentManager().findFragmentByTag("Confirmation");
        if (f instanceof ConfirmOrderNewFragment) {//the fragment on which you want to handle your back press
            Log.i("BACK PRESSED", "BACK PRESSED");
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController
                (this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // NOTE:  Code to replace the toolbar title based current visible fragment
        //getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
    }
}
