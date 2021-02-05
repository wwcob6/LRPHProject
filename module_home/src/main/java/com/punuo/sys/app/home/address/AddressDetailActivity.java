package com.punuo.sys.app.home.address;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.app.R2;
import com.app.model.AddressItem;
import com.app.request.AddAddressRequest;
import com.app.request.DeleteAddressRequest;
import com.app.request.UpdateAddressRequest;
import com.app.views.CleanEditText;
import com.hengyi.wheelpicker.listener.OnCityWheelComfirmListener;
import com.hengyi.wheelpicker.ppw.CityWheelPickerBottomSheetDialogFragment;
import com.punuo.sys.sdk.account.UserInfoManager;
import com.punuo.sys.sdk.activity.BaseSwipeBackActivity;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.model.PNBaseModel;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.util.RegexUtils;
import com.punuo.sys.sdk.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = HomeRouter.ROUTER_ADDRESS_DETAIL_ACTIVITY)
public class AddressDetailActivity extends BaseSwipeBackActivity {
    @BindView(R2.id.back)
    ImageView back;
    @BindView(R2.id.title)
    TextView title;
    @BindView(R2.id.tv_addressSelect)
    TextView tvAddressSelect;
    @BindView(R2.id.Rl_address)
    RelativeLayout RlAddress;
    @BindView(R2.id.et_detailAddress)
    CleanEditText etDetailAddress;
    @BindView(R2.id.et_userName)
    CleanEditText etUserName;
    @BindView(R2.id.et_userPhoneNum)
    CleanEditText etUserPhoneNum;
    @BindView(R2.id.box1)
    CheckBox box1;
    @BindView(R2.id.bt_addressSave)
    Button btAddressSave;
    @BindView(R2.id.rl_addressDelete)
    RelativeLayout rlAddressDelete;
    private boolean isDefault;

    @Autowired(name = "isEdit")
    boolean isEdit;

    @Autowired(name = "addressItem")
    AddressItem mAddressItem;

