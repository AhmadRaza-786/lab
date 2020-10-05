package com.adobe.flashruntime.air;

import android.graphics.SurfaceTexture;
import android.os.Build;
import android.util.Log;
import android.view.Surface;

public class VideoTextureSurface implements SurfaceTexture.OnFrameAvailableListener {
    private static final String TAG = "VideoSurfaceTexture";
    private boolean mAmCreated = false;
    private long mCPPInstance;
    private boolean mFrameAvailable = false;
    private boolean mPlanePositionSet = false;
    private Surface mSurface = null;
    private int mTextureId;
    private SurfaceTexture mVideoTexture;

    private native void nativeSetJavaTextureSurfaceReady(long j, boolean z);

    public VideoTextureSurface(int i) {
        this.mTextureId = i;
        this.mVideoTexture = new SurfaceTexture(this.mTextureId);
        this.mVideoTexture.setOnFrameAvailableListener(this);
        this.mSurface = new Surface(this.mVideoTexture);
        this.mAmCreated = true;
    }

    public boolean updateSurfaceTextureTexImage() {
        if (!this.mFrameAvailable) {
            return false;
        }
        this.mVideoTexture.updateTexImage();
        this.mFrameAvailable = false;
        return true;
    }

    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        this.mFrameAvailable = true;
        notifyNativeReadyForVideoTexture();
    }

    public void VideoPlaybackRestarted() {
    }

    public void setFPInstance(long j) {
        Log.d(TAG, "Changing FP Instance from " + this.mCPPInstance + " to " + j);
        this.mCPPInstance = j;
    }

    public long getFPInstance() {
        return this.mCPPInstance;
    }

    public boolean useOverlay() {
        if (Build.VERSION.SDK_INT >= 14) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void setNativeInstance(long j) {
    }

    public Surface getSurface() {
        if (!this.mAmCreated || !useOverlay()) {
            return null;
        }
        return this.mSurface;
    }

    public void notifyNativeReadyForVideoTexture() {
        if (this.mCPPInstance != 0) {
            nativeSetJavaTextureSurfaceReady(this.mCPPInstance, this.mAmCreated);
        }
    }
}
