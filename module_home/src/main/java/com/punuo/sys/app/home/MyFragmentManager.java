package com.punuo.sys.app.home;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.R;


/**
 * Created by han.chen.
 * Date on 2019/4/2.
 **/
public class MyFragmentManager {
    private String lastFragmentTag;
    private Context mContext;

    private FragmentManager mFragmentManager;
    public MyFragmentManager(FragmentActivity context) {
        mFragmentManager = context.getSupportFragmentManager();
        mContext = context;
    }

    public MyFragmentManager(Fragment fragment) {
        mFragmentManager = fragment.getChildFragmentManager();
        mContext = fragment.getContext();
    }

    public void switchFragmentWithCache(String key, Bundle bundle) {
        if (lastFragmentTag == null || !TextUtils.equals(lastFragmentTag, key)) {

            FragmentTransaction ft = mFragmentManager.beginTransaction();
            if (lastFragmentTag != null) {
                ft.hide(mFragmentManager.findFragmentByTag(lastFragmentTag));
            }

            Fragment fragment = mFragmentManager.findFragmentByTag(key);
            if (fragment == null) {
                fragment = Fragment.instantiate(mContext, key, bundle);
                ft.add(R.id.content_frame, fragment, key);
            } else {
                ft.show(fragment);
            }

            lastFragmentTag = key;

            ft.commitAllowingStateLoss();
            mFragmentManager.executePendingTransactions();
        }
    }
}
