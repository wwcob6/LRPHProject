package com.app.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.R;
import com.app.R2;
import com.punuo.sys.sdk.activity.BaseSwipeBackActivity;
import com.punuo.sys.sdk.update.AutoUpdateService;
import com.punuo.sys.sdk.util.DeviceHelper;
import com.punuo.sys.sdk.util.IntentUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.image_bar));
        }
        tv_version.setText("v" + DeviceHelper.getVersionName());
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
