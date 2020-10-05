package com.adobe.air;

public final class DebuggerSettings {
    private int debuggerPort = -1;
    private String host = null;
    private boolean listenForConn = false;

    DebuggerSettings() {
    }

    DebuggerSettings(int i, String str, boolean z) {
        this.debuggerPort = i;
        this.host = str;
        this.listenForConn = z;
    }

    public int getDebuggerPort() {
        return this.debuggerPort;
    }

    public String getHost() {
        return this.host;
    }

    public boolean shouldListen() {
        return this.listenForConn;
    }

    public void setDebugerPort(int i) {
        this.debuggerPort = i;
    }

    public void setHost(String str) {
        this.host = str;
    }

    public void setListen(boolean z) {
        this.listenForConn = z;
    }
}
