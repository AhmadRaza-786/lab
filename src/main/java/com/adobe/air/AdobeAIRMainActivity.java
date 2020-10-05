package com.adobe.air;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import com.adobe.air.AndroidGcmResultReceiver;
import com.adobe.air.utils.Utils;
import com.adobe.app.AppManager;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.GooglePlayServicesUtil;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class AdobeAIRMainActivity extends Activity implements AndroidGcmResultReceiver.Receiver {
    private static final String AIR_PROPERTIES_URL = "airPropertiesUrl";
    public static final String ENABLE_MY_GAMES = "EnableMyGames";
    private static final String MSG_ID = "msgId";
    private static final String NEW_UI_ANALYTICS_URL = "http://www.adobe.com/airgames/3/";
    private static final String NOTIFICATION_ANALYTICS_URL = "https://www.adobe.com/gamepreview/?game=notification/notificationClicked.html_";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PROPERTY_DEFAULT_ACTIVITY = "AIRDefaultActivity";
    private static final String PROPERTY_ENABLE_MY_GAMES_PERCENTAGE = "MyGamesPercentage";
    private static final String PROPERTY_FIRST_LAUNCH = "firstLaunch";
    private static final String PROPERTY_ID = "UA-54849355-1";
    private static final String PROPERTY_NEW_UI_PERCENTAGE = "NewUIPercentage";
    private static final String PROPERTY_RANDOM_NO = "AirRandomNumber";
    public static long RATE_LIMIT = 86400000;
    public static final String RESULT_RECEIVER = "resultReceiver";
    private static final String TAG = "AdobeAIRMainActivity";
    /* access modifiers changed from: private */
    public String mAirPropsFileUrl = "http://s3-us-west-1.amazonaws.com/gamepreview/prod/airandroid/air.properties";
    private AdobeAIRWebView mAnalyticsWebView = null;
    private Context mCtx = null;
    /* access modifiers changed from: private */
    public int mEnableMyGamesThreshold = 100;
    /* access modifiers changed from: private */
    public boolean mIsGameListDefaultActivity = true;
    private boolean mIsNewUISupported = false;
    /* access modifiers changed from: private */
    public int mNewUIThreshold = 100;
    private AdobeAIRWebView mNotificationWebView = null;
    /* access modifiers changed from: private */
    public int mRandomNumber;
    private AndroidGcmResultReceiver mReceiver = null;
    private Tracker mTracker = null;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mCtx = this;
        copyActivityLevelPrefsToApplicationLevel();
        verifyNewUISupport();
        if (isFirstLaunch()) {
            updateSharedPrefForInitialLaunch();
            decideDefaultActivity();
            updateSharedPrefForDefaultActivity();
        } else {
            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.mCtx);
            this.mIsGameListDefaultActivity = defaultSharedPreferences.getBoolean(PROPERTY_DEFAULT_ACTIVITY, true);
            if (isWidgetShown()) {
                this.mRandomNumber = 0;
                this.mIsGameListDefaultActivity = false;
                SharedPreferences.Editor edit = defaultSharedPreferences.edit();
                edit.putBoolean(PROPERTY_DEFAULT_ACTIVITY, this.mIsGameListDefaultActivity);
                edit.commit();
            } else {
                this.mRandomNumber = defaultSharedPreferences.getInt(PROPERTY_RANDOM_NO, 100);
            }
            this.mNewUIThreshold = defaultSharedPreferences.getInt(PROPERTY_NEW_UI_PERCENTAGE, this.mNewUIThreshold);
            this.mEnableMyGamesThreshold = defaultSharedPreferences.getInt(PROPERTY_ENABLE_MY_GAMES_PERCENTAGE, this.mEnableMyGamesThreshold);
        }
        onNewIntent(getIntent());
        Tracker tracker = getTracker();
        tracker.setScreenName(TAG);
        tracker.send(new HitBuilders.AppViewBuilder().build());
        startService(new Intent(this, ShakeListenerService.class));
    }

    private void copyActivityLevelPrefsToApplicationLevel() {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this.mCtx).edit();
        SharedPreferences sharedPreferences = getSharedPreferences(AdobeAIR.class.getSimpleName(), 0);
        for (Map.Entry next : sharedPreferences.getAll().entrySet()) {
            Object value = next.getValue();
            if (value instanceof String) {
                edit.putString((String) next.getKey(), value.toString());
            } else if (value instanceof Long) {
                edit.putLong((String) next.getKey(), ((Long) value).longValue());
            } else if (value instanceof Integer) {
                edit.putInt((String) next.getKey(), ((Integer) value).intValue());
            } else if (value instanceof Boolean) {
                edit.putBoolean((String) next.getKey(), ((Boolean) value).booleanValue());
            }
        }
        edit.commit();
        SharedPreferences.Editor edit2 = sharedPreferences.edit();
        edit2.clear();
        edit2.commit();
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras == null || !extras.containsKey("gameUrl")) {
            launchDefaultActivity();
        } else {
            String string = extras.getString("gameUrl");
            this.mNotificationWebView = new AdobeAIRWebView(this.mCtx);
            this.mNotificationWebView.create();
            this.mNotificationWebView.loadUrl(string);
            if (extras.containsKey(MSG_ID)) {
                this.mNotificationWebView.loadAnalyticsUrl(NOTIFICATION_ANALYTICS_URL + extras.getString(MSG_ID));
            }
        }
        decideDefaultActivityForNextLaunch();
    }

    public void onBackPressed() {
        launchDefaultActivity();
    }

    private void launchDefaultActivity() {
        boolean z = false;
        Intent intent = new Intent();
        if (this.mIsGameListDefaultActivity) {
            intent.setClassName(this, "com.adobe.air.AdobeAIR");
        } else {
            intent.setClassName(this, "com.adobe.mobile_playpanel.LeadingPageActivity");
            if (AppManager.doesDeviceHaveExcessiveApps(this)) {
                intent.putExtra(ENABLE_MY_GAMES, false);
            } else {
                if (this.mRandomNumber <= this.mEnableMyGamesThreshold) {
                    z = true;
                }
                intent.putExtra(ENABLE_MY_GAMES, z);
            }
            this.mAnalyticsWebView = new AdobeAIRWebView(this.mCtx);
            this.mAnalyticsWebView.createAnalyticsWebView();
            this.mAnalyticsWebView.loadAnalyticsUrl(NEW_UI_ANALYTICS_URL);
        }
        startActivity(intent);
    }

    private boolean isFirstLaunch() {
        return PreferenceManager.getDefaultSharedPreferences(this.mCtx).getBoolean(PROPERTY_FIRST_LAUNCH, true);
    }

    private void updateSharedPrefForInitialLaunch() {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this.mCtx).edit();
        edit.putBoolean(PROPERTY_FIRST_LAUNCH, false);
        edit.commit();
    }

    /* access modifiers changed from: private */
    public void updateSharedPrefForDefaultActivity() {
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(this.mCtx).edit();
        edit.putBoolean(PROPERTY_DEFAULT_ACTIVITY, this.mIsGameListDefaultActivity);
        if (this.mIsNewUISupported) {
            edit.putInt(PROPERTY_RANDOM_NO, this.mRandomNumber);
            edit.putInt(PROPERTY_NEW_UI_PERCENTAGE, this.mNewUIThreshold);
            if (AppManager.doesDeviceHaveExcessiveApps(this)) {
                edit.putInt(PROPERTY_ENABLE_MY_GAMES_PERCENTAGE, 0);
            } else {
                edit.putInt(PROPERTY_ENABLE_MY_GAMES_PERCENTAGE, this.mEnableMyGamesThreshold);
            }
        }
        edit.commit();
    }

    private void decideDefaultActivity() {
        boolean z = false;
        if (this.mIsNewUISupported) {
            try {
                if (isWidgetShown()) {
                    this.mRandomNumber = 0;
                } else {
                    this.mRandomNumber = generateRandomNumber();
                }
            } catch (NumberFormatException e) {
                this.mRandomNumber = 100;
            }
            if (this.mRandomNumber > this.mNewUIThreshold) {
                z = true;
            }
            this.mIsGameListDefaultActivity = z;
            return;
        }
        this.mIsGameListDefaultActivity = true;
    }

    private void verifyNewUISupport() {
        if (Build.VERSION.SDK_INT >= 10) {
            this.mIsNewUISupported = true;
        }
    }

    private int generateRandomNumber() throws NumberFormatException {
        Random random;
        String string = Settings.Secure.getString(getApplicationContext().getContentResolver(), "android_id");
        if (string == null) {
            random = new Random();
        } else {
            random = new Random(new Date().getTime() + ((long) string.hashCode()));
        }
        return random.nextInt(100);
    }

    private void decideDefaultActivityForNextLaunch() {
        if (this.mIsNewUISupported && Utils.isNetworkAvailable(getApplicationContext())) {
            configureTestEnv();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        InputStream content = new DefaultHttpClient().execute(new HttpGet(AdobeAIRMainActivity.this.mAirPropsFileUrl)).getEntity().getContent();
                        Properties properties = new Properties();
                        properties.load(content);
                        if (properties != null) {
                            String property = properties.getProperty(AdobeAIRMainActivity.PROPERTY_NEW_UI_PERCENTAGE);
                            if (property != null) {
                                try {
                                    int unused = AdobeAIRMainActivity.this.mNewUIThreshold = Integer.parseInt(property);
                                    boolean unused2 = AdobeAIRMainActivity.this.mIsGameListDefaultActivity = AdobeAIRMainActivity.this.mRandomNumber > AdobeAIRMainActivity.this.mNewUIThreshold;
                                } catch (Exception e) {
                                }
                            }
                            try {
                                String property2 = properties.getProperty(AdobeAIRMainActivity.PROPERTY_ENABLE_MY_GAMES_PERCENTAGE);
                                if (property2 != null) {
                                    int unused3 = AdobeAIRMainActivity.this.mEnableMyGamesThreshold = Integer.parseInt(property2);
                                }
                            } catch (Exception e2) {
                            }
                            AdobeAIRMainActivity.this.updateSharedPrefForDefaultActivity();
                        }
                    } catch (Exception e3) {
                    }
                }
            }).start();
        }
    }

    private void registerForNotifications() {
        this.mReceiver = new AndroidGcmResultReceiver(new Handler());
        this.mReceiver.setReceiver(this);
        Intent intent = new Intent(this.mCtx, AndroidGcmRegistrationService.class);
        intent.putExtra(RESULT_RECEIVER, this.mReceiver);
        this.mCtx.startService(intent);
    }

    public void onReceiveResult(int i, Bundle bundle) {
        GooglePlayServicesUtil.getErrorDialog(i, (Activity) this.mCtx, PLAY_SERVICES_RESOLUTION_REQUEST).show();
    }

    private void configureTestEnv() {
        String string;
        try {
            Bundle bundle = getPackageManager().getActivityInfo(getComponentName(), 128).metaData;
            if (bundle != null && (string = bundle.getString(AIR_PROPERTIES_URL)) != null && !string.isEmpty()) {
                this.mAirPropsFileUrl = string;
            }
        } catch (PackageManager.NameNotFoundException | NullPointerException e) {
        }
    }

    private boolean isWidgetShown() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (defaultSharedPreferences.getBoolean("widgetShown", false) || defaultSharedPreferences.getBoolean("featuredWidgetShown", false)) {
            return true;
        }
        return false;
    }

    private synchronized Tracker getTracker() {
        if (this.mTracker == null) {
            GoogleAnalytics instance = GoogleAnalytics.getInstance(this);
            instance.enableAutoActivityReports(getApplication());
            this.mTracker = instance.newTracker(PROPERTY_ID);
        }
        return this.mTracker;
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }
}
