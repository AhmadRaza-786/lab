package com.adobe.air;

import android.content.SharedPreferences;
import android.util.Base64;

class AndroidEncryptedLocalStore {
    private static final String LOG_TAG = "AndroidELS -------";

    AndroidEncryptedLocalStore() {
    }

    public boolean setItem(String str, String str2, byte[] bArr) throws OutOfMemoryError {
        String encodeToString = Base64.encodeToString(bArr, 0);
        SharedPreferences.Editor edit = AndroidActivityWrapper.GetAndroidActivityWrapper().getActivity().getApplicationContext().getSharedPreferences(str, 0).edit();
        edit.putString(str2, encodeToString);
        return edit.commit();
    }

    public byte[] getItem(String str, String str2) throws OutOfMemoryError {
        String string = AndroidActivityWrapper.GetAndroidActivityWrapper().getActivity().getApplicationContext().getSharedPreferences(str, 0).getString(str2, (String) null);
        if (string != null) {
            return Base64.decode(string, 0);
        }
        return null;
    }

    public boolean removeItem(String str, String str2) {
        SharedPreferences.Editor edit = AndroidActivityWrapper.GetAndroidActivityWrapper().getActivity().getApplicationContext().getSharedPreferences(str, 0).edit();
        edit.remove(str2);
        return edit.commit();
    }

    public boolean reset(String str) {
        SharedPreferences.Editor edit = AndroidActivityWrapper.GetAndroidActivityWrapper().getActivity().getApplicationContext().getSharedPreferences(str, 0).edit();
        edit.clear();
        return edit.commit();
    }
}
