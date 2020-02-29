package alex.disco.ball;


import alex.disco.ball.check.QRReader;
import alex.disco.ball.entity.Category;
import alex.disco.ball.entity.Check;
import alex.disco.ball.entity.Product;

import alex.disco.ball.util.JDBCUtil;
import alex.disco.ball.util.QueryUtil;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Test {
    public static void main(String[] args) throws SQLException {
        Connection connection = JDBCUtil.createConnection();
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(QueryUtil.betweenDatesAndCategory(LocalDate.now().minusDays(10), LocalDate.now().plusDays(10), Category.ALL));

        System.out.println(JDBCUtil.convertToProducts(rs));
        connection.close();
    }
}
