package com.adobe.air;

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
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import dalvik.system.DexClassLoader;
import java.lang.reflect.Method;
import java.net.URISyntaxException;

public class GamePreviewAppEntry extends Activity {
    private static final String GAME_PREVIEW_APP_XML = "assets/META-INF/AIR/GamePreview-app.xml";
    private static final String GAME_PREVIEW_SRC_DIR = "assets";
    private static final String GAME_PREVIEW_SWF = "assets/GamePreview.swf";
    private static final String LOG_TAG = "AppEntry";
    private static final String RESOURCE_BUTTON_EXIT = "string.button_exit";
    private static final String RESOURCE_BUTTON_INSTALL = "string.button_install";
    private static final String RESOURCE_CLASS = "air.com.adobe.appentry.R";
    private static final String RESOURCE_TEXT_RUNTIME_REQUIRED = "string.text_runtime_required";
    private static final String RESOURCE_TITLE_ADOBE_AIR = "string.title_adobe_air";
    private static String RUNTIME_PACKAGE_ID = "com.adobe.air";
    private static Object sAndroidActivityWrapper = null;
    private static Class<?> sAndroidActivityWrapperClass;
    private static DexClassLoader sDloader;
    /* access modifiers changed from: private */
    public static boolean sRuntimeClassesLoaded = false;

    private void BroadcastIntent(String str, String str2) {
        try {
            startActivity(Intent.parseUri(str2, 0).setAction(str).addFlags(268435456));
        } catch (ActivityNotFoundException | URISyntaxException e) {
        }
    }

