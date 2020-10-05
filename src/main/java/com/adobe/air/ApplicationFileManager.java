package com.adobe.air;

import android.content.pm.PackageManager;
import android.os.Bundle;
import com.distriqt.extension.inappbilling.BuildConfig;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public final class ApplicationFileManager {
    private static final String APP_PREFIX = "app";
    private static final String APP_XML_PATH = "META-INF/AIR/application.xml";
    private static final String ASSET_STRING = "assets";
    public static String sAndroidPackageName;
    public static String sApkPath;
    public static String sAppDataPath;
    public static String sInitialContentName;
    private final int BUFFER_SIZE = 8192;
    private final int DEFAULT_SIZE = -1;
    private HashMap<Object, Object> mFileInfoMap = new HashMap<>();

    public static void setAndroidPackageName(String str) {
        sAndroidPackageName = str;
    }

    public static void setAndroidAPKPath(String str) {
        sApkPath = str;
    }

    private static void setAndroidDataPath(String str) {
        sAppDataPath = str;
    }

    public static String getAndroidApkPath() {
        return sApkPath;
    }

    public static String getAndroidAppDataPath() {
        return sAppDataPath;
    }

    public static String getAppXMLRoot() {
        return getAndroidUnzipContentPath() + File.separatorChar + APP_XML_PATH;
    }

    public static String getAppRoot() {
        return getAndroidUnzipContentPath() + File.separatorChar + ASSET_STRING;
    }

    public static String getAndroidUnzipContentPath() {
        return sAppDataPath;
    }

    private File getApkPathFile() {
        return new File(getAndroidApkPath());
    }

    private static void setInitialContentName(String str) {
        sInitialContentName = str;
    }

    ApplicationFileManager() {
        procZipContents(getApkPathFile());
    }

    public static boolean deleteUnzippedContents(String str) {
        File file = new File(str);
        if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            for (File absolutePath : listFiles) {
                deleteUnzippedContents(absolutePath.getAbsolutePath());
            }
        }
        return file.delete();
    }

    public void deleteFile(String str) {
        new File(str).delete();
    }

    public void procZipContents(File file) {
        try {
            ZipFile zipFile = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) entries.nextElement();
                String name = zipEntry.getName();
                if (name.substring(0, ASSET_STRING.length()).equals(ASSET_STRING)) {
                    this.mFileInfoMap.put(name, new FileInfo(zipEntry.getSize(), true, false));
                    File file2 = new File(name);
                    while (file2.getParent() != null && ((FileInfo) this.mFileInfoMap.get(file2.getParent())) == null) {
                        this.mFileInfoMap.put(file2.getParent(), new FileInfo(-1, false, true));
                        file2 = new File(file2.getParent());
                    }
                }
            }
            zipFile.close();
        } catch (Exception e) {
        }
    }

    public boolean fileExists(String str) {
        return this.mFileInfoMap.containsKey(!str.equals(BuildConfig.FLAVOR) ? new StringBuilder().append(ASSET_STRING).append(File.separator).append(str).toString() : ASSET_STRING);
    }

    public boolean isDirectory(String str) {
        FileInfo fileInfo = (FileInfo) this.mFileInfoMap.get(!str.equals(BuildConfig.FLAVOR) ? ASSET_STRING + File.separator + str : ASSET_STRING);
        return fileInfo != null && fileInfo.mIsDirectory;
    }

    public long getLSize(String str) {
        FileInfo fileInfo = (FileInfo) this.mFileInfoMap.get(ASSET_STRING + File.separator + str);
        if (fileInfo == null || fileInfo.mFileSize == -1) {
            return 0;
        }
        return fileInfo.mFileSize;
    }

    public boolean addToCache(String str) {
        if (sInitialContentName == null || str.indexOf(sInitialContentName) == -1) {
            return false;
        }
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0085, code lost:
        r0 = r1.getInputStream(r0);
        new java.io.File(r4.getParent()).mkdirs();
        r2 = new java.io.BufferedOutputStream(new java.io.FileOutputStream(r4), 8192);
        r3 = new byte[8192];
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x00a5, code lost:
        r4 = r0.read(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x00aa, code lost:
        if (r4 == -1) goto L_0x00bb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x00ac, code lost:
        r2.write(r3, 0, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:?, code lost:
        closeInputStream(r0);
        closeOutputStream(r2);
     */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x00b5 A[SYNTHETIC, Splitter:B:21:0x00b5] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00ff A[SYNTHETIC, Splitter:B:36:0x00ff] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean readFileName(java.lang.String r12) {
        /*
            r11 = this;
            r9 = 1
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "assets"
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.String r1 = java.io.File.separator
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.StringBuilder r0 = r0.append(r12)
            java.lang.String r2 = r0.toString()
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = getAndroidUnzipContentPath()
            java.lang.StringBuilder r0 = r0.append(r1)
            char r1 = java.io.File.separatorChar
            java.lang.StringBuilder r0 = r0.append(r1)
            java.lang.String r3 = r0.toString()
            java.io.File r4 = new java.io.File
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.StringBuilder r0 = r0.append(r3)
            java.lang.StringBuilder r0 = r0.append(r2)
            java.lang.String r0 = r0.toString()
            r4.<init>(r0)
            boolean r0 = r4.exists()
            if (r0 == 0) goto L_0x004e
        L_0x004d:
            return r9
        L_0x004e:
            java.io.File r5 = r11.getApkPathFile()
            r0 = 0
            java.util.zip.ZipFile r1 = new java.util.zip.ZipFile     // Catch:{ Exception -> 0x010a, all -> 0x0105 }
            r1.<init>(r5)     // Catch:{ Exception -> 0x010a, all -> 0x0105 }
            java.util.Enumeration r5 = r1.entries()     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
        L_0x005c:
            boolean r0 = r5.hasMoreElements()     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            if (r0 == 0) goto L_0x00c1
            java.lang.Object r0 = r5.nextElement()     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            java.util.zip.ZipEntry r0 = (java.util.zip.ZipEntry) r0     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            java.lang.String r6 = r0.getName()     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            r7 = 0
            java.lang.String r8 = "assets"
            int r8 = r8.length()     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            java.lang.String r7 = r6.substring(r7, r8)     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            java.lang.String r8 = "assets"
            boolean r7 = r7.equals(r8)     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            if (r7 == 0) goto L_0x005c
            boolean r7 = r6.equals(r2)     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            if (r7 == 0) goto L_0x00c9
            java.io.InputStream r0 = r1.getInputStream(r0)     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            java.io.File r2 = new java.io.File     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            java.lang.String r3 = r4.getParent()     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            r2.<init>(r3)     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            r2.mkdirs()     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            java.io.BufferedOutputStream r2 = new java.io.BufferedOutputStream     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            java.io.FileOutputStream r3 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            r3.<init>(r4)     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            r4 = 8192(0x2000, float:1.14794E-41)
            r2.<init>(r3, r4)     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            r3 = 8192(0x2000, float:1.14794E-41)
            byte[] r3 = new byte[r3]     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
        L_0x00a5:
            int r4 = r0.read(r3)     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            r5 = -1
            if (r4 == r5) goto L_0x00bb
            r5 = 0
            r2.write(r3, r5, r4)     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            goto L_0x00a5
        L_0x00b1:
            r0 = move-exception
            r0 = r1
        L_0x00b3:
            if (r0 == 0) goto L_0x004d
            r0.close()     // Catch:{ Exception -> 0x00b9 }
            goto L_0x004d
        L_0x00b9:
            r0 = move-exception
            goto L_0x004d
        L_0x00bb:
            r11.closeInputStream(r0)     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            r11.closeOutputStream(r2)     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
        L_0x00c1:
            if (r1 == 0) goto L_0x004d
            r1.close()     // Catch:{ Exception -> 0x00c7 }
            goto L_0x004d
        L_0x00c7:
            r0 = move-exception
            goto L_0x004d
        L_0x00c9:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            r0.<init>()     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            java.lang.StringBuilder r0 = r0.append(r2)     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            java.lang.String r7 = "/"
            java.lang.StringBuilder r0 = r0.append(r7)     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            boolean r0 = r6.startsWith(r0)     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            if (r0 == 0) goto L_0x005c
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            r4.<init>()     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            java.lang.StringBuilder r3 = r4.append(r3)     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            java.lang.StringBuilder r2 = r3.append(r2)     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            r0.<init>(r2)     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            r0.mkdirs()     // Catch:{ Exception -> 0x00b1, all -> 0x00fc }
            goto L_0x00c1
        L_0x00fc:
            r0 = move-exception
        L_0x00fd:
            if (r1 == 0) goto L_0x0102
            r1.close()     // Catch:{ Exception -> 0x0103 }
        L_0x0102:
            throw r0
        L_0x0103:
            r1 = move-exception
            goto L_0x0102
        L_0x0105:
            r1 = move-exception
            r10 = r1
            r1 = r0
            r0 = r10
            goto L_0x00fd
        L_0x010a:
            r1 = move-exception
            goto L_0x00b3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adobe.air.ApplicationFileManager.readFileName(java.lang.String):boolean");
    }

    public void copyFolder(String str) {
        String str2 = !str.equals(BuildConfig.FLAVOR) ? ASSET_STRING + File.separator + str : ASSET_STRING;
        String str3 = getAndroidUnzipContentPath() + File.separatorChar;
        try {
            ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(getApkPathFile()), 8192));
            while (true) {
                ZipEntry nextEntry = zipInputStream.getNextEntry();
                if (nextEntry != null) {
                    String name = nextEntry.getName();
                    if (name.substring(0, ASSET_STRING.length()).equals(ASSET_STRING) && name.startsWith(str2)) {
                        File file = new File(str3 + name);
                        new File(file.getParent()).mkdirs();
                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file), 8192);
                        byte[] bArr = new byte[8192];
                        while (true) {
                            int read = zipInputStream.read(bArr, 0, 8192);
                            if (read == -1) {
                                break;
                            }
                            bufferedOutputStream.write(bArr, 0, read);
                        }
                        closeOutputStream(bufferedOutputStream);
                    }
                } else {
                    closeInputStream(zipInputStream);
                    return;
                }
            }
        } catch (Exception e) {
        }
    }

    public String[] appDirectoryNameList(String str) {
        String str2 = !str.equals(BuildConfig.FLAVOR) ? ASSET_STRING + File.separator + str : ASSET_STRING;
        Iterator<Object> it = this.mFileInfoMap.keySet().iterator();
        ArrayList arrayList = new ArrayList();
        while (it.hasNext()) {
            String str3 = (String) it.next();
            if (!str3.equals(str2) && str3.startsWith(str2) && -1 == str3.indexOf(File.separator, str2.length() + 1)) {
                arrayList.add(str3.substring(str2.length() + 1));
            }
        }
        return (String[]) arrayList.toArray(new String[0]);
    }

    public boolean[] appDirectoryTypeList(String str) {
        String str2 = !str.equals(BuildConfig.FLAVOR) ? ASSET_STRING + File.separator + str : ASSET_STRING;
        Iterator<Object> it = this.mFileInfoMap.keySet().iterator();
        ArrayList arrayList = new ArrayList();
        while (it.hasNext()) {
            String str3 = (String) it.next();
            if (!str3.equals(str2) && str3.startsWith(str2) && -1 == str3.indexOf(File.separator, str2.length() + 1)) {
                arrayList.add(new Boolean(((FileInfo) this.mFileInfoMap.get(str3)).mIsFile));
            }
        }
        boolean[] zArr = new boolean[arrayList.size()];
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= zArr.length) {
                return zArr;
            }
            zArr[i2] = ((Boolean) arrayList.get(i2)).booleanValue();
            i = i2 + 1;
        }
    }

    private static void RefreshAppCache(String str, String str2) {
        if (!new File(str + File.separator + str2).exists()) {
            deleteDir(new File(str));
        }
    }

    public static boolean deleteDir(File file) {
        if (file.isDirectory()) {
            for (File deleteDir : file.listFiles()) {
                if (!deleteDir(deleteDir)) {
                    return false;
                }
            }
        }
        if (file.delete()) {
            return true;
        }
        return false;
    }

    public static void processAndroidDataPath(String str) {
        String str2;
        String str3;
        String str4;
        String str5 = APP_PREFIX;
        String str6 = str + File.separator + APP_PREFIX;
        String str7 = null;
        try {
            Bundle bundle = AndroidActivityWrapper.GetAndroidActivityWrapper().getActivity().getPackageManager().getActivityInfo(AndroidActivityWrapper.GetAndroidActivityWrapper().getActivity().getComponentName(), 128).metaData;
            if (bundle != null) {
                str2 = (String) bundle.get("uniqueappversionid");
                try {
                    if (AndroidActivityWrapper.IsGamePreviewMode()) {
                        str5 = UUID.randomUUID().toString();
                    } else {
                        str5 = str2;
                    }
                    RefreshAppCache(str6, str5);
                    str3 = (String) bundle.get("initialcontent");
                    str4 = str5;
                } catch (PackageManager.NameNotFoundException | NullPointerException e) {
                }
            } else {
                str3 = null;
                str4 = str5;
            }
            String str8 = str3;
            str2 = str4;
            str7 = str8;
        } catch (PackageManager.NameNotFoundException e2) {
            str2 = str5;
        } catch (NullPointerException e3) {
            str2 = str5;
        }
        setAndroidDataPath(str6 + File.separator + str2);
        new File(str6 + File.separator + str2).mkdirs();
        setInitialContentName(str7);
    }

    private void closeInputStream(InputStream inputStream) throws Exception {
        inputStream.close();
    }

    private void closeOutputStream(OutputStream outputStream) throws Exception {
        outputStream.flush();
        outputStream.close();
    }

    public static void checkAndCreateAppDataDir() {
        File file = new File(sAppDataPath);
        if (!file.exists()) {
            try {
                file.mkdirs();
            } catch (SecurityException e) {
            }
        }
    }
}
