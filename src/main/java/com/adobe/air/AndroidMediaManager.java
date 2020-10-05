package com.adobe.air;

import android.app.Activity;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import com.adobe.air.AndroidActivityWrapper;
import com.distriqt.extension.inappbilling.BuildConfig;

public class AndroidMediaManager {
    public static final int ERROR_ACTIVITY_DESTROYED = 2;
    public static final int ERROR_IMAGE_PICKER_NOT_FOUND = 1;
    /* access modifiers changed from: private */
    public static long MediaManagerObjectPointer = 0;
    private static final String PHONE_STORAGE = "phoneStorage";
    private AndroidActivityWrapper.ActivityResultCallback mActivityResultCB = null;
    /* access modifiers changed from: private */
    public boolean mCallbacksRegistered = false;

    public native void useImagePickerData(long j, boolean z, boolean z2, String str, String str2, String str3);

    public native void useStreamData(long j, boolean z, boolean z2, String str);

    public AndroidMediaManager() {
        MediaManagerObjectPointer = 0;
    }

    public void registerCallbacks() {
        doCallbackRegistration(true);
    }

    public void unregisterCallbacks() {
        doCallbackRegistration(false);
    }

    private void doCallbackRegistration(boolean z) {
        this.mCallbacksRegistered = z;
        if (z) {
            if (this.mActivityResultCB == null) {
                this.mActivityResultCB = new AndroidActivityWrapper.ActivityResultCallback() {
                    public void onActivityResult(int i, int i2, Intent intent) {
                        if (i == 2 && AndroidMediaManager.MediaManagerObjectPointer != 0 && AndroidMediaManager.this.mCallbacksRegistered) {
                            AndroidMediaManager.this.onBrowseImageResult(i2, intent, AndroidActivityWrapper.GetAndroidActivityWrapper().getActivity());
                            AndroidMediaManager.this.unregisterCallbacks();
                        }
                    }
                };
            }
            AndroidActivityWrapper.GetAndroidActivityWrapper().addActivityResultListener(this.mActivityResultCB);
        } else if (this.mActivityResultCB != null) {
            AndroidActivityWrapper.GetAndroidActivityWrapper().removeActivityResultListener(this.mActivityResultCB);
            this.mActivityResultCB = null;
        }
    }

