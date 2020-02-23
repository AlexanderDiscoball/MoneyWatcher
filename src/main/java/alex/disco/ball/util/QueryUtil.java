package alex.disco.ball.util;

import alex.disco.ball.entity.Category;

import java.time.LocalDate;

public class QueryUtil {
    private final static String datePicker = "from Product where date BETWEEN '";

    public static String getDatePicker(LocalDate start, LocalDate end, Category selectedCategory) {
        if(selectedCategory == Category.ALL) {
            return datePicker +
                    start.format(DateUtil.getDateFormatterForSql()) +
                    "' AND '" +
                    end.format(DateUtil.getDateFormatterForSql()) + "'";
        }
        return datePicker +
                start.format(DateUtil.getDateFormatterForSql()) +
                "' AND '" +
                end.format(DateUtil.getDateFormatterForSql()) + "'" +
                " AND category = '" + selectedCategory.getName() + "'";
    }
}
