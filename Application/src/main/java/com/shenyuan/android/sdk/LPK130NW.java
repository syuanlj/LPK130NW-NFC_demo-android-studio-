package com.shenyuan.android.sdk;

/**
 * Created by sheny on 2018/7/12.
 */

import android.graphics.Bitmap;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

public class LPK130NW {
    private static final boolean ER_COMMRANGE = false;
    private static final boolean ER_INTERRUPT = false;
    static byte indexCode = 0;

    public LPK130NW() {
    }

    public byte[] printContent(String str) {
        try {
            return str.getBytes("GB2312");
        } catch (UnsupportedEncodingException var3) {
            var3.printStackTrace();
            return new byte[0];
        }
    }

    public byte[] NFCP_printerBeep() {
        byte[] arrayOfByte = new byte[]{7};
        return arrayOfByte;
    }

    public byte[] NFCP_printerWake() {
        byte[] arrayOfByte = new byte[]{0, 0, 0, 0, 0, 0};
        return arrayOfByte;
    }

    public byte[] NFCP_printStr(String str) throws UnsupportedEncodingException {
        return this.printContent(str);
    }

    public byte[] NFCP_printStrLine(String str) throws UnsupportedEncodingException {
        String tmpStr = str + "\r\n";
        return this.printContent(tmpStr);
    }

    public byte[] NFCP_printLF() {
        byte[] arrayOfByte = new byte[]{10};
        return arrayOfByte;
    }

    public byte[] NFCP_printCR() {
        byte[] arrayOfByte = new byte[]{10, 13};
        return arrayOfByte;
    }

    public byte[] NFCP_backPaper(int n) {
        byte[] arrayOfByte = new byte[]{27, 75, (byte)n};
        return arrayOfByte;
    }

    public byte[] NFCP_setFontDouble(int op) {
        byte[] arrayOfByte = new byte[]{27, 71, (byte)op};
        if(op != 1 && op != 0) {
            arrayOfByte[2] = 0;
        }

        return arrayOfByte;
    }

    public byte[] NFCP_setFontBold(int op) {
        byte[] arrayOfByte = new byte[]{27, 69, (byte)op};
        if(op != 1 && op != 0) {
            arrayOfByte[2] = 0;
        }

        return arrayOfByte;
    }

    public byte[] NFCP_setUnderLine(int op) {
        if(op <= 2 && op >= 0) {
            byte[] arrayOfByte = new byte[]{27, 45, (byte)op};
            return arrayOfByte;
        } else {
            return new byte[0];
        }
    }

    public byte[] NFCP_feedToBlack() {
        byte[] arrayOfByte = new byte[]{12};
        return arrayOfByte;
    }

    public byte[] NFCP_feed(int n) {
        byte[] arrayOfByte = new byte[]{27, 74, (byte)n};
        return arrayOfByte;
    }

    public byte[] NFCP_setLineSpace(int distance) {
        byte[] arrayOfByte = new byte[]{27, 51, (byte)distance};
        return arrayOfByte;
    }

    public byte[] NFCP_setAbsPosition(int abspos) {
        byte[] arrayOfByte = new byte[]{27, 36, (byte)(abspos % 256), (byte)(abspos / 256)};
        return arrayOfByte;
    }

    public byte[] NFCP_setRelPosition(int relpos) {
        byte[] arrayOfByte = new byte[]{27, 92, (byte)(relpos % 256), (byte)(relpos / 256)};
        return arrayOfByte;
    }

    public byte[] NFCP_setSnapMode(int snapMode) {
        if(snapMode <= 2 && snapMode >= 0) {
            byte[] arrayOfByte = new byte[]{27, 97, (byte)snapMode};
            return arrayOfByte;
        } else {
            return new byte[0];
        }
    }

    public byte[] NFCP_setRotation(int op) {
        byte[] arrayOfByte = new byte[]{27, 86, 0};
        if(op < 0 || op > 3) {
            op = 0;
        }

        arrayOfByte[2] = (byte)op;
        return arrayOfByte;
    }

    public byte[] NFCP_setLeftMargin(int dot) {
        byte[] arrayOfByte = new byte[]{29, 76, (byte)(dot % 256), (byte)(dot / 256)};
        return arrayOfByte;
    }

    public byte[] NFCP_setCharSpace(int dot) {
        byte[] arrayOfByte = new byte[]{27, 32, (byte)dot};
        return arrayOfByte;
    }

    public byte[] NFCP_chooseFont(int op) {
        if(op != 1 && op != 0 && op != 48 && op != 49) {
            return new byte[0];
        } else {
            byte[] arrayOfByte = new byte[]{27, 77, (byte)op};
            return arrayOfByte;
        }
    }

    public byte[] NFCP_fontSize(int height, int width) {
        if(height <= 2 && width <= 2) {
            byte[] arrayOfByte = new byte[]{29, 33, (byte)((width - 1) * 16 + height - 1)};
            return arrayOfByte;
        } else {
            return new byte[0];
        }
    }

