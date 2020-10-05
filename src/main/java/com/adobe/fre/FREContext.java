package com.adobe.fre;

import android.app.Activity;
import android.content.res.Resources;
import java.util.Map;

public abstract class FREContext {
    private long m_objectPointer;

    private native void registerFunction(long j, String str, FREFunction fREFunction);

    private native void registerFunctionCount(long j, int i);

    public native void dispatchStatusEventAsync(String str, String str2) throws IllegalArgumentException, IllegalStateException;

    public abstract void dispose();

    public native FREObject getActionScriptData() throws FREWrongThreadException, IllegalStateException;

    public native Activity getActivity() throws IllegalStateException;

    public abstract Map<String, FREFunction> getFunctions();

    public native int getResourceId(String str) throws IllegalArgumentException, Resources.NotFoundException, IllegalStateException;

    public native void setActionScriptData(FREObject fREObject) throws FREWrongThreadException, IllegalArgumentException, IllegalStateException;

    /* access modifiers changed from: protected */
    public void VisitFunctions(long clientID) {
        Map<String, FREFunction> m = getFunctions();
        registerFunctionCount(clientID, m.size());
        for (Map.Entry<String, FREFunction> e : m.entrySet()) {
            registerFunction(clientID, e.getKey(), e.getValue());
        }
    }
}
