package com.distriqt.extension.inappbilling.util;

import android.util.Log;
import java.lang.reflect.Field;

public class Resources {
    public static int getResourceIdByName(String packageName, String className, String name) {
        int id = 0;
        int i = 0;
        while (true) {
            try {
                if (i >= Class.forName(packageName + ".R").getClasses().length) {
                    break;
                } else if (!Class.forName(packageName + ".R").getClasses()[i].getName().split("\\$")[1].equals(className)) {
                    i++;
                } else if (Class.forName(packageName + ".R").getClasses()[i] != null) {
                    id = Class.forName(packageName + ".R").getClasses()[i].getField(name).getInt(Class.forName(packageName + ".R").getClasses()[i]);
                }
            } catch (Exception e) {
                return -1;
            }
        }
        return id;
    }

    public static void listResources(String packageName) {
        int i = 0;
        while (i < Class.forName(packageName + ".R").getClasses().length) {
            try {
                Field[] fields = Class.forName(packageName + ".R").getClasses()[i].getFields();
                for (int j = 0; j < fields.length; j++) {
                    Log.d("Resources", Class.forName(packageName + ".R").getClasses()[i].getName() + "::" + fields[j].getName());
                }
                i++;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
