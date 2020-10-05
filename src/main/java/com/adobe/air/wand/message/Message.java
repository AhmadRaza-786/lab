package com.adobe.air.wand.message;

public abstract class Message {
    protected final Data mData;
    protected final Header mHeader;

    public static abstract class Data {
    }

    public enum Type {
        REQUEST("REQUEST"),
        RESPONSE("RESPONSE"),
        NOTIFICATION("NOTIFICATION");
        
        private final String mType;

        private Type(String str) {
            this.mType = str;
        }

        public String toString() {
            return this.mType;
        }
    }

    public static abstract class Header {
        protected long mTimestamp = 0;
        protected String mTitle = null;
        protected Type mType = null;

        public Header(String str, Type type, long j) {
            this.mTitle = str;
            this.mType = type;
            this.mTimestamp = j;
        }

        public String getTitle() {
            return this.mTitle;
        }

        public Type getType() {
            return this.mType;
        }

        public long getTimestamp() {
            return this.mTimestamp;
        }

        public void setTitle(String str) {
            this.mTitle = str;
        }

        public void setType(Type type) {
            this.mType = type;
        }

        public void setTimestamp(long j) {
            this.mTimestamp = j;
        }
    }

    public Header getHeader() {
        return this.mHeader;
    }

    public Data getData() {
        return this.mData;
    }

    public Message(Header header, Data data) {
        this.mHeader = header;
        this.mData = data;
    }
}
