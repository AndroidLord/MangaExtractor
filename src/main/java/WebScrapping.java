import ImageExtraction.ImageHub;
import Util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class WebScrapping {

    static ChapterManager chapter = ChapterManager.getInstance();

//
//    public static List<BufferedImage> scrapeWebsite(String url){
//
//        if(url.isEmpty()) return null;
//
//        //String url = "https://ww6.mangakakalot.tv/chapter/manga-fj982918/chapter-1";
//
//        String htmlContent = scrapeHtml(url);
//
//        try {
//            String baseUrl = new URL(url).getHost();
//
//            List<String> imageUrls = extractImageUrls(htmlContent);
//
//            System.out.println("ImageUrl to be processed: " + imageUrls.size());
//
//            extractChapterLinks(htmlContent);
//
//            List<BufferedImage> imageList = ImageHub.getImages(imageUrls);
//
//            return imageList;
//
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        return new ArrayList<>();
//    }



// version 1.0
public static void extractChapterLinks(String htmlContent, String baseUrl) {
    try {
        // Parse the HTML content using Jsoup
        Document doc = Jsoup.parse(htmlContent);

        ChapterLinkProvider linkProvider = new ChapterLinkProvider();

        // Select the <a> elements within the <div class="btn-navigation-chap"> element
        ChapterLinks links = linkProvider.getChapterLinks(baseUrl);

        Elements prevChapterElements = doc.select(links.getPreviousChapterLink());
        Elements nextChapterElements = doc.select(links.getNextChapterLink());

        // Check if the links have been dynamically loaded and are shorter than expected
        boolean useSelenium = false;

        if (prevChapterElements.isEmpty() || prevChapterElements.attr("href").length() < 10) {
            useSelenium = true;
        }

        if (nextChapterElements.isEmpty() || nextChapterElements.attr("href").length() < 10) {
            useSelenium = true;
        }

        // If the links are short or missing, use Selenium to fetch them
        if (useSelenium) {
            // Use Selenium WebDriver here to fetch the links
            WebDriver driver = setupWebDriver();
            driver.get(chapter.getCurrentChapter()); // Load the page
            Thread.sleep(1000); // Adjust the wait time as needed

            WebElement prevLink = driver.findElement(By.cssSelector(links.getPreviousChapterLink()));
            WebElement nextLink = driver.findElement(By.cssSelector(links.getNextChapterLink()));

            String prevChapterUrl = prevLink.getAttribute("href");
            String nextChapterUrl = nextLink.getAttribute("href");

            chapter.setPrevChapter(prevChapterUrl);
            chapter.setNextChapter(nextChapterUrl);

            driver.quit();
        } else {
            // Use the original approach to extract links
            Element prevChapterElement = prevChapterElements.first();
            Element nextChapterElement = nextChapterElements.first();

            if (prevChapterElement != null) {
                chapter.setPrevChapter(prevChapterElement.attr("href"));
            }
            if (nextChapterElement != null) {
                chapter.setNextChapter(nextChapterElement.attr("href"));
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    if (chapter.getCurrentChapter() == null) {
        System.err.println("Current Chapter is Null");
    }
    if (chapter.getPrevChapter() == null) {
        System.err.println("Previous Chapter is Null");
    }
    if (chapter.getNextChapter() == null) {
        System.err.println("Next Chapter is Null");
    }
    System.out.println("Curr url: " + chapter.getCurrentChapter() );
       System.out.println("next chap: " + chapter.getNextChapter());
       System.out.println("prev chap: " + chapter.getPrevChapter());

}
    public static WebDriver setupWebDriver() {
        // Set the path to the ChromeDriver executable (adjust this to your system)
        System.setProperty("webdriver.chrome.driver", Credentials.WEBDRIVERPATH);

        // Configure Chrome options
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless"); // Run Chrome in headless mode (no GUI)
        chromeOptions.addArguments("--disable-gpu"); // Disable GPU for headless mode
        chromeOptions.addArguments("--disable-extensions"); // Disable extensions
        chromeOptions.addArguments("--disable-dev-shm-usage"); // Disable /dev/shm usage (Linux-specific)
        chromeOptions.addArguments("--no-sandbox"); // Disable sandbox mode (Linux-specific)

        // Initialize the WebDriver
        WebDriver driver = new ChromeDriver(chromeOptions);
        Cookie cookie = new Cookie("cookieName", "cookieValue");

        // Add the cookie to the WebDriver's cookie store.
        driver.manage().addCookie(cookie);

        // Refresh the page to use the cookie (if needed).
        driver.navigate().refresh();

        // Wait for JavaScript to load
        WebDriverWait wait = new WebDriverWait(driver, 10); // Adjust the timeout as needed

        // Wait for document.readyState to be 'complete' before proceeding
        wait.until((ExpectedCondition<Boolean>) webDriver ->
                ((org.openqa.selenium.JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));

        return driver;

    }
//    public static void extractChapterLinks(String htmlContent) {
//        try {
//            // Parse the HTML content using Jsoup
//            Document doc = Jsoup.parse(htmlContent);
//
//            // Check if the HTML structure matches the first website
//            Element prevChapterElement = doc.select("div.btn-navigation-chap a:contains(PREV CHAPTER)").first();
//            Element nextChapterElement = doc.select("div.btn-navigation-chap a:contains(NEXT CHAPTER)").first();
//
//            if (prevChapterElement != null) {
//                chapter.setPrevChapter(prevChapterElement.attr("href"));
//            }
//            if (nextChapterElement != null) {
//                chapter.setNextChapter(nextChapterElement.attr("href"));
//            }
//
//            // If the above check didn't find chapter links, check for the second website's structure
//            if (chapter.getPrevChapter() == null || chapter.getNextChapter() == null) {
//                prevChapterElement = doc.select("div.nav-previous a.btn.prev_page").first();
//                nextChapterElement = doc.select("div.nav-next a.btn.next_page").first();
//
//                if (prevChapterElement != null) {
//                    chapter.setPrevChapter(prevChapterElement.attr("href"));
//                }
//                if (nextChapterElement != null) {
//                    chapter.setNextChapter(nextChapterElement.attr("href"));
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println();
//        System.out.println("Curr url: " + chapter.getCurrentChapter() );
//        System.out.println("next chap: " + chapter.getNextChapter());
//        System.out.println("prev chap: " + chapter.getPrevChapter());
//    }



//
//    public static List<String> extractImageUrls(String htmlContent) {
//        List<String> imageUrlList = new ArrayList<>();
//
//
//        // Parse the HTML content using Jsoup
//        Document doc = Jsoup.parse(htmlContent);
//
//        // Select the <img> elements within the <div class="vung-doc"> element
//        Elements imgElements = doc.select("div.vung-doc img");
//
//        // Extract and print the src attribute of each <img> element
//        for (Element imgElement : imgElements) {
//            String imgUrl = imgElement.attr("data-src");
//            imageUrlList.add(imgUrl);
//        }
//
//        return imageUrlList;
//    }

//    public static List<String> extractImageUrls(String htmlContent) {
//        List<String> imageUrlList = new ArrayList<>();
//
//        // Parse the HTML content using Jsoup
//        Document doc = Jsoup.parse(htmlContent);
//
//        // Check if the HTML structure matches the first website
//        Elements imgElements = doc.select("div.vung-doc img");
//        if (!imgElements.isEmpty()) {
//            // Extract and print the src attribute of each <img> element
//            for (Element imgElement : imgElements) {
//                String imgUrl = imgElement.attr("data-src");
//                imageUrlList.add(imgUrl);
//            }
//        } else {
//            // Check if the HTML structure matches the second website
//            imgElements = doc.select("div.page-break.no-gaps img");
//            if (!imgElements.isEmpty()) {
//                // Extract and print the src attribute of each <img> element
//                for (Element imgElement : imgElements) {
//                    String imgUrl = imgElement.attr("src");
//                    imageUrlList.add(imgUrl);
//                }
//            } else {
//                System.err.println("No image elements found in the HTML content.");
//            }
//        }
//
//        return imageUrlList;
//    }



    public static List<String> extractImageUrls(String htmlContent,String baseUrl) {
        List<String> imageUrlList = new ArrayList<>();


        // Parse the HTML content using Jsoup
        Document doc = Jsoup.parse(htmlContent);

        System.out.print("Checking among the present Website:");

        WebsiteChecker websiteChecker = new WebsiteChecker();

        String cssSelector = websiteChecker.getWebsiteElement(baseUrl);

        if (cssSelector != null) {
            Elements imgElements = doc.select(cssSelector);

            String attribute = websiteChecker.getWebsiteAttribute(baseUrl);
            System.out.println("Attribute Used: " + attribute);

            // Extract and print the src attribute of each <img> element
            for (Element imgElement : imgElements) {
                String imgUrl = imgElement.attr(attribute);
                imageUrlList.add(imgUrl);
            }
            // Now you can work with imgElements
        }
        else
            System.err.println("Element Not Found");



        return imageUrlList;
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
