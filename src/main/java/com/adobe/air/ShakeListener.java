package com.adobe.air;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeListener implements SensorEventListener {
    private static final int FORCE_THRESHOLD = 1200;
    private static final int SHAKE_COUNT = 2;
    private static final int SHAKE_DURATION = 1000;
    private static final int SHAKE_TIMEOUT = 500;
    private static final int TIME_THRESHOLD = 100;
    private Sensor mAccelerometer = null;
    private Context mContext = null;
    private long mLastForce = 0;
    private long mLastShake = 0;
    private long mLastTime = 0;
    private float mLastX = -1.0f;
    private float mLastY = -1.0f;
    private float mLastZ = -1.0f;
    private Listener mListener = null;
    private SensorManager mSensorMgr = null;
    private int mShakeCount = 0;

    public interface Listener {
        void onShake();
    }

    public ShakeListener(Context context) throws Exception {
        if (context == null) {
            throw new Exception("Invalid context");
        }
        this.mContext = context;
        resume();
    }

    public void registerListener(Listener listener) {
        this.mListener = listener;
    }

    public void unregisterListener() {
        this.mListener = null;
    }

    public void resume() throws Exception {
        if (this.mSensorMgr == null) {
            this.mAccelerometer = null;
            this.mSensorMgr = (SensorManager) this.mContext.getSystemService("sensor");
            if (this.mSensorMgr == null) {
                throw new UnsupportedOperationException("Sensors not supported");
            }
            this.mAccelerometer = this.mSensorMgr.getDefaultSensor(1);
            if (this.mAccelerometer == null) {
                this.mSensorMgr = null;
                throw new UnsupportedOperationException("Accelerometer is not supported");
            } else if (!this.mSensorMgr.registerListener(this, this.mAccelerometer, 2)) {
                this.mSensorMgr = null;
                this.mAccelerometer = null;
                throw new UnsupportedOperationException("Accelerometer is not supported");
            }
        }
    }

    public void pause() {
        if (this.mSensorMgr != null && this.mAccelerometer != null) {
            this.mSensorMgr.unregisterListener(this, this.mAccelerometer);
            this.mSensorMgr = null;
            this.mAccelerometer = null;
        }
    }

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.mLastForce > 500) {
            this.mShakeCount = 0;
        }
        if (currentTimeMillis - this.mLastTime > 100) {
            if (((float) ((Math.sqrt((double) ((((sensorEvent.values[0] - this.mLastX) * (sensorEvent.values[0] - this.mLastX)) + ((sensorEvent.values[1] - this.mLastY) * (sensorEvent.values[1] - this.mLastY))) + ((sensorEvent.values[2] - this.mLastZ) * (sensorEvent.values[2] - this.mLastZ)))) / ((double) (currentTimeMillis - this.mLastTime))) * 10000.0d)) > 1200.0f) {
                int i = this.mShakeCount + 1;
                this.mShakeCount = i;
                if (i >= 2 && currentTimeMillis - this.mLastShake > 1000) {
                    this.mLastShake = currentTimeMillis;
                    this.mShakeCount = 0;
                    if (this.mListener != null) {
                        this.mListener.onShake();
                    }
                }
                this.mLastForce = currentTimeMillis;
            }
            this.mLastTime = currentTimeMillis;
            this.mLastX = sensorEvent.values[0];
            this.mLastY = sensorEvent.values[1];
            this.mLastZ = sensorEvent.values[2];
        }
    }
}
