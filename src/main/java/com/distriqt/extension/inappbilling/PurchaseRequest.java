package com.distriqt.extension.inappbilling;

import java.util.Locale;

public class PurchaseRequest {
    public String applicationUsername = BuildConfig.FLAVOR;
    public String developerPayload = BuildConfig.FLAVOR;
    public String productId = BuildConfig.FLAVOR;
    public int quantity = 1;

    public String toString() {
        return String.format(Locale.UK, "[%s, %d]", new Object[]{this.productId, Integer.valueOf(this.quantity)});
    }
}
