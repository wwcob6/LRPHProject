package com.punuo.sys.app.home.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.app.R2;
import com.app.model.MessageEvent;
import com.app.views.CleanEditText;
import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.punuo.sys.app.home.db.ContractPerson;
import com.punuo.sys.app.home.db.ContractPerson_Table;
import com.punuo.sys.app.linphone.LinphoneHelper;
import com.punuo.sys.sdk.activity.BaseSwipeBackActivity;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.util.ToastUtils;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 联系人编辑/新增页面
 */
@Route(path = HomeRouter.ROUTER_CONTRACT_MANAGER_ACTIVITY)
public class ContractManagerActivity extends BaseSwipeBackActivity {
    @BindView(R2.id.call)
    Button callBtn;
    @BindView(R2.id.add)
    Button add;
    @BindView(R2.id.delete)
    Button delete;
    @BindView(R2.id.selectavator)
    ImageView selectavator;
    @BindView(R2.id.edit_name)
    CleanEditText edit_name;
    @BindView(R2.id.edit_number)
    CleanEditText edit_number;
    @BindView(R2.id.back)
    ImageView back;
    @BindView(R2.id.title)
    TextView title;

    private String mContractName;
    private String mContractPhoneNumber;
    private String mContractAvatarUrl;
    @Autowired(name = "contract_person")
    ContractPerson mContractPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_manager);
        ARouter.getInstance().inject(this);
        ButterKnife.bind(this);
        initView();
    }

    public void initView() {
        title.setText("修改信息");
        if (mContractPerson != null) {
            edit_name.setText(mContractPerson.name);
            edit_number.setText(mContractPerson.phoneNumber);
            Glide.with(this).load(mContractPerson.avatarUrl).into(selectavator);

            add.setText("修改");
            delete.setVisibility(View.VISIBLE);
            callBtn.setVisibility(View.VISIBLE);
        } else  {
            add.setText("添加");
            delete.setVisibility(View.GONE);
            callBtn.setVisibility(View.GONE);
        }
        add.setOnClickListener(v->{
            mContractName = edit_name.getText().toString();
            mContractPhoneNumber = edit_number.getText().toString();

            if (TextUtils.isEmpty(mContractName)) {
                Toast.makeText(this, "联系人为空", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(mContractPhoneNumber)) {
                Toast.makeText(this, "电话号码为空", Toast.LENGTH_SHORT).show();
            } else {
                if (mContractPerson == null) {
                    ContractPerson person = new ContractPerson();
                    person.id = UUID.randomUUID();
                    person.avatarUrl = mContractAvatarUrl;
                    person.name = mContractName;
                    person.phoneNumber = mContractPhoneNumber;
                    person.insert();
                    ToastUtils.showToast("添加成功");
                } else {
                    ContractPerson person = SQLite.select()
                            .from(ContractPerson.class)
                            .where(ContractPerson_Table.id.eq(mContractPerson.id))
                            .querySingle();
                    if (person != null) {
                        if (!TextUtils.isEmpty(mContractAvatarUrl)) {
                            person.avatarUrl = mContractAvatarUrl;
                        }
                        person.name = mContractName;
                        person.phoneNumber = mContractPhoneNumber;
                        person.update();
                        ToastUtils.showToast("修改成功");
                    }
                }
                EventBus.getDefault().post(new MessageEvent("addcompelete"));
                scrollToFinishActivity();
            }
        });

        delete.setOnClickListener(v -> {
            SQLite.delete().from(ContractPerson.class)
                    .where(ContractPerson_Table.id.eq(mContractPerson.id))
                    .execute();
            EventBus.getDefault().post(new MessageEvent("addcompelete"));
            ToastUtils.showToast("删除成功");
            scrollToFinishActivity();
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinphoneHelper.getInstance().call(ContractManagerActivity.this, mContractPerson.phoneNumber, false, 0);
            }
        });

        selectavator.setOnClickListener(v->{
            PictureSelector.create(ContractManagerActivity.this)
                    .openGallery(PictureMimeType.ofImage())
                    .imageSpanCount(4)
                    .selectionMode(PictureConfig.SINGLE)
                    .imageFormat(PictureMimeType.JPEG)
                    .forResult(PictureConfig.CHOOSE_REQUEST);
        });
        back.setOnClickListener(v-> scrollToFinishActivity());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                LocalMedia localMedia = selectList.get(0);
                mContractAvatarUrl = localMedia.getPath();
                Glide.with(this).load(mContractAvatarUrl).into(selectavator);
            }
        }
    }

}

