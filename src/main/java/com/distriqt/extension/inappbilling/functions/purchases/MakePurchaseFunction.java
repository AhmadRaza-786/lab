package com.distriqt.extension.inappbilling.functions.purchases;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.distriqt.extension.inappbilling.InAppBillingContext;
import com.distriqt.extension.inappbilling.PurchaseRequest;
import com.distriqt.extension.inappbilling.util.FREUtils;

public class MakePurchaseFunction implements FREFunction {
    private static String TAG = MakePurchaseFunction.class.getSimpleName();

    public FREObject call(FREContext context, FREObject[] args) {
        FREUtils.log(TAG, "call", new Object[0]);
        try {
            InAppBillingContext ctx = (InAppBillingContext) context;
            Boolean success = false;
            if (ctx.v) {
                PurchaseRequest request = new PurchaseRequest();
                request.productId = args[0].getProperty("productId").getAsString();
                request.quantity = args[0].getProperty("quantity").getAsInt();
                request.developerPayload = args[0].getProperty("developerPayload").getAsString();
                request.applicationUsername = args[0].getProperty("applicationUsername").getAsString();
                success = Boolean.valueOf(ctx.controller().makePurchase(request));
            }
            return FREObject.newObject(success.booleanValue());
        } catch (Exception e) {
            FREUtils.handleException(context, e);
            return null;
        }
    }
}
