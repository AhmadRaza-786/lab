package com.adobe.air.microphone;

public class AIRMicrophone {
    public static boolean m_isEnhanced = false;
    private AIRMicrophoneRecorder m_airRecorder;
    private int m_audioFormat = 0;
    private int m_audioSource = 0;
    private int m_bufferSize = 0;
    private int m_channelConfiguration = 0;
    private int m_sampleRate = 0;
    private Thread m_thread;
    private boolean m_useEnhancedMic = false;

    public boolean Open(int i, int i2, int i3, int i4) {
        this.m_audioSource = 1;
        this.m_sampleRate = i;
        if (i3 == 8) {
            this.m_audioFormat = 3;
        } else if (i3 != 16) {
            return false;
        } else {
            this.m_audioFormat = 2;
        }
        if (i2 == 1) {
            this.m_channelConfiguration = 16;
        } else if (i2 != 2) {
            return false;
        } else {
            this.m_channelConfiguration = 12;
        }
        this.m_bufferSize = i4;
        this.m_airRecorder = new AIRMicrophoneRecorder(this.m_audioSource, this.m_sampleRate, this.m_channelConfiguration, this.m_audioFormat, this.m_bufferSize);
        this.m_airRecorder.setIsEnhanced(this.m_useEnhancedMic);
        this.m_thread = new Thread(this.m_airRecorder);
        this.m_thread.start();
        if (!this.m_airRecorder.Open().booleanValue()) {
            return false;
        }
        this.m_airRecorder.setRecording(true);
        return true;
    }

    public void Close() {
        this.m_airRecorder.setRecording(false);
    }

    public byte[] GetNextBuffer() {
        return this.m_airRecorder.getBuffer();
    }

    public void setEnhanced(boolean z) {
        this.m_useEnhancedMic = z;
        m_isEnhanced = z;
    }
}
