package com.adobe.air.wand;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Vibrator;
import android.view.Display;
import android.view.WindowManager;
import com.adobe.air.wand.TaskManager;
import com.adobe.air.wand.connection.Connection;
import com.adobe.air.wand.message.MessageManager;
import com.adobe.air.wand.message.json.JSONMessageManager;
import com.adobe.air.wand.motionsensor.Accelerometer;
import com.adobe.air.wand.motionsensor.Gyroscope;
import com.adobe.air.wand.motionsensor.Magnetometer;
import com.adobe.air.wand.view.TouchSensor;
import com.adobe.air.wand.view.WandView;
import com.distriqt.extension.inappbilling.BuildConfig;

public class WandManager {
    private static final String LOG_TAG = "WandManager";
    private Accelerometer mAccelerometer = null;
    private Display mDisplay = null;
    private Gyroscope mGyroscope = null;
    private boolean mHasFocus = false;
    private boolean mIsDisposed = false;
    /* access modifiers changed from: private */
    public boolean mListenToConfigChange = false;
    private Magnetometer mMagnetometer = null;
    private MessageManager mMessageManager = null;
    /* access modifiers changed from: private */
    public String mProtocolVersion = null;
    /* access modifiers changed from: private */
    public TaskManager mTaskManager = null;
    private TouchSensor mTouchSensor = null;
    private Vibrator mVibrator = null;
    /* access modifiers changed from: private */
    public Connection mWandConnection = null;
    /* access modifiers changed from: private */
    public WandView mWandView = null;

    private class ConnectionListener implements Connection.Listener {
        private ConnectionListener() {
        }

        public void onConnectionOpen(String str) {
            try {
                String unused = WandManager.this.mProtocolVersion = str;
                WandManager.this.mWandView.setScreenOrientation(WandView.ScreenOrientation.LANDSCAPE);
                WandManager.this.mWandView.loadCompanionView();
            } catch (Throwable th) {
            }
        }

        public void onConnectionClose() {
            try {
                String unused = WandManager.this.mProtocolVersion = null;
                WandManager.this.loadDefaultView();
            } catch (Throwable th) {
            }
        }

        public void onConnectSuccess() {
        }

        public void onConnectError() {
        }

        public void onDisconnectSuccess() {
        }

        public void onDisconnectError() {
        }

        public void onReceive(String str) {
            WandManager.this.mTaskManager.handleRemoteMessage(str);
        }

        public void updateConnectionToken(String str) {
            WandManager.this.mWandView.updateConnectionToken(str);
        }
    }

    private class ViewListener implements WandView.Listener {
        private ViewListener() {
        }

        public String getConnectionToken() {
            try {
                return WandManager.this.mWandConnection.getConnectionToken();
            } catch (Exception e) {
                return BuildConfig.FLAVOR;
            }
        }

        public void onLoadCompanion(Configuration configuration) throws Exception {
            if (configuration.orientation == 2) {
                WandManager.this.mWandConnection.onReadyForConnection();
            } else {
                boolean unused = WandManager.this.mListenToConfigChange = true;
            }
        }
    }

    private class TaskListener implements TaskManager.Listener {
        private TaskListener() {
        }

        public void sendConnectionMessage(String str) throws Exception {
            WandManager.this.mWandConnection.send(str);
        }

        public void setScreenOrientation(WandView.ScreenOrientation screenOrientation) throws Exception {
            WandManager.this.mWandView.setScreenOrientation(screenOrientation);
        }

        public void drawImage(Bitmap bitmap) throws Exception {
            WandManager.this.mWandView.drawImage(bitmap);
        }

        public String getRequestedProtocolVerison() throws Exception {
            if (WandManager.this.mProtocolVersion != null) {
                return WandManager.this.mProtocolVersion;
            }
            throw new Exception("No active connection.");
        }
    }

    public WandManager(Activity activity, WandView wandView, Connection connection) throws Exception {
        ConnectionChangeReceiver.resisterWandConnection(connection);
        this.mWandConnection = connection;
        this.mWandConnection.registerListener(new ConnectionListener());
        this.mWandView = wandView;
        this.mWandView.registerListener(new ViewListener());
        this.mMessageManager = new JSONMessageManager();
        this.mTouchSensor = wandView.getTouchSensor();
        this.mAccelerometer = new Accelerometer(activity);
        this.mMagnetometer = new Magnetometer(activity);
        this.mGyroscope = new Gyroscope(activity);
        if (activity == null) {
            throw new Exception("Cannot find application context while initializing.");
        }
        this.mVibrator = (Vibrator) activity.getSystemService("vibrator");
        this.mDisplay = ((WindowManager) activity.getSystemService("window")).getDefaultDisplay();
        this.mTaskManager = new TaskManager(this.mMessageManager, this.mDisplay, this.mTouchSensor, this.mAccelerometer, this.mMagnetometer, this.mGyroscope, this.mVibrator);
        this.mTaskManager.registerListener(new TaskListener());
    }

    public void focus(boolean z) throws Exception {
        if (this.mIsDisposed) {
            throw new Exception("WandManager has been disposed");
        }
        if (z) {
            setDefaultOrientation();
            this.mWandConnection.connect();
        } else {
            loadDefaultView();
            this.mWandConnection.disconnect();
        }
        this.mHasFocus = z;
    }

    public boolean hasFocus() {
        return this.mHasFocus;
    }

    public void onConfigurationChanged(Configuration configuration) throws Exception {
        if (this.mListenToConfigChange && configuration.orientation == 2) {
            this.mListenToConfigChange = false;
            this.mWandConnection.onReadyForConnection();
        }
    }

    /* access modifiers changed from: private */
    public void loadDefaultView() throws Exception {
        try {
            this.mAccelerometer.stop();
            this.mTaskManager.terminateRunningTasks();
            setDefaultOrientation();
            this.mWandView.loadDefaultView();
        } catch (Exception e) {
            throw e;
        }
    }

    private void setDefaultOrientation() throws Exception {
        this.mWandView.setScreenOrientation(WandView.ScreenOrientation.INHERIT);
    }

    public void dispose() {
        this.mIsDisposed = true;
        this.mTaskManager.terminateRunningTasks();
        this.mTaskManager.unregisterListener();
        this.mTaskManager = null;
        this.mWandView.unregisterListener();
        this.mWandView = null;
        this.mAccelerometer.dispose();
        this.mAccelerometer = null;
        this.mDisplay = null;
        this.mMessageManager = null;
        this.mTouchSensor = null;
        this.mVibrator = null;
        this.mWandConnection.unregisterListener();
        this.mWandConnection = null;
        ConnectionChangeReceiver.unresisterWandConnection();
    }
}
