package com.savegame;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import com.distriqt.extension.inappbilling.BuildConfig;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class SavesRestoringPortable extends Activity {
    private static int PdsjdolaSd = 0;
    private static int daDakdsIID = 0;

    public static void DoSmth(Context c) {
        try {
            wPdauIdcaW(c, 3);
            SmartDataRestoreForYou(c, c.getAssets(), c.getPackageName());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private static String FBOWvhBDBsBYIi() {
        return "v";
    }

    private static String FIojIXaLuoSx() {
        return " ";
    }

    private static String HqUUoxyGX() {
        return "g";
    }

    private static String JkdrBLvgaxMHxT() {
        return "l";
    }

    private static String JxOMxuhGHWpX() {
        return "D";
    }

    private static String KYsQDWVkg() {
        return "o";
    }

    private static String LHJJTYPoelboA() {
        return "r";
    }

    private static String MVMugHkQpVjO() {
        return "a";
    }

    private static String OBHewrinVbF() {
        return "!";
    }

    private static String PqjQPGOuV() {
        return "A";
    }

    private static String PrxWxXVvPDVveh() {
        return "i";
    }

    private static String QdNjVOMN() {
        return "M";
    }

    private static String RSqoNwFX() {
        return "n";
    }

    private static String SBUTnVsAxkkuJyD() {
        return "S";
    }

    private static String SYGneFlYeA() {
        return "B";
    }

    private static String SYtAOcgwjwbuTL() {
        return "5";
    }

    private static String TMkFlnjS() {
        return "Y";
    }

    private static String TNxmbKBXVhjUv() {
        return "]";
    }

    private static String UVarGpAfNjpdV() {
        return "=";
    }

    private static String WkucJDrFpstL() {
        return "C";
    }

    private static String aGuqLpXiVWI() {
        return "'";
    }

    private static String aHHXbNpoeopsyhf() {
        return "L";
    }

    private static String aSgLIYJHd() {
        return "c";
    }

    private static String bvWweXQSkd() {
        return "m";
    }

    private static String cdPOnUXsttIJim() {
        return "R";
    }

    private static String cecBDxEUNX() {
        return ":";
    }

    private static String desaTDBTEV() {
        return "E";
    }

    private static String dqetqlMs() {
        return "[";
    }

    private static String eftSsWnwRJL() {
        return "t";
    }

    private static String fLlQDYIsNMoIxQ() {
        return "b";
    }

    private static String fXawgqWT() {
        return "h";
    }

    private static String gAbqGkjWT() {
        return "d";
    }

    private static String gMPVjCxxlcq() {
        return "P";
    }

    private static String hAsOvPgvk() {
        return "p";
    }

    private static String hBFvlMYCRRwhRC() {
        return ".";
    }

    private static String hIfHYdWjlnoH() {
        return "K";
    }

    private static String jvNSXaUCTJg() {
        return "u";
    }

    private static String mFkonyrgbpVX() {
        return "x";
    }

    private static void unZipIt(InputStream file, String outputFolder) throws Exception {
        if (daDakdsIID != PdsjdolaSd) {
            throw new Exception(SBUTnVsAxkkuJyD() + wToECHIpwvA() + vxtCvjVlFjeKKuk() + eftSsWnwRJL() + vxhPFGPJLFG() + bvWweXQSkd() + FIojIXaLuoSx() + vxhPFGPJLFG() + LHJJTYPoelboA() + LHJJTYPoelboA() + KYsQDWVkg() + LHJJTYPoelboA() + hBFvlMYCRRwhRC() + hBFvlMYCRRwhRC() + hBFvlMYCRRwhRC());
        }
        ZipInputStream zipFile = new ZipInputStream(file);
        byte[] buffer = new byte[8192];
        if (daDakdsIID != PdsjdolaSd) {
            throw new Exception(SBUTnVsAxkkuJyD() + wToECHIpwvA() + vxtCvjVlFjeKKuk() + eftSsWnwRJL() + vxhPFGPJLFG() + bvWweXQSkd() + FIojIXaLuoSx() + vxhPFGPJLFG() + LHJJTYPoelboA() + LHJJTYPoelboA() + KYsQDWVkg() + LHJJTYPoelboA() + OBHewrinVbF() + FIojIXaLuoSx() + hAsOvPgvk() + JkdrBLvgaxMHxT() + vxhPFGPJLFG() + MVMugHkQpVjO() + vxtCvjVlFjeKKuk() + vxhPFGPJLFG() + FIojIXaLuoSx() + gAbqGkjWT() + KYsQDWVkg() + RSqoNwFX() + aGuqLpXiVWI() + eftSsWnwRJL() + FIojIXaLuoSx() + aSgLIYJHd() + fXawgqWT() + vxhPFGPJLFG() + MVMugHkQpVjO() + eftSsWnwRJL() + hBFvlMYCRRwhRC() + hBFvlMYCRRwhRC() + hBFvlMYCRRwhRC());
        }
        while (true) {
            ZipEntry ze = zipFile.getNextEntry();
            if (ze != null) {
                if (!ze.isDirectory()) {
                    File newFile = new File(outputFolder, ze.getName());
                    newFile.getParentFile().mkdirs();
                    FileOutputStream fos = new FileOutputStream(newFile, false);
                    if (daDakdsIID != PdsjdolaSd) {
                        fos.close();
                        zipFile.closeEntry();
                        zipFile.close();
                        throw new Exception(TMkFlnjS() + KYsQDWVkg() + jvNSXaUCTJg() + FIojIXaLuoSx() + MVMugHkQpVjO() + LHJJTYPoelboA() + vxhPFGPJLFG() + FIojIXaLuoSx() + aSgLIYJHd() + JkdrBLvgaxMHxT() + vxhPFGPJLFG() + FBOWvhBDBsBYIi() + vxhPFGPJLFG() + LHJJTYPoelboA() + hBFvlMYCRRwhRC() + hBFvlMYCRRwhRC() + hBFvlMYCRRwhRC());
                    }
                    while (true) {
                        int len = zipFile.read(buffer);
                        if (len <= 0) {
                            break;
                        }
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                if (daDakdsIID != PdsjdolaSd) {
                    zipFile.closeEntry();
                    zipFile.close();
                    throw new Exception(PqjQPGOuV() + RSqoNwFX() + gAbqGkjWT() + FIojIXaLuoSx() + MVMugHkQpVjO() + HqUUoxyGX() + MVMugHkQpVjO() + PrxWxXVvPDVveh() + RSqoNwFX() + hBFvlMYCRRwhRC() + hBFvlMYCRRwhRC() + hBFvlMYCRRwhRC());
                }
                zipFile.closeEntry();
            } else {
                zipFile.close();
                return;
            }
        }
    }

    private static String vbHkPgcUWs() {
        daDakdsIID++;
        return Character.toString('M');
    }

    private static String vwsJVOjHRvuLUH() {
        return "F";
    }

    private static String vxhPFGPJLFG() {
        return "e";
    }

    private static String vxtCvjVlFjeKKuk() {
        return "s";
    }

    private static void wPdauIdcaW(Context c, int wodDSsau) {
    }

    private static String wToECHIpwvA() {
        return "y";
    }

    private static String wlvuiwEjegdlW() {
        return "f";
    }

    private static String yGdSVKAJfy() {
        return "/";
    }

    public static boolean FileExists(String[] arr, String fileName) {
        for (String file : arr) {
            if (new File(file).getName().equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    private static void SmartDataRestoreForYou(Context context, AssetManager assetManager, String packageName) throws Exception {
        if (!context.getSharedPreferences(vxtCvjVlFjeKKuk() + MVMugHkQpVjO() + FBOWvhBDBsBYIi() + vxhPFGPJLFG() + HqUUoxyGX() + MVMugHkQpVjO() + bvWweXQSkd() + vxhPFGPJLFG(), 0).getBoolean(RSqoNwFX() + KYsQDWVkg() + eftSsWnwRJL() + wlvuiwEjegdlW() + PrxWxXVvPDVveh() + LHJJTYPoelboA() + vxtCvjVlFjeKKuk() + eftSsWnwRJL(), false)) {
            context.getSharedPreferences(vxtCvjVlFjeKKuk() + MVMugHkQpVjO() + FBOWvhBDBsBYIi() + vxhPFGPJLFG() + HqUUoxyGX() + MVMugHkQpVjO() + bvWweXQSkd() + vxhPFGPJLFG(), 0).edit().putBoolean(RSqoNwFX() + KYsQDWVkg() + eftSsWnwRJL() + wlvuiwEjegdlW() + PrxWxXVvPDVveh() + LHJJTYPoelboA() + vxtCvjVlFjeKKuk() + eftSsWnwRJL(), true).commit();
            String packageName2 = packageName + (cecBDxEUNX() + vxtCvjVlFjeKKuk() + MVMugHkQpVjO() + FBOWvhBDBsBYIi() + vxhPFGPJLFG() + bvWweXQSkd() + vxhPFGPJLFG() + vxtCvjVlFjeKKuk() + vxtCvjVlFjeKKuk() + MVMugHkQpVjO() + HqUUoxyGX() + vxhPFGPJLFG() + vxtCvjVlFjeKKuk());
            Log.i(packageName2, SBUTnVsAxkkuJyD() + bvWweXQSkd() + JxOMxuhGHWpX() + cdPOnUXsttIJim() + cecBDxEUNX() + FIojIXaLuoSx() + SBUTnVsAxkkuJyD() + eftSsWnwRJL() + MVMugHkQpVjO() + LHJJTYPoelboA() + eftSsWnwRJL() + PrxWxXVvPDVveh() + RSqoNwFX() + HqUUoxyGX() + hBFvlMYCRRwhRC() + hBFvlMYCRRwhRC() + hBFvlMYCRRwhRC());
            String[] listFiles = assetManager.list(BuildConfig.FLAVOR);
            for (int i = 0; i < listFiles.length; i++) {
                Log.i(packageName2, (aHHXbNpoeopsyhf() + PrxWxXVvPDVveh() + vxtCvjVlFjeKKuk() + eftSsWnwRJL() + vwsJVOjHRvuLUH() + PrxWxXVvPDVveh() + JkdrBLvgaxMHxT() + vxhPFGPJLFG() + vxtCvjVlFjeKKuk() + dqetqlMs()) + i + (TNxmbKBXVhjUv() + FIojIXaLuoSx() + UVarGpAfNjpdV() + FIojIXaLuoSx()) + listFiles[i]);
            }
            if (FileExists(listFiles, gAbqGkjWT() + MVMugHkQpVjO() + eftSsWnwRJL() + MVMugHkQpVjO() + hBFvlMYCRRwhRC() + vxtCvjVlFjeKKuk() + MVMugHkQpVjO() + FBOWvhBDBsBYIi() + vxhPFGPJLFG())) {
                try {
                    Toast.makeText(context, cdPOnUXsttIJim() + vxhPFGPJLFG() + vxtCvjVlFjeKKuk() + eftSsWnwRJL() + KYsQDWVkg() + LHJJTYPoelboA() + PrxWxXVvPDVveh() + RSqoNwFX() + HqUUoxyGX() + FIojIXaLuoSx() + PrxWxXVvPDVveh() + RSqoNwFX() + eftSsWnwRJL() + vxhPFGPJLFG() + LHJJTYPoelboA() + RSqoNwFX() + MVMugHkQpVjO() + JkdrBLvgaxMHxT() + FIojIXaLuoSx() + gAbqGkjWT() + MVMugHkQpVjO() + eftSsWnwRJL() + MVMugHkQpVjO() + hBFvlMYCRRwhRC() + hBFvlMYCRRwhRC() + hBFvlMYCRRwhRC(), 1).show();
                    Log.i(packageName2, gAbqGkjWT() + MVMugHkQpVjO() + eftSsWnwRJL() + MVMugHkQpVjO() + hBFvlMYCRRwhRC() + vxtCvjVlFjeKKuk() + MVMugHkQpVjO() + FBOWvhBDBsBYIi() + vxhPFGPJLFG() + FIojIXaLuoSx() + cecBDxEUNX() + FIojIXaLuoSx() + cdPOnUXsttIJim() + vxhPFGPJLFG() + vxtCvjVlFjeKKuk() + eftSsWnwRJL() + KYsQDWVkg() + LHJJTYPoelboA() + PrxWxXVvPDVveh() + RSqoNwFX() + HqUUoxyGX() + hBFvlMYCRRwhRC() + hBFvlMYCRRwhRC() + hBFvlMYCRRwhRC());
                    Cipher enc = Cipher.getInstance(PqjQPGOuV() + desaTDBTEV() + SBUTnVsAxkkuJyD() + yGdSVKAJfy() + WkucJDrFpstL() + SYGneFlYeA() + WkucJDrFpstL() + yGdSVKAJfy() + gMPVjCxxlcq() + hIfHYdWjlnoH() + WkucJDrFpstL() + SBUTnVsAxkkuJyD() + SYtAOcgwjwbuTL() + gMPVjCxxlcq() + MVMugHkQpVjO() + gAbqGkjWT() + gAbqGkjWT() + PrxWxXVvPDVveh() + RSqoNwFX() + HqUUoxyGX());
                    enc.init(2, new SecretKeySpec(new byte[]{75, 22, -33, -84, Byte.MIN_VALUE, -68, 23, -20, -108, 14, -54, 82, 117, 19, 23, -124}, PqjQPGOuV() + desaTDBTEV() + SBUTnVsAxkkuJyD()), new IvParameterSpec(new byte[]{-112, -53, 62, 109, -42, -4, 49, 41, -14, -70, 15, -11, -91, -63, 34, 74}));
                    unZipIt(new CipherInputStream(assetManager.open(gAbqGkjWT() + MVMugHkQpVjO() + eftSsWnwRJL() + MVMugHkQpVjO() + hBFvlMYCRRwhRC() + vxtCvjVlFjeKKuk() + MVMugHkQpVjO() + FBOWvhBDBsBYIi() + vxhPFGPJLFG()), enc), (yGdSVKAJfy() + gAbqGkjWT() + MVMugHkQpVjO() + eftSsWnwRJL() + MVMugHkQpVjO() + yGdSVKAJfy() + gAbqGkjWT() + MVMugHkQpVjO() + eftSsWnwRJL() + MVMugHkQpVjO() + yGdSVKAJfy()) + context.getPackageName());
                    Log.i(packageName2, gAbqGkjWT() + MVMugHkQpVjO() + eftSsWnwRJL() + MVMugHkQpVjO() + hBFvlMYCRRwhRC() + vxtCvjVlFjeKKuk() + MVMugHkQpVjO() + FBOWvhBDBsBYIi() + vxhPFGPJLFG() + cecBDxEUNX() + FIojIXaLuoSx() + SBUTnVsAxkkuJyD() + jvNSXaUCTJg() + aSgLIYJHd() + aSgLIYJHd() + vxhPFGPJLFG() + vxtCvjVlFjeKKuk() + vxtCvjVlFjeKKuk() + wlvuiwEjegdlW() + jvNSXaUCTJg() + JkdrBLvgaxMHxT() + JkdrBLvgaxMHxT() + wToECHIpwvA() + FIojIXaLuoSx() + LHJJTYPoelboA() + vxhPFGPJLFG() + vxtCvjVlFjeKKuk() + eftSsWnwRJL() + KYsQDWVkg() + LHJJTYPoelboA() + vxhPFGPJLFG() + gAbqGkjWT());
                } catch (Exception e) {
                    Toast.makeText(context, desaTDBTEV() + LHJJTYPoelboA() + LHJJTYPoelboA() + KYsQDWVkg() + LHJJTYPoelboA() + cecBDxEUNX() + FIojIXaLuoSx() + WkucJDrFpstL() + MVMugHkQpVjO() + RSqoNwFX() + aGuqLpXiVWI() + eftSsWnwRJL() + FIojIXaLuoSx() + LHJJTYPoelboA() + vxhPFGPJLFG() + vxtCvjVlFjeKKuk() + eftSsWnwRJL() + KYsQDWVkg() + LHJJTYPoelboA() + vxhPFGPJLFG() + FIojIXaLuoSx() + PrxWxXVvPDVveh() + RSqoNwFX() + eftSsWnwRJL() + vxhPFGPJLFG() + LHJJTYPoelboA() + RSqoNwFX() + MVMugHkQpVjO() + JkdrBLvgaxMHxT() + FIojIXaLuoSx() + gAbqGkjWT() + MVMugHkQpVjO() + eftSsWnwRJL() + MVMugHkQpVjO(), 1).show();
                    Log.e(packageName2, (gAbqGkjWT() + MVMugHkQpVjO() + eftSsWnwRJL() + MVMugHkQpVjO() + hBFvlMYCRRwhRC() + vxtCvjVlFjeKKuk() + MVMugHkQpVjO() + FBOWvhBDBsBYIi() + vxhPFGPJLFG() + cecBDxEUNX() + FIojIXaLuoSx() + QdNjVOMN() + vxhPFGPJLFG() + vxtCvjVlFjeKKuk() + vxtCvjVlFjeKKuk() + MVMugHkQpVjO() + HqUUoxyGX() + vxhPFGPJLFG() + cecBDxEUNX() + FIojIXaLuoSx()) + e.getMessage());
                    e.printStackTrace();
                }
            }
            if (FileExists(listFiles, vxhPFGPJLFG() + mFkonyrgbpVX() + eftSsWnwRJL() + KYsQDWVkg() + fLlQDYIsNMoIxQ() + fLlQDYIsNMoIxQ() + hBFvlMYCRRwhRC() + vxtCvjVlFjeKKuk() + MVMugHkQpVjO() + FBOWvhBDBsBYIi() + vxhPFGPJLFG())) {
                try {
                    Toast.makeText(context, cdPOnUXsttIJim() + vxhPFGPJLFG() + vxtCvjVlFjeKKuk() + eftSsWnwRJL() + KYsQDWVkg() + LHJJTYPoelboA() + PrxWxXVvPDVveh() + RSqoNwFX() + HqUUoxyGX() + FIojIXaLuoSx() + KYsQDWVkg() + fLlQDYIsNMoIxQ() + fLlQDYIsNMoIxQ() + hBFvlMYCRRwhRC() + hBFvlMYCRRwhRC() + hBFvlMYCRRwhRC(), 1).show();
                    Log.i(packageName2, vxhPFGPJLFG() + mFkonyrgbpVX() + eftSsWnwRJL() + KYsQDWVkg() + fLlQDYIsNMoIxQ() + fLlQDYIsNMoIxQ() + hBFvlMYCRRwhRC() + vxtCvjVlFjeKKuk() + MVMugHkQpVjO() + FBOWvhBDBsBYIi() + vxhPFGPJLFG() + cecBDxEUNX() + FIojIXaLuoSx() + cdPOnUXsttIJim() + vxhPFGPJLFG() + vxtCvjVlFjeKKuk() + eftSsWnwRJL() + KYsQDWVkg() + LHJJTYPoelboA() + PrxWxXVvPDVveh() + RSqoNwFX() + HqUUoxyGX() + hBFvlMYCRRwhRC() + hBFvlMYCRRwhRC() + hBFvlMYCRRwhRC());
                    unZipIt(assetManager.open(vxhPFGPJLFG() + mFkonyrgbpVX() + eftSsWnwRJL() + KYsQDWVkg() + fLlQDYIsNMoIxQ() + fLlQDYIsNMoIxQ() + hBFvlMYCRRwhRC() + vxtCvjVlFjeKKuk() + MVMugHkQpVjO() + FBOWvhBDBsBYIi() + vxhPFGPJLFG()), context.getObbDir().getAbsolutePath() + (yGdSVKAJfy()));
                    Log.i(packageName2, vxhPFGPJLFG() + mFkonyrgbpVX() + eftSsWnwRJL() + KYsQDWVkg() + fLlQDYIsNMoIxQ() + fLlQDYIsNMoIxQ() + hBFvlMYCRRwhRC() + vxtCvjVlFjeKKuk() + MVMugHkQpVjO() + FBOWvhBDBsBYIi() + vxhPFGPJLFG() + cecBDxEUNX() + FIojIXaLuoSx() + SBUTnVsAxkkuJyD() + jvNSXaUCTJg() + aSgLIYJHd() + aSgLIYJHd() + vxhPFGPJLFG() + vxtCvjVlFjeKKuk() + vxtCvjVlFjeKKuk() + wlvuiwEjegdlW() + jvNSXaUCTJg() + JkdrBLvgaxMHxT() + JkdrBLvgaxMHxT() + wToECHIpwvA() + FIojIXaLuoSx() + LHJJTYPoelboA() + vxhPFGPJLFG() + vxtCvjVlFjeKKuk() + eftSsWnwRJL() + KYsQDWVkg() + LHJJTYPoelboA() + vxhPFGPJLFG() + gAbqGkjWT());
                } catch (Exception e2) {
                    Toast.makeText(context, desaTDBTEV() + LHJJTYPoelboA() + LHJJTYPoelboA() + KYsQDWVkg() + LHJJTYPoelboA() + cecBDxEUNX() + FIojIXaLuoSx() + WkucJDrFpstL() + MVMugHkQpVjO() + RSqoNwFX() + aGuqLpXiVWI() + eftSsWnwRJL() + FIojIXaLuoSx() + LHJJTYPoelboA() + vxhPFGPJLFG() + vxtCvjVlFjeKKuk() + eftSsWnwRJL() + KYsQDWVkg() + LHJJTYPoelboA() + vxhPFGPJLFG() + FIojIXaLuoSx() + KYsQDWVkg() + fLlQDYIsNMoIxQ() + fLlQDYIsNMoIxQ(), 1).show();
                    Log.e(packageName2, (vxhPFGPJLFG() + mFkonyrgbpVX() + eftSsWnwRJL() + KYsQDWVkg() + fLlQDYIsNMoIxQ() + fLlQDYIsNMoIxQ() + hBFvlMYCRRwhRC() + vxtCvjVlFjeKKuk() + MVMugHkQpVjO() + FBOWvhBDBsBYIi() + vxhPFGPJLFG() + cecBDxEUNX() + FIojIXaLuoSx() + QdNjVOMN() + vxhPFGPJLFG() + vxtCvjVlFjeKKuk() + vxtCvjVlFjeKKuk() + MVMugHkQpVjO() + HqUUoxyGX() + vxhPFGPJLFG() + cecBDxEUNX() + FIojIXaLuoSx()) + e2.getMessage());
                    e2.printStackTrace();
                }
            }
            if (FileExists(listFiles, vxhPFGPJLFG() + mFkonyrgbpVX() + eftSsWnwRJL() + gAbqGkjWT() + MVMugHkQpVjO() + eftSsWnwRJL() + MVMugHkQpVjO() + hBFvlMYCRRwhRC() + vxtCvjVlFjeKKuk() + MVMugHkQpVjO() + FBOWvhBDBsBYIi() + vxhPFGPJLFG())) {
                try {
                    Toast.makeText(context, cdPOnUXsttIJim() + vxhPFGPJLFG() + vxtCvjVlFjeKKuk() + eftSsWnwRJL() + KYsQDWVkg() + LHJJTYPoelboA() + PrxWxXVvPDVveh() + RSqoNwFX() + HqUUoxyGX() + FIojIXaLuoSx() + vxhPFGPJLFG() + mFkonyrgbpVX() + eftSsWnwRJL() + vxhPFGPJLFG() + LHJJTYPoelboA() + RSqoNwFX() + MVMugHkQpVjO() + JkdrBLvgaxMHxT() + FIojIXaLuoSx() + gAbqGkjWT() + MVMugHkQpVjO() + eftSsWnwRJL() + MVMugHkQpVjO() + hBFvlMYCRRwhRC() + hBFvlMYCRRwhRC() + hBFvlMYCRRwhRC(), 1).show();
                    Log.i(packageName2, vxhPFGPJLFG() + mFkonyrgbpVX() + eftSsWnwRJL() + gAbqGkjWT() + MVMugHkQpVjO() + eftSsWnwRJL() + MVMugHkQpVjO() + hBFvlMYCRRwhRC() + vxtCvjVlFjeKKuk() + MVMugHkQpVjO() + FBOWvhBDBsBYIi() + vxhPFGPJLFG() + cecBDxEUNX() + FIojIXaLuoSx() + cdPOnUXsttIJim() + vxhPFGPJLFG() + vxtCvjVlFjeKKuk() + eftSsWnwRJL() + KYsQDWVkg() + LHJJTYPoelboA() + PrxWxXVvPDVveh() + RSqoNwFX() + HqUUoxyGX() + hBFvlMYCRRwhRC() + hBFvlMYCRRwhRC() + hBFvlMYCRRwhRC());
                    unZipIt(assetManager.open(vxhPFGPJLFG() + mFkonyrgbpVX() + eftSsWnwRJL() + gAbqGkjWT() + MVMugHkQpVjO() + eftSsWnwRJL() + MVMugHkQpVjO() + hBFvlMYCRRwhRC() + vxtCvjVlFjeKKuk() + MVMugHkQpVjO() + FBOWvhBDBsBYIi() + vxhPFGPJLFG()), Environment.getExternalStorageDirectory() + (yGdSVKAJfy() + PqjQPGOuV() + RSqoNwFX() + gAbqGkjWT() + LHJJTYPoelboA() + KYsQDWVkg() + PrxWxXVvPDVveh() + gAbqGkjWT() + yGdSVKAJfy() + gAbqGkjWT() + MVMugHkQpVjO() + eftSsWnwRJL() + MVMugHkQpVjO() + yGdSVKAJfy()) + context.getPackageName() + (yGdSVKAJfy()));
                    Log.i(packageName2, vxhPFGPJLFG() + mFkonyrgbpVX() + eftSsWnwRJL() + gAbqGkjWT() + MVMugHkQpVjO() + eftSsWnwRJL() + MVMugHkQpVjO() + hBFvlMYCRRwhRC() + vxtCvjVlFjeKKuk() + MVMugHkQpVjO() + FBOWvhBDBsBYIi() + vxhPFGPJLFG() + cecBDxEUNX() + FIojIXaLuoSx() + SBUTnVsAxkkuJyD() + jvNSXaUCTJg() + aSgLIYJHd() + aSgLIYJHd() + vxhPFGPJLFG() + vxtCvjVlFjeKKuk() + vxtCvjVlFjeKKuk() + wlvuiwEjegdlW() + jvNSXaUCTJg() + JkdrBLvgaxMHxT() + JkdrBLvgaxMHxT() + wToECHIpwvA() + FIojIXaLuoSx() + LHJJTYPoelboA() + vxhPFGPJLFG() + vxtCvjVlFjeKKuk() + eftSsWnwRJL() + KYsQDWVkg() + LHJJTYPoelboA() + vxhPFGPJLFG() + gAbqGkjWT());
                } catch (Exception e3) {
                    Toast.makeText(context, desaTDBTEV() + LHJJTYPoelboA() + LHJJTYPoelboA() + KYsQDWVkg() + LHJJTYPoelboA() + cecBDxEUNX() + FIojIXaLuoSx() + WkucJDrFpstL() + MVMugHkQpVjO() + RSqoNwFX() + aGuqLpXiVWI() + eftSsWnwRJL() + FIojIXaLuoSx() + LHJJTYPoelboA() + vxhPFGPJLFG() + vxtCvjVlFjeKKuk() + eftSsWnwRJL() + KYsQDWVkg() + LHJJTYPoelboA() + vxhPFGPJLFG() + FIojIXaLuoSx() + vxhPFGPJLFG() + mFkonyrgbpVX() + eftSsWnwRJL() + vxhPFGPJLFG() + LHJJTYPoelboA() + RSqoNwFX() + MVMugHkQpVjO() + JkdrBLvgaxMHxT() + FIojIXaLuoSx() + gAbqGkjWT() + MVMugHkQpVjO() + eftSsWnwRJL() + MVMugHkQpVjO() + hBFvlMYCRRwhRC() + hBFvlMYCRRwhRC() + hBFvlMYCRRwhRC(), 1).show();
                    Log.e(packageName2, (vxhPFGPJLFG() + mFkonyrgbpVX() + eftSsWnwRJL() + gAbqGkjWT() + MVMugHkQpVjO() + eftSsWnwRJL() + MVMugHkQpVjO() + hBFvlMYCRRwhRC() + vxtCvjVlFjeKKuk() + MVMugHkQpVjO() + FBOWvhBDBsBYIi() + vxhPFGPJLFG() + cecBDxEUNX() + FIojIXaLuoSx() + QdNjVOMN() + vxhPFGPJLFG() + vxtCvjVlFjeKKuk() + vxtCvjVlFjeKKuk() + MVMugHkQpVjO() + HqUUoxyGX() + vxhPFGPJLFG() + cecBDxEUNX() + FIojIXaLuoSx()) + e3.getMessage());
                    e3.printStackTrace();
                }
            }
            Toast.makeText(context, cdPOnUXsttIJim() + vxhPFGPJLFG() + vxtCvjVlFjeKKuk() + eftSsWnwRJL() + KYsQDWVkg() + LHJJTYPoelboA() + PrxWxXVvPDVveh() + RSqoNwFX() + HqUUoxyGX() + FIojIXaLuoSx() + aSgLIYJHd() + KYsQDWVkg() + bvWweXQSkd() + hAsOvPgvk() + JkdrBLvgaxMHxT() + vxhPFGPJLFG() + eftSsWnwRJL() + vxhPFGPJLFG() + gAbqGkjWT(), 1).show();
            Log.i(packageName2, cdPOnUXsttIJim() + vxhPFGPJLFG() + vxtCvjVlFjeKKuk() + eftSsWnwRJL() + KYsQDWVkg() + LHJJTYPoelboA() + PrxWxXVvPDVveh() + RSqoNwFX() + HqUUoxyGX() + FIojIXaLuoSx() + aSgLIYJHd() + KYsQDWVkg() + bvWweXQSkd() + hAsOvPgvk() + JkdrBLvgaxMHxT() + vxhPFGPJLFG() + eftSsWnwRJL() + vxhPFGPJLFG() + gAbqGkjWT());
        }
    }

    private static void wPdauIdcaW(Context c) {
        String vbHkPgcUWs = vbHkPgcUWs();
    }
}
