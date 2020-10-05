package com.distriqt.extension.inappbilling.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.adobe.fre.FREWrongThreadException;
import com.distriqt.extension.inappbilling.InAppBillingContext;

public class VersionFunction implements FREFunction {
    public FREObject call(FREContext context, FREObject[] passedArgs) {
        try {
            return FREObject.newObject(InAppBillingContext.VERSION);
        } catch (FREWrongThreadException e) {
            return null;
        }
    }
}
