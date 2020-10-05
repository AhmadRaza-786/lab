package com.adobe.air;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.SurfaceHolder;
import java.util.concurrent.Semaphore;

public class AIRWindowThreadedSurfaceView extends AIRWindowSurfaceView {
    public final Semaphore drawSemaphore = new Semaphore(0);
    private DrawThread thread = null;

    public AIRWindowThreadedSurfaceView(Context context, AndroidActivityWrapper androidActivityWrapper) {
        super(context, androidActivityWrapper);
        Init(context);
    }

    /* access modifiers changed from: protected */
    public void Init(Context context) {
        this.thread = new DrawThread(this, this.mSurfaceHolder, context);
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        super.surfaceCreated(surfaceHolder);
        this.thread.setRunning(true);
        if (!this.thread.isAlive()) {
            try {
                this.thread.start();
            } catch (Exception e) {
            }
        }
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        super.surfaceChanged(surfaceHolder, i, i2, i3);
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        super.surfaceDestroyed(surfaceHolder);
        boolean z = true;
        this.thread.setRunning(false);
        while (z && this.thread.isAlive()) {
            try {
                this.thread.join();
                z = false;
            } catch (InterruptedException e) {
            }
        }
    }

    public void drawBitmap(int i, int i2, int i3, int i4, Bitmap bitmap) {
        this.thread.requestDraw(i, i2, i3, i4, bitmap);
        try {
            this.drawSemaphore.acquire();
        } catch (Exception e) {
        }
    }

    public void drawBitmap(int i, int i2, int i3, int i4, Bitmap bitmap, int i5, int i6, int i7, int i8, boolean z, int i9) {
        this.thread.requestDraw(i, i2, i3, i4, bitmap, i5, i6, i7, i8, z, i9);
        try {
            this.drawSemaphore.acquire();
        } catch (Exception e) {
        }
    }
}
