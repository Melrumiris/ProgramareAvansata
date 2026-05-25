package org.com;

import java.util.Locale;
import java.util.ResourceBundle;

public class DisplayLocales {
    public void execute(ResourceBundle messages) {
        System.out.println(messages.getString("locales"));
        Locale[] available = Locale.getAvailableLocales();
        for (Locale locale : available) {
            System.out.println(locale.getDisplayCountry() + "\t" + locale.getDisplayLanguage(locale));
        }
    }
}