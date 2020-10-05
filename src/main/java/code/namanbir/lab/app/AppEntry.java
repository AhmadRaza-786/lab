package code.namanbir.lab.app;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import com.adobe.air.InstallOfferPingUtils;
import com.adobe.air.ResourceIdMap;
import dalvik.system.DexClassLoader;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Date;

public class AppEntry extends Activity {
    /* access modifiers changed from: private */
    public static String AIR_PING_URL = "https://airdownload2.adobe.com/air?";
    private static final String LOG_TAG = "AppEntry";
    private static final String RESOURCE_BUTTON_EXIT = "string.button_exit";
    private static final String RESOURCE_BUTTON_INSTALL = "string.button_install";
    private static final String RESOURCE_CLASS = "code.namanbir.lab.app.R";
    private static final String RESOURCE_TEXT_RUNTIME_REQUIRED = "string.text_runtime_required";
    private static final String RESOURCE_TITLE_ADOBE_AIR = "string.title_adobe_air";
    private static String RUNTIME_PACKAGE_ID = "com.adobe.air";
    private static Object sAndroidActivityWrapper = null;
    private static Class<?> sAndroidActivityWrapperClass;
    private static DexClassLoader sDloader;
    /* access modifiers changed from: private */
    public static boolean sRuntimeClassesLoaded = false;
    /* access modifiers changed from: private */
    public static AppEntry sThis = null;

