package utils;

import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.util.prefs.Preferences;

public class ThemeConfiguration {
    private static final Preferences THEME_PREFERENCES = Preferences.userRoot().node("themePreferences");

    /**
     * get saved jMetro style
     * @return JMetro style
     */
    public static JMetro getJMetroStyled() {
        JMetro jMetro;
        if (getThemeMode().equals("light"))
            jMetro = new JMetro(Style.LIGHT);
        else
            jMetro = new JMetro(Style.DARK);
        return jMetro;
    }

    /**
     * get theme mode
     * @return theme mode
     */
    public static String getThemeMode() {
        return THEME_PREFERENCES.get("mode", "light");
    }

    /**
     * set theme mode
     * @param mode mode
     */
    public static void setThemeMode(String mode) {
        setThemePreference("mode",mode);
    }

    /**
     * save preference
     * @param key name of preference
     * @param value value
     */
    private static void setThemePreference(String key, String value) {
        THEME_PREFERENCES.put(key, value);
    }


}
