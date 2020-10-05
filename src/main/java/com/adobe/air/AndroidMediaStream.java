package com.adobe.air;

import android.net.Uri;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AndroidMediaStream {
    private static final int ERROR = -2;
    private int mBufferSize = 0;
    private BufferedInputStream mBufferedStream = null;
    private Uri mContentURI = null;

    public boolean loadContentURI(String str) {
        this.mContentURI = Uri.parse(str);
        if (this.mBufferedStream == null) {
            try {
                this.mBufferedStream = new BufferedInputStream(AndroidActivityWrapper.GetAndroidActivityWrapper().getActivity().getContentResolver().openInputStream(this.mContentURI));
            } catch (FileNotFoundException | Exception e) {
                return false;
            }
        }
        return true;
    }

    public int Read(byte[] bArr, int i) {
        try {
            return this.mBufferedStream.read(bArr, 0, i);
        } catch (IOException e) {
            return ERROR;
        }
    }

    public void Close() {
        if (this.mBufferedStream != null) {
            try {
                this.mBufferedStream.close();
            } catch (Exception e) {
            }
            this.mBufferedStream = null;
        }
    }
}
