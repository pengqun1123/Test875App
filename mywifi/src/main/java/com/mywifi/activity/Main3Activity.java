package com.mywifi.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.LinkAddress;
import android.net.ProxyInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.mywifi.R;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Main3Activity extends AppCompatActivity {
    public static final String TAG = "Main3Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wf_activity_main3);


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void testNet(View view) {
        testEthernet();
    }

    /**
     * 检查wang网络的连接状态，以及能否上网
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("PrivateApi")
    private void testEthernet() {
        try {
            //获取ETHERNET_SERVICE
            String ETHERNET_SERVICE = (String) Context.class.getField("ETHERNET_SERVICE").get(null);
            Class<?> etherNetManagerClass = Class.forName("android.net.EthernetManager");
            Class<?> ipConfigurationClass = Class.forName("android.net.IpConfiguration");
            Object ethernetManager = getSystemService(ETHERNET_SERVICE);

            Object getConfiguration = etherNetManagerClass.getDeclaredMethod("getConfiguration").invoke(ethernetManager);
            Log.i(TAG, "ETHERNET_SERVICE : " + ETHERNET_SERVICE);
            //获取EthernetManager中的mService
            Field mService = etherNetManagerClass.getDeclaredField("mService");
            //修改private权限
            mService.setAccessible(true);
            //获取抽象的实例化对象
            Object mServiceObject = mService.get(ethernetManager);
            Class<?> iEthernetManagerClass = Class.forName("android.net.IEthernetManager");
            Method[] methods = iEthernetManagerClass.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals("setEthernetEnabled")) {
                    method.invoke(mServiceObject, true);
                    Log.i(TAG, "mServiceObject : " + mServiceObject);
                }
            }
            Class<?> staticIpConfigurationClass = Class.forName("android.net.StaticIpConfiguration");
            Constructor<?> staticIpConfigConstructor = staticIpConfigurationClass.getDeclaredConstructor(staticIpConfigurationClass);
            Object staticIpConfigInstance = staticIpConfigurationClass.newInstance();
            Constructor<LinkAddress> linkAddressConstructor = LinkAddress.class.getDeclaredConstructor(String.class);
            //实例化linkAddress对象  //192.168.1.1/24--子网掩码长度,24相当于255.255.255.0
            LinkAddress linkAddress = linkAddressConstructor.newInstance("192.168.1.10/24");
            Class<?> iNetAddressClass = Class.forName("android.net.InetAddress");
            //默认网关参数
            byte[] bytes = new byte[]{(byte) 192, (byte) 168, 3, 1};
            Constructor<?>[] addressClassDeclaredConstructors = iNetAddressClass.getDeclaredConstructors();
            InetAddress inetAddress = null;
            for (Constructor<?> constructor : addressClassDeclaredConstructors) {
                if (constructor.getTypeParameters().length == 3) {
                    //获取去有三个参数的构造方法
                    constructor.setAccessible(true);
                    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                    int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
                    //获取主机名
                    String hostName = String.format(Locale.getDefault(), "%d.%d.%d.%d",
                            (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                            (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
                    inetAddress = (InetAddress) constructor.newInstance(2, bytes, hostName);
                }
            }
            //获取staticIpConfiguration里边的所有变量
            Field[] staticIpConfigFields = staticIpConfigInstance.getClass().getDeclaredFields();
            for (Field staticIpConfigField : staticIpConfigFields) {
                //设置成员变量的值
                String fieldName = staticIpConfigField.getName();
                if (fieldName.equals("ipAddress")) {
                    //设置IP地址和子网掩码
                    staticIpConfigField.set(staticIpConfigInstance, linkAddress);
                } else if (fieldName.equals("gateway")) {
                    //设置默认网关
                    staticIpConfigField.set(staticIpConfigInstance, inetAddress);
                } else if (fieldName.equals("domains")) {
                    staticIpConfigField.set(staticIpConfigInstance, "");
                } else if (fieldName.equals("dnsServers")) {
                    //设置DNS
                    staticIpConfigField.set(staticIpConfigField, new ArrayList<InetAddress>());
                }
            }
            Object staticInstance = staticIpConfigConstructor.newInstance(staticIpConfigInstance);
            //存放ipASSignment枚举类参数的集合
            HashMap ipAssignmentMap = new HashMap();
            //存放proxySettings枚举类参数的集合
            HashMap proxySettingsMap = new HashMap();
            Class<?>[] enumClass = ipConfigurationClass.getDeclaredClasses();
            for (Class enumC : enumClass) {
                //获取枚举数组
                Object[] enumConstants = enumC.getEnumConstants();
                if (enumC.getSimpleName().equals("ProxySettings")) {
                    for (Object enu : enumConstants) {
                        //设置代理设置集合 STATIC DHCP UNASSIGNED PAC
                        proxySettingsMap.put(enu.toString(), enu);
                    }
                } else if (enumC.getSimpleName().equals("IpAssignment")) {
                    for (Object enu : enumConstants) {
                        //设置以太网连接模式设置集合 STATIC DHCP UNASSIGNED
                        ipAssignmentMap.put(enu.toString(), enu);
                    }
                }
            }
            //获取ipConfiguration类的构造方法
            Constructor<?>[] ipConfigConstructors = ipConfigurationClass.getDeclaredConstructors();
            Object ipConfigurationInstance = null;
            for (Constructor constru : ipConfigConstructors) {
                //获取ipConfiguration类的4个参数的构造方法
                if (constru.getParameterTypes().length == 4) {//设置以上四种类型
                    //初始化ipConfiguration对象,设置参数
                    ipConfigurationInstance = constru.newInstance(ipAssignmentMap.get("STATIC"),
                            proxySettingsMap.get("NONE"), staticInstance,
                            ProxyInfo.buildDirectProxy(null, 0));
                }
            }
            Log.e(TAG, "ipCon : " + ipConfigurationInstance);
            //获取ipConfiguration类中带有StaticIpConfiguration参数类型的名叫setStaticIpConfiguration的方法
            Method setStaticIpConfiguration = ipConfigurationClass.
                    getDeclaredMethod("setStaticIpConfiguration", staticIpConfigurationClass);
            //修改private方法权限
            setStaticIpConfiguration.setAccessible(true);
            //在ipConfiguration对象中使用setStaticIpConfiguration方法,并传入参数
            setStaticIpConfiguration.invoke(ipConfigurationInstance, staticInstance);
            Object ethernetManagerInstance = etherNetManagerClass.getDeclaredConstructor(Context.class,
                    iEthernetManagerClass).newInstance(this, mServiceObject);
            etherNetManagerClass.getDeclaredMethod("setConfiguration", ipConfigurationClass)
                    .invoke(ethernetManagerInstance, ipConfigurationInstance);
            Log.e(TAG, "getConfiguration : " + getConfiguration.toString());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }


}
