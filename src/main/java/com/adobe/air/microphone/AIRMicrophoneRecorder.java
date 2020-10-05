package com.adobe.air.microphone;

import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;
import android.os.Process;
import android.util.Log;
import com.adobe.air.AndroidActivityWrapper;
import com.adobe.air.microphone.MobileAEC;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

public class AIRMicrophoneRecorder implements Runnable {
    public static final String[] BLACKLIST = {"Nexus 9", "Nexus 10", "ONE A2005", "D6503"};
    private AcousticEchoCanceler echoCanceler = null;
    private boolean hardwareAEC = false;
    private ByteBuffer mBuffer_toWrite;
    public volatile boolean mKeepAlive = true;
    private byte[] mMicBuffer;
    private int m_audioFormat = 0;
    private int m_audioSource = 0;
    private int m_bufferSize = 0;
    private int m_channelConfiguration = 0;
    public short m_delay = 0;
    public boolean m_isEnhancedMicrophone = false;
    private volatile boolean m_isPaused;
    private volatile boolean m_isRecording;
    private AudioRecord m_recorder;
    private int m_sampleRate = 0;
    private final Object mutex = new Object();
    private NoiseSuppressor noiseSuppressor = null;
    private long t_analyze = 0;
    private long t_capture = 0;
    private long t_process = 0;
    private long t_render = 0;

    public AIRMicrophoneRecorder(int i, int i2, int i3, int i4, int i5) {
        this.m_audioSource = i;
        this.m_sampleRate = i2;
        this.m_channelConfiguration = i3;
        this.m_audioFormat = i4;
        this.m_bufferSize = i5;
        this.mMicBuffer = new byte[this.m_bufferSize];
    }

    public void run() {
        synchronized (this.mutex) {
            while (!isRecording()) {
                try {
                    this.mutex.wait();
                } catch (InterruptedException e) {
                    throw new IllegalStateException("Wait interrupted", e);
                }
            }
        }
        Process.setThreadPriority(-19);
        if (this.m_recorder != null) {
            try {
                if (this.m_isEnhancedMicrophone) {
                    ((AudioManager) AndroidActivityWrapper.GetAndroidActivityWrapper().getActivity().getSystemService("audio")).setMode(3);
                    if (Build.VERSION.SDK_INT >= 16) {
                        int audioSessionId = this.m_recorder.getAudioSessionId();
                        if (!AcousticEchoCanceler.isAvailable() || !NoiseSuppressor.isAvailable()) {
                            this.hardwareAEC = false;
                        } else {
                            this.hardwareAEC = true;
                        }
                        if (this.hardwareAEC) {
                            this.echoCanceler = AcousticEchoCanceler.create(audioSessionId);
                            if (this.echoCanceler != null) {
                                this.echoCanceler.setEnabled(true);
                            }
                            Log.d("AJAudioRecord", "IS Hardware NoiseSuppressor enabled " + NoiseSuppressor.isAvailable());
                            this.noiseSuppressor = NoiseSuppressor.create(audioSessionId);
                            if (this.noiseSuppressor != null) {
                                this.noiseSuppressor.setEnabled(true);
                            }
                        }
                    }
                }
                this.m_recorder.startRecording();
            } catch (IllegalStateException e2) {
            }
        }
    }

    public Boolean Open() {
        int i;
        int i2;
        int minBufferSize = AudioRecord.getMinBufferSize(this.m_sampleRate, this.m_channelConfiguration, this.m_audioFormat);
        if (this.m_isEnhancedMicrophone) {
            if (this.m_bufferSize > minBufferSize) {
                i2 = this.m_bufferSize;
            } else {
                i2 = minBufferSize * 2;
            }
            this.mBuffer_toWrite = ByteBuffer.allocateDirect(this.m_bufferSize);
            i = i2;
        } else if (this.m_bufferSize > minBufferSize) {
            i = this.m_bufferSize;
        } else {
            i = minBufferSize * 2;
        }
        try {
            if (this.m_isEnhancedMicrophone) {
                this.m_recorder = new AudioRecord(7, this.m_sampleRate, this.m_channelConfiguration, this.m_audioFormat, i);
            } else {
                this.m_recorder = new AudioRecord(this.m_audioSource, this.m_sampleRate, this.m_channelConfiguration, this.m_audioFormat, i);
            }
            if (this.m_recorder == null || this.m_recorder.getState() != 1) {
                return false;
            }
            return true;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    public byte[] getBuffer() {
        this.t_capture = System.currentTimeMillis();
        int read = this.m_recorder.read(this.mMicBuffer, 0, this.m_bufferSize);
        if (read != -3 && read == -2) {
        }
        if (this.m_isEnhancedMicrophone && (!this.hardwareAEC || deviceIsBlackListed())) {
            this.t_render = System.currentTimeMillis();
            doAECM();
            this.mMicBuffer = Arrays.copyOf(this.mBuffer_toWrite.array(), this.m_bufferSize);
        }
        return this.mMicBuffer;
    }

    public void setRecording(boolean z) {
        synchronized (this.mutex) {
            this.m_isRecording = z;
            if (this.m_isRecording) {
                this.mutex.notify();
            } else {
                if (this.m_recorder.getState() == 1) {
                    this.m_recorder.stop();
                }
                this.m_recorder.release();
            }
        }
    }

    private void doAECM() {
        try {
            MobileAEC mobileAEC = new MobileAEC((MobileAEC.SamplingFrequency) null);
            mobileAEC.setAecmMode(MobileAEC.AggressiveMode.MOST_AGGRESSIVE).prepare();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.mMicBuffer);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(this.mBuffer_toWrite.capacity());
            byte[] bArr = new byte[320];
            while (byteArrayInputStream.read(bArr, 0, bArr.length) != -1) {
                short[] sArr = new short[160];
                short[] sArr2 = new short[160];
                ByteBuffer.wrap(bArr).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(sArr);
                this.t_analyze = System.currentTimeMillis();
                mobileAEC.farendBuffer(sArr, 160);
                this.t_process = System.currentTimeMillis();
                this.m_delay = (short) ((int) ((this.t_render - this.t_analyze) + (this.t_process - this.t_capture)));
                this.m_delay = 200;
                mobileAEC.echoCancellation(sArr, (short[]) null, sArr2, 160, this.m_delay);
                byte[] bArr2 = new byte[320];
                ByteBuffer.wrap(bArr2).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(sArr2);
                byteArrayOutputStream.write(bArr2, 0, bArr2.length);
            }
            this.mBuffer_toWrite.clear();
            this.mBuffer_toWrite = ByteBuffer.allocateDirect(byteArrayOutputStream.size());
            this.mBuffer_toWrite.put(byteArrayOutputStream.toByteArray());
            byteArrayOutputStream.close();
            byteArrayInputStream.close();
            mobileAEC.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isRecording() {
        boolean z;
        synchronized (this.mutex) {
            z = this.m_isRecording;
        }
        return z;
    }

    public void setIsEnhanced(boolean z) {
        this.m_isEnhancedMicrophone = z;
    }

    public static List<String> getBlackListedModels() {
        return Arrays.asList(BLACKLIST);
    }

    public static boolean deviceIsBlackListed() {
        return Arrays.asList(BLACKLIST).contains(Build.MODEL);
    }
}
