import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.openqa.selenium.net.UrlChecker;

import java.net.URL;
import java.util.Scanner;

public class Test {


    public static void main(String[] args) throws Exception {
        String url = "https://www.google.com";
        String path = "/path/to/file";

        UrlChecker urlChecker = new UrlChecker();
        URL url1 = new URL(new Scanner(System.in).nextLine());
        String host = url1.getHost();

        System.out.println("base Name: " + host);
        System.out.println("Path Name: " + url1.getPath());
        System.out.println("Info: " + url1.getFile());


    }

}
