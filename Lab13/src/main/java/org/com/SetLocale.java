package org.com;

import java.util.Locale;
import java.util.ResourceBundle;
import java.text.MessageFormat;

public class SetLocale {
    public void execute(String languageTag, ResourceBundle messages) {
        Locale newLocale = Locale.forLanguageTag(languageTag);
        Locale.setDefault(newLocale);

        String pattern = messages.getString("locale.set");
        String output = MessageFormat.format(pattern, newLocale.toString());
        System.out.println(output);
    }
}