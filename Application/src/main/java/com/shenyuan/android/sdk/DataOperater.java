package com.shenyuan.android.sdk;

/**
 * Created by sheny on 2018/7/12.
 */

public class DataOperater {
    private static final String TAG = "DataOperater";
    private static final Object sAccountLock = new Object();
    private static byte[] result = null;
    public static final int PRINT_MODE = 0;
    public static final int READ_MODE = 1;
    public static int currentMode = 0;
    public static final String broadcastAction = "com.fujitsu.sdk.CardService";
    public static final int SEND_START = 0;
    public static final int SEND_OVER = 1;
    public static final int SEND_ERROR = 2;
    public static final int READ_START = 3;
    public static final int READ_OVER = 4;
    public static final int READ_ERROR = 5;
    public static final int XINGMING = 6;
    public static final int SFZH = 7;
    public static final int DABH = 8;
    public static final int ZJCX = 9;
    public static final int LZRQ = 10;

    public DataOperater() {
    }

    public static void setContentData(PrinterObject printerObject) {
        Object var1 = sAccountLock;
        synchronized(sAccountLock) {
            try {
                if(printerObject == null) {
                    result = new byte[0];
                } else {
                    int len = printerObject.size();
                    if(len == 0) {
                        result = new byte[0];
                    } else {
                        byte[] temp1 = printerObject.get(0);
                        result = temp1;

                        for(int i = 1; i < len; ++i) {
                            byte[] temp2 = printerObject.get(i);
                            result = new byte[temp1.length + temp2.length];
                            System.arraycopy(temp1, 0, result, 0, temp1.length);
                            System.arraycopy(temp2, 0, result, temp1.length, temp2.length);
                            temp1 = result;
                        }
                    }
                }
            } catch (Exception var7) {

            }

        }
    }

    public static byte[] GetContentData() {
        Object var0 = sAccountLock;
        synchronized(sAccountLock) {
            return result;
        }
    }
}

