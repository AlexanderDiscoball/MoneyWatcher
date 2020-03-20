package alex.disco.ball;

import alex.disco.ball.util.JDBCUtil;
import alex.disco.ball.util.QueryUtil;
import java.sql.*;

public class Test {
    public static void main(String[] args) throws SQLException {
        Connection connection = JDBCUtil.createConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT LAST_INSERT_ROWID()");
        ResultSet rs = statement.executeQuery();
        System.out.println(rs.getString(1));
        connection.close();
    }
}
