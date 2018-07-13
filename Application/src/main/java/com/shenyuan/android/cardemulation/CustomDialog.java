package com.shenyuan.android.cardemulation;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


public class CustomDialog extends Dialog {
    private EditText xingMingEditText;
    private EditText shengFenZhengHaoEditText;
    private EditText dangAnBianHaoEditText;
    private EditText zhuiJiaCheXingEditText;
    private EditText punishEditText;
    private TextView readStatus;
    private LinearLayout statusLinearLayout;
    private Button positiveButton, negativeButton;

    public CustomDialog(Context context) {
        super(context, R.style.CustomDialog);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.editlayout, null);
        statusLinearLayout = (LinearLayout) mView.findViewById(R.id.statusLayout);
        readStatus = (TextView) mView.findViewById(R.id.readStaus);
        xingMingEditText = (EditText) mView.findViewById(R.id.xingming);
        shengFenZhengHaoEditText = (EditText) mView.findViewById(R.id.shenfenzhenghao);
        dangAnBianHaoEditText = (EditText) mView.findViewById(R.id.danganbianhao);
        zhuiJiaCheXingEditText = (EditText) mView.findViewById(R.id.zhuijiachexing);
        punishEditText = (EditText) mView.findViewById(R.id.punishMode);
        positiveButton = (Button) mView.findViewById(R.id.positiveButton);
        negativeButton = (Button) mView.findViewById(R.id.negativeButton);
        super.setContentView(mView);
    }

    public View getEditText(int position) {
        switch (position) {
            case 0:
                return xingMingEditText;
            case 1:
                return shengFenZhengHaoEditText;
            case 2:
                return dangAnBianHaoEditText;
            case 3:
                return zhuiJiaCheXingEditText;
            case 4:
                return readStatus;
            case 5:
                return statusLinearLayout;
            case 6:
                return punishEditText;
            default:
                return xingMingEditText;
        }
    }

    @Override
    public void setContentView(int layoutResID) {
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
    }

    @Override
    public void setContentView(View view) {
    }

    /**
     * 确定键监听器
     *
     * @param listener
     */
    public void setOnPositiveListener(View.OnClickListener listener) {
        positiveButton.setOnClickListener(listener);
    }

    /**
     * 取消键监听器
     *
     * @param listener
     */
    public void setOnNegativeListener(View.OnClickListener listener) {
        negativeButton.setOnClickListener(listener);
    }
}