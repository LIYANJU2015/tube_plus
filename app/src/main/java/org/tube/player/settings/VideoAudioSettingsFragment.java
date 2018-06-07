package org.tube.player.settings;

import android.os.Bundle;

public class VideoAudioSettingsFragment extends BasePreferenceFragment {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(org.tube.player.R.xml.video_audio_settings);
    }
}
