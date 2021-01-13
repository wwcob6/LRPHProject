package com.punuo.sys.sdk.compat.process;

import android.app.Application;

import com.punuo.sys.sdk.activity.ActivityLifeCycle;
import com.punuo.sys.sdk.httplib.HttpConfig;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.IHttpConfig;
import com.punuo.sys.sdk.util.DebugCrashHandler;
import com.punuo.sys.sdk.util.DeviceHelper;

/**
 * Created by han.chen.
 * Date on 2019/4/2.
 **/
public class ProcessTasks {

    public static void commonLaunchTasks(Application app) {
        if (DeviceHelper.isApkInDebug()) {
            DebugCrashHandler.getInstance().init(); //崩溃日志收集
        }
        app.registerActivityLifecycleCallbacks(ActivityLifeCycle.getInstance());
        HttpConfig.init(new IHttpConfig() {
            @Override
            public String getHost() {
                return "sip.qinqingonline.com";
            }

            @Override
            public int getPort() {
                return 8000;
            }

            @Override
            public boolean isUseHttps() {
                return false;
            }

            @Override
            public String getUserAgent() {
                return "punuo";
            }

            @Override
            public String getPrefixPath() {
                return "/xiaoyupeihu/public/index.php";
            }
        });
        HttpManager.setContext(app);
        HttpManager.init();

    }
}
