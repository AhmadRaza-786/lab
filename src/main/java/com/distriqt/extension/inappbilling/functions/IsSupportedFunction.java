package com.distriqt.extension.inappbilling.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.adobe.fre.FREWrongThreadException;
import com.distriqt.extension.inappbilling.InAppBillingContext;
import com.distriqt.extension.inappbilling.util.FREUtils;
import com.distriqt.extension.inappbilling.util.Resources;

public class IsSupportedFunction implements FREFunction {
    public FREObject call(FREContext context, FREObject[] passedArgs) {
        try {
            FREUtils.log("DEBUG", "LIST RESOURCES: %s", context.getActivity().getPackageName());
            Resources.listResources(context.getActivity().getPackageName());
            FREUtils.log("DEBUG", "LIST RESOURCES: %s", "android.support.v7.appcompat");
            Resources.listResources("android.support.v7.appcompat");
            return FREObject.newObject(((InAppBillingContext) context).controller().isSupported().booleanValue());
        } catch (FREWrongThreadException e) {
            FREUtils.handleException(e);
            return null;
        }
    }
}
