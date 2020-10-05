package com.distriqt.extension.inappbilling;

public class Purchase {
    public static final String STATE_CANCELLED = "transaction:cancelled";
    public static final String STATE_DEFERRED = "transaction:deferred";
    public static final String STATE_FAILED = "transaction:failed";
    public static final String STATE_NOTALLOWED = "transaction:notallowed";
    public static final String STATE_PURCHASED = "transaction:purchased";
    public static final String STATE_PURCHASING = "transaction:purchasing";
    public static final String STATE_REFUNDED = "transaction:refunded";
    public static final String STATE_REMOVED = "transaction:removed";
    public static final String STATE_RESTORED = "transaction:restored";
    public static final String STATE_UNKNOWN = "transaction:unknown";
    public String developerPayload = BuildConfig.FLAVOR;
    public String error = BuildConfig.FLAVOR;
    public String errorCode = BuildConfig.FLAVOR;
    public String originalMessage = BuildConfig.FLAVOR;
    public Purchase originalPurchase = null;
    public String productId = BuildConfig.FLAVOR;
    public int quantity = 1;
    public String signature = BuildConfig.FLAVOR;
    public String transactionId = BuildConfig.FLAVOR;
    public String transactionReceipt = BuildConfig.FLAVOR;
    public String transactionState = STATE_UNKNOWN;
    public long transactionTimestamp = -1;
}
