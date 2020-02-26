package alex.disco.ball.controllers;

import alex.disco.ball.App;
import alex.disco.ball.check.CheckParser;
import alex.disco.ball.check.QRReader;
import alex.disco.ball.entity.Category;
import alex.disco.ball.entity.HandleTimeContainer;
import alex.disco.ball.entity.Product;
import alex.disco.ball.util.DateUtil;
import alex.disco.ball.util.HibernateUtil;
import alex.disco.ball.util.QueryUtil;
import com.google.zxing.NotFoundException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class AppPanelController {

    private App app;

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
                showErrorMassage("Ошибка удаления", "Ошибка удаления");
            }
        }
        else {
            showErrorMassage("Не выбран продукт","Выберете что хотите удалить");
        }
    }

    @FXML
    private void handleAddPerson() {
        Product product = new Product("", Category.ELSE,0, LocalDateTime.now().toLocalDate());
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
            showErrorMassage("Не выбран продукт","Выберете что хотите изменить");
        }
    }

    @FXML
    private void handleTimePeriod(){
        HandleTimeContainer container = app.showTimeChangerDialog();
        if(container.isOkClicked()){
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query query = session.createQuery(QueryUtil.getDatePicker(
                    container.getStartLocalDate(),
                    container.getEndLocalDate(),
                    container.getSelectedCategory()));

            if(query != null){
                List<Product> list = (List<Product>)query.getResultList();
                ObservableList<Product> oList = productTable.getItems();
                oList.clear();
                oList.addAll(list);
                computeNewSum();
            }
            session.getTransaction().commit();
            session.close();
        }
    }

    @FXML
    private void choseFile() {
        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(app.getPrimaryStage());
        if (file != null) {
            String extension = getFileExtension(file.getName());
            if(extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("jpg")) {
                try {
                    CheckParser cp = new CheckParser(QRReader.readQRCode(file.getAbsolutePath()), "+79160910800","655694");
                    List<Product> productList = cp.parse();

                    Session session = HibernateUtil.getSessionFactory().openSession();
                    session.beginTransaction();
                    for (Product product : productList) {
                        session.save(product);
                    }
                    session.getTransaction().commit();

                    session.close();

                    productTable.getItems().addAll(productList);
                    computeNewSum();
                } catch (IOException | NotFoundException | InterruptedException e) {
                    showErrorMassage("Ошибка чтения файла","QR-код не смог быть прочитан, сделайте более четкое фото");

                }
            }
            else {
                showErrorMassage("Ошибка чтения файла","Неправильный формат файла");
            }
        }
    }

    private void showErrorMassage(String header, String massage) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initOwner(app.getPrimaryStage());
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(massage);

        alert.showAndWait();
    }

    public String getFileExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1)).get();
    }

    private void computeNewSum(){
        sumInt = 0;
        for (Product product : productTable.getItems()) {
            sumInt += product.getPrice();
        }
        sum.setText(sumInt.toString());
    }

}
