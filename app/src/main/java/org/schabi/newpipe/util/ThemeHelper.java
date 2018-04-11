package org.schabi.newpipe.util;

import android.content.Context;
import android.preference.PreferenceManager;

import org.schabi.newpipe.R;

public class ThemeHelper {

    /**
     * Apply the selected theme (on NewPipe settings) in the context
     *
     * @param context context that the theme will be applied
     */
    public static void setTheme(Context context) {
        context.setTheme(R.style.LightTheme);
    }

    /**
     * Return true if the selected theme (on NewPipe settings) is the Light theme
     *
     * @param context context to get the preference
     */
    public static boolean isLightThemeSelected(Context context) {
        return getSelectedTheme(context).equals(context.getResources().getString(R.string.light_theme_key));
    }

    public static String getSelectedTheme(Context context) {
        String themeKey = context.getString(R.string.theme_key);
        String defaultTheme = context.getResources().getString(R.string.default_theme_value);
        return PreferenceManager.getDefaultSharedPreferences(context).getString(themeKey, defaultTheme);
    }
}
