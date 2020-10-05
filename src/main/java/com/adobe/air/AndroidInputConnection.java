package com.adobe.air;

import android.text.Editable;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputMethodManager;
import com.adobe.air.utils.AIRLogger;
import com.distriqt.extension.inappbilling.BuildConfig;

public class AndroidInputConnection extends BaseInputConnection {
    private static final String LOG_TAG = "AndroidInputConnection";
    private CharSequence mComposedText = null;
    private ExtractedTextRequest mExtractRequest = null;
    private ExtractedText mExtractedText = null;
    private final AIRWindowSurfaceView mWindowSurfaceView;

    private native String nativeGetTextAfterCursor(int i);

    private native String nativeGetTextBeforeCursor(int i);

    private native int nativeGetTextBoxMaxChars();

    private native void nativeSetSelection(int i, int i2);

    public AndroidInputConnection(AIRWindowSurfaceView aIRWindowSurfaceView) {
        super(aIRWindowSurfaceView, true);
        this.mWindowSurfaceView = aIRWindowSurfaceView;
    }

    public boolean deleteSurroundingText(int i, int i2) {
        for (int i3 = 0; i3 < i2; i3++) {
            AIRLogger.d(LOG_TAG, "[JP] deleteSurroundingText ");
            this.mWindowSurfaceView.nativeOnKeyListener(0, 22, 0, false, false, false);
            this.mWindowSurfaceView.nativeOnKeyListener(1, 22, 0, false, false, false);
        }
        int i4 = i2 + i;
        for (int i5 = 0; i5 < i4; i5++) {
            AIRLogger.d(LOG_TAG, "[JP] deleteSurroundingText 2 ");
            this.mWindowSurfaceView.nativeOnKeyListener(0, 67, 0, false, false, false);
            this.mWindowSurfaceView.nativeOnKeyListener(1, 67, 0, false, false, false);
        }
        return true;
    }

    public CharSequence getTextAfterCursor(int i, int i2) {
        return nativeGetTextAfterCursor(i);
    }

    public CharSequence getTextBeforeCursor(int i, int i2) {
        return nativeGetTextBeforeCursor(i);
    }

    public ExtractedText getExtractedText(ExtractedTextRequest extractedTextRequest, int i) {
        this.mWindowSurfaceView.setInputConnection(this);
        ExtractedText nativeGetTextContent = this.mWindowSurfaceView.nativeGetTextContent();
        if (nativeGetTextContent == null || nativeGetTextContent.text == null) {
            return null;
        }
        nativeGetTextContent.partialEndOffset = -1;
        nativeGetTextContent.partialStartOffset = -1;
        if ((i & 1) == 0) {
            return nativeGetTextContent;
        }
        nativeGetTextContent.startOffset = 0;
        this.mExtractedText = nativeGetTextContent;
        this.mExtractRequest = extractedTextRequest;
        this.mWindowSurfaceView.nativeShowOriginalRect();
        return nativeGetTextContent;
    }

    public Editable getEditable() {
        return null;
    }

    public boolean performEditorAction(int i) {
        if (i != 6) {
            return false;
        }
        this.mWindowSurfaceView.showSoftKeyboard(false);
        this.mWindowSurfaceView.nativeDispatchUserTriggeredSkDeactivateEvent();
        return true;
    }

    public boolean performContextMenuAction(int i) {
        int i2 = 0;
        switch (i) {
            case 16908319:
                break;
            case 16908320:
                if (!this.mWindowSurfaceView.nativeIsTextSelected()) {
                    i2 = 2;
                    break;
                } else {
                    i2 = 1;
                    break;
                }
            case 16908321:
                if (!this.mWindowSurfaceView.nativeIsTextSelected()) {
                    i2 = 4;
                    break;
                } else {
                    i2 = 3;
                    break;
                }
            case 16908322:
                i2 = 5;
                break;
            case 16908324:
                i2 = 6;
                break;
            case 16908328:
                i2 = 7;
                break;
            case 16908329:
                i2 = 8;
                break;
            default:
                return false;
        }
        return this.mWindowSurfaceView.onTextBoxContextMenuItem(i2);
    }

    public boolean setComposingRegion(int i, int i2) {
        if (i <= i2) {
            int i3 = i2;
            i2 = i;
            i = i3;
        }
        ExtractedText nativeGetTextContent = this.mWindowSurfaceView.nativeGetTextContent();
        if (i > nativeGetTextContent.text.length() || i2 < 0) {
            String charSequence = nativeGetTextContent.text.toString();
            this.mComposedText = charSequence.substring(charSequence.lastIndexOf(32) + 1);
            return true;
        }
        this.mComposedText = nativeGetTextContent.text.subSequence(i2, i);
        return true;
    }

