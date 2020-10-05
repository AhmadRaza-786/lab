package com.distriqt.extension.inappbilling.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import com.adobe.fre.FREBitmapData;

public class FREImageUtils {
    public static Bitmap getFREObjectAsBitmap(FREBitmapData object) throws Exception {
        object.acquire();
        Bitmap bmp = Bitmap.createBitmap(object.getWidth(), object.getHeight(), Bitmap.Config.ARGB_8888);
        bmp.copyPixelsFromBuffer(object.getBits());
        object.release();
        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(new ColorMatrix(new float[]{0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f}));
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        paint.setColorFilter(colorFilter);
        canvas.drawBitmap(bmp, 0.0f, 0.0f, paint);
        return bmp;
    }

    public static void drawBitmapToBitmapData(Bitmap bitmap, FREBitmapData bitmapData) {
        Paint paint;
        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(new ColorMatrix(new float[]{0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f}));
        try {
            Bitmap encodingBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(encodingBitmap);
            try {
                paint = new Paint();
            } catch (Exception e) {
                e = e;
                Canvas canvas2 = canvas;
                e.printStackTrace();
            }
            try {
                paint.setColorFilter(colorFilter);
                canvas.drawBitmap(bitmap, 0.0f, 0.0f, paint);
                bitmapData.acquire();
                encodingBitmap.copyPixelsToBuffer(bitmapData.getBits());
                bitmapData.release();
                Paint paint2 = paint;
                Canvas canvas3 = canvas;
            } catch (Exception e2) {
                e = e2;
                Paint paint3 = paint;
                Canvas canvas4 = canvas;
                e.printStackTrace();
            }
        } catch (Exception e3) {
            e = e3;
            e.printStackTrace();
        }
    }
}
