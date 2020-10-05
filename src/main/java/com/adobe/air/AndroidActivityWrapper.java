package com.adobe.air;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.UiModeManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.adobe.air.telephony.AndroidTelephonyManager;
import com.adobe.air.utils.Utils;
import com.adobe.flashplayer.HDMIUtils;
import com.distriqt.extension.inappbilling.BuildConfig;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AndroidActivityWrapper {
    private static final String ADOBE_COM = "adobe.com";
    private static final int ASPECT_RATIO_ANY = 3;
    private static final int ASPECT_RATIO_LANDSCAPE = 2;
    private static final int ASPECT_RATIO_PORTRAIT = 1;
    public static final int IMAGE_PICKER_REQUEST_CODE = 2;
    private static final int INVOKE_EVENT_OPEN_URL = 1;
    private static final int INVOKE_EVENT_STANDARD = 0;
    private static final String LOG_TAG = "AndroidActivityWrapper";
    public static final int STILL_PICTURE_REQUEST_CODE = 3;
    public static final int VIDEO_CAPTURE_REQUEST_CODE = 4;
    public static final int WEBVIEW_UPLOAD_FILE_CHOOSER_CODE = 5;
    private static final String WWW_ADOBE_COM = "www.adobe.com";
    private static boolean mIsAndroidTV = false;
    private static AndroidActivityWrapper sActivityWrapper = null;
    private static AndroidTelephonyManager sAndroidTelephonyManager = null;
    private static boolean sApplicationLaunched = false;
    private static boolean sDepthAndStencil = false;
    private static Entrypoints sEntryPoint = null;
    private static String sGamePreviewHost = BuildConfig.FLAVOR;
    private static boolean sHasCaptiveRuntime = false;
    private static AndroidIdleState sIdleStateManager = null;
    private static boolean sIsSwfPreviewMode = false;
    private static boolean sRuntimeLibrariesLoaded = false;
    private int debuggerPort = -1;
    private KeyguardManager keyGuardManager = null;
    private boolean mActivateEventPending = false;
    private List<ActivityResultCallback> mActivityResultListeners = null;
    private ActivityState mActivityState = ActivityState.STARTED;
    private List<StateChangeCallback> mActivityStateListeners = null;
    private ConfigDownloadListener mConfigDownloadListener = null;
    private boolean mContainsVideo = false;
    private DebuggerSettings mDebuggerSettings = new DebuggerSettings();
    private boolean mDisplayWaitingDialog = false;
    private String mExtraArgs = null;
    private boolean mFullScreenSetFromMetaData = false;
    private int mHardKeyboardHidden = 2;
    private int mHardKeyboardType = 0;
    private List<InputEventCallback> mInputEventListeners = null;
    private boolean mInvokeEventPendingFromOnCreate = false;
    private boolean mIsADL = false;
    private boolean mIsDebuggerMode = false;
    private boolean mIsFullScreen = false;
    private String mLibCorePath = null;
    private OrientationManager mOrientationManager = null;
    private boolean mRGB565Override = false;
    private String mRootDir = null;
    private boolean mScreenOn = true;
    private boolean mShowDebuggerDialog = false;
    private String mXmlPath = null;
    private Activity m_activity = null;
    private Application m_application = null;
    private AndroidCameraView m_cameraView = null;
    private FlashEGL m_flashEGL = null;
    private FrameLayout m_layout = null;
    private AIRWindowSurfaceView m_mainView = null;
    private Condition m_newActivityCondition = null;
    private Lock m_newActivityLock = null;
    private RelativeLayout m_overlaysLayout = null;
    private boolean m_planeBreakCascade = false;
    private boolean m_planeCascadeInit = false;
    private int m_planeCascadeStep = 0;
    private List<SurfaceView> m_planes = null;
    private Context m_runtimeContext = null;
    private boolean m_skipKickCascade = true;
    private SurfaceView m_videoView = null;

    interface ActivityResultCallback {
        void onActivityResult(int i, int i2, Intent intent);
    }

    public enum ActivityState {
        STARTED,
        RESTARTED,
        RESUMED,
        PAUSED,
        STOPPED,
        DESTROYED
    }

    private enum DebugMode {
        None,
        ConnectMode,
        ListenMode,
        ConflictMode
    }

    interface InputEventCallback {
        boolean onGenericMotionEvent(MotionEvent motionEvent);

        boolean onKeyEvent(KeyEvent keyEvent);
    }

    public static class PlaneID {
        public static final int PLANE_CAMERA = 5;
        public static final int PLANE_COUNT = 8;
        public static final int PLANE_FLASH = 3;
        public static final int PLANE_OVERLAY = 2;
        public static final int PLANE_STAGE3D = 6;
        public static final int PLANE_STAGETEXT = 1;
        public static final int PLANE_STAGEVIDEO = 7;
        public static final int PLANE_STAGEVIDEOAUTOMATIC = 4;
        public static final int PLANE_STAGEWEBVIEW = 0;
    }

    interface StateChangeCallback {
        void onActivityStateChanged(ActivityState activityState);

        void onConfigurationChanged(Configuration configuration);
    }

    private native void nativeActivateEvent();

    private native void nativeDeactivateEvent();

    private native void nativeLowMemoryEvent();

    private native void nativeOnFocusListener(boolean z);

    private native void nativeSendInvokeEventWithData(String str, String str2, int i);

    private native void nativeSetVisible(boolean z);

    public boolean isScreenOn() {
        return this.mScreenOn;
    }

    public boolean isScreenLocked() {
        return this.keyGuardManager.inKeyguardRestrictedInputMode();
    }

    public boolean isResumed() {
        return this.mActivityState == ActivityState.RESUMED;
    }

    public boolean isStarted() {
        return this.mActivityState == ActivityState.STARTED || this.mActivityState == ActivityState.RESTARTED;
    }

    public static boolean isGingerbread() {
        return Build.VERSION.SDK_INT >= 9;
    }

    public static boolean isHoneycomb() {
        return Build.VERSION.SDK_INT >= 11;
    }

    public static boolean isIceCreamSandwich() {
        return Build.VERSION.SDK_INT >= 14;
    }

    public static boolean isJellybean() {
        return Build.VERSION.SDK_INT >= 16;
    }

    public static AndroidActivityWrapper CreateAndroidActivityWrapper(Activity activity) {
        return CreateAndroidActivityWrapper(activity, false);
    }

    private static boolean isAndroidTV(Activity activity) {
        try {
            if (((UiModeManager) activity.getSystemService("uimode")).getCurrentModeType() == 4) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static AndroidActivityWrapper CreateAndroidActivityWrapper(Activity activity, Boolean bool) {
        sHasCaptiveRuntime = bool.booleanValue();
        if (bool.booleanValue()) {
            Utils.setRuntimePackageName(activity.getApplicationContext().getPackageName());
        } else {
            Utils.setRuntimePackageName("com.adobe.air");
        }
        if (sActivityWrapper == null) {
            sActivityWrapper = new AndroidActivityWrapper(activity);
        }
        mIsAndroidTV = isAndroidTV(activity);
        return sActivityWrapper;
    }

    private AndroidActivityWrapper(Activity activity) {
        this.m_activity = activity;
        this.m_newActivityLock = new ReentrantLock();
        this.m_newActivityCondition = this.m_newActivityLock.newCondition();
        this.m_application = activity.getApplication();
        LoadRuntimeLibraries();
        this.keyGuardManager = (KeyguardManager) activity.getSystemService("keyguard");
    }

    public static AndroidActivityWrapper GetAndroidActivityWrapper() {
        return sActivityWrapper;
    }

    public static boolean GetHasCaptiveRuntime() {
        return sHasCaptiveRuntime;
    }

    public static boolean GetIsAndroidTV() {
        return mIsAndroidTV;
    }

    public static boolean IsGamePreviewMode() {
        return sIsSwfPreviewMode;
    }

    public static boolean GetDepthAndStencilForGamePreview() {
        return sDepthAndStencil;
    }

    public static boolean ShouldShowGamePreviewWatermark() {
        Boolean valueOf = Boolean.valueOf(sIsSwfPreviewMode);
        if (valueOf.booleanValue() && (sGamePreviewHost.equalsIgnoreCase("www.adobe.com") || sGamePreviewHost.equalsIgnoreCase(ADOBE_COM))) {
            valueOf = false;
        }
        return valueOf.booleanValue();
    }

    public Activity getActivity() {
        return this.m_activity;
    }

    public void setSpeakerphoneOn(boolean z) {
        ((AudioManager) getActivity().getSystemService("audio")).setSpeakerphoneOn(z);
    }

    public boolean getSpeakerphoneOn() {
        return ((AudioManager) getActivity().getSystemService("audio")).isSpeakerphoneOn();
    }

    public boolean getWebContentsDebuggingEnabled() {
        Boolean bool;
        try {
            Bundle bundle = this.m_activity.getPackageManager().getActivityInfo(this.m_activity.getComponentName(), 128).metaData;
            if (!(bundle == null || (bool = (Boolean) bundle.get("webContentsDebuggingEnabled")) == null)) {
                return bool.booleanValue();
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }

    public void registerPlane(SurfaceView surfaceView, int i) {
        this.m_planes.set(i, surfaceView);
        planeBreakCascade();
    }

    public void unregisterPlane(int i) {
        this.m_planes.set(i, (Object) null);
        planeBreakCascade();
    }

    public void planeCleanCascade() {
        if (!this.m_planeCascadeInit) {
            this.m_planeCascadeInit = true;
            planeBreakCascade();
        }
    }

    public void planeBreakCascade() {
        int i = 0;
        for (int i2 = 0; i2 < 8; i2++) {
            if (this.m_planes.get(i2) != null) {
                i++;
            }
        }
        if (i > 1) {
            this.m_planeBreakCascade = true;
        }
    }

    private boolean planeRemovedSuccessfully(SurfaceView surfaceView) {
        if (!surfaceView.getHolder().getSurface().isValid()) {
            return true;
        }
        if ((Build.MODEL.equals("LT18i") || Build.MODEL.equals("LT15i") || Build.MODEL.equals("Arc")) && isIceCreamSandwich() && this.m_layout.indexOfChild(surfaceView) < 0) {
            return true;
        }
        return false;
    }

    public void planeKickCascade() {
        if (isHoneycomb() && this.m_skipKickCascade) {
            return;
        }
        if (!isJellybean() || !this.mContainsVideo) {
            planeCleanCascade();
            if (this.m_layout != null) {
                if (this.m_planeBreakCascade) {
                    for (int i = 0; i < 8; i++) {
                        if (this.m_planes.get(i) != null && this.m_layout.indexOfChild(this.m_planes.get(i)) >= 0) {
                            this.m_layout.removeView(this.m_planes.get(i));
                        }
                    }
                    this.m_planeBreakCascade = false;
                    int i2 = 0;
                    while (true) {
                        if (i2 < 8) {
                            if (this.m_planes.get(i2) != null && !planeRemovedSuccessfully(this.m_planes.get(i2))) {
                                this.m_planeBreakCascade = true;
                                break;
                            }
                            i2++;
                        } else {
                            break;
                        }
                    }
                    this.m_planeCascadeStep = 0;
                }
                if (this.m_planeCascadeStep == 0) {
                    planeStepCascade();
                    this.m_mainView.requestFocus();
                }
            }
        }
    }

    public void planeStepCascade() {
        this.m_skipKickCascade = false;
        if (this.m_layout != null && !this.m_planeBreakCascade) {
            while (this.m_planeCascadeStep < 8) {
                if (this.m_planes.get(this.m_planeCascadeStep) != null) {
                    if (this.m_layout.indexOfChild(this.m_planes.get(this.m_planeCascadeStep)) < 0) {
                        this.m_layout.addView(this.m_planes.get(this.m_planeCascadeStep), 0);
                    }
                    this.m_planeCascadeStep++;
                    return;
                }
                this.m_planeCascadeStep++;
            }
        }
    }

    public void ensureZOrder() {
        for (int i = 7; i >= 0; i--) {
            if (this.m_planes.get(i) != null && this.m_layout.indexOfChild(this.m_planes.get(i)) >= 0) {
                this.m_layout.bringChildToFront(this.m_planes.get(i));
            }
        }
    }

    public Context getRuntimeContext() {
        return this.m_runtimeContext;
    }

    public Application getApplication() {
        return this.m_application;
    }

    public Context getApplicationContext() {
        return this.m_application;
    }

    public Context getDefaultContext() {
        if (this.m_activity != null) {
            return this.m_activity;
        }
        return this.m_application;
    }

    public int getDefaultIntentFlags() {
        if (this.m_activity != null) {
            return 0;
        }
        return 268435456;
    }

    public RelativeLayout getOverlaysLayout(boolean z) {
        if (z && this.m_overlaysLayout == null) {
            this.m_overlaysLayout = new RelativeLayout(this.m_activity);
            this.m_layout.addView(this.m_overlaysLayout);
        }
        return this.m_overlaysLayout;
    }

    public void didRemoveOverlay() {
        if (this.m_overlaysLayout != null && this.m_overlaysLayout.getChildCount() == 0) {
            this.m_layout.removeView(this.m_overlaysLayout);
            this.m_overlaysLayout = null;
        }
    }

    public View getView() {
        return this.m_mainView;
    }

    public AndroidCameraView getCameraView() {
        return this.m_cameraView;
    }

    public boolean isApplicationLaunched() {
        return sApplicationLaunched;
    }

    public FlashEGL getEgl() {
        if (this.m_flashEGL == null) {
            this.m_flashEGL = FlashEGLFactory.CreateFlashEGL();
        }
        return this.m_flashEGL;
    }

    public boolean isSurfaceValid() {
        return this.m_mainView != null && this.m_mainView.isSurfaceValid();
    }

    public void SendIntentToRuntime(Class<?> cls, String str, String str2) {
        try {
            Intent intent = new Intent(this.m_runtimeContext, cls);
            intent.setAction(str);
            intent.addCategory(str2);
            this.m_activity.startActivity(intent);
        } catch (Exception e) {
        }
    }

    public void SendIntentToRuntime(Class<?> cls, String str, String str2, String str3, String str4) {
        try {
            Intent intent = new Intent(this.m_runtimeContext, cls);
            intent.setAction(str);
            intent.addCategory(str2);
            intent.putExtra(str3, str4);
            this.m_activity.startActivity(intent);
        } catch (Exception e) {
        }
    }

    public void StartDownloadConfigService() {
        Intent intent = new Intent();
        intent.setPackage(Utils.getRuntimePackageName());
        intent.setAction(AIRService.INTENT_DOWNLOAD_CONFIG);
        try {
            getApplicationContext().startService(intent);
        } catch (SecurityException e) {
        }
    }

    public void ShowImmediateUpdateDialog() {
        String str;
        String str2;
        try {
            Bundle bundle = this.m_activity.getPackageManager().getActivityInfo(this.m_activity.getComponentName(), 128).metaData;
            if (bundle != null) {
                str2 = (String) bundle.get("airDownloadURL");
            } else {
                str2 = null;
            }
            str = str2;
        } catch (PackageManager.NameNotFoundException e) {
            str = null;
        }
        if (str != null) {
            SendIntentToRuntime(AIRUpdateDialog.class, "android.intent.action.MAIN", "AIRUpdateDialog", "airDownloadURL", str);
            return;
        }
        SendIntentToRuntime(AIRUpdateDialog.class, "android.intent.action.MAIN", "AIRUpdateDialog");
    }

    private void initializeAndroidAppVars(ApplicationInfo applicationInfo) {
        ApplicationFileManager.setAndroidPackageName(applicationInfo.packageName);
        ApplicationFileManager.setAndroidAPKPath(applicationInfo.sourceDir);
        ApplicationFileManager.processAndroidDataPath(this.m_application.getCacheDir().getAbsolutePath());
    }

    private void parseArgs(Activity activity, String[] strArr) {
        String str = BuildConfig.FLAVOR;
        String str2 = BuildConfig.FLAVOR;
        String str3 = BuildConfig.FLAVOR;
        String str4 = "false";
        String str5 = "false";
        String str6 = "false";
        String str7 = BuildConfig.FLAVOR;
        try {
            str = strArr[0];
            str2 = strArr[1];
            str3 = strArr[2];
            str4 = strArr[3];
            str5 = strArr[4];
            if (strArr.length >= 6) {
                str6 = strArr[5];
                str7 = strArr[6];
            }
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        this.mExtraArgs = str3;
        this.mIsADL = Boolean.valueOf(str4).booleanValue();
        this.mIsDebuggerMode = Boolean.valueOf(str5).booleanValue();
        sIsSwfPreviewMode = Boolean.valueOf(str6).booleanValue();
        sGamePreviewHost = str7;
        initializeAndroidAppVars(this.m_activity.getApplicationInfo());
        if (this.mIsADL) {
            this.mXmlPath = str;
            this.mRootDir = str2;
            return;
        }
        this.mXmlPath = ApplicationFileManager.getAppXMLRoot();
        this.mRootDir = ApplicationFileManager.getAppRoot();
    }

    public void onCreate(Activity activity, String[] strArr) {
        this.m_activity = activity;
        this.mActivityState = ActivityState.STARTED;
        parseArgs(activity, strArr);
        SignalNewActivityCreated();
        try {
            this.m_runtimeContext = this.m_activity.createPackageContext(Utils.getRuntimePackageName(), 4);
        } catch (Exception e) {
        }
        if (!this.mIsDebuggerMode || this.mIsADL || sApplicationLaunched || DeviceProfiling.checkAndInitiateProfiler(this.m_activity)) {
            afterOnCreate();
        } else {
            checkForDebuggerAndLaunchDialog();
        }
        this.mInvokeEventPendingFromOnCreate = true;
        this.mConfigDownloadListener = ConfigDownloadListener.GetConfigDownloadListener();
    }

    private void afterOnCreate() {
        Boolean bool;
        try {
            if (this.m_planes == null) {
                this.m_planes = new ArrayList(8);
                for (int i = 0; i < 8; i++) {
                    this.m_planes.add(i, (Object) null);
                }
            }
            Context applicationContext = getApplicationContext();
            this.m_layout = new FrameLayout(applicationContext);
            this.m_mainView = new AIRWindowSurfaceView(applicationContext, this);
            if (this.m_cameraView == null && this.m_runtimeContext.checkCallingOrSelfPermission("android.permission.CAMERA") == 0) {
                this.m_cameraView = new AndroidCameraView(applicationContext, this);
            }
            if (this.m_cameraView != null) {
                this.m_layout.addView(this.m_cameraView, 8, 16);
            }
            Bundle bundle = this.m_activity.getPackageManager().getActivityInfo(this.m_activity.getComponentName(), 128).metaData;
            if (!(bundle == null || (bool = (Boolean) bundle.get("containsVideo")) == null || !bool.booleanValue())) {
                this.mContainsVideo = bool.booleanValue();
                this.m_videoView = this.m_mainView.getVideoView();
                this.m_layout.addView(this.m_videoView, 0);
            }
            this.m_layout.addView(this.m_mainView);
            if (this.m_overlaysLayout != null) {
                this.m_layout.addView(this.m_overlaysLayout);
            }
            this.m_activity.setContentView(this.m_layout);
            if (!((!this.mIsADL && !this.mShowDebuggerDialog) || this.m_activity == null || this.m_activity.getCurrentFocus() == this.m_mainView)) {
                this.m_mainView.requestFocus();
                this.m_mainView.onWindowFocusChanged(true);
            }
            if (!this.mFullScreenSetFromMetaData) {
                setFullScreenFromMetaData();
            }
            this.mFullScreenSetFromMetaData = true;
            if (getIsFullScreen()) {
                this.m_mainView.setFullScreen();
            }
            this.mHardKeyboardHidden = this.m_activity.getResources().getConfiguration().hardKeyboardHidden;
            this.mHardKeyboardType = this.m_activity.getResources().getConfiguration().keyboard;
            this.mOrientationManager = OrientationManager.getOrientationManager();
            this.mOrientationManager.onActivityCreated(this.m_activity, this.m_mainView);
            callActivityStateListeners();
            HDMIUtils.initHelper(applicationContext);
        } catch (Exception e) {
        }
    }

    private void LaunchApplication(Activity activity, AIRWindowSurfaceView aIRWindowSurfaceView, String str, String str2, String str3, boolean z, boolean z2) {
        String str4;
        String str5;
        String str6;
        if (!sApplicationLaunched) {
            if (z) {
                try {
                    String stringExtra = activity.getIntent().getStringExtra("args");
                    if (stringExtra != null) {
                        String[] split = stringExtra.split(" ");
                        str = split[0];
                        str2 = split[1];
                        if (split.length >= 2) {
                            str3 = split[2] + " ";
                        }
                        for (int i = 3; i < split.length; i++) {
                            str3 = str3 + split[i] + " ";
                        }
                    }
                    str4 = str3;
                    str5 = str2;
                    str6 = str;
                } catch (Exception e) {
                    str4 = str3;
                    str5 = str2;
                    str6 = str;
                }
            } else if (sIsSwfPreviewMode) {
                try {
                    String dataString = activity.getIntent().getDataString();
                    if (dataString != null && dataString.indexOf("?") > 0) {
                        int i2 = -1;
                        for (String str7 : dataString.substring(dataString.indexOf("?") + 1).split("&")) {
                            if (str7.substring(0, str7.indexOf("=")).equalsIgnoreCase("depthAndStencil")) {
                                if (str7.substring(str7.indexOf("=") + 1).equalsIgnoreCase("true")) {
                                    sDepthAndStencil = true;
                                } else {
                                    sDepthAndStencil = false;
                                }
                            } else if (str7.substring(0, str7.indexOf("=")).equalsIgnoreCase("autoorients")) {
                                if (str7.substring(str7.indexOf("=") + 1).equalsIgnoreCase("true")) {
                                    setAutoOrients(true);
                                } else {
                                    setAutoOrients(false);
                                }
                            } else if (str7.substring(0, str7.indexOf("=")).equalsIgnoreCase("aspectratio")) {
                                String substring = str7.substring(str7.indexOf("=") + 1);
                                if (substring.equalsIgnoreCase("portrait")) {
                                    i2 = 1;
                                } else if (substring.equalsIgnoreCase("landscape")) {
                                    i2 = 2;
                                } else if (substring.equalsIgnoreCase("any")) {
                                    i2 = 3;
                                }
                            }
                        }
                        if (i2 != -1) {
                            setAspectRatio(i2);
                        }
                    }
                    str4 = str3;
                    str5 = str2;
                    str6 = str;
                } catch (Exception e2) {
                    str4 = str3;
                    str5 = str2;
                    str6 = str;
                }
            } else {
                str4 = str3;
                str5 = str2;
                str6 = str;
            }
            try {
                Context applicationContext = getApplicationContext();
                sEntryPoint = new Entrypoints();
                sEntryPoint.EntryMain(str6, str5, str4, Utils.getRuntimePackageName(), aIRWindowSurfaceView, activity.getApplication(), activity.getApplicationInfo(), applicationContext, this, z, z2);
                sIdleStateManager = AndroidIdleState.GetIdleStateManager(applicationContext);
                sApplicationLaunched = true;
            } catch (Exception e3) {
            }
        }
    }

    private void setMainView(View view) {
        if (sApplicationLaunched && sEntryPoint != null && isResumed()) {
            try {
                sEntryPoint.setMainView(view);
            } catch (Exception e) {
            }
        }
    }

    public void initCallStateListener() {
        if (sAndroidTelephonyManager == null) {
            sAndroidTelephonyManager = AndroidTelephonyManager.CreateAndroidTelephonyManager(getApplicationContext());
            sAndroidTelephonyManager.listen(true);
        }
    }

    public void onPause() {
        this.mActivityState = ActivityState.PAUSED;
        callActivityStateListeners();
        if (this.m_mainView != null) {
            this.m_mainView.forceSoftKeyboardDown();
        }
        if (this.mOrientationManager != null) {
            this.mOrientationManager.onActivityPaused();
        }
        if (sIdleStateManager != null) {
            sIdleStateManager.releaseLock();
        }
        if (isApplicationLaunched()) {
            nativeOnFocusListener(false);
            nativeDeactivateEvent();
        }
        planeBreakCascade();
    }

    public void onResume() {
        this.mActivityState = ActivityState.RESUMED;
        callActivityStateListeners();
        if (this.mOrientationManager != null) {
            this.mOrientationManager.onActivityResumed();
        }
        if (sIdleStateManager != null) {
            sIdleStateManager.acquireLock();
        }
        if (isApplicationLaunched()) {
            nativeActivateEvent();
            nativeOnFocusListener(true);
        } else {
            this.mActivateEventPending = true;
        }
        this.m_skipKickCascade = true;
        planeBreakCascade();
    }

    public void onRestart() {
        this.mActivityState = ActivityState.RESTARTED;
        callActivityStateListeners();
        if (this.m_mainView != null) {
            this.m_mainView.HideSoftKeyboardOnWindowFocusChange();
        }
        SetVisible(true);
    }

    public void onStop() {
        this.mActivityState = ActivityState.STOPPED;
        callActivityStateListeners();
        SetVisible(false);
    }

    public void onDestroy() {
        this.mActivityState = ActivityState.DESTROYED;
        callActivityStateListeners();
        if (this.mOrientationManager != null) {
            this.mOrientationManager.onActivityDestroyed();
        }
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= 8) {
                break;
            }
            if (this.m_planes.get(i2) != null) {
                this.m_layout.removeView(this.m_planes.get(i2));
            }
            i = i2 + 1;
        }
        if (this.m_overlaysLayout != null) {
            this.m_layout.removeView(this.m_overlaysLayout);
        }
        this.m_activity = null;
        this.m_cameraView = null;
        this.m_mainView = null;
        this.m_layout = null;
        setMainView((View) null);
        HDMIUtils.closeHelper();
    }

    public void SendInvokeEvent() {
        Intent intent = this.m_activity.getIntent();
        String dataString = intent.getDataString();
        int i = 0;
        if (dataString != null) {
            i = 1;
        }
        nativeSendInvokeEventWithData(dataString, intent.getAction(), i);
    }

    public void onNewIntent(Intent intent) {
        this.m_activity.setIntent(intent);
        SendInvokeEvent();
    }

    public void onSurfaceInitialized() {
        setMainView(this.m_mainView);
        SetVisible(true);
        if (this.mDisplayWaitingDialog) {
            showDialogWaitingForConnection(this.debuggerPort);
            this.mDisplayWaitingDialog = false;
        }
        LaunchApplication(this.m_activity, this.m_mainView, this.mXmlPath, this.mRootDir, this.mExtraArgs, this.mIsADL, this.mIsDebuggerMode);
        if (this.mInvokeEventPendingFromOnCreate) {
            if (!this.mIsADL) {
                SendInvokeEvent();
            }
            this.mInvokeEventPendingFromOnCreate = false;
        }
        if (this.mActivateEventPending) {
            nativeActivateEvent();
            this.mActivateEventPending = false;
        }
        planeCleanCascade();
    }

    public void finish() {
        if (this.m_activity != null) {
            this.m_activity.finish();
        }
    }

    public void onSurfaceDestroyed() {
        SetVisible(false);
    }

    public void onScreenStateChanged(boolean z) {
        this.mScreenOn = z;
        SetVisible(z);
        if (z) {
            this.m_skipKickCascade = false;
            planeBreakCascade();
        }
    }

    private void SetVisible(boolean z) {
        if (z) {
            if (isSurfaceValid() && this.mScreenOn && this.mActivityState != ActivityState.STOPPED && this.mActivityState != ActivityState.DESTROYED) {
                nativeSetVisible(true);
            }
        } else if (isApplicationLaunched()) {
            nativeSetVisible(false);
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        this.mHardKeyboardHidden = configuration.hardKeyboardHidden;
        this.mHardKeyboardType = configuration.keyboard;
        this.mOrientationManager.onConfigurationChanged(configuration);
        callActivityStateListeners(configuration);
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent, boolean z) {
        return callInputEventListeners(keyEvent);
    }

    public boolean dispatchGenericMotionEvent(MotionEvent motionEvent, boolean z) {
        return callInputEventListeners(motionEvent);
    }

    public void onLowMemory() {
        nativeLowMemoryEvent();
    }

    public int getOrientation() {
        return this.mOrientationManager.getOrientation();
    }

    public int getDeviceOrientation() {
        return this.mOrientationManager.getDeviceOrientation();
    }

    public void setOrientation(int i) {
        this.mOrientationManager.setOrientation(i);
    }

    public void setAspectRatio(int i) {
        this.mOrientationManager.setAspectRatio(i);
    }

    public void setAutoOrients(boolean z) {
        this.mOrientationManager.setAutoOrients(z);
    }

    public boolean getAutoOrients() {
        return this.mOrientationManager.getAutoOrients();
    }

    public int[] getSupportedOrientations() {
        return this.mOrientationManager.getSupportedOrientations();
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        callActivityResultListeners(i, i2, intent);
    }

    public boolean isHardKeyboardHidden() {
        return this.mHardKeyboardHidden == 2;
    }

    public int getHardKeyboardType() {
        return this.mHardKeyboardType;
    }

    public boolean needsCompositingSurface() {
        if (this.m_runtimeContext.checkCallingOrSelfPermission("android.permission.CAMERA") == 0) {
            return true;
        }
        return false;
    }

    public void setUseRGB565(Boolean bool) {
        this.mRGB565Override = bool.booleanValue();
    }

    public boolean useRGB565() {
        if (this.mIsADL) {
            return this.mRGB565Override;
        }
        ResourceFileManager resourceFileManager = new ResourceFileManager(this.m_activity);
        return !resourceFileManager.resExists(resourceFileManager.lookupResId(AndroidConstants.ANDROID_RESOURCE_RGBA8888));
    }

    public void BroadcastIntent(String str, String str2) {
        try {
            getDefaultContext().startActivity(Intent.parseUri(str2, 0).setAction(str).addFlags(getDefaultIntentFlags()));
        } catch (ActivityNotFoundException | URISyntaxException e) {
        }
    }

    public void LaunchMarketPlaceForAIR(String str) {
        if (str == null) {
            str = "market://details?id=" + Utils.getRuntimePackageName();
        }
        try {
            BroadcastIntent("android.intent.action.VIEW", str);
        } catch (Exception e) {
        }
    }

    public String GetLibCorePath() {
        if (this.mLibCorePath == null) {
            this.mLibCorePath = Utils.GetLibCorePath(this.m_application);
        }
        return this.mLibCorePath;
    }

    private void LoadRuntimeLibraries() {
        if (!sRuntimeLibrariesLoaded) {
            try {
                System.load(Utils.GetLibSTLPath(this.m_application));
                System.load(GetLibCorePath());
                sRuntimeLibrariesLoaded = true;
            } catch (UnsatisfiedLinkError e) {
            }
        }
    }

    private void showDialogUnableToListenOnPort(int i) {
        new ListenErrorDialog(this.m_activity, i).createAndShowDialog();
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x0069 A[SYNTHETIC, Splitter:B:23:0x0069] */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0080  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x008b  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x009d A[SYNTHETIC, Splitter:B:41:0x009d] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00a8 A[SYNTHETIC, Splitter:B:48:0x00a8] */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x00c1  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x00c4 A[SYNTHETIC, Splitter:B:59:0x00c4] */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x00ca  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x00d3  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x00d9  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x00db  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x00f0  */
    /* JADX WARNING: Removed duplicated region for block: B:88:? A[Catch:{ Exception -> 0x0056 }, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkForDebuggerAndLaunchDialog() {
        /*
            r9 = this;
            r2 = 0
            r3 = 1
            r4 = 0
            boolean r0 = r9.mIsADL
            if (r0 != 0) goto L_0x0057
            com.adobe.air.ResourceFileManager r0 = new com.adobe.air.ResourceFileManager
            android.app.Activity r1 = r9.m_activity
            r0.<init>(r1)
            com.adobe.air.AndroidActivityWrapper$DebugMode r1 = com.adobe.air.AndroidActivityWrapper.DebugMode.None
            java.lang.String r5 = "raw.debuginfo"
            int r5 = r0.lookupResId(r5)
            boolean r5 = r0.resExists((int) r5)
            if (r5 == 0) goto L_0x0108
            java.lang.String r5 = "raw.debuginfo"
            int r5 = r0.lookupResId(r5)     // Catch:{ Exception -> 0x0056 }
            java.io.InputStream r0 = r0.getFileStreamFromRawRes(r5)     // Catch:{ Exception -> 0x0056 }
            java.lang.String r5 = "="
            java.util.HashMap r5 = com.adobe.air.utils.Utils.parseKeyValuePairFile((java.io.InputStream) r0, (java.lang.String) r5)     // Catch:{ Exception -> 0x0056 }
            java.lang.String r0 = "incomingDebugPort"
            java.lang.Object r0 = r5.get(r0)     // Catch:{ Exception -> 0x0056 }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ Exception -> 0x0056 }
            if (r0 == 0) goto L_0x003e
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x0105 }
            r9.debuggerPort = r0     // Catch:{ NumberFormatException -> 0x0105 }
            com.adobe.air.AndroidActivityWrapper$DebugMode r1 = com.adobe.air.AndroidActivityWrapper.DebugMode.ListenMode     // Catch:{ NumberFormatException -> 0x0105 }
        L_0x003e:
            java.lang.String r0 = "outgoingDebugHost"
            java.lang.Object r0 = r5.get(r0)     // Catch:{ Exception -> 0x0056 }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ Exception -> 0x0056 }
            if (r0 == 0) goto L_0x0108
            com.adobe.air.AndroidActivityWrapper$DebugMode r5 = com.adobe.air.AndroidActivityWrapper.DebugMode.ListenMode     // Catch:{ Exception -> 0x0056 }
            if (r1 != r5) goto L_0x0058
            com.adobe.air.AndroidActivityWrapper$DebugMode r0 = com.adobe.air.AndroidActivityWrapper.DebugMode.ConflictMode     // Catch:{ Exception -> 0x0056 }
            java.lang.Exception r0 = new java.lang.Exception     // Catch:{ Exception -> 0x0056 }
            java.lang.String r1 = "listen and connect are mutually exclusive."
            r0.<init>(r1)     // Catch:{ Exception -> 0x0056 }
            throw r0     // Catch:{ Exception -> 0x0056 }
        L_0x0056:
            r0 = move-exception
        L_0x0057:
            return
        L_0x0058:
            com.adobe.air.AndroidActivityWrapper$DebugMode r1 = com.adobe.air.AndroidActivityWrapper.DebugMode.ConnectMode     // Catch:{ Exception -> 0x0056 }
            r8 = r1
            r1 = r0
            r0 = r8
        L_0x005d:
            int[] r5 = com.adobe.air.AndroidActivityWrapper.AnonymousClass2.$SwitchMap$com$adobe$air$AndroidActivityWrapper$DebugMode
            int r6 = r0.ordinal()
            r5 = r5[r6]
            switch(r5) {
                case 1: goto L_0x0069;
                case 2: goto L_0x00db;
                case 3: goto L_0x00f0;
                case 4: goto L_0x0057;
                default: goto L_0x0068;
            }
        L_0x0068:
            goto L_0x0057
        L_0x0069:
            java.net.ServerSocket r1 = new java.net.ServerSocket     // Catch:{ IOException -> 0x009a, SecurityException -> 0x00a5, Exception -> 0x00b0 }
            int r5 = r9.debuggerPort     // Catch:{ IOException -> 0x009a, SecurityException -> 0x00a5, Exception -> 0x00b0 }
            r6 = 1
            java.net.InetAddress r7 = java.net.InetAddress.getLocalHost()     // Catch:{ IOException -> 0x009a, SecurityException -> 0x00a5, Exception -> 0x00b0 }
            r1.<init>(r5, r6, r7)     // Catch:{ IOException -> 0x009a, SecurityException -> 0x00a5, Exception -> 0x00b0 }
            r1.close()     // Catch:{ IOException -> 0x0102, SecurityException -> 0x00ff, Exception -> 0x00fa, all -> 0x00f7 }
            if (r1 == 0) goto L_0x007d
            r1.close()     // Catch:{ IOException -> 0x0097 }
        L_0x007d:
            r1 = r3
        L_0x007e:
            if (r1 == 0) goto L_0x00d3
            r9.mDisplayWaitingDialog = r3
            r9.afterOnCreate()
        L_0x0085:
            com.adobe.air.DebuggerSettings r1 = r9.mDebuggerSettings
            com.adobe.air.AndroidActivityWrapper$DebugMode r2 = com.adobe.air.AndroidActivityWrapper.DebugMode.ListenMode
            if (r0 != r2) goto L_0x00d9
            r0 = r3
        L_0x008c:
            r1.setListen(r0)
            com.adobe.air.DebuggerSettings r0 = r9.mDebuggerSettings
            int r1 = r9.debuggerPort
            r0.setDebugerPort(r1)
            goto L_0x0057
        L_0x0097:
            r1 = move-exception
            r1 = r3
            goto L_0x007e
        L_0x009a:
            r1 = move-exception
        L_0x009b:
            if (r2 == 0) goto L_0x00a0
            r2.close()     // Catch:{ IOException -> 0x00a2 }
        L_0x00a0:
            r1 = r4
            goto L_0x007e
        L_0x00a2:
            r1 = move-exception
            r1 = r4
            goto L_0x007e
        L_0x00a5:
            r1 = move-exception
        L_0x00a6:
            if (r2 == 0) goto L_0x00ab
            r2.close()     // Catch:{ IOException -> 0x00ad }
        L_0x00ab:
            r1 = r4
            goto L_0x007e
        L_0x00ad:
            r1 = move-exception
            r1 = r4
            goto L_0x007e
        L_0x00b0:
            r1 = move-exception
        L_0x00b1:
            java.lang.Class r1 = r1.getClass()     // Catch:{ all -> 0x00cc }
            java.lang.String r1 = r1.getName()     // Catch:{ all -> 0x00cc }
            java.lang.String r5 = "android.os.NetworkOnMainThreadException"
            boolean r1 = r1.equals(r5)     // Catch:{ all -> 0x00cc }
            if (r1 == 0) goto L_0x00ca
            r1 = r3
        L_0x00c2:
            if (r2 == 0) goto L_0x007e
            r2.close()     // Catch:{ IOException -> 0x00c8 }
            goto L_0x007e
        L_0x00c8:
            r2 = move-exception
            goto L_0x007e
        L_0x00ca:
            r1 = r4
            goto L_0x00c2
        L_0x00cc:
            r0 = move-exception
        L_0x00cd:
            if (r2 == 0) goto L_0x00d2
            r2.close()     // Catch:{ IOException -> 0x00f5 }
        L_0x00d2:
            throw r0
        L_0x00d3:
            int r1 = r9.debuggerPort
            r9.showDialogUnableToListenOnPort(r1)
            goto L_0x0085
        L_0x00d9:
            r0 = r4
            goto L_0x008c
        L_0x00db:
            boolean r0 = com.adobe.air.utils.Utils.nativeConnectDebuggerSocket(r1)
            if (r0 != 0) goto L_0x00e6
            r9.showDialogforIpAddress(r1)
            goto L_0x0057
        L_0x00e6:
            com.adobe.air.DebuggerSettings r0 = r9.mDebuggerSettings
            r0.setHost(r1)
            r9.afterOnCreate()
            goto L_0x0057
        L_0x00f0:
            r9.afterOnCreate()
            goto L_0x0057
        L_0x00f5:
            r1 = move-exception
            goto L_0x00d2
        L_0x00f7:
            r0 = move-exception
            r2 = r1
            goto L_0x00cd
        L_0x00fa:
            r2 = move-exception
            r8 = r2
            r2 = r1
            r1 = r8
            goto L_0x00b1
        L_0x00ff:
            r2 = move-exception
            r2 = r1
            goto L_0x00a6
        L_0x0102:
            r2 = move-exception
            r2 = r1
            goto L_0x009b
        L_0x0105:
            r0 = move-exception
            goto L_0x003e
        L_0x0108:
            r0 = r1
            r1 = r2
            goto L_0x005d
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adobe.air.AndroidActivityWrapper.checkForDebuggerAndLaunchDialog():void");
    }

    private void showDialogforIpAddress(String str) {
        getApplicationContext();
        new RemoteDebuggerDialog(this.m_activity).createAndShowDialog(str);
    }

    private void closeDialogWaitingForConnection() {
        Context applicationContext = getApplicationContext();
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.MAIN");
            intent.addCategory("RemoteDebuggerListenerDialogClose");
            intent.putExtra("debuggerPort", this.debuggerPort);
            applicationContext.sendBroadcast(intent);
        } catch (Exception e) {
        }
    }

    private void showDialogWaitingForConnection(final int i) {
        getApplicationContext();
        if (sHasCaptiveRuntime) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(30000);
                        new Socket(InetAddress.getLocalHost(), i).close();
                    } catch (Exception e) {
                    }
                }
            }).start();
            return;
        }
        try {
            Intent intent = new Intent(this.m_runtimeContext, RemoteDebuggerListenerDialog.class);
            intent.setAction("android.intent.action.MAIN");
            intent.addCategory("RemoteDebuggerListenerDialog");
            intent.putExtra("debuggerPort", i);
            this.m_activity.startActivity(intent);
        } catch (Exception e) {
        }
    }

    public void gotResultFromDialog(boolean z, String str) {
        boolean z2;
        if (z) {
            if (str.length() != 0) {
                z2 = Utils.nativeConnectDebuggerSocket(str);
            } else {
                z2 = false;
            }
            if (!z2) {
                showDialogforIpAddress(str);
            } else {
                this.mDebuggerSettings.setHost(str);
                this.mDebuggerSettings.setListen(false);
                this.mShowDebuggerDialog = true;
            }
        } else {
            z2 = false;
        }
        if (z2 || !z) {
            afterOnCreate();
        }
    }

    public void addInputEventListner(InputEventCallback inputEventCallback) {
        if (this.mInputEventListeners == null) {
            this.mInputEventListeners = new ArrayList();
        }
        if (!this.mInputEventListeners.contains(inputEventCallback)) {
            this.mInputEventListeners.add(inputEventCallback);
        }
    }

    public void removeInputEventListner(InputEventCallback inputEventCallback) {
        if (this.mInputEventListeners != null) {
            this.mInputEventListeners.remove(inputEventCallback);
        }
    }

    private boolean callInputEventListeners(KeyEvent keyEvent) {
        boolean z;
        boolean z2;
        if (this.mInputEventListeners == null) {
            return false;
        }
        try {
            int size = this.mInputEventListeners.size();
            int i = 0;
            boolean z3 = false;
            while (i < size) {
                if (!z3) {
                    try {
                        if (!this.mInputEventListeners.get(i).onKeyEvent(keyEvent)) {
                            z2 = false;
                            i++;
                            z3 = z2;
                        }
                    } catch (Exception e) {
                        z = z3;
                    }
                }
                z2 = true;
                i++;
                z3 = z2;
            }
            z = z3;
        } catch (Exception e2) {
            z = false;
        }
        return z;
    }

    private boolean callInputEventListeners(MotionEvent motionEvent) {
        boolean z;
        boolean z2;
        if (this.mInputEventListeners == null) {
            return false;
        }
        try {
            int size = this.mInputEventListeners.size();
            int i = 0;
            boolean z3 = false;
            while (i < size) {
                if (!z3) {
                    try {
                        if (!this.mInputEventListeners.get(i).onGenericMotionEvent(motionEvent)) {
                            z2 = false;
                            i++;
                            z3 = z2;
                        }
                    } catch (Exception e) {
                        z = z3;
                    }
                }
                z2 = true;
                i++;
                z3 = z2;
            }
            z = z3;
        } catch (Exception e2) {
            z = false;
        }
        return z;
    }

    public void addActivityStateChangeListner(StateChangeCallback stateChangeCallback) {
        if (this.mActivityStateListeners == null) {
            this.mActivityStateListeners = new ArrayList();
        }
        if (!this.mActivityStateListeners.contains(stateChangeCallback)) {
            this.mActivityStateListeners.add(stateChangeCallback);
        }
    }

    public void removeActivityStateChangeListner(StateChangeCallback stateChangeCallback) {
        if (this.mActivityStateListeners != null) {
            this.mActivityStateListeners.remove(stateChangeCallback);
        }
    }

    private void callActivityStateListeners() {
        if (this.mActivityStateListeners != null) {
            try {
                int size = this.mActivityStateListeners.size();
                for (int i = 0; i < size; i++) {
                    this.mActivityStateListeners.get(i).onActivityStateChanged(this.mActivityState);
                }
            } catch (Exception e) {
            }
        }
    }

    private void callActivityStateListeners(Configuration configuration) {
        if (this.mActivityStateListeners != null) {
            try {
                int size = this.mActivityStateListeners.size();
                for (int i = 0; i < size; i++) {
                    this.mActivityStateListeners.get(i).onConfigurationChanged(configuration);
                }
            } catch (Exception e) {
            }
        }
    }

    public void addActivityResultListener(ActivityResultCallback activityResultCallback) {
        if (this.mActivityResultListeners == null) {
            this.mActivityResultListeners = new ArrayList();
        }
        if (!this.mActivityResultListeners.contains(activityResultCallback)) {
            this.mActivityResultListeners.add(activityResultCallback);
        }
    }

    public void removeActivityResultListener(ActivityResultCallback activityResultCallback) {
        if (this.mActivityResultListeners != null) {
            this.mActivityResultListeners.remove(activityResultCallback);
        }
    }

    private void callActivityResultListeners(int i, int i2, Intent intent) {
        if (this.mActivityResultListeners != null) {
            try {
                int size = this.mActivityResultListeners.size();
                for (int i3 = 0; i3 < size; i3++) {
                    this.mActivityResultListeners.get(i3).onActivityResult(i, i2, intent);
                }
            } catch (Exception e) {
            }
        }
    }

    private void SignalNewActivityCreated() {
        this.m_newActivityLock.lock();
        this.m_newActivityCondition.signalAll();
        this.m_newActivityLock.unlock();
    }

    public Activity WaitForNewActivity() {
        this.m_newActivityLock.lock();
        try {
            if (this.m_activity == null) {
                this.m_newActivityCondition.await();
            }
        } catch (InterruptedException e) {
        } finally {
            this.m_newActivityLock.unlock();
        }
        return this.m_activity;
    }

    private void setFullScreenFromMetaData() {
        Boolean bool;
        try {
            Bundle bundle = this.m_activity.getPackageManager().getActivityInfo(this.m_activity.getComponentName(), 128).metaData;
            if (bundle != null && (bool = (Boolean) bundle.get("fullScreen")) != null && bool.booleanValue()) {
                this.m_mainView.setFullScreen();
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
    }

    /* access modifiers changed from: protected */
    public void setIsFullScreen(boolean z) {
        this.mIsFullScreen = z;
    }

    /* access modifiers changed from: protected */
    public boolean getIsFullScreen() {
        return this.mIsFullScreen;
    }

    public String GetAppCacheDirectory() {
        return this.m_application.getCacheDir().getAbsolutePath();
    }

    public String GetAppDataDirectory() {
        return this.m_application.getApplicationInfo().dataDir;
    }

    public String GetRuntimeDataDirectory() {
        return this.m_runtimeContext.getApplicationInfo().dataDir + "/";
    }

    public void finishActivityFromChild(Activity activity, int i) {
    }

    public void finishFromChild(Activity activity) {
    }

    public void onAttachedToWindow() {
    }

    public void onBackPressed() {
    }

    public void onContentChanged() {
    }

    public boolean onContextItemSelected(MenuItem menuItem, boolean z) {
        return z;
    }

    public void onContextMenuClosed(Menu menu) {
    }

    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
    }

    public CharSequence onCreateDescription(CharSequence charSequence) {
        return charSequence;
    }

    public boolean onCreateOptionsMenu(Menu menu, boolean z) {
        return z;
    }

    public boolean onCreatePanelMenu(int i, Menu menu, boolean z) {
        return z;
    }

    public View onCreatePanelView(int i, View view) {
        return view;
    }

    public boolean onCreateThumbnail(Bitmap bitmap, Canvas canvas, boolean z) {
        return z;
    }

    public View onCreateView(String str, Context context, AttributeSet attributeSet, View view) {
        return view;
    }

    public void onDetachedFromWindow() {
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent, boolean z) {
        return z;
    }

    public boolean onKeyLongPress(int i, KeyEvent keyEvent, boolean z) {
        return z;
    }

    public boolean onKeyMultiple(int i, int i2, KeyEvent keyEvent, boolean z) {
        return z;
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent, boolean z) {
        return z;
    }

    public boolean onMenuItemSelected(int i, MenuItem menuItem, boolean z) {
        return z;
    }

    public boolean onMenuOpened(int i, Menu menu, boolean z) {
        return z;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem, boolean z) {
        return z;
    }

    public void onOptionsMenuClosed(Menu menu) {
    }

    public void onPanelClosed(int i, Menu menu) {
    }

    public boolean onPrepareOptionsMenu(Menu menu, boolean z) {
        return z;
    }

    public boolean onPreparePanel(int i, View view, Menu menu, boolean z) {
        return z;
    }

    public Object onRetainNonConfigurationInstance(Object obj) {
        return obj;
    }

    public boolean onSearchRequested(boolean z) {
        return z;
    }

    public boolean onTouchEvent(MotionEvent motionEvent, boolean z) {
        return z;
    }

    public boolean onTrackballEvent(MotionEvent motionEvent, boolean z) {
        return z;
    }

    public void onUserInteraction() {
    }

    public void onWindowAttributesChanged(WindowManager.LayoutParams layoutParams) {
    }

    public void onWindowFocusChanged(boolean z) {
    }

    public void onApplyThemeResource(Resources.Theme theme, int i, boolean z) {
    }

    public void onChildTitleChanged(Activity activity, CharSequence charSequence) {
    }

    public Dialog onCreateDialog(int i, Bundle bundle, Dialog dialog) {
        return dialog;
    }

    public Dialog onCreateDialog(int i, Dialog dialog) {
        return dialog;
    }

    public void onPostCreate(Bundle bundle) {
    }

    public void onPostResume() {
    }

    public void onPrepareDialog(int i, Dialog dialog, Bundle bundle) {
    }

    public void onPrepareDialog(int i, Dialog dialog) {
    }

    public void onRestoreInstanceState(Bundle bundle) {
    }

    public void onSaveInstanceState(Bundle bundle) {
    }

    public void onTitleChanged(CharSequence charSequence, int i) {
    }

    public void onUserLeaveHint() {
    }

    public DebuggerSettings GetDebuggerSettings() {
        return this.mDebuggerSettings;
    }

    public void applyDownloadedConfig() {
        if (sEntryPoint != null) {
            sEntryPoint.EntryApplyDownloadedConfig();
        }
    }
}
