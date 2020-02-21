package alex.disco.ball;

import alex.disco.ball.entity.Category;
import alex.disco.ball.entity.Product;
import alex.disco.ball.util.HibernateUtil;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AddData {

    public static void main(String[] args) {

        HibernateUtil.buildSessionFactory();
        Session session = HibernateUtil.getSessionFactory().openSession();

        Calendar c =  new GregorianCalendar();
        c.set(2020, Calendar.DECEMBER, 30);
        Date date = c.getTime();

        session.beginTransaction();
        session.save(new Product("Лапша", Category.ЕДА,140, LocalDateTime.now().toLocalDate()));

        c.set(2020,Calendar.NOVEMBER,12);
        date = c.getTime();

        session.save(new Product("Обувь", Category.ЕДА,5000, LocalDateTime.now().toLocalDate()));

        session.getTransaction().commit();
        session.close();
    }
}
