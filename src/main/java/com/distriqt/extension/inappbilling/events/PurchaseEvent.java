package com.distriqt.extension.inappbilling.events;

import com.android.vending.billing.util.Inventory;
import com.android.vending.billing.util.Purchase;
import com.distriqt.extension.inappbilling.BuildConfig;
import com.distriqt.extension.inappbilling.helpers.ResponseHelper;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class PurchaseEvent {
    public static final String PURCHASES_QUEUE_REMOVED = "purchases:queue:removed";
    public static final String PURCHASES_QUEUE_UPDATED = "purchases:queue:updated";
    public static final String PURCHASES_REMOVED = "purchases:removed";
    public static final String PURCHASES_UPDATED = "purchases:updated";
    public static final String PURCHASE_CANCELLED = "purchase:cancelled";
    public static final String PURCHASE_FAILED = "purchase:failed";
    public static final String PURCHASE_PURCHASING = "purchase:purchasing";
    public static final String PURCHASE_REFUNDED = "purchase:refunded";
    public static final String PURCHASE_RESTORED = "purchase:restored";
    public static final String PURCHASE_SUCCESS = "purchase:success";

    public static String formatPurchaseForEvent(Purchase purchase) {
        try {
            return ResponseHelper.encodePurchase(purchase == null ? BuildConfig.FLAVOR : purchase.getSku(), purchase).toString();
        } catch (Exception e) {
            return "{}";
        }
    }

    public static String formatRestoredPurchasesForEvent(List<String> productIds, Inventory inventory) {
        try {
            JSONObject jsonObj = new JSONObject();
            JSONArray purchasesObj = new JSONArray();
            for (String productId : productIds) {
                JSONObject purchase = ResponseHelper.encodePurchase(productId, inventory.getPurchase(productId), true);
                if (purchase != null) {
                    purchasesObj.put(purchase);
                }
            }
            jsonObj.put("purchases", purchasesObj);
            return jsonObj.toString();
        } catch (Exception e) {
            return "{}";
        }
    }

    public static String formatErrorForEvent(int errorCode, String error) {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("error", error);
            jsonObj.put("errorCode", String.valueOf(errorCode));
            return jsonObj.toString();
        } catch (Exception e) {
            return "{}";
        }
    }
}
