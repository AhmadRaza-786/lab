package com.adobe.air.wand.message;

import com.adobe.air.wand.message.Message;

public class Notification extends Message {

    public static class Header extends Message.Header {
        public Header(String str, long j) {
            super(str, Message.Type.NOTIFICATION, j);
        }
    }

    public static class Data extends Message.Data {
        MessageDataObject mNotification = null;

        public Data(MessageDataObject messageDataObject) {
            setNotification(messageDataObject);
        }

        public Object getNotification() {
            return this.mNotification;
        }

        public void setNotification(MessageDataObject messageDataObject) {
            this.mNotification = messageDataObject;
        }
    }

    public Notification(Header header, Data data) {
        super(header, data);
    }

    public Header getHeader() {
        return (Header) this.mHeader;
    }

    public Data getData() {
        return (Data) this.mData;
    }
}
