package com.distriqt.extension.inappbilling.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageConversion {
    public static void YUV_NV21_TO_RGB(byte[] argb, byte[] yuv, int width, int height) {
        int frameSize = width * height;
        int a = 0;
        int i = 0;
        int ci = 0;
        while (i < height) {
            int j = 0;
            int cj = 0;
            int a2 = a;
            while (j < width) {
                int y = yuv[(ci * width) + cj] & 255;
                int v = yuv[((ci >> 1) * width) + frameSize + (cj & -2) + 0] & 255;
                int u = yuv[((ci >> 1) * width) + frameSize + (cj & -2) + 1] & 255;
                if (y < 16) {
                    y = 16;
                }
                int r = (int) ((1.164f * ((float) (y - 16))) + (1.596f * ((float) (v - 128))));
                int g = (int) (((1.164f * ((float) (y - 16))) - (0.813f * ((float) (v - 128)))) - (0.391f * ((float) (u - 128))));
                int b = (int) ((1.164f * ((float) (y - 16))) + (2.018f * ((float) (u - 128))));
                if (r < 0) {
                    r = 0;
                } else if (r > 255) {
                    r = 255;
                }
                if (g < 0) {
                    g = 0;
                } else if (g > 255) {
                    g = 255;
                }
                if (b < 0) {
                    b = 0;
                } else if (b > 255) {
                    b = 255;
                }
                int a3 = a2 + 1;
                argb[a2] = (byte) b;
                int a4 = a3 + 1;
                argb[a3] = (byte) g;
                int a5 = a4 + 1;
                argb[a4] = (byte) r;
                a2 = a5 + 1;
                argb[a5] = -1;
                j++;
                cj++;
            }
            i++;
            ci++;
            a = a2;
        }
    }

    public static void JPEG_TO_RGB(byte[] argb, byte[] data, int width, int height) throws Exception {
        if (width > 2048 || height > 2048) {
            throw new Exception("Image is too large to store in memory");
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        int[] buffer = new int[(width * height)];
        BitmapFactory.decodeByteArray(data, 0, data.length, options).getPixels(buffer, 0, width, 0, 0, width, height);
        int a = 0;
        int i = 0;
        while (i < height) {
            int k = width * i;
            int a2 = a;
            for (int j = 0; j < width; j++) {
                int pixel = buffer[k + j];
                int a3 = a2 + 1;
                argb[a2] = (byte) (pixel & 255);
                int a4 = a3 + 1;
                argb[a3] = (byte) ((pixel >> 8) & 255);
                int a5 = a4 + 1;
                argb[a4] = (byte) ((pixel >> 16) & 255);
                a2 = a5 + 1;
                argb[a5] = (byte) ((pixel >> 24) & 255);
            }
            i++;
            a = a2;
        }
    }

    public static int BGRA_TO_ARGB(int bgra) {
        return ((16711680 & bgra) >> 16) | ((65280 & bgra) >> 8) | ((bgra & 255) << 8) | -16777216;
    }
}
