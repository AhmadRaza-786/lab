package com.adobe.air;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/* compiled from: AndroidCamera */
class AndroidCameraView extends SurfaceView implements SurfaceHolder.Callback {
    private AndroidActivityWrapper mActivityWrapper = null;
    private boolean mRecreating = false;

    public AndroidCameraView(Context context, AndroidActivityWrapper androidActivityWrapper) {
        super(context);
        this.mActivityWrapper = androidActivityWrapper;
        getHolder().setType(3);
        setZOrderMediaOverlay(false);
        getHolder().addCallback(this);
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.mActivityWrapper.planeStepCascade();
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.mActivityWrapper.planeBreakCascade();
    }
}
