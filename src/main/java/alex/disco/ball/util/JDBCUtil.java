package alex.disco.ball.util;

import alex.disco.ball.entity.Category;
import alex.disco.ball.entity.Product;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JDBCUtil {

    public static Connection createConnection(){
        try{
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:moneywatcher.db");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Product> convertToProducts(ResultSet resultSet) throws SQLException {
        List<Product> products = new ArrayList<>();
        while (resultSet.next()) {
            Integer product_id = Integer.parseInt(resultSet.getString("product_id"));
            String name = resultSet.getString("name");
            Category category = Category.valueOf(resultSet.getString("category"));
            Integer price = Integer.parseInt(resultSet.getString("price"));
            LocalDate date = LocalDate.parse(resultSet.getString("date"));
            products.add(new Product(product_id,name,category,price,date));
        }
        return products;
    }
}
