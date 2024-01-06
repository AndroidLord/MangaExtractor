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


        //String url = "https://ww6.mangakakalot.tv/chapter/manga-ov991456/chapter-28";
       //String url = "https://ww6.mangakakalot.tv/chapter/manga-sc995637/chapter-30";
       String url = "https://ww6.mangakakalot.tv/chapter/manga-sc995637/chapter-30";
        //String url = "https://ww6.mangakakalot.tv/chapter/manga-sx995980/chapter-46";
        // String url = "https://asuracomics.com/4533579728-f-class-destiny-hunter-chapter-39/";

       //String url = "https://kunmanga.com/manga/revenge-of-the-iron-blooded-sword-hound/chapter-21/";

        SwingUtilities.invokeLater(() -> {
            // Create and display the MangaViewer window
            MangaViewer mangaViewer = new MangaViewer(new ArrayList<>());
            mangaViewer.setVisible(true);

            mangaViewer.fetchChapterInBackground(url);
        });
    }




}
