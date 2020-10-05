package com.adobe.air;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.InternalErrorException;
import com.amazonaws.services.sns.model.InvalidParameterException;
import com.amazonaws.services.sns.model.SetEndpointAttributesRequest;
import com.distriqt.extension.inappbilling.BuildConfig;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import java.util.HashMap;

public class AndroidGcmRegistrationService extends IntentService {
    private static String ACCESS_KEY = BuildConfig.FLAVOR;
    private static String APPLICATION_ARN = "arn:aws:sns:us-west-2:502492504188:app/GCM/AdobeAIRGCM";
    private static final String CUSTOM_USER_DATA = "CustomUserData";
    private static final String ENABLED = "Enabled";
    private static final String ENABLE_LOGGING = "enableLogging";
    private static final int MAX_RETRIES = 10;
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PROPERTY_ENDPOINT_ARN = "endpointArn";
    private static final String RATE_LIMIT = "rateLimit";
    private static final int RETRY_TIME = 300000;
    private static String SECRET_KEY = BuildConfig.FLAVOR;
    /* access modifiers changed from: private */
    public static String SENDER_ID = "233437466354";
    private static final String TAG = "AndroidGcmRegistrationService";
    private static final String TEST_ACCESS_KEY = "";
    private static final String TEST_APPLICATION_ARN = "arn:aws:sns:us-west-2:413177889857:app/GCM/airruntimetestapp";
    private static final String TEST_ENV = "testEnv";
    private static final String TEST_SECRET_KEY = "";
    private static final String TEST_SENDER_ID = "1078258869814";
    private static final String TOKEN = "Token";
    private AmazonSNS mClient = null;
    private boolean mEnableLogging = false;
    private String mEndpointArn = null;
    /* access modifiers changed from: private */
    public GoogleCloudMessaging mGcm = null;
    /* access modifiers changed from: private */
    public String mRegId = null;
    private ResultReceiver mResultReceiver = null;
    private int mRetryCount = 0;
    private boolean mTestEnv = false;

    public AndroidGcmRegistrationService() {
        super(TAG);
    }

    /* access modifiers changed from: protected */
    public void onHandleIntent(Intent intent) {
        this.mResultReceiver = (ResultReceiver) intent.getParcelableExtra(AdobeAIRMainActivity.RESULT_RECEIVER);
        registerForNotifications();
    }

    private void registerForNotifications() {
        if (!isAppRegistered() && checkPlayServices()) {
            configureTestEnv();
            registerInBackground(0);
        }
    }

    private boolean checkPlayServices() {
        int isGooglePlayServicesAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (isGooglePlayServicesAvailable == 0) {
            return true;
        }
        if (GooglePlayServicesUtil.isUserRecoverableError(isGooglePlayServicesAvailable) && this.mResultReceiver != null) {
            this.mResultReceiver.send(isGooglePlayServicesAvailable, (Bundle) null);
        }
        return false;
    }

    private boolean isAppRegistered() {
        int i = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int appVersion = getAppVersion();
        if (i == Integer.MIN_VALUE || i != appVersion) {
            return false;
        }
        return true;
    }

