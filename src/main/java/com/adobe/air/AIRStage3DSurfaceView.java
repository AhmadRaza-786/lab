package com.adobe.air;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

public class AIRStage3DSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "com.adobe.air";
    private AndroidActivityWrapper mActivityWrapper = null;
    /* access modifiers changed from: private */
    public long mFPInstance = 0;
    private boolean mInit = false;
    private boolean mSurfaceValid = false;
    private int mXmax = 16;
    private int mXmin = 0;
    private int mYmax = 16;
    private int mYmin = 0;
    private ViewTreeObserver.OnGlobalLayoutListener m_layOutListener = null;

    private native void nativeSurfaceChanged(long j, int i, int i2);

    private native void nativeSurfaceCreated(long j);

    /* access modifiers changed from: private */
    public native void nativeSurfaceLayoutChanged(long j);

    private native void nativeSurfaceLost(long j);

    public AIRStage3DSurfaceView(Context context, AndroidActivityWrapper androidActivityWrapper, long j) {
        super(context);
        setFPInstance(context, androidActivityWrapper, j);
        setWillNotDraw(false);
        setEnabled(true);
        setClickable(false);
        setFocusable(false);
        setFocusableInTouchMode(false);
        setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        if (AndroidActivityWrapper.isGingerbread()) {
            getHolder().setFormat(2);
        } else {
            getHolder().setFormat(1);
        }
        getHolder().addCallback(this);
        setZOrderOnTop(false);
        this.mActivityWrapper.registerPlane(this, 6);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(this.mXmax - this.mXmin, this.mYmax - this.mYmin);
    }

    public void setPlanePosition(int i, int i2, int i3, int i4) {
        this.mXmin = i;
        this.mYmin = i2;
        this.mXmax = i3;
        this.mYmax = i4;
        layout(i, i2, i3, i4);
    }

    public boolean setFPInstance(Context context, AndroidActivityWrapper androidActivityWrapper, long j) {
        this.mActivityWrapper = androidActivityWrapper;
        this.mFPInstance = j;
        if (this.mFPInstance != 0) {
            this.mActivityWrapper.registerPlane(this, 6);
        } else {
            this.mActivityWrapper.unregisterPlane(6);
        }
        return this.mSurfaceValid;
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        if (this.mFPInstance != 0) {
            nativeSurfaceChanged(this.mFPInstance, i2, i3);
        }
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.mSurfaceValid = true;
        if (this.mFPInstance != 0) {
            nativeSurfaceCreated(this.mFPInstance);
        }
        this.mActivityWrapper.planeStepCascade();
        ViewTreeObserver viewTreeObserver = getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            if (this.m_layOutListener == null) {
                this.m_layOutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (AIRStage3DSurfaceView.this.mFPInstance != 0) {
                            AIRStage3DSurfaceView.this.nativeSurfaceLayoutChanged(AIRStage3DSurfaceView.this.mFPInstance);
                        }
                    }
                };
            }
            if (this.m_layOutListener != null) {
                viewTreeObserver.addOnGlobalLayoutListener(this.m_layOutListener);
            }
        }
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.mSurfaceValid = false;
        if (this.mFPInstance != 0) {
            nativeSurfaceLost(this.mFPInstance);
        }
        this.mActivityWrapper.planeBreakCascade();
        ViewTreeObserver viewTreeObserver = getViewTreeObserver();
        if (viewTreeObserver.isAlive() && this.m_layOutListener != null) {
            viewTreeObserver.removeGlobalOnLayoutListener(this.m_layOutListener);
        }
    }
}
