package com.adobe.air.wand.view;

public class GestureEventData {
    public boolean mIsTransform = true;
    public float mOffsetX = 0.0f;
    public float mOffsetY = 0.0f;
    public int mPhase = 2;
    public float mRotation = 0.0f;
    public float mScaleX = 1.0f;
    public float mScaleY = 1.0f;
    public int mType = 0;
    public float mXCoord = 0.0f;
    public float mYCoord = 0.0f;

    public GestureEventData(int i, int i2, boolean z, float f, float f2, float f3, float f4, float f5, float f6, float f7) {
        this.mPhase = i;
        this.mType = i2;
        this.mIsTransform = z;
        this.mXCoord = f;
        this.mYCoord = f2;
        this.mScaleX = f3;
        this.mScaleY = f4;
        this.mRotation = f5;
        this.mOffsetX = f6;
        this.mOffsetY = f7;
    }
}
