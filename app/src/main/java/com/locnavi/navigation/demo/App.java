package com.locnavi.navigation.demo;

import android.app.Application;
import android.os.Build;

import com.locnavi.websdk.LocNaviWebSDK;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        String uuid = App.getDeviceID();
        LocNaviWebSDK.init(new LocNaviWebSDK.Configuration
                .Builder(this)
                .serverUrl("https://mo.sailstech.com")
                .appKey(Constants.appKey)
                .userId(uuid)
//                .uploadApi("http://20.205.107.64:82/api/receive/LoraPosition")     //定时上传定位的api地址
//                .uploadInterval(3000)   //定时上传时间间隔
//                .uuids(new String[] {"FDA50693-A4E2-4FB1-AFCF-C6EB07647825", "441651AA-4036-11E6-BEB8-9E71128CAE77"})
                .debug(true)
                .build());
    }

    public static String getDeviceID() {
        String m_szDevIDShort = ""  //we make this look like a valid IMEI
                + Build.BOARD.length() % 10
                + Build.BRAND.length() % 10
                + Build.CPU_ABI.length() % 10
                + Build.DEVICE.length() % 10
                + Build.DISPLAY.length() % 10
                + Build.HOST.length() % 10
                + Build.ID.length() % 10
                + Build.MANUFACTURER.length() % 10
                + Build.MODEL.length() % 10
                + Build.PRODUCT.length() % 10
                + Build.TAGS.length() % 10
                + Build.TYPE.length() % 10
                + Build.USER.length() % 10;  //13 digits
        String serial;
        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
            //可能出现返回unknown的情况，这时候用以太网的mac地址
            if (serial.equals("unknown") || serial.equals("")) {
                serial = getMACAddress("eth0");
                if (serial.equals("unknown") || serial.equals("")) {
                    serial = getMACAddress("wlan0");
                }
            }

            //API>=9 使用serial号
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
        }
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    /**
     * Returns MAC address of the given interface name.
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return  mac address or empty string
     */
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac==null) return "";
                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) buf.append(String.format("%02X:",aMac));
                if (buf.length()>0) buf.deleteCharAt(buf.length()-1);
                return buf.toString();
            }
        } catch (Exception ignored) { } // for now eat exceptions
        return "";
    }
}
