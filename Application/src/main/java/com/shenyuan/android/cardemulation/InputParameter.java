package com.shenyuan.android.cardemulation;

import java.util.Calendar;

/**
 * Created by pzz on 2016/11/4.
 */
public class InputParameter {
    public String identifier = "1701752218";//编号
    public String authorityCode = "310302";//执法机关代码
    public String cardNumber = "3140006147821";//证芯编号
    public String contactInformation = "上海市闵行区虹梅南路1661弄（都市宜家）7号301室";//联系方式
    public String carBrand = "沪A12345";//车辆牌号
    public String carType = "轿车";//车辆类型
    public String ssuingAuthority = "上海市公安局交通警察总队车辆管理所";//发证机关
    public Calendar calendar;
    public int year = 2016;
    public int month = 11;
    public int date = 02;
    public int hour = 11;
    public int minute = 21;
    public int second = 34;
    public String peccancyLocale = "徐虹北路虹桥路东";//违章地点
    public String punishLocale = "徐虹北路虹桥路东";//处罚地点
    public String peccancyContent = "驾驶营运客车在高速公路行车道上停车";//违章内容
    public String peccancyCode = "47040";//违章内容代码
    public String peccancyLaw = "《中华人民共和国道路交通安全法实施条例》第八十二条第一项的规定";//违章法律依据
    public String punishLaw = "《中华人民共和国道路交通安全法》第九十条";//处罚结果法律依据
    public String reviewCompany = "上海市公安局交警总队";//复议单位
    public String litigationCompany = "徐汇区人民法院";//诉讼单位
    public String punishScore = "12";//处罚分数
    public String policeNumber = "99999942";//警号
    public String fineLineCapital = "贰佰";//罚款额度大写
    public String fineLinelowercase = "200";//罚款额度小写

    public String punishContent = "";//处罚内容
    public int punishMode = 02;//处罚方式
    public static final int FINE = 0x01;//罚款
    public static final int DETAIN_CAR = 0x02;//扣车


}
