package com.app.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.R;
import com.app.R2;
import com.app.model.CallModel;
import com.app.request.ChangeCallRequest;
import com.app.request.GetCallRequest;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.model.PNBaseModel;
import com.punuo.sys.sdk.util.ToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by maojianhui on 2018/6/26.
 */

public class ServiceManagerActivity extends BaseActivity {

    @BindView(R2.id.et_call1)
    EditText et_call1;
    @BindView(R2.id.et_call2)
    EditText et_call2;
    @BindView(R2.id.et_call3)
    EditText et_call3;
    @BindView(R2.id.back)
    ImageView ivBack1;
    @BindView(R2.id.title)
    TextView titleset;

    @BindView(R2.id.setcall)
    Button setcall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_servicecall1);
        ButterKnife.bind(this);
        titleset.setText("服务电话");
        ivBack1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getServiceCall();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.image_bar));
        }

    }

    private GetCallRequest mGetCallRequest;
    public void getServiceCall(){
        if (mChangeCallRequest != null && !mChangeCallRequest.isFinish()) {
            return;
        }
        mGetCallRequest = new GetCallRequest();
        mGetCallRequest.addUrlParam("devid", AccountManager.getBindDevId());
        mGetCallRequest.setRequestListener(new RequestListener<List<CallModel>>() {
            @Override
            public void onComplete() {

            }
            @Override
            public void onSuccess(List<CallModel> result) {
                if (result == null ||result.isEmpty()) {
                    return;
                }
                CallModel data = result.get(0);
                String house = data.housekeep;
                String order = data.orderfood;
                String property = data.property;
                et_call1.setText(house);
                et_call2.setText(order);
                et_call3.setText(property);
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mGetCallRequest);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private ChangeCallRequest mChangeCallRequest;

    @OnClick({R2.id.setcall,R2.id.back})
    public void onViewClick1(View view) {
        int id = view.getId();
        if (id == R.id.setcall) {
            String HouseKeepingCall = et_call1.getText().toString();
            String OrderingCall = et_call2.getText().toString();
            String PropertyCall = et_call3.getText().toString();
            if (mChangeCallRequest != null && !mChangeCallRequest.isFinish()) {
                return;
            }
            mChangeCallRequest = new ChangeCallRequest();
//                mChangeCallRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
            mChangeCallRequest.addUrlParam("devid", AccountManager.getBindDevId());
            mChangeCallRequest.addUrlParam("housekeep", HouseKeepingCall);
            mChangeCallRequest.addUrlParam("orderfood", OrderingCall);
            mChangeCallRequest.addUrlParam("property", PropertyCall);
            mChangeCallRequest.setRequestListener(new RequestListener<PNBaseModel>() {
                @Override
                public void onComplete() {

                }

                @Override
                public void onSuccess(PNBaseModel result) {
                    if (result == null) {
                        return;
                    }
                    if (result.isSuccess()) {
                        ToastUtils.showToast("服务电话修改成功");
                        finish();
                    } else {
                        ToastUtils.showToast("服务电话修改失败");
                    }
                }

                @Override
                public void onError(Exception e) {

                }
            });
            HttpManager.addRequest(mChangeCallRequest);


        } else if (id == R.id.iv_back1) {
            finish();
        }
    }
}








