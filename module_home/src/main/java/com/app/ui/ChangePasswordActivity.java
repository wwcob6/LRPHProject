package com.app.ui;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.app.R;
import com.app.R2;
import com.punuo.sys.sdk.model.PNBaseModel;
import com.app.request.ChangePwdRequest;
import com.app.views.CleanEditText;
import com.punuo.sys.app.home.login.BaseSwipeBackLoginActivity;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author chzjy
 * Date 2016/12/19.
 * 修改密码页
 */

public class ChangePasswordActivity extends BaseSwipeBackLoginActivity {
    @BindView(R2.id.back)
    ImageView back;
    @BindView(R2.id.title)
    TextView title;
    @BindView(R2.id.oldpassword_input)
    CleanEditText oldpasswordInput;
    @BindView(R2.id.newpassword_input)
    CleanEditText newpasswordInput;
    @BindView(R2.id.newpassword_again)
    CleanEditText newpasswordAgain;
    @BindView(R2.id.btn_revise)
    Button btnRevise;
    @BindView(R2.id.layout_root)
    LinearLayout layoutRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frogetpwd);
        ButterKnife.bind(this);
        initViews();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.image_bar));
        }
    }


    private void initViews() {
        title.setText("修改密码");
        oldpasswordInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);// 下一步
        newpasswordInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);// 下一步
        newpasswordAgain.setImeOptions(EditorInfo.IME_ACTION_NEXT);// 下一步
        newpasswordAgain.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                // 点击虚拟键盘的done
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_GO) {
                    commit();
                }
                return false;
            }
        });
        back.setOnClickListener(v->{
            finish();
        });
        btnRevise.setOnClickListener(v->{
            commit();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //注册回调监听接口
    private void commit() {
        final String old = oldpasswordInput.getText().toString().trim();
        final String newPassword = newpasswordInput.getText().toString().trim();
        final String again = newpasswordAgain.getText().toString().trim();
        if (checkOldPassword(old) && checkPassWordValid(newPassword, again)) {
            changePwd(AccountManager.getUserAccount(), newPassword);
        }
    }

    private ChangePwdRequest mChangePwdRequest;

    private void changePwd(String telNum, String newPwd) {
        if (mChangePwdRequest != null && !mChangePwdRequest.isFinish()) {
            return;
        }
        mChangePwdRequest = new ChangePwdRequest();
        mChangePwdRequest.addUrlParam("tel_num", telNum);
        mChangePwdRequest.addUrlParam("password", newPwd);
        mChangePwdRequest.setRequestListener(new RequestListener<PNBaseModel>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(PNBaseModel result) {
                if (result.isSuccess()) {
                    ToastUtils.showToast("密码修改成功");
                    AccountManager.setPassword(newPwd);
                    finish();
                } else {
                    if (!TextUtils.isEmpty(result.msg)) {
                        ToastUtils.showToast(result.msg);
                    }
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mChangePwdRequest);
    }
}
