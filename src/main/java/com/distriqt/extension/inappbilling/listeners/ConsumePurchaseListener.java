package com.distriqt.extension.inappbilling.listeners;

import com.android.vending.billing.util.IabHelper;
import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Inventory;
import com.android.vending.billing.util.Purchase;
import com.distriqt.extension.inappbilling.events.InAppBillingEvent;
import com.distriqt.extension.inappbilling.events.PurchaseEvent;
import com.distriqt.extension.inappbilling.util.FREUtils;
import com.distriqt.extension.inappbilling.util.IEventDispatcher;

public class ConsumePurchaseListener implements IabHelper.QueryInventoryFinishedListener {
    private static String TAG = ConsumePurchaseListener.class.getSimpleName();
    /* access modifiers changed from: private */
    public IEventDispatcher _dispatcher = null;
    public IabHelper billingHelper = null;
    public String productId;

    public IEventDispatcher getDispatcher() {
        return this._dispatcher;
    }

    public void setDispatcher(IEventDispatcher dispatcher) {
        this._dispatcher = dispatcher;
    }

    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
        boolean z;
        FREUtils.log(TAG, "onQueryInventoryFinished()", new Object[0]);
        if (this._dispatcher != null) {
            if (result.isSuccess()) {
                Purchase purchase = inventory.getPurchase(this.productId);
                String str = TAG;
                StringBuilder append = new StringBuilder().append("onQueryInventoryFinished(): success:: found valid purchase = ");
                if (purchase != null) {
                    z = true;
                } else {
                    z = false;
                }
                FREUtils.log(str, append.append(z).toString(), new Object[0]);
                if (purchase != null) {
                    try {
                        this.billingHelper.consumeAsync(purchase, (IabHelper.OnConsumeFinishedListener) new IabHelper.OnConsumeFinishedListener() {
                            public void onConsumeFinished(Purchase purchase, IabResult result) {
                                if (ConsumePurchaseListener.this._dispatcher != null) {
                                    if (result.isSuccess()) {
                                        ConsumePurchaseListener.this._dispatcher.dispatchEvent(InAppBillingEvent.CONSUME_SUCCESS, PurchaseEvent.formatPurchaseForEvent(purchase));
                                    } else {
                                        ConsumePurchaseListener.this._dispatcher.dispatchEvent(InAppBillingEvent.CONSUME_FAILED, PurchaseEvent.formatErrorForEvent(result.getResponse(), result.getMessage()));
                                    }
                                }
                            }
                        });
                    } catch (IllegalStateException e) {
                        this._dispatcher.dispatchEvent(InAppBillingEvent.CONSUME_FAILED, PurchaseEvent.formatErrorForEvent(result.getResponse(), e.getLocalizedMessage()));
                    }
                } else {
                    FREUtils.log(TAG, "onQueryInventoryFinished(): Purchase could not be located, consume has failed", new Object[0]);
                    this._dispatcher.dispatchEvent(InAppBillingEvent.CONSUME_FAILED, PurchaseEvent.formatErrorForEvent(result.getResponse(), result.getMessage()));
                }
            } else {
                this._dispatcher.dispatchEvent(InAppBillingEvent.CONSUME_FAILED, PurchaseEvent.formatErrorForEvent(result.getResponse(), result.getMessage()));
            }
        }
    }
}
