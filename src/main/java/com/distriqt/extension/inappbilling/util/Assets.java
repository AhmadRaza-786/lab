package com.distriqt.extension.inappbilling.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import com.distriqt.extension.inappbilling.BuildConfig;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Locale;

public class Assets {
    public static File getAssetFile(Context context, String assetName) {
        try {
            String assetFileName = assetName.replace("/", "_");
            if (!context.getFileStreamPath(assetFileName).exists()) {
                FileOutputStream soundOutputStream = context.openFileOutput(assetFileName, 1);
                InputStream assetInputStream = context.getAssets().open(assetName);
                byte[] buffer = new byte[16384];
                while (true) {
                    int length = assetInputStream.read(buffer);
                    if (length == -1) {
                        break;
                    }
                    soundOutputStream.write(buffer, 0, length);
                }
                soundOutputStream.close();
                assetInputStream.close();
            }
            return context.getFileStreamPath(assetFileName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void listAssets(Context context) {
        listAssetsInDirectory(context.getAssets(), BuildConfig.FLAVOR);
    }

    private static void listAssetsInDirectory(AssetManager assetManager, String path) {
        try {
            if (!path.equals("images") && !path.equals("webkit") && !path.equals("sounds") && !path.equals("META-INF")) {
                String[] files = assetManager.list(path);
                for (int i = 0; i < files.length; i++) {
                    Log.d("Assets", String.format(Locale.UK, "%s%s", new Object[]{path, files[i]}));
                    listAssetsInDirectory(assetManager, path + (path.length() > 0 ? "/" : BuildConfig.FLAVOR) + files[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
