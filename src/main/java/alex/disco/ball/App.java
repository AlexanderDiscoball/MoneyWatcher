package alex.disco.ball;

import alex.disco.ball.controllers.AppPanelController;
import alex.disco.ball.controllers.ProductEditDialogController;
import alex.disco.ball.controllers.TimeChangerController;
import alex.disco.ball.controllers.WrapperController;
import alex.disco.ball.entity.Category;
import alex.disco.ball.entity.HandleTimeContainer;
import alex.disco.ball.entity.Product;
import alex.disco.ball.util.HibernateUtil;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.hibernate.Session;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class App extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private ObservableList<Product> productData = FXCollections.observableArrayList();

    private LocalDate currentStartDate = LocalDate.now();
    private LocalDate currentEndDate = LocalDate.now();
    private Category currentCategory = Category.ALL;

    public App(){
        HibernateUtil.buildSessionFactory();
        Session session = HibernateUtil.getSessionFactory().openSession();

        session.beginTransaction();
        List<Product> result = session.createQuery( "from Product" ).list();
        session.getTransaction().commit();
        session.close();
        productData.addAll(result);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        this.primaryStage.setTitle("MoneyWatcher");
        this.primaryStage.setMinHeight(620);
        this.primaryStage.setMinWidth(690);
        this.primaryStage.getIcons().add(new Image("images/mainIcon.png"));
        initWrapper();

        initViewAppPanel();
    }

    private void initWrapper(){
        try{
            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getClassLoader().getResource("fxmlView/Wrapper.fxml").toURI().toURL();
            loader.setLocation(url);

            rootLayout = loader.load();

            WrapperController controller = loader.getController();
            controller.setMainApp(this);

            Scene scene = new Scene(rootLayout);

            primaryStage.setScene(scene);
            primaryStage.show();


        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void initViewAppPanel(){
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getClassLoader().getResource("fxmlView/AppPanel.fxml").toURI().toURL();
            loader.setLocation(url);

            AnchorPane appPanel = loader.load();

            rootLayout.setCenter(appPanel);

            AppPanelController controller = loader.getController();
            controller.setAppData(this);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public ObservableList<Product> getProductData() {
        return productData;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public boolean showProductEditDialog(Product product) {
        try {

            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getClassLoader().getResource("fxmlView/Wrapper.fxml").toURI().toURL();
            loader.setLocation(url);
            AnchorPane page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Person");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.getIcons().add(new Image("images/addDialog.png"));
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            ProductEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setProduct(product);

            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return false;
        }
    }

    public HandleTimeContainer showTimeChangerDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = new URL(new File("").toURI().toURL() + "build/resources/main/fxmlView/TimeChangerDialog.fxml");
            loader.setLocation(url);
            AnchorPane page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Интервал затрат");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.getIcons().add(new Image("images/addDialog.png"));
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            TimeChangerController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setCurrentStartDate(currentStartDate);
            controller.setCurrentEndDate(currentEndDate);
            controller.setCategoryChoiceBox(currentCategory);

            dialogStage.showAndWait();

            HandleTimeContainer container = controller.getContainer();

            currentStartDate = container.getStartLocalDate();
            currentEndDate = container.getEndLocalDate();
            currentCategory = container.getSelectedCategory();

            return container;
        } catch (IOException e) {
            e.printStackTrace();
            return new HandleTimeContainer(LocalDate.now(),LocalDate.now(), Category.ALL,false);
        }
    }

}
