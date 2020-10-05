package com.adobe.air.net;

import com.distriqt.extension.inappbilling.BuildConfig;
import java.util.Vector;

public class NetworkInterface {
    public boolean active = false;
    private Vector<InterfaceAddress> addresses = new Vector<>();
    public String displayName = BuildConfig.FLAVOR;
    public String hardwareAddress = BuildConfig.FLAVOR;
    public int mtu = -1;
    public String name = BuildConfig.FLAVOR;
    public NetworkInterface parent = null;
    public NetworkInterface subInterfaces = null;

    public void addAddress(InterfaceAddress interfaceAddress) {
        this.addresses.add(interfaceAddress);
    }

    public int GetAddressesCount() {
        return this.addresses.size();
    }

    public InterfaceAddress GetAddress(int i) {
        return this.addresses.elementAt(i);
    }
}
