package com.distriqt.extension.inappbilling.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.distriqt.extension.inappbilling.InAppBillingContext;
import com.distriqt.extension.inappbilling.util.FREUtils;

public class CanMakePaymentsFunction implements FREFunction {
    public FREObject call(FREContext context, FREObject[] args) {
        try {
            return FREObject.newObject(((InAppBillingContext) context).controller().canMakePayments().booleanValue());
        } catch (Exception e) {
            FREUtils.handleException(e);
            return null;
        }
    }
}
