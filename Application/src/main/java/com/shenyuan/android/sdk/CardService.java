package com.shenyuan.android.sdk;

/**
 * Created by sheny on 2018/7/12.
 */

import android.content.Intent;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class CardService extends HostApduService {
    private static final String TAG = "CardService";
    private static final String SAMPLE_LOYALTY_CARD_AID = "F222222222";
    private static final String SELECT_APDU_HEADER = "00A40400";
    private static final byte[] RESET = new byte[]{79, 75};
    private static final byte[] SELECT_OK_SW = HexStringToByteArray("");
    private static final byte[] UNKNOWN_CMD_SW = HexStringToByteArray("0000");
    private static final byte[] SELECT_APDU = BuildSelectApdu("F222222222");
    private static byte[] data;
    private static final int buffersize = 245;
    private static final int timeOut = 1000;
    private static byte[] accountBytes = new byte[245];
    private static int times = 0;
    private static int blockNumber = 0;
    private static int size = 0;
    private static long preTime = System.currentTimeMillis();
    private static long difftime = 0L;
    private int result = 0;
    private String stringData;
    private byte[] aCmdBuff;
    private StringBuffer cardInfo = new StringBuffer();
    Intent intent = new Intent();
    private boolean isflag = true;
    private Thread thread = null;

    public CardService() {
    }

    public void onDeactivated(int reason) {
    }

    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        for(int i = 0; i < 245; ++i) {
            accountBytes[i] = 0;
        }

        difftime = System.currentTimeMillis() - preTime;
        if(difftime >= 1000L) {
            times = 0;
        }

        if(Arrays.equals(RESET, commandApdu)) {
            preTime = System.currentTimeMillis();
        }

        int i;
        if(DataOperater.currentMode == 0) {
            if(!Arrays.equals(SELECT_APDU, commandApdu)) {
                return UNKNOWN_CMD_SW;
            } else {
                if(0 == times) {
                    preTime = System.currentTimeMillis();
                    this.isflag = true;
                    this.thread = new Thread(new Runnable() {
                        public void run() {
                            while(CardService.this.isflag) {
                                if(System.currentTimeMillis() - CardService.preTime >= 1000L) {
                                    CardService.this.intent.setAction("com.fujitsu.sdk.CardService");
                                    CardService.this.intent.putExtra("action", 2);
                                    CardService.this.sendBroadcast(CardService.this.intent);
                                    CardService.this.isflag = false;
                                }
                            }

                        }
                    });
                    this.thread.start();
                    this.intent.setAction("com.fujitsu.sdk.CardService");
                    this.intent.putExtra("action", 0);
                    this.sendBroadcast(this.intent);
                    data = DataOperater.GetContentData();
                    blockNumber = (data.length + 245 - 4) / 242;
                } else {
                    Log.i("CardService", "第" + String.valueOf(times) + "包数据发送完成");
                }

                if(blockNumber == 0) {
                    return UNKNOWN_CMD_SW;
                } else {
                    if(times + 1 == blockNumber) {
                        size = data.length - times * 242;
                        accountBytes[0] = -86;
                        accountBytes[1] = (byte)size;
                        size += 3;
                    } else {
                        size = 245;
                        accountBytes[0] = -69;
                        accountBytes[1] = -14;
                    }

                    for(i = 2; i < size - 1; ++i) {
                        accountBytes[i] = data[times * 242 + i - 2];
                    }

                    accountBytes[size - 1] = -1;
                    ++times;
                    if(times == blockNumber) {
                        Log.i("CardService", "第" + String.valueOf(times) + "包数据发送完成");
                        Log.i("CardService", "数据全部发送完毕！");
                        this.intent.setAction("com.fujitsu.sdk.CardService");
                        this.intent.putExtra("action", 1);
                        this.sendBroadcast(this.intent);
                        times = 0;
                        this.isflag = false;
                    }

                    preTime = System.currentTimeMillis();
                    return ConcatArrays(accountBytes, new byte[][]{SELECT_OK_SW});
                }
            }
        } else {
            switch(times) {
                case 0:
                    preTime = System.currentTimeMillis();
                    this.isflag = true;
                    this.thread = new Thread(new Runnable() {
                        public void run() {
                            while(CardService.this.isflag) {
                                if(System.currentTimeMillis() - CardService.preTime >= 1000L) {
                                    CardService.this.intent.setAction("com.fujitsu.sdk.CardService");
                                    CardService.this.intent.putExtra("action", 2);
                                    CardService.this.sendBroadcast(CardService.this.intent);
                                    CardService.this.isflag = false;
                                }
                            }

                        }
                    });
                    this.thread.start();
                    this.intent.setAction("com.fujitsu.sdk.CardService");
                    this.intent.putExtra("action", 3);
                    this.sendBroadcast(this.intent);
                    preTime = System.currentTimeMillis();
                    this.aCmdBuff = new byte[11];
                    this.aCmdBuff[0] = -86;
                    this.aCmdBuff[1] = 8;
                    this.aCmdBuff[2] = 27;
                    this.aCmdBuff[3] = 27;
                    this.aCmdBuff[4] = 16;
                    this.aCmdBuff[5] = 16;
                    this.aCmdBuff[6] = 0;
                    this.aCmdBuff[7] = 1;
                    this.aCmdBuff[8] = 0;
                    this.aCmdBuff[9] = this.GetVerify(this.aCmdBuff, 8, this.aCmdBuff[6] * 256 + this.aCmdBuff[7]);
                    this.aCmdBuff[10] = -1;
                    ++times;
                    preTime = System.currentTimeMillis();
                    return ConcatArrays(this.aCmdBuff, new byte[][]{SELECT_OK_SW});
                case 1:
                    preTime = System.currentTimeMillis();
                    if(commandApdu.length >= 8) {
                        if(commandApdu[3] < 0) {
                            commandApdu[3] = (byte)(commandApdu[3] + 256);
                        }

                        if(commandApdu[4] < 0) {
                            commandApdu[4] = (byte)(commandApdu[4] + 256);
                        }

                        i = commandApdu[3] * 256 + commandApdu[4];
                        if(i == commandApdu.length - 6) {
                            if(commandApdu[0] == 79 && commandApdu[1] == 75 && commandApdu[2] == 16) {
                                this.result = 0;
                            } else {
                                this.result = -1;
                            }
                        } else {
                            this.result = -2;
                        }
                    } else {
                        this.result = -3;
                    }

                    if(this.result < 0) {
                        times = 0;
                        this.isflag = false;
                        this.intent.putExtra("action", 5);
                        this.sendBroadcast(this.intent);
                        Log.i("CardService", "卡片不存在或未良好接触，请确认。");
                    } else if(this.result == 0) {
                        this.aCmdBuff = new byte[23];
                        this.aCmdBuff[0] = -86;
                        this.aCmdBuff[1] = 20;
                        this.aCmdBuff[2] = 27;
                        this.aCmdBuff[3] = 27;
                        this.aCmdBuff[4] = 16;
                        this.aCmdBuff[5] = 17;
                        this.aCmdBuff[6] = 0;
                        this.aCmdBuff[7] = 13;
                        this.aCmdBuff[8] = 0;
                        this.aCmdBuff[9] = -92;
                        this.aCmdBuff[10] = 4;
                        this.aCmdBuff[11] = 0;
                        this.aCmdBuff[12] = 8;
                        this.aCmdBuff[13] = -96;
                        this.aCmdBuff[14] = 0;
                        this.aCmdBuff[15] = 0;
                        this.aCmdBuff[16] = 0;
                        this.aCmdBuff[17] = 1;
                        this.aCmdBuff[18] = 80;
                        this.aCmdBuff[19] = 79;
                        this.aCmdBuff[20] = 76;
                        this.aCmdBuff[21] = this.GetVerify(this.aCmdBuff, 8, this.aCmdBuff[6] * 256 + this.aCmdBuff[7]);
                        this.aCmdBuff[22] = -1;
                        ++times;
                        preTime = System.currentTimeMillis();
                        return ConcatArrays(this.aCmdBuff, new byte[][]{SELECT_OK_SW});
                    }

                    preTime = System.currentTimeMillis();
                    break;
                case 2:
                    preTime = System.currentTimeMillis();
                    if(commandApdu.length >= 8) {
                        if(commandApdu[3] < 0) {
                            commandApdu[3] = (byte)(commandApdu[3] + 256);
                        }

                        if(commandApdu[4] < 0) {
                            commandApdu[4] = (byte)(commandApdu[4] + 256);
                        }

                        i = commandApdu[3] * 256 + commandApdu[4];
                        if(i == commandApdu.length - 6) {
                            if(commandApdu[0] == 79 && commandApdu[1] == 75 && commandApdu[2] == 17) {
                                this.result = 0;
                            } else {
                                this.result = -10;
                            }
                        } else {
                            this.result = -10;
                        }
                    } else {
                        this.result = -10;
                    }

                    if(this.result < 0) {
                        times = 0;
                        this.intent.putExtra("action", 5);
                        this.sendBroadcast(this.intent);
                        this.isflag = false;
                        Log.i("CardService", "卡片不存在或未良好接触，请确认。");
                    } else if(0 == this.result) {
                        this.aCmdBuff = new byte[15];
                        this.aCmdBuff[0] = -86;
                        this.aCmdBuff[1] = 12;
                        this.aCmdBuff[2] = 27;
                        this.aCmdBuff[3] = 27;
                        this.aCmdBuff[4] = 16;
                        this.aCmdBuff[5] = 17;
                        this.aCmdBuff[6] = 0;
                        this.aCmdBuff[7] = 5;
                        this.aCmdBuff[8] = 0;
                        this.aCmdBuff[9] = -80;
                        this.aCmdBuff[10] = -127;
                        this.aCmdBuff[11] = 28;
                        this.aCmdBuff[12] = 12;
                        this.aCmdBuff[13] = this.GetVerify(this.aCmdBuff, 8, this.aCmdBuff[6] * 256 + this.aCmdBuff[7]);
                        this.aCmdBuff[14] = -1;
                        ++times;
                        preTime = System.currentTimeMillis();
                        return ConcatArrays(this.aCmdBuff, new byte[][]{SELECT_OK_SW});
                    }

                    preTime = System.currentTimeMillis();
                    break;
                case 3:
                    this.stringData = this.analyzeInfor(commandApdu);
                    if(this.stringData == null) {
                        Log.i("CardService", "读取姓名失败！");
                    } else {
                        this.intent.putExtra("action", 6);
                        this.intent.putExtra("information", this.stringData);
                        this.sendBroadcast(this.intent);
                        Log.i("CardService", "卡片信息\n");
                        Log.i("CardService", "姓名：" + this.stringData + "\n");
                    }

                    this.aCmdBuff = new byte[15];
                    this.aCmdBuff[0] = -86;
                    this.aCmdBuff[1] = 12;
                    this.aCmdBuff[2] = 27;
                    this.aCmdBuff[3] = 27;
                    this.aCmdBuff[4] = 16;
                    this.aCmdBuff[5] = 17;
                    this.aCmdBuff[6] = 0;
                    this.aCmdBuff[7] = 5;
                    this.aCmdBuff[8] = 0;
                    this.aCmdBuff[9] = -80;
                    this.aCmdBuff[10] = -127;
                    this.aCmdBuff[11] = 10;
                    this.aCmdBuff[12] = 18;
                    this.aCmdBuff[13] = this.GetVerify(this.aCmdBuff, 8, this.aCmdBuff[6] * 256 + this.aCmdBuff[7]);
                    this.aCmdBuff[14] = -1;
                    ++times;
                    preTime = System.currentTimeMillis();
                    return ConcatArrays(this.aCmdBuff, new byte[][]{SELECT_OK_SW});
                case 4:
                    this.stringData = this.analyzeInfor(commandApdu);
                    if(this.stringData == null) {
                        Log.i("CardService", "读取身份证号失败！");
                    } else {
                        this.intent.putExtra("action", 7);
                        this.intent.putExtra("information", this.stringData);
                        this.sendBroadcast(this.intent);
                        Log.i("CardService", "身份证号：" + this.stringData + "\n");
                    }

                    this.aCmdBuff = new byte[14];
                    this.aCmdBuff[0] = -86;
                    this.aCmdBuff[1] = 11;
                    this.aCmdBuff[2] = 27;
                    this.aCmdBuff[3] = 27;
                    this.aCmdBuff[4] = 16;
                    this.aCmdBuff[5] = 17;
                    this.aCmdBuff[6] = 0;
                    this.aCmdBuff[7] = 4;
                    this.aCmdBuff[8] = 0;
                    this.aCmdBuff[9] = -80;
                    this.aCmdBuff[10] = -127;
                    this.aCmdBuff[11] = 10;
                    this.aCmdBuff[12] = this.GetVerify(this.aCmdBuff, 8, this.aCmdBuff[6] * 256 + this.aCmdBuff[7]);
                    this.aCmdBuff[13] = -1;
                    ++times;
                    preTime = System.currentTimeMillis();
                    return ConcatArrays(this.aCmdBuff, new byte[][]{SELECT_OK_SW});
                case 5:
                    this.stringData = this.analyzeInfor(commandApdu);
                    if(this.stringData == null) {
                        Log.i("CardService", "读取档案编号失败！");
                    } else {
                        this.intent.putExtra("action", 8);
                        this.intent.putExtra("information", this.stringData);
                        this.sendBroadcast(this.intent);
                        Log.i("CardService", "档案编号：" + this.stringData + "\n");
                    }

                    this.aCmdBuff = new byte[15];
                    this.aCmdBuff[0] = -86;
                    this.aCmdBuff[1] = 12;
                    this.aCmdBuff[2] = 27;
                    this.aCmdBuff[3] = 27;
                    this.aCmdBuff[4] = 16;
                    this.aCmdBuff[5] = 17;
                    this.aCmdBuff[6] = 0;
                    this.aCmdBuff[7] = 5;
                    this.aCmdBuff[8] = 0;
                    this.aCmdBuff[9] = -80;
                    this.aCmdBuff[10] = -127;
                    this.aCmdBuff[11] = 40;
                    this.aCmdBuff[12] = 5;
                    this.aCmdBuff[13] = this.GetVerify(this.aCmdBuff, 8, this.aCmdBuff[6] * 256 + this.aCmdBuff[7]);
                    this.aCmdBuff[14] = -1;
                    ++times;
                    preTime = System.currentTimeMillis();
                    return ConcatArrays(this.aCmdBuff, new byte[][]{SELECT_OK_SW});
                case 6:
                    this.stringData = this.analyzeInfor(commandApdu);
                    if(this.stringData == null) {
                        Log.i("CardService", "读取准驾车型失败！");
                    } else {
                        this.intent.putExtra("action", 9);
                        this.intent.putExtra("information", this.stringData);
                        this.sendBroadcast(this.intent);
                        Log.i("CardService", "准驾车型：" + this.stringData + "\n");
                    }

                    this.aCmdBuff = new byte[15];
                    this.aCmdBuff[0] = -86;
                    this.aCmdBuff[1] = 12;
                    this.aCmdBuff[2] = 27;
                    this.aCmdBuff[3] = 27;
                    this.aCmdBuff[4] = 16;
                    this.aCmdBuff[5] = 17;
                    this.aCmdBuff[6] = 0;
                    this.aCmdBuff[7] = 5;
                    this.aCmdBuff[8] = 0;
                    this.aCmdBuff[9] = -80;
                    this.aCmdBuff[10] = -127;
                    this.aCmdBuff[11] = 56;
                    this.aCmdBuff[12] = 4;
                    this.aCmdBuff[13] = this.GetVerify(this.aCmdBuff, 8, this.aCmdBuff[6] * 256 + this.aCmdBuff[7]);
                    this.aCmdBuff[14] = -1;
                    ++times;
                    preTime = System.currentTimeMillis();
                    return ConcatArrays(this.aCmdBuff, new byte[][]{SELECT_OK_SW});
                case 7:
                    String temp = " ";

                    try {
                        byte[] tempByte = new byte[]{-1};
                        temp = new String(tempByte, "gb2312");
                    } catch (UnsupportedEncodingException var6) {
                        var6.printStackTrace();
                    }

                    if(this.stringData != null) {
                        this.stringData = this.analyzeInfor(commandApdu).replaceAll(temp, "");
                        if(!this.stringData.equals("")) {
                            this.intent.putExtra("action", 10);
                            this.intent.putExtra("information", this.stringData);
                            this.sendBroadcast(this.intent);
                            Log.i("CardService", "领证日期：" + this.stringData + "\n");
                        }
                    }

                    this.isflag = false;
                    Log.i("CardService", "数据读取完毕！");
                    this.intent.putExtra("action", 4);
                    this.sendBroadcast(this.intent);
                    times = 0;
                    preTime = System.currentTimeMillis();
            }

            return UNKNOWN_CMD_SW;
        }
    }

    public static byte[] BuildSelectApdu(String aid) {
        return HexStringToByteArray("00A40400" + String.format("%02X", new Object[]{Integer.valueOf(aid.length() / 2)}) + aid);
    }

    private static String ByteArrayToHexString(byte[] bytes) {
        char[] hexArray = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 2];

        for(int j = 0; j < bytes.length; ++j) {
            int v = bytes[j] & 255;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 15];
        }

        return new String(hexChars);
    }

    private static byte[] HexStringToByteArray(String s) throws IllegalArgumentException {
        int len = s.length();
        if(len % 2 == 1) {
            throw new IllegalArgumentException("Hex string must have even number of characters");
        } else {
            byte[] data = new byte[len / 2];

            for(int i = 0; i < len; i += 2) {
                data[i / 2] = (byte)((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
            }

            return data;
        }
    }

    private static byte[] ConcatArrays(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        byte[][] var3 = rest;
        int offset = rest.length;

        for(int var5 = 0; var5 < offset; ++var5) {
            byte[] array = var3[var5];
            totalLength += array.length;
        }

        byte[] result = Arrays.copyOf(first, totalLength);
        offset = first.length;
        byte[][] var10 = rest;
        int var11 = rest.length;

        for(int var7 = 0; var7 < var11; ++var7) {
            byte[] array = var10[var7];
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }

        return result;
    }

    private byte GetVerify(byte[] data, int index, int len) {
        byte a = 0;

        for(int i = 0; i < len; ++i) {
            a ^= data[index + i];
        }

        return a;
    }

    private String analyzeInfor(byte[] revdata) {
        if(revdata.length >= 8) {
            if(revdata[3] < 0) {
                revdata[3] = (byte)(revdata[3] + 256);
            }

            if(revdata[4] < 0) {
                revdata[4] = (byte)(revdata[4] + 256);
            }

            int len = revdata[3] * 256 + revdata[4];
            if(revdata[0] == 79 && revdata[1] == 75 && revdata[2] == 17) {
                byte[] temp = new byte[len - 2];
                System.arraycopy(revdata, 5, temp, 0, len - 2);

                try {
                    String Resultstr = new String(temp, "gb2312");
                    return Resultstr;
                } catch (UnsupportedEncodingException var6) {
                    var6.printStackTrace();
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}

