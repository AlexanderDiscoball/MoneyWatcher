package alex.disco.ball.controllers;

import alex.disco.ball.entity.Category;
import alex.disco.ball.entity.Product;
import alex.disco.ball.util.DateUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class ProductEditDialogController {
    @FXML
    private TextField nameField;
    @FXML
    private ChoiceBox<Category> categoryChoiceBox;
    @FXML
    private TextField priceField;
    @FXML
    private DatePicker datePicker;

    private Stage dialogStage;
    private Product product;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
        categoryChoiceBox.setItems(FXCollections.observableArrayList(Category.values()));
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    public void setProduct(Product product) {
        this.product = product;

        nameField.setText(product.getName());
        categoryChoiceBox.setValue(product.getCategory());
        priceField.setText(product.getPrice().toString());
        datePicker.setValue(product.getDate());
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            product.setName(nameField.getText());
            product.setCategory(categoryChoiceBox.getValue());
            product.setPrice(Integer.parseInt(priceField.getText()));
            product.setDate(datePicker.getValue());

            okClicked = true;
            dialogStage.close();
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (nameField.getText() == null || nameField.getText().length() == 0) {
            errorMessage += "Неккоректное имя\n";
        }

        if (categoryChoiceBox.getValue() == null) {
            errorMessage += "Не правильно написана категория\n";
        }

        if (priceField.getText() == null || priceField.getText().length() == 0) {
            errorMessage += "Не правильно написана цена\n";
        } else {
            try {
                Integer.parseInt(priceField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Не правильная цена (должно быть число)!\n";
            }
        }

        if (datePicker.getValue() == null) {
            errorMessage += "No valid birthday!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Поправьте следующее: ");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }
}
