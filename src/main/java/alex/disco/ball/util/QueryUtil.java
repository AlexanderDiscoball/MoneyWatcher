package alex.disco.ball.util;

import alex.disco.ball.entity.Category;


public class QueryUtil {

    public static String insertSingleProduct() {
        return "INSERT INTO products(NAME, CATEGORY, PRICE, DATE) VALUES( ?, ?, ?, ? );";
    }

    public static String selectAll() {
        return "Select * FROM products;";
    }

    public static String deleteProduct() {
        return "DELETE FROM products WHERE product_id = ?;";
    }

    public static String updateProduct() {
        return "UPDATE products SET name = ?, category = ?, price = ?, date = ? WHERE product_id = ?;";
    }

    public static String betweenDatesAndCategory(Category selectedCategory) {
        if(selectedCategory == Category.ALL) {
            return "Select * FROM products where date BETWEEN ? AND ?;";
        }
        else {
            return "Select * FROM products where date BETWEEN ? AND ? AND category = ?;";
        }
    }

    public static String dateAfter() {
        return "Select * from products where date >= ?;";
    }


    public static String maxIndex() {
        return "SELECT LAST_INSERT_ROWID()";
    }
}
