package org.tube.player.settings;

import android.os.Bundle;

import org.tube.player.App;
import org.tube.player.R;

public class MainSettingsFragment extends BasePreferenceFragment {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        if (App.isSuper()) {
            addPreferencesFromResource(R.xml.main_settings);
        } else {
            addPreferencesFromResource(R.xml.main_settings2);
        }
    }
}
