package com.adobe.air.wand.view;

import com.adobe.air.TouchEventData;

public class TouchSensor {
    private static final String LOG_TAG = "TouchSensor";
    private boolean mGestureEventListening = false;
    private Listener mListener = null;
    private boolean mTouchEventListening = false;

    public interface Listener {
        void onGestureEvent(GestureEventData gestureEventData);

        void onTouchEvent(TouchEventData touchEventData);
    }

    public void TouchListener() {
    }

    public void setListener(Listener listener) throws Exception {
        if (listener == null) {
            throw new Exception("Invalid Listener");
        }
        this.mListener = listener;
    }

    /* access modifiers changed from: package-private */
    public void dispatchEvent(TouchEventData touchEventData) {
        if (this.mTouchEventListening && this.mListener != null) {
            this.mListener.onTouchEvent(touchEventData);
        }
    }

    /* access modifiers changed from: package-private */
    public void dispatchEvent(GestureEventData gestureEventData) {
        if (this.mGestureEventListening && this.mListener != null) {
            this.mListener.onGestureEvent(gestureEventData);
        }
    }

    public void startTouchEventListening() {
        this.mTouchEventListening = true;
    }

    public void stopTouchEventListening() {
        this.mTouchEventListening = false;
    }

    public void startGestureEventListening() {
        this.mGestureEventListening = true;
    }

    public void stopGestureEventListening() {
        this.mGestureEventListening = false;
    }

    public boolean activeTouchListening() {
        return this.mTouchEventListening;
    }

    public boolean activeGestureListening() {
        return this.mGestureEventListening;
    }
}
