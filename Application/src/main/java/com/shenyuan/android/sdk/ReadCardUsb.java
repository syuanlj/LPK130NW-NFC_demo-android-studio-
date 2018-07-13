package com.shenyuan.android.sdk;

/**
 * Created by sheny on 2018/7/12.
 */

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbRequest;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class ReadCardUsb {
    public static final String TAG = "ReadCardUsb";
    private DriverInfor driverInfor = new DriverInfor();
    Handler myHandler;
    private int result = -1;
    private final int NO_USB_PERMISSION = 0;
    private final int RECEIVEDATA = 1;
    private final int CARDINFOR = 2;
    private UsbEndpoint mUsbEndpointIn;
    private UsbEndpoint mUsbEndpointOut;
    private UsbDeviceConnection mUsbDeviceConnection;
    private byte[] receiveData = new byte[64];

    public ReadCardUsb(Handler myHandler) {
        this.myHandler = myHandler;
    }

    public ReadCardUsb() {
    }

    public void setmUsbEndpointIn(UsbEndpoint mUsbEndpointIn) {
        this.mUsbEndpointIn = mUsbEndpointIn;
    }

    public void setmUsbEndpointOut(UsbEndpoint mUsbEndpointOut) {
        this.mUsbEndpointOut = mUsbEndpointOut;
    }

    public void setmUsbDeviceConnection(UsbDeviceConnection mUsbDeviceConnection) {
        this.mUsbDeviceConnection = mUsbDeviceConnection;
    }

    public DriverInfor readCardInfor() {
        int sta;
        try {
            this.myHandler.sendEmptyMessage(3);
            sta = this.readReady();
        } catch (Exception var7) {
            this.myHandler.sendEmptyMessage(5);
            return null;
        }

        if(sta < 0) {
            if(sta == -10) {
                this.myHandler.sendEmptyMessage(5);
                return null;
            } else {
                this.myHandler.sendEmptyMessage(5);
                return null;
            }
        } else {
//            int CmdLen = false;
//            int SleepTime = false;
            byte[] aCmdBuff = new byte[12];
            this.myHandler.sendEmptyMessage(3);
            aCmdBuff[0] = 27;
            aCmdBuff[1] = 27;
            aCmdBuff[2] = 16;
            aCmdBuff[3] = 17;
            aCmdBuff[4] = 0;
            aCmdBuff[5] = 5;
            aCmdBuff[6] = 0;
            aCmdBuff[7] = -80;
            aCmdBuff[8] = -127;
            aCmdBuff[9] = 28;
            aCmdBuff[10] = 12;
            aCmdBuff[11] = this.GetVerify(aCmdBuff, 6, aCmdBuff[4] * 256 + aCmdBuff[5]);
//            CmdLen = true;
//            SleepTime = true;
            String temp = this.Readxinxi(aCmdBuff);
            Message msg;
            if(temp == null) {
                this.ShowMessage("读取姓名失败！");
            } else {
                this.driverInfor.setXingming(temp);
                msg = this.myHandler.obtainMessage();
                msg.what = 6;
                msg.obj = temp;
                this.myHandler.sendMessage(msg);
                temp = null;
            }

            aCmdBuff = new byte[]{27, 27, 16, 17, 0, 5, 0, -80, -127, 10, 18, 0};
            aCmdBuff[11] = this.GetVerify(aCmdBuff, 6, aCmdBuff[4] * 256 + aCmdBuff[5]);
//            CmdLen = true;
//            SleepTime = true;
            temp = this.Readxinxi(aCmdBuff);
            if(temp == null) {
                this.ShowMessage("读取身份证号失败！");
            } else {
                this.driverInfor.setShengfenzheng(temp);
                msg = this.myHandler.obtainMessage();
                msg.what = 7;
                msg.obj = temp;
                this.myHandler.sendMessage(msg);
                temp = null;
            }

            aCmdBuff = new byte[]{27, 27, 16, 17, 0, 4, 0, -80, -127, 10, 0, 0};
            aCmdBuff[10] = this.GetVerify(aCmdBuff, 6, aCmdBuff[4] * 256 + aCmdBuff[5]);
//            CmdLen = true;
//            SleepTime = true;
            temp = this.Readxinxi(aCmdBuff);
            if(temp == null) {
                this.ShowMessage("读取档案编号失败！");
            } else {
                this.driverInfor.setDanganbianhao(temp);
                msg = this.myHandler.obtainMessage();
                msg.what = 8;
                msg.obj = temp;
                this.myHandler.sendMessage(msg);
                temp = null;
            }

            aCmdBuff = new byte[]{27, 27, 16, 17, 0, 5, 0, -80, -127, 40, 5, 0};
            aCmdBuff[11] = this.GetVerify(aCmdBuff, 6, aCmdBuff[4] * 256 + aCmdBuff[5]);
//            CmdLen = true;
//            SleepTime = true;
            temp = this.Readxinxi(aCmdBuff);
            if(temp == null) {
                this.ShowMessage("读取准驾车型失败！");
            } else {
                this.driverInfor.setZhunjiachexing(temp);
                msg = this.myHandler.obtainMessage();
                msg.what = 9;
                msg.obj = temp;
                this.myHandler.sendMessage(msg);
                temp = null;
            }

            aCmdBuff = new byte[]{27, 27, 16, 17, 0, 5, 0, -80, -127, 56, 4, 0};
            aCmdBuff[11] = this.GetVerify(aCmdBuff, 6, aCmdBuff[4] * 256 + aCmdBuff[5]);
            temp = this.Readxinxi(aCmdBuff);
            if(temp == null) {
                this.ShowMessage("读取领证日期失败！");
            } else {
                this.driverInfor.setZhunjiachexing(temp);
                msg = this.myHandler.obtainMessage();
                msg.what = 10;
                msg.obj = temp;
                this.myHandler.sendMessage(msg);
                temp = null;
            }

            this.myHandler.sendEmptyMessage(4);
            return this.driverInfor;
        }
    }

    private int readReady() {
        byte[] aCmdBuff = new byte[]{27, 27, 16, 16, 0, 1, 0, 0};
        aCmdBuff[7] = this.GetVerify(aCmdBuff, 6, aCmdBuff[4] * 256 + aCmdBuff[5]);
        int CmdLen = 8;
        this.result = this.mUsbDeviceConnection.bulkTransfer(this.mUsbEndpointOut, aCmdBuff, CmdLen, 1500);
        Log.e("ReadCardUsb", String.valueOf(this.result));
        int revLength = this.read(this.receiveData);
        Log.e("ReadCardUsb", new String(this.receiveData));
        int len;
        if(revLength >= 8) {
            len = this.receiveData[3] * 256 + this.receiveData[4];
            if(this.receiveData[0] == 79 && this.receiveData[1] == 75 && this.receiveData[2] == 16) {
                this.result = 0;
            } else {
                this.result = -1;
            }
        } else {
            this.result = -3;
        }

        if(this.result < 0) {
            return this.result;
        } else {
            aCmdBuff = new byte[]{27, 27, 16, 17, 0, 13, 0, -92, 4, 0, 8, -96, 0, 0, 0, 1, 80, 79, 76, 0};
            aCmdBuff[19] = this.GetVerify(aCmdBuff, 6, aCmdBuff[4] * 256 + aCmdBuff[5]);
            CmdLen = 20;
            this.result = this.mUsbDeviceConnection.bulkTransfer(this.mUsbEndpointOut, aCmdBuff, CmdLen, 1500);
            Log.e("ReadCardUsb", String.valueOf(this.result));
            revLength = this.read(this.receiveData);
            Log.e("ReadCardUsb", new String(this.receiveData));
            this.myHandler.sendEmptyMessage(1);
            if(revLength >= 8) {
                len = this.receiveData[3] * 256 + this.receiveData[4];
                if(this.receiveData[0] == 79 && this.receiveData[1] == 75 && this.receiveData[2] == 17) {
                    this.result = 0;
                } else {
                    this.result = -10;
                }
            } else {
                this.result = -10;
            }

            return this.result;
        }
    }

    private int read(byte[] tempBytes) {
        int inMax = this.mUsbEndpointIn.getMaxPacketSize();
        ByteBuffer byteBuffer = ByteBuffer.allocate(inMax);
        UsbRequest usbRequest = new UsbRequest();
        usbRequest.initialize(this.mUsbDeviceConnection, this.mUsbEndpointIn);
        usbRequest.queue(byteBuffer, inMax);
        if(this.mUsbDeviceConnection.requestWait() != usbRequest) {
            return 0;
        } else {
            byte[] retData = byteBuffer.array();
//            int length = false;
            new String(retData);

            for(int i = 0; i < inMax; ++i) {
                tempBytes[i] = retData[i];
            }

            return inMax;
        }
    }

    private String Readxinxi(byte[] aCmdBuff) {
        String Resultstr = null;
        this.result = this.mUsbDeviceConnection.bulkTransfer(this.mUsbEndpointOut, aCmdBuff, aCmdBuff.length, 1500);
        Log.e("ReadCardUsb", String.valueOf(this.result));
        int revLength = this.read(this.receiveData);
        Log.e("ReadCardUsb", new String(this.receiveData));
        this.myHandler.sendEmptyMessage(1);
        if(revLength >= 8) {
            if(this.receiveData[3] < 0) {
                this.receiveData[3] = (byte)(this.receiveData[3] + 256);
            }

            if(this.receiveData[4] < 0) {
                this.receiveData[4] = (byte)(this.receiveData[4] + 256);
            }

            int len = this.receiveData[3] * 256 + this.receiveData[4];
            if(this.receiveData[0] == 79 && this.receiveData[1] == 75 && this.receiveData[2] == 17) {
                byte[] temp = new byte[len - 2];
                System.arraycopy(this.receiveData, 5, temp, 0, len - 2);

                try {
                    Resultstr = new String(temp, "gb2312");
                } catch (UnsupportedEncodingException var8) {
                    var8.printStackTrace();
                }

                this.myHandler.sendEmptyMessage(1);
                this.result = 0;
            } else {
                this.result = -1;
            }
        } else {
            this.result = -3;
        }

        return this.result < 0?null:Resultstr;
    }

    private byte GetVerify(byte[] data, int index, int len) {
        byte a = 0;

        for(int i = 0; i < len; ++i) {
            a ^= data[index + i];
        }

        return a;
    }

    private static String Bytes2HexString(byte[] b) {
        String ret = "";

        for(int i = 0; i < b.length; ++i) {
            String hex = Integer.toHexString(b[i] & 255);
            if(hex.length() == 1) {
                hex = '0' + hex;
            }

            hex = "0x" + hex + ",";
            ret = ret + hex;
        }

        return ret;
    }

    void ShowMessage(String message) {
        Log.e("ReadCardUsb", message);
    }
}

