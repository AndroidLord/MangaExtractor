import ImageExtraction.ImageHub;
import Util.ChapterManager;
import Util.HelperMethod;
import Util.WebsiteChecker;

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
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private GraphicsDevice gd; // Store the GraphicsDevice
    private boolean isFullScreen = false; // Track full-screen mode

    private JPanel loadingPanel; // Add a loading panel member

    // Adjust the thread pool size as needed
    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    private static ChapterManager chapterManager = ChapterManager.getInstance();

    // Keep track of the last viewed page index for each chapter
    private Map<String, Integer> lastViewedPages = new HashMap<>();

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
        toggleTopBarButton = new JButton(" Hide Menu ");
        toggleTopBarButton.setBorder(null);

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
        add(toggleTopBarButton, BorderLayout.EAST);

        // Create the loading panel and set it as visible initially
        loadingPanel = createLoadingPanel();
        add(loadingPanel, BorderLayout.CENTER);
        loadingPanel.setVisible(true);

        // Load the last viewed page index for the current chapter
        currentPageIndex = getLastViewedPage(chapterManager.getCurrentChapter());
        showPage(currentPageIndex);

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
        scrollPane.getVerticalScrollBar().setUnitIncrement(50);
        scrollPane.getVerticalScrollBar().setBlockIncrement(40);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        add(scrollPane, BorderLayout.CENTER);
        scrollPane.getVerticalScrollBar().setBackground(Color.BLACK); // Set the background color to match your background
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

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
                System.out.println("\nClicked Prev chapter");
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
                loadingPanel.setVisible(true);
                String url = chapterLinkField.getText();
                fetchChapterInBackground(url);
            }
        });

        toggleTopBarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleTopBarButtonsVisibility();
            }
        });

        // Add key listener for arrow key navigation
        // Add a KeyListener to the MangaViewer frame
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();

                // Toggle the top bar when Shift key is pressed
                if (e.isShiftDown() && keyCode == KeyEvent.VK_SHIFT) {
                    toggleTopBarButtonsVisibility();
                }

                // Perform the same action as toggleBackgroundColorButton when "b" key is pressed
                if (keyCode == KeyEvent.VK_B) {
                    toggleBackgroundColor();
                }

                // Handle navigation using left and right arrow keys
                if (keyCode == KeyEvent.VK_LEFT) {
                    // Navigate to the previous chapter
                    String url = null;
                    try {
                        url = HelperMethod.checkUrl(chapterManager.getPrevChapter());
                    } catch (MalformedURLException ex) {
                        ex.printStackTrace();
                    }
                    fetchChapterInBackground(url);
                } else if (keyCode == KeyEvent.VK_RIGHT) {
                    // Navigate to the next chapter
                    String url = null;
                    try {
                        url = HelperMethod.checkUrl(chapterManager.getNextChapter());
                    } catch (MalformedURLException ex) {
                        ex.printStackTrace();
                    }
                    fetchChapterInBackground(url);
                }
            }
        });



        // Get the default screen device
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        gd = ge.getDefaultScreenDevice();

        if (gd.isFullScreenSupported()) {
            // Set up a key binding to exit full-screen mode with the Escape key
            KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
            Action escapeAction = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    exitFullScreen();
                }
            };
            getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
            getRootPane().getActionMap().put("ESCAPE", escapeAction);

            // Set up a key listener to toggle full-screen mode with F11
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_F11) {
                        toggleFullScreen();
                    }
                }
            });

            setFocusable(true); // Ensure the JFrame receives keyboard input
        } else {
            System.err.println("Full-screen exclusive mode not supported.");
        }

        updateChapterButtons();
    }

    private JPanel createLoadingPanel() {
        // Create a JPanel to display your loading animation (e.g., a JLabel with a GIF)
        JPanel panel = new JPanel();
        ImageIcon loadingIcon = new ImageIcon("G:\\waiting2.gif"); // Replace with your GIF file path
        JLabel loadingLabel = new JLabel(loadingIcon);
        panel.add(loadingLabel);
        return panel;
    }

    public void toggleFullScreen() {
        if (isFullScreen) {
            exitFullScreen();
        } else {
            enterFullScreen();
        }
    }

    public void enterFullScreen() {
        if (gd.isFullScreenSupported()) {
            gd.setFullScreenWindow(this);
            isFullScreen = true;
        }
    }

    public void exitFullScreen() {
        gd.setFullScreenWindow(null);
        isFullScreen = false;
    }

    public void fetchChapterInBackground(String url) {
        if (url == null || url.isEmpty())
            return;

        executorService.submit(() -> {
            String htmlContent = WebScrapping.scrapeHtml(url);
            List<String> imageUrls = null;
            try {
                String baseUrl = new URL(url).getHost();
                imageUrls = WebScrapping.extractImageUrls(htmlContent, baseUrl);

                if (imageUrls == null || imageUrls.size() == 0) {
                    System.err.println("Image Element Not Found, Inspect Website Checker and update the HTML Element");
                }

                chapterManager.setCurrentChapter(url);
                WebScrapping.extractChapterLinks(htmlContent, baseUrl);

                System.out.println("ImageUrl Count: " + imageUrls.size());

                List<BufferedImage> imageList = ImageHub.getImages(imageUrls);
                System.out.println("Data Size: "+imageList.size());

                SwingUtilities.invokeLater(() -> {
                    // Update the UI with the new pages
                    if (imageList.size() > 0) {
                        loadingPanel.setVisible(false);
                        mangaPages.clear(); // Clear the existing pages
                        mangaPages.addAll(imageList); // Add the new pages

                        // Clear the existing pages in the panel
                        mangaPagesPanel.removeAll();

                        // Re-add all pages (new) to the panel
                        for (BufferedImage page : mangaPages) {
                            if (page == null) continue;
                            JLabel pageLabel = new JLabel();
                            pageLabel.setIcon(new ImageIcon(page));
                            pageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                            mangaPagesPanel.add(pageLabel);
                        }

                        // Store the last viewed page for the current chapter (if needed)
                        storeLastViewedPage(chapterManager.getCurrentChapter(), 0); // Start from the first page
                        currentPageIndex = 0; // Start from the first page
                        showPage(currentPageIndex);
                        revalidate();
                    } else {
                        System.err.println("No Chapter Available");
                    }
                });

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        });
    }

    private void updateChapterButtons() {
        String prevChapter = chapterManager.getPrevChapter();
        String nextChapter = chapterManager.getNextChapter();

        boolean hasPrevChapter = prevChapter != null && !prevChapter.isEmpty();
        boolean hasNextChapter = nextChapter != null && !nextChapter.isEmpty();

        previousChapterButton.setEnabled(!hasPrevChapter);
        nextChapterButton.setEnabled(!hasNextChapter);
    }

    private int getLastViewedPage(String chapterUrl) {
        // Get the last viewed page index for the given chapter URL
        return lastViewedPages.getOrDefault(chapterUrl, 0);
    }

    private void storeLastViewedPage(String chapterUrl, int pageIndex) {
        // Store the last viewed page index for the given chapter URL
        lastViewedPages.put(chapterUrl, pageIndex);
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

            toggleTopBarButton.setOpaque(true);
        }
        isBlackBackground = !isBlackBackground;
    }

    private void downloadAllImages() {
        // Specify the directory where images will be downloaded
        String downloadDir = "downloaded_images";
        downloadImages(mangaPages, downloadDir);
    }

    private void toggleTopBarButtonsVisibility() {
        if (areTopBarButtonsVisible) {
            controlPanel.setVisible(false);
            toggleTopBarButton.setText("  Show Menu  ");
        } else {
            controlPanel.setVisible(true);
            toggleTopBarButton.setText("  Hide Menu  ");
        }
        areTopBarButtonsVisible = !areTopBarButtonsVisible;
    }

    public static void downloadImages(List<BufferedImage> images, String outputDirectory) {
        File dir = new File(outputDirectory);

        if (!dir.exists())
            dir.mkdirs();

        if (dir.exists()) {
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
            this.thumbColor = null;
            this.trackColor = null;
        }
    }
}
