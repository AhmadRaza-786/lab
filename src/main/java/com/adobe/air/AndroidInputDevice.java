package com.adobe.air;

import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.List;

/* compiled from: AndroidInputManager */
class AndroidInputDevice {
    private InputDevice mDevice = null;
    private List<AndroidInputControl> mInputControls = null;
    private int mNumControls = 0;

    public AndroidInputDevice(InputDevice inputDevice) {
        this.mDevice = inputDevice;
        this.mInputControls = new ArrayList();
        for (InputDevice.MotionRange next : inputDevice.getMotionRanges()) {
            if ((next.getSource() & 16) != 0) {
                this.mInputControls.add(new AndroidInputControl(ControlType.AXIS, next.getAxis(), next.getMin(), next.getMax()));
                this.mNumControls++;
            }
        }
        for (int i = 0; i < KeyEvent.getMaxKeyCode(); i++) {
            if (KeyCharacterMap.deviceHasKey(i) && isGameKey(i)) {
                this.mInputControls.add(new AndroidInputControl(ControlType.BUTTON, i, 0.0f, 1.0f));
                this.mNumControls++;
            }
        }
    }

    public String getName() {
        return this.mDevice.getName();
    }

    public String getUniqueId() {
        return this.mDevice.getDescriptor();
    }

    public int getNumControls() {
        return this.mNumControls;
    }

    public AndroidInputControl getInputControl(int i) {
        return this.mInputControls.get(i);
    }

    private static boolean isGameKey(int i) {
        switch (i) {
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
                return true;
            default:
                return KeyEvent.isGamepadButton(i);
        }
    }

    public boolean onKeyEvent(KeyEvent keyEvent) {
        for (int i = 0; i < this.mInputControls.size(); i++) {
            AndroidInputControl androidInputControl = this.mInputControls.get(i);
            if (androidInputControl.getType() == ControlType.BUTTON.ordinal() && androidInputControl.getCode() == keyEvent.getKeyCode()) {
                switch (keyEvent.getAction()) {
                    case 0:
                        androidInputControl.setData(1.0f);
                        return true;
                    case 1:
                        androidInputControl.setData(0.0f);
                        return true;
                }
            }
        }
        return false;
    }

    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= this.mInputControls.size()) {
                return true;
            }
            AndroidInputControl androidInputControl = this.mInputControls.get(i2);
            if (androidInputControl.getType() == ControlType.AXIS.ordinal()) {
                androidInputControl.setData(motionEvent.getAxisValue(androidInputControl.getCode()));
            }
            i = i2 + 1;
        }
    }
}
