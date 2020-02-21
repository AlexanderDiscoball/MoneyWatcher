package alex.disco.ball.controllers;

import alex.disco.ball.App;
import alex.disco.ball.entity.Category;
import alex.disco.ball.entity.Product;
import alex.disco.ball.util.HibernateUtil;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AppPanelController {

    private App app;

    @FXML
    private SplitPane allSplitPane;

    @FXML
    private TableView<Product> productTable;
    @FXML
    private TableColumn<Product, String> nameColumn;
    @FXML
    private TableColumn<Product, String> categoryColumn;
    @FXML
    private TableColumn<Product, String> priceColumn;
    @FXML
    private TableColumn<Product, String> dateColumn;

    @FXML
    private Label sum;
    private Integer sumInt;

    @FXML
    private void initialize(){
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

    }


    public void setAppData(App app){
        this.app = app;

        productTable.setItems(app.getProductData());
        calculateSumProducts(app.getProductData());
    }

    private void calculateSumProducts(ObservableList<Product> productData) {
        sumInt = 0;
        if(productData != null && !productData.isEmpty()){
            for (Product product : productData) {
                sumInt += product.getPrice();
            }
        }
        sum.setText(sumInt.toString());
    }

    @FXML
    private void handleDeletePerson() {
        int selectedIndex = productTable.getSelectionModel().getSelectedIndex();
        Alert alert = new Alert(Alert.AlertType.WARNING);
        if(selectedIndex >= 0) {
            Product product = productTable.getSelectionModel().getSelectedItem();
            Session session = HibernateUtil.getSessionFactory().openSession();
            Query query = session.createQuery("from Product where product_id = " + product.getId());
            if (query != null) {
                session.beginTransaction();
                Product middle = (Product) query.getSingleResult();
                session.delete(middle);
                session.getTransaction().commit();
                session.close();
                productTable.getItems().remove(selectedIndex);
                sumInt -= product.getPrice();
                sum.setText(sumInt.toString());
            } else {
                alert.initOwner(app.getPrimaryStage());
                alert.setTitle("Пустой запрос... Что-то с БД");
                alert.setHeaderText("Супер страшная ошибка");

                alert.showAndWait();
            }
        }
        else {

            alert.initOwner(app.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("Строка не выбрана");
            alert.setContentText("Выберете строку, которую хотите удалить");

            alert.showAndWait();
        }
    }

    @FXML
    private void handleAddPerson() {
        Product product = new Product("", Category.ДРУГОЕ,0, LocalDateTime.now().toLocalDate());
        boolean okClicked = app.showProductEditDialog(product);
        if (okClicked) {

            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(product);
            session.getTransaction().commit();

            session.close();
            app.getProductData().add(product);
            sumInt += product.getPrice();
            sum.setText(sumInt.toString());
        }
    }

    @FXML
    private void handleChangePerson() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            int oldPrice = selectedProduct.getPrice();
            boolean okClicked = app.showProductEditDialog(selectedProduct);
            if (okClicked) {
                Session session = HibernateUtil.getSessionFactory().openSession();
                session.beginTransaction();
                Query query = session.createQuery( "from Product where product_id = "+ selectedProduct.getId());
                if(query != null){
                    Product product = (Product) query.getSingleResult();
                    product.setAll(selectedProduct);
                    session.update(product);
                    sumInt -= oldPrice - product.getPrice();
                    sum.setText(sumInt.toString());
                    productTable.refresh();
                }
                session.getTransaction().commit();
                session.close();


            }

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(app.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("Не выбран продукт для изменение");
            alert.setContentText("Выберети продукт.");

            alert.showAndWait();
        }
    }

    private void showProductDetails(Product newValue) {
        if(newValue != null){
            productTable.getItems().set(newValue.getId(),newValue);
        }
    }

}
