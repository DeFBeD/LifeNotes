package com.example.jburgos.life_notes.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

public class BottomBarAdapter extends SmartSwipePager {
    private final List<Fragment> fragments = new ArrayList<>();

    public BottomBarAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public void addFragments(Fragment fragment) {
        fragments.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
