package org.tube.player;

import android.content.Intent;
import android.widget.Toast;

import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.tube.player.player.PopupVideoPlayer;
import org.tube.player.util.Constants;
import org.tube.player.util.PermissionHelper;

/**
 * Get the url from the intent and open a popup player
 */
public class RouterPopupActivity extends RouterActivity {

    @Override
    protected void handleUrl(String url) {
        if (!PermissionHelper.isPopupEnabled(this)) {
            PermissionHelper.showPopupEnablementToast(this);
            finish();
            return;
        }
        StreamingService service;
        try {
            service = NewPipe.getServiceByUrl(url);
        } catch (ExtractionException e) {
            Toast.makeText(this, org.tube.player.R.string.url_not_supported_toast, Toast.LENGTH_LONG).show();
            return;
        }

        Intent callIntent = new Intent(this, PopupVideoPlayer.class);
        switch (service.getLinkTypeByUrl(url)) {
            case STREAM:
                break;
            default:
                Toast.makeText(this, org.tube.player.R.string.url_not_supported_toast, Toast.LENGTH_LONG).show();
                return;
        }

        callIntent.putExtra(Constants.KEY_URL, url);
        callIntent.putExtra(Constants.KEY_SERVICE_ID, service.getServiceId());
        startService(callIntent);

        finish();
    }
}