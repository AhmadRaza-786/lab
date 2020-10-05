package com.distriqt.extension.inappbilling.util;

import android.os.Bundle;

public class DebugUtil {
    public static String bundleToString(Bundle bundle) {
        if (bundle == null) {
            return "null";
        }
        String string = "Bundle{\n";
        for (String key : bundle.keySet()) {
            string = string + " " + key + " => " + bundle.get(key) + ";\n";
        }
        return string + " }Bundle";
    }
}
