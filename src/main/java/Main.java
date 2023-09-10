import ImageExtraction.ImageHub;
import ImageExtraction.ImageLoader1;
import Operations.FetchChapter;
import Util.ChapterManager;
import Util.HelperMethod;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {

    private static String baseUrl = "";
    private static ChapterManager chapter = ChapterManager.getInstance();

    public static void main(String[] args) throws MalformedURLException {

        String url = "https://ww6.mangakakalot.tv/chapter/manga-sc995637/chapter-42.5";
        //String url = "https://ww6.mangakakalot.tv/chapter/manga-sx995980/chapter-46";


        baseUrl = new URL(url).getHost();

        String htmlContent = WebScrapping.scrapeHtml(url);

        List<String> imageUrls = WebScrapping.extractImageUrls(htmlContent);

        chapter.setCurrentChapter(url);
        WebScrapping.extractChapterLinks(htmlContent);

        System.out.println("ImageUrl Count: " + imageUrls.size());

        List<BufferedImage> imageList = ImageHub.getImages(imageUrls);

        // SwingUtilities.invokeLater(()->{

        new MangaViewer(imageList).setVisible(true);

        // });
    }




}
