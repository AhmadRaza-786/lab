package com.android.vending.billing.util;

import org.json.JSONException;
import org.json.JSONObject;

public class SkuDetails {
    String mCurrencyCode;
    String mDescription;
    String mItemType;
    String mJson;
    String mPrice;
    String mPriceString;
    String mSku;
    String mTitle;
    String mType;

    public SkuDetails(String jsonSkuDetails) throws JSONException {
        this(IabHelper.ITEM_TYPE_INAPP, jsonSkuDetails);
    }

    public SkuDetails(String itemType, String jsonSkuDetails) throws JSONException {
        this.mItemType = itemType;
        this.mJson = jsonSkuDetails;
        JSONObject o = new JSONObject(this.mJson);
        this.mSku = o.optString("productId");
        this.mType = o.optString("type");
        this.mPriceString = o.optString("price");
        this.mTitle = o.optString("title");
        this.mDescription = o.optString("description");
        this.mPrice = o.optString("price_amount_micros");
        this.mCurrencyCode = o.optString("price_currency_code");
    }

    public String getItemType() {
        return this.mItemType;
    }

    public String getJson() {
        return this.mJson;
    }

    public String getSku() {
        return this.mSku;
    }

    public String getType() {
        return this.mType;
    }

    public String getPrice() {
        return this.mPrice;
    }

    public String getPriceString() {
        return this.mPriceString;
    }

    public String getCurrencyCode() {
        return this.mCurrencyCode;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public String toString() {
        return "SkuDetails:" + this.mJson;
    }
}
