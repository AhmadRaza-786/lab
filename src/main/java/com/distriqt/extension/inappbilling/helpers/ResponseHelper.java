package com.distriqt.extension.inappbilling.helpers;

import android.util.Log;
import com.adobe.fre.FREContext;
import com.android.vending.billing.util.SkuDetails;
import com.distriqt.extension.inappbilling.BuildConfig;
import com.distriqt.extension.inappbilling.Purchase;
import com.distriqt.extension.inappbilling.util.FREUtils;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import org.json.JSONObject;

public class ResponseHelper {
    public static String RESPONSE_BILLING_UNAVAILABLE = "response:billing:unavailable";
    public static String RESPONSE_DEVELOPER_ERROR = "response:developer:error";
    public static String RESPONSE_ERROR = "response:error";
    public static String RESPONSE_ITEM_ALREADY_OWNED = "response:item:already:owned";
    public static String RESPONSE_ITEM_NOT_OWNED = "response:item:not:owned";
    public static String RESPONSE_ITEM_UNAVAILABLE = "response:item:unavailable";
    public static String RESPONSE_OK = "response:ok";
    public static String RESPONSE_UNKNOWN = "response:unknown";
    public static String RESPONSE_USER_CANCELLED = "response:user:cancelled";
    public static String STATE_CANCELLED = Purchase.STATE_CANCELLED;
    public static String STATE_FAILED = Purchase.STATE_FAILED;
    public static String STATE_PURCHASED = Purchase.STATE_PURCHASED;
    public static String STATE_REFUNDED = Purchase.STATE_REFUNDED;
    public static String STATE_RESTORED = Purchase.STATE_RESTORED;
    public static String STATE_UNKNOWN = Purchase.STATE_UNKNOWN;
    private static String TAG = ResponseHelper.class.getSimpleName();

