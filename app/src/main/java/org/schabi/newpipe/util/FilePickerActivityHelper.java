package org.schabi.newpipe.util;

import android.os.Bundle;
import org.schabi.newpipe.R;

public class FilePickerActivityHelper extends com.nononsenseapps.filepicker.FilePickerActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.setTheme(R.style.FilePickerThemeLight);
        super.onCreate(savedInstanceState);
    }
}
