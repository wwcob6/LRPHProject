package com.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.R;
import com.app.R2;
import com.punuo.sys.sdk.activity.BaseSwipeBackActivity;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.update.AutoUpdateService;
import com.punuo.sys.sdk.util.DeviceHelper;
import com.punuo.sys.sdk.util.IntentUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = HomeRouter.ROUTER_SOFTWARE_INSTRUCT_ACTIVITY)
public class SoftwareInstructActivity extends BaseSwipeBackActivity {

    @BindView(R2.id.tv_version)
    TextView tv_version;
    @BindView(R2.id.tv_introduct)
    TextView tvIntroduct;
    @BindView(R2.id.tv_update)
    TextView tvUpdate;
    @BindView(R2.id.back)
    ImageView back;
    @BindView(R2.id.title)
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_software_intruct);
        ButterKnife.bind(this);
        title.setText("关于");
        tv_version.setText("当前版本：v" + DeviceHelper.getVersionName());
    }

    @OnClick({R2.id.tv_introduct, R2.id.tv_update, R2.id.back})
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_introduct) {
            Toast.makeText(this, "该功能即将上线", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.tv_update) {
            Intent intent = new Intent(this, AutoUpdateService.class);
            intent.putExtra("needToast", true);
            IntentUtil.startServiceInSafeMode(this, intent);
        } else if (id == R.id.back) {
            scrollToFinishActivity();
        }
    }
}
