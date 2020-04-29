package com.bupt.sse.group7.covid19;

import android.app.Activity;

import java.lang.ref.WeakReference;

public class ActivityManager {
    private static ActivityManager sInstance = new ActivityManager();
    private WeakReference<Activity> sCurrentActivityWeakRef;
    private HospitalListActivity hla;

    private ActivityManager() {
    }

    public HospitalListActivity getHLA() {
        return hla;
    }

    public void setHLA(HospitalListActivity hla) {
        this.hla = hla;
    }

    public static ActivityManager getInstance() {
        return sInstance;
    }

    public Activity getCurrentActivity() {
        Activity currentActivity = null;
        if (sCurrentActivityWeakRef != null && sCurrentActivityWeakRef.get() != null) {
            currentActivity = sCurrentActivityWeakRef.get();
        }
        return currentActivity;
    }

    public void setCurrentActivity(Activity activity) {
        sCurrentActivityWeakRef = new WeakReference<Activity>(activity);
    }
}