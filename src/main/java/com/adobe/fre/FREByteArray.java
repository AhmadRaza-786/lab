package com.adobe.fre;

import com.adobe.fre.FREObject;
import java.nio.ByteBuffer;

public class FREByteArray extends FREObject {
    private long m_dataPointer;

    public native void acquire() throws FREInvalidObjectException, FREWrongThreadException, IllegalStateException;

    public native ByteBuffer getBytes() throws FREInvalidObjectException, FREWrongThreadException, IllegalStateException;

    public native long getLength() throws FREInvalidObjectException, FREWrongThreadException, IllegalStateException;

    public native void release() throws FREInvalidObjectException, FREWrongThreadException, IllegalStateException;

    private FREByteArray(FREObject.CFREObjectWrapper obj) {
        super(obj);
    }

    protected FREByteArray() throws FRETypeMismatchException, FREInvalidObjectException, FREASErrorException, FRENoSuchNameException, FREWrongThreadException, IllegalStateException {
        super("flash.utils.ByteArray", (FREObject[]) null);
    }

    public static FREByteArray newByteArray() throws FREASErrorException, FREWrongThreadException, IllegalStateException {
        try {
            return new FREByteArray();
        } catch (FREInvalidObjectException | FRENoSuchNameException | FRETypeMismatchException e) {
            return null;
        }
    }
}
