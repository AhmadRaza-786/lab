package com.adobe.air;

import android.os.Build;

public class FlashEGLFactory {

    public enum FlashEGLType {
        FLASHEGL10,
        FLASHEGL14
    }

    private FlashEGLFactory() {
    }

    public static FlashEGL CreateFlashEGL() {
        FlashEGLType flashEGLType = FlashEGLType.FLASHEGL10;
        if (Build.VERSION.SDK_INT >= 21 || Build.VERSION.CODENAME.equals("L")) {
            flashEGLType = FlashEGLType.FLASHEGL14;
        }
        return CreateFlashEGL(flashEGLType);
    }

    public static FlashEGL CreateFlashEGL(FlashEGLType flashEGLType) {
        switch (flashEGLType) {
            case FLASHEGL14:
                return new FlashEGL10();
            default:
                return new FlashEGL10();
        }
    }
}
