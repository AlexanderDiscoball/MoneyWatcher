package alex.disco.ball.controllers;

import alex.disco.ball.entity.Category;
import alex.disco.ball.entity.HandleTimeContainer;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import java.time.LocalDate;

public class TimeChangerController {

    private Stage dialogStage;
    private HandleTimeContainer container;

    @FXML
    private DatePicker startDate;
    @FXML
    private DatePicker endDate;
    @FXML
    private ChoiceBox<Category> categoryChoiceBox;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setCurrentStartDate(LocalDate currentStartDate) {
        startDate.setValue(currentStartDate);
    }

    public void setCategoryChoiceBox(Category category) {
        this.categoryChoiceBox.setValue(category);
    }

    public void setCurrentEndDate(LocalDate currentEndDate) {
        endDate.setValue(currentEndDate);
    }

    @FXML
    private void initialize() {
        categoryChoiceBox.setItems(FXCollections.observableArrayList(Category.values()));
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            container = new HandleTimeContainer(startDate.getValue(), endDate.getValue(), categoryChoiceBox.getValue(), true);
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (startDate.getValue() == null ) {
            errorMessage += "Заполните начало периода\n";
        }
        if (endDate.getValue() == null ) {
            errorMessage += "Заполните конец периода\n";
        }

        if (errorMessage.length() == 0 && !startDate.getValue().isBefore(endDate.getValue())) {
            errorMessage += "Начало периода позже конца\n";
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

    public HandleTimeContainer getContainer() {
        if(container == null){
            container = new HandleTimeContainer(LocalDate.now(),LocalDate.now(),Category.ALL,false);
        }
        return container;
    }

}
