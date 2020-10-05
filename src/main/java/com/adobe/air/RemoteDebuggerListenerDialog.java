package com.adobe.air;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.appcompat.R;
import android.view.KeyEvent;
import java.net.InetAddress;
import java.net.Socket;

public class RemoteDebuggerListenerDialog extends Activity {
    private final String LOG_TAG = getClass().getName();
    /* access modifiers changed from: private */
    public int count = 0;
    /* access modifiers changed from: private */
    public int debuggerPort = -1;
    private Activity mActivity = null;
    /* access modifiers changed from: private */
    public Runnable mCheckAgain = null;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler();
    /* access modifiers changed from: private */
    public BroadcastReceiver mReceiver;
    /* access modifiers changed from: private */
    public AlertDialog mWaitDialog = null;

    private enum DialogState {
        StateRuntimeNotReady,
        StateRuntimeWaitingForDebugger,
        StateRuntimeTimedOut
    }

    static /* synthetic */ int access$608(RemoteDebuggerListenerDialog remoteDebuggerListenerDialog) {
        int i = remoteDebuggerListenerDialog.count;
        remoteDebuggerListenerDialog.count = i + 1;
        return i;
    }

    public void onCreate(Bundle bundle) {
        final String string = getString(R.string.IDA_APP_WAITING_DEBUGGER_WARNING);
        final String string2 = getString(R.string.IDA_APP_DEBUGGER_TIMEOUT_INFO);
        this.mActivity = this;
        super.onCreate(bundle);
        Bundle extras = getIntent().getExtras();
        this.debuggerPort = extras != null ? extras.getInt("debuggerPort") : 7936;
        this.mWaitDialog = new AlertDialog.Builder(this).create();
        String format = String.format(string, new Object[]{60});
        this.mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (!isInitialStickyBroadcast()) {
                    Bundle extras = RemoteDebuggerListenerDialog.this.getIntent().getExtras();
                    if ((extras != null ? extras.getInt("debuggerPort") : 7936) == RemoteDebuggerListenerDialog.this.debuggerPort) {
                        RemoteDebuggerListenerDialog.this.dismissDialog();
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN");
        intentFilter.addCategory("RemoteDebuggerListenerDialogClose");
        registerReceiver(this.mReceiver, intentFilter);
        this.mWaitDialog = createDialog(getString(R.string.IDA_APP_WAITING_DEBUGGER_TITLE), format, getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                RemoteDebuggerListenerDialog.this.mHandler.removeCallbacks(RemoteDebuggerListenerDialog.this.mCheckAgain);
                RemoteDebuggerListenerDialog.this.closeListeningDebuggerSocket();
                RemoteDebuggerListenerDialog.this.unregisterReceiver(RemoteDebuggerListenerDialog.this.mReceiver);
                BroadcastReceiver unused = RemoteDebuggerListenerDialog.this.mReceiver = null;
                dialogInterface.cancel();
                RemoteDebuggerListenerDialog.this.finish();
            }
        }, new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i != 4) {
                    return false;
                }
                RemoteDebuggerListenerDialog.this.mHandler.removeCallbacks(RemoteDebuggerListenerDialog.this.mCheckAgain);
                RemoteDebuggerListenerDialog.this.closeListeningDebuggerSocket();
                RemoteDebuggerListenerDialog.this.unregisterReceiver(RemoteDebuggerListenerDialog.this.mReceiver);
                BroadcastReceiver unused = RemoteDebuggerListenerDialog.this.mReceiver = null;
                dialogInterface.cancel();
                RemoteDebuggerListenerDialog.this.finish();
                return false;
            }
        });
        this.count = 0;
        this.mCheckAgain = new Runnable() {
            public void run() {
                if (RemoteDebuggerListenerDialog.this.count < 60) {
                    String format = String.format(string, new Object[]{Integer.valueOf(60 - RemoteDebuggerListenerDialog.this.count)});
                    RemoteDebuggerListenerDialog.access$608(RemoteDebuggerListenerDialog.this);
                    RemoteDebuggerListenerDialog.this.mWaitDialog.setMessage(format);
                    RemoteDebuggerListenerDialog.this.mHandler.postDelayed(this, 1000);
                    return;
                }
                RemoteDebuggerListenerDialog.this.mHandler.removeCallbacks(this);
                RemoteDebuggerListenerDialog.this.mWaitDialog.cancel();
                if (RemoteDebuggerListenerDialog.this.mReceiver != null) {
                    RemoteDebuggerListenerDialog.this.unregisterReceiver(RemoteDebuggerListenerDialog.this.mReceiver);
                    BroadcastReceiver unused = RemoteDebuggerListenerDialog.this.mReceiver = null;
                }
                final AnonymousClass1 r4 = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        RemoteDebuggerListenerDialog.this.closeListeningDebuggerSocket();
                        dialogInterface.cancel();
                        RemoteDebuggerListenerDialog.this.finish();
                    }
                };
                AlertDialog unused2 = RemoteDebuggerListenerDialog.this.mWaitDialog = RemoteDebuggerListenerDialog.this.createDialog(AndroidConstants.ADOBE_AIR, string2, RemoteDebuggerListenerDialog.this.getString(R.string.button_continue), r4, new DialogInterface.OnKeyListener() {
                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                        if (i != 4) {
                            return false;
                        }
                        r4.onClick(dialogInterface, -1);
                        return false;
                    }
                });
                RemoteDebuggerListenerDialog.this.mWaitDialog.show();
            }
        };
        this.mHandler.postDelayed(this.mCheckAgain, 1000);
        this.mWaitDialog.show();
    }

    /* access modifiers changed from: private */
    public AlertDialog createDialog(CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, DialogInterface.OnClickListener onClickListener, DialogInterface.OnKeyListener onKeyListener) {
        AlertDialog create = new AlertDialog.Builder(this.mActivity).create();
        create.setTitle(charSequence);
        create.setMessage(charSequence2);
        create.setButton(-1, charSequence3, onClickListener);
        create.setOnKeyListener(onKeyListener);
        create.setCancelable(true);
        return create;
    }

    /* access modifiers changed from: private */
    public void closeListeningDebuggerSocket() {
        new AsyncTask<Integer, Integer, Integer>() {
            /* access modifiers changed from: protected */
            public Integer doInBackground(Integer... numArr) {
                try {
                    new Socket(InetAddress.getLocalHost(), numArr[0].intValue()).close();
                } catch (Exception e) {
                }
                return 0;
            }
        }.execute(new Integer[]{Integer.valueOf(this.debuggerPort)});
    }

    /* access modifiers changed from: private */
    public void dismissDialog() {
        if (this.mWaitDialog != null) {
            this.mWaitDialog.cancel();
        }
        if (this.mReceiver != null) {
            unregisterReceiver(this.mReceiver);
        }
        this.mReceiver = null;
        this.mHandler.removeCallbacks(this.mCheckAgain);
        this.mActivity.finish();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            closeListeningDebuggerSocket();
            dismissDialog();
        }
        return super.onKeyDown(i, keyEvent);
    }

    public void onStop() {
        closeListeningDebuggerSocket();
        dismissDialog();
        super.onStop();
    }
}