    public byte[] NFCP_feedRow(int row) {
        byte[] arrayOfByte = new byte[]{27, 100, (byte)row};
        return arrayOfByte;
    }

    public byte[] NFCP_printPicInflash(int index, int mode) {
        if(index <= 5 && index >= 1) {
            byte[] data_logo = new byte[]{28, 112, (byte)index, (byte)mode};
            if(mode < 0 || mode > 4) {
                data_logo[3] = (byte)mode;
            }

            return data_logo;
        } else {
            return new byte[0];
        }
    }

    public byte[] NFCP_drawBarCode(String str, int barType, int heightDot, int HRI_location) throws UnsupportedEncodingException {
        if(HRI_location > 3 || HRI_location < 0) {
            HRI_location = 2;
        }

        byte bType;
        switch(barType) {
            case 0:
                bType = 69;
                break;
            case 1:
                bType = 73;
                break;
            case 2:
                bType = 72;
                break;
            case 3:
                bType = 71;
                break;
            case 4:
                bType = 68;
                break;
            case 5:
                bType = 67;
                break;
            case 6:
                bType = 65;
                break;
            case 7:
                bType = 66;
                break;
            case 8:
                bType = 70;
                break;
            default:
                bType = 73;
        }

        byte[] barHeightCommand = new byte[]{29, 104, (byte)heightDot};
        byte[] barWidthCommand = new byte[]{29, 72, (byte)HRI_location};
        byte[] barTypeCommand = new byte[]{29, 107, (byte)bType};
        byte[] merger = this.byteMerger(this.byteMerger(barHeightCommand, barWidthCommand), barTypeCommand);
//        boolean i = false;
//        byte[] temp = null;
        byte[] data = new byte[513];
        byte[] temp = str.getBytes("GB2312");
        int strLen = temp.length;
        int i;
        if(barType == 1) {
            byte[] budf = new byte[256];
            byte index = 0;
//            byte index;
            if(strLen <= 4) {
//                byte curset = true;
                index = (byte)(index + 1);
                budf[index] = 123;
                budf[index++] = 65;

                for(i = 0; i < strLen; ++i) {
                    budf[index++] = temp[i];
                }
            } else {
                 i = 0;
                byte curset;
                if(isnum(temp[0]) && isnum(temp[1]) && isnum(temp[2]) && isnum(temp[3])) {
                    curset = 67;
                    index = (byte)(index + 1);
                    budf[index] = 123;
                    budf[index++] = 67;
                    budf[index++] = seta2c(temp[0], temp[1]);
                    budf[index++] = seta2c(temp[2], temp[3]);
                    i = i + 4;
                } else {
                    curset = 65;
                    index = (byte)(index + 1);
                    budf[index] = 123;
                    budf[index++] = 65;
                    budf[index++] = temp[0];
                    i = i + 1;
                }

                label113:
                while(true) {
                    while(true) {
                        if(i >= strLen) {
                            break label113;
                        }

                        if(strLen - i == 1) {
                            if(curset == 65) {
                                budf[index++] = temp[i];
                                ++i;
                            } else {
                                curset = 65;
                                budf[index++] = 123;
                                budf[index++] = 65;
                                budf[index++] = temp[i];
                                ++i;
                            }
                        } else if(strLen - i == 2) {
                            if(curset == 65) {
                                budf[index++] = temp[i];
                                budf[index++] = temp[i + 1];
                                i += 2;
                            } else if(isnum(temp[i]) && isnum(temp[i + 1])) {
                                budf[index++] = seta2c(temp[i], temp[i + 1]);
                                i += 2;
                            } else {
                                curset = 65;
                                budf[index++] = 123;
                                budf[index++] = 65;
                                budf[index++] = temp[i];
                                budf[index++] = temp[i + 1];
                                i += 2;
                            }
                        } else if(strLen - i == 3) {
                            if(curset == 65) {
                                budf[index++] = temp[i];
                                budf[index++] = temp[i + 1];
                                i += 2;
                            } else if(isnum(temp[i]) && isnum(temp[i + 1])) {
                                budf[index++] = seta2c(temp[i], temp[i + 1]);
                                i += 2;
                            } else {
                                curset = 65;
                                budf[index++] = 123;
                                budf[index++] = 65;
                                budf[index++] = temp[i];
                                ++i;
                            }
                        } else if(curset == 65) {
                            if(isnum(temp[i]) && isnum(temp[i + 1]) && isnum(temp[i + 2]) && isnum(temp[i + 3])) {
                                curset = 67;
                                budf[index++] = 123;
                                budf[index++] = 67;
                                budf[index++] = seta2c(temp[i], temp[i + 1]);
                                budf[index++] = seta2c(temp[i + 2], temp[i + 3]);
                                i += 4;
                            } else {
                                budf[index++] = temp[i];
                                ++i;
                            }
                        } else if(isnum(temp[i]) && isnum(temp[i + 1])) {
                            budf[index++] = seta2c(temp[i], temp[i + 1]);
                            i += 2;
                        } else {
                            curset = 65;
                            budf[index++] = 123;
                            budf[index++] = 65;
                            budf[index++] = temp[i];
                            ++i;
                        }
                    }
                }
            }

            data[0] = index;

            for(i = 0; i < index; ++i) {
                data[1 + i] = budf[i];
            }

            return this.byteMerger(merger, data);
        } else {
            data[0] = (byte)strLen;

            for(i = 0; i < strLen; ++i) {
                data[1 + i] = temp[i];
            }

            return this.byteMerger(merger, data);
        }
    }

