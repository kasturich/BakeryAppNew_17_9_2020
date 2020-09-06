package com.mi5.bakeryappnew.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.mi5.bakeryappnew.fragment.ContactInformationFragment;
import com.mi5.bakeryappnew.fragment.GenerateOrderFragment;
import com.mi5.bakeryappnew.fragment.HomeFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 04-04-2018.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {

        //System.out.println("mFragmentList.get(position) " +mFragmentList.get(position));
        //return mFragmentList.get(position);
        switch (position) {
            case 0: // Fragment # 0
                return new HomeFragment();
            case 1: // Fragment # 1
                return new GenerateOrderFragment();
            case 2:// Fragment # 2
                return new ContactInformationFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        //System.out.println("mFragmentList.size() " + mFragmentList.size());
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    public void removeFragment(Fragment fragment, int position) {
        mFragmentList.remove(position);
        mFragmentTitleList.remove(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}