    public boolean setComposingText(CharSequence charSequence, int i) {
        int i2;
        int nativeGetTextBoxMaxChars = nativeGetTextBoxMaxChars();
        if (nativeGetTextBoxMaxChars != 0 && charSequence.length() > 0) {
            ExtractedText nativeGetTextContent = this.mWindowSurfaceView.nativeGetTextContent();
            int length = ((nativeGetTextBoxMaxChars - nativeGetTextContent.text.length()) + nativeGetTextContent.selectionEnd) - nativeGetTextContent.selectionStart;
            if (this.mComposedText != null) {
                i2 = this.mComposedText.length();
            } else {
                i2 = 0;
            }
            int min = Math.min(i2 + length, charSequence.length());
            if (min > 0) {
                charSequence = charSequence.subSequence(0, min);
            } else {
                charSequence = null;
            }
        }
        if (charSequence != null) {
            AIRLogger.d(LOG_TAG, "[JP] setComposingText " + charSequence);
            writeText(charSequence);
            this.mComposedText = charSequence;
            if (i <= 0) {
                int length2 = charSequence.length() + Math.abs(i);
                for (int i3 = 0; i3 < length2; i3++) {
                    AIRLogger.d(LOG_TAG, "[JP] setComposingText " + charSequence);
                    this.mWindowSurfaceView.nativeOnKeyListener(0, 21, 0, false, false, false);
                    this.mWindowSurfaceView.nativeOnKeyListener(1, 21, 0, false, false, false);
                }
            } else if (i > 1) {
                int i4 = i - 1;
                for (int i5 = 0; i5 < i4; i5++) {
                    AIRLogger.d(LOG_TAG, "[JP] setComposingText 2 " + charSequence);
                    this.mWindowSurfaceView.nativeOnKeyListener(0, 22, 0, false, false, false);
                    this.mWindowSurfaceView.nativeOnKeyListener(1, 22, 0, false, false, false);
                }
            }
        }
        return true;
    }

    public boolean finishComposingText() {
        this.mComposedText = null;
        if (!this.mWindowSurfaceView.getIsFullScreen() || this.mWindowSurfaceView.IsSurfaceChangedForSoftKeyboard()) {
            return true;
        }
        this.mWindowSurfaceView.nativeShowOriginalRect();
        return true;
    }

    public boolean setSelection(int i, int i2) {
        nativeSetSelection(i, i2);
        return true;
    }

    public boolean commitText(CharSequence charSequence, int i) {
        AIRLogger.d(LOG_TAG, "[JP] setComposingText " + charSequence);
        writeText(charSequence);
        this.mComposedText = null;
        return true;
    }

    private void writeText(CharSequence charSequence) {
        int i;
        int length = charSequence.length();
        if (this.mComposedText != null) {
            int length2 = this.mComposedText.length();
            int min = Math.min(length, length2);
            i = 0;
            while (i < min && charSequence.charAt(i) == this.mComposedText.charAt(i)) {
                i++;
            }
            for (int i2 = i; i2 < length2; i2++) {
                AIRLogger.d(LOG_TAG, "[JP] writeText " + charSequence);
                this.mWindowSurfaceView.nativeOnKeyListener(0, 67, 0, false, false, false);
                this.mWindowSurfaceView.nativeOnKeyListener(1, 67, 0, false, false, false);
            }
        } else {
            i = 0;
        }
        while (i < length) {
            AIRLogger.d(LOG_TAG, "[JP] writeText 2 " + charSequence);
            this.mWindowSurfaceView.nativeOnKeyListener(0, 0, charSequence.charAt(i), false, false, false);
            this.mWindowSurfaceView.nativeOnKeyListener(1, 0, charSequence.charAt(i), false, false, false);
            i++;
        }
        updateIMEText();
    }

    public void updateIMEText() {
        InputMethodManager inputMethodManager;
        if (this.mExtractRequest != null && (inputMethodManager = this.mWindowSurfaceView.getInputMethodManager()) != null && inputMethodManager.isActive(this.mWindowSurfaceView)) {
            ExtractedText nativeGetTextContent = this.mWindowSurfaceView.nativeGetTextContent();
            boolean equals = nativeGetTextContent.text.toString().equals(this.mExtractedText.text.toString());
            if (!equals || nativeGetTextContent.selectionStart != this.mExtractedText.selectionStart || nativeGetTextContent.selectionEnd != this.mExtractedText.selectionEnd || nativeGetTextContent.flags != this.mExtractedText.flags) {
                nativeGetTextContent.startOffset = 0;
                if (equals) {
                    nativeGetTextContent.partialStartOffset = 0;
                    nativeGetTextContent.partialEndOffset = 0;
                    nativeGetTextContent.text = BuildConfig.FLAVOR;
                    this.mExtractedText.flags = nativeGetTextContent.flags;
                    this.mExtractedText.selectionStart = nativeGetTextContent.selectionStart;
                    this.mExtractedText.selectionEnd = nativeGetTextContent.selectionEnd;
                } else {
                    nativeGetTextContent.partialStartOffset = -1;
                    nativeGetTextContent.partialEndOffset = -1;
                    this.mExtractedText = nativeGetTextContent;
                }
                inputMethodManager.updateExtractedText(this.mWindowSurfaceView, this.mExtractRequest.token, nativeGetTextContent);
            }
        }
    }

    public void Reset() {
        this.mComposedText = null;
        this.mExtractRequest = null;
        this.mExtractedText = null;
    }
}
