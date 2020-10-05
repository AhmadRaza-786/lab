package com.distriqt.extension.inappbilling.listeners;

import com.android.vending.billing.util.IabHelper;
import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Purchase;
import com.distriqt.extension.inappbilling.InAppBillingExtension;
import com.distriqt.extension.inappbilling.events.PurchaseEvent;
import com.distriqt.extension.inappbilling.util.FREUtils;
import com.distriqt.extension.inappbilling.util.IEventDispatcher;
import java.util.List;

public class PurchaseListener implements IabHelper.OnIabPurchaseFinishedListener {
    private static String TAG = PurchaseListener.class.getSimpleName();
    private IEventDispatcher _dispatcher = null;
    private List<String> _productIds;

    public IEventDispatcher getDispatcher() {
        return this._dispatcher;
    }

    public void setDispatcher(IEventDispatcher dispatcher) {
        this._dispatcher = dispatcher;
    }

    public List<String> getProductIds() {
        return this._productIds;
    }

    public void setProductIds(List<String> productIds) {
        this._productIds = productIds;
    }

    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
        FREUtils.log(TAG, String.format("onIabPurchaseFinished( [%d]:%s )", new Object[]{Integer.valueOf(result.getResponse()), result.getMessage()}), new Object[0]);
        if (this._dispatcher != null) {
            switch (result.getResponse()) {
                case IabHelper.IABHELPER_USER_CANCELLED:
                case 1:
                    this._dispatcher.dispatchEvent(PurchaseEvent.PURCHASE_CANCELLED, PurchaseEvent.formatPurchaseForEvent(purchase));
                    break;
                case 0:
                    this._dispatcher.dispatchEvent(PurchaseEvent.PURCHASE_SUCCESS, PurchaseEvent.formatPurchaseForEvent(purchase));
                    break;
                case 7:
                    this._dispatcher.dispatchEvent(PurchaseEvent.PURCHASE_FAILED, PurchaseEvent.formatErrorForEvent(result.getResponse(), result.getMessage()));
                    break;
                default:
                    this._dispatcher.dispatchEvent(PurchaseEvent.PURCHASE_FAILED, PurchaseEvent.formatErrorForEvent(result.getResponse(), result.getMessage()));
                    break;
            }
            InAppBillingExtension.context.controller().finishActivePurchaseActivity();
        }
    }
}
