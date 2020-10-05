package com.adobe.air;

import android.app.Activity;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FileChooserStub {
    public static final String TAG = FileChooserStub.class.toString();
    /* access modifiers changed from: private */
    public AIRExpandableFileChooser fileChooser;
    private Condition m_condition = this.m_lock.newCondition();
    private ArrayList<String> m_filenames = null;
    /* access modifiers changed from: private */
    public ArrayList<String> m_filterList = new ArrayList<>();
    private Lock m_lock = new ReentrantLock();
    private String m_userAction = null;

    public void show(String str, boolean z, boolean z2) {
        Activity activity;
        AndroidActivityWrapper GetAndroidActivityWrapper = AndroidActivityWrapper.GetAndroidActivityWrapper();
        Activity activity2 = GetAndroidActivityWrapper.getActivity();
        if (activity2 == null) {
            activity = GetAndroidActivityWrapper.WaitForNewActivity();
        } else {
            activity = activity2;
        }
        final boolean z3 = z;
        final boolean z4 = z2;
        final String str2 = str;
        activity.runOnUiThread(new Runnable() {
            public void run() {
                AIRExpandableFileChooser unused = FileChooserStub.this.fileChooser = new AIRExpandableFileChooser(FileChooserStub.this.m_filterList, z3, z4, str2, this);
                FileChooserStub.this.fileChooser.GetDialog().show();
            }
        });
        this.m_lock.lock();
        try {
            if (this.m_userAction == null) {
                this.m_condition.await();
            }
        } catch (InterruptedException e) {
        } finally {
            this.m_lock.unlock();
        }
        this.m_filenames = this.fileChooser.GetFileNames();
        if (this.m_filenames != null) {
            for (int i = 0; i < this.m_filenames.size(); i++) {
            }
        }
    }

    public void addFilter(String str) {
        this.m_filterList.add(str);
    }

    public void SetUserAction(String str) {
        this.m_lock.lock();
        this.m_userAction = str;
        this.m_condition.signal();
        this.m_lock.unlock();
    }

    public int getNumFilenames() {
        return this.m_filenames.size();
    }

    public String getFilename(int i) {
        if (i >= this.m_filenames.size()) {
            return null;
        }
        return this.m_filenames.get(i);
    }

    public boolean userCancelled() {
        if (this.m_userAction == null || this.m_userAction.equals("cancel")) {
            return true;
        }
        return false;
    }
}
