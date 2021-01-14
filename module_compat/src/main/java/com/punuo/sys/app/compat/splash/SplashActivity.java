package com.punuo.sys.app.compat.splash;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.punuo.sys.app.compat.R;
import com.punuo.sys.app.compat.process.ProcessTasks;
import com.punuo.sys.app.compat.when_page.PageFrameLayout;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.util.MMKVUtil;

public class SplashActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ProcessTasks.commonLaunchTasks(getApplication());
        init();
    }

    public void init() {
        boolean isFirstRun = MMKVUtil.getBoolean("is_first_run", true);
        if (isFirstRun) {
            MMKVUtil.setBoolean("is_first_run", false);
            setContentView(R.layout.activity_splash);
            PageFrameLayout contentFrameLayout = (PageFrameLayout) findViewById(R.id.contentFrameLayout);
            // 设置资源文件和选中圆点
            contentFrameLayout.setUpViews(new int[]{
                    R.layout.compat_page_tab1,
                    R.layout.compat_page_tab2,
                    R.layout.compat_page_tab3
            }, R.drawable.banner_on, R.drawable.banner_off);
        } else {
            ARouter.getInstance().build(HomeRouter.ROUTER_LOGIN_ACTIVITY)
                    .navigation();
            finish();
        }
    }
}

