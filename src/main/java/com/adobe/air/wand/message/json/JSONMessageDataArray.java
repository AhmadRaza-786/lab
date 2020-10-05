package com.adobe.air.wand.message.json;

import com.adobe.air.wand.message.MessageDataArray;
import com.adobe.air.wand.message.MessageDataObject;
import org.json.JSONArray;

public class JSONMessageDataArray implements MessageDataArray {
    final JSONArray mJSONArray;

    public JSONMessageDataArray() {
        this.mJSONArray = new JSONArray();
    }

    public JSONMessageDataArray(JSONArray jSONArray) {
        this.mJSONArray = jSONArray;
    }

    public boolean getBoolean(int i) throws Exception {
        return this.mJSONArray.getBoolean(i);
    }

    public int getInt(int i) throws Exception {
        return this.mJSONArray.getInt(i);
    }

    public long getLong(int i) throws Exception {
        return this.mJSONArray.getLong(i);
    }

    public double getDouble(int i) throws Exception {
        return this.mJSONArray.getDouble(i);
    }

    public String getString(int i) throws Exception {
        return this.mJSONArray.getString(i);
    }

    public MessageDataObject getObject(int i) throws Exception {
        return new JSONMessageDataObject(this.mJSONArray.getJSONObject(i));
    }

    public MessageDataArray getArray(int i) throws Exception {
        return new JSONMessageDataArray(this.mJSONArray.getJSONArray(i));
    }

    public MessageDataArray put(boolean z) throws Exception {
        this.mJSONArray.put(z);
        return this;
    }

    public MessageDataArray put(int i) throws Exception {
        this.mJSONArray.put(i);
        return this;
    }

    public MessageDataArray put(long j) throws Exception {
        this.mJSONArray.put(j);
        return this;
    }

    public MessageDataArray put(double d) throws Exception {
        this.mJSONArray.put(d);
        return this;
    }

    public MessageDataArray put(String str) throws Exception {
        this.mJSONArray.put(str);
        return this;
    }

    public MessageDataArray put(MessageDataObject messageDataObject) throws Exception {
        this.mJSONArray.put(((JSONMessageDataObject) messageDataObject).mJSONObject);
        return this;
    }

    public MessageDataArray put(MessageDataArray messageDataArray) throws Exception {
        this.mJSONArray.put(((JSONMessageDataArray) messageDataArray).mJSONArray);
        return this;
    }

    public MessageDataArray put(int i, boolean z) throws Exception {
        this.mJSONArray.put(i, z);
        return this;
    }

    public MessageDataArray put(int i, int i2) throws Exception {
        this.mJSONArray.put(i, i2);
        return this;
    }

    public MessageDataArray put(int i, long j) throws Exception {
        this.mJSONArray.put(i, j);
        return this;
    }

    public MessageDataArray put(int i, double d) throws Exception {
        this.mJSONArray.put(i, d);
        return this;
    }

    public MessageDataArray put(int i, String str) throws Exception {
        this.mJSONArray.put(i, str);
        return this;
    }

    public MessageDataArray put(int i, MessageDataObject messageDataObject) throws Exception {
        this.mJSONArray.put(i, ((JSONMessageDataObject) messageDataObject).mJSONObject);
        return this;
    }

    public MessageDataArray put(int i, MessageDataArray messageDataArray) throws Exception {
        this.mJSONArray.put(i, ((JSONMessageDataArray) messageDataArray).mJSONArray);
        return this;
    }

    public boolean remove(int i) {
        return false;
    }

    public int length() {
        return this.mJSONArray.length();
    }
}