    public static boolean AddImage(Application application, Bitmap bitmap, boolean z) {
        String str;
        String str2 = null;
        if (application != null) {
            ContentResolver contentResolver = application.getContentResolver();
            try {
                str2 = MediaStore.Images.Media.insertImage(contentResolver, bitmap, (String) null, (String) null);
            } catch (Exception e) {
            }
            if (str2 == null) {
                str = SaveImage(PHONE_STORAGE, contentResolver, bitmap, z);
            } else {
                str = str2;
            }
            if (str != null) {
                try {
                    Cursor query = contentResolver.query(Uri.parse(str), new String[]{"_data"}, (String) null, (String[]) null, (String) null);
                    if (query != null) {
                        int columnIndexOrThrow = query.getColumnIndexOrThrow("_data");
                        query.moveToFirst();
                        MediaScannerConnection.scanFile(AndroidActivityWrapper.GetAndroidActivityWrapper().getDefaultContext(), new String[]{query.getString(columnIndexOrThrow)}, (String[]) null, (MediaScannerConnection.OnScanCompletedListener) null);
                    }
                } catch (Exception e2) {
                }
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: finally extract failed */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0065  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x008f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String SaveImage(java.lang.String r11, android.content.ContentResolver r12, android.graphics.Bitmap r13, boolean r14) {
        /*
            r9 = 0
            android.content.ContentValues r0 = new android.content.ContentValues     // Catch:{ Exception -> 0x0072 }
            r0.<init>()     // Catch:{ Exception -> 0x0072 }
            if (r14 == 0) goto L_0x006a
            java.lang.String r1 = "mime_type"
            java.lang.String r2 = "image/jpeg"
            r0.put(r1, r2)     // Catch:{ Exception -> 0x0072 }
        L_0x000f:
            java.util.Date r1 = new java.util.Date     // Catch:{ Exception -> 0x0072 }
            r1.<init>()     // Catch:{ Exception -> 0x0072 }
            java.lang.String r2 = "datetaken"
            long r4 = r1.getTime()     // Catch:{ Exception -> 0x0072 }
            java.lang.Long r3 = java.lang.Long.valueOf(r4)     // Catch:{ Exception -> 0x0072 }
            r0.put(r2, r3)     // Catch:{ Exception -> 0x0072 }
            java.lang.String r2 = "date_added"
            long r4 = r1.getTime()     // Catch:{ Exception -> 0x0072 }
            r6 = 1000(0x3e8, double:4.94E-321)
            long r4 = r4 / r6
            java.lang.Long r1 = java.lang.Long.valueOf(r4)     // Catch:{ Exception -> 0x0072 }
            r0.put(r2, r1)     // Catch:{ Exception -> 0x0072 }
            android.net.Uri r1 = android.provider.MediaStore.Images.Media.getContentUri(r11)     // Catch:{ Exception -> 0x0072 }
            android.net.Uri r0 = r12.insert(r1, r0)     // Catch:{ Exception -> 0x0072 }
            if (r0 == 0) goto L_0x0063
            java.io.OutputStream r10 = r12.openOutputStream(r0)     // Catch:{ Exception -> 0x0088 }
            android.graphics.Bitmap$CompressFormat r1 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ Exception -> 0x007b }
            r2 = 90
            r13.compress(r1, r2, r10)     // Catch:{ Exception -> 0x007b }
            long r4 = android.content.ContentUris.parseId(r0)     // Catch:{ Exception -> 0x007b }
            r6 = 1134559232(0x43a00000, float:320.0)
            r7 = 1131413504(0x43700000, float:240.0)
            r8 = 1
            r1 = r11
            r2 = r12
            r3 = r13
            android.graphics.Bitmap r3 = SaveThumbnail(r1, r2, r3, r4, r6, r7, r8)     // Catch:{ Exception -> 0x007b }
            r6 = 1112014848(0x42480000, float:50.0)
            r7 = 1112014848(0x42480000, float:50.0)
            r8 = 3
            r1 = r11
            r2 = r12
            SaveThumbnail(r1, r2, r3, r4, r6, r7, r8)     // Catch:{ Exception -> 0x007b }
            r10.close()     // Catch:{ Exception -> 0x0088 }
        L_0x0063:
            if (r0 == 0) goto L_0x008f
            java.lang.String r0 = r0.toString()
        L_0x0069:
            return r0
        L_0x006a:
            java.lang.String r1 = "mime_type"
            java.lang.String r2 = "image/png"
            r0.put(r1, r2)     // Catch:{ Exception -> 0x0072 }
            goto L_0x000f
        L_0x0072:
            r0 = move-exception
            r0 = r9
        L_0x0074:
            if (r0 == 0) goto L_0x0063
            r12.delete(r0, r9, r9)
            r0 = r9
            goto L_0x0063
        L_0x007b:
            r1 = move-exception
            if (r0 == 0) goto L_0x0084
            r1 = 0
            r2 = 0
            r12.delete(r0, r1, r2)     // Catch:{ all -> 0x008a }
            r0 = r9
        L_0x0084:
            r10.close()     // Catch:{ Exception -> 0x0088 }
            goto L_0x0063
        L_0x0088:
            r1 = move-exception
            goto L_0x0074
        L_0x008a:
            r1 = move-exception
            r10.close()     // Catch:{ Exception -> 0x0088 }
            throw r1     // Catch:{ Exception -> 0x0088 }
        L_0x008f:
            r0 = r9
            goto L_0x0069
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adobe.air.AndroidMediaManager.SaveImage(java.lang.String, android.content.ContentResolver, android.graphics.Bitmap, boolean):java.lang.String");
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0078  */
    /* JADX WARNING: Removed duplicated region for block: B:22:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static final android.graphics.Bitmap SaveThumbnail(java.lang.String r9, android.content.ContentResolver r10, android.graphics.Bitmap r11, long r12, float r14, float r15, int r16) {
        /*
            r7 = 0
            if (r11 != 0) goto L_0x0005
            r0 = r7
        L_0x0004:
            return r0
        L_0x0005:
            android.graphics.Matrix r5 = new android.graphics.Matrix
            r5.<init>()
            int r0 = r11.getWidth()     // Catch:{ Exception -> 0x007a }
            float r0 = (float) r0     // Catch:{ Exception -> 0x007a }
            float r0 = r14 / r0
            int r1 = r11.getHeight()     // Catch:{ Exception -> 0x007a }
            float r1 = (float) r1     // Catch:{ Exception -> 0x007a }
            float r1 = r15 / r1
            r5.setScale(r0, r1)     // Catch:{ Exception -> 0x007a }
            r1 = 0
            r2 = 0
            int r3 = r11.getWidth()     // Catch:{ Exception -> 0x007a }
            int r4 = r11.getHeight()     // Catch:{ Exception -> 0x007a }
            r6 = 1
            r0 = r11
            android.graphics.Bitmap r0 = android.graphics.Bitmap.createBitmap(r0, r1, r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x007a }
            android.content.ContentValues r1 = new android.content.ContentValues
            r2 = 4
            r1.<init>(r2)
            java.lang.String r2 = "kind"
            java.lang.Integer r3 = java.lang.Integer.valueOf(r16)
            r1.put(r2, r3)
            java.lang.String r2 = "image_id"
            int r3 = (int) r12
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r1.put(r2, r3)
            java.lang.String r2 = "height"
            int r3 = r0.getHeight()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r1.put(r2, r3)
            java.lang.String r2 = "width"
            int r3 = r0.getWidth()
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r1.put(r2, r3)
            android.net.Uri r2 = android.provider.MediaStore.Images.Thumbnails.getContentUri(r9)     // Catch:{ Exception -> 0x007d }
            android.net.Uri r1 = r10.insert(r2, r1)     // Catch:{ Exception -> 0x007d }
            if (r1 == 0) goto L_0x0076
            java.io.OutputStream r2 = r10.openOutputStream(r1)     // Catch:{ Exception -> 0x0086 }
            android.graphics.Bitmap$CompressFormat r3 = android.graphics.Bitmap.CompressFormat.JPEG     // Catch:{ Exception -> 0x0086 }
            r4 = 100
            r0.compress(r3, r4, r2)     // Catch:{ Exception -> 0x0086 }
            r2.close()     // Catch:{ Exception -> 0x0086 }
        L_0x0076:
            if (r1 != 0) goto L_0x0004
            r0 = r7
            goto L_0x0004
        L_0x007a:
            r0 = move-exception
            r0 = r7
            goto L_0x0004
        L_0x007d:
            r1 = move-exception
            r1 = r7
        L_0x007f:
            if (r1 == 0) goto L_0x0076
            r10.delete(r1, r7, r7)
            r1 = r7
            goto L_0x0076
        L_0x0086:
            r2 = move-exception
            goto L_0x007f
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adobe.air.AndroidMediaManager.SaveThumbnail(java.lang.String, android.content.ContentResolver, android.graphics.Bitmap, long, float, float, int):android.graphics.Bitmap");
    }

    public int BrowseImage(long j) {
        int i = 0;
        try {
            AndroidActivityWrapper GetAndroidActivityWrapper = AndroidActivityWrapper.GetAndroidActivityWrapper();
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction("android.intent.action.PICK");
            if (GetAndroidActivityWrapper.getActivity() != null) {
                GetAndroidActivityWrapper.getActivity().startActivityForResult(Intent.createChooser(intent, BuildConfig.FLAVOR), 2);
            } else {
                i = 2;
            }
        } catch (ActivityNotFoundException e) {
            i = 1;
        }
        if (i == 0) {
            registerCallbacks();
            MediaManagerObjectPointer = j;
        }
        return i;
    }

    public void onBrowseImageResult(int i, Intent intent, Activity activity) {
        if (i == 0) {
            useImagePickerData(MediaManagerObjectPointer, false, true, BuildConfig.FLAVOR, BuildConfig.FLAVOR, BuildConfig.FLAVOR);
        } else if (i == -1) {
            try {
                Activity activity2 = activity;
                Cursor managedQuery = activity2.managedQuery(intent.getData(), new String[]{"_data", "mime_type", "_display_name"}, (String) null, (String[]) null, (String) null);
                if (managedQuery == null) {
                    useImagePickerData(MediaManagerObjectPointer, false, false, BuildConfig.FLAVOR, BuildConfig.FLAVOR, BuildConfig.FLAVOR);
                    return;
                }
                int columnIndexOrThrow = managedQuery.getColumnIndexOrThrow("_data");
                int columnIndexOrThrow2 = managedQuery.getColumnIndexOrThrow("_display_name");
                managedQuery.moveToFirst();
                String string = managedQuery.getString(columnIndexOrThrow);
                String string2 = managedQuery.getString(columnIndexOrThrow2);
                if (string == null || string.startsWith("http")) {
                    useStreamData(MediaManagerObjectPointer, true, true, intent.getDataString());
                    return;
                }
                useImagePickerData(MediaManagerObjectPointer, true, true, string, "image", string2);
            } catch (IllegalArgumentException e) {
                useImagePickerData(MediaManagerObjectPointer, false, false, BuildConfig.FLAVOR, BuildConfig.FLAVOR, BuildConfig.FLAVOR);
            } catch (Exception e2) {
                useImagePickerData(MediaManagerObjectPointer, false, false, BuildConfig.FLAVOR, BuildConfig.FLAVOR, BuildConfig.FLAVOR);
            }
        }
    }
}
