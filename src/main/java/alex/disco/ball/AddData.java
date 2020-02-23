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


        session.beginTransaction();
        session.save(new Product("Лапша", Category.FOOD,140, LocalDateTime.now().toLocalDate()));

        session.save(new Product("Обувь", Category.FOOD,5000, LocalDateTime.now().toLocalDate()));

        session.getTransaction().commit();
        session.close();
    }
}
