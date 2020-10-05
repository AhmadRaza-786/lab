package com.adobe.flashruntime.shared;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

public class VideoView extends SurfaceView {
    private static final String TAG = "VideoSurfaceView";
    /* access modifiers changed from: private */
    public boolean mAmCreated = false;
    private long mCPPInstance;
    private Context mContext;
    /* access modifiers changed from: private */
    public boolean mPlanePositionSet = false;
    /* access modifiers changed from: private */
    public Surface mSurface = null;
    /* access modifiers changed from: private */
    public int mXmax = 16;
    /* access modifiers changed from: private */
    public int mXmin = 0;
    /* access modifiers changed from: private */
    public int mYmax = 16;
    /* access modifiers changed from: private */
    public int mYmin = 0;

    private native void nativeSetJavaViewReady(long j, boolean z);

    public VideoView(Context context) {
        super(context);
        this.mContext = context;
        setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
        if (useOverlay()) {
            getHolder().setFormat(842094169);
        }
        getHolder().addCallback(new SurfaceHolder.Callback() {
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
                Log.v(VideoView.TAG, "surfaceChanged format=" + i + ", width=" + i2 + ", height=" + i3);
                if (VideoView.this.useOverlay() && VideoView.this.mPlanePositionSet) {
                    if (i2 != VideoView.this.mXmax - VideoView.this.mXmin || i3 != VideoView.this.mYmax - VideoView.this.mYmin) {
                        VideoView.this.setPlanePosition(VideoView.this.mXmin, VideoView.this.mYmin, VideoView.this.mXmax, VideoView.this.mYmax);
                    }
                }
            }

            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                Log.v(VideoView.TAG, "surfaceCreated");
                Surface unused = VideoView.this.mSurface = surfaceHolder.getSurface();
                boolean unused2 = VideoView.this.mAmCreated = true;
                VideoView.this.notifyNativeReadyForVideo();
            }

            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                Log.v(VideoView.TAG, "surfaceDestroyed");
                VideoView.this.mSurface.release();
                boolean unused = VideoView.this.mAmCreated = false;
                VideoView.this.notifyNativeReadyForVideo();
            }
        });
    }

    public void VideoPlaybackRestarted() {
    }

    /* access modifiers changed from: package-private */
    public void setNativeInstance(long j) {
    }

    public void setFPInstance(long j) {
        Log.d(TAG, "Changing FP Instance from " + this.mCPPInstance + " to " + j);
        this.mCPPInstance = j;
        notifyNativeReadyForVideo();
    }

    public long getFPInstance() {
        return this.mCPPInstance;
    }

    public void setPlanePosition(int i, int i2, int i3, int i4) {
        this.mXmin = i;
        this.mYmin = i2;
        this.mXmax = i3;
        this.mYmax = i4;
        this.mPlanePositionSet = true;
        getHandler().post(new Runnable() {
            public void run() {
                VideoView.this.layout(VideoView.this.mXmin, VideoView.this.mYmin, VideoView.this.mXmax, VideoView.this.mYmax);
            }
        });
    }

    /* access modifiers changed from: protected */
    public boolean useOverlay() {
        if (Build.VERSION.SDK_INT >= 14) {
            return true;
        }
        return false;
    }

    public Surface getSurface() {
        if (!this.mAmCreated || !useOverlay()) {
            return null;
        }
        return this.mSurface;
    }

    public void notifyNativeReadyForVideo() {
        if (this.mCPPInstance != 0) {
            nativeSetJavaViewReady(this.mCPPInstance, this.mAmCreated);
        }
    }
}
