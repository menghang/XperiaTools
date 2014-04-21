package me.moonshadow.xperia.tools.helpers;

import android.util.Log;

import java.io.File;

public class RootHelper {

    private static final String LOG_TAG = RootHelper.class.getName();

    public static boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2();
    }

    public static boolean checkRootMethod1() {
        try {
            File file = new File("/system/app/Superuser.apk");
            return file.exists();
        } catch (Exception e) {
            Log.e(LOG_TAG, "An Error occured while checking for Superuser.apk.", e);
            return false;
        }

    }

    public static boolean checkRootMethod2() {
        try {
            File file = new File("/system/xbin/su");
            return file.exists();
        } catch (Exception e) {
            Log.e(LOG_TAG, "An Error occured while checking for su.", e);
            return false;
        }
    }

}
