package me.moonshadow.xperia.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import me.moonshadow.xperia.tools.fragments.KnockOnSettings;

public class Startup extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent bootintent) {
        if (KnockOnSettings.restoreBootConfig(context).equals("1")) {
            KnockOnSettings.restoreKnockOnConfig(context);
            Toast.makeText(context, R.string.boot_info, Toast.LENGTH_LONG).show();
        }
    }
}
