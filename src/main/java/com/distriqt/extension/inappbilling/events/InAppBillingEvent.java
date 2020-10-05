package com.distriqt.extension.inappbilling.events;

import com.android.vending.billing.util.SkuDetails;
import com.distriqt.extension.inappbilling.BuildConfig;
import com.distriqt.extension.inappbilling.helpers.ResponseHelper;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class InAppBillingEvent {
    public static String CONSUME_FAILED = "consume:failed";
    public static String CONSUME_SUCCESS = "consume:success";
    public static String PRODUCTS_FAILED = "products:failed";
    public static String PRODUCTS_LOADED = "products:loaded";
    public static String PRODUCT_INVALID = "product:invalid";
    public static String RESTORE_PURCHASES_COMPLETE = "restore:purchases:complete";
    public static String RESTORE_PURCHASES_FAILED = "restore:purchases:failed";
    public static String SETUP_FAILURE = "setup:failure";
    public static String SETUP_SUCCESS = "setup:success";

    public static String formatProductsForEvent(Map<String, SkuDetails> products) {
        try {
            JSONObject jsonObj = new JSONObject();
            JSONArray productsObj = new JSONArray();
            for (Map.Entry<String, SkuDetails> product : products.entrySet()) {
                try {
                    JSONObject productObj = ResponseHelper.encodeProduct(product.getKey(), product.getValue());
                    if (productObj != null) {
                        productsObj.put(productObj);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            jsonObj.put("products", productsObj);
            return jsonObj.toString();
        } catch (Exception e2) {
            e2.printStackTrace();
            return BuildConfig.FLAVOR;
        }
    }

    public static String formatErrorForEvent(int errorCode, String error) {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("error", error);
            jsonObj.put("errorCode", String.valueOf(errorCode));
            return jsonObj.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return BuildConfig.FLAVOR;
        }
    }
}
