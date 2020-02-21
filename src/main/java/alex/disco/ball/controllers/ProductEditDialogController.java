package alex.disco.ball.controllers;

import alex.disco.ball.entity.Category;
import alex.disco.ball.entity.Product;
import alex.disco.ball.util.DateUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class ProductEditDialogController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField categoryField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField dateField;

    private Stage dialogStage;
    private Product product;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
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
        categoryField.setText(product.getCategory().toString());
        priceField.setText(product.getPrice().toString());
        dateField.setText(product.getDate().format(DateUtil.getDateFormatter()));
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            product.setName(nameField.getText());
            product.setCategory(categoryField.getText().toUpperCase());
            product.setPrice(Integer.parseInt(priceField.getText()));
            product.setDate(LocalDate.parse(dateField.getText(), DateTimeFormatter.ofPattern("dd.M.yyyy")));

            okClicked = true;
            dialogStage.close();
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (nameField.getText() == null || nameField.getText().length() == 0) {
            errorMessage += "Неккоректное имя\n";
        }
        if (categoryField.getText() == null || categoryField.getText().length() == 0) {
            errorMessage += "Не правильно написана категория\n";
        }else {
            try {
                Category.valueOf(categoryField.getText().toUpperCase());
            } catch (IllegalArgumentException e) {
                errorMessage += "Такой категории нет\n";
            }
        }

        if (priceField.getText() == null || priceField.getText().length() == 0) {
            errorMessage += "Не правильно написана цена\n";
        } else {
            try {
                Integer.parseInt(priceField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "No valid price code (must be an integer)!\n";
            }
        }

        if (dateField.getText() == null || dateField.getText().length() == 0) {
            errorMessage += "No valid birthday!\n";
        } else {
            if (!DateUtil.validDate(dateField.getText())) {
                errorMessage += "Используейте формат записи dd.mm.yyyy!\n";
            }
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