    /* access modifiers changed from: private */
    public void launchMarketPlaceForAIR() {
        String str;
        try {
            Bundle bundle = getPackageManager().getActivityInfo(getComponentName(), 128).metaData;
            str = bundle != null ? (String) bundle.get("airDownloadURL") : null;
        } catch (PackageManager.NameNotFoundException e) {
            str = null;
        }
        if (str == null) {
            str = "market://details?id=" + RUNTIME_PACKAGE_ID;
        }
        try {
            BroadcastIntent("android.intent.action.VIEW", str);
        } catch (Exception e2) {
        }
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

    private void showDialog(int i, String str, int i2, int i3) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(i);
        builder.setMessage(str);
        builder.setPositiveButton(i2, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                GamePreviewAppEntry.this.launchMarketPlaceForAIR();
                System.exit(0);
            }
        });
        builder.setNegativeButton(i3, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                System.exit(0);
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialogInterface) {
                System.exit(0);
            }
        });
        builder.show();
    }

    private void showRuntimeNotInstalledDialog() {
        ResourceIdMap resourceIdMap = new ResourceIdMap(RESOURCE_CLASS);
        showDialog(resourceIdMap.getId(RESOURCE_TITLE_ADOBE_AIR), getString(resourceIdMap.getId(RESOURCE_TEXT_RUNTIME_REQUIRED)) + getString(resourceIdMap.getId("string.text_install_runtime")), resourceIdMap.getId(RESOURCE_BUTTON_INSTALL), resourceIdMap.getId(RESOURCE_BUTTON_EXIT));
    }

    private void showRuntimeOnExternalStorageDialog() {
        ResourceIdMap resourceIdMap = new ResourceIdMap(RESOURCE_CLASS);
        showDialog(resourceIdMap.getId(RESOURCE_TITLE_ADOBE_AIR), getString(resourceIdMap.getId(RESOURCE_TEXT_RUNTIME_REQUIRED)) + getString(resourceIdMap.getId("string.text_runtime_on_external_storage")), resourceIdMap.getId(RESOURCE_BUTTON_INSTALL), resourceIdMap.getId(RESOURCE_BUTTON_EXIT));
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        boolean loadCaptiveRuntimeClasses = loadCaptiveRuntimeClasses();
        if (!loadCaptiveRuntimeClasses) {
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
            createActivityWrapper(loadCaptiveRuntimeClasses);
            InvokeWrapperOnCreate();
        } else if (loadCaptiveRuntimeClasses) {
            KillSelf();
        } else {
            launchAIRService();
        }
    }

    private void launchAIRService() {
        try {
            Intent intent = new Intent("com.adobe.air.AIRServiceAction");
            intent.setClassName(RUNTIME_PACKAGE_ID, "com.adobe.air.AIRService");
            bindService(intent, new ServiceConnection() {
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    GamePreviewAppEntry.this.unbindService(this);
                    GamePreviewAppEntry.this.loadSharedRuntimeDex();
                    GamePreviewAppEntry.this.createActivityWrapper(false);
                    if (GamePreviewAppEntry.sRuntimeClassesLoaded) {
                        GamePreviewAppEntry.this.InvokeWrapperOnCreate();
                    } else {
                        GamePreviewAppEntry.KillSelf();
                    }
                }

                public void onServiceDisconnected(ComponentName componentName) {
                }
            }, 1);
        } catch (Exception e) {
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v9, resolved type: java.lang.Object[]} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void InvokeWrapperOnCreate() {
        /*
            r10 = this;
            java.lang.Class<?> r0 = sAndroidActivityWrapperClass     // Catch:{ Exception -> 0x006a }
            java.lang.String r1 = "onCreate"
            r2 = 2
            java.lang.Class[] r2 = new java.lang.Class[r2]     // Catch:{ Exception -> 0x006a }
            r3 = 0
            java.lang.Class<android.app.Activity> r4 = android.app.Activity.class
            r2[r3] = r4     // Catch:{ Exception -> 0x006a }
            r3 = 1
            java.lang.Class<java.lang.String[]> r4 = java.lang.String[].class
            r2[r3] = r4     // Catch:{ Exception -> 0x006a }
            java.lang.reflect.Method r0 = r0.getMethod(r1, r2)     // Catch:{ Exception -> 0x006a }
            java.lang.String r1 = "assets/META-INF/AIR/GamePreview-app.xml"
            java.lang.String r2 = "assets"
            java.lang.String r3 = "-nodebug"
            java.lang.Boolean r4 = new java.lang.Boolean     // Catch:{ Exception -> 0x006a }
            r5 = 0
            r4.<init>(r5)     // Catch:{ Exception -> 0x006a }
            java.lang.Boolean r5 = new java.lang.Boolean     // Catch:{ Exception -> 0x006a }
            r6 = 0
            r5.<init>(r6)     // Catch:{ Exception -> 0x006a }
            java.lang.Boolean r6 = new java.lang.Boolean     // Catch:{ Exception -> 0x006a }
            r7 = 1
            r6.<init>(r7)     // Catch:{ Exception -> 0x006a }
            android.content.Intent r7 = r10.getIntent()     // Catch:{ Exception -> 0x006a }
            android.net.Uri r7 = r7.getData()     // Catch:{ Exception -> 0x006a }
            java.lang.String r7 = r7.getHost()     // Catch:{ Exception -> 0x006a }
            r8 = 7
            java.lang.String[] r8 = new java.lang.String[r8]     // Catch:{ Exception -> 0x006a }
            r9 = 0
            r8[r9] = r1     // Catch:{ Exception -> 0x006a }
            r1 = 1
            r8[r1] = r2     // Catch:{ Exception -> 0x006a }
            r1 = 2
            r8[r1] = r3     // Catch:{ Exception -> 0x006a }
            r1 = 3
            java.lang.String r2 = r4.toString()     // Catch:{ Exception -> 0x006a }
            r8[r1] = r2     // Catch:{ Exception -> 0x006a }
            r1 = 4
            java.lang.String r2 = r5.toString()     // Catch:{ Exception -> 0x006a }
            r8[r1] = r2     // Catch:{ Exception -> 0x006a }
            r1 = 5
            java.lang.String r2 = r6.toString()     // Catch:{ Exception -> 0x006a }
            r8[r1] = r2     // Catch:{ Exception -> 0x006a }
            r1 = 6
            r8[r1] = r7     // Catch:{ Exception -> 0x006a }
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ Exception -> 0x006a }
            r2 = 0
            r1[r2] = r10     // Catch:{ Exception -> 0x006a }
            r2 = 1
            r1[r2] = r8     // Catch:{ Exception -> 0x006a }
            r10.InvokeMethod(r0, r1)     // Catch:{ Exception -> 0x006a }
        L_0x0069:
            return
        L_0x006a:
            r0 = move-exception
            goto L_0x0069
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adobe.air.GamePreviewAppEntry.InvokeWrapperOnCreate():void");
    }

    private Object InvokeMethod(Method method, Object... objArr) {
        if (!sRuntimeClassesLoaded) {
            return null;
        }
        if (objArr == null) {
            return method.invoke(sAndroidActivityWrapper, new Object[0]);
        }
        try {
            return method.invoke(sAndroidActivityWrapper, objArr);
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
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onConfigurationChanged", new Class[]{Configuration.class}), configuration);
        } catch (Exception e) {
        }
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("dispatchKeyEvent", new Class[]{KeyEvent.class, Boolean.TYPE}), keyEvent, false);
        } catch (Exception e) {
        }
        if (super.dispatchKeyEvent(keyEvent)) {
            return true;
        }
        return false;
    }

    public boolean dispatchGenericMotionEvent(MotionEvent motionEvent) {
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("dispatchGenericMotionEvent", new Class[]{MotionEvent.class, Boolean.TYPE}), motionEvent, false);
        } catch (Exception e) {
        }
        if (super.dispatchGenericMotionEvent(motionEvent)) {
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
    public void onActivityResult(int i, int i2, Intent intent) {
        try {
            if (sRuntimeClassesLoaded) {
                InvokeMethod(sAndroidActivityWrapperClass.getMethod("onActivityResult", new Class[]{Integer.TYPE, Integer.TYPE, Intent.class}), Integer.valueOf(i), Integer.valueOf(i2), intent);
            }
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onNewIntent", new Class[]{Intent.class}), intent);
        } catch (Exception e) {
        }
    }

    private boolean loadCaptiveRuntimeClasses() {
        try {
            sAndroidActivityWrapperClass = Class.forName("com.adobe.air.AndroidActivityWrapper");
            try {
                if (sAndroidActivityWrapperClass == null) {
                    return true;
                }
                sRuntimeClassesLoaded = true;
                return true;
            } catch (Exception e) {
                return true;
            }
        } catch (Exception e2) {
            return false;
        }
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
    public void createActivityWrapper(boolean z) {
        if (z) {
            try {
                sAndroidActivityWrapper = sAndroidActivityWrapperClass.getMethod("CreateAndroidActivityWrapper", new Class[]{Activity.class, Boolean.class}).invoke((Object) null, new Object[]{this, Boolean.valueOf(z)});
            } catch (Exception e) {
            }
        } else {
            sAndroidActivityWrapper = sAndroidActivityWrapperClass.getMethod("CreateAndroidActivityWrapper", new Class[]{Activity.class}).invoke((Object) null, new Object[]{this});
        }
    }

    public void finishActivityFromChild(Activity activity, int i) {
        super.finishActivityFromChild(activity, i);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("finishActivityFromChild", new Class[]{Activity.class, Integer.TYPE}), activity, Integer.valueOf(i));
        } catch (Exception e) {
        }
    }

    public void finishFromChild(Activity activity) {
        super.finishFromChild(activity);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("finishFromChild", new Class[]{Activity.class}), activity);
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

    public boolean onContextItemSelected(MenuItem menuItem) {
        boolean onContextItemSelected = super.onContextItemSelected(menuItem);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onContextItemSelected", new Class[]{MenuItem.class, Boolean.TYPE}), menuItem, Boolean.valueOf(onContextItemSelected))).booleanValue();
        } catch (Exception e) {
            return onContextItemSelected;
        }
    }

    public void onContextMenuClosed(Menu menu) {
        super.onContextMenuClosed(menu);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onContextMenuClosed", new Class[]{Menu.class}), menu);
        } catch (Exception e) {
        }
    }

    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        super.onCreateContextMenu(contextMenu, view, contextMenuInfo);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onCreateContextMenu", new Class[]{ContextMenu.class, View.class, ContextMenu.ContextMenuInfo.class}), contextMenu, view, contextMenuInfo);
        } catch (Exception e) {
        }
    }

    public CharSequence onCreateDescription() {
        CharSequence onCreateDescription = super.onCreateDescription();
        try {
            return (CharSequence) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onCreateDescription", new Class[]{CharSequence.class}), onCreateDescription);
        } catch (Exception e) {
            return onCreateDescription;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        boolean onCreateOptionsMenu = super.onCreateOptionsMenu(menu);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onCreateOptionsMenu", new Class[]{Menu.class, Boolean.TYPE}), menu, Boolean.valueOf(onCreateOptionsMenu))).booleanValue();
        } catch (Exception e) {
            return onCreateOptionsMenu;
        }
    }

    public boolean onCreatePanelMenu(int i, Menu menu) {
        boolean onCreatePanelMenu = super.onCreatePanelMenu(i, menu);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onCreatePanelMenu", new Class[]{Integer.TYPE, Menu.class, Boolean.TYPE}), Integer.valueOf(i), menu, Boolean.valueOf(onCreatePanelMenu))).booleanValue();
        } catch (Exception e) {
            return onCreatePanelMenu;
        }
    }

    public View onCreatePanelView(int i) {
        View onCreatePanelView = super.onCreatePanelView(i);
        try {
            return (View) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onCreatePanelView", new Class[]{Integer.TYPE, View.class}), Integer.valueOf(i), onCreatePanelView);
        } catch (Exception e) {
            return onCreatePanelView;
        }
    }

    public boolean onCreateThumbnail(Bitmap bitmap, Canvas canvas) {
        boolean onCreateThumbnail = super.onCreateThumbnail(bitmap, canvas);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onCreateThumbnail", new Class[]{Bitmap.class, Canvas.class, Boolean.TYPE}), bitmap, canvas, Boolean.valueOf(onCreateThumbnail))).booleanValue();
        } catch (Exception e) {
            return onCreateThumbnail;
        }
    }

    public View onCreateView(String str, Context context, AttributeSet attributeSet) {
        View onCreateView = super.onCreateView(str, context, attributeSet);
        try {
            return (View) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onCreateView", new Class[]{String.class, Context.class, AttributeSet.class, View.class}), str, context, attributeSet, onCreateView);
        } catch (Exception e) {
            return onCreateView;
        }
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onDetachedFromWindow", new Class[0]), new Object[0]);
        } catch (Exception e) {
        }
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        boolean onKeyDown = super.onKeyDown(i, keyEvent);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onKeyDown", new Class[]{Integer.TYPE, KeyEvent.class, Boolean.TYPE}), Integer.valueOf(i), keyEvent, Boolean.valueOf(onKeyDown))).booleanValue();
        } catch (Exception e) {
            return onKeyDown;
        }
    }

    public boolean onKeyLongPress(int i, KeyEvent keyEvent) {
        boolean onKeyLongPress = super.onKeyLongPress(i, keyEvent);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onKeyLongPress", new Class[]{Integer.TYPE, KeyEvent.class, Boolean.TYPE}), Integer.valueOf(i), keyEvent, Boolean.valueOf(onKeyLongPress))).booleanValue();
        } catch (Exception e) {
            return onKeyLongPress;
        }
    }

    public boolean onKeyMultiple(int i, int i2, KeyEvent keyEvent) {
        boolean onKeyMultiple = super.onKeyMultiple(i, i2, keyEvent);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onKeyMultiple", new Class[]{Integer.TYPE, Integer.TYPE, KeyEvent.class, Boolean.TYPE}), Integer.valueOf(i), Integer.valueOf(i2), keyEvent, Boolean.valueOf(onKeyMultiple))).booleanValue();
        } catch (Exception e) {
            return onKeyMultiple;
        }
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        boolean onKeyUp = super.onKeyUp(i, keyEvent);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onKeyUp", new Class[]{Integer.TYPE, KeyEvent.class, Boolean.TYPE}), Integer.valueOf(i), keyEvent, Boolean.valueOf(onKeyUp))).booleanValue();
        } catch (Exception e) {
            return onKeyUp;
        }
    }

    public boolean onMenuItemSelected(int i, MenuItem menuItem) {
        boolean onMenuItemSelected = super.onMenuItemSelected(i, menuItem);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onMenuItemSelected", new Class[]{Integer.TYPE, MenuItem.class, Boolean.TYPE}), Integer.valueOf(i), menuItem, Boolean.valueOf(onMenuItemSelected))).booleanValue();
        } catch (Exception e) {
            return onMenuItemSelected;
        }
    }

    public boolean onMenuOpened(int i, Menu menu) {
        boolean onMenuOpened = super.onMenuOpened(i, menu);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onMenuOpened", new Class[]{Integer.TYPE, Menu.class, Boolean.TYPE}), Integer.valueOf(i), menu, Boolean.valueOf(onMenuOpened))).booleanValue();
        } catch (Exception e) {
            return onMenuOpened;
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        boolean onOptionsItemSelected = super.onOptionsItemSelected(menuItem);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onOptionsItemSelected", new Class[]{MenuItem.class, Boolean.TYPE}), menuItem, Boolean.valueOf(onOptionsItemSelected))).booleanValue();
        } catch (Exception e) {
            return onOptionsItemSelected;
        }
    }

    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onOptionsMenuClosed", new Class[]{Menu.class}), menu);
        } catch (Exception e) {
        }
    }

    public void onPanelClosed(int i, Menu menu) {
        super.onPanelClosed(i, menu);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onPanelClosed", new Class[]{Integer.TYPE, Menu.class}), Integer.valueOf(i), menu);
        } catch (Exception e) {
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean onPrepareOptionsMenu = super.onPrepareOptionsMenu(menu);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onPrepareOptionsMenu", new Class[]{Menu.class, Boolean.TYPE}), menu, Boolean.valueOf(onPrepareOptionsMenu))).booleanValue();
        } catch (Exception e) {
            return onPrepareOptionsMenu;
        }
    }

    public boolean onPreparePanel(int i, View view, Menu menu) {
        boolean onPreparePanel = super.onPreparePanel(i, view, menu);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onPreparePanel", new Class[]{Integer.TYPE, View.class, Menu.class, Boolean.TYPE}), Integer.valueOf(i), view, menu, Boolean.valueOf(onPreparePanel))).booleanValue();
        } catch (Exception e) {
            return onPreparePanel;
        }
    }

    public Object onRetainNonConfigurationInstance() {
        Object onRetainNonConfigurationInstance = super.onRetainNonConfigurationInstance();
        try {
            return InvokeMethod(sAndroidActivityWrapperClass.getMethod("onRetainNonConfigurationInstance", new Class[]{Object.class}), onRetainNonConfigurationInstance);
        } catch (Exception e) {
            return onRetainNonConfigurationInstance;
        }
    }

    public boolean onSearchRequested() {
        boolean onSearchRequested = super.onSearchRequested();
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onSearchRequested", new Class[]{Boolean.TYPE}), Boolean.valueOf(onSearchRequested))).booleanValue();
        } catch (Exception e) {
            return onSearchRequested;
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean onTouchEvent = super.onTouchEvent(motionEvent);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onTouchEvent", new Class[]{MotionEvent.class, Boolean.TYPE}), motionEvent, Boolean.valueOf(onTouchEvent))).booleanValue();
        } catch (Exception e) {
            return onTouchEvent;
        }
    }

    public boolean onTrackballEvent(MotionEvent motionEvent) {
        boolean onTrackballEvent = super.onTrackballEvent(motionEvent);
        try {
            return ((Boolean) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onTrackballEvent", new Class[]{MotionEvent.class, Boolean.TYPE}), motionEvent, Boolean.valueOf(onTrackballEvent))).booleanValue();
        } catch (Exception e) {
            return onTrackballEvent;
        }
    }

    public void onUserInteraction() {
        super.onUserInteraction();
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onUserInteraction", new Class[0]), new Object[0]);
        } catch (Exception e) {
        }
    }

    public void onWindowAttributesChanged(WindowManager.LayoutParams layoutParams) {
        super.onWindowAttributesChanged(layoutParams);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onWindowAttributesChanged", new Class[]{WindowManager.LayoutParams.class}), layoutParams);
        } catch (Exception e) {
        }
    }

    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onWindowFocusChanged", new Class[]{Boolean.TYPE}), Boolean.valueOf(z));
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: protected */
    public void onApplyThemeResource(Resources.Theme theme, int i, boolean z) {
        super.onApplyThemeResource(theme, i, z);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onApplyThemeResource", new Class[]{Resources.Theme.class, Integer.TYPE, Boolean.TYPE}), theme, Integer.valueOf(i), Boolean.valueOf(z));
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: protected */
    public void onChildTitleChanged(Activity activity, CharSequence charSequence) {
        super.onChildTitleChanged(activity, charSequence);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onChildTitleChanged", new Class[]{Activity.class, CharSequence.class}), activity, charSequence);
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: protected */
    public Dialog onCreateDialog(int i) {
        Dialog onCreateDialog = super.onCreateDialog(i);
        try {
            return (Dialog) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onCreateDialog", new Class[]{Integer.TYPE, Dialog.class}), Integer.valueOf(i), onCreateDialog);
        } catch (Exception e) {
            return onCreateDialog;
        }
    }

    /* access modifiers changed from: protected */
    public Dialog onCreateDialog(int i, Bundle bundle) {
        Dialog onCreateDialog = super.onCreateDialog(i, bundle);
        try {
            return (Dialog) InvokeMethod(sAndroidActivityWrapperClass.getMethod("onCreateDialog", new Class[]{Integer.TYPE, Bundle.class, Dialog.class}), Integer.valueOf(i), bundle, onCreateDialog);
        } catch (Exception e) {
            return onCreateDialog;
        }
    }

    /* access modifiers changed from: protected */
    public void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onPostCreate", new Class[]{Bundle.class}), bundle);
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
    public void onPrepareDialog(int i, Dialog dialog) {
        super.onPrepareDialog(i, dialog);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onPrepareDialog", new Class[]{R.id.class, Dialog.class}), Integer.valueOf(i), dialog);
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: protected */
    public void onPrepareDialog(int i, Dialog dialog, Bundle bundle) {
        super.onPrepareDialog(i, dialog, bundle);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onPrepareDialog", new Class[]{R.id.class, Dialog.class, Bundle.class}), Integer.valueOf(i), dialog, bundle);
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onRestoreInstanceState", new Class[]{Bundle.class}), bundle);
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onSaveInstanceState", new Class[]{Bundle.class}), bundle);
        } catch (Exception e) {
        }
    }

    /* access modifiers changed from: protected */
    public void onTitleChanged(CharSequence charSequence, int i) {
        super.onTitleChanged(charSequence, i);
        try {
            InvokeMethod(sAndroidActivityWrapperClass.getMethod("onTitleChanged", new Class[]{CharSequence.class, Integer.TYPE}), charSequence, Integer.valueOf(i));
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
