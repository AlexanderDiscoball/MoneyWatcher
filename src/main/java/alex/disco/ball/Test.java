package alex.disco.ball;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Test {
    public static void main(String[] args) {
        Calendar c =  new GregorianCalendar();
        c.set(2020, Calendar.DECEMBER, 30);
        Date date = c.getTime();
        System.out.println(date);
        System.out.println(new Date(date.toString()));
    }
}
