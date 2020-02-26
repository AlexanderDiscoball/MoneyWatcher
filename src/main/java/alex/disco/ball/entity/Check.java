package alex.disco.ball.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Check {

    private final int sum;
    private int countRequests = 0;
    private final String fn;
    private final String fp;
    private final String fd;
    private final LocalDateTime localDateTime;

    public Check(int sum, String fn, String fp, String fd, LocalDateTime localDateTime) {
        this.sum = sum;
        this.fn = fn;
        this.fp = fp;
        this.fd = fd;
        this.localDateTime = localDateTime;
    }

    public int getSum() {
        return sum;
    }

    public String getFn() {
        return fn;
    }

    public String getFp() {
        return fp;
    }

    public String getFd() {
        return fd;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setCountRequests(int countRequests) {
        this.countRequests = countRequests;
    }

    public int getCountRequests() {
        return countRequests;
    }

    @Override
    public String toString() {
        return "Check{" + "sum=" + sum + ", fn='" + fn + '\'' + ", fp='" + fp + '\'' + ", fd='" + fd + '\'' + ", Date=" + localDateTime + '}';
    }
}