    private void BroadcastIntent(String action, String data) {
        try {
            startActivity(Intent.parseUri(data, 0).setAction(action).addFlags(268435456));
        } catch (ActivityNotFoundException | URISyntaxException e) {
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v8, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v0, resolved type: java.lang.String} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void launchMarketPlaceForAIR() {
        /*
            r8 = this;
            r1 = 0
            android.content.pm.PackageManager r5 = r8.getPackageManager()     // Catch:{ NameNotFoundException -> 0x003d }
            android.content.ComponentName r6 = r8.getComponentName()     // Catch:{ NameNotFoundException -> 0x003d }
            r7 = 128(0x80, float:1.794E-43)
            android.content.pm.ActivityInfo r2 = r5.getActivityInfo(r6, r7)     // Catch:{ NameNotFoundException -> 0x003d }
            android.os.Bundle r4 = r2.metaData     // Catch:{ NameNotFoundException -> 0x003d }
            if (r4 == 0) goto L_0x001d
            java.lang.String r5 = "airDownloadURL"
            java.lang.Object r5 = r4.get(r5)     // Catch:{ NameNotFoundException -> 0x003d }
            r0 = r5
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ NameNotFoundException -> 0x003d }
            r1 = r0
        L_0x001d:
            r3 = r1
            if (r3 != 0) goto L_0x0035
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "market://details?id="
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r6 = RUNTIME_PACKAGE_ID
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r3 = r5.toString()
        L_0x0035:
            java.lang.String r5 = "android.intent.action.VIEW"
            r8.BroadcastIntent(r5, r3)     // Catch:{ Exception -> 0x003b }
        L_0x003a:
            return
        L_0x003b:
            r5 = move-exception
            goto L_0x003a
        L_0x003d:
            r5 = move-exception
            goto L_0x001d
        */
        throw new UnsupportedOperationException("Method not decompiled: code.namanbir.lab.app.AppEntry.launchMarketPlaceForAIR():void");
    }

    private boolean isRuntimeInstalled() {
        try {
            getPackageManager().getPackageInfo(RUNTIME_PACKAGE_ID, 256);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private boolean isRuntimeOnExternalStorage() {
        try {
            if ((getPackageManager().getApplicationInfo(RUNTIME_PACKAGE_ID, 8192).flags & 262144) == 262144) {
                return true;
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
        }
    }

    private void showDialog(int titleId, String text, int positiveButtonId, int negativeButtonId) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(titleId);
        alertDialogBuilder.setMessage(text);
        alertDialogBuilder.setPositiveButton(positiveButtonId, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                AppEntry.this.launchMarketPlaceForAIR();
                InstallOfferPingUtils.PingAndExit(AppEntry.sThis, AppEntry.AIR_PING_URL, true, false, true);
            }
        });
        alertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                InstallOfferPingUtils.PingAndExit(AppEntry.sThis, AppEntry.AIR_PING_URL, false, false, true);
            }
        });
        alertDialogBuilder.show();
    }

    private void showRuntimeNotInstalledDialog() {
        ResourceIdMap r = new ResourceIdMap(RESOURCE_CLASS);
        showDialog(r.getId(RESOURCE_TITLE_ADOBE_AIR), getString(r.getId(RESOURCE_TEXT_RUNTIME_REQUIRED)) + getString(r.getId("string.text_install_runtime")), r.getId(RESOURCE_BUTTON_INSTALL), r.getId(RESOURCE_BUTTON_EXIT));
    }

    private void showRuntimeOnExternalStorageDialog() {
        ResourceIdMap r = new ResourceIdMap(RESOURCE_CLASS);
        showDialog(r.getId(RESOURCE_TITLE_ADOBE_AIR), getString(r.getId(RESOURCE_TEXT_RUNTIME_REQUIRED)) + getString(r.getId("string.text_runtime_on_external_storage")), r.getId(RESOURCE_BUTTON_INSTALL), r.getId(RESOURCE_BUTTON_EXIT));
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sThis = this;
        Log.i("StartupTime1", ":" + new Date().getTime());
        boolean hasCaptiveRuntime = loadCaptiveRuntimeClasses();
        if (!hasCaptiveRuntime) {
            if (sRuntimeClassesLoaded || isRuntimeInstalled()) {
                loadSharedRuntimeDex();
            } else if (isRuntimeOnExternalStorage()) {
                showRuntimeOnExternalStorageDialog();
                return;
            } else {
                showRuntimeNotInstalledDialog();
                return;
            }
        }
        if (sRuntimeClassesLoaded) {
            createActivityWrapper(hasCaptiveRuntime);
            InvokeWrapperOnCreate();
        } else if (hasCaptiveRuntime) {
            KillSelf();
        } else {
            launchAIRService();
        }
    }

    private void registerForNotifications() {
        Intent serviceIntent = new Intent("com.adobe.air.AndroidGcmRegistrationService");
        serviceIntent.setClassName(RUNTIME_PACKAGE_ID, "com.adobe.air.AndroidGcmRegistrationService");
        startService(serviceIntent);
    }

    private void launchAIRService() {
        try {
            Intent intent = new Intent("com.adobe.air.AIRServiceAction");
            intent.setClassName(RUNTIME_PACKAGE_ID, "com.adobe.air.AIRService");
            bindService(intent, new ServiceConnection() {
                public void onServiceConnected(ComponentName name, IBinder service) {
                    AppEntry.this.unbindService(this);
                    AppEntry.this.loadSharedRuntimeDex();
                    AppEntry.this.createActivityWrapper(false);
                    if (AppEntry.sRuntimeClassesLoaded) {
                        AppEntry.this.InvokeWrapperOnCreate();
                    } else {
                        AppEntry.KillSelf();
                    }
                }

                public void onServiceDisconnected(ComponentName name) {
                }
            }, 1);
        } catch (Exception e) {
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v11, resolved type: java.lang.Object[]} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void InvokeWrapperOnCreate() {
        /*
            r12 = this;
            java.lang.Class<?> r7 = sAndroidActivityWrapperClass     // Catch:{ Exception -> 0x0051 }
            java.lang.String r8 = "onCreate"
            r9 = 2
            java.lang.Class[] r9 = new java.lang.Class[r9]     // Catch:{ Exception -> 0x0051 }
            r10 = 0
            java.lang.Class<android.app.Activity> r11 = android.app.Activity.class
            r9[r10] = r11     // Catch:{ Exception -> 0x0051 }
            r10 = 1
            java.lang.Class<java.lang.String[]> r11 = java.lang.String[].class
            r9[r10] = r11     // Catch:{ Exception -> 0x0051 }
            java.lang.reflect.Method r4 = r7.getMethod(r8, r9)     // Catch:{ Exception -> 0x0051 }
            java.lang.String r6 = ""
            java.lang.String r5 = ""
            java.lang.String r1 = "-nodebug"
            java.lang.Boolean r2 = new java.lang.Boolean     // Catch:{ Exception -> 0x0051 }
            r7 = 0
            r2.<init>(r7)     // Catch:{ Exception -> 0x0051 }
            com.savegame.SavesRestoringPortable.DoSmth(r12)     // Catch:{ Exception -> 0x0051 }
            java.lang.Boolean r3 = new java.lang.Boolean     // Catch:{ Exception -> 0x0051 }
            r7 = 0
            r3.<init>(r7)     // Catch:{ Exception -> 0x0051 }
            r7 = 5
            java.lang.String[] r0 = new java.lang.String[r7]     // Catch:{ Exception -> 0x0051 }
            r7 = 0
            r0[r7] = r6     // Catch:{ Exception -> 0x0051 }
            r7 = 1
            r0[r7] = r5     // Catch:{ Exception -> 0x0051 }
            r7 = 2
            r0[r7] = r1     // Catch:{ Exception -> 0x0051 }
            r7 = 3
            java.lang.String r8 = r2.toString()     // Catch:{ Exception -> 0x0051 }
            r0[r7] = r8     // Catch:{ Exception -> 0x0051 }
            r7 = 4
            java.lang.String r8 = r3.toString()     // Catch:{ Exception -> 0x0051 }
            r0[r7] = r8     // Catch:{ Exception -> 0x0051 }
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x0051 }
            r8 = 0
            r7[r8] = r12     // Catch:{ Exception -> 0x0051 }
            r8 = 1
            r7[r8] = r0     // Catch:{ Exception -> 0x0051 }
            r12.InvokeMethod(r4, r7)     // Catch:{ Exception -> 0x0051 }
        L_0x0050:
            return
        L_0x0051:
            r7 = move-exception
            goto L_0x0050
        */
        throw new UnsupportedOperationException("Method not decompiled: code.namanbir.lab.app.AppEntry.InvokeWrapperOnCreate():void");
    }

    private Object InvokeMethod(Method method, Object... args) {
        if (!sRuntimeClassesLoaded) {
            return null;
        }
        if (args == null) {
            return method.invoke(sAndroidActivityWrapper, new Object[0]);
        }
        try {
            return method.invoke(sAndroidActivityWrapper, args);
        } catch (Exception e) {
            return null;
        }
    }

    /* access modifiers changed from: private */
    public static void KillSelf() {
        Process.killProcess(Process.myPid());
    }

    public void onStart() {
        super.onStart();
    }

    public void onRestart() {
        super.onRestart();
        try {
            if (sRuntimeClassesLoaded) {
                InvokeMethod(sAndroidActivityWrapperClass.getMethod("onRestart", new Class[0]), new Object[0]);
            }
        } catch (Exception e) {
        }
    }

    public void onPause() {
        super.onPause();
        try {
            if (sRuntimeClassesLoaded) {
                InvokeMethod(sAndroidActivityWrapperClass.getMethod("onPause", new Class[0]), new Object[0]);
            }
        } catch (Exception e) {
        }
    }

    public void onResume() {
        super.onResume();
        try {
            if (sRuntimeClassesLoaded) {
                InvokeMethod(sAndroidActivityWrapperClass.getMethod("onResume", new Class[0]), new Object[0]);
            }
        } catch (Exception e) {
        }
    }

    public void onStop() {
        super.onStop();
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onStop", new Class[0]), new Object[0]);
        } catch (Exception e) {
        }
    }

    public void onDestroy() {
        super.onDestroy();
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onDestroy", new Class[0]), new Object[0]);
        } catch (Exception e) {
        }
        sThis = null;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onConfigurationChanged", new Class[]{Configuration.class}), newConfig);
        } catch (Exception e) {
        }
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("dispatchKeyEvent", new Class[]{KeyEvent.class, Boolean.TYPE}), event, false);
        } catch (Exception e) {
        }
        if (0 != 0 || super.dispatchKeyEvent(event)) {
            return true;
        }
        return false;
    }

    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("dispatchGenericMotionEvent", new Class[]{MotionEvent.class, Boolean.TYPE}), event, false);
        } catch (Exception e) {
        }
        if (0 != 0 || super.dispatchGenericMotionEvent(event)) {
            return true;
        }
        return false;
    }

    public void onLowMemory() {
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onLowMemory", new Class[0]), new Object[0]);
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (sRuntimeClassesLoaded) {
                InvokeMethod(sAndroidActivityWrapperClass.getMethod("onActivityResult", new Class[]{Integer.TYPE, Integer.TYPE, Intent.class}), Integer.valueOf(requestCode), Integer.valueOf(resultCode), data);
            }
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent aIntent) {
        Intent ii = aIntent;
        super.onNewIntent(ii);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onNewIntent", new Class[]{Intent.class}), ii);
        } catch (Exception e) {
        }
    }

    private boolean loadCaptiveRuntimeClasses() {
        boolean hasCaptiveRuntime = false;
        try {
            sAndroidActivityWrapperClass = Class.forName("com.adobe.air.AndroidActivityWrapper");
            hasCaptiveRuntime = true;
            if (sAndroidActivityWrapperClass != null) {
                sRuntimeClassesLoaded = true;
            }
        } catch (Exception e) {
        }
        return hasCaptiveRuntime;
    }

    /* access modifiers changed from: private */
    public void loadSharedRuntimeDex() {
        try {
            if (!sRuntimeClassesLoaded) {
                sDloader = new DexClassLoader(RUNTIME_PACKAGE_ID, getFilesDir().getAbsolutePath(), (String) null, createPackageContext(RUNTIME_PACKAGE_ID, 3).getClassLoader());
                sAndroidActivityWrapperClass = sDloader.loadClass("com.adobe.air.AndroidActivityWrapper");
                if (sAndroidActivityWrapperClass != null) {
                    sRuntimeClassesLoaded = true;
                }
            }
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: private */
    public void createActivityWrapper(boolean hasCaptiveRuntime) {
        if (hasCaptiveRuntime) {
            try {
                sAndroidActivityWrapper = sAndroidActivityWrapperClass.getMethod("CreateAndroidActivityWrapper", new Class[]{Activity.class, Boolean.class}).invoke((Object) null, new Object[]{this, Boolean.valueOf(hasCaptiveRuntime)});
            } catch (Exception e) {
            }
        } else {
            sAndroidActivityWrapper = sAndroidActivityWrapperClass.getMethod("CreateAndroidActivityWrapper", new Class[]{Activity.class}).invoke((Object) null, new Object[]{this});
        }
    }

    public void finishActivityFromChild(Activity child, int requestCode) {
        super.finishActivityFromChild(child, requestCode);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("finishActivityFromChild", new Class[]{Activity.class, Integer.TYPE}), child, Integer.valueOf(requestCode));
        } catch (Exception e) {
        }
    }

    public void finishFromChild(Activity child) {
        super.finishFromChild(child);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("finishFromChild", new Class[]{Activity.class}), child);
        } catch (Exception e) {
        }
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onAttachedToWindow", new Class[0]), new Object[0]);
        } catch (Exception e) {
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onBackPressed", new Class[0]), new Object[0]);
        } catch (Exception e) {
        }
    }

    public void onContentChanged() {
        super.onContentChanged();
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onContentChanged", new Class[0]), new Object[0]);
        } catch (Exception e) {
        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        boolean retval = super.onContextItemSelected(item);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onContextItemSelected", new Class[]{MenuItem.class, Boolean.TYPE}), item, Boolean.valueOf(retval))).booleanValue();
        } catch (Exception e) {
            return retval;
        }
    }

    public void onContextMenuClosed(Menu menu) {
        super.onContextMenuClosed(menu);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onContextMenuClosed", new Class[]{Menu.class}), menu);
        } catch (Exception e) {
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onCreateContextMenu", new Class[]{ContextMenu.class, View.class, ContextMenu.ContextMenuInfo.class}), menu, v, menuInfo);
        } catch (Exception e) {
        }
    }

    public CharSequence onCreateDescription() {
        CharSequence retval = super.onCreateDescription();
        try {
            return (CharSequence) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onCreateDescription", new Class[]{CharSequence.class}), retval);
        } catch (Exception e) {
            return retval;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        boolean retval = super.onCreateOptionsMenu(menu);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onCreateOptionsMenu", new Class[]{Menu.class, Boolean.TYPE}), menu, Boolean.valueOf(retval))).booleanValue();
        } catch (Exception e) {
            return retval;
        }
    }

    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        boolean retval = super.onCreatePanelMenu(featureId, menu);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onCreatePanelMenu", new Class[]{Integer.TYPE, Menu.class, Boolean.TYPE}), Integer.valueOf(featureId), menu, Boolean.valueOf(retval))).booleanValue();
        } catch (Exception e) {
            return retval;
        }
    }

    public View onCreatePanelView(int featureId) {
        View retval = super.onCreatePanelView(featureId);
        try {
            return (View) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onCreatePanelView", new Class[]{Integer.TYPE, View.class}), Integer.valueOf(featureId), retval);
        } catch (Exception e) {
            return retval;
        }
    }

    public boolean onCreateThumbnail(Bitmap outBitmap, Canvas canvas) {
        boolean retval = super.onCreateThumbnail(outBitmap, canvas);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onCreateThumbnail", new Class[]{Bitmap.class, Canvas.class, Boolean.TYPE}), outBitmap, canvas, Boolean.valueOf(retval))).booleanValue();
        } catch (Exception e) {
            return retval;
        }
    }

    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View retval = super.onCreateView(name, context, attrs);
        try {
            return (View) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onCreateView", new Class[]{String.class, Context.class, AttributeSet.class, View.class}), name, context, attrs, retval);
        } catch (Exception e) {
            return retval;
        }
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onDetachedFromWindow", new Class[0]), new Object[0]);
        } catch (Exception e) {
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean retval = super.onKeyDown(keyCode, event);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onKeyDown", new Class[]{Integer.TYPE, KeyEvent.class, Boolean.TYPE}), Integer.valueOf(keyCode), event, Boolean.valueOf(retval))).booleanValue();
        } catch (Exception e) {
            return retval;
        }
    }

    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        boolean retval = super.onKeyLongPress(keyCode, event);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onKeyLongPress", new Class[]{Integer.TYPE, KeyEvent.class, Boolean.TYPE}), Integer.valueOf(keyCode), event, Boolean.valueOf(retval))).booleanValue();
        } catch (Exception e) {
            return retval;
        }
    }

    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        boolean retval = super.onKeyMultiple(keyCode, repeatCount, event);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onKeyMultiple", new Class[]{Integer.TYPE, Integer.TYPE, KeyEvent.class, Boolean.TYPE}), Integer.valueOf(keyCode), Integer.valueOf(repeatCount), event, Boolean.valueOf(retval))).booleanValue();
        } catch (Exception e) {
            return retval;
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean retval = super.onKeyUp(keyCode, event);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onKeyUp", new Class[]{Integer.TYPE, KeyEvent.class, Boolean.TYPE}), Integer.valueOf(keyCode), event, Boolean.valueOf(retval))).booleanValue();
        } catch (Exception e) {
            return retval;
        }
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        boolean retval = super.onMenuItemSelected(featureId, item);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onMenuItemSelected", new Class[]{Integer.TYPE, MenuItem.class, Boolean.TYPE}), Integer.valueOf(featureId), item, Boolean.valueOf(retval))).booleanValue();
        } catch (Exception e) {
            return retval;
        }
    }

    public boolean onMenuOpened(int featureId, Menu menu) {
        boolean retval = super.onMenuOpened(featureId, menu);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onMenuOpened", new Class[]{Integer.TYPE, Menu.class, Boolean.TYPE}), Integer.valueOf(featureId), menu, Boolean.valueOf(retval))).booleanValue();
        } catch (Exception e) {
            return retval;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        boolean retval = super.onOptionsItemSelected(item);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onOptionsItemSelected", new Class[]{MenuItem.class, Boolean.TYPE}), item, Boolean.valueOf(retval))).booleanValue();
        } catch (Exception e) {
            return retval;
        }
    }

    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onOptionsMenuClosed", new Class[]{Menu.class}), menu);
        } catch (Exception e) {
        }
    }

    public void onPanelClosed(int featureId, Menu menu) {
        super.onPanelClosed(featureId, menu);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onPanelClosed", new Class[]{Integer.TYPE, Menu.class}), Integer.valueOf(featureId), menu);
        } catch (Exception e) {
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean retval = super.onPrepareOptionsMenu(menu);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onPrepareOptionsMenu", new Class[]{Menu.class, Boolean.TYPE}), menu, Boolean.valueOf(retval))).booleanValue();
        } catch (Exception e) {
            return retval;
        }
    }

    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        boolean retval = super.onPreparePanel(featureId, view, menu);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onPreparePanel", new Class[]{Integer.TYPE, View.class, Menu.class, Boolean.TYPE}), Integer.valueOf(featureId), view, menu, Boolean.valueOf(retval))).booleanValue();
        } catch (Exception e) {
            return retval;
        }
    }

    public Object onRetainNonConfigurationInstance() {
        Object retval = super.onRetainNonConfigurationInstance();
        try {
            return InvokeMethod(sAndroidActivityWrapperClass.getMethod("onRetainNonConfigurationInstance", new Class[]{Object.class}), retval);
        } catch (Exception e) {
            return retval;
        }
    }

    public boolean onSearchRequested() {
        boolean retval = super.onSearchRequested();
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onSearchRequested", new Class[]{Boolean.TYPE}), Boolean.valueOf(retval))).booleanValue();
        } catch (Exception e) {
            return retval;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean retval = super.onTouchEvent(event);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onTouchEvent", new Class[]{MotionEvent.class, Boolean.TYPE}), event, Boolean.valueOf(retval))).booleanValue();
        } catch (Exception e) {
            return retval;
        }
    }

    public boolean onTrackballEvent(MotionEvent event) {
        boolean retval = super.onTrackballEvent(event);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onTrackballEvent", new Class[]{MotionEvent.class, Boolean.TYPE}), event, Boolean.valueOf(retval))).booleanValue();
        } catch (Exception e) {
            return retval;
        }
    }

    public void onUserInteraction() {
        super.onUserInteraction();
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onUserInteraction", new Class[0]), new Object[0]);
        } catch (Exception e) {
        }
    }

    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        super.onWindowAttributesChanged(params);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onWindowAttributesChanged", new Class[]{WindowManager.LayoutParams.class}), params);
        } catch (Exception e) {
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onWindowFocusChanged", new Class[]{Boolean.TYPE}), Boolean.valueOf(hasFocus));
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: protected */
    public void onApplyThemeResource(Resources.Theme theme, int resid, boolean first) {
        super.onApplyThemeResource(theme, resid, first);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onApplyThemeResource", new Class[]{Resources.Theme.class, Integer.TYPE, Boolean.TYPE}), theme, Integer.valueOf(resid), Boolean.valueOf(first));
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: protected */
    public void onChildTitleChanged(Activity childActivity, CharSequence title) {
        super.onChildTitleChanged(childActivity, title);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onChildTitleChanged", new Class[]{Activity.class, CharSequence.class}), childActivity, title);
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: protected */
    public Dialog onCreateDialog(int id) {
        Dialog retval = super.onCreateDialog(id);
        try {
            return (Dialog) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onCreateDialog", new Class[]{Integer.TYPE, Dialog.class}), Integer.valueOf(id), retval);
        } catch (Exception e) {
            return retval;
        }
    }

    /* access modifiers changed from: protected */
    public Dialog onCreateDialog(int id, Bundle args) {
        Dialog retval = super.onCreateDialog(id, args);
        try {
            return (Dialog) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onCreateDialog", new Class[]{Integer.TYPE, Bundle.class, Dialog.class}), Integer.valueOf(id), args, retval);
        } catch (Exception e) {
            return retval;
        }
    }

    /* access modifiers changed from: protected */
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onPostCreate", new Class[]{Bundle.class}), savedInstanceState);
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: protected */
    public void onPostResume() {
        super.onPostResume();
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onPostResume", new Class[0]), new Object[0]);
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: protected */
    public void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onPrepareDialog", new Class[]{R.id.class, Dialog.class}), Integer.valueOf(id), dialog);
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: protected */
    public void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        super.onPrepareDialog(id, dialog, args);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onPrepareDialog", new Class[]{R.id.class, Dialog.class, Bundle.class}), Integer.valueOf(id), dialog, args);
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onRestoreInstanceState", new Class[]{Bundle.class}), savedInstanceState);
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onSaveInstanceState", new Class[]{Bundle.class}), outState);
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: protected */
    public void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onTitleChanged", new Class[]{CharSequence.class, Integer.TYPE}), title, Integer.valueOf(color));
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: protected */
    public void onUserLeaveHint() {
        super.onUserLeaveHint();
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onUserLeaveHint", new Class[0]), new Object[0]);
        } catch (Exception e) {
        }
    }
}
