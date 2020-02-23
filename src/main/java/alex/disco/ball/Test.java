package alex.disco.ball;

import alex.disco.ball.entity.Category;
import alex.disco.ball.entity.Product;
import alex.disco.ball.util.DateUtil;
import alex.disco.ball.util.HibernateUtil;
import javafx.fxml.FXMLLoader;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Test {
    public static void main(String[] args) {
        System.out.println(Category.ALL.toString());
        System.out.println(Arrays.asList(Category.values()));
    }
}
