package com.adobe.air.utils;

import com.distriqt.extension.inappbilling.BuildConfig;
import java.io.IOException;
import java.io.InputStream;

public class DeviceInfo {
    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0027, code lost:
        r3 = new java.lang.String(r0, 0, r2);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static java.lang.String getHardwareInfo() {
        /*
            r0 = 2
            java.lang.String[] r0 = new java.lang.String[r0]     // Catch:{ IOException -> 0x005b }
            r1 = 0
            java.lang.String r2 = "/system/bin/cat"
            r0[r1] = r2     // Catch:{ IOException -> 0x005b }
            r1 = 1
            java.lang.String r2 = "/proc/cpuinfo"
            r0[r1] = r2     // Catch:{ IOException -> 0x005b }
            java.lang.ProcessBuilder r1 = new java.lang.ProcessBuilder     // Catch:{ IOException -> 0x005b }
            r1.<init>(r0)     // Catch:{ IOException -> 0x005b }
            r0 = 1024(0x400, float:1.435E-42)
            byte[] r0 = new byte[r0]     // Catch:{ IOException -> 0x005b }
            java.lang.Process r1 = r1.start()     // Catch:{ IOException -> 0x005b }
            java.io.InputStream r1 = r1.getInputStream()     // Catch:{ IOException -> 0x005b }
            r2 = 0
            r3 = 1024(0x400, float:1.435E-42)
            int r2 = r1.read(r0, r2, r3)     // Catch:{ IOException -> 0x005b }
            if (r2 < 0) goto L_0x0050
            java.lang.String r3 = new java.lang.String     // Catch:{ IOException -> 0x005b }
            r4 = 0
            r3.<init>(r0, r4, r2)     // Catch:{ IOException -> 0x005b }
            java.lang.String r0 = "Hardware"
            int r0 = r3.indexOf(r0)     // Catch:{ IOException -> 0x005b }
            if (r0 < 0) goto L_0x0050
            r2 = 58
            int r0 = r3.indexOf(r2, r0)     // Catch:{ IOException -> 0x005b }
            if (r0 < 0) goto L_0x0050
            int r1 = r0 + 1
            r2 = 10
            int r0 = r0 + 1
            int r0 = r3.indexOf(r2, r0)     // Catch:{ IOException -> 0x005b }
            java.lang.String r0 = r3.substring(r1, r0)     // Catch:{ IOException -> 0x005b }
            java.lang.String r0 = r0.trim()     // Catch:{ IOException -> 0x005b }
        L_0x004f:
            return r0
        L_0x0050:
            r1.close()     // Catch:{ IOException -> 0x005b }
        L_0x0053:
            java.lang.String r0 = new java.lang.String
            java.lang.String r1 = ""
            r0.<init>(r1)
            goto L_0x004f
        L_0x005b:
            r0 = move-exception
            goto L_0x0053
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adobe.air.utils.DeviceInfo.getHardwareInfo():java.lang.String");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0027, code lost:
        r3 = new java.lang.String(r0, 0, r2);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static java.lang.String getTotalMemory() {
        /*
            r0 = 2
            java.lang.String[] r0 = new java.lang.String[r0]     // Catch:{ IOException -> 0x005b }
            r1 = 0
            java.lang.String r2 = "/system/bin/cat"
            r0[r1] = r2     // Catch:{ IOException -> 0x005b }
            r1 = 1
            java.lang.String r2 = "/proc/meminfo"
            r0[r1] = r2     // Catch:{ IOException -> 0x005b }
            java.lang.ProcessBuilder r1 = new java.lang.ProcessBuilder     // Catch:{ IOException -> 0x005b }
            r1.<init>(r0)     // Catch:{ IOException -> 0x005b }
            r0 = 1024(0x400, float:1.435E-42)
            byte[] r0 = new byte[r0]     // Catch:{ IOException -> 0x005b }
            java.lang.Process r1 = r1.start()     // Catch:{ IOException -> 0x005b }
            java.io.InputStream r1 = r1.getInputStream()     // Catch:{ IOException -> 0x005b }
            r2 = 0
            r3 = 1024(0x400, float:1.435E-42)
            int r2 = r1.read(r0, r2, r3)     // Catch:{ IOException -> 0x005b }
            if (r2 < 0) goto L_0x0050
            java.lang.String r3 = new java.lang.String     // Catch:{ IOException -> 0x005b }
            r4 = 0
            r3.<init>(r0, r4, r2)     // Catch:{ IOException -> 0x005b }
            java.lang.String r0 = "MemTotal"
            int r0 = r3.indexOf(r0)     // Catch:{ IOException -> 0x005b }
            if (r0 < 0) goto L_0x0050
            r2 = 58
            int r0 = r3.indexOf(r2, r0)     // Catch:{ IOException -> 0x005b }
            if (r0 < 0) goto L_0x0050
            int r1 = r0 + 1
            r2 = 10
            int r0 = r0 + 1
            int r0 = r3.indexOf(r2, r0)     // Catch:{ IOException -> 0x005b }
            java.lang.String r0 = r3.substring(r1, r0)     // Catch:{ IOException -> 0x005b }
            java.lang.String r0 = r0.trim()     // Catch:{ IOException -> 0x005b }
        L_0x004f:
            return r0
        L_0x0050:
            r1.close()     // Catch:{ IOException -> 0x005b }
        L_0x0053:
            java.lang.String r0 = new java.lang.String
            java.lang.String r1 = ""
            r0.<init>(r1)
            goto L_0x004f
        L_0x005b:
            r0 = move-exception
            goto L_0x0053
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adobe.air.utils.DeviceInfo.getTotalMemory():java.lang.String");
    }

    static String getCPUCount() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(new String[]{"/system/bin/cat", "/sys/devices/system/cpu/present"});
            byte[] bArr = new byte[1024];
            InputStream inputStream = processBuilder.start().getInputStream();
            int read = inputStream.read(bArr, 0, 1024);
            if (read >= 0) {
                String str = new String(bArr, 0, read);
                int indexOf = str.indexOf("-");
                if (indexOf >= 0) {
                    return Integer.toString(Integer.parseInt(str.substring(indexOf + 1, indexOf + 2)) + 1);
                }
                return Integer.toString(Integer.parseInt(str.substring(0, 1)) + 1);
            }
            inputStream.close();
            return new String(BuildConfig.FLAVOR);
        } catch (IOException e) {
        }
    }
}
