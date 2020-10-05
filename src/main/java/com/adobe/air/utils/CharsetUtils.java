package com.adobe.air.utils;

import com.distriqt.extension.inappbilling.BuildConfig;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.Iterator;

public class CharsetUtils {
    public static final byte[] EMPTY_BARRAY = new byte[0];
    public static final char[] EMPTY_CARRAY = new char[0];
    public static final String LOG_TAG = CharsetUtils.class.toString();

    public static byte[] utf16ToUtf8(char[] cArr) {
        try {
            byte[] array = Charset.forName("UTF-8").newEncoder().encode(CharBuffer.wrap(cArr)).array();
            if (array == null) {
                return EMPTY_BARRAY;
            }
            return array;
        } catch (Exception e) {
            return EMPTY_BARRAY;
        }
    }

    public static char[] mbcsToUtf16(byte[] bArr) {
        try {
            char[] array = Charset.forName("ISO-8859-1").newDecoder().decode(ByteBuffer.wrap(bArr)).array();
            if (array == null) {
                return EMPTY_CARRAY;
            }
            return array;
        } catch (Exception e) {
            return EMPTY_CARRAY;
        }
    }

    public static byte[] mbcsToUtf8(byte[] bArr) {
        return utf16ToUtf8(mbcsToUtf16(bArr));
    }

    public static CharBuffer DecodeBuffer(byte[] bArr, String str) throws CharacterCodingException {
        CharsetDecoder newDecoder = Charset.forName(str).newDecoder();
        newDecoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
        return newDecoder.decode(ByteBuffer.wrap(bArr));
    }

    public static byte[] ConvertMBCStoUTF16(byte[] bArr, String str) {
        try {
            return Charset.forName("UTF-16LE").encode(DecodeBuffer(bArr, str)).array();
        } catch (Exception e) {
            return EMPTY_BARRAY;
        }
    }

    public static byte[] ConvertUTF16toMBCS(byte[] bArr, String str) {
        try {
            CharBuffer DecodeBuffer = DecodeBuffer(bArr, "UTF-16LE");
            CharsetEncoder newEncoder = Charset.forName(str).newEncoder();
            newEncoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
            return newEncoder.encode(DecodeBuffer).array();
        } catch (Exception e) {
            return EMPTY_BARRAY;
        }
    }

    public static byte[] ConvertUTF8toMBCS(byte[] bArr, String str) {
        try {
            CharBuffer DecodeBuffer = DecodeBuffer(bArr, "UTF-8");
            CharsetEncoder newEncoder = Charset.forName(str).newEncoder();
            newEncoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
            return newEncoder.encode(DecodeBuffer).array();
        } catch (Exception e) {
            return EMPTY_BARRAY;
        }
    }

    public static byte[] ConvertMBCStoUTF8(byte[] bArr, String str) {
        try {
            return Charset.forName("UTF-8").newEncoder().encode(DecodeBuffer(bArr, str)).array();
        } catch (Exception e) {
            return EMPTY_BARRAY;
        }
    }

    public static String QueryAvailableCharsets() {
        String str = BuildConfig.FLAVOR;
        Iterator<String> it = Charset.availableCharsets().keySet().iterator();
        while (true) {
            String str2 = str;
            if (!it.hasNext()) {
                return str2;
            }
            str = str2 + it.next() + " ";
        }
    }
}
