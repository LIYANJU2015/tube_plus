package org.schabi.newpipe.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import us.shandian.giga.util.ReferVersions;

/**
 * Created by liyanju on 2018/4/11.
 */

public class MultipleInstallBroadcastReceiver extends BroadcastReceiver {

    private ReferVersions.MultipleReferrerReceiverHandler receiverHandler =
            ReferVersions.createInstallReferrerReceiverHandler();

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            receiverHandler.onHandleIntent(context, intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
