package alex.disco.ball.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Objects;

public class DateUtil {


    private static final String DATE_PATTERN = "dd.MM.yyyy";
    private static final String DATE_PATTERN_FOR_SQL = "yyyy-MM-dd";


    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(DATE_PATTERN);

    private static final DateTimeFormatter DATE_FORMATTER_FOR_SQL =
            DateTimeFormatter.ofPattern(DATE_PATTERN_FOR_SQL);

    public static DateTimeFormatter getDateFormatter() {
        return DATE_FORMATTER;
    }

    public static DateTimeFormatter getDateFormatterForSql() {
        return DATE_FORMATTER_FOR_SQL;
    }

    public static String format(LocalDate date) {
        if (date == null) {
            return null;
        }
        return DATE_FORMATTER.format(date);
    }


    public static LocalDate parse(String dateString) {
        try {
            return DATE_FORMATTER.parse(dateString, LocalDate::from);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static Date formatToDate(LocalDate localDate){
        return java.sql.Date.valueOf(localDate);
    }
    public static Date formatToDate(String localDate){
        return java.sql.Date.valueOf(Objects.requireNonNull(parse(localDate)));
    }

    public static boolean validDate(String dateString) {
        return DateUtil.parse(dateString) != null;
    }
}
