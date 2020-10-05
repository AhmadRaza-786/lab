package com.adobe.air.wand.connection;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import com.adobe.air.wand.Version;
import com.adobe.air.wand.connection.Connection;
import com.distriqt.extension.inappbilling.BuildConfig;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.channels.CancelledKeyException;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

public class WandWebSocket implements Connection {
    private static final String CONNECT_PING_URL = "http://dh8vjmvwgc27o.cloudfront.net/AIRGamepad/connect_ping.txt";
    private static final String LOG_TAG = "WandWebSocket";
    private static final String WEBSOCKET_PROTOCOL = "WEBSOCKET";
    private final String HANDSHAKE_SYNCHRONIZER = "HANDSHAKE_SYNCHRONIZER";
    private final String OPEN_SYNCHRONIZER = "OPEN_SYNCHRONIZER";
    private Activity mActivity = null;
    /* access modifiers changed from: private */
    public boolean mAllowIncomingConnection = false;
    /* access modifiers changed from: private */
    public WebSocket mConnection = null;
    /* access modifiers changed from: private */
    public Connection.Listener mConnectionListener = null;
    private String mConnectionToken = null;
    /* access modifiers changed from: private */
    public Handshake mHandshake = null;
    /* access modifiers changed from: private */
    public Timer mHandshakeTimer = null;
    private boolean mIsDisposed = false;
    private String mLocalID = null;
    /* access modifiers changed from: private */
    public WandSocketServer mWandSocketServer = null;

    private static class Handshake {
        private static final String APPLICATION_URL = "applicationURL";
        private static final String DESTINATION_ID = "destinationID";
        private static final String PROTOCOL = "protocol";
        private static final String PUBLISHER = "publisher";
        private static final String SOURCE_ID = "sourceID";
        private static final String STATUS = "status";
        private static final String STATUS_SUCCESS = "SUCCESS";
        public static final int TIMEOUT_MILLISECONDS = 30000;
        private static final String VERSION = "version";
        /* access modifiers changed from: private */
        public String mApplicationURL = null;
        private String mDestinationID = null;
        private String mProtocol = WandWebSocket.WEBSOCKET_PROTOCOL;
        /* access modifiers changed from: private */
        public String mPublisher = null;
        private String mSourceID = null;
        private String mVersion = null;

        public String getProtocol() {
            return this.mProtocol;
        }

        public String getVersion() {
            return this.mVersion;
        }

        public String getSourceID() {
            return this.mSourceID;
        }

        public String getDestinationID() {
            return this.mDestinationID;
        }

        public String getPublisher() {
            return this.mPublisher;
        }

        public String getApplicationURL() {
            return this.mApplicationURL;
        }

        private Handshake() {
        }

        public static Handshake parse(String str) throws Exception {
            JSONObject jSONObject = new JSONObject(str);
            Handshake handshake = new Handshake();
            handshake.mProtocol = jSONObject.getString(PROTOCOL);
            handshake.mVersion = jSONObject.getString(VERSION);
            handshake.mSourceID = jSONObject.getString(SOURCE_ID);
            handshake.mDestinationID = jSONObject.getString(DESTINATION_ID);
            if (jSONObject.has(PUBLISHER)) {
                handshake.mPublisher = jSONObject.getString(PUBLISHER);
            }
            if (jSONObject.has(APPLICATION_URL)) {
                handshake.mApplicationURL = jSONObject.getString(APPLICATION_URL);
            }
            return handshake;
        }

        public String getSuccessResponse(String str) throws Exception {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put(PROTOCOL, WandWebSocket.WEBSOCKET_PROTOCOL);
            jSONObject.put(VERSION, this.mVersion);
            jSONObject.put(SOURCE_ID, str);
            jSONObject.put(DESTINATION_ID, this.mSourceID);
            jSONObject.put(STATUS, STATUS_SUCCESS);
            if (Version.isGreaterThanEqualTo(this.mVersion, "1.1.0")) {
                jSONObject.put(PUBLISHER, this.mPublisher);
                jSONObject.put(APPLICATION_URL, this.mApplicationURL);
            }
            return jSONObject.toString();
        }
    }

    private class WandSocketServer extends WebSocketServer {
        private boolean mHasStartedServer = false;

        public WandSocketServer(InetSocketAddress inetSocketAddress) throws UnknownHostException {
            super(inetSocketAddress);
        }

        public void start() {
            if (!this.mHasStartedServer) {
                WandWebSocket.super.start();
                this.mHasStartedServer = true;
            }
        }

