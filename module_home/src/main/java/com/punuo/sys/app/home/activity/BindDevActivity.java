package com.punuo.sys.app.home.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.punuo.sys.app.member.request.BindDevRequest;
import com.punuo.sys.app.member.request.IsDevBindRequest;
import com.punuo.sys.app.member.request.JoinDevRequest;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.account.UserInfoManager;
import com.punuo.sys.sdk.account.model.Group;
import com.punuo.sys.sdk.activity.BaseSwipeBackActivity;
import com.punuo.sys.sdk.activity.QRScanActivity;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.model.PNBaseModel;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@Route(path = HomeRouter.ROUTER_BIND_DEV_ACTIVITY)
public class BindDevActivity extends BaseSwipeBackActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_SCAN = 1;
    private View inflate;
    private TextView saoma;
    private TextView shoudong;
    private View back;
    private TextView title;
    private Dialog dialog;
    private TextView cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_dev);
        title = (TextView) findViewById(R.id.title);
        title.setText("绑定设备");
        back = findViewById(R.id.back);
        back.setOnClickListener(v -> scrollToFinishActivity());
        Button bindButton = (Button) findViewById(R.id.bt_bind);
        bindButton.setOnClickListener(v -> {
            try {
                show(v);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        if (!TextUtils.isEmpty(AccountManager.getBindDevId())) {
            ARouter.getInstance().build(HomeRouter.ROUTER_DEV_BIND_SUCCESS_ACTIVITY).navigation();
            finish();
        }
        // Check if we have write PermissionUtils
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have PermissionUtils so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        EventBus.getDefault().register(this);
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (resultCode == RESULT_OK) {
            if (data != null) {
                String text = data.getStringExtra("result");
                String[] dataString = text.split(" ");
                String devId = dataString[0];
                isDevBind(devId);
            }
        }
    }

    private void isDevBind(String devId) {
        IsDevBindRequest request = new IsDevBindRequest();
        request.addUrlParam("devid", devId);
        request.setRequestListener(new RequestListener<PNBaseModel>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(PNBaseModel result) {
                if (result != null) {
                    if (TextUtils.equals("已绑定", result.msg)) {
                        joinDev(devId);
                    } else if (TextUtils.equals("未绑定", result.msg)) {
                        bindDev(devId);
                    }
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(request);
    }

    private void joinDev(String devId) {
        JoinDevRequest request = new JoinDevRequest();
        request.addUrlParam("devid", devId);
        request.addUrlParam("id", UserInfoManager.getUserInfo().id);
        request.setRequestListener(new RequestListener<PNBaseModel>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(PNBaseModel result) {
                if (result != null) {
                    if (result.isSuccess()) {
                        ToastUtils.showToast("绑定成功");
                        AccountManager.getBindDevInfo();
                    } else {
                        ToastUtils.showToast(result.msg);
                    }
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(request);
    }

    private void bindDev(String devId) {
        BindDevRequest request = new BindDevRequest();
        request.addUrlParam("devid", devId);
        request.addUrlParam("id", UserInfoManager.getUserInfo().id);
        request.setRequestListener(new RequestListener<PNBaseModel>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(PNBaseModel result) {
                if (result != null) {
                    if (result.isSuccess()) {
                        ToastUtils.showToast("绑定成功");
                        AccountManager.getBindDevInfo();
                    } else {
                        ToastUtils.showToast(result.msg);
                    }
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(request);
    }

    public void show(View view) {
        dialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        //填充对话框的布局
        inflate = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null);
        //初始化控件
        saoma = (TextView) inflate.findViewById(R.id.tv_saoma);
        shoudong = (TextView) inflate.findViewById(R.id.tv_shoudong);
        cancel = (TextView) inflate.findViewById(R.id.cancel);
        saoma.setOnClickListener(this);
        shoudong.setOnClickListener(this);
        cancel.setOnClickListener(this);
        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        Display display = getWindowManager().getDefaultDisplay();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) display.getWidth();
        lp.y = 20;//设置Dialog距离底部的距离
//       将属性设置给窗体
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_saoma) {
            Intent intent = new Intent(this, QRScanActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SCAN);
        } else if (id == R.id.tv_shoudong) {
            final EditText editText = new EditText(this);
            new AlertDialog.Builder(this).setTitle("请输入设备号").setIcon(
                    android.R.drawable.ic_dialog_info).setView(editText
            ).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String devId = editText.getText().toString();
                    isDevBind(devId);
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            }).show();
        } else if (id == R.id.cancel) {

        }
        dialog.dismiss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Group event) {
        ARouter.getInstance().build(HomeRouter.ROUTER_DEV_BIND_SUCCESS_ACTIVITY).navigation();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

