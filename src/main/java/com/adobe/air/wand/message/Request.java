package com.adobe.air.wand.message;

import com.adobe.air.wand.message.Message;
import com.distriqt.extension.inappbilling.BuildConfig;

public class Request extends Message {

    public static class Header extends Message.Header {
        protected String mTaskID = BuildConfig.FLAVOR;

        public Header(String str, String str2, long j) {
            super(str, Message.Type.REQUEST, j);
            this.mTaskID = str2;
        }

        public String getTaskID() {
            return this.mTaskID;
        }

        public void setTaskID(String str) {
            this.mTaskID = str;
        }
    }

    public static class Data extends Message.Data {
        MessageDataArray mArguments = null;

        public Data(MessageDataArray messageDataArray) {
            setArguments(messageDataArray);
        }

        public MessageDataArray getArguments() {
            return this.mArguments;
        }

        public void setArguments(MessageDataArray messageDataArray) {
            this.mArguments = messageDataArray;
        }
    }

    public Request(Header header, Data data) {
        super(header, data);
    }

    public Header getHeader() {
        return (Header) this.mHeader;
    }

    public Data getData() {
        return (Data) this.mData;
    }
}
