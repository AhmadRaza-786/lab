package com.adobe.air.net;

import com.distriqt.extension.inappbilling.BuildConfig;

public class InterfaceAddress {
    public String address = BuildConfig.FLAVOR;
    public String broadcast = BuildConfig.FLAVOR;
    public String ipVersion = "IPv4";
    public int prefixLength = -1;
}
