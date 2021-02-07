package com.punuo.sys.app.home.friendCircle.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.app.R;

/**
 * 右上点击出现列表pop
 *
 * @author GURR 2014-9-13
 */
public class MyCustomDialog extends Dialog {

    //定义回调事件，用于dialog的点击事件
    public interface OnCustomDialogListener {
        void back(String name);
    }

    private String name;
    private TextView title;
    private OnCustomDialogListener customDialogListener;
    private EditText etName;

    public MyCustomDialog(Context context, int theme, String name, OnCustomDialogListener customDialogListener) {
        super(context, theme);
        this.name = name;
        this.customDialogListener = customDialogListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.micro_comment);
        title = findViewById(R.id.tv_title);
        title.setText(name);
        etName = findViewById(R.id.microComment);
        findViewById(R.id.microSubmit).setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            customDialogListener.back(String.valueOf(etName.getText()));
            MyCustomDialog.this.dismiss();
        }
    };

}