        public void stop() throws IOException, InterruptedException {
            if (this.mHasStartedServer) {
                try {
                    WandWebSocket.super.stop();
                } catch (CancelledKeyException e) {
                }
                this.mHasStartedServer = false;
                if (WandWebSocket.this.mWandSocketServer != null) {
                    if (WandWebSocket.this.mConnection != null) {
                        WandWebSocket.this.forceCloseConnection();
                    }
                    WandSocketServer unused = WandWebSocket.this.mWandSocketServer = null;
                    try {
                        WandWebSocket.this.startSocketServer();
                    } catch (Exception e2) {
                    }
                }
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x001e, code lost:
            r3.close(1003, "AIR Gamepad is already connected");
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onOpen(org.java_websocket.WebSocket r3, org.java_websocket.handshake.ClientHandshake r4) {
            /*
                r2 = this;
                java.lang.String r1 = "OPEN_SYNCHRONIZER"
                monitor-enter(r1)
                com.adobe.air.wand.connection.WandWebSocket r0 = com.adobe.air.wand.connection.WandWebSocket.this     // Catch:{ all -> 0x0026 }
                boolean r0 = r0.mAllowIncomingConnection     // Catch:{ all -> 0x0026 }
                if (r0 == 0) goto L_0x001d
                com.adobe.air.wand.connection.WandWebSocket r0 = com.adobe.air.wand.connection.WandWebSocket.this     // Catch:{ all -> 0x0026 }
                org.java_websocket.WebSocket r0 = r0.mConnection     // Catch:{ all -> 0x0026 }
                if (r0 != 0) goto L_0x001d
                com.adobe.air.wand.connection.WandWebSocket r0 = com.adobe.air.wand.connection.WandWebSocket.this     // Catch:{ all -> 0x0026 }
                org.java_websocket.WebSocket unused = r0.mConnection = r3     // Catch:{ all -> 0x0026 }
                r2.scheduleHandshakeTimer()     // Catch:{ all -> 0x0026 }
                monitor-exit(r1)     // Catch:{ all -> 0x0026 }
            L_0x001c:
                return
            L_0x001d:
                monitor-exit(r1)     // Catch:{ all -> 0x0026 }
                r0 = 1003(0x3eb, float:1.406E-42)
                java.lang.String r1 = "AIR Gamepad is already connected"
                r3.close(r0, r1)
                goto L_0x001c
            L_0x0026:
                r0 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x0026 }
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.adobe.air.wand.connection.WandWebSocket.WandSocketServer.onOpen(org.java_websocket.WebSocket, org.java_websocket.handshake.ClientHandshake):void");
        }

        private void scheduleHandshakeTimer() {
            try {
                WandWebSocket.this.mHandshakeTimer.schedule(new TimerTask() {
                    public void run() {
                        synchronized ("OPEN_SYNCHRONIZER") {
                            if (WandWebSocket.this.mConnection != null) {
                                synchronized ("HANDSHAKE_SYNCHRONIZER") {
                                    if (WandWebSocket.this.mHandshake == null) {
                                        WandWebSocket.this.mConnection.close(1003, "AIR Gamepad handshake timedout");
                                    }
                                }
                            }
                        }
                    }
                }, 30000);
            } catch (Exception e) {
            }
        }

        public void onClose(WebSocket webSocket, int i, String str, boolean z) {
            synchronized ("OPEN_SYNCHRONIZER") {
                if (WandWebSocket.this.mConnection == webSocket) {
                    WebSocket unused = WandWebSocket.this.mConnection = null;
                    synchronized ("HANDSHAKE_SYNCHRONIZER") {
                        if (WandWebSocket.this.mHandshake != null) {
                            Handshake unused2 = WandWebSocket.this.mHandshake = null;
                            if (WandWebSocket.this.mConnectionListener != null) {
                                WandWebSocket.this.mConnectionListener.onConnectionClose();
                            }
                        }
                    }
                }
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:64:?, code lost:
            return;
         */
        /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onMessage(org.java_websocket.WebSocket r7, java.lang.String r8) {
            /*
                r6 = this;
                r2 = 0
                java.lang.String r3 = "OPEN_SYNCHRONIZER"
                monitor-enter(r3)
                com.adobe.air.wand.connection.WandWebSocket r0 = com.adobe.air.wand.connection.WandWebSocket.this     // Catch:{ all -> 0x0024 }
                org.java_websocket.WebSocket r0 = r0.mConnection     // Catch:{ all -> 0x0024 }
                if (r7 == r0) goto L_0x000e
                monitor-exit(r3)     // Catch:{ all -> 0x0024 }
            L_0x000d:
                return
            L_0x000e:
                java.lang.String r4 = "HANDSHAKE_SYNCHRONIZER"
                monitor-enter(r4)     // Catch:{ all -> 0x0024 }
                com.adobe.air.wand.connection.WandWebSocket r0 = com.adobe.air.wand.connection.WandWebSocket.this     // Catch:{ all -> 0x0099 }
                com.adobe.air.wand.connection.WandWebSocket$Handshake r0 = r0.mHandshake     // Catch:{ all -> 0x0099 }
                if (r0 == 0) goto L_0x003b
                java.lang.String r0 = "NO_OP"
                boolean r0 = r8.equals(r0)     // Catch:{ all -> 0x0099 }
                if (r0 == 0) goto L_0x0027
                monitor-exit(r4)     // Catch:{ all -> 0x0099 }
                monitor-exit(r3)     // Catch:{ all -> 0x0024 }
                goto L_0x000d
            L_0x0024:
                r0 = move-exception
                monitor-exit(r3)     // Catch:{ all -> 0x0024 }
                throw r0
            L_0x0027:
                com.adobe.air.wand.connection.WandWebSocket r0 = com.adobe.air.wand.connection.WandWebSocket.this     // Catch:{ all -> 0x0099 }
                com.adobe.air.wand.connection.Connection$Listener r0 = r0.mConnectionListener     // Catch:{ all -> 0x0099 }
                if (r0 == 0) goto L_0x0038
                com.adobe.air.wand.connection.WandWebSocket r0 = com.adobe.air.wand.connection.WandWebSocket.this     // Catch:{ all -> 0x0099 }
                com.adobe.air.wand.connection.Connection$Listener r0 = r0.mConnectionListener     // Catch:{ all -> 0x0099 }
                r0.onReceive(r8)     // Catch:{ all -> 0x0099 }
            L_0x0038:
                monitor-exit(r4)     // Catch:{ all -> 0x0099 }
                monitor-exit(r3)     // Catch:{ all -> 0x0024 }
                goto L_0x000d
            L_0x003b:
                java.lang.String r0 = ""
                com.adobe.air.wand.connection.WandWebSocket$Handshake r1 = com.adobe.air.wand.connection.WandWebSocket.Handshake.parse(r8)     // Catch:{ Exception -> 0x0063 }
                com.adobe.air.wand.connection.WandWebSocket r5 = com.adobe.air.wand.connection.WandWebSocket.this     // Catch:{ Exception -> 0x006c }
                r5.validateHandshake(r1)     // Catch:{ Exception -> 0x006c }
            L_0x0046:
                if (r1 != 0) goto L_0x0073
                r1 = 1003(0x3eb, float:1.406E-42)
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0099 }
                r2.<init>()     // Catch:{ all -> 0x0099 }
                java.lang.String r5 = "Invalid AIR Gamepad handshake : "
                java.lang.StringBuilder r2 = r2.append(r5)     // Catch:{ all -> 0x0099 }
                java.lang.StringBuilder r0 = r2.append(r0)     // Catch:{ all -> 0x0099 }
                java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0099 }
                r7.close(r1, r0)     // Catch:{ all -> 0x0099 }
                monitor-exit(r4)     // Catch:{ all -> 0x0099 }
                monitor-exit(r3)     // Catch:{ all -> 0x0024 }
                goto L_0x000d
            L_0x0063:
                r0 = move-exception
                java.lang.Exception r0 = new java.lang.Exception     // Catch:{ Exception -> 0x006c }
                java.lang.String r1 = "Unable to parse the handshake"
                r0.<init>(r1)     // Catch:{ Exception -> 0x006c }
                throw r0     // Catch:{ Exception -> 0x006c }
            L_0x006c:
                r0 = move-exception
                java.lang.String r0 = r0.getMessage()     // Catch:{ all -> 0x0099 }
                r1 = r2
                goto L_0x0046
            L_0x0073:
                com.adobe.air.wand.connection.WandWebSocket r0 = com.adobe.air.wand.connection.WandWebSocket.this     // Catch:{ all -> 0x0099 }
                com.adobe.air.wand.connection.WandWebSocket.Handshake unused = r0.mHandshake = r1     // Catch:{ all -> 0x0099 }
                com.adobe.air.wand.connection.WandWebSocket r0 = com.adobe.air.wand.connection.WandWebSocket.this     // Catch:{ Exception -> 0x0091 }
                com.adobe.air.wand.connection.Connection$Listener r0 = r0.mConnectionListener     // Catch:{ Exception -> 0x0091 }
                if (r0 == 0) goto L_0x008d
                com.adobe.air.wand.connection.WandWebSocket r0 = com.adobe.air.wand.connection.WandWebSocket.this     // Catch:{ Exception -> 0x0091 }
                com.adobe.air.wand.connection.Connection$Listener r0 = r0.mConnectionListener     // Catch:{ Exception -> 0x0091 }
                java.lang.String r1 = r1.getVersion()     // Catch:{ Exception -> 0x0091 }
                r0.onConnectionOpen(r1)     // Catch:{ Exception -> 0x0091 }
            L_0x008d:
                monitor-exit(r4)     // Catch:{ all -> 0x0099 }
                monitor-exit(r3)     // Catch:{ all -> 0x0024 }
                goto L_0x000d
            L_0x0091:
                r0 = move-exception
                com.adobe.air.wand.connection.WandWebSocket r0 = com.adobe.air.wand.connection.WandWebSocket.this     // Catch:{ all -> 0x0099 }
                r1 = 0
                com.adobe.air.wand.connection.WandWebSocket.Handshake unused = r0.mHandshake = r1     // Catch:{ all -> 0x0099 }
                goto L_0x008d
            L_0x0099:
                r0 = move-exception
                monitor-exit(r4)     // Catch:{ all -> 0x0099 }
                throw r0     // Catch:{ all -> 0x0024 }
            */
            throw new UnsupportedOperationException("Method not decompiled: com.adobe.air.wand.connection.WandWebSocket.WandSocketServer.onMessage(org.java_websocket.WebSocket, java.lang.String):void");
        }

        public void onError(WebSocket webSocket, Exception exc) {
            if (WandWebSocket.this.mConnection != null && WandWebSocket.this.mConnection != webSocket && webSocket != null) {
                try {
                    webSocket.close(1003, "AIR Gamepad is already connected");
                } catch (Exception e) {
                }
            }
        }
    }

    public WandWebSocket(Activity activity) throws UnknownHostException {
        this.mActivity = activity;
        this.mHandshakeTimer = new Timer();
    }

    /* access modifiers changed from: private */
    public void startSocketServer() throws Exception {
        if (this.mWandSocketServer == null) {
            this.mWandSocketServer = new WandSocketServer(new InetSocketAddress(getLocalIpAddress(), getPreferredPort()));
            this.mWandSocketServer.start();
        }
    }

    private void stopSocketServer() {
        WandSocketServer wandSocketServer = this.mWandSocketServer;
        this.mWandSocketServer = null;
        if (wandSocketServer != null) {
            try {
                wandSocketServer.stop();
            } catch (Exception e) {
            }
        }
    }

    private String constructLocalID() throws Exception {
        InetAddress localIpAddress = getLocalIpAddress();
        if (localIpAddress == null) {
            return null;
        }
        byte[] address = localIpAddress.getAddress();
        return Long.toString((((long) getUnsignedByte(address[0])) * 16777216) + (((long) getUnsignedByte(address[3])) * 1) + (((long) getUnsignedByte(address[2])) * 256) + (((long) getUnsignedByte(address[1])) * 65536), 32).toUpperCase();
    }

    public void connect() throws Exception {
        if (this.mIsDisposed) {
            throw new Exception("Connection has been disposed");
        } else if (this.mAllowIncomingConnection) {
            throw new Exception("Connection is already established");
        } else {
            this.mAllowIncomingConnection = true;
            if (this.mWandSocketServer == null) {
                startSocketServer();
            }
            this.mLocalID = constructLocalID();
            this.mConnectionToken = this.mLocalID;
            if (this.mConnectionListener != null) {
                this.mConnectionListener.updateConnectionToken(getConnectionToken());
            }
            if (this.mConnectionListener != null) {
                this.mConnectionListener.onConnectSuccess();
            }
        }
    }

    /* access modifiers changed from: private */
    public void forceCloseConnection() {
        WebSocket webSocket = this.mConnection;
        this.mWandSocketServer.onClose(this.mConnection, 1001, "AIR Gamepad has closed", false);
        webSocket.close(1001, "AIR Gamepad has closed");
    }

    public void disconnect() throws Exception {
        if (this.mIsDisposed) {
            throw new Exception("Connection has been disposed");
        } else if (!this.mAllowIncomingConnection) {
            throw new Exception("Connection is not established");
        } else {
            if (this.mConnection != null) {
                forceCloseConnection();
            }
            stopSocketServer();
            this.mAllowIncomingConnection = false;
            if (this.mConnectionListener != null) {
                this.mConnectionListener.onDisconnectSuccess();
            }
        }
    }

    public String getConnectionToken() throws Exception {
        if (this.mIsDisposed) {
            throw new Exception("Connection has been disposed");
        } else if (this.mAllowIncomingConnection) {
            return this.mConnectionToken == null ? BuildConfig.FLAVOR : this.mConnectionToken;
        } else {
            throw new Exception("Connection is not established");
        }
    }

    public void registerListener(Connection.Listener listener) throws Exception {
        if (this.mIsDisposed) {
            throw new Exception("Connection has been disposed");
        } else if (listener == null) {
            throw new Exception("Invalid Connection.Listener");
        } else if (this.mConnectionListener != null) {
            throw new Exception("A listener is already registered");
        } else {
            this.mConnectionListener = listener;
        }
    }

    public void unregisterListener() {
        this.mConnectionListener = null;
    }

    public void send(String str) throws Exception {
        if (this.mIsDisposed) {
            throw new Exception("Connection has been disposed");
        } else if (this.mConnection != null) {
            try {
                this.mConnection.send(str);
            } catch (Throwable th) {
                throw new Exception("Unable to send Message");
            }
        }
    }

    private InetAddress getWiFiIpAddress() throws UnknownHostException {
        WifiManager wifiManager = (WifiManager) this.mActivity.getSystemService("wifi");
        if (wifiManager == null) {
            return null;
        }
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        if (connectionInfo == null) {
            return null;
        }
        int ipAddress = connectionInfo.getIpAddress();
        if (ipAddress == 0) {
            return null;
        }
        return InetAddress.getByName(String.format("%d.%d.%d.%d", new Object[]{Integer.valueOf(ipAddress & 255), Integer.valueOf((ipAddress >> 8) & 255), Integer.valueOf((ipAddress >> 16) & 255), Integer.valueOf((ipAddress >> 24) & 255)}));
    }

    private InetAddress getWiFiHotspotIpAddress() throws UnknownHostException, SocketException {
        boolean z;
        boolean z2;
        if (((ConnectivityManager) this.mActivity.getSystemService("connectivity")) == null) {
            return null;
        }
        WifiManager wifiManager = (WifiManager) this.mActivity.getSystemService("wifi");
        if (wifiManager == null) {
            return null;
        }
        boolean z3 = false;
        for (Method method : wifiManager.getClass().getDeclaredMethods()) {
            if (method.getName().equals("isWifiApEnabled")) {
                try {
                    if (((Boolean) method.invoke(wifiManager, new Object[0])).booleanValue()) {
                        z2 = true;
                    } else {
                        z2 = z3;
                    }
                    z3 = z2;
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                }
            }
        }
        if (!z3) {
            return null;
        }
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        if (connectionInfo == null) {
            return null;
        }
        String lowerCase = connectionInfo.getMacAddress().toLowerCase();
        byte[] bArr = new byte[6];
        if (lowerCase.indexOf(":") == -1 && lowerCase.length() == 12) {
            for (int i = 0; i < bArr.length; i++) {
                bArr[i] = (byte) Integer.parseInt(lowerCase.substring(i * 2, (i * 2) + 2), 16);
            }
        } else {
            String[] split = lowerCase.split(":");
            int i2 = 0;
            while (i2 < bArr.length && i2 < split.length) {
                bArr[i2] = (byte) Integer.parseInt(split[i2], 16);
                i2++;
            }
        }
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface nextElement = networkInterfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = nextElement.getInetAddresses();
            while (true) {
                if (inetAddresses.hasMoreElements()) {
                    InetAddress nextElement2 = inetAddresses.nextElement();
                    if (!nextElement2.isLoopbackAddress() && InetAddressUtils.isIPv4Address(nextElement2.getHostAddress())) {
                        try {
                            byte[] hardwareAddress = nextElement.getHardwareAddress();
                            if (hardwareAddress != null && hardwareAddress.length == 6) {
                                int i3 = 0;
                                while (true) {
                                    if (i3 >= hardwareAddress.length) {
                                        z = true;
                                        break;
                                    } else if (hardwareAddress[i3] != bArr[i3]) {
                                        z = false;
                                        break;
                                    } else {
                                        i3++;
                                    }
                                }
                                if (z) {
                                    return nextElement2;
                                }
                            }
                        } catch (SocketException e2) {
                        }
                    }
                }
            }
        }
        return null;
    }

    private InetAddress getLocalIpAddress() {
        try {
            InetAddress wiFiIpAddress = getWiFiIpAddress();
            if (wiFiIpAddress == null) {
                return getWiFiHotspotIpAddress();
            }
            return wiFiIpAddress;
        } catch (Exception e) {
            return null;
        }
    }

    private int getPreferredPort() {
        return 1234;
    }

    private int getUnsignedByte(byte b) {
        return b >= 0 ? b : b + 256;
    }

    /* access modifiers changed from: private */
    public void validateHandshake(Handshake handshake) throws Exception {
        if (handshake == null) {
            throw new Exception("Handshake is null");
        }
        String version = handshake.getVersion();
        if (!Pattern.matches("\\d+\\.\\d+\\.\\d+", version)) {
            throw new Exception("Invalid version format");
        } else if (Version.isGreaterThan(version, "1.1.0") || !Version.isGreaterThanEqualTo(version, Version.V1_0_0)) {
            throw new Exception("Unsupported version");
        } else if (!WEBSOCKET_PROTOCOL.equals(handshake.getProtocol())) {
            throw new Exception("Invalid protocol");
        } else if (!this.mLocalID.equals(handshake.getDestinationID())) {
            throw new Exception("Invalid destinationID");
        } else if (!Version.isGreaterThanEqualTo(version, "1.1.0")) {
        } else {
            if (handshake.getPublisher() == null) {
                throw new Exception("Invalid publisher");
            }
            String applicationURL = handshake.getApplicationURL();
            if (applicationURL == null) {
                throw new Exception("Invalid applicationURL");
            }
            try {
                new URL(applicationURL);
            } catch (Exception e) {
                throw new Exception("Invalid applicationURL");
            }
        }
    }

    public void onConnectionChanged() {
        if (!this.mIsDisposed && this.mAllowIncomingConnection) {
            try {
                String constructLocalID = constructLocalID();
                if (this.mLocalID != null || constructLocalID != null) {
                    if (this.mLocalID == null || constructLocalID == null || !this.mLocalID.equals(constructLocalID)) {
                        disconnect();
                        connect();
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    private class ConnectPingTask extends AsyncTask<String, Integer, Long> {
        private ConnectPingTask() {
        }

        /* access modifiers changed from: protected */
        public Long doInBackground(String... strArr) {
            if (strArr == null || strArr.length < 1) {
                return 0L;
            }
            try {
                new DefaultHttpClient().execute(new HttpGet(strArr[0]));
                return 0L;
            } catch (Exception e) {
                return -1L;
            }
        }

        /* access modifiers changed from: protected */
        public void onProgressUpdate(Integer... numArr) {
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Long l) {
        }
    }

    private void pingServerOnConnect(String str, String str2) throws Exception {
        if (str == null || str2 == null) {
            new ConnectPingTask().execute(new String[]{CONNECT_PING_URL});
            return;
        }
        new ConnectPingTask().execute(new String[]{"http://dh8vjmvwgc27o.cloudfront.net/AIRGamepad/connect_ping.txt?publisher=" + URLEncoder.encode(str, "UTF-8") + "&applicationURL=" + URLEncoder.encode(str2, "UTF-8")});
    }

    public void onReadyForConnection() throws Exception {
        if (this.mHandshake == null || this.mLocalID == null) {
            throw new Exception("Invalid state at onReadyForConnection callback.");
        }
        this.mConnection.send(this.mHandshake.getSuccessResponse(this.mLocalID));
        pingServerOnConnect(this.mHandshake.mPublisher, this.mHandshake.mApplicationURL);
    }

    public void dispose() throws Exception {
        if (!this.mIsDisposed) {
            this.mIsDisposed = true;
            if (this.mAllowIncomingConnection) {
                disconnect();
            }
            unregisterListener();
            if (this.mConnection != null) {
                this.mConnection.close(1001, "AIR Gamepad has closed");
            }
            this.mConnection = null;
            this.mHandshake = null;
            if (this.mHandshakeTimer != null) {
                this.mHandshakeTimer.cancel();
                this.mHandshakeTimer.purge();
            }
            this.mHandshakeTimer = null;
            this.mWandSocketServer = null;
            this.mActivity = null;
        }
    }
}
