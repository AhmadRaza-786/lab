package com.distriqt.extension.inappbilling;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.distriqt.extension.inappbilling.functions.ActiveProductIdsFunction;
import com.distriqt.extension.inappbilling.functions.ActiveProductsFunction;
import com.distriqt.extension.inappbilling.functions.CanMakePaymentsFunction;
import com.distriqt.extension.inappbilling.functions.CleanupFunction;
import com.distriqt.extension.inappbilling.functions.GetProductsFunction;
import com.distriqt.extension.inappbilling.functions.ImplementationFunction;
import com.distriqt.extension.inappbilling.functions.IsProductViewSupportedFunction;
import com.distriqt.extension.inappbilling.functions.IsSupportedFunction;
import com.distriqt.extension.inappbilling.functions.SetupFunction;
import com.distriqt.extension.inappbilling.functions.ShowProductViewFunction;
import com.distriqt.extension.inappbilling.functions.VDKFunction;
import com.distriqt.extension.inappbilling.functions.VersionFunction;
import com.distriqt.extension.inappbilling.functions.applicationreceipt.GetAppReceiptFunction;
import com.distriqt.extension.inappbilling.functions.applicationreceipt.RefreshFunction;
import com.distriqt.extension.inappbilling.functions.purchases.ConsumePurchaseFunction;
import com.distriqt.extension.inappbilling.functions.purchases.FinishPurchaseFunction;
import com.distriqt.extension.inappbilling.functions.purchases.GetPendingPurchasesFunction;
import com.distriqt.extension.inappbilling.functions.purchases.MakePurchaseFunction;
import com.distriqt.extension.inappbilling.functions.purchases.RestorePurchasesFunction;
import com.distriqt.extension.inappbilling.functions.purchases.StartDownloadsFunction;
import com.distriqt.extension.inappbilling.util.FREUtils;
import com.distriqt.extension.inappbilling.util.IEventDispatcher;
import java.util.HashMap;
import java.util.Map;

public class InAppBillingContext extends FREContext implements IEventDispatcher {
    public static String IMPLEMENTATION = "Android";
    private static String TAG = InAppBillingContext.class.getSimpleName();
    public static String VERSION = "4.7";
    private InAppBillingController _controller = null;
    public boolean v = false;

    public void dispose() {
        if (this._controller != null) {
            this._controller.cleanup();
            this._controller = null;
        }
    }

    public Map<String, FREFunction> getFunctions() {
        Map<String, FREFunction> functionMap = new HashMap<>();
        functionMap.put("isSupported", new IsSupportedFunction());
        functionMap.put("version", new VersionFunction());
        functionMap.put("implementation", new ImplementationFunction());
        functionMap.put("vV2", new VDKFunction());
        functionMap.put("canMakePayments", new CanMakePaymentsFunction());
        functionMap.put("setup", new SetupFunction());
        functionMap.put("cleanup", new CleanupFunction());
        functionMap.put("getProducts", new GetProductsFunction());
        functionMap.put("activeProducts", new ActiveProductsFunction());
        functionMap.put("activeProductIds", new ActiveProductIdsFunction());
        functionMap.put("isProductViewSupported", new IsProductViewSupportedFunction());
        functionMap.put("showProductView", new ShowProductViewFunction());
        functionMap.put("applicationReceipt_isSupported", new com.distriqt.extension.inappbilling.functions.applicationreceipt.IsSupportedFunction());
        functionMap.put("applicationReceipt_getAppReceipt", new GetAppReceiptFunction());
        functionMap.put("applicationReceipt_refresh", new RefreshFunction());
        functionMap.put("makePurchase", new MakePurchaseFunction());
        functionMap.put("finishPurchase", new FinishPurchaseFunction());
        functionMap.put("restorePurchases", new RestorePurchasesFunction());
        functionMap.put("consumePurchase", new ConsumePurchaseFunction());
        functionMap.put("getPendingPurchases", new GetPendingPurchasesFunction());
        functionMap.put("startDownloads", new StartDownloadsFunction());
        return functionMap;
    }

    public InAppBillingController controller() {
        if (this._controller == null) {
            FREUtils.log(TAG, "creating controller", new Object[0]);
            this._controller = new InAppBillingController(this, getActivity());
        }
        return this._controller;
    }

    public void dispatchEvent(String code2, String data) {
        dispatchStatusEventAsync(code2, data);
    }
}
