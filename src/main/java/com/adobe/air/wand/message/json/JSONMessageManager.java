package com.adobe.air.wand.message.json;

import com.adobe.air.wand.message.Message;
import com.adobe.air.wand.message.MessageDataArray;
import com.adobe.air.wand.message.MessageDataObject;
import com.adobe.air.wand.message.MessageManager;
import com.adobe.air.wand.message.Notification;
import com.adobe.air.wand.message.Request;
import com.adobe.air.wand.message.Response;
import org.json.JSONObject;

public class JSONMessageManager extends MessageManager {
    public MessageDataObject createDataObject() {
        return new JSONMessageDataObject();
    }

    public MessageDataArray createDataArray() {
        return new JSONMessageDataArray();
    }

    public String serializeMessage(Message message) throws Exception {
        return createJSONMessage(message).toString();
    }

    public Message deserializeWandMessage(String str) throws Exception {
        return createWandMessage(new JSONObject(str));
    }

    public Request createWandRequest(String str, String str2, MessageDataArray messageDataArray) throws Exception {
        Request.Header header = new Request.Header(str, str2, System.currentTimeMillis());
        if (messageDataArray == null) {
            messageDataArray = new JSONMessageDataArray();
        }
        return new Request(header, new Request.Data(messageDataArray));
    }

    public Response createWandResponse(String str, String str2, MessageDataObject messageDataObject, Response.Status status) throws Exception {
        Response.Header header = new Response.Header(str, str2, System.currentTimeMillis(), status);
        if (messageDataObject == null) {
            messageDataObject = new JSONMessageDataObject();
        }
        return new Response(header, new Response.Data(messageDataObject));
    }

    public Notification createWandNotification(String str, MessageDataObject messageDataObject) throws Exception {
        Notification.Header header = new Notification.Header(str, System.currentTimeMillis());
        if (messageDataObject == null) {
            messageDataObject = new JSONMessageDataObject();
        }
        return new Notification(header, new Notification.Data(messageDataObject));
    }

