package alex.disco.ball.check;

import alex.disco.ball.entity.Category;
import alex.disco.ball.entity.Check;
import alex.disco.ball.entity.Product;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CheckParser {

    private final Check check;
    private final String logName;
    private final String logPass;

    public CheckParser(Check check, String logName, String logPass){
        this.check = check;
        this.logName = logName;
        this.logPass = logPass;
    }

    public List<Product> parse() throws IOException, InterruptedException {
        List<Product> products = new ArrayList<>();
        HttpResponse response = createRequest();

        if (response == null){
            throw new RuntimeException("Неправильный запрос");
        }
        if ( response.getStatusLine().getStatusCode() != 200 && response.getStatusLine().getStatusCode() != 202) {
            throw new RuntimeException("Невозможно обработать запрос "
                    + response.getStatusLine().getStatusCode());
        }
        String res = EntityUtils.toString(response.getEntity(), "UTF-8");
        System.out.println("RESPONSE"+res);
        final JSONObject obj = new JSONObject(res);
        final JSONObject document =(JSONObject) obj.get("document");
        final JSONObject receipt =(JSONObject) document.get("receipt");
        final JSONArray productsJSON =(JSONArray) receipt.get("items");
        for (Object jObj : productsJSON) {
            JSONObject jsonObject = (JSONObject) jObj;
            int price = (int) Math.ceil(Double.valueOf((Integer)jsonObject.get("price"))/100);
            String name = jsonObject.getString("name");
            products.add(new Product(name, Category.FOOD, price,check.getLocalDateTime().toLocalDate()));
        }

        return products;
    }

    private HttpResponse createRequest() throws IOException, InterruptedException {
        HttpResponse response;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

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
        response = httpClient.execute(get);
        if(response.getStatusLine().getStatusCode() == 204) {
            System.out.println("Чек зарегестрирован в базе");
            if(check.getCountRequests() == 0){
                Thread.sleep(1000);
            }
            HttpGet getRequest = new HttpGet("https://" + this.logName + ":" + this.logPass + "@proverkacheka.nalog.ru:9999/v1/inns/*/kkts/*/fss/" + this.check.getFn() + "/tickets/" + this.check.getFp() + "?fiscalSign=" + this.check.getFd() + "&sendToEmail=no");
            getRequest.addHeader("Device-Id", "curl");
            getRequest.addHeader("Device-OS", "DEVICEOS");
            getRequest.addHeader("accept", "application/json");
            try {
                response = httpClient.execute(getRequest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Ошибка ебать");
        }
        return response;
    }

}
