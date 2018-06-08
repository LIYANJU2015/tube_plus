package org.tube.player.referrer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.leiting.mission.util.ReferVersions;

/**
 * Created by liyanju on 2018/6/8.
 */

public class FacebookInstallBroadcastReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            ReferVersions.onHandleIntent(context, intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
