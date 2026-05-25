package org.com;

import java.text.DateFormatSymbols;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Currency;
import java.util.Locale;
import java.util.ResourceBundle;

public class Info {
    public void execute(String languageTag, ResourceBundle messages) {
        Locale loc = (languageTag != null) ? Locale.forLanguageTag(languageTag) : Locale.getDefault();

        String pattern = messages.getString("info");
        System.out.println(MessageFormat.format(pattern, loc.toString()));

        System.out.println("Country: " + loc.getDisplayCountry(loc) + " (" + loc.getDisplayCountry() + ")");
        System.out.println("Language: " + loc.getDisplayLanguage(loc) + " (" + loc.getDisplayLanguage() + ")");

        try {
            Currency currency = Currency.getInstance(loc);
            System.out.println("Currency: " + currency.getCurrencyCode() + " (" + currency.getDisplayName(loc) + ")");
        } catch (IllegalArgumentException e) {
            System.out.println("Currency: Not applicable for this locale");
        }

        DateFormatSymbols dfs = DateFormatSymbols.getInstance(loc);
        String[] weekDays = dfs.getWeekdays();
        System.out.print("Week Days: ");
        System.out.println(String.join(", ", Arrays.copyOfRange(weekDays, 1, 8)));

        String[] months = dfs.getMonths();
        System.out.print("Months: ");
        System.out.println(String.join(", ", Arrays.copyOfRange(months, 0, 12)));

        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(loc);
        System.out.println("Today: " + LocalDateTime.now().format(formatter));
    }
}