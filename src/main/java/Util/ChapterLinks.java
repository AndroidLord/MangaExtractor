package Util;


public class ChapterLinks {
    private String previousChapterLink;
    private String nextChapterLink;

    // Constructors, getters, and setters


    public ChapterLinks(String previousChapterLink, String nextChapterLink) {
        this.previousChapterLink = previousChapterLink;
        this.nextChapterLink = nextChapterLink;
    }

    public String getPreviousChapterLink() {
        return previousChapterLink;
    }

    public String getNextChapterLink() {
        return nextChapterLink;
    }
}