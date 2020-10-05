package com.distriqt.extension.inappbilling.listeners;

import com.android.vending.billing.util.IabHelper;
import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Inventory;
import com.android.vending.billing.util.SkuDetails;
import com.distriqt.extension.inappbilling.BuildConfig;
import com.distriqt.extension.inappbilling.events.InAppBillingEvent;
import com.distriqt.extension.inappbilling.events.PurchaseEvent;
import com.distriqt.extension.inappbilling.util.FREUtils;
import com.distriqt.extension.inappbilling.util.IEventDispatcher;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryInventoryListener implements IabHelper.QueryInventoryFinishedListener {
    public static final String QUERY_GET_PRODUCTS = "query.get.products";
    public static final String QUERY_RESTORE_PURCHASES = "query.get.purchases";
    private static String TAG = QueryInventoryListener.class.getSimpleName();
    private IEventDispatcher _dispatcher = null;
    private List<String> _productIds = null;
    private Map<String, SkuDetails> _skuMap = null;
    public String query = QUERY_GET_PRODUCTS;

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

    public Map<String, SkuDetails> getSkuMap() {
        return this._skuMap;
    }

    public SkuDetails getSkuDetails(String sku) {
        if (this._skuMap == null) {
            return null;
        }
        return this._skuMap.get(sku);
    }

    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
        FREUtils.log(TAG, "onQueryInventoryFinished", new Object[0]);
        if (this._dispatcher != null) {
            if (QUERY_GET_PRODUCTS.equals(this.query)) {
                if (result.isSuccess()) {
                    this._skuMap = new HashMap();
                    for (String productId : this._productIds) {
                        if (inventory.hasDetails(productId)) {
                            this._skuMap.put(productId, inventory.getSkuDetails(productId));
                        } else {
                            this._dispatcher.dispatchEvent(InAppBillingEvent.PRODUCT_INVALID, productId);
                        }
                    }
                    this._dispatcher.dispatchEvent(InAppBillingEvent.PRODUCTS_LOADED, InAppBillingEvent.formatProductsForEvent(this._skuMap));
                    return;
                }
                this._dispatcher.dispatchEvent(InAppBillingEvent.PRODUCTS_FAILED, InAppBillingEvent.formatErrorForEvent(result.getResponse(), result.getMessage()));
            } else if (!QUERY_RESTORE_PURCHASES.equals(this.query)) {
            } else {
                if (result.isSuccess()) {
                    this._dispatcher.dispatchEvent(PurchaseEvent.PURCHASES_UPDATED, PurchaseEvent.formatRestoredPurchasesForEvent(this._productIds, inventory));
                    this._dispatcher.dispatchEvent(InAppBillingEvent.RESTORE_PURCHASES_COMPLETE, BuildConfig.FLAVOR);
                    return;
                }
                this._dispatcher.dispatchEvent(InAppBillingEvent.RESTORE_PURCHASES_FAILED, InAppBillingEvent.formatErrorForEvent(result.getResponse(), result.getMessage()));
            }
        }
    }
}
