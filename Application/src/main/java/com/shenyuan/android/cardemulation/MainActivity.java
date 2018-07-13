/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


package com.shenyuan.android.cardemulation;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.shenyuan.android.sdk.CardService;
import com.shenyuan.android.sdk.DataOperater;
import com.shenyuan.android.sdk.DriverInfor;
import com.shenyuan.android.sdk.LPK130NW;
import com.shenyuan.android.sdk.PrinterObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;


/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p/>
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
public class MainActivity extends Activity {

    public static final String TAG = "MainActivity";
    // Whether the Log Fragment is currently shown
    public MyReceiver myReceiver = null;
    public Button editMode;
    public Button sendFirstlian;
    public Button sendSecondPages;
    public Button sendThirdlian;
    public Button readCard;
    public TextView logText;
    public EditText xmEdit = null;
    public EditText sfzhEdit = null;
    public EditText dabhEdit = null;
    public EditText zjcxEdit = null;
    public EditText punishEdit = null;
    private TextView readStatus;
    private LinearLayout statusLinearLayout;
    private boolean isOpenDialog = false;
    DriverInfor driverInfor = new DriverInfor();
    InputParameter inputParameter = new InputParameter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataOperater.setContentData(printDataFirst(driverInfor, inputParameter));
//        Intent intent = new Intent(MainActivity.this, CardService.class);
//        startService(intent);//需要启动自定义service
        editMode = (Button) findViewById(R.id.editMode);
        logText = (TextView) findViewById(R.id.logText);
        editMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataOperater.currentMode = DataOperater.READ_MODE;
                dialog();
            }
        });
        sendFirstlian = (Button) findViewById(R.id.firstlian);
        sendSecondPages = (Button) findViewById(R.id.sencondlian);
        sendThirdlian = (Button) findViewById(R.id.thirdlian);
        readCard = (Button) findViewById(R.id.readCard);
        sendFirstlian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataOperater.setContentData(printDataFirst(driverInfor, inputParameter));
                DataOperater.currentMode = DataOperater.PRINT_MODE;
            }
        });
        sendSecondPages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataOperater.setContentData(printDataSecond(driverInfor, inputParameter));
                DataOperater.currentMode = DataOperater.PRINT_MODE;
            }
        });
        sendThirdlian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataOperater.setContentData(printDataThird(driverInfor, inputParameter));
                DataOperater.currentMode = DataOperater.PRINT_MODE;
            }
        });
        readCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataOperater.currentMode = DataOperater.READ_MODE;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (myReceiver == null) {
            myReceiver = new MyReceiver();
        }
