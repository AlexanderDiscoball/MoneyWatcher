package alex.disco.ball.entity;

import alex.disco.ball.util.DateUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.time.LocalDate;

public class Product {

    private Integer id;
    private String name;
    private Category category;
    private Integer price;
    private LocalDate date;

    private Product(){}

    public Product(Integer id, String name, Category category, Integer price, LocalDate date) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setCategory(String category) {
        this.category = Category.valueOf(category);
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public StringProperty nameProperty(){
        return new SimpleStringProperty(name);
    }

    public StringProperty categoryProperty(){
        return new SimpleStringProperty(category.getTitle());
    }
    public StringProperty dateProperty(){
        return new SimpleStringProperty(date.format(DateUtil.getDateFormatter()));
    }

    public StringProperty priceProperty(){
        return new SimpleStringProperty(price.toString());
    }

    @Override
    public String toString() {
        return "Product{" + "id=" + id + ", name='" + name + '\'' + ", category=" + category + ", price=" + price + ", date=" + date + '}';
    }

    public void setAll(Product selectedProduct) {
        this.price = selectedProduct.price;
        this.category = selectedProduct.category;
        this.date = selectedProduct.date;
        this.name = selectedProduct.name;
    }
}
