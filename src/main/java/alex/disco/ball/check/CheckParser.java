package alex.disco.ball.check;

import alex.disco.ball.entity.Category;
import alex.disco.ball.entity.Check;
import alex.disco.ball.entity.Product;
import javafx.stage.Stage;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static alex.disco.ball.controllers.AppPanelController.showErrorMassage;


public class CheckParser {

    private final Check check;
    private final String logName;
    private final String logPass;
    private final Stage primaryStage;

    private static final int waitDuration = 10;

    public CheckParser(Check check, String logName, String logPass, Stage primaryStage){
        this.check = check;
        this.logName = logName;
        this.logPass = logPass;
        this.primaryStage = primaryStage;
    }

    public List<Product> parse() throws IOException, InterruptedException {
        List<Product> products = new ArrayList<>();
        HttpResponse response = createRequest();

        if (response == null){
            showErrorMassage(primaryStage, "Ошибка срвера", "Сервер не отдал никакого запроса");
            return new ArrayList<>();
        }

        for (int i = 0; i <= waitDuration; i++) {
            if(response.getStatusLine().getStatusCode() == 202){
                Thread.sleep(1000);
                response = createRequest();
            }
        }

        if ( response.getStatusLine().getStatusCode() != 200 && response.getStatusLine().getStatusCode() != 202) {
            showErrorMassage(primaryStage, "Ошибка срвера", "Чек зарегестрирован, но данные не могут быть отданы "+ response.getStatusLine());
            return new ArrayList<>();
        }

        String res = EntityUtils.toString(response.getEntity(), "UTF-8");

        System.out.println(res);

        final JSONObject obj = new JSONObject(res);
        final JSONObject document =(JSONObject) obj.get("document");
        final JSONObject receipt =(JSONObject) document.get("receipt");
        final JSONArray productsJSON =(JSONArray) receipt.get("items");

        for (Object jObj : productsJSON) {
            JSONObject jsonObject = (JSONObject) jObj;
            int price = (int) Math.ceil(Double.valueOf((Integer)jsonObject.get("sum"))/100);
            String name = jsonObject.getString("name");
            //TODO Доавить сюда поиск макс id
            products.add(new Product(null, name, Category.FOOD, price, check.getLocalDateTime().toLocalDate()));
        }

        return products;
    }

    private HttpResponse createRequest() throws IOException, InterruptedException {
        HttpResponse response = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        if(isCheckValid(httpClient)) {
            if(check.getCountRequests() == 0){
                Thread.sleep(1000);
                check.setCountRequests(check.getCountRequests() + 1);
            }

            HttpGet getRequest = new HttpGet("https://" + this.logName + ":" + this.logPass + "@proverkacheka.nalog.ru:9999/v1/inns/*/kkts/*/fss/" + this.check.getFn() + "/tickets/" + this.check.getFp() + "?fiscalSign=" + this.check.getFd() + "&sendToEmail=no");
            getRequest.addHeader("Device-Id", "curl");
            getRequest.addHeader("Device-OS", "DEVICEOS");
            getRequest.addHeader("accept", "application/json");

            response = httpClient.execute(getRequest);
        }
        else {
            showErrorMassage(primaryStage, "Проблемы с чеком", "Чек устарел или не зарегестрирован в базе налоговой");
        }
        return response;
    }

    private boolean isCheckValid(CloseableHttpClient httpClient) throws IOException {
        HttpGet get = new HttpGet("https://proverkacheka.nalog.ru:9999/v1/ofds/*/inns/*/fss/" +
                check.getFn() +
                "/operations/1/tickets/" +
                check.getFp() +
                "?fiscalSign=" +
                check.getFd() +
                "&date=" +
                check.getLocalDateTime() +
                "&sum=" +
                check.getSum());
        get.addHeader("Content-Type", "application/json");
        get.addHeader("charset", "UTF-8");

        HttpResponse response = httpClient.execute(get);
        return response.getStatusLine().getStatusCode() == 204;
    }

}
