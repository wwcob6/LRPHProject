package com.app.ui.address;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.R2;
import com.app.UserInfoManager;
import com.app.adapter.AddressItemAdapter;
import com.app.model.AddressResult;
import com.app.model.MessageEvent;
import com.app.request.GetAddressListRequest;
import com.app.sip.SipInfo;
import com.punuo.sys.sdk.activity.BaseSwipeBackActivity;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddressManagerActivity extends BaseSwipeBackActivity {
    @BindView(R2.id.back)
    ImageView back;
    @BindView(R2.id.title)
    TextView title;
    @BindView(R2.id.rv_addressDispaly)
    RecyclerView rvAddressDispaly;
    @BindView(R2.id.iv_addressicon)
    ImageView ivAddressicon;
    @BindView(R2.id.tv_noAddress)
    TextView tvNoAddress;
    @BindView(R2.id.btn_newAddress)
    Button btnNewAddress;

    private AddressItemAdapter mAddressItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_supervise);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        init();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvAddressDispaly.setLayoutManager(layoutManager);
        mAddressItemAdapter = new AddressItemAdapter(this, new ArrayList<>());
        rvAddressDispaly.setAdapter(mAddressItemAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.white));
        }

    }

    private void init() {
        title.setText("地址管理");
        getAddressList();
    }

    private GetAddressListRequest mGetAddressListRequest;

    //获取收货地址
    public void getAddressList() {
        if (mGetAddressListRequest != null && !mGetAddressListRequest.isFinish()) {
            return;
        }
        mGetAddressListRequest = new GetAddressListRequest();
        mGetAddressListRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mGetAddressListRequest.setRequestListener(new RequestListener<AddressResult>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(AddressResult result) {
                if (result == null) {
                    return;
                }
                if (result.mAddressItems == null || result.mAddressItems.isEmpty()) {
                    rvAddressDispaly.setVisibility(View.INVISIBLE);
                    ivAddressicon.setVisibility(View.VISIBLE);
                    tvNoAddress.setVisibility(View.VISIBLE);
                } else {
                    ivAddressicon.setVisibility(View.INVISIBLE);
                    tvNoAddress.setVisibility(View.INVISIBLE);
                    rvAddressDispaly.setVisibility(View.VISIBLE);
                    mAddressItemAdapter.appendData(result.mAddressItems);

                    SipInfo.addressList = result.mAddressItems;
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mGetAddressListRequest);
    }


    @OnClick({R2.id.btn_newAddress, R2.id.back})
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_newAddress) {
            SipInfo.isEditor = false;
            startActivity(new Intent(this, AddressDetailActivity.class));
        } else if (id == R.id.back) {
            scrollToFinishActivity();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getMessage().equals("编辑")) {
            SipInfo.isEditor = true;
            startActivity(new Intent(this, AddressDetailActivity.class));
        } else if (event.getMessage().equals("刷新")) {
            getAddressList();
        }
    }
}
