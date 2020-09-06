package com.mi5.bakeryappnew.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.mi5.bakeryappnew.R;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView imViewAndroid;
    RelativeLayout mainRelative;

    Context context=this;

    SharedPreferences sharedpreferences;
    boolean hasLoggedIn, downloadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mainRelative = findViewById(R.id.mainRelative);

        sharedpreferences = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);

        hasLoggedIn = sharedpreferences.getBoolean("hasLoggedIn", false);
        downloadData = sharedpreferences.getBoolean("downloadData", false);

        System.out.println("hasLoggedIn " + hasLoggedIn);
        System.out.println("downloadData " + downloadData);

        new Handler().postDelayed(new Runnable() {
            public void run()
            {

                if(hasLoggedIn == false) {
                    startActivity(new Intent(context, LoginActivity.class));
                }
                else
                {
                    startActivity(new Intent(context, NavigationDrawerActivity.class));
                }
            }
        }, 2000);
    }
}
