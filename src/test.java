import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class test {
    public static void main(String[] args)
    {
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat tf = new SimpleDateFormat("HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT+8"));

        System.out.println("Date : " + df.format(date).split("-")[2]);
        System.out.println("Time : " + tf.format(date));
    }
}