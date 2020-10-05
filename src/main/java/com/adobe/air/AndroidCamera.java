package com.adobe.air;

import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import com.adobe.air.AndroidActivityWrapper;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class AndroidCamera {
    private static final int CAMERA_POSITION_UNKNOWN = -1;
    private static final String LOG_TAG = "AndroidCamera";
    private static boolean sAreMultipleCamerasSupportedInitialized = false;
    private static boolean sAreMultipleCamerasSupportedOnDevice = false;
    private static Class<?> sCameraInfoClass = null;
    private static Method sMIDGetCameraInfo = null;
    private static Method sMIDGetNumberOfCameras = null;
    private static Method sMIDOpen = null;
    private static Method sMIDOpenWithCameraID = null;
    private AndroidActivityWrapper.StateChangeCallback mActivityStateCB = null;
    /* access modifiers changed from: private */
    public byte[] mBuffer1 = null;
    /* access modifiers changed from: private */
    public byte[] mBuffer2 = null;
    /* access modifiers changed from: private */
    public byte[] mCallbackBuffer = null;
    /* access modifiers changed from: private */
    public boolean mCallbacksRegistered = false;
    /* access modifiers changed from: private */
    public Camera mCamera = null;
    private int mCameraId = 0;
    private boolean mCapturing = false;
    /* access modifiers changed from: private */
    public long mClientId = 0;
    private boolean mInitialized = false;
    /* access modifiers changed from: private */
    public boolean mPreviewSurfaceValid = true;

    /* access modifiers changed from: private */
    public native void nativeOnCanOpenCamera(long j);

    /* access modifiers changed from: private */
    public native void nativeOnFrameCaptured(long j, byte[] bArr);

    /* access modifiers changed from: private */
    public native void nativeOnShouldCloseCamera(long j);

    public AndroidCamera(long j) {
        this.mClientId = j;
        areMultipleCamerasSupportedOnDevice();
    }

    public static boolean areMultipleCamerasSupportedOnDevice() {
        if (sAreMultipleCamerasSupportedInitialized) {
            return sAreMultipleCamerasSupportedOnDevice;
        }
        sAreMultipleCamerasSupportedInitialized = true;
        Class<Camera> cls = Camera.class;
        try {
            sMIDOpenWithCameraID = cls.getMethod("open", new Class[]{Integer.TYPE});
            sMIDGetNumberOfCameras = Camera.class.getDeclaredMethod("getNumberOfCameras", (Class[]) null);
            try {
                sCameraInfoClass = Class.forName("android.hardware.Camera$CameraInfo");
                sMIDGetCameraInfo = Camera.class.getMethod("getCameraInfo", new Class[]{Integer.TYPE, sCameraInfoClass});
                if (!(sMIDOpenWithCameraID == null || sMIDGetNumberOfCameras == null || sMIDGetCameraInfo == null)) {
                    sAreMultipleCamerasSupportedOnDevice = true;
                }
                return sAreMultipleCamerasSupportedOnDevice;
            } catch (Exception e) {
                return false;
            }
        } catch (NoSuchMethodException e2) {
            return false;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x0041  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0067  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean open(int r10) {
        /*
            r9 = this;
            r4 = 0
            r2 = 1
            r3 = 0
            android.hardware.Camera r0 = r9.mCamera
            if (r0 == 0) goto L_0x0008
        L_0x0007:
            return r2
        L_0x0008:
            com.adobe.air.AndroidActivityWrapper r0 = com.adobe.air.AndroidActivityWrapper.GetAndroidActivityWrapper()     // Catch:{ Exception -> 0x0064 }
            com.adobe.air.AndroidCameraView r0 = r0.getCameraView()     // Catch:{ Exception -> 0x0064 }
            android.view.SurfaceHolder r1 = r0.getHolder()     // Catch:{ Exception -> 0x0064 }
            if (r1 == 0) goto L_0x003c
            android.view.Surface r0 = r1.getSurface()     // Catch:{ Exception -> 0x0056 }
            if (r0 == 0) goto L_0x003c
            boolean r0 = sAreMultipleCamerasSupportedOnDevice     // Catch:{ Exception -> 0x0056 }
            if (r0 == 0) goto L_0x004c
            java.lang.reflect.Method r0 = sMIDOpenWithCameraID     // Catch:{ Exception -> 0x0056 }
            r5 = 0
            r6 = 1
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0056 }
            r7 = 0
            java.lang.Integer r8 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x0056 }
            r6[r7] = r8     // Catch:{ Exception -> 0x0056 }
            java.lang.Object r0 = r0.invoke(r5, r6)     // Catch:{ Exception -> 0x0056 }
            android.hardware.Camera r0 = (android.hardware.Camera) r0     // Catch:{ Exception -> 0x0056 }
            r9.mCamera = r0     // Catch:{ Exception -> 0x0056 }
            r9.mCameraId = r10     // Catch:{ Exception -> 0x0056 }
        L_0x0037:
            android.hardware.Camera r0 = r9.mCamera     // Catch:{ Exception -> 0x0056 }
            r0.setPreviewDisplay(r1)     // Catch:{ Exception -> 0x0056 }
        L_0x003c:
            r0 = r1
        L_0x003d:
            android.hardware.Camera r1 = r9.mCamera
            if (r1 == 0) goto L_0x0067
            com.adobe.air.AndroidCamera$PreviewSurfaceCallback r1 = new com.adobe.air.AndroidCamera$PreviewSurfaceCallback
            r1.<init>()
            r0.addCallback(r1)
            r0 = r2
        L_0x004a:
            r2 = r0
            goto L_0x0007
        L_0x004c:
            android.hardware.Camera r0 = android.hardware.Camera.open()     // Catch:{ Exception -> 0x0056 }
            r9.mCamera = r0     // Catch:{ Exception -> 0x0056 }
            r0 = 0
            r9.mCameraId = r0     // Catch:{ Exception -> 0x0056 }
            goto L_0x0037
        L_0x0056:
            r0 = move-exception
            r0 = r1
        L_0x0058:
            android.hardware.Camera r1 = r9.mCamera
            if (r1 == 0) goto L_0x003d
            android.hardware.Camera r1 = r9.mCamera
            r1.release()
            r9.mCamera = r4
            goto L_0x003d
        L_0x0064:
            r0 = move-exception
            r0 = r4
            goto L_0x0058
        L_0x0067:
            r0 = r3
            goto L_0x004a
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adobe.air.AndroidCamera.open(int):boolean");
    }

    public Camera getCamera() {
        return this.mCamera;
    }

    public int[] getSupportedFps() {
        try {
            List<Integer> supportedPreviewFrameRates = this.mCamera.getParameters().getSupportedPreviewFrameRates();
            int[] iArr = new int[supportedPreviewFrameRates.size()];
            int i = 0;
            for (Integer intValue : supportedPreviewFrameRates) {
                int i2 = i + 1;
                iArr[i] = intValue.intValue();
                i = i2;
            }
            return iArr;
        } catch (Exception e) {
            return new int[0];
        }
    }

    public int getCameraPosition() {
        Object obj;
        Field field = null;
        if (!sAreMultipleCamerasSupportedOnDevice) {
            return CAMERA_POSITION_UNKNOWN;
        }
        if (sCameraInfoClass != null) {
            try {
                obj = sCameraInfoClass.newInstance();
            } catch (Exception e) {
                return CAMERA_POSITION_UNKNOWN;
            }
        } else {
            obj = null;
        }
        try {
            sMIDGetCameraInfo.invoke(this.mCamera, new Object[]{Integer.valueOf(this.mCameraId), obj});
            if (obj != null) {
                try {
                    field = obj.getClass().getField("facing");
                } catch (Exception e2) {
                    return CAMERA_POSITION_UNKNOWN;
                }
            }
            try {
                return field.getInt(obj);
            } catch (Exception e3) {
                return CAMERA_POSITION_UNKNOWN;
            }
        } catch (Exception e4) {
            return CAMERA_POSITION_UNKNOWN;
        }
    }

    public static int getNumberOfCameras() {
        if (areMultipleCamerasSupportedOnDevice()) {
            try {
                return ((Integer) sMIDGetNumberOfCameras.invoke((Object) null, (Object[]) null)).intValue();
            } catch (Exception e) {
            }
        }
        return 1;
    }

    public int[] getSupportedFormats() {
        try {
            List<Integer> supportedPreviewFormats = this.mCamera.getParameters().getSupportedPreviewFormats();
            int[] iArr = new int[supportedPreviewFormats.size()];
            int i = 0;
            for (Integer intValue : supportedPreviewFormats) {
                int i2 = i + 1;
                iArr[i] = intValue.intValue();
                i = i2;
            }
            return iArr;
        } catch (Exception e) {
            return new int[0];
        }
    }

    public int[] getSupportedVideoSizes() {
        try {
            List<Camera.Size> supportedPreviewSizes = this.mCamera.getParameters().getSupportedPreviewSizes();
            int[] iArr = new int[(supportedPreviewSizes.size() * 2)];
            int i = 0;
            for (Camera.Size next : supportedPreviewSizes) {
                int i2 = i + 1;
                iArr[i] = next.width;
                i = i2 + 1;
                iArr[i2] = next.height;
            }
            return iArr;
        } catch (Exception e) {
            return new int[0];
        }
    }

    public int getCaptureWidth() {
        try {
            return this.mCamera.getParameters().getPreviewSize().width;
        } catch (Exception e) {
            return 0;
        }
    }

    public int getCaptureHeight() {
        try {
            return this.mCamera.getParameters().getPreviewSize().height;
        } catch (Exception e) {
            return 0;
        }
    }

    public int getCaptureFormat() {
        try {
            return this.mCamera.getParameters().getPreviewFormat();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean setContinuousFocusMode() {
        boolean z = true;
        if (this.mCamera == null) {
            return false;
        }
        try {
            Camera.Parameters parameters = this.mCamera.getParameters();
            List<String> supportedFocusModes = parameters.getSupportedFocusModes();
            if (supportedFocusModes.contains("continuous-video")) {
                parameters.setFocusMode("continuous-video");
                this.mCamera.setParameters(parameters);
            } else if (supportedFocusModes.contains("edof")) {
                parameters.setFocusMode("edof");
                this.mCamera.setParameters(parameters);
            } else {
                z = false;
            }
        } catch (Exception e) {
            z = false;
        }
        return z;
    }

    public boolean autoFocus() {
        if (this.mCamera == null || !this.mCapturing) {
            return false;
        }
        try {
            String focusMode = this.mCamera.getParameters().getFocusMode();
            if (focusMode == "fixed" || focusMode == "infinity" || focusMode == "continuous-video") {
                return false;
            }
            this.mCamera.autoFocus((Camera.AutoFocusCallback) null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean startCapture(int i, int i2, int i3, int i4) {
        if (this.mCamera == null) {
            return false;
        }
        try {
            Camera.Parameters parameters = this.mCamera.getParameters();
            parameters.setPreviewSize(i, i2);
            parameters.setPreviewFrameRate(i3);
            parameters.setPreviewFormat(i4);
            this.mCamera.setParameters(parameters);
            this.mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
                public void onPreviewFrame(byte[] bArr, Camera camera) {
                    try {
                        if (AndroidCamera.this.mClientId != 0 && AndroidCamera.this.mCallbacksRegistered) {
                            AndroidCamera.this.nativeOnFrameCaptured(AndroidCamera.this.mClientId, bArr);
                        }
                        if (AndroidCamera.this.mCallbackBuffer == AndroidCamera.this.mBuffer1) {
                            byte[] unused = AndroidCamera.this.mCallbackBuffer = AndroidCamera.this.mBuffer2;
                        } else {
                            byte[] unused2 = AndroidCamera.this.mCallbackBuffer = AndroidCamera.this.mBuffer1;
                        }
                        AndroidCamera.this.mCamera.addCallbackBuffer(AndroidCamera.this.mCallbackBuffer);
                    } catch (Exception e) {
                    }
                }
            });
            this.mCamera.startPreview();
            Camera.Parameters parameters2 = this.mCamera.getParameters();
            int bitsPerPixel = ImageFormat.getBitsPerPixel(parameters2.getPreviewFormat()) * parameters2.getPreviewSize().width * parameters2.getPreviewSize().height;
            this.mBuffer1 = new byte[bitsPerPixel];
            this.mBuffer2 = new byte[bitsPerPixel];
            this.mCallbackBuffer = this.mBuffer1;
            this.mCamera.addCallbackBuffer(this.mCallbackBuffer);
            try {
                this.mCapturing = true;
                return true;
            } catch (Exception e) {
                return true;
            }
        } catch (Exception e2) {
            return false;
        }
    }

    public void stopCapture() {
        if (this.mCamera != null) {
            this.mCamera.setPreviewCallback((Camera.PreviewCallback) null);
            this.mCamera.stopPreview();
            this.mCallbackBuffer = null;
            this.mBuffer1 = null;
            this.mBuffer2 = null;
        }
        this.mCapturing = false;
    }

    public void close() {
        if (this.mCamera != null) {
            stopCapture();
            this.mCamera.release();
            this.mCamera = null;
        }
    }

    public void registerCallbacks(boolean z) {
        this.mCallbacksRegistered = z;
        if (z) {
            if (this.mActivityStateCB == null) {
                this.mActivityStateCB = new AndroidActivityWrapper.StateChangeCallback() {
                    public void onActivityStateChanged(AndroidActivityWrapper.ActivityState activityState) {
                        if (AndroidCamera.this.mClientId != 0 && AndroidCamera.this.mCallbacksRegistered) {
                            if (activityState == AndroidActivityWrapper.ActivityState.RESUMED && AndroidCamera.this.mPreviewSurfaceValid) {
                                AndroidCamera.this.nativeOnCanOpenCamera(AndroidCamera.this.mClientId);
                            } else if (activityState == AndroidActivityWrapper.ActivityState.PAUSED) {
                                AndroidCamera.this.nativeOnShouldCloseCamera(AndroidCamera.this.mClientId);
                            }
                        }
                    }

                    public void onConfigurationChanged(Configuration configuration) {
                    }
                };
            }
            AndroidActivityWrapper.GetAndroidActivityWrapper().addActivityStateChangeListner(this.mActivityStateCB);
            return;
        }
        if (this.mActivityStateCB != null) {
            AndroidActivityWrapper.GetAndroidActivityWrapper().removeActivityStateChangeListner(this.mActivityStateCB);
        }
        this.mActivityStateCB = null;
    }

    class PreviewSurfaceCallback implements SurfaceHolder.Callback {
        PreviewSurfaceCallback() {
        }

        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        }

        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            boolean unused = AndroidCamera.this.mPreviewSurfaceValid = true;
            if (AndroidCamera.this.mClientId != 0 && AndroidCamera.this.mCallbacksRegistered) {
                AndroidCamera.this.nativeOnCanOpenCamera(AndroidCamera.this.mClientId);
            }
        }

        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            boolean unused = AndroidCamera.this.mPreviewSurfaceValid = false;
            if (AndroidCamera.this.mClientId != 0 && AndroidCamera.this.mCallbacksRegistered) {
                AndroidCamera.this.nativeOnShouldCloseCamera(AndroidCamera.this.mClientId);
            }
        }
    }
}
