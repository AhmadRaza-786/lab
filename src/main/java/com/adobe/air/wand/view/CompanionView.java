package com.adobe.air.wand.view;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class CompanionView extends View {
    private static final String LOG_TAG = "CompanionView";
    static final int POST_TOUCH_MESSAGE_AFTER_DELAY = 0;
    public static final int kTouchActionBegin = 2;
    public static final int kTouchActionEnd = 4;
    public static final int kTouchActionMove = 1;
    public static final int kTouchMetaStateIsEraser = 67108864;
    public static final int kTouchMetaStateIsPen = 33554432;
    public static final int kTouchMetaStateMask = 234881024;
    public static final int kTouchMetaStateSideButton1 = 134217728;
    public final int kMultitouchGesture = 2;
    public final int kMultitouchNone = 0;
    public final int kMultitouchRaw = 1;
    private int mBoundHeight = 0;
    private int mBoundWidth = 0;
    private int mCurrentOrientation = 0;
    private GestureDetector mGestureDetector;
    private GestureListener mGestureListener;
    private boolean mIsFullScreen = false;
    private int mMultitouchMode = 2;
    private ScaleGestureDetector mScaleGestureDetector;
    private int mSkipHeightFromTop = 0;
    private TouchSensor mTouchSensor = null;
    private int mVisibleBoundHeight = 0;
    private int mVisibleBoundWidth = 0;

    public CompanionView(Context context) {
        super(context);
        initView(context);
    }

    public CompanionView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(context);
    }

    private void initView(Context context) {
        this.mTouchSensor = new TouchSensor();
        this.mGestureListener = new GestureListener(context, this);
        this.mGestureDetector = new GestureDetector(context, this.mGestureListener, (Handler) null, false);
        this.mScaleGestureDetector = new ScaleGestureDetector(context, this.mGestureListener);
        setWillNotDraw(false);
        setClickable(true);
        setEnabled(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public View returnThis() {
        return this;
    }

    public void onWindowFocusChanged(boolean z) {
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return false;
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onFocusChanged(boolean z, int i, Rect rect) {
        super.onFocusChanged(z, i, rect);
    }

    /* JADX WARNING: Removed duplicated region for block: B:101:0x01dc A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x00cf  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00db  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x015a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r20) {
        /*
            r19 = this;
            r13 = 1
            int r1 = r20.getAction()
            r2 = r1 & 255(0xff, float:3.57E-43)
            r0 = r19
            com.adobe.air.wand.view.GestureListener r1 = r0.mGestureListener
            if (r1 == 0) goto L_0x0081
            r1 = 5
            if (r2 == r1) goto L_0x0012
            if (r2 != 0) goto L_0x0035
        L_0x0012:
            r1 = 0
        L_0x0013:
            int r3 = r20.getPointerCount()
            if (r1 >= r3) goto L_0x0035
            r0 = r20
            int r3 = r0.getPointerId(r1)
            r0 = r19
            com.adobe.air.wand.view.GestureListener r4 = r0.mGestureListener
            r0 = r20
            float r5 = r0.getX(r1)
            r0 = r20
            float r6 = r0.getY(r1)
            r4.setDownTouchPoint(r5, r6, r3)
            int r1 = r1 + 1
            goto L_0x0013
        L_0x0035:
            if (r2 != 0) goto L_0x003e
            r0 = r19
            com.adobe.air.wand.view.GestureListener r1 = r0.mGestureListener
            r1.mayStartNewTransformGesture()
        L_0x003e:
            r1 = 5
            if (r2 != r1) goto L_0x0115
            r0 = r19
            com.adobe.air.wand.view.GestureListener r1 = r0.mGestureListener
            r2 = 1
            r1.setCouldBeTwoFingerTap(r2)
            r0 = r19
            com.adobe.air.wand.view.GestureListener r1 = r0.mGestureListener
            r2 = 0
            r0 = r20
            float r2 = r0.getX(r2)
            r3 = 0
            r0 = r20
            float r3 = r0.getY(r3)
            r4 = 0
            r0 = r20
            int r4 = r0.getPointerId(r4)
            r1.setPrimaryPointOfTwoFingerTap(r2, r3, r4)
            r0 = r19
            com.adobe.air.wand.view.GestureListener r1 = r0.mGestureListener
            r2 = 1
            r0 = r20
            float r2 = r0.getX(r2)
            r3 = 1
            r0 = r20
            float r3 = r0.getY(r3)
            r4 = 1
            r0 = r20
            int r4 = r0.getPointerId(r4)
            r1.setSecondaryPointOfTwoFingerTap(r2, r3, r4)
        L_0x0081:
            int r15 = r20.getPointerCount()
            r2 = 0
            r1 = 0
            r14 = r1
        L_0x0088:
            if (r14 >= r15) goto L_0x01e4
            r0 = r20
            float r3 = r0.getX(r14)
            r0 = r20
            float r1 = r0.getY(r14)
            r0 = r19
            int r4 = r0.mSkipHeightFromTop
            float r4 = (float) r4
            float r4 = r4 + r1
            r7 = 1
            int r5 = r20.getAction()
            r0 = r20
            int r6 = r0.getPointerId(r14)
            int r1 = r20.getMetaState()
            int r8 = android.os.Build.VERSION.SDK_INT
            r9 = 14
            if (r8 < r9) goto L_0x0222
            r8 = -234881025(0xfffffffff1ffffff, float:-2.535301E30)
            r1 = r1 & r8
            r0 = r20
            int r8 = r0.getToolType(r14)
            r9 = 4
            if (r8 != r9) goto L_0x0152
            r8 = 67108864(0x4000000, float:1.5046328E-36)
            r1 = r1 | r8
        L_0x00c1:
            int r8 = r20.getButtonState()
            r8 = r8 & 2
            if (r8 == 0) goto L_0x0222
            r8 = 134217728(0x8000000, float:3.85186E-34)
            r1 = r1 | r8
            r10 = r1
        L_0x00cd:
            if (r2 == 0) goto L_0x015a
            r1 = 3
            r5 = 4
            r12 = r2
            r2 = r5
        L_0x00d3:
            r0 = r19
            boolean r5 = r0.IsTouchEventHandlingAllowed(r2, r3, r4)
            if (r5 == 0) goto L_0x01dc
            r0 = r20
            float r7 = r0.getSize(r14)
            if (r6 != 0) goto L_0x019b
            r9 = 1
        L_0x00e4:
            int r11 = r20.getHistorySize()
            int r5 = r11 + 1
            int r5 = r5 * 3
            float[] r0 = new float[r5]
            r16 = r0
            r8 = 0
            r5 = 0
        L_0x00f2:
            if (r5 >= r11) goto L_0x019e
            int r17 = r8 + 1
            r0 = r20
            float r18 = r0.getHistoricalX(r14, r5)
            r16[r8] = r18
            int r18 = r17 + 1
            r0 = r20
            float r8 = r0.getHistoricalY(r14, r5)
            r16[r17] = r8
            int r8 = r18 + 1
            r0 = r20
            float r17 = r0.getHistoricalPressure(r14, r5)
            r16[r18] = r17
            int r5 = r5 + 1
            goto L_0x00f2
        L_0x0115:
            r1 = 6
            if (r2 != r1) goto L_0x012d
            r0 = r19
            com.adobe.air.wand.view.GestureListener r1 = r0.mGestureListener
            int r1 = r1.getCouldBeTwoFingerTap()
            r3 = 1
            if (r1 != r3) goto L_0x012d
            r0 = r19
            com.adobe.air.wand.view.GestureListener r1 = r0.mGestureListener
            r2 = 2
            r1.setCouldBeTwoFingerTap(r2)
            goto L_0x0081
        L_0x012d:
            r1 = 1
            if (r2 != r1) goto L_0x0145
            r0 = r19
            com.adobe.air.wand.view.GestureListener r1 = r0.mGestureListener
            int r1 = r1.getCouldBeTwoFingerTap()
            r3 = 2
            if (r1 != r3) goto L_0x0145
            r0 = r19
            com.adobe.air.wand.view.GestureListener r1 = r0.mGestureListener
            r2 = 3
            r1.setCouldBeTwoFingerTap(r2)
            goto L_0x0081
        L_0x0145:
            r1 = 2
            if (r2 == r1) goto L_0x0081
            r0 = r19
            com.adobe.air.wand.view.GestureListener r1 = r0.mGestureListener
            r2 = 0
            r1.setCouldBeTwoFingerTap(r2)
            goto L_0x0081
        L_0x0152:
            r9 = 2
            if (r8 != r9) goto L_0x00c1
            r8 = 33554432(0x2000000, float:9.403955E-38)
            r1 = r1 | r8
            goto L_0x00c1
        L_0x015a:
            int r1 = r20.getPointerCount()
            r8 = 1
            if (r1 == r8) goto L_0x016f
            r1 = 65280(0xff00, float:9.1477E-41)
            r1 = r1 & r5
            int r1 = r1 >> 8
            r0 = r20
            int r1 = r0.getPointerId(r1)
            if (r6 != r1) goto L_0x021d
        L_0x016f:
            r5 = r5 & 255(0xff, float:3.57E-43)
            switch(r5) {
                case 0: goto L_0x017a;
                case 1: goto L_0x021a;
                case 2: goto L_0x0174;
                case 3: goto L_0x0180;
                case 4: goto L_0x0174;
                case 5: goto L_0x017a;
                case 6: goto L_0x021a;
                default: goto L_0x0174;
            }
        L_0x0174:
            r1 = 1
            r12 = r2
            r2 = r1
            r1 = r5
            goto L_0x00d3
        L_0x017a:
            r1 = 2
            r12 = r2
            r2 = r1
            r1 = r5
            goto L_0x00d3
        L_0x0180:
            r1 = 1
        L_0x0181:
            r2 = 4
            r0 = r19
            com.adobe.air.wand.view.GestureListener r7 = r0.mGestureListener
            if (r7 == 0) goto L_0x0216
            r0 = r19
            com.adobe.air.wand.view.GestureListener r7 = r0.mGestureListener
            r7.endTwoFingerGesture()
            r0 = r19
            com.adobe.air.wand.view.GestureListener r7 = r0.mGestureListener
            r8 = 1
            r7.setCheckForSwipe(r8)
            r12 = r1
            r1 = r5
            goto L_0x00d3
        L_0x019b:
            r9 = 0
            goto L_0x00e4
        L_0x019e:
            r0 = r20
            float r5 = r0.getPressure(r14)
            r16[r8] = r3
            int r11 = r8 + 1
            r16[r11] = r4
            int r8 = r8 + 2
            r16[r8] = r5
            r11 = r10 & -2
            r8 = 1
            if (r2 == r8) goto L_0x01c6
            r8 = 3
            if (r1 != r8) goto L_0x01b8
            r11 = r11 | 1
        L_0x01b8:
            com.adobe.air.TouchEventData r1 = new com.adobe.air.TouchEventData
            r10 = 0
            r8 = r7
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            r0 = r19
            com.adobe.air.wand.view.TouchSensor r8 = r0.mTouchSensor
            r8.dispatchEvent((com.adobe.air.TouchEventData) r1)
        L_0x01c6:
            r1 = 0
            switch(r2) {
                case 1: goto L_0x01e2;
                case 2: goto L_0x01e2;
                default: goto L_0x01ca;
            }
        L_0x01ca:
            r2 = r1
        L_0x01cb:
            if (r2 == 0) goto L_0x01dc
            com.adobe.air.TouchEventData r1 = new com.adobe.air.TouchEventData
            r8 = r7
            r10 = r16
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            r0 = r19
            com.adobe.air.wand.view.TouchSensor r2 = r0.mTouchSensor
            r2.dispatchEvent((com.adobe.air.TouchEventData) r1)
        L_0x01dc:
            int r1 = r14 + 1
            r14 = r1
            r2 = r12
            goto L_0x0088
        L_0x01e2:
            r2 = 1
            goto L_0x01cb
        L_0x01e4:
            r0 = r19
            android.view.ScaleGestureDetector r1 = r0.mScaleGestureDetector
            if (r1 == 0) goto L_0x0214
            r0 = r19
            android.view.ScaleGestureDetector r1 = r0.mScaleGestureDetector     // Catch:{ Exception -> 0x020f }
            r0 = r20
            boolean r1 = r1.onTouchEvent(r0)     // Catch:{ Exception -> 0x020f }
            if (r1 == 0) goto L_0x020d
            r1 = 1
        L_0x01f7:
            r0 = r19
            android.view.GestureDetector r2 = r0.mGestureDetector
            if (r2 == 0) goto L_0x020c
            if (r1 == 0) goto L_0x0212
            r0 = r19
            android.view.GestureDetector r1 = r0.mGestureDetector
            r0 = r20
            boolean r1 = r1.onTouchEvent(r0)
            if (r1 == 0) goto L_0x0212
            r1 = 1
        L_0x020c:
            return r1
        L_0x020d:
            r1 = 0
            goto L_0x01f7
        L_0x020f:
            r1 = move-exception
            r1 = r13
            goto L_0x01f7
        L_0x0212:
            r1 = 0
            goto L_0x020c
        L_0x0214:
            r1 = r13
            goto L_0x01f7
        L_0x0216:
            r12 = r1
            r1 = r5
            goto L_0x00d3
        L_0x021a:
            r1 = r2
            goto L_0x0181
        L_0x021d:
            r1 = r5
            r12 = r2
            r2 = r7
            goto L_0x00d3
        L_0x0222:
            r10 = r1
            goto L_0x00cd
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adobe.air.wand.view.CompanionView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void onGestureListener(int i, int i2, boolean z, float f, float f2, float f3, float f4, float f5, float f6, float f7) {
        this.mTouchSensor.dispatchEvent(new GestureEventData(i, i2, z, f, f2, f3, f4, f5, f6, f7));
    }

    public TouchSensor getTouchSensor() {
        return this.mTouchSensor;
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
    }

    public void setMultitouchMode(int i) {
        this.mMultitouchMode = i;
    }

    public int getMultitouchMode() {
        return this.mMultitouchMode;
    }

    public boolean getIsFullScreen() {
        return this.mIsFullScreen;
    }

    public int getBoundWidth() {
        return this.mBoundWidth;
    }

    public int getBoundHeight() {
        return this.mBoundHeight;
    }

    public int getVisibleBoundWidth() {
        return this.mVisibleBoundWidth;
    }

    public int getVisibleBoundHeight() {
        return this.mVisibleBoundHeight;
    }

    public boolean IsLandScape() {
        return this.mCurrentOrientation == 2;
    }

    private boolean IsTouchEventHandlingAllowed(int i, float f, float f2) {
        return true;
    }

    public boolean IsTouchUpHandlingAllowed() {
        return true;
    }
}
