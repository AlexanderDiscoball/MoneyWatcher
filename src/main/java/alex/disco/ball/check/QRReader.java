package alex.disco.ball.check;

import alex.disco.ball.entity.Check;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import javax.imageio.ImageIO;
import javax.swing.text.DateFormatter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class QRReader {

    private static Map<DecodeHintType, ErrorCorrectionLevel> map = new HashMap<>();

    public static Check readQRCode(String filePath) throws IOException, NotFoundException {
        BinaryBitmap binaryBitmap = new BinaryBitmap(
                new HybridBinarizer(
                new BufferedImageLuminanceSource(
                ImageIO.read(new FileInputStream(filePath)))));

        Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap, map);

        return parse(qrCodeResult.getText());
    }

    private static Check parse(String str){
        String[] values = str.split("&");
        System.out.println(Arrays.toString(values));
        int sum = (int) (Double.parseDouble(values[1].substring(2)) * 100);
        String fn = values[2].substring(3);
        String fp = values[3].substring(2);
        String fd = values[4].substring(3);
        String[] dates = values[0].substring(2).split("T");
        StringBuilder timeBuilder;
        if(dates[1].length() > 4) {
            timeBuilder = new StringBuilder(dates[1].substring(0, 2)).append(":").append(dates[1], 2, 4).append(":").append(dates[1].substring(4));
        }
        else {
            timeBuilder = new StringBuilder(dates[1].substring(0, 2)).append(":").append(dates[1], 2, 4);
        }
        StringBuilder dateBuilder = new StringBuilder(dates[0].substring(0,4)).append("-").append(dates[0], 4, 6).append("-").append(dates[0].substring(6));

        LocalTime localTime = LocalTime.parse(timeBuilder);
        LocalDate localDate = LocalDate.parse(dateBuilder);

        LocalDateTime localDateTime = LocalDateTime.of(localDate,localTime);
        System.out.println(new Check(sum,fn,fp,fd,localDateTime));
        return new Check(sum,fn,fp,fd,localDateTime);
    }
}
