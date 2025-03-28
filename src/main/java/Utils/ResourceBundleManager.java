package Utils;

import java.util.ResourceBundle;

public class ResourceBundleManager {
    private static ResourceBundle bundle;

    static {
        try {
            bundle = ResourceBundle.getBundle("messages");
        } catch (Exception e) {
            bundle = ResourceBundle.getBundle("messages_en");
        }
    }

    public static String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return key;
        }
    }

    public static void setLocale(java.util.Locale locale) {
        bundle = ResourceBundle.getBundle("messages", locale);
    }
}