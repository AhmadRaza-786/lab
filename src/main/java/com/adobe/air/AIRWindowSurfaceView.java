package com.adobe.air;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.v7.appcompat.R;
import android.text.ClipboardManager;
import android.text.util.Linkify;
import android.view.ContextMenu;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.adobe.air.AndroidLocale;
import com.adobe.air.gestures.AIRGestureListener;
import com.adobe.air.utils.AIRLogger;
import com.adobe.air.utils.Utils;
import com.adobe.flashruntime.air.VideoViewAIR;
import com.adobe.flashruntime.shared.VideoView;

public class AIRWindowSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    static final int CURSOR_POS_END_DOCUMENT = 3;
    static final int CURSOR_POS_END_LINE = 1;
    static final int CURSOR_POS_START_DOCUMENT = 2;
    static final int CURSOR_POS_START_LINE = 0;
    static final int ID_COPY = 3;
    static final int ID_COPY_ALL = 4;
    static final int ID_CUT = 1;
    static final int ID_CUT_ALL = 2;
    static final int ID_PASTE = 5;
    static final int ID_SELECT_ALL = 0;
    static final int ID_START_SELECTING = 7;
    static final int ID_STOP_SELECTING = 8;
    static final int ID_SWITCH_INPUT_METHOD = 6;
    private static final String LOG_TAG = "AIRWindowSurfaceView";
    static final int LONG_PRESS_DELAY = 500;
    static final int MAX_MOVE_ACTION_ALLOWED = 4;
    static final int POST_TOUCH_MESSAGE_AFTER_DELAY = 0;
    private static final int kDefaultBitsPerPixel = 32;
    private static final int kMotionEventButtonSecondary = 2;
    private static final int kMotionEventToolTypeEraser = 4;
    private static final int kMotionEventToolTypeStylus = 2;
    private static final int kTouchActionBegin = 2;
    private static final int kTouchActionEnd = 4;
    private static final int kTouchActionHoverBegin = 16;
    private static final int kTouchActionHoverEnd = 32;
    private static final int kTouchActionHoverMove = 8;
    private static final int kTouchActionMove = 1;
    private static final int kTouchMetaStateIsEraser = 67108864;
    private static final int kTouchMetaStateIsPen = 33554432;
    private static final int kTouchMetaStateMask = 234881024;
    private static final int kTouchMetaStateSideButton1 = 134217728;
    private boolean inTouch;
    public final int kMultitouchGesture;
    public final int kMultitouchNone;
    public final int kMultitouchRaw;
    private final float kSampleSize;
    private AndroidActivityWrapper mActivityWrapper;
    private int mBoundHeight;
    private int mBoundWidth;
    private boolean mContextMenuVisible;
    private int mCurrentOrientation;
    /* access modifiers changed from: private */
    public int mCurrentSurfaceFormat;
    private boolean mDispatchUserTriggeredSkDeactivate;
    private float mDownX;
    private float mDownY;
    private boolean mEatTouchRelease;
    protected FlashEGL mFlashEGL;
    private AndroidStageText mFocusedStageText;
    private AndroidWebView mFocusedWebView;
    private GestureDetector mGestureDetector;
    private AIRGestureListener mGestureListener;
    private boolean mHideSoftKeyboardOnWindowFocusChange;
    /* access modifiers changed from: private */
    public boolean mHoverInProgress;
    /* access modifiers changed from: private */
    public int mHoverMetaState;
    /* access modifiers changed from: private */
    public HoverTimeoutHandler mHoverTimeoutHandler;
    private int mHt;
    private AndroidInputConnection mInputConnection;
    InputMethodReceiver mInputMethodReceiver;
    private boolean mIsFullScreen;
    /* access modifiers changed from: private */
    public float mLastTouchedXCoord;
    /* access modifiers changed from: private */
    public float mLastTouchedYCoord;
    private CheckLongPress mLongPressCheck;
    private boolean mMaliWorkaround;
    private MetaKeyState mMetaAltState;
    private MetaKeyState mMetaShiftState;
    private int mMultitouchMode;
    private boolean mNeedsCompositingSurface;
    private Paint mPaint;
    private Paint mPaintScaled;
    /* access modifiers changed from: private */
    public AndroidStageText mResizedStageText;
    /* access modifiers changed from: private */
    public AndroidWebView mResizedWebView;
    private ScaleGestureDetector mScaleGestureDetector;
    private int mScaledTouchSlop;
    private int mSkipHeightFromTop;
    private boolean mSurfaceChangedForSoftKeyboard;
    protected SurfaceHolder mSurfaceHolder;
    private boolean mSurfaceValid;
    private Rect mTextBoxBounds;
    private boolean mTrackBallPressed;
    private VideoView mVideoView;
    private int mVisibleBoundHeight;
    private int mVisibleBoundWidth;
    private int mWd;
    private boolean mWindowHasFocus;

    private enum MetaKeyState {
        INACTIVE,
        ACTIVE,
        PRESSED,
        LOCKED
    }

    private native void nativeCutText(boolean z);

    private native void nativeDeleteTextLine();

    /* access modifiers changed from: private */
    public native void nativeDispatchFullScreenEvent(boolean z);

    private native void nativeDispatchSelectionChangeEvent(boolean z);

    private native void nativeForceReDraw();

    private native int nativeGetMultitouchMode();

    private native String nativeGetSelectedText();

    private native int nativeGetSoftKeyboardType();

    private native Rect nativeGetTextBoxBounds();

    private native void nativeInsertText(String str);

    private native boolean nativeIsEditable();

    private native boolean nativeIsFullScreenInteractive();

    private native boolean nativeIsMultiLineTextField();

    private native boolean nativeIsPasswordField();

    private native boolean nativeIsTextFieldInSelectionMode();

    private native boolean nativeIsTextFieldSelectable();

    private native void nativeMoveCursor(int i);

    private native void nativeOnFormatChangeListener(int i);

    private native void nativeOnSizeChangedListener(int i, int i2, boolean z);

    private native boolean nativePerformWindowPanning(int i, int i2);

    private native void nativeSelectAllText();

    /* access modifiers changed from: private */
    public native void nativeSetKeyboardVisible(boolean z);

    private native void nativeSurfaceCreated();

    public native void nativeDispatchUserTriggeredSkDeactivateEvent();

    public native ExtractedText nativeGetTextContent();

    public native int nativeGetTextContentLength();

    public native boolean nativeIsTextSelected();

    public native boolean nativeOnDoubleClickListener(float f, float f2);

    public native boolean nativeOnKeyListener(int i, int i2, int i3, boolean z, boolean z2, boolean z3);

    public native void nativeShowOriginalRect();

    class HoverTimeoutHandler extends Handler {
        static final int INTERVAL = 500;
        private AIRWindowSurfaceView mAIRWindowSurfaceView;
        private long mLastMove;

        public HoverTimeoutHandler(AIRWindowSurfaceView aIRWindowSurfaceView) {
            this.mAIRWindowSurfaceView = aIRWindowSurfaceView;
        }

        public void handleMessage(Message message) {
            if (System.currentTimeMillis() - this.mLastMove >= 500) {
                boolean unused = AIRWindowSurfaceView.this.mHoverInProgress = false;
                Entrypoints.registerTouchCallback(0, new TouchEventData(32, this.mAIRWindowSurfaceView.mLastTouchedXCoord, this.mAIRWindowSurfaceView.mLastTouchedYCoord, 0.0f, 0, 0.0f, 0.0f, true, (float[]) null, this.mAIRWindowSurfaceView.mHoverMetaState), (Handler) null);
                return;
            }
            AIRWindowSurfaceView.this.mHoverTimeoutHandler.sendEmptyMessageDelayed(0, 500);
        }

        public void setLastMove(long j) {
            this.mLastMove = j;
        }
    }

    public AIRWindowSurfaceView(Context context, AndroidActivityWrapper androidActivityWrapper) {
        super(context);
        this.kMultitouchNone = 0;
        this.kMultitouchRaw = 1;
        this.kMultitouchGesture = 2;
        this.kSampleSize = 4.0f;
        this.mWd = 0;
        this.mHt = 0;
        this.mSurfaceValid = false;
        this.mSkipHeightFromTop = 0;
        this.mSurfaceHolder = null;
        this.mFlashEGL = null;
        this.mBoundHeight = 0;
        this.mBoundWidth = 0;
        this.mVisibleBoundWidth = 0;
        this.mVisibleBoundHeight = 0;
        this.mMultitouchMode = 0;
        this.mPaint = null;
        this.mPaintScaled = null;
        this.mMaliWorkaround = false;
        this.mHoverInProgress = false;
        this.mHoverMetaState = 0;
        this.mCurrentOrientation = 0;
        this.mEatTouchRelease = false;
        this.mContextMenuVisible = false;
        this.mLongPressCheck = null;
        this.mIsFullScreen = false;
        this.mSurfaceChangedForSoftKeyboard = false;
        this.mDispatchUserTriggeredSkDeactivate = true;
        this.mHideSoftKeyboardOnWindowFocusChange = false;
        this.mTrackBallPressed = false;
        this.mWindowHasFocus = true;
        this.mNeedsCompositingSurface = false;
        this.mCurrentSurfaceFormat = -1;
        this.mFocusedWebView = null;
        this.mResizedWebView = null;
        this.mFocusedStageText = null;
        this.mResizedStageText = null;
        this.inTouch = false;
        this.mMetaShiftState = MetaKeyState.INACTIVE;
        this.mMetaAltState = MetaKeyState.INACTIVE;
        this.mHoverTimeoutHandler = new HoverTimeoutHandler(this);
        this.mInputMethodReceiver = new InputMethodReceiver();
        this.mSurfaceHolder = getHolder();
        this.mActivityWrapper = androidActivityWrapper;
        setSurfaceFormat(false);
        this.mGestureListener = new AIRGestureListener(context, this);
        this.mGestureDetector = new GestureDetector(context, this.mGestureListener, (Handler) null, false);
        this.mScaleGestureDetector = new ScaleGestureDetector(context, this.mGestureListener);
        setWillNotDraw(false);
        setClickable(true);
        setEnabled(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.mSurfaceHolder.addCallback(this);
        setZOrderMediaOverlay(true);
        this.mActivityWrapper.registerPlane(this, 3);
    }

    public AndroidActivityWrapper getActivityWrapper() {
        return this.mActivityWrapper;
    }

    public View returnThis() {
        return this;
    }

    public void onWindowFocusChanged(boolean z) {
        this.mWindowHasFocus = z;
        if (this.mLongPressCheck != null) {
            removeCallbacks(this.mLongPressCheck);
        }
        if (this.mHideSoftKeyboardOnWindowFocusChange) {
            InputMethodManager inputMethodManager = getInputMethodManager();
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
            }
            this.mHideSoftKeyboardOnWindowFocusChange = false;
        }
        if (z) {
            this.mContextMenuVisible = false;
            if (this.mIsFullScreen && Build.VERSION.SDK_INT >= 19) {
                setSystemUiVisibility(5894);
            }
        }
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        boolean z = false;
        if (!AllowOSToHandleKeys(i)) {
            int unicodeChar = keyEvent.getUnicodeChar();
            if (this.mMetaShiftState == MetaKeyState.ACTIVE || this.mMetaShiftState == MetaKeyState.LOCKED || this.mMetaAltState == MetaKeyState.ACTIVE || this.mMetaAltState == MetaKeyState.LOCKED) {
                unicodeChar = GetMetaKeyCharacter(keyEvent);
            }
            HandleMetaKeyAction(keyEvent);
            if (!this.mTrackBallPressed && this.mLongPressCheck != null) {
                removeCallbacks(this.mLongPressCheck);
            }
            if (this.mActivityWrapper.isApplicationLaunched() && !HandleShortCuts(i, keyEvent)) {
                z = nativeOnKeyListener(keyEvent.getAction(), i, unicodeChar, keyEvent.isAltPressed(), keyEvent.isShiftPressed(), keyEvent.isSymPressed());
                if (this.mInputConnection != null) {
                    this.mInputConnection.updateIMEText();
                }
            }
        }
        return z;
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (AllowOSToHandleKeys(i)) {
            return false;
        }
        int unicodeChar = keyEvent.getUnicodeChar();
        if (this.mMetaShiftState == MetaKeyState.ACTIVE || this.mMetaShiftState == MetaKeyState.LOCKED || this.mMetaAltState == MetaKeyState.ACTIVE || this.mMetaAltState == MetaKeyState.LOCKED) {
            unicodeChar = GetMetaKeyCharacter(keyEvent);
        }
        if (this.mLongPressCheck != null) {
            removeCallbacks(this.mLongPressCheck);
        }
        if (i == 23) {
            this.mTrackBallPressed = false;
        }
        if (!this.mActivityWrapper.isApplicationLaunched()) {
            return false;
        }
        boolean nativeOnKeyListener = nativeOnKeyListener(keyEvent.getAction(), i, unicodeChar, keyEvent.isAltPressed(), keyEvent.isShiftPressed(), keyEvent.isSymPressed());
        if (this.mInputConnection != null) {
            this.mInputConnection.updateIMEText();
        }
        if (nativeOnKeyListener || keyEvent.getKeyCode() != 4 || !keyEvent.isTracking() || keyEvent.isCanceled()) {
            return nativeOnKeyListener;
        }
        this.mActivityWrapper.getActivity().moveTaskToBack(false);
        return true;
    }

    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 9 || motionEvent.getAction() == 10 || motionEvent.getAction() == 7) {
            return onTouchEvent(motionEvent);
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void onFocusChanged(boolean z, int i, Rect rect) {
        AIRLogger.d(LOG_TAG, "*** *** onFocusChanged: AIR");
        if (z && this.mFocusedStageText != null && !this.inTouch) {
            this.mDispatchUserTriggeredSkDeactivate = true;
            forceSoftKeyboardDown();
        }
        super.onFocusChanged(z, i, rect);
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:140:0x02fb, code lost:
        r2 = false;
        r13 = r8;
        r14 = r9;
        r15 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x01e4, code lost:
        if (r7 == r22.getPointerId((65280 & r8) >> 8)) goto L_0x01e6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x0247, code lost:
        r21.mGestureListener.endTwoFingerGesture();
        r21.mGestureListener.setCheckForSwipe(true);
        r13 = r8;
        r14 = 4;
        r15 = r2;
        r2 = false;
     */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x02e9  */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x02f4  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x013a  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0148  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x01d1  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r22) {
        /*
            r21 = this;
            r16 = 1
            r2 = 1
            r0 = r21
            r0.inTouch = r2
            int r2 = r22.getAction()
            r3 = r2 & 255(0xff, float:3.57E-43)
            r2 = 9
            if (r3 == r2) goto L_0x0018
            r2 = 10
            if (r3 == r2) goto L_0x0018
            r2 = 7
            if (r3 != r2) goto L_0x0056
        L_0x0018:
            r2 = 1
        L_0x0019:
            boolean r4 = r21.hasFocus()
            if (r4 != 0) goto L_0x0058
            if (r2 != 0) goto L_0x0058
            r0 = r21
            com.adobe.air.AndroidActivityWrapper r2 = r0.mActivityWrapper
            r4 = 0
            android.widget.RelativeLayout r4 = r2.getOverlaysLayout(r4)
            if (r4 == 0) goto L_0x0058
            r0 = r21
            com.adobe.air.AndroidStageText r2 = r0.mFocusedStageText
            if (r2 == 0) goto L_0x003d
            r0 = r21
            com.adobe.air.AndroidStageText r2 = r0.mFocusedStageText
            boolean r2 = r2.getPreventDefault()
            r5 = 1
            if (r2 == r5) goto L_0x0058
        L_0x003d:
            r21.requestFocus()
            r4.clearFocus()
            int r5 = r4.getChildCount()
            r2 = 0
        L_0x0048:
            if (r2 >= r5) goto L_0x0058
            android.view.View r6 = r4.getChildAt(r2)
            if (r6 == 0) goto L_0x0053
            r6.clearFocus()
        L_0x0053:
            int r2 = r2 + 1
            goto L_0x0048
        L_0x0056:
            r2 = 0
            goto L_0x0019
        L_0x0058:
            r2 = 5
            if (r3 == r2) goto L_0x005d
            if (r3 != 0) goto L_0x0080
        L_0x005d:
            r2 = 0
        L_0x005e:
            int r4 = r22.getPointerCount()
            if (r2 >= r4) goto L_0x0080
            r0 = r22
            int r4 = r0.getPointerId(r2)
            r0 = r21
            com.adobe.air.gestures.AIRGestureListener r5 = r0.mGestureListener
            r0 = r22
            float r6 = r0.getX(r2)
            r0 = r22
            float r7 = r0.getY(r2)
            r5.setDownTouchPoint(r6, r7, r4)
            int r2 = r2 + 1
            goto L_0x005e
        L_0x0080:
            if (r3 != 0) goto L_0x0089
            r0 = r21
            com.adobe.air.gestures.AIRGestureListener r2 = r0.mGestureListener
            r2.mayStartNewTransformGesture()
        L_0x0089:
            r2 = 5
            if (r3 != r2) goto L_0x018c
            r0 = r21
            com.adobe.air.gestures.AIRGestureListener r2 = r0.mGestureListener
            r3 = 1
            r2.setCouldBeTwoFingerTap(r3)
            r0 = r21
            com.adobe.air.gestures.AIRGestureListener r2 = r0.mGestureListener
            r3 = 0
            r0 = r22
            float r3 = r0.getX(r3)
            r4 = 0
            r0 = r22
            float r4 = r0.getY(r4)
            r5 = 0
            r0 = r22
            int r5 = r0.getPointerId(r5)
            r2.setPrimaryPointOfTwoFingerTap(r3, r4, r5)
            r0 = r21
            com.adobe.air.gestures.AIRGestureListener r2 = r0.mGestureListener
            r3 = 1
            r0 = r22
            float r3 = r0.getX(r3)
            r4 = 1
            r0 = r22
            float r4 = r0.getY(r4)
            r5 = 1
            r0 = r22
            int r5 = r0.getPointerId(r5)
            r2.setSecondaryPointOfTwoFingerTap(r3, r4, r5)
        L_0x00cc:
            r0 = r21
            com.adobe.air.AndroidActivityWrapper r2 = r0.mActivityWrapper
            boolean r2 = r2.isApplicationLaunched()
            if (r2 == 0) goto L_0x02f0
            int r18 = r22.getPointerCount()
            r3 = 0
            r2 = 0
            r17 = r2
        L_0x00de:
            r0 = r17
            r1 = r18
            if (r0 >= r1) goto L_0x02ca
            r0 = r22
            r1 = r17
            float r4 = r0.getX(r1)
            r0 = r22
            r1 = r17
            float r2 = r0.getY(r1)
            r0 = r21
            int r5 = r0.mSkipHeightFromTop
            float r5 = (float) r5
            float r5 = r5 + r2
            r0 = r21
            r0.mLastTouchedXCoord = r4
            r0 = r21
            r0.mLastTouchedYCoord = r5
            r9 = 1
            int r8 = r22.getAction()
            r0 = r22
            r1 = r17
            int r7 = r0.getPointerId(r1)
            r6 = 0
            int r2 = r22.getMetaState()
            int r10 = android.os.Build.VERSION.SDK_INT
            r11 = 14
            if (r10 < r11) goto L_0x0304
            r10 = -234881025(0xfffffffff1ffffff, float:-2.535301E30)
            r2 = r2 & r10
            r0 = r22
            r1 = r17
            int r10 = r0.getToolType(r1)
            r11 = 4
            if (r10 != r11) goto L_0x01c9
            r10 = 67108864(0x4000000, float:1.5046328E-36)
            r2 = r2 | r10
        L_0x012c:
            int r10 = r22.getButtonState()
            r10 = r10 & 2
            if (r10 == 0) goto L_0x0304
            r10 = 134217728(0x8000000, float:3.85186E-34)
            r2 = r2 | r10
            r12 = r2
        L_0x0138:
            if (r3 == 0) goto L_0x01d1
            r2 = 3
            r8 = 4
            r13 = r2
            r14 = r8
            r15 = r3
            r2 = r6
        L_0x0140:
            r0 = r21
            boolean r3 = r0.IsTouchEventHandlingAllowed(r14, r4, r5)
            if (r3 == 0) goto L_0x02f8
            r0 = r22
            r1 = r17
            float r8 = r0.getSize(r1)
            if (r7 != 0) goto L_0x025d
            r10 = 1
        L_0x0153:
            int r19 = r22.getHistorySize()
            int r3 = r19 + 1
            int r3 = r3 * 3
            float[] r11 = new float[r3]
            r6 = 0
            r3 = 0
            r9 = r6
        L_0x0160:
            r0 = r19
            if (r3 >= r0) goto L_0x0260
            int r6 = r9 + 1
            r0 = r22
            r1 = r17
            float r20 = r0.getHistoricalX(r1, r3)
            r11[r9] = r20
            int r9 = r6 + 1
            r0 = r22
            r1 = r17
            float r20 = r0.getHistoricalY(r1, r3)
            r11[r6] = r20
            int r6 = r9 + 1
            r0 = r22
            r1 = r17
            float r20 = r0.getHistoricalPressure(r1, r3)
            r11[r9] = r20
            int r3 = r3 + 1
            r9 = r6
            goto L_0x0160
        L_0x018c:
            r2 = 6
            if (r3 != r2) goto L_0x01a4
            r0 = r21
            com.adobe.air.gestures.AIRGestureListener r2 = r0.mGestureListener
            int r2 = r2.getCouldBeTwoFingerTap()
            r4 = 1
            if (r2 != r4) goto L_0x01a4
            r0 = r21
            com.adobe.air.gestures.AIRGestureListener r2 = r0.mGestureListener
            r3 = 2
            r2.setCouldBeTwoFingerTap(r3)
            goto L_0x00cc
        L_0x01a4:
            r2 = 1
            if (r3 != r2) goto L_0x01bc
            r0 = r21
            com.adobe.air.gestures.AIRGestureListener r2 = r0.mGestureListener
            int r2 = r2.getCouldBeTwoFingerTap()
            r4 = 2
            if (r2 != r4) goto L_0x01bc
            r0 = r21
            com.adobe.air.gestures.AIRGestureListener r2 = r0.mGestureListener
            r3 = 3
            r2.setCouldBeTwoFingerTap(r3)
            goto L_0x00cc
        L_0x01bc:
            r2 = 2
            if (r3 == r2) goto L_0x00cc
            r0 = r21
            com.adobe.air.gestures.AIRGestureListener r2 = r0.mGestureListener
            r3 = 0
            r2.setCouldBeTwoFingerTap(r3)
            goto L_0x00cc
        L_0x01c9:
            r11 = 2
            if (r10 != r11) goto L_0x012c
            r10 = 33554432(0x2000000, float:9.403955E-38)
            r2 = r2 | r10
            goto L_0x012c
        L_0x01d1:
            int r2 = r22.getPointerCount()
            r10 = 1
            if (r2 == r10) goto L_0x01e6
            r2 = 65280(0xff00, float:9.1477E-41)
            r2 = r2 & r8
            int r2 = r2 >> 8
            r0 = r22
            int r2 = r0.getPointerId(r2)
            if (r7 != r2) goto L_0x02fb
        L_0x01e6:
            r8 = r8 & 255(0xff, float:3.57E-43)
            switch(r8) {
                case 0: goto L_0x023f;
                case 1: goto L_0x0301;
                case 2: goto L_0x01eb;
                case 3: goto L_0x0246;
                case 4: goto L_0x01eb;
                case 5: goto L_0x023f;
                case 6: goto L_0x0301;
                case 7: goto L_0x020d;
                case 8: goto L_0x01eb;
                case 9: goto L_0x01fd;
                case 10: goto L_0x0205;
                default: goto L_0x01eb;
            }
        L_0x01eb:
            r0 = r21
            com.adobe.air.AIRWindowSurfaceView$HoverTimeoutHandler r2 = r0.mHoverTimeoutHandler
            long r10 = java.lang.System.currentTimeMillis()
            r2.setLastMove(r10)
            r2 = 1
            r13 = r8
            r14 = r2
            r15 = r3
            r2 = r6
            goto L_0x0140
        L_0x01fd:
            r2 = 16
            r13 = r8
            r14 = r2
            r15 = r3
            r2 = r6
            goto L_0x0140
        L_0x0205:
            r2 = 32
            r13 = r8
            r14 = r2
            r15 = r3
            r2 = r6
            goto L_0x0140
        L_0x020d:
            r9 = 8
            int r2 = android.os.Build.VERSION.SDK_INT
            r10 = 14
            if (r2 >= r10) goto L_0x02fb
            r0 = r21
            com.adobe.air.AIRWindowSurfaceView$HoverTimeoutHandler r2 = r0.mHoverTimeoutHandler
            long r10 = java.lang.System.currentTimeMillis()
            r2.setLastMove(r10)
            r0 = r21
            r0.mHoverMetaState = r12
            r0 = r21
            boolean r2 = r0.mHoverInProgress
            if (r2 != 0) goto L_0x02fb
            r0 = r21
            com.adobe.air.AIRWindowSurfaceView$HoverTimeoutHandler r2 = r0.mHoverTimeoutHandler
            r6 = 0
            r10 = 500(0x1f4, double:2.47E-321)
            r2.sendEmptyMessageDelayed(r6, r10)
            r2 = 1
            r6 = 1
            r0 = r21
            r0.mHoverInProgress = r6
            r13 = r8
            r14 = r9
            r15 = r3
            goto L_0x0140
        L_0x023f:
            r2 = 2
            r13 = r8
            r14 = r2
            r15 = r3
            r2 = r6
            goto L_0x0140
        L_0x0246:
            r2 = 1
        L_0x0247:
            r3 = 4
            r0 = r21
            com.adobe.air.gestures.AIRGestureListener r9 = r0.mGestureListener
            r9.endTwoFingerGesture()
            r0 = r21
            com.adobe.air.gestures.AIRGestureListener r9 = r0.mGestureListener
            r10 = 1
            r9.setCheckForSwipe(r10)
            r13 = r8
            r14 = r3
            r15 = r2
            r2 = r6
            goto L_0x0140
        L_0x025d:
            r10 = 0
            goto L_0x0153
        L_0x0260:
            r0 = r22
            r1 = r17
            float r6 = r0.getPressure(r1)
            r11[r9] = r4
            int r3 = r9 + 1
            r11[r3] = r5
            int r3 = r9 + 2
            r11[r3] = r6
            r12 = r12 & -2
            r3 = 0
            switch(r14) {
                case 1: goto L_0x02c4;
                case 2: goto L_0x02c4;
                case 8: goto L_0x02b5;
                default: goto L_0x0278;
            }
        L_0x0278:
            if (r3 == 0) goto L_0x028d
            com.adobe.air.TouchEventData r2 = new com.adobe.air.TouchEventData
            r9 = r8
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            if (r16 == 0) goto L_0x02c6
            r3 = 0
            r9 = 0
            boolean r2 = com.adobe.air.Entrypoints.registerTouchCallback(r3, r2, r9)
            if (r2 == 0) goto L_0x02c6
            r2 = 1
        L_0x028b:
            r16 = r2
        L_0x028d:
            r2 = 8
            if (r14 == r2) goto L_0x02f8
            r2 = 1
            if (r14 == r2) goto L_0x02f8
            r2 = 3
            if (r13 != r2) goto L_0x0299
            r12 = r12 | 1
        L_0x0299:
            com.adobe.air.TouchEventData r2 = new com.adobe.air.TouchEventData
            r11 = 0
            r3 = r14
            r9 = r8
            r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)
            if (r16 == 0) goto L_0x02c8
            r3 = 0
            r4 = 0
            boolean r2 = com.adobe.air.Entrypoints.registerTouchCallback(r3, r2, r4)
            if (r2 == 0) goto L_0x02c8
            r2 = 1
        L_0x02ac:
            int r3 = r17 + 1
            r17 = r3
            r16 = r2
            r3 = r15
            goto L_0x00de
        L_0x02b5:
            r3 = 8
            int r9 = android.os.Build.VERSION.SDK_INT
            r19 = 14
            r0 = r19
            if (r9 >= r0) goto L_0x0278
            if (r2 == 0) goto L_0x0278
            r3 = 24
            goto L_0x0278
        L_0x02c4:
            r3 = 1
            goto L_0x0278
        L_0x02c6:
            r2 = 0
            goto L_0x028b
        L_0x02c8:
            r2 = 0
            goto L_0x02ac
        L_0x02ca:
            r2 = r16
        L_0x02cc:
            if (r2 == 0) goto L_0x02f2
            r0 = r21
            android.view.ScaleGestureDetector r3 = r0.mScaleGestureDetector     // Catch:{ Exception -> 0x02f6 }
            r0 = r22
            boolean r2 = r3.onTouchEvent(r0)     // Catch:{ Exception -> 0x02f6 }
            if (r2 == 0) goto L_0x02f2
            r2 = 1
        L_0x02db:
            if (r2 == 0) goto L_0x02f4
            r0 = r21
            android.view.GestureDetector r2 = r0.mGestureDetector
            r0 = r22
            boolean r2 = r2.onTouchEvent(r0)
            if (r2 == 0) goto L_0x02f4
            r2 = 1
        L_0x02ea:
            r3 = 0
            r0 = r21
            r0.inTouch = r3
            return r2
        L_0x02f0:
            r2 = 0
            goto L_0x02cc
        L_0x02f2:
            r2 = 0
            goto L_0x02db
        L_0x02f4:
            r2 = 0
            goto L_0x02ea
        L_0x02f6:
            r3 = move-exception
            goto L_0x02db
        L_0x02f8:
            r2 = r16
            goto L_0x02ac
        L_0x02fb:
            r2 = r6
            r13 = r8
            r14 = r9
            r15 = r3
            goto L_0x0140
        L_0x0301:
            r2 = r3
            goto L_0x0247
        L_0x0304:
            r12 = r2
            goto L_0x0138
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adobe.air.AIRWindowSurfaceView.onTouchEvent(android.view.MotionEvent):boolean");
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

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.mActivityWrapper.planeStepCascade();
        if (this.mIsFullScreen) {
            setFullScreen();
        }
        if (this.mActivityWrapper.isStarted() || this.mActivityWrapper.isResumed() || (Build.MANUFACTURER.equalsIgnoreCase("SAMSUNG") && Build.MODEL.equalsIgnoreCase("GT-I9300"))) {
            nativeSurfaceCreated();
        }
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        Display defaultDisplay = ((WindowManager) this.mActivityWrapper.getActivity().getSystemService("window")).getDefaultDisplay();
        this.mBoundHeight = defaultDisplay.getHeight();
        this.mBoundWidth = defaultDisplay.getWidth();
        this.mVisibleBoundHeight = i3;
        this.mVisibleBoundWidth = i2;
        nativeOnFormatChangeListener(i);
        if (!this.mSurfaceValid) {
            this.mSurfaceValid = true;
            this.mActivityWrapper.onSurfaceInitialized();
            setMultitouchMode(nativeGetMultitouchMode());
        }
        if (this.mSurfaceValid) {
            int i4 = getResources().getConfiguration().orientation;
            if (i4 != this.mCurrentOrientation) {
                showSoftKeyboard(false);
                nativeDispatchUserTriggeredSkDeactivateEvent();
                this.mDispatchUserTriggeredSkDeactivate = false;
            } else if ((i4 == 1 || i4 == 2) && i3 < this.mHt) {
                if (i3 != 0) {
                    if (nativePerformWindowPanning(i4, this.mHt - i3)) {
                        this.mSurfaceChangedForSoftKeyboard = true;
                        return;
                    }
                } else {
                    return;
                }
            }
            boolean z = this.mCurrentOrientation != i4;
            this.mCurrentOrientation = i4;
            this.mWd = i2;
            this.mHt = i3;
            nativeOnSizeChangedListener(this.mWd, this.mHt, z);
            OrientationManager orientationManager = OrientationManager.getOrientationManager();
            if (orientationManager.mDispatchOrientationChangePending) {
                orientationManager.nativeOrientationChanged(orientationManager.mBeforeOrientation, orientationManager.mAfterOrientation);
                orientationManager.mDispatchOrientationChangePending = false;
            }
            nativeForceReDraw();
            forceSoftKeyboardDown();
        }
    }

    public void forceSoftKeyboardDown() {
        nativeShowOriginalRect();
        setScrollTo(0);
        if (this.mDispatchUserTriggeredSkDeactivate && this.mSurfaceChangedForSoftKeyboard) {
            nativeDispatchUserTriggeredSkDeactivateEvent();
        }
        nativeSetKeyboardVisible(false);
        this.mDispatchUserTriggeredSkDeactivate = true;
        this.mSurfaceChangedForSoftKeyboard = false;
    }

    public boolean isSurfaceValid() {
        return this.mSurfaceValid;
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.mSurfaceValid = false;
        if (this.mFlashEGL != null) {
            this.mFlashEGL.DestroyWindowSurface();
        }
        this.mActivityWrapper.onSurfaceDestroyed();
        this.mActivityWrapper.planeBreakCascade();
    }

    class InputMethodReceiver extends ResultReceiver {
        public InputMethodReceiver() {
            super(AIRWindowSurfaceView.this.getHandler());
        }

        /* access modifiers changed from: protected */
        public void onReceiveResult(int i, Bundle bundle) {
            if (i == 1 || i == 3) {
                AIRWindowSurfaceView.this.nativeShowOriginalRect();
            } else {
                AIRWindowSurfaceView.this.nativeSetKeyboardVisible(true);
            }
        }
    }

    public void showSoftKeyboard(boolean z, View view) {
        AIRLogger.d(LOG_TAG, "showSoftKeyboard show: " + z);
        InputMethodManager inputMethodManager = getInputMethodManager();
        if (!z) {
            if (this.mSurfaceChangedForSoftKeyboard) {
                this.mDispatchUserTriggeredSkDeactivate = false;
            }
            inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
            if (this.mInputConnection != null) {
                this.mInputConnection.Reset();
            }
            nativeSetKeyboardVisible(false);
            return;
        }
        inputMethodManager.showSoftInput(view, 0, this.mInputMethodReceiver);
    }

    public void showSoftKeyboard(boolean z) {
        showSoftKeyboard(z, this);
    }

    public void updateFocusedStageWebView(AndroidWebView androidWebView, boolean z) {
        if (z) {
            this.mFocusedWebView = androidWebView;
        } else if (this.mFocusedWebView == androidWebView) {
            this.mFocusedWebView = null;
        }
    }

    public boolean isStageWebViewInFocus() {
        if (this.mFocusedWebView != null) {
            return this.mFocusedWebView.isInTextEditingMode();
        }
        return false;
    }

    public long panStageWebViewInFocus() {
        if (this.mFocusedWebView == null) {
            return 0;
        }
        this.mResizedWebView = this.mFocusedWebView;
        return this.mFocusedWebView.updateViewBoundsWithKeyboard(this.mHt);
    }

    public void updateFocusedStageText(AndroidStageText androidStageText, boolean z) {
        if (z) {
            this.mFocusedStageText = androidStageText;
        } else if (this.mFocusedStageText == androidStageText) {
            this.mFocusedStageText = null;
        }
    }

    public boolean isStageTextInFocus() {
        return this.mFocusedStageText != null;
    }

    public long panStageTextInFocus() {
        if (this.mFocusedStageText == null) {
            return 0;
        }
        this.mResizedStageText = this.mFocusedStageText;
        return this.mFocusedStageText.updateViewBoundsWithKeyboard(this.mHt);
    }

    private boolean IsIMEInFullScreen() {
        return getInputMethodManager().isFullscreenMode();
    }

    public boolean setScrollTo(final int i) {
        this.mSkipHeightFromTop = i;
        final RelativeLayout overlaysLayout = this.mActivityWrapper.getOverlaysLayout(false);
        if (overlaysLayout == null) {
            return true;
        }
        post(new Runnable() {
            public void run() {
                if (i == 0 && AIRWindowSurfaceView.this.mResizedWebView != null) {
                    AIRWindowSurfaceView.this.mResizedWebView.resetGlobalBounds();
                    AndroidWebView unused = AIRWindowSurfaceView.this.mResizedWebView = null;
                }
                if (i == 0 && AIRWindowSurfaceView.this.mResizedStageText != null) {
                    AIRWindowSurfaceView.this.mResizedStageText.resetGlobalBounds();
                    AndroidStageText unused2 = AIRWindowSurfaceView.this.mResizedStageText = null;
                }
                overlaysLayout.setPadding(0, -i, 0, 0);
                overlaysLayout.requestLayout();
            }
        });
        return true;
    }

    private void setSurfaceFormatImpl(boolean z, final int i) {
        if (z) {
            post(new Runnable() {
                public void run() {
                    AIRWindowSurfaceView.this.mSurfaceHolder.setFormat(i);
                    int unused = AIRWindowSurfaceView.this.mCurrentSurfaceFormat = i;
                }
            });
            return;
        }
        this.mSurfaceHolder.setFormat(i);
        this.mCurrentSurfaceFormat = i;
    }

    public void setSurfaceFormat(boolean z) {
        if (!this.mActivityWrapper.useRGB565()) {
            if (!this.mNeedsCompositingSurface && !this.mActivityWrapper.needsCompositingSurface()) {
                AndroidActivityWrapper androidActivityWrapper = this.mActivityWrapper;
                if (AndroidActivityWrapper.isGingerbread()) {
                    setSurfaceFormatImpl(z, 2);
                    return;
                }
            }
            setSurfaceFormatImpl(z, 1);
        } else if (this.mNeedsCompositingSurface) {
            setSurfaceFormatImpl(z, 1);
        } else {
            setSurfaceFormatImpl(z, 4);
        }
    }

    public void setCompositingHint(boolean z) {
        this.mNeedsCompositingSurface = z;
        if (z && this.mCurrentSurfaceFormat == 1) {
            return;
        }
        if (z || this.mCurrentSurfaceFormat != 2) {
            setSurfaceFormat(true);
        }
    }

    /* access modifiers changed from: protected */
    public void draw(int i, int i2, int i3, int i4, Bitmap bitmap) {
        Canvas lockCanvas;
        Rect rect;
        if (this.mSurfaceValid) {
            Rect rect2 = new Rect(i, i2, i + i3, i2 + i4);
            int i5 = this.mSkipHeightFromTop;
            if (i5 != 0) {
                Rect rect3 = new Rect(0, i5, this.mWd, this.mHt);
                if (Rect.intersects(rect2, rect3)) {
                    Rect rect4 = new Rect(rect2);
                    rect4.intersect(rect3);
                    rect4.top -= i5;
                    rect4.bottom -= i5;
                    if (this.mIsFullScreen) {
                        rect4.union(new Rect(0, rect4.bottom, this.mWd, this.mHt));
                    }
                    Canvas lockCanvas2 = this.mSurfaceHolder.lockCanvas(rect4);
                    if (!this.mIsFullScreen && rect4.bottom > this.mHt - i5) {
                        rect4.bottom = this.mHt - i5;
                    }
                    Canvas canvas = lockCanvas2;
                    rect = rect4;
                    lockCanvas = canvas;
                } else {
                    return;
                }
            } else {
                lockCanvas = this.mSurfaceHolder.lockCanvas(rect2);
                rect = rect2;
            }
            try {
                synchronized (this.mSurfaceHolder) {
                    lockCanvas.clipRect(rect);
                    if (i5 != 0 && this.mIsFullScreen) {
                        lockCanvas.drawColor(-16777216);
                    }
                    if (this.mMaliWorkaround) {
                        this.mPaint = null;
                        lockCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
                    } else if (this.mPaint == null && this.mCurrentSurfaceFormat != 4) {
                        this.mPaint = new Paint();
                        this.mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
                        this.mPaint.setFilterBitmap(false);
                    }
                    lockCanvas.drawBitmap(bitmap, 0.0f, (float) (-i5), this.mPaint);
                }
                if (lockCanvas != null) {
                    this.mSurfaceHolder.unlockCanvasAndPost(lockCanvas);
                }
            } catch (Exception e) {
                if (lockCanvas != null) {
                    this.mSurfaceHolder.unlockCanvasAndPost(lockCanvas);
                }
            } catch (Throwable th) {
                if (lockCanvas != null) {
                    this.mSurfaceHolder.unlockCanvasAndPost(lockCanvas);
                }
                throw th;
            }
            if (this.mInputConnection != null) {
                this.mInputConnection.updateIMEText();
            }
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0058  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x00eb  */
    /* JADX WARNING: Removed duplicated region for block: B:65:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void drawScaled(int r12, int r13, int r14, int r15, android.graphics.Bitmap r16, int r17, int r18, int r19, int r20, boolean r21, int r22) {
        /*
            r11 = this;
            boolean r2 = r11.mSurfaceValid
            if (r2 != 0) goto L_0x0005
        L_0x0004:
            return
        L_0x0005:
            r2 = 0
            android.graphics.Rect r5 = new android.graphics.Rect     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            int r3 = r17 + r19
            int r4 = r18 + r20
            r0 = r17
            r1 = r18
            r5.<init>(r0, r1, r3, r4)     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            if (r21 == 0) goto L_0x003e
            android.graphics.Rect r3 = new android.graphics.Rect     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            r4 = 0
            r6 = 0
            int r7 = r11.mWd     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            int r8 = r11.mHt     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            r3.<init>(r4, r6, r7, r8)     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
        L_0x0020:
            int r4 = r11.mSkipHeightFromTop     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            if (r4 == 0) goto L_0x00f3
            int r6 = r11.mSkipHeightFromTop     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            android.graphics.Rect r7 = new android.graphics.Rect     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            r4 = 0
            int r8 = r11.mWd     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            int r9 = r11.mHt     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            r7.<init>(r4, r6, r8, r9)     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            boolean r4 = android.graphics.Rect.intersects(r5, r7)     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            if (r4 != 0) goto L_0x005e
            if (r2 == 0) goto L_0x0004
            android.view.SurfaceHolder r3 = r11.mSurfaceHolder
            r3.unlockCanvasAndPost(r2)
            goto L_0x0004
        L_0x003e:
            android.graphics.Rect r3 = new android.graphics.Rect     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            int r4 = r17 + r19
            int r6 = r18 + r20
            r0 = r17
            r1 = r18
            r3.<init>(r0, r1, r4, r6)     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            goto L_0x0020
        L_0x004c:
            r3 = move-exception
        L_0x004d:
            if (r2 == 0) goto L_0x0054
            android.view.SurfaceHolder r3 = r11.mSurfaceHolder
            r3.unlockCanvasAndPost(r2)
        L_0x0054:
            com.adobe.air.AndroidInputConnection r2 = r11.mInputConnection
            if (r2 == 0) goto L_0x0004
            com.adobe.air.AndroidInputConnection r2 = r11.mInputConnection
            r2.updateIMEText()
            goto L_0x0004
        L_0x005e:
            android.graphics.Rect r4 = new android.graphics.Rect     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            r4.<init>(r5)     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            r4.intersect(r7)     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            int r5 = r4.top     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            int r5 = r5 - r6
            r4.top = r5     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            int r5 = r4.bottom     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            int r5 = r5 - r6
            r4.bottom = r5     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            if (r21 != 0) goto L_0x0073
            r3 = r4
        L_0x0073:
            if (r21 != 0) goto L_0x0081
            int r5 = r4.bottom     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            int r7 = r11.mHt     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            int r7 = r7 - r6
            if (r5 <= r7) goto L_0x0081
            int r5 = r11.mHt     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            int r5 = r5 - r6
            r4.bottom = r5     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
        L_0x0081:
            android.graphics.Rect r5 = new android.graphics.Rect     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            int r6 = r12 + r14
            int r7 = r13 + r15
            r5.<init>(r12, r13, r6, r7)     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            android.view.SurfaceHolder r6 = r11.mSurfaceHolder     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            android.graphics.Canvas r3 = r6.lockCanvas(r3)     // Catch:{ Exception -> 0x004c, all -> 0x00e5 }
            android.view.SurfaceHolder r6 = r11.mSurfaceHolder     // Catch:{ Exception -> 0x00e1, all -> 0x00f1 }
            monitor-enter(r6)     // Catch:{ Exception -> 0x00e1, all -> 0x00f1 }
            if (r21 == 0) goto L_0x00a4
            int r2 = android.graphics.Color.red(r22)     // Catch:{ all -> 0x00de }
            int r7 = android.graphics.Color.green(r22)     // Catch:{ all -> 0x00de }
            int r8 = android.graphics.Color.blue(r22)     // Catch:{ all -> 0x00de }
            r3.drawRGB(r2, r7, r8)     // Catch:{ all -> 0x00de }
        L_0x00a4:
            boolean r2 = r11.mMaliWorkaround     // Catch:{ all -> 0x00de }
            if (r2 == 0) goto L_0x00c1
            r2 = 0
            r11.mPaint = r2     // Catch:{ all -> 0x00de }
            r2 = 0
            android.graphics.PorterDuff$Mode r7 = android.graphics.PorterDuff.Mode.CLEAR     // Catch:{ all -> 0x00de }
            r3.drawColor(r2, r7)     // Catch:{ all -> 0x00de }
        L_0x00b1:
            android.graphics.Paint r2 = r11.mPaintScaled     // Catch:{ all -> 0x00de }
            r0 = r16
            r3.drawBitmap(r0, r5, r4, r2)     // Catch:{ all -> 0x00de }
            monitor-exit(r6)     // Catch:{ all -> 0x00de }
            if (r3 == 0) goto L_0x0054
            android.view.SurfaceHolder r2 = r11.mSurfaceHolder
            r2.unlockCanvasAndPost(r3)
            goto L_0x0054
        L_0x00c1:
            android.graphics.Paint r2 = r11.mPaintScaled     // Catch:{ all -> 0x00de }
            if (r2 != 0) goto L_0x00b1
            int r2 = r11.mCurrentSurfaceFormat     // Catch:{ all -> 0x00de }
            r7 = 4
            if (r2 == r7) goto L_0x00b1
            android.graphics.Paint r2 = new android.graphics.Paint     // Catch:{ all -> 0x00de }
            r2.<init>()     // Catch:{ all -> 0x00de }
            r11.mPaintScaled = r2     // Catch:{ all -> 0x00de }
            android.graphics.Paint r2 = r11.mPaintScaled     // Catch:{ all -> 0x00de }
            android.graphics.PorterDuffXfermode r7 = new android.graphics.PorterDuffXfermode     // Catch:{ all -> 0x00de }
            android.graphics.PorterDuff$Mode r8 = android.graphics.PorterDuff.Mode.SRC     // Catch:{ all -> 0x00de }
            r7.<init>(r8)     // Catch:{ all -> 0x00de }
            r2.setXfermode(r7)     // Catch:{ all -> 0x00de }
            goto L_0x00b1
        L_0x00de:
            r2 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x00de }
            throw r2     // Catch:{ Exception -> 0x00e1, all -> 0x00f1 }
        L_0x00e1:
            r2 = move-exception
            r2 = r3
            goto L_0x004d
        L_0x00e5:
            r3 = move-exception
            r10 = r3
            r3 = r2
            r2 = r10
        L_0x00e9:
            if (r3 == 0) goto L_0x00f0
            android.view.SurfaceHolder r4 = r11.mSurfaceHolder
            r4.unlockCanvasAndPost(r3)
        L_0x00f0:
            throw r2
        L_0x00f1:
            r2 = move-exception
            goto L_0x00e9
        L_0x00f3:
            r4 = r5
            goto L_0x0081
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adobe.air.AIRWindowSurfaceView.drawScaled(int, int, int, int, android.graphics.Bitmap, int, int, int, int, boolean, int):void");
    }

    public void drawBitmap(int i, int i2, int i3, int i4, Bitmap bitmap) {
        draw(i, i2, i3, i4, bitmap);
    }

    public void drawBitmap(int i, int i2, int i3, int i4, Bitmap bitmap, int i5, int i6, int i7, int i8, boolean z, int i9) {
        drawScaled(i, i2, i3, i4, bitmap, i5, i6, i7, i8, z, i9);
    }

    public boolean getIsFullScreen() {
        return this.mIsFullScreen;
    }

    private static boolean supportsSystemUiVisibilityAPI() {
        return Build.VERSION.SDK_INT >= 11;
    }

    private static boolean supportsSystemUiFlags() {
        return Build.VERSION.SDK_INT >= 14;
    }

    private boolean hasStatusBar(Window window) {
        Rect rect = new Rect();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top > 0;
    }

    private void DoSetOnSystemUiVisibilityChangeListener() {
        setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            public void onSystemUiVisibilityChange(int i) {
                this.setOnSystemUiVisibilityChangeListener((View.OnSystemUiVisibilityChangeListener) null);
                if (this.getIsFullScreen()) {
                    this.nativeDispatchFullScreenEvent(true);
                } else {
                    this.nativeDispatchFullScreenEvent(false);
                }
            }
        });
    }

    public void setFullScreen() {
        int i = 1;
        if (!this.mIsFullScreen) {
            this.mIsFullScreen = true;
            this.mActivityWrapper.setIsFullScreen(this.mIsFullScreen);
            if (supportsSystemUiVisibilityAPI()) {
                if (supportsSystemUiFlags()) {
                }
                DoSetOnSystemUiVisibilityChangeListener();
                if (Build.VERSION.SDK_INT >= 19) {
                    i = 4103;
                }
                setSystemUiVisibility(i);
            }
            this.mActivityWrapper.planeBreakCascade();
        }
        Activity activity = this.mActivityWrapper.getActivity();
        if (activity != null) {
            Window window = activity.getWindow();
            if (!supportsSystemUiVisibilityAPI() || hasStatusBar(window)) {
                window.setFlags(1024, 1024);
            }
        }
    }

    public void clearFullScreen() {
        this.mIsFullScreen = false;
        this.mActivityWrapper.setIsFullScreen(this.mIsFullScreen);
        if (supportsSystemUiVisibilityAPI()) {
            if (supportsSystemUiFlags()) {
            }
            DoSetOnSystemUiVisibilityChangeListener();
            setSystemUiVisibility(0);
        }
        Activity activity = this.mActivityWrapper.getActivity();
        if (activity != null) {
            activity.getWindow().clearFlags(1024);
        }
        this.mActivityWrapper.planeBreakCascade();
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

    public int getColorDepth() {
        if (this.mCurrentSurfaceFormat == 4) {
            return 16;
        }
        Activity activity = this.mActivityWrapper.getActivity();
        if (activity == null) {
            return 32;
        }
        Display defaultDisplay = ((WindowManager) activity.getSystemService("window")).getDefaultDisplay();
        PixelFormat pixelFormat = new PixelFormat();
        PixelFormat.getPixelFormatInfo(defaultDisplay.getPixelFormat(), pixelFormat);
        return pixelFormat.bitsPerPixel;
    }

    public int getAppSpecifiedPixelFormat() {
        if (this.mActivityWrapper.useRGB565()) {
            return 16;
        }
        return 32;
    }

    public void showActionScript2Warning() {
        Activity activity = this.mActivityWrapper.getActivity();
        if (activity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            TextView textView = new TextView(activity);
            textView.setText("Your application is attempting to run ActionScript2.0, which is not supported on smart phone profile. \nSee the Adobe Developer Connection for more info www.adobe.com/devnet");
            Linkify.addLinks(textView, 1);
            builder.setView(textView);
            builder.setTitle("Action Script 2.0");
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builder.show();
        }
    }

    public boolean IsLandScape() {
        return this.mCurrentOrientation == 2;
    }

    public boolean onCheckIsTextEditor() {
        return true;
    }

    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        if (!this.mActivityWrapper.isApplicationLaunched() || !nativeIsEditable()) {
            this.mInputConnection = null;
            editorInfo.imeOptions = 268435456;
        } else {
            editorInfo.imeOptions |= 1073741824;
            editorInfo.imeOptions |= 6;
            int nativeGetSoftKeyboardType = nativeGetSoftKeyboardType();
            editorInfo.inputType |= 1;
            switch (nativeGetSoftKeyboardType) {
                case 2:
                    editorInfo.inputType = 17;
                    break;
                case 3:
                    editorInfo.inputType = 2;
                    break;
                case 4:
                    editorInfo.inputType = 1;
                    break;
                case 5:
                    editorInfo.inputType = 33;
                    break;
            }
            if (nativeIsPasswordField()) {
                editorInfo.inputType |= 128;
            }
            if (nativeIsMultiLineTextField()) {
                editorInfo.inputType |= 131072;
            }
            this.mInputConnection = new AndroidInputConnection(this);
            editorInfo.initialSelStart = -1;
            editorInfo.initialSelEnd = -1;
            editorInfo.initialCapsMode = 0;
        }
        return this.mInputConnection;
    }

    public void RestartInput() {
        this.mMetaShiftState = MetaKeyState.INACTIVE;
        this.mMetaAltState = MetaKeyState.INACTIVE;
        InputMethodManager inputMethodManager = getInputMethodManager();
        if (inputMethodManager != null) {
            inputMethodManager.restartInput(this);
        }
        if (this.mInputConnection != null) {
            this.mInputConnection.Reset();
        }
    }

    public InputMethodManager getInputMethodManager() {
        return (InputMethodManager) getContext().getSystemService("input_method");
    }

    public boolean performLongClick() {
        Rect nativeGetTextBoxBounds;
        if (!this.mWindowHasFocus || (nativeGetTextBoxBounds = nativeGetTextBoxBounds()) == null) {
            return false;
        }
        if ((this.mLastTouchedXCoord <= ((float) nativeGetTextBoxBounds.left) || this.mLastTouchedXCoord >= ((float) nativeGetTextBoxBounds.right) || this.mLastTouchedYCoord <= ((float) nativeGetTextBoxBounds.top) || this.mLastTouchedYCoord >= ((float) nativeGetTextBoxBounds.bottom)) && !this.mTrackBallPressed) {
            return false;
        }
        this.mTrackBallPressed = false;
        if (super.performLongClick()) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void onCreateContextMenu(ContextMenu contextMenu) {
        super.onCreateContextMenu(contextMenu);
        if (!this.mIsFullScreen || nativeIsFullScreenInteractive()) {
            ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService("clipboard");
            MenuHandler menuHandler = new MenuHandler();
            boolean nativeIsEditable = nativeIsEditable();
            boolean nativeIsTextFieldSelectable = nativeIsTextFieldSelectable();
            if (nativeIsTextFieldSelectable || nativeIsEditable) {
                if (nativeIsTextFieldSelectable) {
                    boolean z = nativeGetTextContentLength() > 0;
                    if (z) {
                        contextMenu.add(0, 0, 0, AndroidLocale.GetLocalizedString(AndroidLocale.STRING_ID.IDA_SELECT_ALL)).setOnMenuItemClickListener(menuHandler).setAlphabeticShortcut('a');
                        if (nativeIsTextFieldInSelectionMode()) {
                            contextMenu.add(0, 8, 0, AndroidLocale.GetLocalizedString(AndroidLocale.STRING_ID.IDA_STOP_SELECTING_TEXT)).setOnMenuItemClickListener(menuHandler);
                        } else {
                            contextMenu.add(0, 7, 0, AndroidLocale.GetLocalizedString(AndroidLocale.STRING_ID.IDA_SELECT_TEXT)).setOnMenuItemClickListener(menuHandler);
                        }
                    }
                    if (!nativeIsPasswordField() && z) {
                        boolean nativeIsTextSelected = nativeIsTextSelected();
                        if (nativeIsEditable) {
                            if (nativeIsTextSelected) {
                                contextMenu.add(0, 1, 0, AndroidLocale.GetLocalizedString(AndroidLocale.STRING_ID.IDA_CUT_STRING)).setOnMenuItemClickListener(menuHandler).setAlphabeticShortcut('x');
                            } else {
                                contextMenu.add(0, 2, 0, AndroidLocale.GetLocalizedString(AndroidLocale.STRING_ID.IDA_CUT_ALL_STRING)).setOnMenuItemClickListener(menuHandler);
                            }
                        }
                        if (nativeIsTextSelected) {
                            contextMenu.add(0, 3, 0, AndroidLocale.GetLocalizedString(AndroidLocale.STRING_ID.IDA_COPY_STRING)).setOnMenuItemClickListener(menuHandler).setAlphabeticShortcut('c');
                        } else {
                            contextMenu.add(0, 4, 0, AndroidLocale.GetLocalizedString(AndroidLocale.STRING_ID.IDA_COPY_ALL_STRING)).setOnMenuItemClickListener(menuHandler);
                        }
                    }
                }
                if (nativeIsEditable) {
                    if (clipboardManager != null && clipboardManager.hasText()) {
                        contextMenu.add(0, 5, 0, AndroidLocale.GetLocalizedString(AndroidLocale.STRING_ID.IDA_PASTE_STRING)).setOnMenuItemClickListener(menuHandler).setAlphabeticShortcut('v');
                    }
                    contextMenu.add(0, 6, 0, AndroidLocale.GetLocalizedString(AndroidLocale.STRING_ID.IDA_INPUT_METHOD_STRING)).setOnMenuItemClickListener(menuHandler);
                }
                this.mEatTouchRelease = true;
                this.mContextMenuVisible = true;
                contextMenu.setHeaderTitle(AndroidLocale.GetLocalizedString(AndroidLocale.STRING_ID.IDA_CONTEXT_MENU_TITLE_STRING));
            }
        }
    }

    private void postCheckLongPress() {
        if (this.mLongPressCheck == null) {
            this.mLongPressCheck = new CheckLongPress();
        }
        postDelayed(this.mLongPressCheck, 500);
    }

    class CheckLongPress implements Runnable {
        CheckLongPress() {
        }

        public void run() {
            AIRWindowSurfaceView.this.performLongClick();
        }
    }

    private class MenuHandler implements MenuItem.OnMenuItemClickListener {
        private MenuHandler() {
        }

        public boolean onMenuItemClick(MenuItem menuItem) {
            return AIRWindowSurfaceView.this.onTextBoxContextMenuItem(menuItem.getItemId());
        }
    }

    public boolean onTextBoxContextMenuItem(int i) {
        ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService("clipboard");
        switch (i) {
            case 0:
                nativeSelectAllText();
                break;
            case 1:
                String nativeGetSelectedText = nativeGetSelectedText();
                if (nativeGetSelectedText != null) {
                    nativeCutText(false);
                    if (nativeIsPasswordField()) {
                        nativeGetSelectedText = Utils.ReplaceTextContentWithStars(nativeGetSelectedText);
                    }
                    clipboardManager.setText(nativeGetSelectedText);
                }
                SetSelectionMode(false);
                break;
            case 2:
                CharSequence charSequence = nativeGetTextContent().text;
                if (charSequence != null) {
                    nativeCutText(true);
                    if (nativeIsPasswordField()) {
                        charSequence = Utils.ReplaceTextContentWithStars(charSequence.toString());
                    }
                    clipboardManager.setText(charSequence);
                    break;
                }
                break;
            case 3:
                String nativeGetSelectedText2 = nativeGetSelectedText();
                if (nativeGetSelectedText2 != null) {
                    if (nativeIsPasswordField()) {
                        nativeGetSelectedText2 = Utils.ReplaceTextContentWithStars(nativeGetSelectedText2);
                    }
                    clipboardManager.setText(nativeGetSelectedText2);
                }
                SetSelectionMode(false);
                break;
            case 4:
                CharSequence charSequence2 = nativeGetTextContent().text;
                if (charSequence2 != null) {
                    if (nativeIsPasswordField()) {
                        charSequence2 = Utils.ReplaceTextContentWithStars(charSequence2.toString());
                    }
                    clipboardManager.setText(charSequence2);
                    break;
                }
                break;
            case 5:
                CharSequence text = clipboardManager.getText();
                if (text != null) {
                    nativeInsertText(text.toString());
                }
                SetSelectionMode(false);
                break;
            case 6:
                InputMethodManager inputMethodManager = getInputMethodManager();
                if (inputMethodManager != null) {
                    inputMethodManager.showInputMethodPicker();
                    break;
                }
                break;
            case 7:
                SetSelectionMode(true);
                break;
            case 8:
                SetSelectionMode(false);
                break;
            default:
                return false;
        }
        if (this.mInputConnection != null) {
            this.mInputConnection.updateIMEText();
        }
        return true;
    }

    private boolean IsTouchEventHandlingAllowed(int i, float f, float f2) {
        boolean IsPointInTextBox = IsPointInTextBox(f, f2, i);
        if (i == 2) {
            this.mDownX = f;
            this.mDownY = f2;
            this.mEatTouchRelease = false;
            if (!IsPointInTextBox) {
                return true;
            }
            postCheckLongPress();
            return true;
        } else if (i == 1) {
            if (!IsPointInTextBox) {
                return true;
            }
            if (!IsTouchMove(f, f2)) {
                return false;
            }
            if (this.mLongPressCheck == null) {
                return true;
            }
            removeCallbacks(this.mLongPressCheck);
            return true;
        } else if (i != 4 || this.mLongPressCheck == null) {
            return true;
        } else {
            removeCallbacks(this.mLongPressCheck);
            return true;
        }
    }

    private boolean IsTouchMove(float f, float f2) {
        float f3 = this.mDownX - f;
        float f4 = this.mDownY - f2;
        if (((float) Math.sqrt((double) ((f3 * f3) + (f4 * f4)))) >= ((float) this.mScaledTouchSlop)) {
            return true;
        }
        return false;
    }

    private boolean IsPointInTextBox(float f, float f2, int i) {
        boolean z;
        if (i == 2) {
            this.mTextBoxBounds = nativeGetTextBoxBounds();
        }
        if (this.mTextBoxBounds == null || ((int) f) <= this.mTextBoxBounds.left || ((int) f) >= this.mTextBoxBounds.right || ((int) f2) <= this.mTextBoxBounds.top || ((int) f2) >= this.mTextBoxBounds.bottom) {
            z = false;
        } else {
            z = true;
        }
        if (i == 4) {
            this.mTextBoxBounds = null;
        }
        return z;
    }

    private void HandleMetaKeyAction(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case R.styleable.Theme_activityChooserViewStyle:
            case R.styleable.Theme_toolbarStyle:
                if (keyEvent.getRepeatCount() == 0) {
                    this.mMetaAltState = GetMetaKeyState(this.mMetaAltState, keyEvent.isAltPressed(), false);
                    return;
                }
                return;
            case R.styleable.Theme_toolbarNavigationButtonStyle:
            case R.styleable.Theme_popupMenuStyle:
                if (keyEvent.getRepeatCount() == 0) {
                    this.mMetaShiftState = GetMetaKeyState(this.mMetaShiftState, keyEvent.isShiftPressed(), false);
                    return;
                }
                return;
            default:
                this.mMetaShiftState = GetMetaKeyState(this.mMetaShiftState, keyEvent.isShiftPressed(), true);
                this.mMetaAltState = GetMetaKeyState(this.mMetaAltState, keyEvent.isAltPressed(), true);
                return;
        }
    }

    private MetaKeyState GetMetaKeyState(MetaKeyState metaKeyState, boolean z, boolean z2) {
        if (z2) {
            switch (metaKeyState) {
                case INACTIVE:
                case PRESSED:
                    return z ? MetaKeyState.PRESSED : MetaKeyState.INACTIVE;
                case ACTIVE:
                    return z ? MetaKeyState.PRESSED : MetaKeyState.INACTIVE;
                case LOCKED:
                    return MetaKeyState.LOCKED;
                default:
                    return MetaKeyState.INACTIVE;
            }
        } else if (!z) {
            return MetaKeyState.INACTIVE;
        } else {
            switch (metaKeyState) {
                case INACTIVE:
                case PRESSED:
                    return MetaKeyState.ACTIVE;
                case ACTIVE:
                    return MetaKeyState.LOCKED;
                default:
                    return MetaKeyState.INACTIVE;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int GetMetaKeyCharacter(KeyEvent keyEvent) {
        int i = 0;
        if (this.mMetaShiftState == MetaKeyState.LOCKED || this.mMetaShiftState == MetaKeyState.ACTIVE) {
            i = 1;
        }
        if (this.mMetaAltState == MetaKeyState.LOCKED || this.mMetaAltState == MetaKeyState.ACTIVE) {
            i |= 2;
        }
        return keyEvent.getUnicodeChar(i);
    }

    private boolean AllowOSToHandleKeys(int i) {
        switch (i) {
            case 24:
            case 25:
            case 26:
                return true;
            default:
                return false;
        }
    }

    public void HideSoftKeyboardOnWindowFocusChange() {
        this.mHideSoftKeyboardOnWindowFocusChange = true;
    }

    private boolean HandleShortCuts(int i, KeyEvent keyEvent) {
        if (i == 23) {
            if (this.mTrackBallPressed || this.mContextMenuVisible) {
                return true;
            }
            this.mTrackBallPressed = true;
            postCheckLongPress();
            return false;
        } else if (!keyEvent.isAltPressed()) {
            return false;
        } else {
            switch (i) {
                case 19:
                    nativeMoveCursor(2);
                    return true;
                case 20:
                    nativeMoveCursor(3);
                    return true;
                case 21:
                    nativeMoveCursor(0);
                    return true;
                case 22:
                    nativeMoveCursor(1);
                    return true;
                case R.styleable.Theme_searchViewStyle:
                    nativeDeleteTextLine();
                    return true;
                default:
                    return false;
            }
        }
    }

    public void setInputConnection(AndroidInputConnection androidInputConnection) {
        this.mInputConnection = androidInputConnection;
    }

    public void setFlashEGL(FlashEGL flashEGL) {
        this.mFlashEGL = flashEGL;
    }

    public boolean IsPasswordVisibleSettingEnabled() {
        try {
            return Settings.System.getInt(getContext().getContentResolver(), "show_password") == 1;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean IsTouchUpHandlingAllowed() {
        if (this.mEatTouchRelease) {
            return false;
        }
        return true;
    }

    public void SetSelectionMode(boolean z) {
        nativeDispatchSelectionChangeEvent(z);
    }

    public boolean onKeyPreIme(int i, KeyEvent keyEvent) {
        if (i != 4 || keyEvent.getAction() != 0) {
            return false;
        }
        DispatchSoftKeyboardEventOnBackKey();
        return false;
    }

    public void DispatchSoftKeyboardEventOnBackKey() {
        if ((this.mIsFullScreen && !this.mSurfaceChangedForSoftKeyboard) || this.mFlashEGL != null || IsIMEInFullScreen() || (!this.mSurfaceChangedForSoftKeyboard && !nativeIsEditable())) {
            nativeDispatchUserTriggeredSkDeactivateEvent();
            if (!this.mSurfaceChangedForSoftKeyboard && !nativeIsEditable()) {
                nativeShowOriginalRect();
            }
        }
    }

    public boolean IsSurfaceChangedForSoftKeyboard() {
        return this.mSurfaceChangedForSoftKeyboard;
    }

    public int getKeyboardHeight() {
        return this.mHt - getVisibleBoundHeight();
    }

    public void SetSurfaceChangedForSoftKeyboard(boolean z) {
        this.mSurfaceChangedForSoftKeyboard = z;
    }

    public VideoView getVideoView() {
        if (this.mVideoView == null) {
            this.mVideoView = new VideoViewAIR(getContext(), this.mActivityWrapper);
        }
        return this.mVideoView;
    }

    public boolean gatherTransparentRegion(Region region) {
        int[] iArr = new int[2];
        getLocationInWindow(iArr);
        region.op(iArr[0], iArr[1], this.mVisibleBoundWidth, this.mVisibleBoundHeight, Region.Op.REPLACE);
        return false;
    }
}
