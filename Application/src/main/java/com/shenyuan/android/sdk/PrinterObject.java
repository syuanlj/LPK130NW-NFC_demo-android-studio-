package com.shenyuan.android.sdk;

/**
 * Created by sheny on 2018/7/12.
 */
import java.io.Serializable;
import java.util.ArrayList;

public class PrinterObject implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<byte[]> list = new ArrayList();

    public PrinterObject() {
    }

    public void add(byte[] bts) {
        this.list.add(bts);
    }

    public int size() {
        return this.list.size();
    }

    public byte[] get(int i) {
        return (byte[])this.list.get(i);
    }
}

