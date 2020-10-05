package com.adobe.air;

import android.app.Activity;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.os.Build;
import android.view.SurfaceView;
import com.adobe.flashruntime.air.VideoViewAIR;

public class FlashEGL14 implements FlashEGL {
    private static int EGL_BUFFER_DESTROYED = 12437;
    private static int EGL_BUFFER_PRESERVED = 12436;
    private static int EGL_CONTEXT_CLIENT_VERSION = 12440;
    private static int EGL_COVERAGE_BUFFERS_NV = 12512;
    private static int EGL_COVERAGE_SAMPLES_NV = 12513;
    private static int EGL_OPENGL_ES2_BIT = 4;
    private static int EGL_SWAP_BEHAVIOR = 12435;
    private static final int MAX_CONFIGS = 100;
    private static String TAG = "FlashEGL14";
    private static int[] cfgAttrs = {12339, -1, 12325, -1, 12326, -1, 12352, EGL_OPENGL_ES2_BIT, 12344};
    private static int[] fbPBufferSurfaceAttrs = {12375, 64, 12374, 64, 12344};
    private static int[] fbWindowSurfaceDefAttrs = {12344};
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
    private EGL14 mEgl = null;
    private EGLConfig mEglConfig = null;
    private int mEglConfigCount = 0;
    private EGLConfig[] mEglConfigList = null;
    volatile EGLContext mEglContext = EGL14.EGL_NO_CONTEXT;
    private EGLDisplay mEglDisplay = EGL14.EGL_NO_DISPLAY;
    private EGLSurface mEglPbufferSurface = EGL14.EGL_NO_SURFACE;
    private EGLSurface mEglSurface = EGL14.EGL_NO_SURFACE;
    private int[] mEglVersion = null;
    private EGLSurface mEglWindowSurface = EGL14.EGL_NO_SURFACE;
    private boolean mIsARGBSurface = false;
    private boolean mIsBufferPreserve = false;
    private boolean mIsES3Device = false;
    private boolean mIsGPUOOM = false;
    private int mPbufferConfigCount = 0;
    private int mPixmapConfigCount = 0;
    private int mWindowConfigCount = 0;

    public void FlashEGL14() {
        this.mEgl = null;
        this.mEglDisplay = EGL14.EGL_NO_DISPLAY;
        this.mEglConfig = null;
        this.mEglContext = EGL14.EGL_NO_CONTEXT;
        this.mEglSurface = EGL14.EGL_NO_SURFACE;
        this.mEglWindowSurface = EGL14.EGL_NO_SURFACE;
        this.mEglPbufferSurface = EGL14.EGL_NO_SURFACE;
        this.mIsARGBSurface = false;
    }

    private int XX(int i, int i2) {
        return (this.kNumElements * i) + i2;
    }

    public boolean HasGLContext() {
        return this.mEglContext != EGL14.EGL_NO_CONTEXT;
    }

    public int GetSurfaceWidth() {
        int[] iArr = new int[1];
        EGL14 egl14 = this.mEgl;
        EGL14.eglQuerySurface(this.mEglDisplay, this.mEglSurface, 12375, iArr, 0);
        return iArr[0];
    }

    public int GetSurfaceHeight() {
        int[] iArr = new int[1];
        EGL14 egl14 = this.mEgl;
        EGL14.eglQuerySurface(this.mEglDisplay, this.mEglSurface, 12374, iArr, 0);
        return iArr[0];
    }

    public boolean IsEmulator() {
        return Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic");
    }

