package utils;

import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.util.prefs.Preferences;

public class ThemeConfiguration {
    private static final Preferences THEME_PREFERENCES = Preferences.userRoot().node("themePreferences");

    public static JMetro getJMetroStyled() {
        JMetro jMetro;
        if (getThemeMode().equals("light"))
            jMetro = new JMetro(Style.LIGHT);
        else
            jMetro = new JMetro(Style.DARK);
        return jMetro;
    }

    public static String getThemeMode() {
        return THEME_PREFERENCES.get("mode", "light");
    }

    public static void setThemeMode(String mode) {
        setThemePreference("mode",mode);
    }

    private static void setThemePreference(String key, String value) {
        THEME_PREFERENCES.put(key, value);
    }


}