    public static JSONObject encodeProduct(String productId, SkuDetails details) {
        FREUtils.log(TAG, "encodeProduct( %s ) : %s ", productId, details.toString());
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("id", productId);
            jsonObj.put("title", details.getTitle());
            jsonObj.put("description", details.getDescription());
            jsonObj.put("type", details.getType());
            jsonObj.put("price", Double.parseDouble(details.getPrice()) / 1000000.0d);
            jsonObj.put("priceString", details.getPriceString());
            jsonObj.put("locale", BuildConfig.FLAVOR);
            jsonObj.put("countryCode", BuildConfig.FLAVOR);
            jsonObj.put("currencySymbol", details.getPriceString().replaceAll("[\\d.,]+", BuildConfig.FLAVOR));
            jsonObj.put("internationalCurrencySymbol", BuildConfig.FLAVOR);
            jsonObj.put("currencyCode", details.getCurrencyCode());
            jsonObj.put("itemType", details.getItemType());
            jsonObj.put("source", details.getJson());
            return jsonObj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encodeProductAsXml(String productId, SkuDetails details) {
        FREUtils.log(TAG, "encodeProductAsXml( " + productId + " )", new Object[0]);
        String priceString = details.getPrice();
        Number price = 0;
        String currency = BuildConfig.FLAVOR;
        try {
            currency = priceString.replaceAll("[\\d.,]+", BuildConfig.FLAVOR);
            price = NumberFormat.getInstance().parse(priceString.replaceAll("[^\\d.]+", BuildConfig.FLAVOR));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        String productXML = (((((((((("<product>" + "<id>" + details.getSku() + "</id>") + "<title>" + details.getTitle() + "</title>") + "<description>" + details.getDescription() + "</description>") + "<currencySymbol>" + currency + "</currencySymbol>") + "<price>" + price.toString() + "</price>") + "<priceString>" + priceString + "</priceString>") + "<locale></locale>") + "<type>" + details.getType() + "</type>") + "<itemType>" + details.getItemType() + "</itemType>") + "<source><![CDATA[" + details.getJson() + "]]></source>") + "</product>";
        FREUtils.log(TAG, productXML, new Object[0]);
        return productXML;
    }

    public static JSONObject encodePurchase(String productId, com.android.vending.billing.util.Purchase purchase) {
        return encodePurchase(productId, purchase, false);
    }

    public static JSONObject encodePurchase(String productId, com.android.vending.billing.util.Purchase purchase, boolean restored) {
        if (purchase == null) {
            try {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("productId", productId);
                jsonObj.put("error", "Invalid Purchase");
                jsonObj.put("errorCode", "-1");
                return jsonObj;
            } catch (Exception e) {
                FREUtils.handleException((FREContext) null, e);
                return null;
            }
        } else {
            FREUtils.log(TAG, String.format("encodePurchase( %s )", new Object[]{purchase.getSku()}), new Object[0]);
            JSONObject jsonObj2 = purchaseToObject(purchase);
            if (restored) {
                jsonObj2.put("transactionState", STATE_RESTORED);
                jsonObj2.put("originalTransaction", purchaseToObject(purchase));
            }
            jsonObj2.put("error", BuildConfig.FLAVOR);
            jsonObj2.put("errorCode", BuildConfig.FLAVOR);
            return jsonObj2;
        }
    }

    public static JSONObject purchaseToObject(com.android.vending.billing.util.Purchase purchase) throws Exception {
        JSONObject jsonObj = new JSONObject();
        String transactionState = getPurchaseState(purchase.getPurchaseState());
        jsonObj.put("productId", purchase.getSku());
        jsonObj.put("quantity", 1);
        jsonObj.put("transactionDate", purchase.getPurchaseTime());
        jsonObj.put("transactionIdentifier", purchase.getToken());
        jsonObj.put("transactionState", transactionState);
        jsonObj.put("transactionReceipt", purchase.getOrderId());
        jsonObj.put("developerPayload", purchase.getDeveloperPayload());
        jsonObj.put("signature", purchase.getSignature());
        jsonObj.put("originalMessage", URLEncoder.encode(purchase.getOriginalJson(), "UTF-8"));
        return jsonObj;
    }

    public static String encodePurchaseAsXml(com.android.vending.billing.util.Purchase purchase) {
        if (purchase == null) {
            Log.d(TAG, "Invalid purchase");
            return "<transaction><error>Invalid Purchase|-1</error></transaction>";
        }
        Log.d(TAG, "encodePurchase( " + purchase.getSku() + " )");
        String transactionState = getPurchaseState(purchase.getPurchaseState());
        Date date = new Date(purchase.getPurchaseTime());
        Log.d(TAG, "encodePurchase( " + purchase.getSku() + " ) [" + purchase.getPurchaseState() + "] ::" + transactionState);
        return (((((((((("<transaction>" + "<productId>" + purchase.getSku() + "</productId>") + "<quantity>1</quantity>") + "<transactionDate>" + String.format(Locale.US, "%d", new Object[]{Long.valueOf(date.getTime())}) + "</transactionDate>") + "<transactionIdentifier>" + purchase.getToken() + "</transactionIdentifier>") + "<transactionState>" + transactionState + "</transactionState>") + "<transactionReceipt>" + purchase.getOrderId() + "</transactionReceipt>") + "<developerPayload>" + purchase.getDeveloperPayload() + "</developerPayload>") + "<signature>" + purchase.getSignature() + "</signature>") + "<originalMessage><![CDATA[" + purchase.getOriginalJson() + "]]></originalMessage>") + "<error></error>") + "</transaction>";
    }

    public static String getResponseState(int code2) {
        String responseState = RESPONSE_UNKNOWN;
        switch (code2) {
            case 0:
                return RESPONSE_OK;
            case 1:
                return RESPONSE_USER_CANCELLED;
            case 2:
                return RESPONSE_UNKNOWN;
            case 3:
                return RESPONSE_BILLING_UNAVAILABLE;
            case 4:
                return RESPONSE_ITEM_UNAVAILABLE;
            case 5:
                return RESPONSE_DEVELOPER_ERROR;
            case 6:
                return RESPONSE_ERROR;
            case 7:
                return RESPONSE_ITEM_ALREADY_OWNED;
            case 8:
                return RESPONSE_ITEM_NOT_OWNED;
            default:
                return responseState;
        }
    }

    public static String getPurchaseState(int code2) {
        String transactionState = STATE_UNKNOWN;
        switch (code2) {
            case 0:
                return STATE_PURCHASED;
            case 1:
                return STATE_CANCELLED;
            case 2:
                return STATE_REFUNDED;
            default:
                return transactionState;
        }
    }
}
