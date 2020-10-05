package com.adobe.air.wand.motionsensor;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public abstract class MotionSensor {
    protected static final String LOG_TAG = "MotionSensor";
    protected Activity mActivity = null;
    protected boolean mRegistered = false;
    protected Sensor mSensor = null;
    protected int mSensorDelay = 1;
    protected SensorEventListener mSensorEventListener = null;
    protected SensorManager mSensorManager = null;

    public interface Listener {
        void onSensorChanged(float[] fArr, long j);
    }

    public MotionSensor(Activity activity, int i) {
        this.mActivity = activity;
        this.mSensorManager = (SensorManager) this.mActivity.getSystemService("sensor");
        this.mSensor = this.mSensorManager.getDefaultSensor(i);
        if (this.mSensor != null) {
        }
    }

    /* access modifiers changed from: private */
    public float[] getScreenOrientedValues(float[] fArr) {
        float[] fArr2 = (float[]) fArr.clone();
        int rotation = this.mActivity.getWindowManager().getDefaultDisplay().getRotation();
        if (rotation != 0) {
            if (rotation == 2) {
                fArr2[0] = -fArr2[0];
                fArr2[1] = -fArr2[1];
            } else if (rotation == 1) {
                float f = fArr2[0];
                fArr2[0] = -fArr2[1];
                fArr2[1] = f;
            } else if (rotation == 3) {
                fArr2[0] = fArr2[1];
                fArr2[1] = -fArr2[0];
            }
        }
        return fArr2;
    }

    public void setListener(final Listener listener) {
        AnonymousClass1 r0;
        boolean z = this.mRegistered;
        stop();
        if (listener == null) {
            r0 = null;
        } else {
            r0 = new SensorEventListener() {
                public void onSensorChanged(SensorEvent sensorEvent) {
                    listener.onSensorChanged(MotionSensor.this.getScreenOrientedValues(sensorEvent.values), sensorEvent.timestamp);
                }

                public void onAccuracyChanged(Sensor sensor, int i) {
                }
            };
        }
        this.mSensorEventListener = r0;
        if (z) {
            start();
        }
    }

    public void start(int i) {
        if (!this.mRegistered && this.mSensor != null) {
            this.mSensorDelay = i * 1000;
            start();
        }
    }

    public void start() {
        if (!this.mRegistered && this.mSensor != null) {
            this.mRegistered = this.mSensorManager.registerListener(this.mSensorEventListener, this.mSensor, this.mSensorDelay);
            if (this.mRegistered) {
            }
        }
    }

    public void stop() {
        if (this.mRegistered) {
            this.mSensorManager.unregisterListener(this.mSensorEventListener, this.mSensor);
            this.mRegistered = false;
        }
    }

    public boolean available() {
        return this.mSensor != null;
    }

    public boolean active() {
        return this.mRegistered;
    }

    public void dispose() {
        stop();
        setListener((Listener) null);
        this.mSensor = null;
        this.mSensorManager = null;
        this.mActivity = null;
    }
}
