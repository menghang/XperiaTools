package me.moonshadow.xperia.tools.fragments;


import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import me.moonshadow.xperia.tools.MainActivity;
import me.moonshadow.xperia.tools.R;
import me.moonshadow.xperia.tools.helpers.RootHelper;
import me.moonshadow.xperia.tools.helpers.ShellHelper;


public class KnockOnSettings extends Fragment {

    private Context context;
    private View rootView;

    public KnockOnSettings(Context context) {
        // Required empty public constructor
        this.context = context;
    }

    public static String restoreBootConfig(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return (settings.getBoolean("boot", false) ? "1" : "0");
    }

    public static String restoreKnockOnConfig(Context context) {
        if (!ShellHelper.fileExists("/sys/devices/virtual/input/clearpad/wakeup_gesture")) {
            return null;
        }
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String value = (settings.getBoolean("knock_on", false) ? "1" : "0");
        ShellHelper.setRootInfo(value, "/sys/devices/virtual/input/clearpad/wakeup_gesture");
        return value;
    }

    public static void changeBootValue(Context context, boolean value) {
        SharedPreferences.Editor settingsEditor
                = PreferenceManager.getDefaultSharedPreferences(context).edit();
        settingsEditor.putBoolean("boot", value);
        settingsEditor.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_knock_on_settings, container, false);
        ImageView imageView1 = (ImageView)rootView.findViewById(R.id.imageView1);
        imageView1.setImageDrawable(getResources().getDrawable(MainActivity.mPictureArray[MainActivity.getRandomPicture()]));
        Switch switchKnockOn = (Switch) rootView.findViewById(R.id.switch_tap_to_wake);
        if ((!RootHelper.isDeviceRooted())
                || (!ShellHelper.fileExists("/sys/devices/virtual/input/clearpad/wakeup_gesture"))) {
            switchKnockOn.setEnabled(false);
        } else {
            switchKnockOn.setChecked(ShellHelper.getInfo("/sys/devices/virtual/input/clearpad/wakeup_gesture").equals("1"));
            switchKnockOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences.Editor settingsEditor
                            = PreferenceManager.getDefaultSharedPreferences(context).edit();
                    settingsEditor.putBoolean("knock_on", isChecked);
                    settingsEditor.commit();
                    String strIsChecked = null;
                    if (isChecked) {
                        strIsChecked = "1";
                    } else {
                        strIsChecked = "0";
                    }
                    ShellHelper.setRootInfo(strIsChecked,
                            "/sys/devices/virtual/input/clearpad/wakeup_gesture");
                }
            });
        }
        Switch switchSetonBoot = (Switch) rootView.findViewById(R.id.switch_set_on_boot);
        switchSetonBoot.setChecked(restoreBootConfig(context).equals("1"));
        switchSetonBoot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeBootValue(context, isChecked);
            }
        });
        return rootView;
    }

}
