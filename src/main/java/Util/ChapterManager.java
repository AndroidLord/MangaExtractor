package Util;


public class ChapterManager {
    private static ChapterManager instance;

    private String currentChapter;
    private String nextChapter;
    private String prevChapter;

    private ChapterManager() {
        // Private constructor to prevent instantiation.
    }

    public static ChapterManager getInstance() {
        if (instance == null) {
            instance = new ChapterManager();
        }
        return instance;
    }

    public String getCurrentChapter() {
        return currentChapter;
    }

    public void setCurrentChapter(String currentChapter) {
        this.currentChapter = currentChapter;
    }

    public String getNextChapter() {
        return nextChapter;
    }

    public void setNextChapter(String nextChapter) {
        this.nextChapter = nextChapter;
    }

    public String getPrevChapter() {
        return prevChapter;
    }

    public void setPrevChapter(String prevChapter) {
        this.prevChapter = prevChapter;
    }
}
