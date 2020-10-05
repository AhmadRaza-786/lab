package com.adobe.air;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.SyncFailedException;
import java.security.KeyStore;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public final class JavaTrustStoreHelper {
    private static final String LOG_TAG = "JavaTrustStoreHelper";
    private static SystemKeyStoreManager mSystemKeyStoreMgr = initSystemKeyStoreMgr();

    private interface SystemKeyStoreManager {
        KeyStore getKeyStore();

        long getTimestamp();
    }

    private static class SystemKeyStoreManager_stub implements SystemKeyStoreManager {
        private SystemKeyStoreManager_stub() {
        }

        public KeyStore getKeyStore() {
            return null;
        }

        public long getTimestamp() {
            return 0;
        }
    }

    private static class SystemKeyStoreManager_sysprop implements SystemKeyStoreManager {
        private static final String kTheProp = "javax.net.ssl.trustStore";

        private SystemKeyStoreManager_sysprop() {
        }

        public static boolean isValid() {
            return System.getProperty(kTheProp) != null;
        }

        public KeyStore getKeyStore() {
            File file;
            try {
                KeyStore instance = KeyStore.getInstance(KeyStore.getDefaultType());
                String property = System.getProperty(kTheProp);
                if (property == null || (file = new File(property)) == null) {
                    return null;
                }
                instance.load(new FileInputStream(file), (char[]) null);
                return instance;
            } catch (Exception e) {
                return null;
            }
        }

        public long getTimestamp() {
            return new File(System.getProperty(kTheProp)).lastModified();
        }
    }

    private static class SystemKeyStoreManager_files implements SystemKeyStoreManager {
        private static final String kDirectoryPath = "/system/etc/security/cacerts";
        private int mNumFilesWhenCreated;

        private SystemKeyStoreManager_files() {
            this.mNumFilesWhenCreated = -1;
        }

        public static boolean isValid() {
            return new File(kDirectoryPath).exists();
        }

        public long getTimestamp() {
            String[] fileListing = getFileListing();
            if (this.mNumFilesWhenCreated != -1 && fileListing.length != this.mNumFilesWhenCreated) {
                return Long.MAX_VALUE;
            }
            long j = 0;
            for (int i = 0; i < fileListing.length; i++) {
                j = Math.max(new File("/system/etc/security/cacerts/" + fileListing[i]).lastModified(), j);
            }
            return j;
        }

        private static String[] getFileListing() {
            return new File(kDirectoryPath).list();
        }

        public KeyStore getKeyStore() {
            try {
                KeyStore instance = KeyStore.getInstance(KeyStore.getDefaultType());
                instance.load((KeyStore.LoadStoreParameter) null);
                CertificateFactory instance2 = CertificateFactory.getInstance("X.509");
                String[] fileListing = getFileListing();
                this.mNumFilesWhenCreated = fileListing.length;
                for (int i = 0; i < fileListing.length; i++) {
                    instance.setCertificateEntry(String.valueOf(i), instance2.generateCertificate(new FileInputStream(new File("/system/etc/security/cacerts/" + fileListing[i]))));
                }
                return instance;
            } catch (Exception e) {
                this.mNumFilesWhenCreated = -1;
                return null;
            }
        }
    }

    private static boolean isAirCertBundleStale(String str) {
        File file = new File(str);
        if (!file.exists() || mSystemKeyStoreMgr.getTimestamp() > file.lastModified()) {
            return true;
        }
        return false;
    }

    private static SystemKeyStoreManager initSystemKeyStoreMgr() {
        if (SystemKeyStoreManager_sysprop.isValid()) {
            return new SystemKeyStoreManager_sysprop();
        }
        if (SystemKeyStoreManager_files.isValid()) {
            return new SystemKeyStoreManager_files();
        }
        return new SystemKeyStoreManager_stub();
    }

    private static long getTrustStoreTimestamp() {
        return mSystemKeyStoreMgr.getTimestamp();
    }

    private static TrustManagerFactory getX509TrustManagerFactory() {
        try {
            TrustManagerFactory instance = TrustManagerFactory.getInstance("X509");
            instance.init(mSystemKeyStoreMgr.getKeyStore());
            return instance;
        } catch (Exception e) {
            return null;
        }
    }

    private static X509TrustManager getX509TrustManager() {
        TrustManagerFactory x509TrustManagerFactory = getX509TrustManagerFactory();
        if (x509TrustManagerFactory != null) {
            TrustManager[] trustManagers = x509TrustManagerFactory.getTrustManagers();
            for (int i = 0; i < trustManagers.length; i++) {
                if (trustManagers[i] instanceof X509TrustManager) {
                    return (X509TrustManager) trustManagers[i];
                }
            }
        }
        return null;
    }

    public static void copyTimestamp(String str, String str2) {
        try {
            new File(str2).setLastModified(new File(str).lastModified());
        } catch (IllegalArgumentException | SecurityException e) {
        }
    }

    private static int dumpAcceptedIssuersToFile(X509TrustManager x509TrustManager, RandomAccessFile randomAccessFile) throws CertificateEncodingException, IOException, SyncFailedException {
        X509Certificate[] acceptedIssuers = x509TrustManager.getAcceptedIssuers();
        for (X509Certificate encoded : acceptedIssuers) {
            byte[] encoded2 = encoded.getEncoded();
            randomAccessFile.writeInt(encoded2.length);
            randomAccessFile.write(encoded2);
        }
        randomAccessFile.getFD().sync();
        return acceptedIssuers.length;
    }

    public static boolean enumerateRootCAs(String str, String str2) {
        X509TrustManager x509TrustManager;
        if (!isAirCertBundleStale(str) || (x509TrustManager = getX509TrustManager()) == null) {
            return false;
        }
        try {
            File file = new File(str2);
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.setLength(0);
            dumpAcceptedIssuersToFile(x509TrustManager, randomAccessFile);
            file.setLastModified(getTrustStoreTimestamp());
            randomAccessFile.close();
            return true;
        } catch (FileNotFoundException | IOException | IllegalArgumentException | SecurityException | CertificateEncodingException e) {
            return false;
        }
    }
}
