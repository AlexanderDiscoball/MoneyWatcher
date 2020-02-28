package alex.disco.ball.util;

import alex.disco.ball.entity.Category;
import alex.disco.ball.entity.Product;

import java.time.LocalDate;
import java.util.List;

public class QueryUtil {
    private final static String insert = "INSERT INTO products(NAME, CATEGORY, PRICE, DATE)\n";

    public static String insertSingleProduct(Product product) {
        return insert +
                "VALUES(" +
                wrapIntoQuotes(product.getName()) +
                ","+
                wrapIntoQuotes(product.getCategory().getName()) +
                ","+
                product.getPrice()+
                ","+
                wrapIntoQuotes(product.getDate().toString()) +
                ");";
    }

    public static String insertMultipleProducts(List<Product> products) {
        return insert + "VALUES" +  manyProducts(products);

    }

    private static String manyProducts(List<Product> products) {
        StringBuilder sb = new StringBuilder();
        for (Product product : products) {
            sb.append("(")
                    .append(wrapIntoQuotes(product.getName()))
                    .append(",")
                    .append(wrapIntoQuotes(product.getCategory().getName()))
                    .append(",")
                    .append(product.getPrice())
                    .append(",")
                    .append(wrapIntoQuotes(product.getDate().toString()))
                    .append("), ");
        }
        return sb.toString().substring(0,sb.toString().length()-2) + ";";
    }


    public static String selectAll() {
        return "Select * from products;";
    }

    public static String deleteProduct(Product product) {
        return "DELETE FROM products WHERE product_id = "+ product.getId() +";";
    }

    public static String updateProduct(Product product) {
        return "UPDATE products " +
                "SET name = "+ wrapIntoQuotes(product.getName())+
                "," +
                "category = " +
                wrapIntoQuotes(product.getCategory().getName()) +
                "," +
                "price = " +
                product.getPrice() +
                "," +
                "date = " +
                wrapIntoQuotes(product.getDate().toString()) +
                "WHERE product_id = " +
                product.getId() +
                ";";
    }

    public static String betweenDatesAndCategory(LocalDate start, LocalDate end, Category selectedCategory) {
        if(selectedCategory == Category.ALL) {
            return "Select * from products where date BETWEEN " +
                    wrapIntoQuotes(start.toString()) +
                    " AND " +
                    wrapIntoQuotes(end.toString()) +
                    ";";
        }
        else {
            return "Select * from products where date BETWEEN " +
                    wrapIntoQuotes(start.toString()) +
                    " AND " +
                    wrapIntoQuotes(end.toString()) +
                    " AND category = " +
                    wrapIntoQuotes(selectedCategory.getName()) +
                    ";";
        }
    }

    public static String dateAfter(LocalDate before) {

            return "Select * from products where date >= " +
                    wrapIntoQuotes(before.toString()) +
                    ";";
    }


    private static String wrapIntoQuotes(String value){
        return "'" + value +"'";
    }
}
