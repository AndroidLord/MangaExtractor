
import ImageExtraction.ImageHub;
import Util.ChapterManager;
import Util.HelperMethod;
import org.jsoup.Jsoup;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MangaViewer extends JFrame {
    private List<BufferedImage> mangaPages;
    private int currentPageIndex = 0;
    private JPanel mangaPagesPanel;
    private JScrollPane scrollPane;
    private JPanel controlPanel;
    private JButton nextChapterButton;
    private JButton previousChapterButton;
    private JButton toggleBackgroundColorButton;
    private JButton downloadButton;
    private JTextField chapterLinkField;
    private JButton fetchChapterButton;
    private JButton toggleTopBarButton;

    private boolean isBlackBackground = false;
    private boolean areTopBarButtonsVisible = true;

    // Adjust the thread pool size as needed
    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    private static ChapterManager chapterManager = ChapterManager.getInstance();

    public MangaViewer(List<BufferedImage> mangaPages) {

        this.mangaPages = mangaPages;

        setTitle("Manga Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        nextChapterButton = new JButton("Next —>");
        previousChapterButton = new JButton("<— Prev");
        toggleBackgroundColorButton = new JButton("Toggle Background");
        downloadButton = new JButton("Download All");
        chapterLinkField = new JTextField(20);
        fetchChapterButton = new JButton("Fetch Chapter");
        //topBarButtonPanel = new JPanel();
        toggleTopBarButton = new JButton("Toggle Top Buttons");


        controlPanel.add(previousChapterButton);
        controlPanel.add(toggleBackgroundColorButton);
        controlPanel.add(downloadButton);
        controlPanel.add(chapterLinkField);
        controlPanel.add(fetchChapterButton);
        controlPanel.add(nextChapterButton);

        toggleTopBarButton.setVisible(false);
        toggleTopBarButton.setOpaque(false);
        toggleTopBarButton.setContentAreaFilled(false);
        toggleTopBarButton.setBorderPainted(false);
        add(toggleTopBarButton,BorderLayout.EAST);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);

                if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                    toggleTopBarButton.setVisible(true);

                } else {
                    toggleTopBarButton.setVisible(false);
                }

            }
        });

        add(controlPanel, BorderLayout.SOUTH);

        mangaPagesPanel = new JPanel();
        mangaPagesPanel.setLayout(new BoxLayout(mangaPagesPanel, BoxLayout.Y_AXIS));

        for (BufferedImage page : mangaPages) {
            JLabel pageLabel = new JLabel();
            pageLabel.setIcon(new ImageIcon(page));
            pageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mangaPagesPanel.add(pageLabel);
        }

        scrollPane = new JScrollPane(mangaPagesPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(30);
        scrollPane.getVerticalScrollBar().setBlockIncrement(100);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        add(scrollPane, BorderLayout.CENTER);

        showPage(currentPageIndex);

        nextChapterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {


                System.out.println("Clicked Next chapter");

                String url = null;
                try {
                    url = HelperMethod.checkUrl(chapterManager.getNextChapter());
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }

                fetchChapterInBackground(url);


            }
        });

        previousChapterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                System.out.println("Clicked Prev chapter");


                String url = null;
                try {
                    url = HelperMethod.checkUrl(chapterManager.getPrevChapter());
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }

                fetchChapterInBackground(url);

            }
        });

        toggleBackgroundColorButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleBackgroundColor();
            }
        });

        downloadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                downloadAllImages();
            }
        });

        fetchChapterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String url = chapterLinkField.getText();
                fetchChapterInBackground(url);
            }
        });

        toggleTopBarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleTopBarButtonsVisibility();
            }
        });
    }

    private void fetchChapterInBackground(String url) {

        if(url==null || url.isEmpty())
            return;

        executorService.submit(() -> {

            String htmlContent = WebScrapping.scrapeHtml(url);

            List<String> imageUrls = WebScrapping.extractImageUrls(htmlContent);

            chapterManager.setCurrentChapter(url);

            WebScrapping.extractChapterLinks(htmlContent);

            System.out.println("ImageUrl Count: " + imageUrls.size());

            List<BufferedImage> imageList = ImageHub.getImages(imageUrls);
            System.out.println("Data acquired");

            SwingUtilities.invokeLater(() -> {
                // Update the UI with the new pages
                if (imageList.size() > 0) {
                    // Add the new pages to the existing list
                    mangaPages.addAll(imageList);

                    // Clear the existing pages in the panel
                    mangaPagesPanel.removeAll();

                    // Re-add all pages (both old and new) to the panel
                    for (BufferedImage page : mangaPages) {
                        JLabel pageLabel = new JLabel();
                        pageLabel.setIcon(new ImageIcon(page));
                        pageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                        mangaPagesPanel.add(pageLabel);
                    }

                    // Update the current page index
                    showPage(currentPageIndex);
                    revalidate();
                } else {
                    System.err.println("No Chapter Available");
                }
            });
        });
    }

    private void handleFetchedChapter(List<BufferedImage> newMangaPages) {
        if (newMangaPages.size() > 0) {
            // Add the new pages to the existing list
            mangaPages.addAll(newMangaPages);

            // Clear the existing pages in the panel
            mangaPagesPanel.removeAll();

            // Re-add all pages (both old and new) to the panel
            for (BufferedImage page : mangaPages) {
                JLabel pageLabel = new JLabel();
                pageLabel.setIcon(new ImageIcon(page));
                pageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                mangaPagesPanel.add(pageLabel);
            }

            showPage(currentPageIndex);
            revalidate();
        } else {
            System.err.println("No Chapter Available");
        }
    }
    private void showPage(int pageIndex) {
        if (pageIndex >= 0 && pageIndex < mangaPages.size()) {
            currentPageIndex = pageIndex;
            scrollPane.getVerticalScrollBar().setValue(pageIndex * scrollPane.getHeight());
            setTitle("Manga Viewer - Page " + (pageIndex + 1));
        }
    }

    private void adjustPageWidth(int delta) {
        int newWidth = mangaPagesPanel.getWidth() + delta;
        if (newWidth < getWidth()) {
            mangaPagesPanel.setPreferredSize(new Dimension(newWidth, mangaPagesPanel.getHeight()));
            revalidate();
        }
    }

    private void toggleBackgroundColor() {
        if (isBlackBackground) {
            controlPanel.setBackground(null);
            mangaPagesPanel.setBackground(null);
            toggleTopBarButton.setBackground(null);


            toggleTopBarButton.setBorderPainted(false);
            toggleTopBarButton.setOpaque(false);
            toggleTopBarButton.setContentAreaFilled(false);

        } else {
            toggleTopBarButton.setBackground(Color.BLACK);
            controlPanel.setBackground(Color.BLACK);
            mangaPagesPanel.setBackground(Color.BLACK);


            toggleTopBarButton.setBorderPainted(true);
            toggleTopBarButton.setOpaque(true);
            toggleTopBarButton.setContentAreaFilled(true);
        }
        isBlackBackground = !isBlackBackground;
    }

    private void downloadAllImages() {
        // Specify the directory where images will be downloaded
        String downloadDir = "downloaded_images";

        downloadImages(mangaPages,downloadDir);

    }

    private void toggleTopBarButtonsVisibility() {
        if (areTopBarButtonsVisible) {
            controlPanel.setVisible(false);
//            topBarButtonPanel.add(controlPanel, BorderLayout.CENTER);
        } else {
            controlPanel.setVisible(true);
            //          topBarButtonPanel.remove(controlPanel);
            //        topBarButtonPanel.revalidate();
        }
        areTopBarButtonsVisible = !areTopBarButtonsVisible;
    }

