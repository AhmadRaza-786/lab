package com.adobe.air;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import com.adobe.air.ShakeListener;
import com.adobe.air.wand.WandActivity;
import java.lang.Thread;
import java.util.List;

public class ShakeListenerService extends Service {
    private final String AIR_WAND_CLASS_NAME = "com.adobe.air.wand.WandActivity";
    private BackgroundThread backGroundThread = null;
    /* access modifiers changed from: private */
    public ShakeListener mShakeListener;
    /* access modifiers changed from: private */
    public Context mcontext;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        if (this.backGroundThread == null) {
            this.backGroundThread = new BackgroundThread(getApplicationContext());
        }
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        if (this.backGroundThread == null) {
            this.backGroundThread = new BackgroundThread(getApplicationContext());
        }
        if (this.backGroundThread.getState() != Thread.State.NEW && this.backGroundThread.getState() != Thread.State.TERMINATED) {
            return 1;
        }
        if (this.backGroundThread.getState() == Thread.State.TERMINATED) {
            this.backGroundThread = new BackgroundThread(getApplicationContext());
        }
        this.backGroundThread.start();
        return 1;
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.backGroundThread != null) {
            try {
                this.backGroundThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.backGroundThread = null;
        }
    }

    class BackgroundThread extends Thread {
        public BackgroundThread(Context context) {
            Context unused = ShakeListenerService.this.mcontext = context;
        }

        public void run() {
            try {
                ShakeListener unused = ShakeListenerService.this.mShakeListener = new ShakeListener(ShakeListenerService.this.mcontext);
                ShakeListenerService.this.mShakeListener.registerListener(new ShakeListener.Listener() {
                    public void onShake() {
                        List<ActivityManager.RunningTaskInfo> runningTasks = ((ActivityManager) ShakeListenerService.this.getApplicationContext().getSystemService("activity")).getRunningTasks(1);
                        if (!runningTasks.isEmpty()) {
                            ComponentName componentName = runningTasks.get(0).topActivity;
                            if (componentName.getPackageName().equals(ShakeListenerService.this.getApplicationContext().getPackageName()) && !componentName.getClassName().equalsIgnoreCase("com.adobe.air.wand.WandActivity")) {
                                Intent intent = new Intent(ShakeListenerService.this.getApplicationContext(), WandActivity.class);
                                intent.setFlags(272629760);
                                ShakeListenerService.this.startActivity(intent);
                            }
                        }
                    }
                });
            } catch (Exception e) {
            }
        }
    }
}
