package Testing;

import Util.WebDriverProvider;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;

import java.io.IOException;

public class WebScrapeTest {


    public static void main(String[] args) {
        String url = "https://chapmanganato.com/manga-kf988340/chapter-139";
//        WebDriver webDriver = WebDriverProvider.getInstance().getDriver();
//        webDriver.get(url);
//        String source = webDriver.getPageSource();
//
//        System.out.println(source);

        System.out.println(scrapeHtml(url));


    }

    public static String scrapeHtml(String url) {
        try {
            Document document = Jsoup.connect(url).ignoreHttpErrors(true).get();
            //  System.out.println(document.html());
            return document.html();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



}
