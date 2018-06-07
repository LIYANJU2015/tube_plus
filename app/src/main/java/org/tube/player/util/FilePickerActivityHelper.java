package org.tube.player.util;

import android.os.Bundle;

public class FilePickerActivityHelper extends com.nononsenseapps.filepicker.FilePickerActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.setTheme(org.tube.player.R.style.FilePickerThemeLight);
        super.onCreate(savedInstanceState);
    }
}
