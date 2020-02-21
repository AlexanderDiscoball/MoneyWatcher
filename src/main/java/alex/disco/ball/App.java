package alex.disco.ball;

import alex.disco.ball.controllers.AppPanelController;
import alex.disco.ball.controllers.ProductEditDialogController;
import alex.disco.ball.controllers.WrapperController;
import alex.disco.ball.entity.Category;
import alex.disco.ball.entity.Product;
import alex.disco.ball.util.HibernateUtil;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.hibernate.Session;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class App extends Application {

    private FXMLLoader loader;
    private Stage primaryStage;
    private BorderPane rootLayout;
    private ObservableList<Product> productData = FXCollections.observableArrayList();

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
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        this.primaryStage.setTitle("ДеньгоСледитель");
        this.primaryStage.setMinHeight(620);
        this.primaryStage.setMinWidth(690);
        this.primaryStage.getIcons().add(new Image("images/mainIcon.png"));
        initWrapper();

        initViewAppPanel();
    }

    private void initWrapper(){
        try{
            loader = new FXMLLoader();
            URL url = new URL("file:/D:/job/MoneyWatcher/build/resources/main/fxmlView/Wrapper.fxml");
            loader.setLocation(url);

            rootLayout = loader.load();

            WrapperController controller = loader.getController();
            controller.setMainApp(this);

            Scene scene = new Scene(rootLayout);

            primaryStage.setScene(scene);
            primaryStage.show();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initViewAppPanel(){
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = new URL("file:/D:/job/MoneyWatcher/build/resources/main/fxmlView/AppPanel.fxml");
            loader.setLocation(url);

            AnchorPane appPanel = loader.load();

            rootLayout.setCenter(appPanel);

            AppPanelController controller = loader.getController();
            controller.setAppData(this);
        } catch (IOException e) {
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
            // Загружаем fxml-файл и создаём новую сцену
            // для всплывающего диалогового окна.
            FXMLLoader loader = new FXMLLoader();
            URL url = new URL("file:/D:/job/MoneyWatcher/build/resources/main/fxmlView/ProductEditDialog.fxml");
            loader.setLocation(url);
            AnchorPane page = loader.load();

            // Создаём диалоговое окно Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Person");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            dialogStage.getIcons().add(new Image("images/addDialog.png"));
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Передаём адресата в контроллер.
            ProductEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setProduct(product);

            // Отображаем диалоговое окно и ждём, пока пользователь его не закроет
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
