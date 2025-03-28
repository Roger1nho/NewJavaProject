package Utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ValidationUtils {
    public static boolean isNumeric(String str) {
        return str != null && str.matches("-?\\d+(\\.\\d+)?");
    }

    public static boolean isValidPrice(String price) {
        if (!isNumeric(price)) return false;
        try {
            double value = Double.parseDouble(price);
            return value > 0 && value <= 1000000; // Reasonable upper limit
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isStringValid(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean isValidId(int id) {
        return id > 0;
    }
}