package org.tube.player.settings;

import android.os.Bundle;

public class HistorySettingsFragment extends BasePreferenceFragment {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(org.tube.player.R.xml.history_settings);
    }
}
