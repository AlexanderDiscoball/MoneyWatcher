package alex.disco.ball.controllers;

import alex.disco.ball.App;
import alex.disco.ball.check.CheckParser;
import alex.disco.ball.check.QRReader;
import alex.disco.ball.entity.Category;
import alex.disco.ball.entity.HandleTimeContainer;
import alex.disco.ball.entity.IncomeContainer;
import alex.disco.ball.entity.Product;
import alex.disco.ball.util.DateUtil;
import alex.disco.ball.util.JDBCUtil;
import alex.disco.ball.util.QueryUtil;
import com.google.zxing.NotFoundException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.sql.*;
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
    @FXML
    private Label incomeLabel;
    @FXML
    private Label expenditureLabel;


    private LocalDate incomeDate;
    private Integer income;
    private File fileIncome;

    @FXML
    private void initialize(){
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());

        setIncome();
        incomeLabel.setText(income +" Доход за\n" + incomeDate);
        expenditureLabel.setText(findExpend());
    }

    private void setIncome() {
        String fullInc = readIncomeDataFromTXT();
        if(fullInc.length() != 0) {
            String[] splitInc = fullInc.split("/");
            income = Integer.parseInt(splitInc[0]);
            incomeDate = LocalDate.parse(splitInc[1]);
        }
        else {
            income = 0;
            incomeDate = LocalDate.now();
        }
    }

    private String readIncomeDataFromTXT() {
        StringBuilder income = new StringBuilder();
        fileIncome = new File("income.txt");
        try(FileReader reader = new FileReader(fileIncome))
        {
            int c;
            while((c=reader.read())!=-1){
                income.append((char)c);
            }
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
        return income.toString();
    }

    private void writeIncomeDataToTXT(String incomFull){
        try(FileWriter writer = new FileWriter(fileIncome, false)) {
            writer.write(incomFull);
            writer.flush();
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }


    public void setAppData(App app){
        this.app = app;

        productTable.setItems(app.getProductData());
        int sumInt = calculateSumProducts(app.getProductData());
        sum.setText(Integer.toString(sumInt));
    }

    private int calculateSumProducts(List<Product> productData) {
        int sumInt = 0;
        if(productData != null && !productData.isEmpty()){
            for (Product product : productData) {
                sumInt += product.getPrice();
            }
        }
        return sumInt;
    }

    @FXML
    private void handleDeleteProduct() {
        int selectedIndex = productTable.getSelectionModel().getSelectedIndex();
        if(selectedIndex >= 0) {
            Product product = productTable.getSelectionModel().getSelectedItem();

            try(Connection connection = JDBCUtil.createConnection()) {

                PreparedStatement statement = connection.prepareStatement(QueryUtil.deleteProduct());
                statement.setInt(1,product.getId());
                statement.executeUpdate();
                System.out.println(product.getId());

                productTable.getItems().remove(selectedIndex);
                Integer sumInt = Integer.parseInt(sum.getText());
                sumInt -= product.getPrice();
                sum.setText(sumInt.toString());
                if(product.getDate().isAfter(incomeDate.minusDays(1))){
                    expenditureLabel.setText(String.valueOf(Integer.parseInt(expenditureLabel.getText()) + product.getPrice()));
                }
            } catch (SQLException e) {
                showErrorMassage(app.getPrimaryStage(),"Ошибка удаления", "Ошибка удаления");
            }
        }
        else {
            showErrorMassage(app.getPrimaryStage(), "Не выбран продукт", "Выберете что хотите удалить");
        }
    }

    @FXML
    private void handleAddProduct() {
        Product product = new Product(0,"", Category.ELSE,0, LocalDateTime.now().toLocalDate());
        boolean okClicked = app.showProductEditDialog(product);
        if (okClicked) {
            try(Connection connection = JDBCUtil.createConnection()) {

                PreparedStatement preparedStatement = connection.prepareStatement(QueryUtil.insertSingleProduct());
                preparedStatement.setString(1,product.getName());
                preparedStatement.setString(2,product.getCategory().getName());
                preparedStatement.setInt(3,product.getPrice());
                preparedStatement.setString(4, product.getDate().format(DateUtil.getDateFormatterForSql()));

                preparedStatement.executeUpdate();

                PreparedStatement statement = connection.prepareStatement(QueryUtil.maxIndex());
                ResultSet rs = statement.executeQuery();
                int lastIndex = Integer.parseInt(rs.getString(1));

                product.setId(lastIndex);

                app.getProductData().add(product);
                Integer sumInt = Integer.parseInt(sum.getText());
                sumInt += product.getPrice();
                sum.setText(sumInt.toString());
                if(product.getDate().isAfter(incomeDate.minusDays(1))){
                    expenditureLabel.setText(String.valueOf(Integer.parseInt(expenditureLabel.getText()) - product.getPrice()));
                }
            }
            catch (SQLException e) {
                showErrorMassage(app.getPrimaryStage(),"Ошибка добавления", "Ошибка добавления");
            }
        }
    }

    @FXML
    private void handleChangeProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            int oldPrice = selectedProduct.getPrice();
            LocalDate oldDate = selectedProduct.getDate();
            boolean okClicked = app.showProductEditDialog(selectedProduct);
            if (okClicked) {
                try(Connection connection = JDBCUtil.createConnection()) {

                    PreparedStatement statement = connection.prepareStatement(QueryUtil.updateProduct());

                    statement.setString(1, selectedProduct.getName());
                    statement.setString(2, selectedProduct.getCategory().getName());
                    statement.setInt(3, selectedProduct.getPrice());
                    statement.setString(4, selectedProduct.getDate().format(DateUtil.getDateFormatterForSql()));
                    statement.setInt(5, selectedProduct.getId());

                    statement.executeUpdate();

                    int sumInt = Integer.parseInt(sum.getText());
                    sumInt -= oldPrice - selectedProduct.getPrice();
                    sum.setText(Integer.toString(sumInt));
                    productTable.refresh();


                    if(selectedProduct.getDate().isAfter(incomeDate.minusDays(1)) && oldDate.isBefore(incomeDate)){
                        expenditureLabel.setText(String.valueOf(Integer.parseInt(expenditureLabel.getText()) - selectedProduct.getPrice()));
                    }
                    else if(selectedProduct.getDate().isBefore(incomeDate) && oldDate.isAfter(incomeDate.minusDays(1))){
                        expenditureLabel.setText(String.valueOf(Integer.parseInt(expenditureLabel.getText()) + selectedProduct.getPrice()));
                    }
                    else if(selectedProduct.getDate().isAfter(incomeDate.minusDays(1)) && oldPrice != selectedProduct.getPrice()){
                        expenditureLabel.setText(String.valueOf(Integer.parseInt(expenditureLabel.getText()) + oldPrice - selectedProduct.getPrice()));
                    }

                } catch (NumberFormatException | SQLException e) {
                    showErrorMassage(app.getPrimaryStage(), "Не выбран продукт", "Выберете что хотите изменить");
                }
            }
        }
    }

    @FXML
    private void handleTimePeriod(){
        HandleTimeContainer container = app.showTimeChangerDialog();
        List<Product> list = new ArrayList<>();
        if(container.isOkClicked()){
            try(Connection connection = JDBCUtil.createConnection()) {
                PreparedStatement statement = connection.prepareStatement(QueryUtil.betweenDatesAndCategory(container.getSelectedCategory()));
                if(container.getSelectedCategory() == Category.ALL){
                    statement.setString(1, container.getStartLocalDate().format(DateUtil.getDateFormatterForSql()));
                    statement.setString(2, container.getEndLocalDate().format(DateUtil.getDateFormatterForSql()));
                }
                else {
                    statement.setString(1, container.getStartLocalDate().format(DateUtil.getDateFormatterForSql()));
                    statement.setString(2, container.getEndLocalDate().format(DateUtil.getDateFormatterForSql()));
                    statement.setString(3, container.getSelectedCategory().getName());
                }
                ResultSet rs = statement.executeQuery();
                list.addAll(JDBCUtil.convertToProducts(rs));
                ObservableList<Product> oList = productTable.getItems();
                oList.clear();
                oList.addAll(list);
                int sumInt = computeNewSum();
                sum.setText(Integer.toString(sumInt));

                if(container.getEndLocalDate().isAfter(incomeDate)) {
                    expenditureLabel.setText(findExpend(container.getEndLocalDate()));
                }else {
                    expenditureLabel.setText("0");
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void hadleIncome(){
        IncomeContainer container = app.showIncomeSetterDialog();
        if(container.isOkClicked()){
            incomeDate = container.getDate();
            System.out.println(incomeDate);
            income = container.getIncome();
            incomeLabel.setText(income +" Доход за\n" + incomeDate);
            writeIncomeDataToTXT(income + "/" + incomeDate);
            expenditureLabel.setText(findExpend());
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
                    CheckParser cp = new CheckParser(QRReader.readQRCode(file.getAbsolutePath()), "+79160910800","655694", app.getPrimaryStage());
                    List<Product> productList = cp.parse();

                    try(Connection connection = JDBCUtil.createConnection()) {

                        PreparedStatement preparedStatement = connection.prepareStatement(QueryUtil.insertSingleProduct());

                        int i = 0;
                        for (Product product : productList) {
                            preparedStatement.setString(1,product.getName());
                            preparedStatement.setString(2,product.getCategory().getName());
                            preparedStatement.setInt(3,product.getPrice());
                            preparedStatement.setString(4, product.getDate().format(DateUtil.getDateFormatterForSql()));

                            preparedStatement.addBatch();
                            i++;
                            if (i % 1000 == 0 || i == productList.size()) {
                                preparedStatement.executeBatch(); // Execute every 1000 items.
                            }
                        }

                        PreparedStatement statement = connection.prepareStatement(QueryUtil.maxIndex());
                        ResultSet rs = statement.executeQuery();

                        int lastIndex = Integer.parseInt(rs.getString(1)) - productList.size();
                        setIds(productList, lastIndex);

                        System.out.println(productList);

                        productTable.getItems().addAll(productList);
                        sum.setText(Integer.toString(computeNewSum()));

                        expenditureLabel.setText(findExpend());
                    }
                } catch (IOException | NotFoundException | InterruptedException | SQLException e) {
                    e.printStackTrace();
                    showErrorMassage(app.getPrimaryStage(),"Ошибка чтения файла","QR-код не смог быть прочитан, сделайте более четкое фото");
                }
            }
            else {
                showErrorMassage(app.getPrimaryStage(),"Ошибка чтения файла","Неправильный формат файла");
            }
        }
    }

    private void setIds(List<Product> products, int lastIndex) {
        for (Product product : products) {
            product.setId(lastIndex++);
        }
    }

    public static void showErrorMassage(Stage stage, String header, String massage) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initOwner(stage);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(massage);

        alert.showAndWait();
    }

    private String getFileExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1)).get();
    }

    private int  computeNewSum(){
        int sumInt = 0;
        for (Product product : productTable.getItems()) {
            sumInt += product.getPrice();
        }
        return sumInt;
    }

    private String findExpend() {
        ArrayList<Product> list = new ArrayList<>();
        try(Connection connection = JDBCUtil.createConnection()) {
            PreparedStatement statement = connection.prepareStatement(QueryUtil.dateAfter());
            statement.setString(1,incomeDate.format(DateUtil.getDateFormatterForSql()));
            ResultSet rs = statement.executeQuery();
            list.addAll(JDBCUtil.convertToProducts(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int sumInt = calculateSumProducts(list);
        return Integer.toString(income - sumInt);
    }

    private String findExpend(LocalDate until) {
        ArrayList<Product> list = new ArrayList<>();
        try(Connection connection = JDBCUtil.createConnection()) {
            PreparedStatement statement = connection.prepareStatement(QueryUtil.betweenDatesAndCategory(Category.ALL));

            statement.setString(1, incomeDate.format(DateUtil.getDateFormatterForSql()));
            statement.setString(2, until.format(DateUtil.getDateFormatterForSql()));

            ResultSet rs = statement.executeQuery();
            list.addAll(JDBCUtil.convertToProducts(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int sumInt = calculateSumProducts(list);
        return Integer.toString(income - sumInt);
    }

}
