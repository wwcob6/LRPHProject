package com.punuo.sys.app.home.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import com.app.db.MyDatabaseHelper;
import com.app.model.MessageEvent;
import com.app.views.CleanEditText;
import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.punuo.sys.app.linphone.LinphoneHelper;
import com.punuo.sys.sdk.activity.BaseSwipeBackActivity;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

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

    private String type;
    private String call;
    private String avatorurl;
    @Autowired(name = "extra_name")
    String extra_name;
    @Autowired(name = "extra_phonenumber")
    String extra_phonenumber;
    @Autowired(name = "extra_avatorurl")
    String extra_avatorurl;
    private MyDatabaseHelper dbHelper;

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
        dbHelper = new MyDatabaseHelper(this, "member.db", null, 2);
        Intent intent = getIntent();
        extra_avatorurl = intent.getStringExtra("extra_avatorurl");
        extra_name = intent.getStringExtra("extra_name");
        extra_phonenumber = intent.getStringExtra("extra_phonenumber");
        edit_name.setText(extra_name);
        edit_number.setText(extra_phonenumber);

        if (!TextUtils.isEmpty(extra_avatorurl)) {
            Glide.with(this).load(extra_avatorurl).into(selectavator);
        }
        if (!TextUtils.isEmpty(extra_name) || !TextUtils.isEmpty(extra_phonenumber)) {
            add.setText("修改");
            delete.setVisibility(View.VISIBLE);
            callBtn.setVisibility(View.VISIBLE);
        } else {
            add.setText("添加");
            delete.setVisibility(View.GONE);
            callBtn.setVisibility(View.GONE);
        }
        add.setOnClickListener(v->{
            type = edit_name.getText().toString();
            call = edit_number.getText().toString();

            if (TextUtils.isEmpty(type)) {
                Toast.makeText(this, "联系人为空", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(call)) {
                Toast.makeText(this, "电话号码为空", Toast.LENGTH_SHORT).show();
            } else {
                if (extra_name == null || extra_phonenumber == null) {
                    dbHelper.getWritableDatabase();
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("avatorurl", avatorurl);
                    values.put("name", type);
                    values.put("phonenumber", call);
                    db.insert("Person", null, values);
                    values.clear();
                    ToastUtils.showToast("添加成功");
                } else {
                    add.setText("修改");
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.execSQL("delete from Person where name = ?", new String[]{extra_name});
                    dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    if (avatorurl != null) {
                        values.put("avatorurl", avatorurl);
                    } else {
                        values.put("avatorurl", extra_avatorurl);
                    }
                    values.put("name", type);
                    values.put("phonenumber", call);
                    db.insert("Person", null, values);
                    values.clear();
                    ToastUtils.showToast("修改成功");
                }
                EventBus.getDefault().post(new MessageEvent("addcompelete"));
                scrollToFinishActivity();
            }
        });

        delete.setOnClickListener(v -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("delete from Person where name = ?", new String[]{extra_name});
            EventBus.getDefault().post(new MessageEvent("addcompelete"));
            ToastUtils.showToast("删除成功");
            scrollToFinishActivity();
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinphoneHelper.getInstance().call(ContractManagerActivity.this, extra_phonenumber, false, 0);
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
                avatorurl = localMedia.getPath();
                Glide.with(this).load(avatorurl).into(selectavator);
            }
        }
    }

}

