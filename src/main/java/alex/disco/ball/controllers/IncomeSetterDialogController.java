package alex.disco.ball.controllers;

import alex.disco.ball.entity.IncomeContainer;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;


public class IncomeSetterDialogController {

    @FXML
    private TextField incomeField;
    @FXML
    private DatePicker datePicker;

    private IncomeContainer incomeContainer;
    private Stage dialogStage;

    @FXML
    private void initialize(){}

    @FXML
    private void handleOk(){
        incomeContainer = new IncomeContainer(datePicker.getValue(),Integer.parseInt(incomeField.getText()),true);
        dialogStage.close();
    }

    @FXML
    private void handleCancel(){
        incomeContainer = new IncomeContainer(LocalDate.now(),0,false);
        dialogStage.close();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public IncomeContainer getContainer() {
        return incomeContainer;
    }
}
