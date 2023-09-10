import ImageExtraction.ImageHub;
import Util.ChapterManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WebScrapping {

    static ChapterManager chapter = ChapterManager.getInstance();


    public static List<BufferedImage> scrapeWebsite(String url){

        if(url.isEmpty()) return null;

        //String url = "https://ww6.mangakakalot.tv/chapter/manga-fj982918/chapter-1";

        String htmlContent = scrapeHtml(url);

        try {
            String baseUrl = new URL(url).getHost();

            List<String> imageUrls = extractImageUrls(htmlContent);

            System.out.println("ImageUrl to be processed: " + imageUrls.size());

            extractChapterLinks(htmlContent);

            List<BufferedImage> imageList = ImageHub.getImages(imageUrls);

            return imageList;


        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    public static void extractChapterLinks(String htmlContent) {

        try {
            // Parse the HTML content using Jsoup
            Document doc = Jsoup.parse(htmlContent);

            // Select the <a> elements within the <div class="btn-navigation-chap"> element
            Elements linkElements = doc.select("div.btn-navigation-chap a");

            // Loop through the selected <a> elements to find PREV and NEXT CHAPTER links
            for (Element linkElement : linkElements) {
                String linkText = linkElement.text().trim();

                // Check if the link text contains "PREV CHAPTER" or "NEXT CHAPTER"
                if (linkText.contains("PREV CHAPTER")) {
                    chapter.setPrevChapter( linkElement.attr("href"));
                } else if (linkText.contains("NEXT CHAPTER")) {
                    chapter.setNextChapter( linkElement.attr("href"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Base url: " +  chapter.getCurrentChapter()+"\n");
        System.out.println("next chap: " + chapter.getNextChapter());
        System.out.println("prev chap: " + chapter.getPrevChapter());

        return;
    }


    public static List<String> extractImageUrls(String htmlContent) {
        List<String> imageUrlList = new ArrayList<>();


        // Parse the HTML content using Jsoup
        Document doc = Jsoup.parse(htmlContent);

        // Select the <img> elements within the <div class="vung-doc"> element
        Elements imgElements = doc.select("div.vung-doc img");

        // Extract and print the src attribute of each <img> element
        for (Element imgElement : imgElements) {
            String imgUrl = imgElement.attr("data-src");
            imageUrlList.add(imgUrl);
        }

        return imageUrlList;
    }

    public static String scrapeHtml(String url) {
        try {
            Document document = Jsoup.connect(url).ignoreHttpErrors(true).get();
            return document.html();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
