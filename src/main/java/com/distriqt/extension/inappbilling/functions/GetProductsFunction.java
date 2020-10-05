package com.distriqt.extension.inappbilling.functions;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.distriqt.extension.inappbilling.InAppBillingContext;
import com.distriqt.extension.inappbilling.util.FREUtils;
import java.util.Arrays;
import java.util.List;

public class GetProductsFunction implements FREFunction {
    private static String TAG = GetProductsFunction.class.getSimpleName();

    public FREObject call(FREContext context, FREObject[] args) {
        FREUtils.log(TAG, "call", new Object[0]);
        try {
            InAppBillingContext ctx = (InAppBillingContext) context;
            boolean success = false;
            if (ctx.v) {
                String[] productIds = FREUtils.GetObjectAsArrayOfStrings(args[0]);
                success = ctx.controller().getProducts((List<String>) Arrays.asList(productIds), Boolean.valueOf(args[1].getAsBool()));
            }
            return FREObject.newObject(success);
        } catch (Exception e) {
            FREUtils.handleException(context, e);
            return null;
        }
    }
}
