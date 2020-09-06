package com.mi5.bakeryappnew.activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.mi5.bakeryappnew.R;
import com.mi5.bakeryappnew.adapter.ViewPagerAdapter;
import com.mi5.bakeryappnew.fragment.ConfirmOrderFragment;
import com.mi5.bakeryappnew.fragment.ContactInformationFragment;
import com.mi5.bakeryappnew.fragment.GenerateOrderFragment;
import com.mi5.bakeryappnew.fragment.HomeFragment;

public class TabMainActivity extends AppCompatActivity implements
        ContactInformationFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener,
        GenerateOrderFragment.OnFragmentInteractionListener,
        ConfirmOrderFragment.OnFragmentInteractionListener
{

    TabLayout profileTabLayout;
    ViewPager viewPager;
    Fragment fragment = null;
    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_main);

        viewPager = findViewById(R.id.view_pager);
        profileTabLayout = findViewById(R.id.tabs);

        setupViewPager(viewPager);
        profileTabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        //ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new HomeFragment(), getResources().
                getString(R.string.nav_header_home));
        adapter.addFragment(new GenerateOrderFragment(),
                getResources().getString(R.string.nav_header_generate_order));
        adapter.addFragment(new ContactInformationFragment(),
                getResources().getString(R.string.nav_header_contact_us));

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // NOTE:  Code to replace the toolbar title based current visible fragment
        //getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
    }
}