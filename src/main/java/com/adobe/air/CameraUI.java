package com.adobe.air;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import com.adobe.air.AndroidActivityWrapper;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class CameraUI implements AndroidActivityWrapper.ActivityResultCallback {
    public static final int ERROR_ACTIVITY_DESTROYED = 4;
    public static final int ERROR_CAMERA_BUSY = 1;
    public static final int ERROR_CAMERA_ERROR = 2;
    public static final int ERROR_CAMERA_UNAVAILABLE = 3;
    private static final String LOG_TAG = "CameraUI";
    private static final String PHONE_STORAGE = "phoneStorage";
    public static final int REQUESTED_MEDIA_TYPE_IMAGE = 1;
    public static final int REQUESTED_MEDIA_TYPE_INVALID = 0;
    public static final int REQUESTED_MEDIA_TYPE_VIDEO = 2;
    private static String sCameraRollPath = null;
    private static CameraUI sCameraUI = null;
    private boolean mCameraBusy = false;
    private String mImagePath = null;
    private long mLastClientId = 0;

    private native void nativeOnCameraCancel(long j);

    private native void nativeOnCameraError(long j, int i);

    private native void nativeOnCameraResult(long j, String str, String str2, String str3);

    private void onCameraError(int i) {
        if (this.mLastClientId != 0) {
            nativeOnCameraError(this.mLastClientId, i);
            this.mLastClientId = 0;
        }
    }

    private void onCameraCancel() {
        if (this.mLastClientId != 0) {
            nativeOnCameraCancel(this.mLastClientId);
            this.mLastClientId = 0;
        }
    }

    private void onCameraResult(String str, String str2, String str3) {
        if (this.mLastClientId != 0) {
            nativeOnCameraResult(this.mLastClientId, str, str2, str3);
            this.mLastClientId = 0;
        }
    }

    private CameraUI() {
    }

    public static synchronized CameraUI getCameraUI() {
        CameraUI cameraUI;
        synchronized (CameraUI.class) {
            if (sCameraUI == null) {
                sCameraUI = new CameraUI();
                AndroidActivityWrapper.GetAndroidActivityWrapper().addActivityResultListener(sCameraUI);
            }
            cameraUI = sCameraUI;
        }
        return cameraUI;
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public void unregisterCallbacks(long j) {
        if (this.mLastClientId == j) {
            this.mLastClientId = 0;
        }
    }

    private String toMediaType(String str) {
        if (str == null) {
            return null;
        }
        if (str.startsWith("image/")) {
            return new String("image");
        }
        if (str.startsWith("video/")) {
            return new String("video");
        }
        return null;
    }

    /* JADX INFO: finally extract failed */
    private File getFileFromUri(Uri uri, Activity activity) {
        Cursor cursorFromUri = getCursorFromUri(uri, activity, new String[]{"_data"});
        if (cursorFromUri == null) {
            return null;
        }
        try {
            File file = new File(cursorFromUri.getString(cursorFromUri.getColumnIndexOrThrow("_data")));
            cursorFromUri.close();
            return file;
        } catch (IllegalArgumentException e) {
            cursorFromUri.close();
            return null;
        } catch (Throwable th) {
            cursorFromUri.close();
            throw th;
        }
    }

    private Cursor getCursorFromUri(Uri uri, Activity activity, String[] strArr) {
        Throwable th;
        boolean z;
        boolean z2;
        boolean z3;
        Cursor cursor = null;
        boolean z4 = true;
        try {
            Cursor query = activity.getContentResolver().query(uri, strArr, (String) null, (String[]) null, (String) null);
            try {
                if (query.moveToFirst()) {
                    if (query != null) {
                        z3 = true;
                    } else {
                        z3 = false;
                    }
                    if (query.moveToFirst()) {
                        z4 = false;
                    }
                    if (z3 && z4) {
                        query.close();
                    }
                    return query;
                }
                query.close();
                if (query != null) {
                    z2 = true;
                } else {
                    z2 = false;
                }
                if (query.moveToFirst()) {
                    z4 = false;
                }
                if (z2 && z4) {
                    query.close();
                }
                return null;
            } catch (Throwable th2) {
                cursor = query;
                th = th2;
            }
        } catch (Throwable th3) {
            th = th3;
        }
        if (cursor != null) {
            z = true;
        } else {
            z = false;
        }
        if (cursor.moveToFirst()) {
            z4 = false;
        }
        if (z && z4) {
            cursor.close();
        }
        throw th;
    }

    private void processImageSuccessResult() {
        String str = new String("image");
        String name = new File(this.mImagePath).getName();
        MediaScannerConnection.scanFile(AndroidActivityWrapper.GetAndroidActivityWrapper().getDefaultContext(), new String[]{this.mImagePath}, (String[]) null, (MediaScannerConnection.OnScanCompletedListener) null);
        onCameraResult(this.mImagePath, str, name);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0078, code lost:
        r2 = null;
        r0 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x007f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0080, code lost:
        r4.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0083, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0089, code lost:
        r2 = null;
        r0 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x008d, code lost:
        r2 = null;
        r1 = r0;
        r0 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0092, code lost:
        r2 = null;
        r1 = r0;
        r0 = r3;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0073  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x007f A[ExcHandler: all (r0v7 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:3:0x0027] */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0084  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void processVideoSuccessResult(android.content.Intent r9) {
        /*
            r8 = this;
            r7 = 2
            r1 = 0
            r0 = 3
            java.lang.String[] r0 = new java.lang.String[r0]
            r2 = 0
            java.lang.String r3 = "_data"
            r0[r2] = r3
            r2 = 1
            java.lang.String r3 = "mime_type"
            r0[r2] = r3
            java.lang.String r2 = "_display_name"
            r0[r7] = r2
            android.net.Uri r2 = r9.getData()
            com.adobe.air.AndroidActivityWrapper r3 = com.adobe.air.AndroidActivityWrapper.GetAndroidActivityWrapper()
            android.app.Activity r3 = r3.getActivity()
            android.database.Cursor r4 = r8.getCursorFromUri(r2, r3, r0)
            if (r4 == 0) goto L_0x009e
            java.lang.String r0 = "_data"
            int r0 = r4.getColumnIndexOrThrow(r0)     // Catch:{ IllegalArgumentException -> 0x0077, all -> 0x007f }
            java.lang.String r2 = "mime_type"
            int r2 = r4.getColumnIndexOrThrow(r2)     // Catch:{ IllegalArgumentException -> 0x0077, all -> 0x007f }
            java.lang.String r3 = "_display_name"
            int r5 = r4.getColumnIndexOrThrow(r3)     // Catch:{ IllegalArgumentException -> 0x0077, all -> 0x007f }
            java.lang.String r3 = r4.getString(r0)     // Catch:{ IllegalArgumentException -> 0x0077, all -> 0x007f }
            if (r3 == 0) goto L_0x009c
            java.lang.String r0 = r4.getString(r2)     // Catch:{ IllegalArgumentException -> 0x0088, all -> 0x007f }
            java.lang.String r0 = r8.toMediaType(r0)     // Catch:{ IllegalArgumentException -> 0x0088, all -> 0x007f }
            if (r0 != 0) goto L_0x004f
            java.lang.String r2 = new java.lang.String     // Catch:{ IllegalArgumentException -> 0x008c, all -> 0x007f }
            java.lang.String r6 = "video"
            r2.<init>(r6)     // Catch:{ IllegalArgumentException -> 0x008c, all -> 0x007f }
            r0 = r2
        L_0x004f:
            java.lang.String r2 = r4.getString(r5)     // Catch:{ IllegalArgumentException -> 0x0091, all -> 0x007f }
            if (r2 != 0) goto L_0x009a
            java.lang.String r1 = new java.lang.String     // Catch:{ IllegalArgumentException -> 0x0096, all -> 0x007f }
            java.lang.String r5 = ""
            r1.<init>(r5)     // Catch:{ IllegalArgumentException -> 0x0096, all -> 0x007f }
        L_0x005c:
            r4.close()
            r2 = r1
            r1 = r0
        L_0x0061:
            if (r1 == 0) goto L_0x006b
            java.lang.String r0 = "image"
            boolean r0 = r1.equals(r0)
            if (r0 != 0) goto L_0x0073
        L_0x006b:
            java.lang.String r0 = "video"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x0084
        L_0x0073:
            r8.onCameraResult(r3, r1, r2)
        L_0x0076:
            return
        L_0x0077:
            r0 = move-exception
            r2 = r1
            r0 = r1
        L_0x007a:
            r4.close()
            r3 = r0
            goto L_0x0061
        L_0x007f:
            r0 = move-exception
            r4.close()
            throw r0
        L_0x0084:
            r8.onCameraError(r7)
            goto L_0x0076
        L_0x0088:
            r0 = move-exception
            r2 = r1
            r0 = r3
            goto L_0x007a
        L_0x008c:
            r2 = move-exception
            r2 = r1
            r1 = r0
            r0 = r3
            goto L_0x007a
        L_0x0091:
            r2 = move-exception
            r2 = r1
            r1 = r0
            r0 = r3
            goto L_0x007a
        L_0x0096:
            r1 = move-exception
            r1 = r0
            r0 = r3
            goto L_0x007a
        L_0x009a:
            r1 = r2
            goto L_0x005c
        L_0x009c:
            r0 = r1
            goto L_0x005c
        L_0x009e:
            r2 = r1
            r3 = r1
            goto L_0x0061
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adobe.air.CameraUI.processVideoSuccessResult(android.content.Intent):void");
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 3 || i == 4) {
            this.mCameraBusy = false;
            if (this.mLastClientId != 0) {
                switch (i2) {
                    case -1:
                        if (i == 3) {
                            if (this.mImagePath != null) {
                                processImageSuccessResult();
                                this.mImagePath = null;
                                return;
                            }
                            onCameraCancel();
                            return;
                        } else if (i == 4) {
                            processVideoSuccessResult(intent);
                            return;
                        } else {
                            return;
                        }
                    case 0:
                        if (this.mImagePath != null) {
                            this.mImagePath = null;
                        }
                        onCameraCancel();
                        return;
                    default:
                        if (this.mImagePath != null) {
                            this.mImagePath = null;
                        }
                        onCameraError(2);
                        return;
                }
            }
        }
    }

    public void launch(long j, int i) {
        int videoCaptureWork;
        if (j != 0) {
            if (this.mCameraBusy) {
                nativeOnCameraError(j, 1);
                return;
            }
            if (this.mLastClientId != 0) {
                onCameraError(1);
            }
            this.mLastClientId = j;
            this.mCameraBusy = true;
            switch (i) {
                case 1:
                    videoCaptureWork = stillPictureWork();
                    break;
                case 2:
                    videoCaptureWork = videoCaptureWork();
                    break;
                default:
                    videoCaptureWork = 3;
                    break;
            }
            if (videoCaptureWork != 0) {
                this.mCameraBusy = false;
                onCameraError(videoCaptureWork);
            }
        }
    }

    private int videoCaptureWork() {
        try {
            Activity activity = AndroidActivityWrapper.GetAndroidActivityWrapper().getActivity();
            if (activity == null) {
                return 4;
            }
            Intent intent = new Intent("android.media.action.VIDEO_CAPTURE");
            intent.putExtra("android.intent.extra.videoQuality", 0);
            activity.startActivityForResult(intent, 4);
            return 0;
        } catch (ActivityNotFoundException e) {
            return 3;
        }
    }

    private String getCameraRollDirectory(Activity activity) {
        Uri uri;
        Uri uri2;
        if (sCameraRollPath != null) {
            return sCameraRollPath;
        }
        try {
            uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        } catch (Exception e) {
            uri = null;
        }
        if (uri == null) {
            try {
                uri2 = activity.getContentResolver().insert(MediaStore.Images.Media.getContentUri(PHONE_STORAGE), new ContentValues());
            } catch (Exception e2) {
                uri2 = uri;
            }
        } else {
            uri2 = uri;
        }
        if (uri2 != null) {
            try {
                sCameraRollPath = getFileFromUri(uri2, activity).getParent();
            } catch (ActivityNotFoundException e3) {
            } catch (NullPointerException e4) {
            } finally {
                activity.getContentResolver().delete(uri2, (String) null, (String[]) null);
            }
        } else {
            File externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (externalStoragePublicDirectory.exists()) {
                sCameraRollPath = externalStoragePublicDirectory.toString();
            }
        }
        return sCameraRollPath;
    }

    private int stillPictureWork() {
        int i;
        Activity activity = AndroidActivityWrapper.GetAndroidActivityWrapper().getActivity();
        if (activity == null) {
            return 4;
        }
        if (getCameraRollDirectory(activity) == null) {
            return 2;
        }
        String str = getCameraRollDirectory(activity) + "/" + new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss").format(new Date(System.currentTimeMillis())) + ".jpg";
        File file = new File(str);
        if (file.exists()) {
            return 2;
        }
        try {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra("output", Uri.fromFile(file));
            activity.startActivityForResult(intent, 3);
            i = 0;
        } catch (ActivityNotFoundException e) {
            str = null;
            i = 3;
        } catch (NullPointerException e2) {
            str = null;
            i = 2;
        }
        this.mImagePath = str;
        return i;
    }
}
