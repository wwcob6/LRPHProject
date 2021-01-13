package com.app.ui.message;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.R2;
import com.app.adapter.SystemNotifyAdapter;
import com.app.model.MessageNotify;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.punuo.sys.sdk.activity.BaseActivity;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.app.sip.SipInfo.messageNotifys;
import static com.app.sip.SipInfo.serverIp;

public class SystemNotify extends BaseActivity {

    private static final String TAG = "SystemNotify";
    @BindView(R2.id.rv_systemNotify)
    RecyclerView rvSystemNotify;
    GridLayoutManager glm;
//    private List<MessageNotifyActivity> notifyList=new ArrayList<>();
    private final String typepath = "http://" + serverIp + ":8000/xiaoyupeihu/public/index.php/posts/getNews";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_notify);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.newbackground));
        }

        EventBus.getDefault().register(this);
        putdata();
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        rvSystemNotify.setLayoutManager(layoutManager);


    }


    private Handler setNotifyHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 0x111:
                    initView();
            }
            return false;
        }
    });

    private void putdata() {
        sendRequestWithOkHttp();
    }

    private void sendRequestWithOkHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("1111", "run: ");
                    OkHttpClient client = new OkHttpClient();
                    Request request1 = new Request.Builder()
                            .url(typepath)
                            .build();
                    Log.d("1111", "run:1 " + client.newCall(request1).execute().body().string());

                    Response response = client.newCall(request1).execute();
                    String responseData = response.body().string();

                    parseJSONWithGSON(responseData);
                    Log.d("1111", "run:3 " + responseData);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
    private void parseJSONWithGSON(String responseData) {
//        Log.d("1111", "run:3 ");
        String jsonData = "[" + responseData.split("\\[")[1].split("\\]")[0] + "]";
        Log.d("3333", "run:2" + jsonData);
        Gson gson = new Gson();

        try {
            messageNotifys = gson.fromJson(jsonData, new TypeToken<List<MessageNotify>>(){}.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        Log.d("3333", "run:4" + messageNotifys);
        new Thread(new Runnable() {
            @Override
            public void run() {
                setNotifyHandler.sendEmptyMessage(0X111);
            }
        }).start();


    }

    private void initView() {
        SystemNotifyAdapter adapter=new SystemNotifyAdapter(messageNotifys);
        rvSystemNotify.setAdapter(adapter);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        finish();
    }
}
