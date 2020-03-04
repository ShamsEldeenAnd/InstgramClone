package com.example.developer.instgramclone.Utils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SectionStatePagerAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentslist = new ArrayList<>();
    private final HashMap<Fragment, Integer> mFragments = new HashMap<>();
    private final HashMap<Integer, String> mFragmentsnames = new HashMap<>();
    private final HashMap<String, Integer> mFragmentsnumber = new HashMap<>();

    public SectionStatePagerAdapter(FragmentManager fm) {
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

    public void addFragment(Fragment fragment, String fragmentName) {
        mFragmentslist.add(fragment);
        mFragments.put(fragment, mFragmentslist.size() - 1);
        mFragmentsnames.put(mFragmentslist.size() - 1, fragmentName);
        mFragmentsnumber.put(fragmentName, mFragmentslist.size() - 1);
    }

    public Integer getFragmentNumber(String fragmentName) {
        if (mFragmentsnumber.containsKey(fragmentName)) {
            return mFragmentsnumber.get(fragmentName);
        } else return null;
    }


    public Integer getFragmentNumber(Fragment fragment) {
        if (mFragments.containsKey(fragment)) {
            return mFragments.get(fragment);
        } else return null;
    }

    public String getFragmentName(int fragmentNumber) {
        if (mFragmentsnames.containsKey(fragmentNumber)) {
            return mFragmentsnames.get(fragmentNumber);
        } else return null;
    }

}
