package com.bupt.sse.group7.covid19;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AuthPagerAdapter extends FragmentStateAdapter {

    public AuthPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        //TODO 根据position创建不同的fragment
        Fragment fragment = new PatientAuthFragment();
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