    private String userName;
    private String userPhoneNum;
    private String userAddress;
    private String detailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_address);
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.white));
        }
        if (isEdit) {
            title.setText("编辑地址");
            etUserName.setText(mAddressItem.userName);
            etUserPhoneNum.setText(mAddressItem.userPhoneNum);
            etDetailAddress.setText(mAddressItem.detailAddress);
            tvAddressSelect.setText(mAddressItem.userAddress);
            isDefault = mAddressItem.isDefault();
            box1.setChecked(isDefault);
        } else {
            title.setText("新增地址");
            rlAddressDelete.setVisibility(View.INVISIBLE);
        }
        box1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isDefault = isChecked;
            }
        });

        final CityWheelPickerBottomSheetDialogFragment wheelPickerPopupWindow = new CityWheelPickerBottomSheetDialogFragment();
        wheelPickerPopupWindow.setListener(new OnCityWheelComfirmListener() {
            @Override
            public void onSelected(String Province, String City, String District, String PostCode) {
                tvAddressSelect.setText(Province + " " + City + " " + District);
            }
        });

        RlAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wheelPickerPopupWindow.show(getSupportFragmentManager(), "CityWheelPickerBottomSheetDialogFragment");
            }
        });
    }

    @OnClick({R2.id.et_detailAddress, R2.id.et_userPhoneNum, R2.id.et_userName,
            R2.id.back, R2.id.bt_addressSave, R2.id.rl_addressDelete})
    public void onClick(View view) {
        int id = view.getId();
        userName = etUserName.getText().toString();
        userPhoneNum = etUserPhoneNum.getText().toString();
        userAddress = tvAddressSelect.getText().toString();
        detailAddress = etDetailAddress.getText().toString();
        if (id == R.id.et_detailAddress) {
            //nothing
        } else if (id == R.id.bt_addressSave) {
            if (!checkInput(userAddress, detailAddress, userName, userPhoneNum)) {
                return;
            }
            if (isEdit) {
                updateAddress();
            } else {
                saveAddress();
            }
        } else if (id == R.id.rl_addressDelete) {
            Dialog dialog = new AlertDialog.Builder(this)
                    .setMessage("确定要删除该地址吗？")
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteAddress();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .create();
            dialog.show();
        } else if (id == R.id.back) {
            scrollToFinishActivity();
        }
    }

    private DeleteAddressRequest mDeleteAddressRequest;

    private void deleteAddress() {
        if (mDeleteAddressRequest != null && !mDeleteAddressRequest.isFinish()) {
            return;
        }
        mDeleteAddressRequest = new DeleteAddressRequest();
        mDeleteAddressRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mDeleteAddressRequest.addUrlParam("position", mAddressItem.position);
        mDeleteAddressRequest.setRequestListener(new RequestListener<PNBaseModel>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(PNBaseModel result) {
                if (result == null) {
                    return;
                }
                if (result.isSuccess()) {
                    EventBus.getDefault().post(new AddressUpdateEvent());
                    finish();
                } else {
                    ToastUtils.showToast("删除失败，请重试");
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mDeleteAddressRequest);
    }

    private UpdateAddressRequest mUpdateAddressRequest;

    private void updateAddress() {
        if (mUpdateAddressRequest != null && !mUpdateAddressRequest.isFinish()) {
            return;
        }
        mUpdateAddressRequest = new UpdateAddressRequest();
        mUpdateAddressRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mUpdateAddressRequest.addUrlParam("userAddress", userAddress);
        mUpdateAddressRequest.addUrlParam("detailAddress", detailAddress);
        mUpdateAddressRequest.addUrlParam("userName", userName);
        mUpdateAddressRequest.addUrlParam("userPhoneNum", userPhoneNum);
        mUpdateAddressRequest.addUrlParam("position", mAddressItem.position);
        mUpdateAddressRequest.addUrlParam("isDefault", isDefault ? 1 : 2);
        mUpdateAddressRequest.setRequestListener(new RequestListener<PNBaseModel>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(PNBaseModel result) {
                if (result == null) {
                    return;
                }
                if (result.isSuccess()) {
                    EventBus.getDefault().post(new AddressUpdateEvent());
                    finish();
                } else {
                    ToastUtils.showToast("更新失败，请重试");
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mUpdateAddressRequest);
    }

    private AddAddressRequest mAddAddressRequest;

    private void saveAddress() {
        if (mAddAddressRequest != null && !mAddAddressRequest.isFinish()) {
            return;
        }
        mAddAddressRequest = new AddAddressRequest();
        mAddAddressRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mAddAddressRequest.addUrlParam("userAddress", userAddress);
        mAddAddressRequest.addUrlParam("detailAddress", detailAddress);
        mAddAddressRequest.addUrlParam("userName", userName);
        mAddAddressRequest.addUrlParam("userPhoneNum", userPhoneNum);
        mAddAddressRequest.addUrlParam("isDefault", isDefault ? 1 : 2);
        mAddAddressRequest.setRequestListener(new RequestListener<PNBaseModel>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(PNBaseModel result) {
                if (result == null) {
                    return;
                }
                if (result.isSuccess()) {
                    EventBus.getDefault().post(new AddressUpdateEvent());
                    finish();
                } else {
                    ToastUtils.showToast("保存失败，请重试");
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mAddAddressRequest);
    }

    private boolean checkInput(String userAddress, String detailAddress, String userName, String userPhoneNum) {
        if (TextUtils.isEmpty(userPhoneNum)) {
            ToastUtils.showToast("手机号码不能为空");
        } else if (!RegexUtils.checkMobile(userPhoneNum)) {
            ToastUtils.showToast("手机号码格式不正确");
        } else if (TextUtils.isEmpty(userName)) {
            ToastUtils.showToast("请输入收货人姓名");
        } else if (TextUtils.isEmpty(detailAddress)) {
            ToastUtils.showToast("请输入具体收货地址");
        } else if (TextUtils.isEmpty(userAddress)) {
            ToastUtils.showToast("请选择所在地区");
        } else {
            return true;
        }
        return false;
    }
}