    private static Message createWandMessage(JSONObject jSONObject) throws Exception {
        Message message;
        Response.Status status;
        synchronized (jSONObject) {
            JSONObject jSONObject2 = jSONObject.getJSONObject("header");
            JSONObject jSONObject3 = jSONObject.getJSONObject("data");
            String string = jSONObject2.getString("title");
            String string2 = jSONObject2.getString("type");
            long j = jSONObject2.getLong("timestamp");
            message = null;
            if (string2.equals(Message.Type.REQUEST.toString())) {
                message = new Request(new Request.Header(string, jSONObject2.getString("taskID"), j), new Request.Data(new JSONMessageDataArray(jSONObject3.getJSONArray("arguments"))));
            } else if (string2.equals(Message.Type.RESPONSE.toString())) {
                String string3 = jSONObject2.getString("taskID");
                String string4 = jSONObject2.getString("status");
                if (string4.equals(Response.Status.SUCCESS.toString())) {
                    status = Response.Status.SUCCESS;
                } else if (string4.equals(Response.Status.ERROR.toString())) {
                    status = Response.Status.ERROR;
                } else {
                    throw new Exception("Unable to fetch Response status");
                }
                message = new Response(new Response.Header(string, string3, j, status), new Response.Data(new JSONMessageDataObject(jSONObject3.getJSONObject("result"))));
            } else if (string2.equals(Message.Type.NOTIFICATION.toString())) {
                message = new Notification(new Notification.Header(string, j), new Notification.Data(new JSONMessageDataObject(jSONObject3.getJSONObject("notification"))));
            }
        }
        return message;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: com.adobe.air.wand.message.Response$Header} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v9, resolved type: com.adobe.air.wand.message.Notification$Header} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: com.adobe.air.wand.message.Request$Header} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v14, resolved type: com.adobe.air.wand.message.Notification$Header} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v15, resolved type: com.adobe.air.wand.message.Notification$Header} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v16, resolved type: com.adobe.air.wand.message.Notification$Header} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static org.json.JSONObject createJSONMessage(com.adobe.air.wand.message.Message r6) throws java.lang.Exception {
        /*
            monitor-enter(r6)
            com.adobe.air.wand.message.Message$Header r0 = r6.getHeader()     // Catch:{ all -> 0x0026 }
            com.adobe.air.wand.message.Message$Type r0 = r0.getType()     // Catch:{ all -> 0x0026 }
            org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ all -> 0x0026 }
            r2.<init>()     // Catch:{ all -> 0x0026 }
            org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ all -> 0x0026 }
            r3.<init>()     // Catch:{ all -> 0x0026 }
            int[] r1 = com.adobe.air.wand.message.json.JSONMessageManager.AnonymousClass1.$SwitchMap$com$adobe$air$wand$message$Message$Type     // Catch:{ all -> 0x0026 }
            int r0 = r0.ordinal()     // Catch:{ all -> 0x0026 }
            r0 = r1[r0]     // Catch:{ all -> 0x0026 }
            switch(r0) {
                case 1: goto L_0x0029;
                case 2: goto L_0x007b;
                case 3: goto L_0x00ab;
                default: goto L_0x001e;
            }     // Catch:{ all -> 0x0026 }
        L_0x001e:
            java.lang.Exception r0 = new java.lang.Exception     // Catch:{ all -> 0x0026 }
            java.lang.String r1 = "Unsupported message type"
            r0.<init>(r1)     // Catch:{ all -> 0x0026 }
            throw r0     // Catch:{ all -> 0x0026 }
        L_0x0026:
            r0 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x0026 }
            throw r0
        L_0x0029:
            com.adobe.air.wand.message.Message$Header r0 = r6.getHeader()     // Catch:{ all -> 0x0026 }
            com.adobe.air.wand.message.Request$Header r0 = (com.adobe.air.wand.message.Request.Header) r0     // Catch:{ all -> 0x0026 }
            com.adobe.air.wand.message.Message$Data r1 = r6.getData()     // Catch:{ all -> 0x0026 }
            com.adobe.air.wand.message.Request$Data r1 = (com.adobe.air.wand.message.Request.Data) r1     // Catch:{ all -> 0x0026 }
            java.lang.String r4 = "taskID"
            java.lang.String r5 = r0.getTaskID()     // Catch:{ all -> 0x0026 }
            r2.put(r4, r5)     // Catch:{ all -> 0x0026 }
            java.lang.String r4 = "arguments"
            com.adobe.air.wand.message.MessageDataArray r1 = r1.getArguments()     // Catch:{ all -> 0x0026 }
            com.adobe.air.wand.message.json.JSONMessageDataArray r1 = (com.adobe.air.wand.message.json.JSONMessageDataArray) r1     // Catch:{ all -> 0x0026 }
            org.json.JSONArray r1 = r1.mJSONArray     // Catch:{ all -> 0x0026 }
            r3.put(r4, r1)     // Catch:{ all -> 0x0026 }
        L_0x004b:
            java.lang.String r1 = "title"
            java.lang.String r4 = r0.getTitle()     // Catch:{ all -> 0x0026 }
            r2.put(r1, r4)     // Catch:{ all -> 0x0026 }
            java.lang.String r1 = "type"
            com.adobe.air.wand.message.Message$Type r4 = r0.getType()     // Catch:{ all -> 0x0026 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0026 }
            r2.put(r1, r4)     // Catch:{ all -> 0x0026 }
            java.lang.String r1 = "timestamp"
            long r4 = r0.getTimestamp()     // Catch:{ all -> 0x0026 }
            r2.put(r1, r4)     // Catch:{ all -> 0x0026 }
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ all -> 0x0026 }
            r0.<init>()     // Catch:{ all -> 0x0026 }
            java.lang.String r1 = "header"
            r0.put(r1, r2)     // Catch:{ all -> 0x0026 }
            java.lang.String r1 = "data"
            r0.put(r1, r3)     // Catch:{ all -> 0x0026 }
            monitor-exit(r6)     // Catch:{ all -> 0x0026 }
            return r0
        L_0x007b:
            com.adobe.air.wand.message.Message$Header r0 = r6.getHeader()     // Catch:{ all -> 0x0026 }
            com.adobe.air.wand.message.Response$Header r0 = (com.adobe.air.wand.message.Response.Header) r0     // Catch:{ all -> 0x0026 }
            com.adobe.air.wand.message.Message$Data r1 = r6.getData()     // Catch:{ all -> 0x0026 }
            com.adobe.air.wand.message.Response$Data r1 = (com.adobe.air.wand.message.Response.Data) r1     // Catch:{ all -> 0x0026 }
            java.lang.String r4 = "status"
            com.adobe.air.wand.message.Response$Status r5 = r0.getStatus()     // Catch:{ all -> 0x0026 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0026 }
            r2.put(r4, r5)     // Catch:{ all -> 0x0026 }
            java.lang.String r4 = "taskID"
            java.lang.String r5 = r0.getTaskID()     // Catch:{ all -> 0x0026 }
            r2.put(r4, r5)     // Catch:{ all -> 0x0026 }
            java.lang.String r4 = "result"
            java.lang.Object r1 = r1.getResult()     // Catch:{ all -> 0x0026 }
            com.adobe.air.wand.message.json.JSONMessageDataObject r1 = (com.adobe.air.wand.message.json.JSONMessageDataObject) r1     // Catch:{ all -> 0x0026 }
            org.json.JSONObject r1 = r1.mJSONObject     // Catch:{ all -> 0x0026 }
            r3.put(r4, r1)     // Catch:{ all -> 0x0026 }
            goto L_0x004b
        L_0x00ab:
            com.adobe.air.wand.message.Message$Header r0 = r6.getHeader()     // Catch:{ all -> 0x0026 }
            com.adobe.air.wand.message.Notification$Header r0 = (com.adobe.air.wand.message.Notification.Header) r0     // Catch:{ all -> 0x0026 }
            com.adobe.air.wand.message.Message$Data r1 = r6.getData()     // Catch:{ all -> 0x0026 }
            com.adobe.air.wand.message.Notification$Data r1 = (com.adobe.air.wand.message.Notification.Data) r1     // Catch:{ all -> 0x0026 }
            java.lang.String r4 = "notification"
            java.lang.Object r1 = r1.getNotification()     // Catch:{ all -> 0x0026 }
            com.adobe.air.wand.message.json.JSONMessageDataObject r1 = (com.adobe.air.wand.message.json.JSONMessageDataObject) r1     // Catch:{ all -> 0x0026 }
            org.json.JSONObject r1 = r1.mJSONObject     // Catch:{ all -> 0x0026 }
            r3.put(r4, r1)     // Catch:{ all -> 0x0026 }
            goto L_0x004b
        */
        throw new UnsupportedOperationException("Method not decompiled: com.adobe.air.wand.message.json.JSONMessageManager.createJSONMessage(com.adobe.air.wand.message.Message):org.json.JSONObject");
    }
}
