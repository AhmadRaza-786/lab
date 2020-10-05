package com.adobe.air.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import com.distriqt.extension.inappbilling.BuildConfig;
import java.util.Vector;

class AndroidNetworkInfo {
    private static final String LOG_TAG = "AndroidNetworkInfo";
    private static AndroidNetworkInfo sAndroidNetworkInfo = null;
    private Context mContext;
    private Vector<NetworkInterface> mInterfaces = new Vector<>();

    public static AndroidNetworkInfo GetAndroidNetworkInfo(Context context) {
        if (sAndroidNetworkInfo == null) {
            sAndroidNetworkInfo = new AndroidNetworkInfo(context);
        }
        return sAndroidNetworkInfo;
    }

    private AndroidNetworkInfo(Context context) {
        this.mContext = context;
    }

    public int GetNetworkInterfacesCount() {
        this.mInterfaces.clear();
        enumerateNetworkInterfaces();
        return this.mInterfaces.size();
    }

    public void InterfacesReadComplete() {
        this.mInterfaces.clear();
    }

    private void enumerateNetworkInterfaces() {
        NetworkInterface wifiData;
        try {
            NetworkInfo[] allNetworkInfo = ((ConnectivityManager) this.mContext.getSystemService("connectivity")).getAllNetworkInfo();
            for (NetworkInfo networkInfo : allNetworkInfo) {
                switch (networkInfo.getType()) {
                    case 1:
                        wifiData = getWifiData(networkInfo);
                        break;
                    default:
                        wifiData = getDefaultData(networkInfo);
                        break;
                }
                this.mInterfaces.add(wifiData);
            }
        } catch (Exception e) {
            this.mInterfaces.clear();
        }
    }

    public NetworkInterface GetNetworkInterface(int i) {
        if (i < 0 || i >= this.mInterfaces.size()) {
            return null;
        }
        return this.mInterfaces.elementAt(i);
    }

    /* access modifiers changed from: package-private */
    public NetworkInterface getWifiData(NetworkInfo networkInfo) {
        NetworkInterface networkInterface = new NetworkInterface();
        WifiManager wifiManager = (WifiManager) this.mContext.getSystemService("wifi");
        if (wifiManager == null) {
        }
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        if (connectionInfo == null) {
        }
        networkInterface.active = networkInfo.isConnected();
        networkInterface.displayName = networkInfo.getTypeName();
        networkInterface.name = networkInfo.getTypeName();
        if (connectionInfo != null) {
            String macAddress = connectionInfo.getMacAddress();
            if (macAddress == null) {
                macAddress = BuildConfig.FLAVOR;
            }
            networkInterface.hardwareAddress = macAddress;
        }
        networkInterface.addAddress(getAddress(networkInfo));
        return networkInterface;
    }

    /* access modifiers changed from: package-private */
    public NetworkInterface getDefaultData(NetworkInfo networkInfo) {
        NetworkInterface networkInterface = new NetworkInterface();
        networkInterface.active = networkInfo.isConnected();
        networkInterface.displayName = networkInfo.getTypeName();
        networkInterface.name = networkInfo.getTypeName();
        return networkInterface;
    }

    /* access modifiers changed from: package-private */
    public InterfaceAddress getAddress(NetworkInfo networkInfo) {
        InterfaceAddress interfaceAddress = new InterfaceAddress();
        if (!networkInfo.isConnected()) {
            return null;
        }
        WifiManager wifiManager = (WifiManager) this.mContext.getSystemService("wifi");
        if (wifiManager == null) {
            return null;
        }
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        if (dhcpInfo != null) {
            interfaceAddress.address = Formatter.formatIpAddress(dhcpInfo.ipAddress);
            interfaceAddress.prefixLength = Integer.bitCount(dhcpInfo.netmask);
            interfaceAddress.broadcast = Formatter.formatIpAddress(dhcpInfo.ipAddress | (dhcpInfo.netmask ^ -1));
            interfaceAddress.ipVersion = "IPv4";
        } else {
            WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo == null) {
                return null;
            }
            interfaceAddress.address = Formatter.formatIpAddress(connectionInfo.getIpAddress());
            interfaceAddress.ipVersion = "IPv4";
        }
        return interfaceAddress;
    }
}
