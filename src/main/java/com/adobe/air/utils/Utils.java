package com.adobe.air.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Process;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Scanner;

public class Utils {
    private static String sRuntimePackageName;

    public static native boolean nativeConnectDebuggerSocket(String str);

    public static String getRuntimePackageName() {
        return sRuntimePackageName;
    }

    public static void setRuntimePackageName(String str) {
        sRuntimePackageName = str;
    }

    public static boolean hasCaptiveRuntime() {
        return !"com.adobe.air".equals(sRuntimePackageName);
    }

    static void KillProcess() {
        Process.killProcess(Process.myPid());
    }

    public static boolean writeStringToFile(String str, String str2) {
        File file = new File(str2);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                return false;
            }
        }
        byte[] bytes = str.getBytes();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bytes, 0, bytes.length);
            fileOutputStream.close();
            return true;
        } catch (IOException e2) {
            return false;
        }
    }

    public static void writeOut(InputStream inputStream, File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        writeThrough(inputStream, fileOutputStream);
        fileOutputStream.close();
    }

    public static void writeThrough(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bArr = new byte[4096];
        while (true) {
            int read = inputStream.read(bArr);
            if (read == -1) {
                return;
            }
            if (outputStream != null) {
                outputStream.write(bArr, 0, read);
                outputStream.flush();
            }
        }
    }

    public static void copyTo(File file, File file2) throws IOException {
        if (file.isDirectory()) {
            file2.mkdirs();
            for (File file3 : file.listFiles()) {
                copyTo(file3, new File(file2, file3.getName()));
            }
            return;
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        FileOutputStream fileOutputStream = new FileOutputStream(file2);
        copyTo((InputStream) fileInputStream, (OutputStream) fileOutputStream);
        fileInputStream.close();
        fileOutputStream.close();
    }

    public static void copyTo(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bArr = new byte[1024];
        while (true) {
            int read = inputStream.read(bArr);
            if (read > 0) {
                outputStream.write(bArr, 0, read);
            } else {
                return;
            }
        }
    }

    public static void writeBufferToFile(StringBuffer stringBuffer, File file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(stringBuffer.toString());
        fileWriter.close();
    }

    public static HashMap<String, String> parseKeyValuePairFile(File file, String str) throws FileNotFoundException, IllegalStateException {
        return parseKeyValuePairFile((InputStream) new FileInputStream(file), str);
    }

    public static HashMap<String, String> parseKeyValuePairFile(InputStream inputStream, String str) throws IllegalStateException {
        HashMap<String, String> hashMap = new HashMap<>();
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNextLine()) {
            Scanner scanner2 = new Scanner(scanner.nextLine());
            scanner2.useDelimiter(str);
            if (scanner2.hasNext()) {
                hashMap.put(scanner2.next().trim(), scanner2.next().trim());
            }
            scanner2.close();
        }
        scanner.close();
        return hashMap;
    }

    public static void writeStringToFile(String str, File file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(str);
        fileWriter.close();
    }

    public static String ReplaceTextContentWithStars(String str) {
        int length = str.length();
        char[] cArr = new char[length];
        for (int i = 0; i < length; i++) {
            cArr[i] = '*';
        }
        return new String(cArr);
    }

    public static String GetResourceStringFromRuntime(String str, Resources resources) {
        return resources.getString(resources.getIdentifier(str, "string", sRuntimePackageName));
    }

    public static View GetWidgetInViewByName(String str, Resources resources, View view) {
        return view.findViewById(resources.getIdentifier(str, "id", sRuntimePackageName));
    }

    public static View GetLayoutViewFromRuntime(String str, Resources resources, LayoutInflater layoutInflater) {
        int identifier = resources.getIdentifier(str, "layout", sRuntimePackageName);
        if (identifier != 0) {
            return layoutInflater.inflate(identifier, (ViewGroup) null);
        }
        return null;
    }

    public static String GetExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String GetSharedDataDirectory() {
        return Environment.getDataDirectory().getAbsolutePath();
    }

    public static String GetLibCorePath(Context context) {
        return GetNativeLibraryPath(context, "libCore.so");
    }

    public static String GetLibSTLPath(Context context) {
        return GetNativeLibraryPath(context, "libstlport_shared.so");
    }

    public static String GetNativeLibraryPath(Context context, String str) {
        String str2;
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(sRuntimePackageName, 0);
            Field field = ApplicationInfo.class.getField("nativeLibraryDir");
            str2 = field != null ? ((String) ApplicationInfo.class.getField("sourceDir").get(applicationInfo)).startsWith("/system/app/") ? new String("/system/lib/" + sRuntimePackageName + "/" + str) : ((String) field.get(applicationInfo)).concat("/" + str) : null;
        } catch (Exception e) {
            str2 = null;
        }
        if (str2 == null) {
            return new String("/data/data/" + sRuntimePackageName + "/lib/" + str);
        }
        return str2;
    }

    public static String GetNativeExtensionPath(Context context, String str) {
        String str2;
        try {
            str2 = ApplicationInfo.class.getField("nativeLibraryDir") != null ? ((String) ApplicationInfo.class.getField("sourceDir").get(context.getPackageManager().getApplicationInfo(sRuntimePackageName, 0))).startsWith("/system/app/") ? new String("/system/lib/" + sRuntimePackageName + "/" + str) : new String("/data/data/" + context.getPackageName() + "/lib/" + str) : null;
        } catch (Exception e) {
            str2 = null;
        }
        if (str2 == null) {
            return new String("/data/data/" + sRuntimePackageName + "/lib/" + str);
        }
        return str2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0027  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0055 A[SYNTHETIC, Splitter:B:32:0x0055] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x005a A[Catch:{ Exception -> 0x0060 }] */
    /* JADX WARNING: Removed duplicated region for block: B:46:? A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String GetTelemetrySettings(android.content.Context r5, java.lang.String r6, java.lang.String r7) {
        /*
            r1 = 0
            android.content.res.Resources r0 = r5.getResources()     // Catch:{ Exception -> 0x003f, all -> 0x0051 }
            android.content.res.AssetManager r0 = r0.getAssets()     // Catch:{ Exception -> 0x003f, all -> 0x0051 }
            r2 = 1
            java.io.InputStream r0 = r0.open(r6, r2)     // Catch:{ Exception -> 0x003f, all -> 0x0051 }
            java.io.ByteArrayOutputStream r2 = new java.io.ByteArrayOutputStream     // Catch:{ Exception -> 0x006d, all -> 0x0062 }
            r2.<init>()     // Catch:{ Exception -> 0x006d, all -> 0x0062 }
            copyTo((java.io.InputStream) r0, (java.io.OutputStream) r2)     // Catch:{ Exception -> 0x0070, all -> 0x0068 }
            java.lang.String r1 = r2.toString()     // Catch:{ Exception -> 0x0070, all -> 0x0068 }
            if (r0 == 0) goto L_0x001f
            r0.close()     // Catch:{ Exception -> 0x003c }
        L_0x001f:
            if (r2 == 0) goto L_0x0024
            r2.close()     // Catch:{ Exception -> 0x003c }
        L_0x0024:
            r0 = r1
        L_0x0025:
            if (r0 != 0) goto L_0x003b
            r1 = 0
            android.content.Context r1 = r5.createPackageContext(r7, r1)     // Catch:{ Exception -> 0x005e }
            java.lang.String r2 = "telemetry"
            r3 = 1
            android.content.SharedPreferences r1 = r1.getSharedPreferences(r2, r3)     // Catch:{ Exception -> 0x005e }
            java.lang.String r2 = "content"
            java.lang.String r3 = ""
            java.lang.String r0 = r1.getString(r2, r3)     // Catch:{ Exception -> 0x005e }
        L_0x003b:
            return r0
        L_0x003c:
            r0 = move-exception
            r0 = r1
            goto L_0x0025
        L_0x003f:
            r0 = move-exception
            r0 = r1
            r2 = r1
        L_0x0042:
            if (r0 == 0) goto L_0x0047
            r0.close()     // Catch:{ Exception -> 0x004e }
        L_0x0047:
            if (r2 == 0) goto L_0x004c
            r2.close()     // Catch:{ Exception -> 0x004e }
        L_0x004c:
            r0 = r1
            goto L_0x0025
        L_0x004e:
            r0 = move-exception
            r0 = r1
            goto L_0x0025
        L_0x0051:
            r0 = move-exception
            r2 = r1
        L_0x0053:
            if (r1 == 0) goto L_0x0058
            r1.close()     // Catch:{ Exception -> 0x0060 }
        L_0x0058:
            if (r2 == 0) goto L_0x005d
            r2.close()     // Catch:{ Exception -> 0x0060 }
        L_0x005d:
            throw r0
        L_0x005e:
            r1 = move-exception
            goto L_0x003b
        L_0x0060:
            r1 = move-exception
            goto L_0x005d
        L_0x0062:
            r2 = move-exception
            r4 = r2
            r2 = r1
            r1 = r0
            r0 = r4
            goto L_0x0053
        L_0x0068:
            r1 = move-exception
            r4 = r1
            r1 = r0
            r0 = r4
            goto L_0x0053
        L_0x006d:
            r2 = move-exception
            r2 = r1
            goto L_0x0042
        L_0x0070:
            r3 = move-exception
            goto L_0x0042
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adobe.air.utils.Utils.GetTelemetrySettings(android.content.Context, java.lang.String, java.lang.String):java.lang.String");
    }

    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            return false;
        }
        return true;
    }
}