    static boolean isnum(byte ch) {
        return ch >= 48 && ch <= 57;
    }

    static byte seta2c(byte a, byte b) {
        return (byte)((a - 48) * 10 + (b - 48));
    }

    public byte[] NFCP_printQRcode(int Ver, int Level, String str) {
        if(Ver > 20) {
            return new byte[0];
        } else if(Level > 3) {
            return new byte[0];
        } else {
            try {
                byte[] by = str.getBytes("GB2312");
                int len = by.length;
                byte[] aCmdBuf = new byte[]{29, 108, (byte)Ver, (byte)Level, (byte)(len % 256), (byte)(len / 256)};
                return this.byteMerger(aCmdBuf, by);
            } catch (UnsupportedEncodingException var7) {
                var7.printStackTrace();
                return new byte[0];
            }
        }
    }

    public byte[] NFCP_printBitmap(Bitmap bitmap, int mode) {
        byte m = (byte)mode;
        if(m < 0 || m > 3) {
            m = 0;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int bytewidth;
        if(width % 8 != 0) {
            bytewidth = width / 8 + 1;
        } else {
            bytewidth = width / 8;
        }

//        byte[] data = null;
        byte[] data = new byte[]{29, 118, 48, m, (byte)(bytewidth % 256), (byte)(bytewidth / 256), (byte)(height % 256), (byte)(height / 256)};
        byte[] pixel = new byte[bytewidth * height];
        int comlen = 0;

        for(int i = 0; i < height; ++i) {
            for(int j = 0; j < bytewidth; ++j) {
                byte temp = 0;

                for(int k = 0; k < 8; ++k) {
                    if(j * 8 + k < width) {
                        int pixelColor = bitmap.getPixel(j * 8 + k, i);
                        if(pixelColor != -1 && pixelColor != 0) {
                            temp |= (byte)(128 >> k);
                        }
                    }
                }

                pixel[comlen++] = temp;
            }
        }

        return this.byteMerger(data, pixel);
    }

    public byte[] NFCP_createPage(int x, int y) {
        byte[] arrayOfByte = new byte[]{28, 76, 112, (byte)(x % 256), (byte)(x / 256), (byte)(y % 256), (byte)(y / 256), 0};
        return arrayOfByte;
    }

    public byte[] NFCP_createPage_Clear(int x, int y, int option) {
        if(option != 0 && option != 1) {
            option = 1;
        }

        byte[] arrayOfByte = new byte[]{28, 76, 112, (byte)(x % 256), (byte)(x / 256), (byte)(y % 256), (byte)(y / 256), (byte)option};
        return arrayOfByte;
    }

    public byte[] NFCP_printPage(int horizontal, int skip) {
        byte[] arrayOfByte = new byte[]{28, 76, 111, (byte)horizontal, (byte)skip, 0, 0};
        return arrayOfByte;
    }

    public byte[] NFCP_Page_drawLine(int w, int sx, int sy, int ex, int ey) {
        byte[] arrayOfByte = new byte[]{28, 76, 108, (byte)w, 1, (byte)(sx % 256), (byte)(sx / 256), (byte)(sy % 256), (byte)(sy / 256), (byte)(ex % 256), (byte)(ex / 256), (byte)(ey % 256), (byte)(ey / 256)};
        return arrayOfByte;
    }

    public byte[] NFCP_Page_location(int skip, int height) {
        if(skip < 0 || skip > 2) {
            skip = 2;
        }

        byte[] arrayOfByte = new byte[]{28, 76, 29, 12, (byte)skip, (byte)(height % 256), (byte)(height / 256)};
        return arrayOfByte;
    }

    public byte[] NFCP_Page_drawLine(int sx, int sy, int ex, int ey) {
        byte[] arrayOfByte = new byte[]{28, 76, 108, 2, 1, (byte)(sx % 256), (byte)(sx / 256), (byte)(sy % 256), (byte)(sy / 256), (byte)(ex % 256), (byte)(ex / 256), (byte)(ey % 256), (byte)(ey / 256)};
        return arrayOfByte;
    }

    public byte[] NFCP_Page_drawRectangle(int w, int sx, int sy, int ex, int ey) {
        byte[] arrayOfByte1 = this.NFCP_Page_drawLine(w, sx, sy, ex, sy);
        byte[] arrayOfByte2 = this.NFCP_Page_drawLine(w, sx, ey, ex, ey);
        byte[] arrayOfByte3 = this.NFCP_Page_drawLine(w, sx, sy, sx, ey);
        byte[] arrayOfByte4 = this.NFCP_Page_drawLine(w, ex, sy, ex, ey);
        return this.ConcatArrays(arrayOfByte1, new byte[][]{arrayOfByte2, arrayOfByte3, arrayOfByte4});
    }

    public byte[] NFCP_Page_setTextBox(int x, int y, int width, int height, String str, int fontsize, int rotate, int bold, boolean underline, boolean reverse) throws UnsupportedEncodingException {
        byte tem1 = 0;
        byte tem2 = 0;
        byte[] data = new byte[]{28, 76, 84, (byte)(x % 256), (byte)(x / 256), (byte)(y % 256), (byte)(y / 256), (byte)(width % 256), (byte)(width / 256), (byte)(height % 256), (byte)(height / 256), 0, 0};
//        byte tem1;
        if(fontsize == 1) {
            tem1 = (byte)(tem1 | 16);
        } else if(fontsize == 2) {
            tem1 = (byte)(tem1 | 0);
        } else if(fontsize == 3) {
            tem1 = (byte)(tem1 | 32);
        } else if(fontsize == 4) {
            tem1 = (byte)(tem1 | 0);
            tem2 = (byte)(tem2 | 10);
        } else if(fontsize == 5) {
            tem1 = (byte)(tem1 | 32);
            tem2 = (byte)(tem2 | 10);
        } else if(fontsize == 6) {
            tem1 = (byte)(tem1 | 0);
            tem2 = (byte)(tem2 | 15);
        } else if(fontsize == 7) {
            tem1 = (byte)(tem1 | 32);
            tem2 = (byte)(tem2 | 15);
        } else {
            tem1 = (byte)(tem1 | 0);
        }

        if(rotate == 0) {
            tem2 = (byte)(tem2 | 0);
        } else if(rotate == 1) {
            tem2 = (byte)(tem2 | 64);
        } else if(rotate == 2) {
            tem2 = (byte)(tem2 | 128);
        } else if(rotate == 3) {
            tem2 = (byte)(tem2 | 192);
        } else {
            tem2 = (byte)(tem2 | 0);
        }

        if(bold != 0) {
            tem1 = (byte)(tem1 | 2);
        }

        if(underline) {
            tem1 = (byte)(tem1 | 1);
        }

        if(reverse) {
            tem1 = (byte)(tem1 | 4);
        }

        data[11] = tem1;
        data[12] = tem2;
        byte[] temp = this.printContent(str);
        byte[] a = new byte[]{0};
        return this.byteMerger(this.byteMerger(data, temp), a);
    }

    public byte[] NFCP_Page_setSuperTextBox(int x, int y, int width, int height, LPK130NW.Parameter... parameters) throws UnsupportedEncodingException {
//        byte tem1 = false;
//        byte tem2 = false;
        byte[] result = new byte[0];
        ArrayList<byte[]> byteList = new ArrayList();
        byte[] data = new byte[]{28, 76, 85, (byte)(x % 256), (byte)(x / 256), (byte)(y % 256), (byte)(y / 256), (byte)(width % 256), (byte)(width / 256), (byte)(height % 256), (byte)(height / 256), 0, 0};
        byteList.add(data);
//        int fontsize = true;
//        int rotate = false;
//        int bold = false;
        boolean underline = false;
        boolean reverse = false;
        String text = "";
        LPK130NW.Parameter[] var17 = parameters;
        int var18 = parameters.length;

        int i;
        for(i = 0; i < var18; ++i) {
            LPK130NW.Parameter parameter = var17[i];
            byte tem1 = 0;
            byte tem2 = 0;
            LPK130NW.Format format = parameter.format;
            int fontsize = format.fontsize;
            int rotate = format.rotate;
            int bold = format.bold;
            underline = format.underline;
            reverse = format.reverse;
            text = parameter.text;
//            byte tem1;
            if(fontsize == 1) {
                tem1 = (byte)(tem1 | 16);
            } else if(fontsize == 2) {
                tem1 = (byte)(tem1 | 0);
            } else if(fontsize == 3) {
                tem1 = (byte)(tem1 | 32);
            } else if(fontsize == 4) {
                tem1 = (byte)(tem1 | 0);
                tem2 = (byte)(tem2 | 10);
            } else if(fontsize == 5) {
                tem1 = (byte)(tem1 | 32);
                tem2 = (byte)(tem2 | 10);
            } else if(fontsize == 6) {
                tem1 = (byte)(tem1 | 0);
                tem2 = (byte)(tem2 | 15);
            } else if(fontsize == 7) {
                tem1 = (byte)(tem1 | 32);
                tem2 = (byte)(tem2 | 15);
            } else {
                tem1 = (byte)(tem1 | 0);
            }

            if(rotate == 0) {
                tem2 = (byte)(tem2 | 0);
            } else if(rotate == 1) {
                tem2 = (byte)(tem2 | 64);
            } else if(rotate == 2) {
                tem2 = (byte)(tem2 | 128);
            } else if(rotate == 3) {
                tem2 = (byte)(tem2 | 192);
            } else {
                tem2 = (byte)(tem2 | 0);
            }

            if(bold != 0) {
                tem1 = (byte)(tem1 | 2);
            }

            if(underline) {
                tem1 = (byte)(tem1 | 1);
            }

            if(reverse) {
                tem1 = (byte)(tem1 | 4);
            }

            byte[] formatCommand = new byte[]{27, 45, tem1, tem2};
            byteList.add(formatCommand);
            byte[] strBytes = this.printContent(text);
            byteList.add(strBytes);
        }

        byteList.add(new byte[]{0});
        int len = byteList.size();
        byte[] temp1 = (byte[])byteList.get(0);

        for(i = 1; i < len; ++i) {
            byte[] temp2 = (byte[])byteList.get(i);
            result = new byte[temp1.length + temp2.length];
            System.arraycopy(temp1, 0, result, 0, temp1.length);
            System.arraycopy(temp2, 0, result, temp1.length, temp2.length);
            temp1 = result;
        }

        return result;
    }

    public byte[] NFCP_Page_Area_Reverse(int startx, int starty, int endx, int endy, boolean reverse) {
        byte[] data = new byte[]{28, 76, 114, (byte)(startx % 256), (byte)(startx / 256), (byte)(starty % 256), (byte)(starty / 256), (byte)(endx % 256), (byte)(endx / 256), (byte)(endy % 256), (byte)(endy / 256), 0};
        if(reverse) {
            data[11] = 1;
        } else {
            data[11] = 0;
        }

        byte[] a = new byte[]{0};
        return this.byteMerger(data, a);
    }

    public byte[] NFCP_Page_setText(int x, int y, String str, int fontsize, int rotate, int bold, boolean underline, boolean reverse) throws UnsupportedEncodingException {
        byte[] arrayOfByte = new byte[]{28, 76, 116, (byte)(x % 256), (byte)(x / 256), (byte)(y % 256), (byte)(y / 256), 0, 0};
        byte tem1 = 0;
        byte tem2 = 0;
//        byte tem1;
        if(fontsize == 1) {
            tem1 = (byte)(tem1 | 16);
        } else if(fontsize == 2) {
            tem1 = (byte)(tem1 | 0);
        } else if(fontsize == 3) {
            tem1 = (byte)(tem1 | 32);
        } else if(fontsize == 4) {
            tem1 = (byte)(tem1 | 0);
            tem2 = (byte)(tem2 | 10);
        } else if(fontsize == 5) {
            tem1 = (byte)(tem1 | 32);
            tem2 = (byte)(tem2 | 10);
        } else if(fontsize == 6) {
            tem1 = (byte)(tem1 | 0);
            tem2 = (byte)(tem2 | 15);
        } else if(fontsize == 7) {
            tem1 = (byte)(tem1 | 32);
            tem2 = (byte)(tem2 | 15);
        } else {
            tem1 = (byte)(tem1 | 0);
        }

        if(rotate == 0) {
            tem2 = (byte)(tem2 | 0);
        } else if(rotate == 1) {
            tem2 = (byte)(tem2 | 64);
        } else if(rotate == 2) {
            tem2 = (byte)(tem2 | 128);
        } else if(rotate == 3) {
            tem2 = (byte)(tem2 | 192);
        } else {
            tem2 = (byte)(tem2 | 0);
        }

        if(bold != 0) {
            tem1 = (byte)(tem1 | 2);
        }

        if(underline) {
            tem1 = (byte)(tem1 | 1);
        }

        if(reverse) {
            tem1 = (byte)(tem1 | 4);
        }

        arrayOfByte[7] = tem1;
        arrayOfByte[8] = tem2;
        byte[] temp = this.printContent(str);
        byte[] a = new byte[]{0};
        return this.byteMerger(this.byteMerger(arrayOfByte, temp), a);
    }

    public byte[] NFCP_Page_setText_Zoom(int x, int y, String str, int fontsize, int zoomW, int zoomH, int rotate, int bold, boolean underline, boolean reverse) throws UnsupportedEncodingException {
        byte[] arrayOfByte = new byte[]{28, 76, 116, (byte)(x % 256), (byte)(x / 256), (byte)(y % 256), (byte)(y / 256), 0, 0};
        byte tem1 = 0;
        byte tem2 = 0;
//        byte tem1;
        if(fontsize == 1) {
            tem1 = (byte)(tem1 | 16);
        } else if(fontsize == 2) {
            tem1 = (byte)(tem1 | 0);
        } else if(fontsize == 3) {
            tem1 = (byte)(tem1 | 32);
        } else if(fontsize == 4) {
            tem1 = (byte)(tem1 | 0);
            tem2 = (byte)(tem2 | 10);
        } else if(fontsize == 5) {
            tem1 = (byte)(tem1 | 32);
            tem2 = (byte)(tem2 | 10);
        } else if(fontsize == 6) {
            tem1 = (byte)(tem1 | 0);
            tem2 = (byte)(tem2 | 15);
        } else if(fontsize == 7) {
            tem1 = (byte)(tem1 | 32);
            tem2 = (byte)(tem2 | 15);
        } else {
            tem1 = (byte)(tem1 | 0);
        }

        if(rotate == 0) {
            tem2 = (byte)(tem2 | 0);
        } else if(rotate == 1) {
            tem2 = (byte)(tem2 | 64);
        } else if(rotate == 2) {
            tem2 = (byte)(tem2 | 128);
        } else if(rotate == 3) {
            tem2 = (byte)(tem2 | 192);
        } else {
            tem2 = (byte)(tem2 | 0);
        }

        if(zoomW == 0) {
            tem2 = (byte)(tem2 | 0);
        }

        if(zoomW == 1) {
            tem2 = (byte)(tem2 | 2);
        }

        if(zoomW == 2) {
            tem2 = (byte)(tem2 | 3);
        }

        if(zoomH == 0) {
            tem2 = (byte)(tem2 | 0);
        }

        if(zoomH == 1) {
            tem2 = (byte)(tem2 | 8);
        }

        if(zoomH == 2) {
            tem2 = (byte)(tem2 | 12);
        }

        if(bold != 0) {
            tem1 = (byte)(tem1 | 2);
        }

        if(underline) {
            tem1 = (byte)(tem1 | 1);
        }

        if(reverse) {
            tem1 = (byte)(tem1 | 4);
        }

        arrayOfByte[7] = tem1;
        arrayOfByte[8] = tem2;
        byte[] temp = this.printContent(str);
        byte[] a = new byte[]{0};
        return this.byteMerger(this.byteMerger(arrayOfByte, temp), a);
    }

    public byte[] NFCP_Page_drawBar(int x, int y, String str, int barcodetype, int rotate, int barWidth, int barHeight) throws UnsupportedEncodingException {
        byte[] data = new byte[522];
        data[0] = 28;
        data[1] = 76;
        data[2] = 98;
        data[3] = (byte)(x % 256);
        data[4] = (byte)(x / 256);
        data[5] = (byte)(y % 256);
        data[6] = (byte)(y / 256);
        if(barcodetype >= 0 && barcodetype <= 8) {
            data[7] = (byte)barcodetype;
        } else {
            data[7] = 1;
        }

        rotate %= 4;
        data[8] = (byte)(rotate * 64 | barWidth);
        data[9] = (byte)barHeight;
//        int i = false;
//        byte[] temp = null;
        byte[] temp = str.getBytes("GB2312");
        int strLen = temp.length;
        byte[] budf;
        int i;
        if(barcodetype == 1) {
            budf = new byte[256];
            byte index = 0;
//            byte index;
            if(strLen <= 4) {
//                byte curset = true;
                index = (byte)(index + 1);
                budf[index] = 123;
                budf[index++] = 65;

                for(i = 0; i < strLen; ++i) {
                    budf[index++] = temp[i];
                }
            } else {
                 i = 0;
                byte curset;
                if(isnum(temp[0]) && isnum(temp[1]) && isnum(temp[2]) && isnum(temp[3])) {
                    curset = 67;
                    index = (byte)(index + 1);
                    budf[index] = 123;
                    budf[index++] = 67;
                    budf[index++] = seta2c(temp[0], temp[1]);
                    budf[index++] = seta2c(temp[2], temp[3]);
                    i = i + 4;
                } else {
                    curset = 65;
                    index = (byte)(index + 1);
                    budf[index] = 123;
                    budf[index++] = 65;
                    budf[index++] = temp[0];
                    i = i + 1;
                }

                label114:
                while(true) {
                    while(true) {
                        if(i >= strLen) {
                            break label114;
                        }

                        if(strLen - i == 1) {
                            if(curset == 65) {
                                budf[index++] = temp[i];
                                ++i;
                            } else {
                                curset = 65;
                                budf[index++] = 123;
                                budf[index++] = 65;
                                budf[index++] = temp[i];
                                ++i;
                            }
                        } else if(strLen - i == 2) {
                            if(curset == 65) {
                                budf[index++] = temp[i];
                                budf[index++] = temp[i + 1];
                                i += 2;
                            } else if(isnum(temp[i]) && isnum(temp[i + 1])) {
                                budf[index++] = seta2c(temp[i], temp[i + 1]);
                                i += 2;
                            } else {
                                curset = 65;
                                budf[index++] = 123;
                                budf[index++] = 65;
                                budf[index++] = temp[i];
                                budf[index++] = temp[i + 1];
                                i += 2;
                            }
                        } else if(strLen - i == 3) {
                            if(curset == 65) {
                                budf[index++] = temp[i];
                                budf[index++] = temp[i + 1];
                                i += 2;
                            } else if(isnum(temp[i]) && isnum(temp[i + 1])) {
                                budf[index++] = seta2c(temp[i], temp[i + 1]);
                                i += 2;
                            } else {
                                curset = 65;
                                budf[index++] = 123;
                                budf[index++] = 65;
                                budf[index++] = temp[i];
                                ++i;
                            }
                        } else if(curset == 65) {
                            if(isnum(temp[i]) && isnum(temp[i + 1]) && isnum(temp[i + 2]) && isnum(temp[i + 3])) {
                                curset = 67;
                                budf[index++] = 123;
                                budf[index++] = 67;
                                budf[index++] = seta2c(temp[i], temp[i + 1]);
                                budf[index++] = seta2c(temp[i + 2], temp[i + 3]);
                                i += 4;
                            } else {
                                budf[index++] = temp[i];
                                ++i;
                            }
                        } else if(isnum(temp[i]) && isnum(temp[i + 1])) {
                            budf[index++] = seta2c(temp[i], temp[i + 1]);
                            i += 2;
                        } else {
                            curset = 65;
                            budf[index++] = 123;
                            budf[index++] = 65;
                            budf[index++] = temp[i];
                            ++i;
                        }
                    }
                }
            }

            data[10] = index;

            for(i = 0; i < index; ++i) {
                data[11 + i] = budf[i];
            }

            byte[] data1 = new byte[11 + index];

            for(i = 0; i < 11 + index; ++i) {
                data1[i] = data[i];
            }

            return data1;
        } else {
            data[10] = (byte)strLen;

            for(i = 0; i < strLen; ++i) {
                data[11 + i] = temp[i];
            }

            budf = new byte[11 + strLen];

            for(i = 0; i < 11 + strLen; ++i) {
                budf[i] = data[i];
            }

            return budf;
        }
    }

    public byte[] NFCP_Page_drawBar_Align(int x, int y, int alignStyle, String str, int barcodetype, int rotate, int barWidth, int barHeight) throws UnsupportedEncodingException {
        int pageWidth = 576;
        int barLength = this.getBarcodeLength(barWidth, str);
        int newx = x;
        if(barLength > 0) {
            switch(alignStyle) {
                case 0:
                    newx = pageWidth - x - barLength > 0?x + (pageWidth - x - barLength) / 2:x;
                    break;
                case 1:
                    newx = x;
                    break;
                case 2:
                    newx = pageWidth - barLength > 0?pageWidth - barLength:0;
            }
        }

        return this.NFCP_Page_drawBar(newx, y, str, barcodetype, rotate, barWidth, barHeight);
    }

    private int getBarcodeLength(int barWidth, String text) {
//        int srclen = false;
        double barlength = 3.0D;
//        int i = false;
        Object var7 = null;

        int srclen;
        byte[] buf;
        try {
            buf = text.getBytes("GBK");
            srclen = buf.length;
        } catch (UnsupportedEncodingException var10) {
            var10.printStackTrace();
            return -1;
        }

//        byte index = false;
        if(srclen <= 4) {
//            byte curset = true;
            barlength += (double)(srclen + 1);
            return (int)barlength * barWidth * 11;
        } else {
            int i = 0;
//            int i;
            byte curset;
            if(isnum(buf[0]) && isnum(buf[1]) && isnum(buf[2]) && isnum(buf[3])) {
                curset = 67;
                i = i + 4;
                barlength += 2.0D;
            } else {
                curset = 65;
                i = i + 1;
                barlength += 2.0D;
            }

            while(true) {
                while(i < srclen) {
                    if(srclen - i == 1) {
                        if(curset == 65) {
                            ++i;
                            ++barlength;
                        } else {
                            curset = 65;
                            ++i;
                            barlength += 2.0D;
                        }
                    } else if(srclen - i == 2) {
                        if(curset == 65) {
                            i += 2;
                            barlength += 2.0D;
                        } else if(isnum(buf[i]) && isnum(buf[i + 1])) {
                            i += 2;
                            ++barlength;
                        } else {
                            curset = 65;
                            i += 2;
                            barlength += 3.0D;
                        }
                    } else if(srclen - i == 3) {
                        if(curset == 65) {
                            i += 2;
                            barlength += 2.0D;
                        } else if(isnum(buf[i]) && isnum(buf[i + 1])) {
                            i += 2;
                            ++barlength;
                        } else {
                            curset = 65;
                            ++i;
                            barlength += 2.0D;
                        }
                    } else if(curset == 65) {
                        if(isnum(buf[i]) && isnum(buf[i + 1]) && isnum(buf[i + 2]) && isnum(buf[i + 3])) {
                            curset = 67;
                            i += 4;
                            barlength += 3.0D;
                        } else {
                            ++i;
                            ++barlength;
                        }
                    } else if(isnum(buf[i]) && isnum(buf[i + 1])) {
                        i += 2;
                        ++barlength;
                    } else {
                        curset = 65;
                        ++i;
                        barlength += 2.0D;
                    }
                }

                return (int)barlength * barWidth * 11;
            }
        }
    }

    public byte[] NFCP_Page_printQrCode(int x, int y, int rotate, int Ver, int lel, String Text) {
        byte n = 0;
        if(rotate == 1) {
            n = (byte)(n | 64);
        } else if(rotate == 2) {
            n = (byte)(n | 128);
        } else if(rotate == 3) {
            n = (byte)(n | 192);
        }

        try {
            byte[] dbyte = Text.getBytes("GB2312");
            int len = dbyte.length;
            byte[] data = new byte[]{28, 76, 66, (byte)(x % 256), (byte)(x / 256), (byte)(y % 256), (byte)(y / 256), 2, n, (byte)((lel << 6) + Ver), (byte)(len % 256), (byte)(len / 256)};
            return this.byteMerger(data, dbyte);
        } catch (UnsupportedEncodingException var11) {
            var11.printStackTrace();
            return new byte[0];
        }
    }

    public byte[] NFCP_Page_printPDF417(int x, int y, int rotate, int width, String Text) {
        byte n = 0;
        if(rotate == 1) {
            n = (byte)(n | 64);
        } else if(rotate == 2) {
            n = (byte)(n | 128);
        } else if(rotate == 3) {
            n = (byte)(n | 192);
        }

        try {
            byte[] dbyte = Text.getBytes("GB2312");
            int len = dbyte.length;
            byte[] data = new byte[]{28, 76, 66, (byte)(x % 256), (byte)(x / 256), (byte)(y % 256), (byte)(y / 256), 0, n, (byte)width, (byte)(len % 256), (byte)(len / 256)};
            return this.byteMerger(data, dbyte);
        } catch (UnsupportedEncodingException var10) {
            var10.printStackTrace();
            return new byte[0];
        }
    }

    public byte[] NFCP_Page_printBitmap(int x, int y, Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int bytewidth;
        if(width % 8 != 0) {
            bytewidth = width / 8 + 1;
        } else {
            bytewidth = width / 8;
        }

//        byte[] data = null;
        byte[] data = new byte[]{28, 76, 109, (byte)(x % 256), (byte)(x / 256), (byte)(y % 256), (byte)(y / 256), (byte)(bytewidth % 256), (byte)(bytewidth / 256), (byte)(height % 256), (byte)(height / 256), 0};
        byte[] pixel = new byte[bytewidth * height];
        int comlen = 0;

        for(int i = 0; i < height; ++i) {
            for(int j = 0; j < bytewidth; ++j) {
                byte temp = 0;

                for(int k = 0; k < 8; ++k) {
                    if(j * 8 + k < width) {
                        int pixelColor = bitmap.getPixel(j * 8 + k, i);
                        if(pixelColor != -1 && pixelColor != 0) {
                            temp |= (byte)(128 >> k);
                        }
                    }
                }

                pixel[comlen++] = temp;
            }
        }

        return this.byteMerger(data, pixel);
    }

    private byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    private byte[] ConcatArrays(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        byte[][] var4 = rest;
        int offset = rest.length;

        for(int var6 = 0; var6 < offset; ++var6) {
            byte[] array = var4[var6];
            totalLength += array.length;
        }

        byte[] result = Arrays.copyOf(first, totalLength);
        offset = first.length;
        byte[][] var11 = rest;
        int var12 = rest.length;

        for(int var8 = 0; var8 < var12; ++var8) {
            byte[] array = var11[var8];
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }

        return result;
    }

    public class Parameter {
        public LPK130NW.Format format;
        public String text = "";

        public Parameter(LPK130NW.Format format, String text) {
            this.format = format;
            this.text = text;
        }
    }

    public class Format {
        public int fontsize = 2;
        public int rotate = 0;
        public int bold = 0;
        public boolean underline = false;
        public boolean reverse = false;

        public Format(int fontsize, int rotate, int bold, boolean underline, boolean reverse) {
            this.fontsize = fontsize;
            this.rotate = rotate;
            this.bold = bold;
            this.underline = underline;
            this.reverse = reverse;
        }
    }
}

