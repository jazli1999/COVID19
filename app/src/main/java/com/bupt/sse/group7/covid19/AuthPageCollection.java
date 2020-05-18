package com.bupt.sse.group7.covid19;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class AuthPageCollection extends Fragment {
    private AuthPagerAdapter authPageAdapter;
    private ViewPager2 viewPager;
    private List<String> authTabLabels;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_auth_collection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authPageAdapter = new AuthPagerAdapter(this);
        viewPager = view.findViewById(R.id.auth_content);
        viewPager.setAdapter(authPageAdapter);

        authTabLabels = new ArrayList<>();
        authTabLabels.add("个人认证");
        authTabLabels.add("医院认证");

        TabLayout tabs = view.findViewById(R.id.auth_tabs);
        new TabLayoutMediator(tabs, viewPager, (tab, position) -> tab.setText(authTabLabels.get(position))).attach();
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}