package com.bupt.sse.group7.covid19.adapter;

import com.bupt.sse.group7.covid19.fragment.HospitalAuthFragment;
import com.bupt.sse.group7.covid19.PatientAuthFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AuthPagerAdapter extends FragmentStateAdapter {

    public AuthPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        //TODO 根据position创建不同的fragment
        Fragment fragment;
        if (position == 0) {
            fragment = new PatientAuthFragment();
        } else {
            fragment = new HospitalAuthFragment();
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