    private void configureTestEnv() {
        try {
            Bundle bundle = getPackageManager().getServiceInfo(new ComponentName(this, getClass()), 128).metaData;
            if (bundle != null) {
                this.mEnableLogging = bundle.getBoolean(ENABLE_LOGGING);
                this.mTestEnv = bundle.getBoolean(TEST_ENV);
                if (this.mTestEnv) {
                    SENDER_ID = TEST_SENDER_ID;
                    APPLICATION_ARN = TEST_APPLICATION_ARN;
                    ACCESS_KEY = BuildConfig.FLAVOR;
                    SECRET_KEY = BuildConfig.FLAVOR;
                    int i = bundle.getInt(RATE_LIMIT, Integer.MIN_VALUE);
                    if (i != Integer.MIN_VALUE) {
                        AdobeAIRMainActivity.RATE_LIMIT = (long) i;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
    }

    /* access modifiers changed from: private */
    public void registerInBackground(int i) {
        if (this.mRetryCount < 10) {
            if (i != 0) {
                this.mRetryCount++;
            }
            if (this.mGcm == null) {
                this.mGcm = GoogleCloudMessaging.getInstance(this);
            }
            new AsyncTaskRunner().execute(new Integer[]{Integer.valueOf(i)});
        }
    }

    private class AsyncTaskRunner extends AsyncTask<Integer, Void, Void> {
        private AsyncTaskRunner() {
        }

        /* access modifiers changed from: protected */
        public Void doInBackground(Integer... numArr) {
            try {
                Thread.sleep((long) numArr[0].intValue());
                String unused = AndroidGcmRegistrationService.this.mRegId = AndroidGcmRegistrationService.this.mGcm.register(new String[]{AndroidGcmRegistrationService.SENDER_ID});
                if (AndroidGcmRegistrationService.this.mRegId == null) {
                    AndroidGcmRegistrationService.this.registerInBackground(AndroidGcmRegistrationService.RETRY_TIME);
                    return null;
                }
                AndroidGcmRegistrationService.this.sendRegistrationIdToAws();
                return null;
            } catch (Exception e) {
                AndroidGcmRegistrationService.this.registerInBackground(AndroidGcmRegistrationService.RETRY_TIME);
                return null;
            }
        }
    }

    private int getAppVersion() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    /* access modifiers changed from: private */
    public void sendRegistrationIdToAws() {
        try {
            BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
            Region region = Region.getRegion(Regions.US_WEST_2);
            this.mClient = new AmazonSNSClient(basicAWSCredentials);
            this.mClient.setRegion(region);
            try {
                CreatePlatformEndpointRequest createPlatformEndpointRequest = new CreatePlatformEndpointRequest();
                createPlatformEndpointRequest.setPlatformApplicationArn(APPLICATION_ARN);
                createPlatformEndpointRequest.setToken(this.mRegId);
                createPlatformEndpointRequest.setCustomUserData(getCustomData());
                CreatePlatformEndpointResult createPlatformEndpoint = this.mClient.createPlatformEndpoint(createPlatformEndpointRequest);
                if (createPlatformEndpoint != null && createPlatformEndpoint.getEndpointArn() != null) {
                    this.mEndpointArn = createPlatformEndpoint.getEndpointArn();
                    if (this.mEnableLogging) {
                        Log.i(TAG, "Creation EndpointArn = " + this.mEndpointArn);
                    }
                    updateSharedPref();
                }
            } catch (InternalErrorException e) {
                registerInBackground(RETRY_TIME);
            } catch (InvalidParameterException e2) {
                updateEndpointAttributes();
            } catch (AmazonClientException | AmazonServiceException e3) {
            }
        } catch (Exception e4) {
        }
    }

    private void updateEndpointAttributes() {
        try {
            SetEndpointAttributesRequest setEndpointAttributesRequest = new SetEndpointAttributesRequest();
            if (this.mEndpointArn == null) {
                this.mEndpointArn = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(PROPERTY_ENDPOINT_ARN, BuildConfig.FLAVOR);
                if (this.mEnableLogging) {
                    Log.i(TAG, "Update EndpointArn = " + this.mEndpointArn);
                }
            }
            setEndpointAttributesRequest.setEndpointArn(this.mEndpointArn);
            HashMap hashMap = new HashMap();
            hashMap.put(CUSTOM_USER_DATA, getCustomData());
            hashMap.put(ENABLED, "true");
            hashMap.put(TOKEN, this.mRegId);
            setEndpointAttributesRequest.setAttributes(hashMap);
            this.mClient.setEndpointAttributes(setEndpointAttributesRequest);
            updateSharedPref();
        } catch (AmazonClientException | AmazonServiceException e) {
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x00eb A[Catch:{ Exception -> 0x0102 }] */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x00fa A[Catch:{ Exception -> 0x0102 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getCustomData() {
        /*
            r9 = this;
            org.json.JSONObject r8 = new org.json.JSONObject     // Catch:{ Exception -> 0x0102 }
            r8.<init>()     // Catch:{ Exception -> 0x0102 }
            java.lang.String r0 = "osVersion"
            java.lang.String r1 = android.os.Build.VERSION.RELEASE     // Catch:{ Exception -> 0x0102 }
            r8.put(r0, r1)     // Catch:{ Exception -> 0x0102 }
            android.content.pm.PackageManager r0 = r9.getPackageManager()     // Catch:{ Exception -> 0x0102 }
            java.lang.String r1 = r9.getPackageName()     // Catch:{ Exception -> 0x0102 }
            r2 = 0
            android.content.pm.PackageInfo r0 = r0.getPackageInfo(r1, r2)     // Catch:{ Exception -> 0x0102 }
            java.lang.String r1 = "airVersion"
            java.lang.String r0 = r0.versionName     // Catch:{ Exception -> 0x0102 }
            r8.put(r1, r0)     // Catch:{ Exception -> 0x0102 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0102 }
            r0.<init>()     // Catch:{ Exception -> 0x0102 }
            java.lang.String r1 = android.os.Build.MODEL     // Catch:{ Exception -> 0x0102 }
            java.lang.StringBuilder r0 = r0.append(r1)     // Catch:{ Exception -> 0x0102 }
            java.lang.String r1 = "&"
            java.lang.StringBuilder r0 = r0.append(r1)     // Catch:{ Exception -> 0x0102 }
            java.lang.String r1 = android.os.Build.MANUFACTURER     // Catch:{ Exception -> 0x0102 }
            java.lang.StringBuilder r0 = r0.append(r1)     // Catch:{ Exception -> 0x0102 }
            java.lang.String r1 = "&"
            java.lang.StringBuilder r0 = r0.append(r1)     // Catch:{ Exception -> 0x0102 }
            int r1 = com.adobe.air.SystemCapabilities.GetScreenHRes(r9)     // Catch:{ Exception -> 0x0102 }
            java.lang.String r1 = java.lang.Integer.toString(r1)     // Catch:{ Exception -> 0x0102 }
            java.lang.StringBuilder r0 = r0.append(r1)     // Catch:{ Exception -> 0x0102 }
            java.lang.String r1 = "&"
            java.lang.StringBuilder r0 = r0.append(r1)     // Catch:{ Exception -> 0x0102 }
            int r1 = com.adobe.air.SystemCapabilities.GetScreenVRes(r9)     // Catch:{ Exception -> 0x0102 }
            java.lang.String r1 = java.lang.Integer.toString(r1)     // Catch:{ Exception -> 0x0102 }
            java.lang.StringBuilder r0 = r0.append(r1)     // Catch:{ Exception -> 0x0102 }
            java.lang.String r1 = "&"
            java.lang.StringBuilder r0 = r0.append(r1)     // Catch:{ Exception -> 0x0102 }
            int r1 = com.adobe.air.SystemCapabilities.GetScreenDPI(r9)     // Catch:{ Exception -> 0x0102 }
            java.lang.String r1 = java.lang.Integer.toString(r1)     // Catch:{ Exception -> 0x0102 }
            java.lang.StringBuilder r0 = r0.append(r1)     // Catch:{ Exception -> 0x0102 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0102 }
            java.lang.String r1 = "deviceInfo"
            r8.put(r1, r0)     // Catch:{ Exception -> 0x0102 }
            java.util.Locale r0 = java.util.Locale.getDefault()     // Catch:{ Exception -> 0x0102 }
            java.lang.String r1 = "locale"
            java.lang.String r2 = r0.toString()     // Catch:{ Exception -> 0x0102 }
            r8.put(r1, r2)     // Catch:{ Exception -> 0x0102 }
            java.util.Locale r1 = java.util.Locale.ENGLISH     // Catch:{ Exception -> 0x0102 }
            java.lang.String r7 = r0.getDisplayCountry(r1)     // Catch:{ Exception -> 0x0102 }
            java.lang.String r0 = "location"
            java.lang.Object r0 = r9.getSystemService(r0)     // Catch:{ Exception -> 0x00f7 }
            android.location.LocationManager r0 = (android.location.LocationManager) r0     // Catch:{ Exception -> 0x00f7 }
            java.lang.String r1 = "network"
            boolean r1 = r0.isProviderEnabled(r1)     // Catch:{ Exception -> 0x00f7 }
            if (r1 == 0) goto L_0x0106
            java.lang.String r1 = "network"
            android.location.Location r0 = r0.getLastKnownLocation(r1)     // Catch:{ Exception -> 0x00f7 }
            android.location.Geocoder r1 = new android.location.Geocoder     // Catch:{ Exception -> 0x00f7 }
            java.util.Locale r2 = java.util.Locale.ENGLISH     // Catch:{ Exception -> 0x00f7 }
            r1.<init>(r9, r2)     // Catch:{ Exception -> 0x00f7 }
            if (r0 == 0) goto L_0x0106
            if (r1 == 0) goto L_0x0106
            boolean r2 = android.location.Geocoder.isPresent()     // Catch:{ Exception -> 0x00f7 }
            if (r2 == 0) goto L_0x0106
            double r2 = r0.getLatitude()     // Catch:{ Exception -> 0x00f7 }
            double r4 = r0.getLongitude()     // Catch:{ Exception -> 0x00f7 }
            r6 = 1
            java.util.List r0 = r1.getFromLocation(r2, r4, r6)     // Catch:{ Exception -> 0x00f7 }
            r1 = 0
            java.lang.Object r0 = r0.get(r1)     // Catch:{ Exception -> 0x00f7 }
            android.location.Address r0 = (android.location.Address) r0     // Catch:{ Exception -> 0x00f7 }
            java.lang.String r0 = r0.getCountryName()     // Catch:{ Exception -> 0x00f7 }
        L_0x00c8:
            java.lang.String r1 = "geo"
            r8.put(r1, r0)     // Catch:{ Exception -> 0x0102 }
            long r0 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0102 }
            java.lang.String r0 = java.lang.String.valueOf(r0)     // Catch:{ Exception -> 0x0102 }
            java.lang.String r1 = "timestamp"
            r8.put(r1, r0)     // Catch:{ Exception -> 0x0102 }
            android.content.Context r0 = r9.getApplicationContext()     // Catch:{ Exception -> 0x0102 }
            android.content.SharedPreferences r0 = android.preference.PreferenceManager.getDefaultSharedPreferences(r0)     // Catch:{ Exception -> 0x0102 }
            java.lang.String r1 = "AIRDefaultActivity"
            r2 = 1
            boolean r0 = r0.getBoolean(r1, r2)     // Catch:{ Exception -> 0x0102 }
            if (r0 == 0) goto L_0x00fa
            java.lang.String r0 = "AIRDefaultActivity"
            java.lang.String r1 = "GL"
            r8.put(r0, r1)     // Catch:{ Exception -> 0x0102 }
        L_0x00f2:
            java.lang.String r0 = r8.toString()     // Catch:{ Exception -> 0x0102 }
        L_0x00f6:
            return r0
        L_0x00f7:
            r0 = move-exception
            r0 = r7
            goto L_0x00c8
        L_0x00fa:
            java.lang.String r0 = "AIRDefaultActivity"
            java.lang.String r1 = "PP"
            r8.put(r0, r1)     // Catch:{ Exception -> 0x0102 }
            goto L_0x00f2
        L_0x0102:
            r0 = move-exception
            java.lang.String r0 = ""
            goto L_0x00f6
        L_0x0106:
            r0 = r7
            goto L_0x00c8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adobe.air.AndroidGcmRegistrationService.getCustomData():java.lang.String");
    }

    private void updateSharedPref() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int appVersion = getAppVersion();
        SharedPreferences.Editor edit = defaultSharedPreferences.edit();
        edit.putInt(PROPERTY_APP_VERSION, appVersion);
        edit.putString(PROPERTY_ENDPOINT_ARN, this.mEndpointArn);
        edit.commit();
    }
}