//        Log.e("myReceiver==", myReceiver.toString());
        IntentFilter filter = new IntentFilter();// 创建IntentFilter对象
        filter.addAction(DataOperater.broadcastAction);
        registerReceiver(myReceiver, filter);// 注册Broadcast Receiver
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.button:
                logText.setText("");
        }
        return super.onOptionsItemSelected(item);
    }


    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("myReceiver==","进入BroadcastReceiver");
            int status = intent.getIntExtra("action", -1);
            String action = intent.getAction();
            if (DataOperater.broadcastAction.equals(action))
                switch (status) {
                    case DataOperater.SEND_START:
                        logText.setText("数据正在传输，请勿将手机离开!");
                        break;
                    case DataOperater.SEND_OVER:
                        logText.setText("数据传输完毕！");
                        VibratorUtil.Vibrate(MainActivity.this, 300);
                        break;
                    case DataOperater.SEND_ERROR:
                        if (isOpenDialog) {
                            statusLinearLayout.setVisibility(View.VISIBLE);
                            readStatus.setText("通信中断，请重新将手机贴近！");
                        } else {
                            logText.setText("通信中断，请重新将手机贴近！");
                        }
                        break;
                    case DataOperater.READ_START:
                        if (!isOpenDialog) {
                            logText.setText("正在读取卡片,请勿将手机离开!");
                        } else {
                            statusLinearLayout.setVisibility(View.VISIBLE);
                            readStatus.setText("正在读取卡片，请勿将手机离开!");
                        }
                        break;
                    case DataOperater.READ_ERROR:
                        if (isOpenDialog) {
                            statusLinearLayout.setVisibility(View.VISIBLE);
                            readStatus.setText("卡片不存在或未接触良好，请确认！");
                        } else {
                            logText.setText("卡片不存在或未接触良好，请确认！");
                        }
                        break;
                    case DataOperater.READ_OVER:
                        if (isOpenDialog) {
                            statusLinearLayout.setVisibility(View.VISIBLE);
                            readStatus.setText("卡片信息读取完毕！");
                        }
                        VibratorUtil.Vibrate(MainActivity.this, 300);
                        break;
                    case DataOperater.XINGMING:
                        String xingming = intent.getStringExtra("information");
                        if (isOpenDialog) {
                            xmEdit.setText(xingming);
                            // xmEdit.setText("姓名");
                        } else {
                            logText.setText("姓        名：" + xingming + "\r\n");
                        }
                        break;
                    case DataOperater.SFZH:
                        String sfzh = intent.getStringExtra("information");
                        if (isOpenDialog) {
                            sfzhEdit.setText(sfzh);
                            // sfzhEdit.setText("身份证号");
                        } else {
                            logText.append("身份证号：" + sfzh + "\r\n");
                        }
                        break;
                    case DataOperater.DABH:
                        String dabh = intent.getStringExtra("information");
                        if (isOpenDialog) {
                            dabhEdit.setText(dabh);
                            // dabhEdit.setText("档案编号");
                        } else {
                            logText.append("档案编号：" + dabh + "\r\n");
                        }
                        break;
                    case DataOperater.ZJCX://准驾车型
                        String zjcx = intent.getStringExtra("information");
                        if (isOpenDialog) {
                            zjcxEdit.setText(zjcx);
                            //  zjcxEdit.setText("准驾车型");
                        } else {
                            logText.append("准驾车型：" + zjcx);
                        }
                        break;
                    case DataOperater.LZRQ:
                        break;

                }
        }
    }

    // 弹窗
    private void dialog() {
        isOpenDialog = true;
        final CustomDialog dialog = new CustomDialog(MainActivity.this);
        dialog.setTitle("编辑模板中驾驶员信息");
        dialog.setCancelable(false);
        xmEdit = (EditText) dialog.getEditText(0);
        sfzhEdit = (EditText) dialog.getEditText(1);
        dabhEdit = (EditText) dialog.getEditText(2);
        zjcxEdit = (EditText) dialog.getEditText(3);
        readStatus = (TextView) dialog.getEditText(4);
        statusLinearLayout = (LinearLayout) dialog.getEditText(5);
        punishEdit = (EditText) dialog.getEditText(6);
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataOperater.currentMode = DataOperater.PRINT_MODE;
                isOpenDialog = false;
                driverInfor.setXingming(xmEdit.getText().toString());
                driverInfor.setShengfenzheng(sfzhEdit.getText().toString());
                driverInfor.setDanganbianhao(dabhEdit.getText().toString());
                driverInfor.setZhunjiachexing(zjcxEdit.getText().toString());
                inputParameter.punishMode = 0x00;
                String temp = punishEdit.getText().toString();
                if (temp.indexOf("扣留") != -1) {
                    inputParameter.punishMode |= 0x02;
                }
                if (temp.indexOf("罚款") != -1) {
                    inputParameter.punishMode |= 0x01;
                }
                DataOperater.setContentData(printDataFirst(driverInfor, inputParameter));

                dialog.dismiss();
            }
        });
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOpenDialog = false;
                DataOperater.currentMode = DataOperater.PRINT_MODE;
                DataOperater.setContentData(printDataFirst(driverInfor, inputParameter));
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public PrinterObject printDataFirst(DriverInfor driverInfor, InputParameter parameter) {
        int offset = 100;
        int offset1 = 150;
        int lineHeight = 2;
        int fontHeight7 = 128;
        int fontHeight6 = 96;
        int fontHeight5 = 64;
        int fontHeight4 = 48;
        int fontHeight3 = 32;
        int fontHeight2 = 24;
        int fontHeight1 = 16;
        int currentY = 5;
        int oldY = 0;
        int oldY1 = 0;
        int spaceHeight = 10;
        int startYS = 0;
        int endYS = 0;
        int startXH = 15;
        int endXH = 566;
        LPK130NW lpk130nw = new LPK130NW();
        LPK130NW.Format format = lpk130nw.new Format(2, 0, 0, false, false);
        LPK130NW.Format format1 = lpk130nw.new Format(2, 0, 0, true, false);
        LPK130NW.Format format2 = lpk130nw.new Format(3, 0, 1, false, false);
        PrinterObject po = new PrinterObject();
        InputStream is = this.getResources().openRawResource(R.raw.logo);
        BitmapDrawable bmpDraw = new BitmapDrawable(is);
        Bitmap bmp = bmpDraw.getBitmap();
        byte[] tempBytes = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        po.add(tempBytes);
        //创建页面并清除缓存
        po.add(lpk130nw.NFCP_createPage_Clear(576, 2200, 1));
//        po.add(lpk130nw.NFCP_createPage_Clear(576, 2200, 0));
        currentY = 100;
        try {
            po.add(lpk130nw.NFCP_Page_setText((576 - fontHeight2 * 16) / 2, currentY, "上海市公安局交通警察总队机动支队", 2, 0, 1, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setText((576 - fontHeight2 * 15) / 2, currentY, "公安交通管理简易程序处罚决定书", 2, 0, 1, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setText((576 - fontHeight2 * 7) / 2, currentY, "第一联（留档）", 2, 0, 0, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_drawBar(56, currentY, "3103023700852637", 0, 0, 2, 80));
            currentY += 80;
            po.add(lpk130nw.NFCP_Page_setText((576 - fontHeight2 * 8) / 2, currentY, "3103023700852637", 2, 0, 0, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setText(0, currentY, "编号：" + parameter.identifier + "    执法机关代码：" + parameter.authorityCode, 2, 0, 0, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                    lpk130nw.new Parameter(format, "被处罚人："),
                    lpk130nw.new Parameter(format1, driverInfor.getXingming())));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                    lpk130nw.new Parameter(format, "机动车驾驶证档案编号："),
                    lpk130nw.new Parameter(format1, driverInfor.getDanganbianhao())));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                    lpk130nw.new Parameter(format, "证芯编号："),
                    lpk130nw.new Parameter(format1, parameter.cardNumber)));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setText(0, currentY, "机动车驾驶证", 2, 0, 0, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                    lpk130nw.new Parameter(format, "或居民身份证号码："),
                    lpk130nw.new Parameter(format1, driverInfor.getShengfenzheng())));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                    lpk130nw.new Parameter(format, "准驾车型："),
                    lpk130nw.new Parameter(format1, driverInfor.getZhunjiachexing())));
            currentY += fontHeight2;
            currentY += spaceHeight;
            if (parameter.contactInformation.length() > 19) {
                po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 * 2 + spaceHeight * 2,
                        lpk130nw.new Parameter(format, "联系方式："),
                        lpk130nw.new Parameter(format1, parameter.contactInformation)));
                currentY += fontHeight2 * 2;
                currentY += spaceHeight * 2;
            } else {
                po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                        lpk130nw.new Parameter(format, "联系方式："),
                        lpk130nw.new Parameter(format1, parameter.contactInformation)));
                currentY += fontHeight2;
                currentY += spaceHeight;
            }
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                    lpk130nw.new Parameter(format, "车辆牌号："),
                    lpk130nw.new Parameter(format1, parameter.carBrand)));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                    lpk130nw.new Parameter(format, "车辆类型："),
                    lpk130nw.new Parameter(format1, parameter.carType)));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 * 4 + spaceHeight * 4,
                    lpk130nw.new Parameter(format, "发证机关："),
                    lpk130nw.new Parameter(format1, parameter.ssuingAuthority)));
            currentY += fontHeight2 * 2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 * 4 + spaceHeight * 4,
                    lpk130nw.new Parameter(format, "    被处罚人于"),
                    lpk130nw.new Parameter(format1, String.valueOf(parameter.year)),
                    lpk130nw.new Parameter(format, "年"),
                    lpk130nw.new Parameter(format1, String.format("%02d", parameter.month)),
                    lpk130nw.new Parameter(format, "月"),
                    lpk130nw.new Parameter(format1, String.format("%02d", parameter.date)),
                    lpk130nw.new Parameter(format, "日"),
                    lpk130nw.new Parameter(format1, String.format("%02d", parameter.hour)),
                    lpk130nw.new Parameter(format, "时"),
                    lpk130nw.new Parameter(format1, String.format("%02d", parameter.minute)),
                    lpk130nw.new Parameter(format, "分，在"),
                    lpk130nw.new Parameter(format1, parameter.peccancyLocale),
                    lpk130nw.new Parameter(format, "实施" + parameter.peccancyContent + "的代码：（"),
                    lpk130nw.new Parameter(format1, parameter.peccancyCode),
                    lpk130nw.new Parameter(format, ")，违反了" + parameter.peccancyLaw + "。")));
            currentY += fontHeight2 * 5;
            currentY += spaceHeight * 5;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 * 2 + spaceHeight * 2, lpk130nw.new Parameter(format, "    根据" + parameter.punishLaw + "，决定给予：")));
            currentY += fontHeight2 * 3;
            currentY += spaceHeight * 2;
            parameter.punishContent = "";
            if ((parameter.punishMode & 0x01) == 0x01) {
                parameter.punishContent += (parameter.fineLineCapital + "圆（￥" + parameter.fineLinelowercase + "）罚款");
            }
            if ((parameter.punishMode & 0x02) == 0x02) {
                parameter.punishContent += " 扣留机动车";
            }
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight3 + spaceHeight, lpk130nw.new Parameter(format2, parameter.punishContent)));
            currentY += (fontHeight2 + fontHeight3);
            currentY += spaceHeight;
            if ((parameter.punishMode & 0x01) == 0x01) {
                po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 * 2 + spaceHeight * 2,
                        lpk130nw.new Parameter(format, "    持本决定书在15日内到通过全国任意工商银行罚款（咨询电话：95588）。")));
                currentY += fontHeight2 * 2;
                currentY += spaceHeight * 2;

                po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                        lpk130nw.new Parameter(format, "    逾期不缴纳的，每日按罚款数额的3%加处罚款。")));
                currentY += fontHeight2;
                currentY += spaceHeight;
            }
            if ((parameter.punishMode & 0x02) == 0x02) {
                po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 * 3 + spaceHeight * 2,
                        lpk130nw.new Parameter(format, "    请在15日内到"),
                        lpk130nw.new Parameter(format1, "（地点：古宜路131号，时间：周一至周六9：00--17：00）"),
                        lpk130nw.new Parameter(format, "接受处理。逾期不处理的，依法承担法律责任。")));
                currentY += fontHeight2 * 3;
                currentY += spaceHeight * 3;
            }
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 * 3 + spaceHeight * 3,
                    lpk130nw.new Parameter(format, "    如不服本决定的，可以在收到本决定书之日起60日内向"),
                    lpk130nw.new Parameter(format1, parameter.reviewCompany),
                    lpk130nw.new Parameter(format, "申请行政复议；后者在6个月内向"),
                    lpk130nw.new Parameter(format1, parameter.litigationCompany),
                    lpk130nw.new Parameter(format, "提起行政诉讼。")
            ));
            currentY += fontHeight2 * 4;
            currentY += spaceHeight * 4;
            if ((parameter.punishMode & 0x01) == 0x01) {
                po.add(lpk130nw.NFCP_Page_setText(0, currentY, "处罚地点：" + parameter.punishLocale, 2, 0, 0, false, false));
            }
            currentY += (fontHeight2 + spaceHeight);
           /* po.add(lpk130nw.NFCP_printPage(0, 2));
            po.add(lpk130nw.NFCP_createPage(576, 800));*/
            po.add(lpk130nw.NFCP_Page_location(2, currentY));
            currentY += 150;
            po.add(lpk130nw.NFCP_Page_setText(0, currentY, "警号：" + parameter.policeNumber, 2, 0, 0, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setText(0, currentY, "交通警察:", 2, 0, 0, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setText(215, currentY, "上海市公安局交通警察总队", 2, 0, 0, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setText(310, currentY, "机动支队", 2, 0, 0, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setText(230, currentY, "日期：" +
                    parameter.year +
                    "年" + String.format("%02d", parameter.month) +
                    "月" + String.format("%02d", parameter.date) +
                    "日", 2, 0, 0, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setText(0, currentY, "被处罚人签名", 2, 0, 0, false, false));
            currentY += fontHeight2 * 4;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                    lpk130nw.new Parameter(format, "签名日期："),
                    lpk130nw.new Parameter(format1, "        "),
                    lpk130nw.new Parameter(format, "年"),
                    lpk130nw.new Parameter(format1, "    "),
                    lpk130nw.new Parameter(format, "月"),
                    lpk130nw.new Parameter(format1, "    "),
                    lpk130nw.new Parameter(format, "日")
            ));
            currentY += fontHeight2;
            currentY += spaceHeight;
            if ((parameter.punishMode & 0x02) == 0x02) {
                po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                        lpk130nw.new Parameter(format, "当事人对本凭证记载内容有无异议：")));
                currentY += fontHeight2;
                currentY += spaceHeight;
                po.add(lpk130nw.NFCP_Page_drawRectangle(2, 2, currentY + 2, 22, currentY + 22));
                po.add(lpk130nw.NFCP_Page_setText(26, currentY, "有异议", 2, 0, 0, false, false));
                po.add(lpk130nw.NFCP_Page_drawRectangle(2, fontHeight2 * 11, currentY + 2, fontHeight2 * 11 + 20, currentY + 22));
                po.add(lpk130nw.NFCP_Page_setText(fontHeight2 * 12, currentY, "无异议", 2, 0, 0, false, false));
                currentY += fontHeight2;
                currentY += spaceHeight;
            }
            po.add(lpk130nw.NFCP_Page_setText(0, currentY, "备注：", 2, 0, 0, false, false));
            currentY += fontHeight2 * 3;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_drawLine(0, currentY, 576, currentY));
            currentY += fontHeight2;
            currentY += spaceHeight;
            if ((parameter.punishMode & 0x01) == 0x01) {
                po.add(lpk130nw.NFCP_Page_setText(0, currentY, "根据《机动车驾驶证申领和使用规定》记" + parameter.punishScore + "分", 2, 0, 0, false, false));
                currentY += fontHeight2;
                currentY += spaceHeight;
                po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 * 2 + spaceHeight * 2,
                        lpk130nw.new Parameter(format, "注：未作罚款处罚或者当场收缴罚款的，银行联不交被处罚人。")));
                currentY += fontHeight2 * 2;
                currentY += spaceHeight * 2;
            }
            if ((parameter.punishMode & 0x02) == 0x02) {
                po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 * 2 + spaceHeight * 2,
                        lpk130nw.new Parameter(format, "本凭证同时作为现场笔录")));
                currentY += fontHeight2;
                currentY += spaceHeight;
            }
            po.add(lpk130nw.NFCP_printPage(0, 1));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
       /* po.add(lpk130nw.NFCP_createPage_Clear(576, 2200, 0));
        currentY = 100;
        currentY += fontHeight2 * 2;
        currentY += spaceHeight * 2;
        try {
            po.add(lpk130nw.NFCP_Page_setText((576 - fontHeight2 * 7) / 2, currentY, "第二联（交当事人）", 2, 0, 0, false, false));
            po.add(lpk130nw.NFCP_printPage(0, 1));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        po.add(lpk130nw.NFCP_createPage_Clear(576, 2200, 0));
        currentY = 100;
        currentY += fontHeight2 * 2;
        currentY += spaceHeight * 2;
        try {
            po.add(lpk130nw.NFCP_Page_setText((576 - fontHeight2 * 7) / 2, currentY, "第三联（交银行）", 2, 0, 0, false, false));
            po.add(lpk130nw.NFCP_printPage(0, 1));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/

        bmp.recycle();
        bmp = null;
        return po;
    }

    public PrinterObject printDataSecond(DriverInfor driverInfor, InputParameter parameter) {
        int offset = 100;
        int offset1 = 150;
        int lineHeight = 2;
        int fontHeight7 = 128;
        int fontHeight6 = 96;
        int fontHeight5 = 64;
        int fontHeight4 = 48;
        int fontHeight3 = 32;
        int fontHeight2 = 24;
        int fontHeight1 = 16;
        int currentY = 5;
        int oldY = 0;
        int oldY1 = 0;
        int spaceHeight = 10;
        int startYS = 0;
        int endYS = 0;
        int startXH = 15;
        int endXH = 566;
        LPK130NW lpk130nw = new LPK130NW();
        LPK130NW.Format format = lpk130nw.new Format(2, 0, 0, false, false);
        LPK130NW.Format format1 = lpk130nw.new Format(2, 0, 0, true, false);
        LPK130NW.Format format2 = lpk130nw.new Format(3, 0, 1, false, false);
        PrinterObject po = new PrinterObject();
        InputStream is = this.getResources().openRawResource(R.raw.logo);
        BitmapDrawable bmpDraw = new BitmapDrawable(is);
        Bitmap bmp = bmpDraw.getBitmap();
        byte[] tempBytes = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        po.add(tempBytes);
        po.add(lpk130nw.NFCP_createPage(576, 1400));
        currentY = 100;
        try {
            po.add(lpk130nw.NFCP_Page_setText((576 - fontHeight2 * 16) / 2, currentY, "上海市公安局交通警察总队机动支队", 2, 0, 1, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setText((576 - fontHeight2 * 15) / 2, currentY, "公安交通管理简易程序处罚决定书", 2, 0, 1, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setText((576 - fontHeight2 * 7) / 2, currentY, "第二联（交当事人）", 2, 0, 0, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_drawBar(56, currentY, "3103023700852637", 0, 0, 2, 80));
            currentY += 80;
            po.add(lpk130nw.NFCP_Page_setText((576 - fontHeight2 * 8) / 2, currentY, "3103023700852637", 2, 0, 0, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setText(0, currentY, "编号：" + parameter.identifier + "    执法机关代码：" + parameter.authorityCode, 2, 0, 0, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                    lpk130nw.new Parameter(format, "被处罚人："),
                    lpk130nw.new Parameter(format1, driverInfor.getXingming())));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                    lpk130nw.new Parameter(format, "机动车驾驶证档案编号："),
                    lpk130nw.new Parameter(format1, driverInfor.getDanganbianhao())));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                    lpk130nw.new Parameter(format, "证芯编号："),
                    lpk130nw.new Parameter(format1, parameter.cardNumber)));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setText(0, currentY, "机动车驾驶证", 2, 0, 0, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                    lpk130nw.new Parameter(format, "或居民身份证号码："),
                    lpk130nw.new Parameter(format1, driverInfor.getShengfenzheng())));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                    lpk130nw.new Parameter(format, "准驾车型："),
                    lpk130nw.new Parameter(format1, driverInfor.getZhunjiachexing())));
            currentY += fontHeight2;
            currentY += spaceHeight;
            if (parameter.contactInformation.length() > 19) {
                po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 * 2 + spaceHeight * 2,
                        lpk130nw.new Parameter(format, "联系方式："),
                        lpk130nw.new Parameter(format1, parameter.contactInformation)));
                currentY += fontHeight2 * 2;
                currentY += spaceHeight * 2;
            } else {
                po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                        lpk130nw.new Parameter(format, "联系方式："),
                        lpk130nw.new Parameter(format1, parameter.contactInformation)));
                currentY += fontHeight2;
                currentY += spaceHeight;
            }
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                    lpk130nw.new Parameter(format, "车辆牌号："),
                    lpk130nw.new Parameter(format1, parameter.carBrand)));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                    lpk130nw.new Parameter(format, "车辆类型："),
                    lpk130nw.new Parameter(format1, parameter.carType)));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 * 4 + spaceHeight * 4,
                    lpk130nw.new Parameter(format, "发证机关："),
                    lpk130nw.new Parameter(format1, parameter.ssuingAuthority)));
            currentY += fontHeight2 * 2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 * 4 + spaceHeight * 4,
                    lpk130nw.new Parameter(format, "    被处罚人于"),
                    lpk130nw.new Parameter(format1, String.valueOf(parameter.year)),
                    lpk130nw.new Parameter(format, "年"),
                    lpk130nw.new Parameter(format1, String.format("%02d", parameter.month)),
                    lpk130nw.new Parameter(format, "月"),
                    lpk130nw.new Parameter(format1, String.format("%02d", parameter.date)),
                    lpk130nw.new Parameter(format, "日"),
                    lpk130nw.new Parameter(format1, String.format("%02d", parameter.hour)),
                    lpk130nw.new Parameter(format, "时"),
                    lpk130nw.new Parameter(format1, String.format("%02d", parameter.minute)),
                    lpk130nw.new Parameter(format, "分，在"),
                    lpk130nw.new Parameter(format1, parameter.peccancyLocale),
                    lpk130nw.new Parameter(format, "实施" + parameter.peccancyContent + "的代码：（"),
                    lpk130nw.new Parameter(format1, parameter.peccancyCode),
                    lpk130nw.new Parameter(format, ")，违反了" + parameter.peccancyLaw + "。")));
            currentY += fontHeight2 * 5;
            currentY += spaceHeight * 5;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 * 2 + spaceHeight * 2, lpk130nw.new Parameter(format, "    根据" + parameter.punishLaw + "，决定给予：")));
            currentY += fontHeight2 * 3;
            currentY += spaceHeight * 2;
            parameter.punishContent = "";
            if ((parameter.punishMode & 0x01) == 0x01) {
                parameter.punishContent += (parameter.fineLineCapital + "圆（￥" + parameter.fineLinelowercase + "）罚款");
            }
            if ((parameter.punishMode & 0x02) == 0x02) {
                parameter.punishContent += " 扣留机动车";
            }
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight3 + spaceHeight, lpk130nw.new Parameter(format2, parameter.punishContent)));
            currentY += (fontHeight2 + fontHeight3);
            currentY += spaceHeight;
            if ((parameter.punishMode & 0x01) == 0x01) {
                po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 * 2 + spaceHeight * 2,
                        lpk130nw.new Parameter(format, "    持本决定书在15日内到通过全国任意工商银行罚款（咨询电话：95588）。")));
                currentY += fontHeight2 * 2;
                currentY += spaceHeight * 2;
                po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                        lpk130nw.new Parameter(format, "    逾期不缴纳的，每日按罚款数额的3%加处罚款。")));
                currentY += fontHeight2;
                currentY += spaceHeight;
            }
            if ((parameter.punishMode & 0x02) == 0x02) {
                po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 * 3 + spaceHeight * 2,
                        lpk130nw.new Parameter(format, "    请在15日内到"),
                        lpk130nw.new Parameter(format1, "（地点：古宜路131号，时间：周一至周六9：00--17：00）"),
                        lpk130nw.new Parameter(format, "接受处理。逾期不处理的，依法承担法律责任。")));
                currentY += fontHeight2 * 3;
                currentY += spaceHeight * 3;
            }
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 * 3 + spaceHeight * 3,
                    lpk130nw.new Parameter(format, "    如不服本决定的，可以在收到本决定书之日起60日内向"),
                    lpk130nw.new Parameter(format1, parameter.reviewCompany),
                    lpk130nw.new Parameter(format, "申请行政复议；后者在6个月内向"),
                    lpk130nw.new Parameter(format1, parameter.litigationCompany),
                    lpk130nw.new Parameter(format, "提起行政诉讼。")
            ));
            currentY += fontHeight2 * 4;
            currentY += spaceHeight * 4;
            if ((parameter.punishMode & 0x01) == 0x01) {
                po.add(lpk130nw.NFCP_Page_setText(0, currentY, "处罚地点：" + parameter.punishLocale, 2, 0, 0, false, false));
            }
            po.add(lpk130nw.NFCP_printPage(0, 2));
            po.add(lpk130nw.NFCP_createPage(576, 800));
            currentY = 150;
            po.add(lpk130nw.NFCP_Page_setText(0, currentY, "警号：" + parameter.policeNumber, 2, 0, 0, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setText(0, currentY, "交通警察:", 2, 0, 0, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setText(215, currentY, "上海市公安局交通警察总队", 2, 0, 0, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setText(310, currentY, "机动支队", 2, 0, 0, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setText(230, currentY, "日期：" +
                    parameter.year +
                    "年" + String.format("%02d", parameter.month) +
                    "月" + String.format("%02d", parameter.date) +
                    "日", 2, 0, 0, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setText(0, currentY, "被处罚人签名", 2, 0, 0, false, false));
            currentY += fontHeight2 * 4;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                    lpk130nw.new Parameter(format, "签名日期："),
                    lpk130nw.new Parameter(format1, "        "),
                    lpk130nw.new Parameter(format, "年"),
                    lpk130nw.new Parameter(format1, "    "),
                    lpk130nw.new Parameter(format, "月"),
                    lpk130nw.new Parameter(format1, "    "),
                    lpk130nw.new Parameter(format, "日")
            ));
            currentY += fontHeight2;
            currentY += spaceHeight;
            if ((parameter.punishMode & 0x02) == 0x02) {
                po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                        lpk130nw.new Parameter(format, "当事人对本凭证记载内容有无异议：")));
                currentY += fontHeight2;
                currentY += spaceHeight;
                po.add(lpk130nw.NFCP_Page_drawRectangle(2, 2, currentY + 2, 22, currentY + 22));
                po.add(lpk130nw.NFCP_Page_setText(26, currentY, "有异议", 2, 0, 0, false, false));
                po.add(lpk130nw.NFCP_Page_drawRectangle(2, fontHeight2 * 11, currentY + 2, fontHeight2 * 11 + 20, currentY + 22));
                po.add(lpk130nw.NFCP_Page_setText(fontHeight2 * 12, currentY, "无异议", 2, 0, 0, false, false));
                currentY += fontHeight2;
                currentY += spaceHeight;
            }
            po.add(lpk130nw.NFCP_Page_setText(0, currentY, "备注：", 2, 0, 0, false, false));
            currentY += fontHeight2 * 3;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_drawLine(0, currentY, 576, currentY));
            currentY += fontHeight2;
            currentY += spaceHeight;
            if ((parameter.punishMode & 0x01) == 0x01) {
                po.add(lpk130nw.NFCP_Page_setText(0, currentY, "根据《机动车驾驶证申领和使用规定》记" + parameter.punishScore + "分", 2, 0, 0, false, false));
                currentY += fontHeight2;
                currentY += spaceHeight;
                po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 * 2 + spaceHeight * 2,
                        lpk130nw.new Parameter(format, "注：未作罚款处罚或者当场收缴罚款的，银行联不交被处罚人。")));
                currentY += fontHeight2 * 2;
                currentY += spaceHeight * 2;
            }
            if ((parameter.punishMode & 0x02) == 0x02) {
                po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 * 2 + spaceHeight * 2,
                        lpk130nw.new Parameter(format, "本凭证同时作为现场笔录")));
                currentY += fontHeight2;
                currentY += spaceHeight;
            }
            po.add(lpk130nw.NFCP_printPage(0, 1));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        bmp.recycle();
        bmp = null;
        return po;
    }

    public PrinterObject printDataThird(DriverInfor driverInfor, InputParameter parameter) {
        int offset = 100;
        int offset1 = 150;
        int lineHeight = 2;
        int fontHeight7 = 128;
        int fontHeight6 = 96;
        int fontHeight5 = 64;
        int fontHeight4 = 48;
        int fontHeight3 = 32;
        int fontHeight2 = 24;
        int fontHeight1 = 16;
        int currentY = 5;
        int oldY = 0;
        int oldY1 = 0;
        int spaceHeight = 10;
        int startYS = 0;
        int endYS = 0;
        int startXH = 15;
        int endXH = 566;
        LPK130NW lpk130nw = new LPK130NW();
        LPK130NW.Format format = lpk130nw.new Format(2, 0, 0, false, false);
        LPK130NW.Format format1 = lpk130nw.new Format(2, 0, 0, true, false);
        LPK130NW.Format format2 = lpk130nw.new Format(3, 0, 1, false, false);
        PrinterObject po = new PrinterObject();
        InputStream is = this.getResources().openRawResource(R.raw.logo);
        BitmapDrawable bmpDraw = new BitmapDrawable(is);
        Bitmap bmp = bmpDraw.getBitmap();
        byte[] tempBytes = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        po.add(tempBytes);
        po.add(lpk130nw.NFCP_createPage(576, 1400));
        currentY = 100;
        try {
            po.add(lpk130nw.NFCP_Page_setText((576 - fontHeight2 * 16) / 2, currentY, "上海市公安局交通警察总队机动支队", 2, 0, 1, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setText((576 - fontHeight2 * 15) / 2, currentY, "公安交通管理简易程序处罚决定书", 2, 0, 1, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setText((576 - fontHeight2 * 7) / 2, currentY, "第三联（交银行）", 2, 0, 0, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_drawBar(56, currentY, "3103023700852637", 0, 0, 2, 80));
            currentY += 80;
            po.add(lpk130nw.NFCP_Page_setText((576 - fontHeight2 * 8) / 2, currentY, "3103023700852637", 2, 0, 0, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setText(0, currentY, "编号：" + parameter.identifier + "    执法机关代码：" + parameter.authorityCode, 2, 0, 0, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                    lpk130nw.new Parameter(format, "被处罚人："),
                    lpk130nw.new Parameter(format1, driverInfor.getXingming())));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                    lpk130nw.new Parameter(format, "机动车驾驶证档案编号："),
                    lpk130nw.new Parameter(format1, driverInfor.getDanganbianhao())));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                    lpk130nw.new Parameter(format, "证芯编号："),
                    lpk130nw.new Parameter(format1, parameter.cardNumber)));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setText(0, currentY, "机动车驾驶证", 2, 0, 0, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                    lpk130nw.new Parameter(format, "或居民身份证号码："),
                    lpk130nw.new Parameter(format1, driverInfor.getShengfenzheng())));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                    lpk130nw.new Parameter(format, "准驾车型："),
                    lpk130nw.new Parameter(format1, driverInfor.getZhunjiachexing())));
            currentY += fontHeight2;
            currentY += spaceHeight;
            if (parameter.contactInformation.length() > 19) {
                po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 * 2 + spaceHeight * 2,
                        lpk130nw.new Parameter(format, "联系方式："),
                        lpk130nw.new Parameter(format1, parameter.contactInformation)));
                currentY += fontHeight2 * 2;
                currentY += spaceHeight * 2;
            } else {
                po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                        lpk130nw.new Parameter(format, "联系方式："),
                        lpk130nw.new Parameter(format1, parameter.contactInformation)));
                currentY += fontHeight2;
                currentY += spaceHeight;
            }
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                    lpk130nw.new Parameter(format, "车辆牌号："),
                    lpk130nw.new Parameter(format1, parameter.carBrand)));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                    lpk130nw.new Parameter(format, "车辆类型："),
                    lpk130nw.new Parameter(format1, parameter.carType)));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 * 4 + spaceHeight * 4,
                    lpk130nw.new Parameter(format, "发证机关："),
                    lpk130nw.new Parameter(format1, parameter.ssuingAuthority)));
            currentY += fontHeight2 * 2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 * 4 + spaceHeight * 4,
                    lpk130nw.new Parameter(format, "    被处罚人于"),
                    lpk130nw.new Parameter(format1, String.valueOf(parameter.year)),
                    lpk130nw.new Parameter(format, "年"),
                    lpk130nw.new Parameter(format1, String.format("%02d", parameter.month)),
                    lpk130nw.new Parameter(format, "月"),
                    lpk130nw.new Parameter(format1, String.format("%02d", parameter.date)),
                    lpk130nw.new Parameter(format, "日"),
                    lpk130nw.new Parameter(format1, String.format("%02d", parameter.hour)),
                    lpk130nw.new Parameter(format, "时"),
                    lpk130nw.new Parameter(format1, String.format("%02d", parameter.minute)),
                    lpk130nw.new Parameter(format, "分，在"),
                    lpk130nw.new Parameter(format1, parameter.peccancyLocale),
                    lpk130nw.new Parameter(format, "实施" + parameter.peccancyContent + "的代码：（"),
                    lpk130nw.new Parameter(format1, parameter.peccancyCode),
                    lpk130nw.new Parameter(format, ")，违反了" + parameter.peccancyLaw + "。")));
            currentY += fontHeight2 * 5;
            currentY += spaceHeight * 5;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 * 2 + spaceHeight * 2, lpk130nw.new Parameter(format, "    根据" + parameter.punishLaw + "，决定给予：")));
            currentY += fontHeight2 * 3;
            currentY += spaceHeight * 2;
            parameter.punishContent = "";
            if ((parameter.punishMode & 0x01) == 0x01) {
                parameter.punishContent += (parameter.fineLineCapital + "圆（￥" + parameter.fineLinelowercase + "）罚款");
            }
            if ((parameter.punishMode & 0x02) == 0x02) {
                parameter.punishContent += " 扣留机动车";
            }
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight3 + spaceHeight, lpk130nw.new Parameter(format2, parameter.punishContent)));
            currentY += (fontHeight2 + fontHeight3);
            currentY += spaceHeight;
            if ((parameter.punishMode & 0x01) == 0x01) {
                po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 * 2 + spaceHeight * 2,
                        lpk130nw.new Parameter(format, "    持本决定书在15日内到通过全国任意工商银行罚款（咨询电话：95588）。")));
                currentY += fontHeight2 * 2;
                currentY += spaceHeight * 2;
                po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                        lpk130nw.new Parameter(format, "    逾期不缴纳的，每日按罚款数额的3%加处罚款。")));
                currentY += fontHeight2;
                currentY += spaceHeight;
            }
            if ((parameter.punishMode & 0x02) == 0x02) {
                po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 * 3 + spaceHeight * 2,
                        lpk130nw.new Parameter(format, "    请在15日内到"),
                        lpk130nw.new Parameter(format1, "（地点：古宜路131号，时间：周一至周六9：00--17：00）"),
                        lpk130nw.new Parameter(format, "接受处理。逾期不处理的，依法承担法律责任。")));
                currentY += fontHeight2 * 3;
                currentY += spaceHeight * 3;
            }
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 * 3 + spaceHeight * 3,
                    lpk130nw.new Parameter(format, "    如不服本决定的，可以在收到本决定书之日起60日内向"),
                    lpk130nw.new Parameter(format1, parameter.reviewCompany),
                    lpk130nw.new Parameter(format, "申请行政复议；后者在6个月内向"),
                    lpk130nw.new Parameter(format1, parameter.litigationCompany),
                    lpk130nw.new Parameter(format, "提起行政诉讼。")
            ));
            currentY += fontHeight2 * 4;
            currentY += spaceHeight * 4;
            if ((parameter.punishMode & 0x01) == 0x01) {
                po.add(lpk130nw.NFCP_Page_setText(0, currentY, "处罚地点：" + parameter.punishLocale, 2, 0, 0, false, false));
            }
            po.add(lpk130nw.NFCP_printPage(0, 2));
            po.add(lpk130nw.NFCP_createPage(576, 800));
            currentY = 150;
            po.add(lpk130nw.NFCP_Page_setText(0, currentY, "警号：" + parameter.policeNumber, 2, 0, 0, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setText(0, currentY, "交通警察:", 2, 0, 0, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setText(215, currentY, "上海市公安局交通警察总队", 2, 0, 0, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setText(310, currentY, "机动支队", 2, 0, 0, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setText(230, currentY, "日期：" +
                    parameter.year +
                    "年" + String.format("%02d", parameter.month) +
                    "月" + String.format("%02d", parameter.date) +
                    "日", 2, 0, 0, false, false));
            currentY += fontHeight2;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setText(0, currentY, "被处罚人签名", 2, 0, 0, false, false));
            currentY += fontHeight2 * 4;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                    lpk130nw.new Parameter(format, "签名日期："),
                    lpk130nw.new Parameter(format1, "        "),
                    lpk130nw.new Parameter(format, "年"),
                    lpk130nw.new Parameter(format1, "    "),
                    lpk130nw.new Parameter(format, "月"),
                    lpk130nw.new Parameter(format1, "    "),
                    lpk130nw.new Parameter(format, "日")
            ));
            currentY += fontHeight2;
            currentY += spaceHeight;
            if ((parameter.punishMode & 0x02) == 0x02) {
                po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 + spaceHeight,
                        lpk130nw.new Parameter(format, "当事人对本凭证记载内容有无异议：")));
                currentY += fontHeight2;
                currentY += spaceHeight;
                po.add(lpk130nw.NFCP_Page_drawRectangle(2, 2, currentY + 2, 22, currentY + 22));
                po.add(lpk130nw.NFCP_Page_setText(26, currentY, "有异议", 2, 0, 0, false, false));
                po.add(lpk130nw.NFCP_Page_drawRectangle(2, fontHeight2 * 11, currentY + 2, fontHeight2 * 11 + 20, currentY + 22));
                po.add(lpk130nw.NFCP_Page_setText(fontHeight2 * 12, currentY, "无异议", 2, 0, 0, false, false));
                currentY += fontHeight2;
                currentY += spaceHeight;
            }
            po.add(lpk130nw.NFCP_Page_setText(0, currentY, "备注：", 2, 0, 0, false, false));
            currentY += fontHeight2 * 3;
            currentY += spaceHeight;
            po.add(lpk130nw.NFCP_Page_drawLine(0, currentY, 576, currentY));
            currentY += fontHeight2;
            currentY += spaceHeight;
            if ((parameter.punishMode & 0x01) == 0x01) {
                po.add(lpk130nw.NFCP_Page_setText(0, currentY, "根据《机动车驾驶证申领和使用规定》记" + parameter.punishScore + "分", 2, 0, 0, false, false));
                currentY += fontHeight2;
                currentY += spaceHeight;
                po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 * 2 + spaceHeight * 2,
                        lpk130nw.new Parameter(format, "注：未作罚款处罚或者当场收缴罚款的，银行联不交被处罚人。")));
                currentY += fontHeight2 * 2;
                currentY += spaceHeight * 2;
            }
            if ((parameter.punishMode & 0x02) == 0x02) {
                po.add(lpk130nw.NFCP_Page_setSuperTextBox(0, currentY, 576, fontHeight2 * 2 + spaceHeight * 2,
                        lpk130nw.new Parameter(format, "本凭证同时作为现场笔录")));
                currentY += fontHeight2;
                currentY += spaceHeight;
            }
            po.add(lpk130nw.NFCP_printPage(0, 1));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        bmp.recycle();
        bmp = null;
        return po;
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(myReceiver);
        super.onDestroy();
    }
}
