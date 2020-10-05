package com.adobe.fre;

import com.adobe.fre.FREObject;

public class FREArray extends FREObject {
    public native long getLength() throws FREInvalidObjectException, FREWrongThreadException;

    public native FREObject getObjectAt(long j) throws FREInvalidObjectException, IllegalArgumentException, FREWrongThreadException;

    public native void setLength(long j) throws FREInvalidObjectException, IllegalArgumentException, FREReadOnlyException, FREWrongThreadException;

    public native void setObjectAt(long j, FREObject fREObject) throws FREInvalidObjectException, FRETypeMismatchException, FREWrongThreadException;

    private FREArray(FREObject.CFREObjectWrapper obj) {
        super(obj);
    }

    protected FREArray(String base, FREObject[] constructorArgs) throws FRETypeMismatchException, FREInvalidObjectException, FREASErrorException, FRENoSuchNameException, FREWrongThreadException {
        super("Vector.<" + base + ">", constructorArgs);
    }

    protected FREArray(FREObject[] constructorArgs) throws FRETypeMismatchException, FREInvalidObjectException, FREASErrorException, FRENoSuchNameException, FREWrongThreadException {
        super("Array", constructorArgs);
    }

    public static FREArray newArray(String classname, int numElements, boolean fixed) throws FREASErrorException, FRENoSuchNameException, FREWrongThreadException, IllegalStateException {
        try {
            return new FREArray(classname, new FREObject[]{new FREObject(numElements), new FREObject(fixed)});
        } catch (FREInvalidObjectException | FRETypeMismatchException e) {
            return null;
        }
    }

    public static FREArray newArray(int numElements) throws FREASErrorException, FREWrongThreadException, IllegalStateException {
        try {
            return new FREArray(new FREObject[]{new FREObject(numElements)});
        } catch (FREInvalidObjectException | FRENoSuchNameException | FRETypeMismatchException e) {
            return null;
        }
    }
}
