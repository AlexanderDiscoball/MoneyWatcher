package alex.disco.ball;


import alex.disco.ball.check.QRReader;
import alex.disco.ball.entity.Check;
import alex.disco.ball.entity.Product;
import alex.disco.ball.check.CheckParser;
import com.google.zxing.NotFoundException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Test {
    public static void main(String[] args) throws MalformedURLException {
        File file = new File(
                Test.class.getClassLoader().getResource("fxmlView/AppPanel.fxml").getFile()
        );
    }
}
