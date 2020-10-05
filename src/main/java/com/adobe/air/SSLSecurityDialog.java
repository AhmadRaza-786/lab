package com.adobe.air;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.net.http.SslCertificate;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.adobe.air.utils.Utils;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SSLSecurityDialog {
    public static final String TAG = SSLSecurityDialog.class.toString();
    private static final String USER_ACTION_TRUST_ALWAYS = "always";
    private static final String USER_ACTION_TRUST_NONE = "none";
    private static final String USER_ACTION_TRUST_SESSION = "session";
    private Condition m_condition = this.m_lock.newCondition();
    private Lock m_lock = new ReentrantLock();
    private String m_useraction = null;

    public String show(String str, byte[] bArr) {
        ShowSSLDialog(str, bArr, (SslCertificate) null, false);
        if (this.m_useraction != null) {
        }
        return this.m_useraction;
    }

    public void ShowSSLDialog(String str, byte[] bArr, SslCertificate sslCertificate, boolean z) {
        Activity activity;
        Certificate certificate;
        AndroidActivityWrapper GetAndroidActivityWrapper = AndroidActivityWrapper.GetAndroidActivityWrapper();
        Activity activity2 = GetAndroidActivityWrapper.getActivity();
        if (activity2 == null) {
            activity = GetAndroidActivityWrapper.WaitForNewActivity();
        } else {
            activity = activity2;
        }
        final AndroidAlertDialog androidAlertDialog = new AndroidAlertDialog(activity);
        AlertDialog.Builder GetAlertDialogBuilder = androidAlertDialog.GetAlertDialogBuilder();
        Context runtimeContext = GetAndroidActivityWrapper.getRuntimeContext();
        LayoutInflater from = LayoutInflater.from(runtimeContext);
        Resources resources = runtimeContext.getResources();
        View GetLayoutViewFromRuntime = Utils.GetLayoutViewFromRuntime("ssl_certificate_warning", resources, from);
        if (GetLayoutViewFromRuntime != null) {
            Resources resources2 = GetLayoutViewFromRuntime.getResources();
            TextView textView = (TextView) Utils.GetWidgetInViewByName("ServerName", resources2, GetLayoutViewFromRuntime);
            textView.setText(textView.getText() + " " + str);
            if (bArr != null) {
                certificate = new Certificate();
                certificate.setCertificate(bArr);
            } else {
                certificate = new Certificate(sslCertificate);
            }
            ((TextView) Utils.GetWidgetInViewByName("CertificateDetails", resources2, GetLayoutViewFromRuntime)).setText(String.format(Utils.GetResourceStringFromRuntime("IDA_CERTIFICATE_DETAILS", resources), new Object[]{certificate.getIssuedToCommonName(), certificate.getIssuedToOrganization(), certificate.getIssuedToOrganizationalUnit(), certificate.getIssuedByCommonName(), certificate.getIssuedByOrganization(), certificate.getIssuedByOrganizationalUnit(), certificate.getIssuedOn(), certificate.getExpiresOn()}), TextView.BufferType.SPANNABLE);
            GetAlertDialogBuilder.setView(GetLayoutViewFromRuntime);
            if (z) {
                GetAlertDialogBuilder.setPositiveButton(Utils.GetResourceStringFromRuntime("IDA_CURL_INTERFACE_ALLSESS", resources), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SSLSecurityDialog.this.SetUserAction(SSLSecurityDialog.USER_ACTION_TRUST_ALWAYS);
                    }
                });
            }
            GetAlertDialogBuilder.setNeutralButton(Utils.GetResourceStringFromRuntime("IDA_CURL_INTERFACE_THISSESS", resources), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    SSLSecurityDialog.this.SetUserAction(SSLSecurityDialog.USER_ACTION_TRUST_SESSION);
                }
            });
            GetAlertDialogBuilder.setNegativeButton(Utils.GetResourceStringFromRuntime("IDA_CURL_INTERFACE_NOSESS", resources), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    SSLSecurityDialog.this.SetUserAction(SSLSecurityDialog.USER_ACTION_TRUST_NONE);
                }
            });
            GetAlertDialogBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    if (keyEvent.getKeyCode() != 4) {
                        return false;
                    }
                    SSLSecurityDialog.this.SetUserAction(SSLSecurityDialog.USER_ACTION_TRUST_NONE);
                    return false;
                }
            });
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    androidAlertDialog.show();
                }
            });
            this.m_lock.lock();
            try {
                if (this.m_useraction == null) {
                    this.m_condition.await();
                }
            } catch (InterruptedException e) {
            } finally {
                this.m_lock.unlock();
            }
        }
    }

    /* access modifiers changed from: private */
    public void SetUserAction(String str) {
        this.m_lock.lock();
        this.m_useraction = str;
        this.m_condition.signal();
        this.m_lock.unlock();
    }

    public String show(String str, SslCertificate sslCertificate) {
        ShowSSLDialog(str, (byte[]) null, sslCertificate, false);
        if (this.m_useraction != null) {
        }
        return this.m_useraction;
    }

    public String getUserAction() {
        return this.m_useraction;
    }
}
