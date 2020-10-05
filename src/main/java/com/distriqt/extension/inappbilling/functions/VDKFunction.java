package com.distriqt.extension.inappbilling.functions;

import android.util.Base64;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.distriqt.extension.inappbilling.BuildConfig;
import com.distriqt.extension.inappbilling.InAppBillingContext;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Locale;
import javax.crypto.Cipher;

public class VDKFunction implements FREFunction {
    public static String TAG = VDKFunction.class.getSimpleName();

    public FREObject call(FREContext context, FREObject[] args) {
        String developerKey = BuildConfig.FLAVOR;
        int extensionId = -1;
        Boolean success = false;
        try {
            developerKey = args[0].getAsString();
            extensionId = args[1].getAsInt();
        } catch (Exception e) {
        }
        try {
            if (developerKey.length() > 40) {
                String hash = developerKey.substring(0, 40);
                byte[] dataEncoded = Base64.decode(developerKey.substring(40), 0);
                PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxYaXDdu7Jr6PRciuK0oOj+Eg5gNeFxDvRpXxjm8L7Md3bYA0Dq361W58SZ3udgUIRqx/czLBiq2fEp2eAcz1WHCJ7rHxo9j8/FyDZhcW7ENmRaLh+x1bqhymA/5xTnVJNDxV2pigUiwe0/EWW+1TMXQrIFYQnWndeGJsg+0Q4D1oSr3r7RbJzy/ZWdJwAVR8jaFcXQftq758lwKtJ/MhLiCp3ZhETtZQQXND6/1FhWu8yvOhzGfWJzPlHS+mPI9ph5XTD6zQ/9Ze2bWFZ/XRxisRhOcgNbuO2cB730jkrOE3+FkA2kd/gN49dRXtl1D2MNmuKQXHJl2dIfSCNsjR0QIDAQAB".getBytes("utf-8"), 0)));
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(2, publicKey);
                String[] details = new String(removePadding(cipher.doFinal(dataEncoded)), "UTF-8").split("\\|");
                if (details.length > 2) {
                    String type = details[0];
                    String key = details[1];
                    if (type.equals("s")) {
                        if (matchAppId(context.getActivity().getPackageName(), key) && containsId(details, extensionId) && SHA1(key).equals(hash)) {
                            success = true;
                        }
                    } else if (type.equals("t")) {
                        if (Long.parseLong(key) > System.currentTimeMillis() && containsId(details, extensionId) && SHA1(key).equals(hash)) {
                            success = true;
                        }
                    }
                }
            }
        } catch (Exception e2) {
        }
        FREObject result = null;
        try {
            result = FREObject.newObject(success.booleanValue());
            ((InAppBillingContext) context).v = success.booleanValue();
            return result;
        } catch (Exception e3) {
            return result;
        }
    }

    private boolean containsId(String[] details, int extensionId) {
        int i = 2;
        while (i < details.length) {
            try {
                if (Integer.parseInt(details[i]) == extensionId) {
                    return true;
                }
                i++;
            } catch (Exception e) {
            }
        }
        return false;
    }

    private boolean matchAppId(String appId, String keyId) {
        String appIdL = appId.toLowerCase(Locale.UK);
        String keyIdL = keyId.toLowerCase(Locale.UK);
        if (keyIdL.equals(appIdL) || (keyIdL + ".debug").equals(appIdL)) {
            return true;
        }
        if (appId.substring(0, 3).toLowerCase(Locale.UK).equals("air")) {
            return matchAppId(appId.substring(4), keyId);
        }
        String modId = keyId.replaceAll("(\\.)([0-9])", "$1A$2").replaceAll("-", "_");
        if (!modId.equals(keyId)) {
            return matchAppId(appId, modId);
        }
        return false;
    }

    private byte[] removePadding(byte[] data) {
        int startIndex = 0;
        while (data[startIndex] == 0 && startIndex < data.length - 1) {
            startIndex++;
        }
        if (data[startIndex] == 1 || data[startIndex] == 2) {
            int startIndex2 = startIndex + 1;
            int k = 1;
            while (k < data.length && data[k] != 0) {
                startIndex2++;
                k++;
            }
            startIndex = startIndex2 + 1;
        }
        byte[] cleaned = new byte[(data.length - startIndex)];
        for (int i = 0; i < data.length - startIndex; i++) {
            cleaned[i] = data[startIndex + i];
        }
        return cleaned;
    }

    private String SHA1(String text) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        return convertToHex(md.digest());
    }

    private String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 15;
            int two_halfs = 0;
            while (true) {
                buf.append((halfbyte < 0 || halfbyte > 9) ? (char) ((halfbyte - 10) + 97) : (char) (halfbyte + 48));
                halfbyte = b & 15;
                int two_halfs2 = two_halfs + 1;
                if (two_halfs >= 1) {
                    break;
                }
                two_halfs = two_halfs2;
            }
        }
        return buf.toString();
    }
}
