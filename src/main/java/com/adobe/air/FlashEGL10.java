package com.adobe.air;

import android.app.Activity;
import android.os.Build;
import android.view.SurfaceView;
import com.adobe.flashruntime.air.VideoViewAIR;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class FlashEGL10 implements FlashEGL {
    private static int EGL_BUFFER_DESTROYED = 12437;
    private static int EGL_BUFFER_PRESERVED = 12436;
    private static int EGL_CONTEXT_CLIENT_VERSION = 12440;
    private static int EGL_COVERAGE_BUFFERS_NV = 12512;
    private static int EGL_COVERAGE_SAMPLES_NV = 12513;
    private static int EGL_OPENGL_ES2_BIT = 4;
    private static int EGL_SWAP_BEHAVIOR = 12435;
    private static String TAG = "FlashEGL10";
    private static int[] cfgAttrs = {12339, -1, 12325, -1, 12326, -1, 12352, EGL_OPENGL_ES2_BIT, 12344};
    private static int[] fbPBufferSurfaceAttrs = {12375, 64, 12374, 64, 12344};
    private static int[] fbWindowSurfaceOffAttrs = {EGL_SWAP_BEHAVIOR, EGL_BUFFER_DESTROYED, 12344};
    private static int[] fbWindowSurfaceOnAttrs = {EGL_SWAP_BEHAVIOR, EGL_BUFFER_PRESERVED, 12344};
    private int kAlphaBits = 5;
    private int kBlueBits = 4;
    private int kColorBits = 6;
    private int kConfigId = 1;
    private int kCsaaSamp = 10;
    private int kDepthBits = 7;
    private int kGreenBits = 3;
    private int kMsaaSamp = 9;
    private int kNumElements = 12;
    private int kRedBits = 2;
    private int kStencilBits = 8;
    private int kSurfaceTypes = 0;
    private int kSwapPreserve = 11;
    private int kSwapPreserveDefault = 0;
    private int kSwapPreserveOff = 2;
    private int kSwapPreserveOn = 1;
    private EGL10 mEgl = null;
    private EGLConfig mEglConfig = null;
    private int mEglConfigCount = 0;
    private EGLConfig[] mEglConfigList = null;
    volatile EGLContext mEglContext = EGL10.EGL_NO_CONTEXT;
    private EGLDisplay mEglDisplay = EGL10.EGL_NO_DISPLAY;
    private EGLSurface mEglPbufferSurface = EGL10.EGL_NO_SURFACE;
    private EGLSurface mEglSurface = EGL10.EGL_NO_SURFACE;
    private int[] mEglVersion = null;
    private EGLSurface mEglWindowSurface = EGL10.EGL_NO_SURFACE;
    private boolean mIsARGBSurface = false;
    private boolean mIsBufferPreserve = false;
    private boolean mIsES3Device = false;
    private boolean mIsGPUOOM = false;
    private int mPbufferConfigCount = 0;
    private int mPixmapConfigCount = 0;
    private int mWindowConfigCount = 0;

    public void FlashEGL10() {
        this.mEgl = null;
        this.mEglDisplay = EGL10.EGL_NO_DISPLAY;
        this.mEglConfig = null;
        this.mEglContext = EGL10.EGL_NO_CONTEXT;
        this.mEglSurface = EGL10.EGL_NO_SURFACE;
        this.mEglWindowSurface = EGL10.EGL_NO_SURFACE;
        this.mEglPbufferSurface = EGL10.EGL_NO_SURFACE;
        this.mIsARGBSurface = false;
    }

    private int XX(int i, int i2) {
        return (this.kNumElements * i) + i2;
    }

    public boolean HasGLContext() {
        return this.mEglContext != EGL10.EGL_NO_CONTEXT;
    }

    public int GetSurfaceWidth() {
        int[] iArr = new int[1];
        this.mEgl.eglQuerySurface(this.mEglDisplay, this.mEglSurface, 12375, iArr);
        return iArr[0];
    }

    public int GetSurfaceHeight() {
        int[] iArr = new int[1];
        this.mEgl.eglQuerySurface(this.mEglDisplay, this.mEglSurface, 12374, iArr);
        return iArr[0];
    }

    public boolean IsEmulator() {
        return Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic");
    }

    public boolean ChooseConfig(EGLDisplay eGLDisplay, int[] iArr, EGLConfig[] eGLConfigArr, int i, int[] iArr2) {
        if (!IsEmulator()) {
            return this.mEgl.eglChooseConfig(eGLDisplay, iArr, eGLConfigArr, i, iArr2);
        }
        int[] iArr3 = new int[1];
        this.mEgl.eglGetConfigs(eGLDisplay, (EGLConfig[]) null, 0, iArr3);
        int i2 = iArr3[0];
        EGLConfig[] eGLConfigArr2 = new EGLConfig[i2];
        this.mEgl.eglGetConfigs(eGLDisplay, eGLConfigArr2, i2, iArr3);
        int length = iArr.length;
        if (iArr.length % 2 != 0) {
            length = iArr.length - 1;
        }
        int i3 = 0;
        for (int i4 = 0; i4 < i2; i4++) {
            int i5 = 0;
            while (i5 < length) {
                if (iArr[i5 + 1] != -1) {
                    int[] iArr4 = new int[1];
                    this.mEgl.eglGetConfigAttrib(eGLDisplay, eGLConfigArr2[i4], iArr[i5], iArr4);
                    if ((iArr4[0] & iArr[i5 + 1]) != iArr[i5 + 1]) {
                        break;
                    }
                }
                i5 += 2;
            }
            if (i5 == length) {
                if (eGLConfigArr != null && i3 < i) {
                    eGLConfigArr[i3] = eGLConfigArr2[i4];
                }
                i3++;
            }
        }
        iArr2[0] = i3;
        return true;
    }

    public int[] GetNumConfigs() {
        int[] iArr = new int[1];
        ChooseConfig(this.mEglDisplay, cfgAttrs, (EGLConfig[]) null, 0, iArr);
        int i = iArr[0];
        this.mEglConfigCount = i;
        cfgAttrs[1] = 4;
        ChooseConfig(this.mEglDisplay, cfgAttrs, (EGLConfig[]) null, 0, iArr);
        int i2 = iArr[0];
        this.mWindowConfigCount = i2;
        cfgAttrs[1] = 2;
        ChooseConfig(this.mEglDisplay, cfgAttrs, (EGLConfig[]) null, 0, iArr);
        int i3 = iArr[0];
        this.mPixmapConfigCount = i3;
        cfgAttrs[1] = 1;
        ChooseConfig(this.mEglDisplay, cfgAttrs, (EGLConfig[]) null, 0, iArr);
        int i4 = iArr[0];
        int[] iArr2 = {i, i2, i3, i4};
        this.mPbufferConfigCount = i4;
        cfgAttrs[1] = -1;
        return iArr2;
    }

    public int[] GetConfigs(boolean z, boolean z2) {
        int i;
        int[] iArr = new int[(this.mEglConfigCount * this.kNumElements)];
        int[] iArr2 = new int[1];
        int[] iArr3 = new int[1];
        this.mEglConfigList = new EGLConfig[this.mEglConfigCount];
        checkEglError("Before eglChooseConfig");
        cfgAttrs[1] = 5;
        ChooseConfig(this.mEglDisplay, cfgAttrs, this.mEglConfigList, this.mEglConfigCount, iArr2);
        checkEglError("After eglChooseConfig");
        cfgAttrs[1] = -1;
        int i2 = iArr2[0];
        this.mEglConfigCount = i2;
        for (int i3 = 0; i3 < i2; i3++) {
            this.mEgl.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfigList[i3], 12339, iArr3);
            iArr[XX(i3, this.kSurfaceTypes)] = iArr3[0];
            iArr[XX(i3, this.kConfigId)] = i3;
            this.mEgl.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfigList[i3], 12324, iArr3);
            iArr[XX(i3, this.kRedBits)] = iArr3[0];
            this.mEgl.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfigList[i3], 12323, iArr3);
            iArr[XX(i3, this.kGreenBits)] = iArr3[0];
            this.mEgl.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfigList[i3], 12322, iArr3);
            iArr[XX(i3, this.kBlueBits)] = iArr3[0];
            this.mEgl.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfigList[i3], 12321, iArr3);
            iArr[XX(i3, this.kAlphaBits)] = iArr3[0];
            this.mEgl.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfigList[i3], 12320, iArr3);
            iArr[XX(i3, this.kColorBits)] = iArr3[0];
            this.mEgl.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfigList[i3], 12325, iArr3);
            iArr[XX(i3, this.kDepthBits)] = iArr3[0];
            this.mEgl.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfigList[i3], 12326, iArr3);
            iArr[XX(i3, this.kStencilBits)] = iArr3[0];
            iArr[XX(i3, this.kCsaaSamp)] = 0;
            iArr[XX(i3, this.kMsaaSamp)] = 0;
            if (z) {
                this.mEgl.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfigList[i3], EGL_COVERAGE_SAMPLES_NV, iArr3);
                if (iArr3[0] != 1) {
                    iArr[XX(i3, this.kCsaaSamp)] = iArr3[0];
                }
            } else {
                this.mEgl.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfigList[i3], 12337, iArr3);
                if (iArr3[0] != 1) {
                    iArr[XX(i3, this.kMsaaSamp)] = iArr3[0];
                }
            }
            if (z2) {
                int XX = XX(i3, this.kSwapPreserve);
                if ((this.mEglVersion[0] > 1 || this.mEglVersion[1] > 3) && (iArr[XX(i3, this.kSurfaceTypes)] & EGL_BUFFER_PRESERVED) != 0) {
                    i = 1;
                } else {
                    i = 0;
                }
                iArr[XX] = i;
            } else {
                iArr[XX(i3, this.kSwapPreserve)] = 0;
            }
        }
        return iArr;
    }

    public void SetConfig(int i) {
        this.mEglConfig = this.mEglConfigList[i];
        int[] iArr = new int[1];
        this.mEgl.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfig, 12324, iArr);
        int i2 = iArr[0];
        this.mEgl.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfig, 12323, iArr);
        int i3 = iArr[0];
        this.mEgl.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfig, 12322, iArr);
        int i4 = iArr[0];
        this.mEgl.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfig, 12321, iArr);
        int i5 = iArr[0];
        this.mEgl.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfig, 12325, iArr);
        int i6 = iArr[0];
        this.mEgl.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfig, 12326, iArr);
        int i7 = iArr[0];
        this.mEgl.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfig, 12337, iArr);
        int i8 = iArr[0];
        this.mEgl.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfig, 12338, iArr);
        int i9 = iArr[0];
        this.mEgl.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfig, 12339, iArr);
        int i10 = iArr[0];
    }

    /* JADX WARNING: Removed duplicated region for block: B:36:0x00f3  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x01a9  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int CreateDummySurfaceAndContext() {
        /*
            r14 = this;
            r13 = 3
            r12 = 2
            r7 = 12294(0x3006, float:1.7228E-41)
            r6 = 0
            r4 = 1
            javax.microedition.khronos.egl.EGLDisplay r0 = r14.mEglDisplay
            javax.microedition.khronos.egl.EGLDisplay r1 = javax.microedition.khronos.egl.EGL10.EGL_NO_DISPLAY
            if (r0 != r1) goto L_0x000f
            r0 = 12296(0x3008, float:1.723E-41)
        L_0x000e:
            return r0
        L_0x000f:
            javax.microedition.khronos.egl.EGLContext r0 = r14.mEglContext
            javax.microedition.khronos.egl.EGLContext r1 = javax.microedition.khronos.egl.EGL10.EGL_NO_CONTEXT
            if (r0 == r1) goto L_0x005b
            javax.microedition.khronos.egl.EGLSurface r0 = r14.mEglWindowSurface
            javax.microedition.khronos.egl.EGLSurface r1 = javax.microedition.khronos.egl.EGL10.EGL_NO_SURFACE
            if (r0 == r1) goto L_0x002b
            javax.microedition.khronos.egl.EGL10 r0 = r14.mEgl
            javax.microedition.khronos.egl.EGLDisplay r1 = r14.mEglDisplay
            javax.microedition.khronos.egl.EGLSurface r2 = r14.mEglWindowSurface
            javax.microedition.khronos.egl.EGLSurface r3 = r14.mEglWindowSurface
            javax.microedition.khronos.egl.EGLContext r4 = r14.mEglContext
            r0.eglMakeCurrent(r1, r2, r3, r4)
            r0 = 12288(0x3000, float:1.7219E-41)
            goto L_0x000e
        L_0x002b:
            javax.microedition.khronos.egl.EGLSurface r0 = r14.mEglPbufferSurface
            javax.microedition.khronos.egl.EGLSurface r1 = javax.microedition.khronos.egl.EGL10.EGL_NO_SURFACE
            if (r0 == r1) goto L_0x0041
            javax.microedition.khronos.egl.EGL10 r0 = r14.mEgl
            javax.microedition.khronos.egl.EGLDisplay r1 = r14.mEglDisplay
            javax.microedition.khronos.egl.EGLSurface r2 = r14.mEglPbufferSurface
            javax.microedition.khronos.egl.EGLSurface r3 = r14.mEglPbufferSurface
            javax.microedition.khronos.egl.EGLContext r4 = r14.mEglContext
            r0.eglMakeCurrent(r1, r2, r3, r4)
            r0 = 12288(0x3000, float:1.7219E-41)
            goto L_0x000e
        L_0x0041:
            javax.microedition.khronos.egl.EGL10 r0 = r14.mEgl
            javax.microedition.khronos.egl.EGLDisplay r1 = r14.mEglDisplay
            javax.microedition.khronos.egl.EGLSurface r2 = javax.microedition.khronos.egl.EGL10.EGL_NO_SURFACE
            javax.microedition.khronos.egl.EGLSurface r3 = javax.microedition.khronos.egl.EGL10.EGL_NO_SURFACE
            javax.microedition.khronos.egl.EGLContext r5 = javax.microedition.khronos.egl.EGL10.EGL_NO_CONTEXT
            r0.eglMakeCurrent(r1, r2, r3, r5)
            javax.microedition.khronos.egl.EGL10 r0 = r14.mEgl
            javax.microedition.khronos.egl.EGLDisplay r1 = r14.mEglDisplay
            javax.microedition.khronos.egl.EGLContext r2 = r14.mEglContext
            r0.eglDestroyContext(r1, r2)
            javax.microedition.khronos.egl.EGLContext r0 = javax.microedition.khronos.egl.EGL10.EGL_NO_CONTEXT
            r14.mEglContext = r0
        L_0x005b:
            int[] r5 = new int[r4]
            javax.microedition.khronos.egl.EGLConfig[] r3 = new javax.microedition.khronos.egl.EGLConfig[r4]
            int[] r0 = cfgAttrs
            r0[r4] = r4
            javax.microedition.khronos.egl.EGLDisplay r1 = r14.mEglDisplay
            int[] r2 = cfgAttrs
            r0 = r14
            r0.ChooseConfig(r1, r2, r3, r4, r5)
            int[] r0 = cfgAttrs
            r1 = -1
            r0[r4] = r1
            r0 = r5[r6]
            if (r0 != 0) goto L_0x0076
            r0 = r7
            goto L_0x000e
        L_0x0076:
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 18
            if (r0 < r1) goto L_0x00a2
            r0 = r4
        L_0x007d:
            int[] r1 = new int[r13]
            int r2 = EGL_CONTEXT_CLIENT_VERSION
            r1[r6] = r2
            r1[r4] = r12
            r2 = 12344(0x3038, float:1.7298E-41)
            r1[r12] = r2
            if (r0 == 0) goto L_0x01af
            javax.microedition.khronos.egl.EGL10 r0 = r14.mEgl
            javax.microedition.khronos.egl.EGLDisplay r2 = r14.mEglDisplay
            r5 = r3[r6]
            javax.microedition.khronos.egl.EGLContext r8 = javax.microedition.khronos.egl.EGL10.EGL_NO_CONTEXT
            javax.microedition.khronos.egl.EGLContext r0 = r0.eglCreateContext(r2, r5, r8, r1)
            r14.mEglContext = r0
            javax.microedition.khronos.egl.EGLContext r0 = r14.mEglContext
            javax.microedition.khronos.egl.EGLContext r2 = javax.microedition.khronos.egl.EGL10.EGL_NO_CONTEXT
            if (r0 != r2) goto L_0x00a4
            r0 = r7
            goto L_0x000e
        L_0x00a2:
            r0 = r6
            goto L_0x007d
        L_0x00a4:
            java.lang.String r0 = "After creating dummy context for checking gl version"
            r14.checkEglError(r0)
            javax.microedition.khronos.egl.EGL10 r0 = r14.mEgl
            javax.microedition.khronos.egl.EGLDisplay r2 = r14.mEglDisplay
            r5 = r3[r6]
            int[] r8 = fbPBufferSurfaceAttrs
            javax.microedition.khronos.egl.EGLSurface r2 = r0.eglCreatePbufferSurface(r2, r5, r8)
            java.lang.String r0 = "After eglCreatePbufferSurface for checking gl version"
            r14.checkEglError(r0)
            javax.microedition.khronos.egl.EGLSurface r0 = javax.microedition.khronos.egl.EGL10.EGL_NO_SURFACE
            if (r2 != r0) goto L_0x00c1
            r0 = r7
            goto L_0x000e
        L_0x00c1:
            javax.microedition.khronos.egl.EGL10 r0 = r14.mEgl
            javax.microedition.khronos.egl.EGLDisplay r5 = r14.mEglDisplay
            javax.microedition.khronos.egl.EGLContext r8 = r14.mEglContext
            r0.eglMakeCurrent(r5, r2, r2, r8)
            java.lang.String r0 = "After eglMakeCurrent for checking gl version"
            r14.checkEglError(r0)
            r0 = 7938(0x1f02, float:1.1124E-41)
            java.lang.String r0 = android.opengl.GLES10.glGetString(r0)
            if (r0 == 0) goto L_0x01ac
            java.util.Scanner r5 = new java.util.Scanner
            r5.<init>(r0)
            java.lang.String r0 = "[^\\w']+"
            r5.useDelimiter(r0)
        L_0x00e1:
            boolean r0 = r5.hasNext()
            if (r0 == 0) goto L_0x01ac
            boolean r0 = r5.hasNextInt()
            if (r0 == 0) goto L_0x0162
            int r0 = r5.nextInt()
        L_0x00f1:
            if (r0 < r13) goto L_0x01a9
            r0 = r4
        L_0x00f4:
            javax.microedition.khronos.egl.EGL10 r5 = r14.mEgl
            javax.microedition.khronos.egl.EGLDisplay r8 = r14.mEglDisplay
            javax.microedition.khronos.egl.EGLSurface r9 = javax.microedition.khronos.egl.EGL10.EGL_NO_SURFACE
            javax.microedition.khronos.egl.EGLSurface r10 = javax.microedition.khronos.egl.EGL10.EGL_NO_SURFACE
            javax.microedition.khronos.egl.EGLContext r11 = javax.microedition.khronos.egl.EGL10.EGL_NO_CONTEXT
            r5.eglMakeCurrent(r8, r9, r10, r11)
            javax.microedition.khronos.egl.EGL10 r5 = r14.mEgl
            javax.microedition.khronos.egl.EGLDisplay r8 = r14.mEglDisplay
            r5.eglDestroySurface(r8, r2)
            javax.microedition.khronos.egl.EGL10 r2 = r14.mEgl
            javax.microedition.khronos.egl.EGLDisplay r5 = r14.mEglDisplay
            javax.microedition.khronos.egl.EGLContext r8 = r14.mEglContext
            r2.eglDestroyContext(r5, r8)
            javax.microedition.khronos.egl.EGLContext r2 = javax.microedition.khronos.egl.EGL10.EGL_NO_CONTEXT
            r14.mEglContext = r2
        L_0x0115:
            if (r0 == 0) goto L_0x0139
            r1[r4] = r13
            java.lang.String r0 = "Before eglCreateContext es3"
            r14.checkEglError(r0)
            javax.microedition.khronos.egl.EGL10 r0 = r14.mEgl
            javax.microedition.khronos.egl.EGLDisplay r2 = r14.mEglDisplay
            r5 = r3[r6]
            javax.microedition.khronos.egl.EGLContext r8 = javax.microedition.khronos.egl.EGL10.EGL_NO_CONTEXT
            javax.microedition.khronos.egl.EGLContext r0 = r0.eglCreateContext(r2, r5, r8, r1)
            r14.mEglContext = r0
            java.lang.String r0 = "After eglCreateContext es3"
            r14.checkEglError(r0)
            javax.microedition.khronos.egl.EGLContext r0 = r14.mEglContext
            javax.microedition.khronos.egl.EGLContext r2 = javax.microedition.khronos.egl.EGL10.EGL_NO_CONTEXT
            if (r0 == r2) goto L_0x0139
            r14.mIsES3Device = r4
        L_0x0139:
            javax.microedition.khronos.egl.EGLContext r0 = r14.mEglContext
            javax.microedition.khronos.egl.EGLContext r2 = javax.microedition.khronos.egl.EGL10.EGL_NO_CONTEXT
            if (r0 != r2) goto L_0x016d
            r1[r4] = r12
            java.lang.String r0 = "Before eglCreateContext es2"
            r14.checkEglError(r0)
            javax.microedition.khronos.egl.EGL10 r0 = r14.mEgl
            javax.microedition.khronos.egl.EGLDisplay r2 = r14.mEglDisplay
            r4 = r3[r6]
            javax.microedition.khronos.egl.EGLContext r5 = javax.microedition.khronos.egl.EGL10.EGL_NO_CONTEXT
            javax.microedition.khronos.egl.EGLContext r0 = r0.eglCreateContext(r2, r4, r5, r1)
            r14.mEglContext = r0
            java.lang.String r0 = "After eglCreateContext es2"
            r14.checkEglError(r0)
            javax.microedition.khronos.egl.EGLContext r0 = r14.mEglContext
            javax.microedition.khronos.egl.EGLContext r1 = javax.microedition.khronos.egl.EGL10.EGL_NO_CONTEXT
            if (r0 != r1) goto L_0x016d
            r0 = r7
            goto L_0x000e
        L_0x0162:
            boolean r0 = r5.hasNext()
            if (r0 == 0) goto L_0x00e1
            r5.next()
            goto L_0x00e1
        L_0x016d:
            java.lang.String r0 = "Before eglCreatePbufferSurface"
            r14.checkEglError(r0)
            javax.microedition.khronos.egl.EGL10 r0 = r14.mEgl
            javax.microedition.khronos.egl.EGLDisplay r1 = r14.mEglDisplay
            r2 = r3[r6]
            int[] r3 = fbPBufferSurfaceAttrs
            javax.microedition.khronos.egl.EGLSurface r0 = r0.eglCreatePbufferSurface(r1, r2, r3)
            r14.mEglPbufferSurface = r0
            java.lang.String r0 = "After eglCreatePbufferSurface"
            r14.checkEglError(r0)
            javax.microedition.khronos.egl.EGLSurface r0 = r14.mEglPbufferSurface
            javax.microedition.khronos.egl.EGLSurface r1 = javax.microedition.khronos.egl.EGL10.EGL_NO_SURFACE
            if (r0 != r1) goto L_0x018e
            r0 = r7
            goto L_0x000e
        L_0x018e:
            java.lang.String r0 = "Before eglMakeCurrent"
            r14.checkEglError(r0)
            javax.microedition.khronos.egl.EGL10 r0 = r14.mEgl
            javax.microedition.khronos.egl.EGLDisplay r1 = r14.mEglDisplay
            javax.microedition.khronos.egl.EGLSurface r2 = r14.mEglPbufferSurface
            javax.microedition.khronos.egl.EGLSurface r3 = r14.mEglPbufferSurface
            javax.microedition.khronos.egl.EGLContext r4 = r14.mEglContext
            r0.eglMakeCurrent(r1, r2, r3, r4)
            java.lang.String r0 = "After eglMakeCurrent"
            r14.checkEglError(r0)
            r0 = 12288(0x3000, float:1.7219E-41)
            goto L_0x000e
        L_0x01a9:
            r0 = r6
            goto L_0x00f4
        L_0x01ac:
            r0 = r6
            goto L_0x00f1
        L_0x01af:
            r0 = r6
            goto L_0x0115
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adobe.air.FlashEGL10.CreateDummySurfaceAndContext():int");
    }

    public int InitEGL() {
        if (this.mEglContext != EGL10.EGL_NO_CONTEXT) {
            return 12288;
        }
        this.mEgl = (EGL10) EGLContext.getEGL();
        checkEglError("Before eglGetDisplay");
        this.mEglDisplay = this.mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        int checkEglError = checkEglError("After eglGetDisplay");
        if (12288 != checkEglError) {
            return checkEglError;
        }
        this.mEglVersion = new int[2];
        checkEglError("Before eglInitialize");
        this.mEgl.eglInitialize(this.mEglDisplay, this.mEglVersion);
        int checkEglError2 = checkEglError("After eglInitialize");
        if (12288 == checkEglError2) {
            return 12288;
        }
        return checkEglError2;
    }

    public boolean DestroyGLContext() {
        if (this.mEglContext == EGL10.EGL_NO_CONTEXT || this.mEglDisplay == EGL10.EGL_NO_DISPLAY) {
            return false;
        }
        checkEglError("DestroyGLContext: Before eglMakeCurrent for noSurface");
        this.mEgl.eglMakeCurrent(this.mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
        checkEglError("DestroyGLContext: After eglMakeCurrent");
        if (this.mEglPbufferSurface != EGL10.EGL_NO_SURFACE) {
            this.mEgl.eglDestroySurface(this.mEglDisplay, this.mEglPbufferSurface);
            this.mEglPbufferSurface = EGL10.EGL_NO_SURFACE;
        }
        checkEglError("Before eglDestroyContext");
        boolean eglDestroyContext = this.mEgl.eglDestroyContext(this.mEglDisplay, this.mEglContext);
        checkEglError("After eglDestroyContext");
        this.mEglContext = EGL10.EGL_NO_CONTEXT;
        return eglDestroyContext;
    }

    public int CreateGLContext(boolean z) {
        if (this.mEglConfig == null) {
            return 12293;
        }
        if (this.mEglContext != EGL10.EGL_NO_CONTEXT && !z) {
            return 12288;
        }
        int[] iArr = {EGL_CONTEXT_CLIENT_VERSION, this.mIsES3Device ? 3 : 2, 12344};
        if (z) {
            EGLContext eGLContext = this.mEglContext;
            checkEglError("Before eglCreateContext");
            this.mEglContext = this.mEgl.eglCreateContext(this.mEglDisplay, this.mEglConfig, eGLContext, iArr);
            checkEglError("After eglCreateContext");
            this.mEgl.eglDestroyContext(this.mEglDisplay, eGLContext);
            checkEglError("After eglDestroyContext");
        } else {
            checkEglError("Before eglCreateContext");
            this.mEglContext = this.mEgl.eglCreateContext(this.mEglDisplay, this.mEglConfig, EGL10.EGL_NO_CONTEXT, iArr);
            checkEglError("After eglCreateContext");
        }
        if (this.mEglContext == EGL10.EGL_NO_CONTEXT) {
            return 12294;
        }
        if (EGL10.EGL_NO_SURFACE == this.mEglPbufferSurface) {
            checkEglError("Before eglCreatePbufferSurface");
            this.mEglPbufferSurface = this.mEgl.eglCreatePbufferSurface(this.mEglDisplay, this.mEglConfig, fbPBufferSurfaceAttrs);
            checkEglError("After eglCreatePbufferSurface");
        }
        return 12288;
    }

    public void TerminateEGL() {
        if (!(this.mEgl == null || this.mEglDisplay == EGL10.EGL_NO_DISPLAY)) {
            this.mEgl.eglTerminate(this.mEglDisplay);
        }
        this.mEglDisplay = EGL10.EGL_NO_DISPLAY;
    }

    public void ReleaseGPUResources() {
        if (this.mEglContext != EGL10.EGL_NO_CONTEXT) {
            checkEglError("Before eglMakeCurrent");
            this.mEgl.eglMakeCurrent(this.mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
            checkEglError("After eglMakeCurrent");
            synchronized (this.mEgl) {
                checkEglError("Before eglDestroySurface");
                if (this.mEglWindowSurface != EGL10.EGL_NO_SURFACE) {
                    this.mEgl.eglDestroySurface(this.mEglDisplay, this.mEglWindowSurface);
                    this.mEglWindowSurface = EGL10.EGL_NO_SURFACE;
                }
                checkEglError("After eglDestroySurface (window)");
            }
            if (this.mEglPbufferSurface != EGL10.EGL_NO_SURFACE) {
                checkEglError("Before eglDestroySurface (pbuffer)");
                this.mEgl.eglDestroySurface(this.mEglDisplay, this.mEglPbufferSurface);
                checkEglError("After eglDestroySurface (pbuffer)");
                this.mEglPbufferSurface = EGL10.EGL_NO_SURFACE;
            }
            checkEglError("Before eglDestroyContext");
            this.mEgl.eglDestroyContext(this.mEglDisplay, this.mEglContext);
            checkEglError("After eglDestroyContext");
            this.mEglContext = EGL10.EGL_NO_CONTEXT;
            this.mEglSurface = EGL10.EGL_NO_SURFACE;
        }
    }

    public void SwapEGLBuffers() {
        if (12288 == MakeGLCurrent()) {
            checkEglError("Before eglSwapBuffers");
            this.mEgl.eglSwapBuffers(this.mEglDisplay, this.mEglSurface);
            checkEglError("After eglSwapBuffers");
        }
    }

    public int MakeGLCurrent() {
        if (this.mEglContext == EGL10.EGL_NO_CONTEXT) {
            return 12294;
        }
        if (this.mEglSurface == EGL10.EGL_NO_SURFACE) {
            return 12301;
        }
        if (this.mEglDisplay == EGL10.EGL_NO_DISPLAY) {
            return 12296;
        }
        if (Build.VERSION.SDK_INT == 23 && Build.MODEL.equals("Nexus 5") && this.mEglSurface == this.mEglPbufferSurface) {
            return 12301;
        }
        checkEglError("Before eglMakeCurrent");
        this.mEgl.eglMakeCurrent(this.mEglDisplay, this.mEglSurface, this.mEglSurface, this.mEglContext);
        return checkEglError("After eglMakeCurrent");
    }

    public int CreateWindowSurface(SurfaceView surfaceView, int i) {
        boolean z;
        boolean z2 = true;
        if (this.mIsGPUOOM) {
            return 12291;
        }
        boolean z3 = surfaceView instanceof AIRWindowSurfaceView;
        if (!(surfaceView instanceof VideoViewAIR) && !(surfaceView instanceof AIRStage3DSurfaceView) && !z3) {
            return 12301;
        }
        if (this.mEglWindowSurface != EGL10.EGL_NO_SURFACE) {
            this.mEglSurface = this.mEglWindowSurface;
            return MakeGLCurrent();
        }
        if (i == this.kSwapPreserveOn) {
            checkEglError("Before eglCreateWindowSurface");
            this.mEglWindowSurface = this.mEgl.eglCreateWindowSurface(this.mEglDisplay, this.mEglConfig, surfaceView.getHolder(), fbWindowSurfaceOnAttrs);
            if (this.mEglWindowSurface == EGL10.EGL_NO_SURFACE) {
                checkEglError("After eglCreateWindowSurface");
                z = false;
            }
            z = true;
        } else {
            if (i == this.kSwapPreserveOff) {
                checkEglError("Before eglCreateWindowSurface");
                this.mEglWindowSurface = this.mEgl.eglCreateWindowSurface(this.mEglDisplay, this.mEglConfig, surfaceView.getHolder(), fbWindowSurfaceOffAttrs);
                if (this.mEglWindowSurface == EGL10.EGL_NO_SURFACE) {
                    checkEglError("After eglCreateWindowSurface");
                    z = false;
                }
            }
            z = true;
        }
        if (this.mEglWindowSurface == EGL10.EGL_NO_SURFACE) {
            checkEglError("Before eglCreateWindowSurface");
            this.mEglWindowSurface = this.mEgl.eglCreateWindowSurface(this.mEglDisplay, this.mEglConfig, surfaceView.getHolder(), (int[]) null);
            int checkEglError = checkEglError("After eglCreateWindowSurface");
            if (checkEglError != 12288) {
                return checkEglError;
            }
        }
        if (this.mEglWindowSurface == EGL10.EGL_NO_SURFACE) {
            return 12301;
        }
        this.mEglSurface = this.mEglWindowSurface;
        if (z3) {
            ((AIRWindowSurfaceView) surfaceView).setFlashEGL(this);
            Activity activity = ((AIRWindowSurfaceView) surfaceView).getActivityWrapper().getActivity();
            if (activity != null) {
                activity.getWindow().setSoftInputMode(34);
            }
        }
        int[] iArr = {0};
        this.mIsBufferPreserve = false;
        if (z && this.mEgl.eglQuerySurface(this.mEglDisplay, this.mEglSurface, EGL_SWAP_BEHAVIOR, iArr)) {
            if (iArr[0] != EGL_BUFFER_PRESERVED) {
                z2 = false;
            }
            this.mIsBufferPreserve = z2;
        }
        return MakeGLCurrent();
    }

    public boolean DestroyWindowSurface() {
        if (this.mEglWindowSurface == EGL10.EGL_NO_SURFACE) {
            return false;
        }
        checkEglError("Before eglMakeCurrent");
        this.mEgl.eglMakeCurrent(this.mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
        if (12288 != checkEglError("After eglMakeCurrent")) {
            return false;
        }
        checkEglError("Before eglDestroySurface (window)");
        this.mEgl.eglDestroySurface(this.mEglDisplay, this.mEglWindowSurface);
        if (12288 != checkEglError("After eglDestroySurface (window)")) {
            return false;
        }
        if (this.mEglSurface == this.mEglWindowSurface) {
            this.mEglSurface = EGL10.EGL_NO_SURFACE;
        }
        this.mEglWindowSurface = EGL10.EGL_NO_SURFACE;
        if (!(this.mEglPbufferSurface == EGL10.EGL_NO_SURFACE || this.mEglContext == EGL10.EGL_NO_CONTEXT)) {
            this.mEglSurface = this.mEglPbufferSurface;
            this.mEgl.eglMakeCurrent(this.mEglDisplay, this.mEglSurface, this.mEglSurface, this.mEglContext);
            if (12288 == checkEglError("After eglMakeCurrent")) {
                return true;
            }
            return false;
        }
        return true;
    }

    public boolean IsARGBSurface() {
        return this.mIsARGBSurface;
    }

    public boolean IsBufferPreserve() {
        return this.mIsBufferPreserve;
    }

    private int checkEglError(String str) {
        int eglGetError = this.mEgl.eglGetError();
        if (eglGetError != 12288 && !this.mIsGPUOOM && eglGetError == 12291) {
            if (this.mEglWindowSurface != EGL10.EGL_NO_SURFACE) {
                this.mEgl.eglDestroySurface(this.mEglDisplay, this.mEglWindowSurface);
                int eglGetError2 = this.mEgl.eglGetError();
                this.mEglWindowSurface = EGL10.EGL_NO_SURFACE;
                this.mEglSurface = EGL10.EGL_NO_SURFACE;
                if (eglGetError2 != 12288) {
                }
                this.mEglWindowSurface = EGL10.EGL_NO_SURFACE;
                this.mEgl.eglMakeCurrent(this.mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                if (this.mEgl.eglGetError() != 12288) {
                }
            }
            if (!(this.mEglPbufferSurface == EGL10.EGL_NO_SURFACE || this.mEglContext == EGL10.EGL_NO_CONTEXT)) {
                this.mEglSurface = this.mEglPbufferSurface;
                this.mEgl.eglMakeCurrent(this.mEglDisplay, this.mEglSurface, this.mEglSurface, this.mEglContext);
                if (this.mEgl.eglGetError() != 12288) {
                }
            }
            this.mIsGPUOOM = true;
        }
        return eglGetError;
    }
}