//    public static void main(String[] args) {
//
//        String url = "https://ww6.mangakakalot.tv/chapter/manga-sc995637/chapter-42.5";
//
//        SwingUtilities.invokeLater(() -> {
//            // Load manga pages (you need to implement this part)
//
//            List<BufferedImage> mangaPages = WebScrapping.scrapeWebsite(url);
//
//            if (mangaPages ==null || mangaPages.size() == 0) {
//                System.err.println("No Chapter Available");
//                mangaPages = new ArrayList<>();
//            }
//
//            MangaViewer mangaViewer = new MangaViewer(mangaPages);
//            mangaViewer.setVisible(true);
//
//
//        });
//
//    }



    public static void downloadImages(List<BufferedImage> images, String outputDirectory) {

        File dir = new File(outputDirectory);

        if(!dir.exists())
            dir.mkdirs();

        if(dir.exists()){

            for (int i = 0; i < images.size(); i++) {
                BufferedImage image = images.get(i);
                String fileName = "image_" + i + ".png"; // Change the file naming logic if needed

                try {
                    File outputFile = new File(outputDirectory, fileName);
                    ImageIO.write(image, "png", outputFile);
                    System.out.println("Downloaded: " + fileName);

                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Failed to download: " + fileName);
                }

            }

            System.out.println("Download Location: " + dir.getAbsolutePath());
        }

    }
    public class CustomScrollBarUI extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            super.configureScrollBarColors();

            // Set the thumb color to transparent
          // this.thumbColor = new Color(0, 0, 0, 0);
           this.thumbColor = new Color(0, 0, 0, 0);

            // Set the track color to transparent
            //this.trackColor = new Color(0, 0, 0, 0);
        }

    }

//    public static String checkUrl(String url) {
//
//        String baseUrl = WebScrapping.chapterLink.baseUrl;
//
//        if (!url.startsWith("http://") && !url.startsWith("https://")) {
//
//            if(url.startsWith(baseUrl))
//            return "http://" + url;
//
//            else
//                return baseUrl+url;
//
//        } else {
//            return url;
//        }
//    }

}
