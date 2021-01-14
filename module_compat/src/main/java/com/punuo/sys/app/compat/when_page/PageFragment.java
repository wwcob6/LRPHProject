package com.punuo.sys.app.compat.when_page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.punuo.sys.app.compat.R;
import com.punuo.sys.sdk.fragment.BaseFragment;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.util.ToastUtils;

/**
 * author：luck
 * project：AppWhenThePage
 * package：com.luck.app.page.when_page
 * email：893855882@qq.com
 * data：2017/2/22
 */
public class PageFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        int index = args.getInt("index");
        int layoutId = args.getInt("layoutId");
        int count = args.getInt("count");
        View rootView = inflater.inflate(layoutId, null);
        // 滑动到最后一页有点击事件
        if (index == count - 1) {
            rootView.findViewById(R.id.id_register).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.showToast("注册");
                    ARouter.getInstance().build(HomeRouter.ROUTER_REGISTER_ACTIVITY)
                            .navigation();
                }
            });
            rootView.findViewById(R.id.id_login).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtils.showToast("登陆");
                    ARouter.getInstance().build(HomeRouter.ROUTER_LOGIN_ACTIVITY)
                            .navigation();
                }
            });
        }

        return rootView;
    }
}

