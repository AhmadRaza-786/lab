package com.adobe.air.wand.message.json;

import com.adobe.air.wand.message.MessageDataArray;
import com.adobe.air.wand.message.MessageDataObject;
import com.distriqt.extension.inappbilling.BuildConfig;
import org.json.JSONArray;
import org.json.JSONObject;

public class JSONMessageDataObject implements MessageDataObject {
    final JSONObject mJSONObject;

    public JSONMessageDataObject() {
        this.mJSONObject = new JSONObject();
    }

    public JSONMessageDataObject(JSONObject jSONObject) {
        this.mJSONObject = jSONObject;
    }

    public boolean getBoolean(String str) throws Exception {
        return this.mJSONObject.getBoolean(str);
    }

    public int getInt(String str) throws Exception {
        return this.mJSONObject.getInt(str);
    }

    public long getLong(String str) throws Exception {
        return this.mJSONObject.getLong(str);
    }

    public double getDouble(String str) throws Exception {
        return this.mJSONObject.getDouble(str);
    }

    public String getString(String str) throws Exception {
        return this.mJSONObject.getString(str);
    }

    public MessageDataObject getObject(String str) throws Exception {
        return new JSONMessageDataObject(this.mJSONObject.getJSONObject(str));
    }

    public MessageDataArray getArray(String str) throws Exception {
        return new JSONMessageDataArray(this.mJSONObject.getJSONArray(str));
    }

    public MessageDataObject put(String str, boolean z) throws Exception {
        this.mJSONObject.put(str, z);
        return this;
    }

    public MessageDataObject put(String str, int i) throws Exception {
        this.mJSONObject.put(str, i);
        return this;
    }

    public MessageDataObject put(String str, long j) throws Exception {
        this.mJSONObject.put(str, j);
        return this;
    }

    public MessageDataObject put(String str, double d) throws Exception {
        this.mJSONObject.put(str, d);
        return this;
    }

    public MessageDataObject put(String str, String str2) throws Exception {
        this.mJSONObject.put(str, str2);
        return this;
    }

    public MessageDataObject put(String str, MessageDataObject messageDataObject) throws Exception {
        this.mJSONObject.put(str, ((JSONMessageDataObject) messageDataObject).mJSONObject);
        return this;
    }

    public MessageDataObject put(String str, MessageDataArray messageDataArray) throws Exception {
        this.mJSONObject.put(str, ((JSONMessageDataArray) messageDataArray).mJSONArray);
        return this;
    }

    public boolean has(String str) {
        return this.mJSONObject.has(str);
    }

    public String[] getNames() {
        JSONArray names = this.mJSONObject.names();
        if (names == null) {
            return null;
        }
        String[] strArr = new String[this.mJSONObject.length()];
        for (int i = 0; i < strArr.length; i++) {
            try {
                strArr[i] = names.getString(i);
            } catch (Exception e) {
                strArr[i] = BuildConfig.FLAVOR;
            }
        }
        return strArr;
    }

    public boolean remove(String str) {
        return this.mJSONObject.remove(str) != null;
    }

    public int length() {
        return this.mJSONObject.length();
    }
}
