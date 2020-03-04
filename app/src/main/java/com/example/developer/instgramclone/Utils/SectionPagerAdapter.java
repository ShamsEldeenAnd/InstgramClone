package com.example.developer.instgramclone.Utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import java.util.ArrayList;
import java.util.List;

public class SectionPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentslist = new ArrayList<>();

    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return mFragmentslist.get(i);
    }

    @Override
    public int getCount() {
        return mFragmentslist.size();
    }

    //method for adding fragments to the list .
    public void addFragment(Fragment fragment) {
        mFragmentslist.add(fragment);
    }
}
