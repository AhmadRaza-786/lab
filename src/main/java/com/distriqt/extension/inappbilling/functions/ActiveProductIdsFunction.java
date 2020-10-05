package com.distriqt.extension.inappbilling.functions;

import com.adobe.fre.FREArray;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.distriqt.extension.inappbilling.InAppBillingContext;
import com.distriqt.extension.inappbilling.util.FREUtils;
import java.util.ArrayList;

public class ActiveProductIdsFunction implements FREFunction {
    private static String TAG = CleanupFunction.class.getSimpleName();

    public FREObject call(FREContext context, FREObject[] args) {
        FREUtils.log(TAG, "call", new Object[0]);
        try {
            InAppBillingContext ctx = (InAppBillingContext) context;
            if (!ctx.v) {
                return FREArray.newArray(0);
            }
            ArrayList<String> productIds = ctx.controller().activeProductIds();
            FREArray freProductIds = FREArray.newArray(productIds.size());
            for (int i = 0; i < productIds.size(); i++) {
                freProductIds.setObjectAt((long) i, FREObject.newObject(productIds.get(i)));
            }
            return freProductIds;
        } catch (Exception e) {
            FREUtils.handleException(context, e);
            return null;
        }
    }
}
