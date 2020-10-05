package com.distriqt.extension.inappbilling;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREExtension;
import com.distriqt.extension.inappbilling.util.FREUtils;

public class InAppBillingExtension implements FREExtension {
    public static String ID = "com.distriqt.inappbilling";
    public static InAppBillingContext context;

    public FREContext createContext(String arg0) {
        context = new InAppBillingContext();
        FREUtils.ID = ID;
        FREUtils.context = context;
        return context;
    }

    public void dispose() {
        if (context != null) {
            context.dispose();
        }
        context = null;
    }

    public void initialize() {
    }
}
