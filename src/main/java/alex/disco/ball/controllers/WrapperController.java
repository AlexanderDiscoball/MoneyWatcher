package alex.disco.ball.controllers;

import alex.disco.ball.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class WrapperController {
    private App mainApp;

    public void setMainApp(App mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void exitButton(ActionEvent event) {
        mainApp.getPrimaryStage().close();
    }
}
