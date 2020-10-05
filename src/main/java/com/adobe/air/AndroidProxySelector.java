package com.adobe.air;

import com.distriqt.extension.inappbilling.BuildConfig;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public final class AndroidProxySelector {
    private static final int LIST_HEAD = 0;
    private static final String LOG_TAG = "AndroidProxySelector";

    public static String getProxyUrl(String str) {
        Proxy proxy;
        try {
            List<Proxy> select = ProxySelector.getDefault().select(new URI(str));
            if (!select.isEmpty() && Proxy.NO_PROXY != (proxy = select.get(0))) {
                if (Proxy.Type.HTTP == proxy.type()) {
                    return proxy.address().toString();
                }
            }
        } catch (IllegalArgumentException | IndexOutOfBoundsException | URISyntaxException e) {
        }
        return BuildConfig.FLAVOR;
    }
}