    public boolean ChooseConfig(EGLDisplay eGLDisplay, int[] iArr, EGLConfig[] eGLConfigArr, int i, int[] iArr2) {
        if (!IsEmulator()) {
            EGL14 egl14 = this.mEgl;
            return EGL14.eglChooseConfig(eGLDisplay, iArr, 0, eGLConfigArr, 0, i, iArr2, 0);
        }
        int[] iArr3 = new int[1];
        EGL14 egl142 = this.mEgl;
        EGL14.eglGetConfigs(eGLDisplay, (EGLConfig[]) null, 0, 0, iArr3, 0);
        int i2 = iArr3[0];
        EGLConfig[] eGLConfigArr2 = new EGLConfig[i2];
        EGL14 egl143 = this.mEgl;
        EGL14.eglGetConfigs(eGLDisplay, eGLConfigArr2, 0, i2, iArr3, 0);
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
                    EGL14 egl144 = this.mEgl;
                    EGL14.eglGetConfigAttrib(eGLDisplay, eGLConfigArr2[i4], iArr[i5], iArr4, 0);
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
        EGLConfig[] eGLConfigArr = new EGLConfig[100];
        ChooseConfig(this.mEglDisplay, cfgAttrs, eGLConfigArr, 100, iArr);
        int i = iArr[0];
        this.mEglConfigCount = i;
        cfgAttrs[1] = 4;
        ChooseConfig(this.mEglDisplay, cfgAttrs, eGLConfigArr, 100, iArr);
        int i2 = iArr[0];
        this.mWindowConfigCount = i2;
        cfgAttrs[1] = 2;
        ChooseConfig(this.mEglDisplay, cfgAttrs, eGLConfigArr, 100, iArr);
        int i3 = iArr[0];
        this.mPixmapConfigCount = i3;
        cfgAttrs[1] = 1;
        ChooseConfig(this.mEglDisplay, cfgAttrs, eGLConfigArr, 100, iArr);
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
        ChooseConfig(this.mEglDisplay, cfgAttrs, this.mEglConfigList, this.mEglConfigCount, iArr2);
        checkEglError("After eglChooseConfig");
        int i2 = iArr2[0];
        this.mEglConfigCount = i2;
        for (int i3 = 0; i3 < i2; i3++) {
            EGL14 egl14 = this.mEgl;
            EGL14.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfigList[i3], 12339, iArr3, 0);
            iArr[XX(i3, this.kSurfaceTypes)] = iArr3[0];
            iArr[XX(i3, this.kConfigId)] = i3;
            EGL14 egl142 = this.mEgl;
            EGL14.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfigList[i3], 12324, iArr3, 0);
            iArr[XX(i3, this.kRedBits)] = iArr3[0];
            EGL14 egl143 = this.mEgl;
            EGL14.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfigList[i3], 12323, iArr3, 0);
            iArr[XX(i3, this.kGreenBits)] = iArr3[0];
            EGL14 egl144 = this.mEgl;
            EGL14.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfigList[i3], 12322, iArr3, 0);
            iArr[XX(i3, this.kBlueBits)] = iArr3[0];
            EGL14 egl145 = this.mEgl;
            EGL14.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfigList[i3], 12321, iArr3, 0);
            iArr[XX(i3, this.kAlphaBits)] = iArr3[0];
            EGL14 egl146 = this.mEgl;
            EGL14.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfigList[i3], 12320, iArr3, 0);
            iArr[XX(i3, this.kColorBits)] = iArr3[0];
            EGL14 egl147 = this.mEgl;
            EGL14.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfigList[i3], 12325, iArr3, 0);
            iArr[XX(i3, this.kDepthBits)] = iArr3[0];
            EGL14 egl148 = this.mEgl;
            EGL14.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfigList[i3], 12326, iArr3, 0);
            iArr[XX(i3, this.kStencilBits)] = iArr3[0];
            iArr[XX(i3, this.kCsaaSamp)] = 0;
            iArr[XX(i3, this.kMsaaSamp)] = 0;
            if (z) {
                EGL14 egl149 = this.mEgl;
                EGL14.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfigList[i3], EGL_COVERAGE_SAMPLES_NV, iArr3, 0);
                if (iArr3[0] != 1) {
                    iArr[XX(i3, this.kCsaaSamp)] = iArr3[0];
                }
            } else {
                EGL14 egl1410 = this.mEgl;
                EGL14.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfigList[i3], 12337, iArr3, 0);
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
        EGL14 egl14 = this.mEgl;
        EGL14.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfig, 12324, iArr, 0);
        int i2 = iArr[0];
        EGL14 egl142 = this.mEgl;
        EGL14.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfig, 12323, iArr, 0);
        int i3 = iArr[0];
        EGL14 egl143 = this.mEgl;
        EGL14.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfig, 12322, iArr, 0);
        int i4 = iArr[0];
        EGL14 egl144 = this.mEgl;
        EGL14.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfig, 12321, iArr, 0);
        int i5 = iArr[0];
        EGL14 egl145 = this.mEgl;
        EGL14.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfig, 12325, iArr, 0);
        int i6 = iArr[0];
        EGL14 egl146 = this.mEgl;
        EGL14.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfig, 12326, iArr, 0);
        int i7 = iArr[0];
        EGL14 egl147 = this.mEgl;
        EGL14.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfig, 12337, iArr, 0);
        int i8 = iArr[0];
        EGL14 egl148 = this.mEgl;
        EGL14.eglGetConfigAttrib(this.mEglDisplay, this.mEglConfig, 12338, iArr, 0);
        int i9 = iArr[0];
    }

    /* JADX WARNING: Removed duplicated region for block: B:36:0x00f3  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x01a9  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int CreateDummySurfaceAndContext() {
        /*
            r13 = this;
            r12 = 3
            r11 = 2
            r7 = 12294(0x3006, float:1.7228E-41)
            r4 = 1
            r6 = 0
            android.opengl.EGLDisplay r0 = r13.mEglDisplay
            android.opengl.EGLDisplay r1 = android.opengl.EGL14.EGL_NO_DISPLAY
            if (r0 != r1) goto L_0x000f
            r0 = 12296(0x3008, float:1.723E-41)
        L_0x000e:
            return r0
        L_0x000f:
            android.opengl.EGLContext r0 = r13.mEglContext
            android.opengl.EGLContext r1 = android.opengl.EGL14.EGL_NO_CONTEXT
            if (r0 == r1) goto L_0x005b
            android.opengl.EGLSurface r0 = r13.mEglWindowSurface
            android.opengl.EGLSurface r1 = android.opengl.EGL14.EGL_NO_SURFACE
            if (r0 == r1) goto L_0x002b
            android.opengl.EGL14 r0 = r13.mEgl
            android.opengl.EGLDisplay r0 = r13.mEglDisplay
            android.opengl.EGLSurface r1 = r13.mEglWindowSurface
            android.opengl.EGLSurface r2 = r13.mEglWindowSurface
            android.opengl.EGLContext r3 = r13.mEglContext
            android.opengl.EGL14.eglMakeCurrent(r0, r1, r2, r3)
            r0 = 12288(0x3000, float:1.7219E-41)
            goto L_0x000e
        L_0x002b:
            android.opengl.EGLSurface r0 = r13.mEglPbufferSurface
            android.opengl.EGLSurface r1 = android.opengl.EGL14.EGL_NO_SURFACE
            if (r0 == r1) goto L_0x0041
            android.opengl.EGL14 r0 = r13.mEgl
            android.opengl.EGLDisplay r0 = r13.mEglDisplay
            android.opengl.EGLSurface r1 = r13.mEglPbufferSurface
            android.opengl.EGLSurface r2 = r13.mEglPbufferSurface
            android.opengl.EGLContext r3 = r13.mEglContext
            android.opengl.EGL14.eglMakeCurrent(r0, r1, r2, r3)
            r0 = 12288(0x3000, float:1.7219E-41)
            goto L_0x000e
        L_0x0041:
            android.opengl.EGL14 r0 = r13.mEgl
            android.opengl.EGLDisplay r0 = r13.mEglDisplay
            android.opengl.EGLSurface r1 = android.opengl.EGL14.EGL_NO_SURFACE
            android.opengl.EGLSurface r2 = android.opengl.EGL14.EGL_NO_SURFACE
            android.opengl.EGLContext r3 = android.opengl.EGL14.EGL_NO_CONTEXT
            android.opengl.EGL14.eglMakeCurrent(r0, r1, r2, r3)
            android.opengl.EGL14 r0 = r13.mEgl
            android.opengl.EGLDisplay r0 = r13.mEglDisplay
            android.opengl.EGLContext r1 = r13.mEglContext
            android.opengl.EGL14.eglDestroyContext(r0, r1)
            android.opengl.EGLContext r0 = android.opengl.EGL14.EGL_NO_CONTEXT
            r13.mEglContext = r0
        L_0x005b:
            int[] r5 = new int[r4]
            android.opengl.EGLConfig[] r3 = new android.opengl.EGLConfig[r4]
            int[] r0 = cfgAttrs
            r0[r4] = r4
            android.opengl.EGLDisplay r1 = r13.mEglDisplay
            int[] r2 = cfgAttrs
            r0 = r13
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
            int[] r1 = new int[r12]
            int r2 = EGL_CONTEXT_CLIENT_VERSION
            r1[r6] = r2
            r1[r4] = r11
            r2 = 12344(0x3038, float:1.7298E-41)
            r1[r11] = r2
            if (r0 == 0) goto L_0x01af
            android.opengl.EGL14 r0 = r13.mEgl
            android.opengl.EGLDisplay r0 = r13.mEglDisplay
            r2 = r3[r6]
            android.opengl.EGLContext r5 = android.opengl.EGL14.EGL_NO_CONTEXT
            android.opengl.EGLContext r0 = android.opengl.EGL14.eglCreateContext(r0, r2, r5, r1, r6)
            r13.mEglContext = r0
            android.opengl.EGLContext r0 = r13.mEglContext
            android.opengl.EGLContext r2 = android.opengl.EGL14.EGL_NO_CONTEXT
            if (r0 != r2) goto L_0x00a4
            r0 = r7
            goto L_0x000e
        L_0x00a2:
            r0 = r6
            goto L_0x007d
        L_0x00a4:
            java.lang.String r0 = "After creating dummy context for checking gl version"
            r13.checkEglError(r0)
            android.opengl.EGL14 r0 = r13.mEgl
            android.opengl.EGLDisplay r0 = r13.mEglDisplay
            r2 = r3[r6]
            int[] r5 = fbPBufferSurfaceAttrs
            android.opengl.EGLSurface r2 = android.opengl.EGL14.eglCreatePbufferSurface(r0, r2, r5, r6)
            java.lang.String r0 = "After eglCreatePbufferSurface for checking gl version"
            r13.checkEglError(r0)
            android.opengl.EGLSurface r0 = android.opengl.EGL14.EGL_NO_SURFACE
            if (r2 != r0) goto L_0x00c1
            r0 = r7
            goto L_0x000e
        L_0x00c1:
            android.opengl.EGL14 r0 = r13.mEgl
            android.opengl.EGLDisplay r0 = r13.mEglDisplay
            android.opengl.EGLContext r5 = r13.mEglContext
            android.opengl.EGL14.eglMakeCurrent(r0, r2, r2, r5)
            java.lang.String r0 = "After eglMakeCurrent for checking gl version"
            r13.checkEglError(r0)
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
            if (r0 < r12) goto L_0x01a9
            r0 = r4
        L_0x00f4:
            android.opengl.EGL14 r5 = r13.mEgl
            android.opengl.EGLDisplay r5 = r13.mEglDisplay
            android.opengl.EGLSurface r8 = android.opengl.EGL14.EGL_NO_SURFACE
            android.opengl.EGLSurface r9 = android.opengl.EGL14.EGL_NO_SURFACE
            android.opengl.EGLContext r10 = android.opengl.EGL14.EGL_NO_CONTEXT
            android.opengl.EGL14.eglMakeCurrent(r5, r8, r9, r10)
            android.opengl.EGL14 r5 = r13.mEgl
            android.opengl.EGLDisplay r5 = r13.mEglDisplay
            android.opengl.EGL14.eglDestroySurface(r5, r2)
            android.opengl.EGL14 r2 = r13.mEgl
            android.opengl.EGLDisplay r2 = r13.mEglDisplay
            android.opengl.EGLContext r5 = r13.mEglContext
            android.opengl.EGL14.eglDestroyContext(r2, r5)
            android.opengl.EGLContext r2 = android.opengl.EGL14.EGL_NO_CONTEXT
            r13.mEglContext = r2
        L_0x0115:
            if (r0 == 0) goto L_0x0139
            r1[r4] = r12
            java.lang.String r0 = "Before eglCreateContext es3"
            r13.checkEglError(r0)
            android.opengl.EGL14 r0 = r13.mEgl
            android.opengl.EGLDisplay r0 = r13.mEglDisplay
            r2 = r3[r6]
            android.opengl.EGLContext r5 = android.opengl.EGL14.EGL_NO_CONTEXT
            android.opengl.EGLContext r0 = android.opengl.EGL14.eglCreateContext(r0, r2, r5, r1, r6)
            r13.mEglContext = r0
            java.lang.String r0 = "After eglCreateContext es3"
            r13.checkEglError(r0)
            android.opengl.EGLContext r0 = r13.mEglContext
            android.opengl.EGLContext r2 = android.opengl.EGL14.EGL_NO_CONTEXT
            if (r0 == r2) goto L_0x0139
            r13.mIsES3Device = r4
        L_0x0139:
            android.opengl.EGLContext r0 = r13.mEglContext
            android.opengl.EGLContext r2 = android.opengl.EGL14.EGL_NO_CONTEXT
            if (r0 != r2) goto L_0x016d
            r1[r4] = r11
            java.lang.String r0 = "Before eglCreateContext es2"
            r13.checkEglError(r0)
            android.opengl.EGL14 r0 = r13.mEgl
            android.opengl.EGLDisplay r0 = r13.mEglDisplay
            r2 = r3[r6]
            android.opengl.EGLContext r4 = android.opengl.EGL14.EGL_NO_CONTEXT
            android.opengl.EGLContext r0 = android.opengl.EGL14.eglCreateContext(r0, r2, r4, r1, r6)
            r13.mEglContext = r0
            java.lang.String r0 = "After eglCreateContext es2"
            r13.checkEglError(r0)
            android.opengl.EGLContext r0 = r13.mEglContext
            android.opengl.EGLContext r1 = android.opengl.EGL14.EGL_NO_CONTEXT
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
            r13.checkEglError(r0)
            android.opengl.EGL14 r0 = r13.mEgl
            android.opengl.EGLDisplay r0 = r13.mEglDisplay
            r1 = r3[r6]
            int[] r2 = fbPBufferSurfaceAttrs
            android.opengl.EGLSurface r0 = android.opengl.EGL14.eglCreatePbufferSurface(r0, r1, r2, r6)
            r13.mEglPbufferSurface = r0
            java.lang.String r0 = "After eglCreatePbufferSurface"
            r13.checkEglError(r0)
            android.opengl.EGLSurface r0 = r13.mEglPbufferSurface
            android.opengl.EGLSurface r1 = android.opengl.EGL14.EGL_NO_SURFACE
            if (r0 != r1) goto L_0x018e
            r0 = r7
            goto L_0x000e
        L_0x018e:
            java.lang.String r0 = "Before eglMakeCurrent"
            r13.checkEglError(r0)
            android.opengl.EGL14 r0 = r13.mEgl
            android.opengl.EGLDisplay r0 = r13.mEglDisplay
            android.opengl.EGLSurface r1 = r13.mEglPbufferSurface
            android.opengl.EGLSurface r2 = r13.mEglPbufferSurface
            android.opengl.EGLContext r3 = r13.mEglContext
            android.opengl.EGL14.eglMakeCurrent(r0, r1, r2, r3)
            java.lang.String r0 = "After eglMakeCurrent"
            r13.checkEglError(r0)
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
        throw new UnsupportedOperationException("Method not decompiled: com.adobe.air.FlashEGL14.CreateDummySurfaceAndContext():int");
    }

    public int InitEGL() {
        if (this.mEglContext != EGL14.EGL_NO_CONTEXT) {
            return 12288;
        }
        this.mEgl = new EGL14();
        checkEglError("Before eglGetDisplay");
        EGL14 egl14 = this.mEgl;
        this.mEglDisplay = EGL14.eglGetDisplay(0);
        int checkEglError = checkEglError("After eglGetDisplay");
        if (12288 != checkEglError) {
            return checkEglError;
        }
        this.mEglVersion = new int[2];
        checkEglError("Before eglInitialize");
        EGL14 egl142 = this.mEgl;
        EGL14.eglInitialize(this.mEglDisplay, this.mEglVersion, 0, this.mEglVersion, 1);
        int checkEglError2 = checkEglError("After eglInitialize");
        if (12288 != checkEglError2) {
            return checkEglError2;
        }
        return 12288;
    }

    public boolean DestroyGLContext() {
        if (this.mEglContext == EGL14.EGL_NO_CONTEXT || this.mEglDisplay == EGL14.EGL_NO_DISPLAY) {
            return false;
        }
        checkEglError("DestroyGLContext: Before eglMakeCurrent for noSurface");
        EGL14 egl14 = this.mEgl;
        EGL14.eglMakeCurrent(this.mEglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
        checkEglError("DestroyGLContext: After eglMakeCurrent");
        if (this.mEglPbufferSurface != EGL14.EGL_NO_SURFACE) {
            EGL14 egl142 = this.mEgl;
            EGL14.eglDestroySurface(this.mEglDisplay, this.mEglPbufferSurface);
            this.mEglPbufferSurface = EGL14.EGL_NO_SURFACE;
        }
        checkEglError("Before eglDestroyContext");
        EGL14 egl143 = this.mEgl;
        boolean eglDestroyContext = EGL14.eglDestroyContext(this.mEglDisplay, this.mEglContext);
        checkEglError("After eglDestroyContext");
        this.mEglContext = EGL14.EGL_NO_CONTEXT;
        return eglDestroyContext;
    }

    public int CreateGLContext(boolean z) {
        if (this.mEglConfig == null) {
            return 12293;
        }
        if (this.mEglContext != EGL14.EGL_NO_CONTEXT && !z) {
            return 12288;
        }
        int[] iArr = {EGL_CONTEXT_CLIENT_VERSION, this.mIsES3Device ? 3 : 2, 12344};
        if (z) {
            EGLContext eGLContext = this.mEglContext;
            checkEglError("Before eglCreateContext");
            EGL14 egl14 = this.mEgl;
            this.mEglContext = EGL14.eglCreateContext(this.mEglDisplay, this.mEglConfig, eGLContext, iArr, 0);
            checkEglError("After eglCreateContext");
            EGL14 egl142 = this.mEgl;
            EGL14.eglDestroyContext(this.mEglDisplay, eGLContext);
            checkEglError("After eglDestroyContext");
        } else {
            checkEglError("Before eglCreateContext");
            EGL14 egl143 = this.mEgl;
            this.mEglContext = EGL14.eglCreateContext(this.mEglDisplay, this.mEglConfig, EGL14.EGL_NO_CONTEXT, iArr, 0);
            checkEglError("After eglCreateContext");
        }
        if (this.mEglContext == EGL14.EGL_NO_CONTEXT) {
            return 12294;
        }
        if (EGL14.EGL_NO_SURFACE == this.mEglPbufferSurface) {
            checkEglError("Before eglCreatePbufferSurface");
            EGL14 egl144 = this.mEgl;
            this.mEglPbufferSurface = EGL14.eglCreatePbufferSurface(this.mEglDisplay, this.mEglConfig, fbPBufferSurfaceAttrs, 0);
            checkEglError("After eglCreatePbufferSurface");
        }
        return 12288;
    }

    public void TerminateEGL() {
        if (!(this.mEgl == null || this.mEglDisplay == EGL14.EGL_NO_DISPLAY)) {
            EGL14 egl14 = this.mEgl;
            EGL14.eglTerminate(this.mEglDisplay);
        }
        this.mEglDisplay = EGL14.EGL_NO_DISPLAY;
    }

    public void ReleaseGPUResources() {
        if (this.mEglContext != EGL14.EGL_NO_CONTEXT) {
            checkEglError("Before eglMakeCurrent");
            EGL14 egl14 = this.mEgl;
            EGL14.eglMakeCurrent(this.mEglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
            checkEglError("After eglMakeCurrent");
            synchronized (this.mEgl) {
                checkEglError("Before eglDestroySurface");
                if (this.mEglWindowSurface != EGL14.EGL_NO_SURFACE) {
                    EGL14 egl142 = this.mEgl;
                    EGL14.eglDestroySurface(this.mEglDisplay, this.mEglWindowSurface);
                    this.mEglWindowSurface = EGL14.EGL_NO_SURFACE;
                }
                checkEglError("After eglDestroySurface (window)");
            }
            if (this.mEglPbufferSurface != EGL14.EGL_NO_SURFACE) {
                checkEglError("Before eglDestroySurface (pbuffer)");
                EGL14 egl143 = this.mEgl;
                EGL14.eglDestroySurface(this.mEglDisplay, this.mEglPbufferSurface);
                checkEglError("After eglDestroySurface (pbuffer)");
                this.mEglPbufferSurface = EGL14.EGL_NO_SURFACE;
            }
            checkEglError("Before eglDestroyContext");
            EGL14 egl144 = this.mEgl;
            EGL14.eglDestroyContext(this.mEglDisplay, this.mEglContext);
            checkEglError("After eglDestroyContext");
            this.mEglContext = EGL14.EGL_NO_CONTEXT;
            this.mEglSurface = EGL14.EGL_NO_SURFACE;
        }
    }

    public void SwapEGLBuffers() {
        if (12288 == MakeGLCurrent()) {
            checkEglError("Before eglSwapBuffers");
            EGL14 egl14 = this.mEgl;
            EGL14.eglSwapBuffers(this.mEglDisplay, this.mEglSurface);
            checkEglError("After eglSwapBuffers");
        }
    }

    public int MakeGLCurrent() {
        if (this.mEglContext == EGL14.EGL_NO_CONTEXT) {
            return 12294;
        }
        if (this.mEglSurface == EGL14.EGL_NO_SURFACE) {
            return 12301;
        }
        if (this.mEglDisplay == EGL14.EGL_NO_DISPLAY) {
            return 12296;
        }
        checkEglError("Before eglMakeCurrent");
        EGL14 egl14 = this.mEgl;
        EGL14.eglMakeCurrent(this.mEglDisplay, this.mEglSurface, this.mEglSurface, this.mEglContext);
        return checkEglError("After eglMakeCurrent");
    }

    public int CreateWindowSurface(SurfaceView surfaceView, int i) {
        boolean z;
        boolean z2;
        boolean z3 = true;
        if (this.mIsGPUOOM) {
            return 12291;
        }
        boolean z4 = surfaceView instanceof AIRWindowSurfaceView;
        if (!(surfaceView instanceof VideoViewAIR) && !(surfaceView instanceof AIRStage3DSurfaceView) && !z4) {
            return 12301;
        }
        if (this.mEglWindowSurface != EGL14.EGL_NO_SURFACE) {
            this.mEglSurface = this.mEglWindowSurface;
            return MakeGLCurrent();
        }
        if (i == this.kSwapPreserveOn) {
            checkEglError("Before eglCreateWindowSurface");
            EGL14 egl14 = this.mEgl;
            this.mEglWindowSurface = EGL14.eglCreateWindowSurface(this.mEglDisplay, this.mEglConfig, surfaceView.getHolder(), fbWindowSurfaceOnAttrs, 0);
            if (this.mEglWindowSurface == EGL14.EGL_NO_SURFACE) {
                checkEglError("After eglCreateWindowSurface");
                z = false;
            }
            z = true;
        } else {
            if (i == this.kSwapPreserveOff) {
                checkEglError("Before eglCreateWindowSurface");
                EGL14 egl142 = this.mEgl;
                this.mEglWindowSurface = EGL14.eglCreateWindowSurface(this.mEglDisplay, this.mEglConfig, surfaceView.getHolder(), fbWindowSurfaceOffAttrs, 0);
                if (this.mEglWindowSurface == EGL14.EGL_NO_SURFACE) {
                    checkEglError("After eglCreateWindowSurface");
                    z = false;
                }
            }
            z = true;
        }
        if (this.mEglWindowSurface == EGL14.EGL_NO_SURFACE) {
            checkEglError("Before eglCreateWindowSurface");
            EGL14 egl143 = this.mEgl;
            this.mEglWindowSurface = EGL14.eglCreateWindowSurface(this.mEglDisplay, this.mEglConfig, surfaceView.getHolder(), fbWindowSurfaceDefAttrs, 0);
            int checkEglError = checkEglError("After eglCreateWindowSurface");
            if (checkEglError != 12288) {
                return checkEglError;
            }
            z2 = true;
        } else {
            z2 = z;
        }
        if (this.mEglWindowSurface == EGL14.EGL_NO_SURFACE) {
            return 12301;
        }
        this.mEglSurface = this.mEglWindowSurface;
        if (z4) {
            ((AIRWindowSurfaceView) surfaceView).setFlashEGL(this);
            Activity activity = ((AIRWindowSurfaceView) surfaceView).getActivityWrapper().getActivity();
            if (activity != null) {
                activity.getWindow().setSoftInputMode(34);
            }
        }
        int[] iArr = {0};
        this.mIsBufferPreserve = false;
        if (z2) {
            EGL14 egl144 = this.mEgl;
            if (EGL14.eglQuerySurface(this.mEglDisplay, this.mEglSurface, EGL_SWAP_BEHAVIOR, iArr, 0)) {
                if (iArr[0] != EGL_BUFFER_PRESERVED) {
                    z3 = false;
                }
                this.mIsBufferPreserve = z3;
            }
        }
        return MakeGLCurrent();
    }

    public boolean DestroyWindowSurface() {
        if (this.mEglWindowSurface == EGL14.EGL_NO_SURFACE) {
            return false;
        }
        checkEglError("Before eglMakeCurrent");
        EGL14 egl14 = this.mEgl;
        EGL14.eglMakeCurrent(this.mEglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
        if (12288 != checkEglError("After eglMakeCurrent")) {
            return false;
        }
        checkEglError("Before eglDestroySurface (window)");
        EGL14 egl142 = this.mEgl;
        EGL14.eglDestroySurface(this.mEglDisplay, this.mEglWindowSurface);
        if (12288 != checkEglError("After eglDestroySurface (window)")) {
            return false;
        }
        if (this.mEglSurface == this.mEglWindowSurface) {
            this.mEglSurface = EGL14.EGL_NO_SURFACE;
        }
        this.mEglWindowSurface = EGL14.EGL_NO_SURFACE;
        if (!(this.mEglPbufferSurface == EGL14.EGL_NO_SURFACE || this.mEglContext == EGL14.EGL_NO_CONTEXT)) {
            this.mEglSurface = this.mEglPbufferSurface;
            EGL14 egl143 = this.mEgl;
            EGL14.eglMakeCurrent(this.mEglDisplay, this.mEglSurface, this.mEglSurface, this.mEglContext);
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
        EGL14 egl14 = this.mEgl;
        int eglGetError = EGL14.eglGetError();
        if (eglGetError != 12288 && !this.mIsGPUOOM && eglGetError == 12291) {
            if (this.mEglWindowSurface != EGL14.EGL_NO_SURFACE) {
                EGL14 egl142 = this.mEgl;
                EGL14.eglDestroySurface(this.mEglDisplay, this.mEglWindowSurface);
                EGL14 egl143 = this.mEgl;
                int eglGetError2 = EGL14.eglGetError();
                this.mEglWindowSurface = EGL14.EGL_NO_SURFACE;
                this.mEglSurface = EGL14.EGL_NO_SURFACE;
                if (eglGetError2 != 12288) {
                }
                this.mEglWindowSurface = EGL14.EGL_NO_SURFACE;
                EGL14 egl144 = this.mEgl;
                EGL14.eglMakeCurrent(this.mEglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
                EGL14 egl145 = this.mEgl;
                if (EGL14.eglGetError() != 12288) {
                }
            }
            if (!(this.mEglPbufferSurface == EGL14.EGL_NO_SURFACE || this.mEglContext == EGL14.EGL_NO_CONTEXT)) {
                this.mEglSurface = this.mEglPbufferSurface;
                EGL14 egl146 = this.mEgl;
                EGL14.eglMakeCurrent(this.mEglDisplay, this.mEglSurface, this.mEglSurface, this.mEglContext);
                EGL14 egl147 = this.mEgl;
                if (EGL14.eglGetError() != 12288) {
                }
            }
            this.mIsGPUOOM = true;
        }
        return eglGetError;
    }
}
