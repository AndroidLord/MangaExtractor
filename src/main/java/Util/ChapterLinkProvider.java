package Util;

import java.util.HashMap;
import java.util.Map;

public class ChapterLinkProvider{


    private Map<String, ChapterLinks> chapterLinksMap = new HashMap<>();

    public ChapterLinkProvider() {

        chapterLinksMap.put("asuracomics.com",new ChapterLinks(
                "div.nextprev a.ch-prev-btn"
                ,"div.nextprev a.ch-next-btn"));

        chapterLinksMap.put("ww6.mangakakalot.tv",new ChapterLinks(
                "div.btn-navigation-chap a:contains(PREV CHAPTER)"
                ,"div.btn-navigation-chap a:contains(NEXT CHAPTER)"));

        chapterLinksMap.put("kunmanga.com",new ChapterLinks(
                "div.nav-previous a.btn.prev_page"
                ,"div.nav-next a.btn.next_page"
        ));


    }

    public ChapterLinks getChapterLinks(String websiteUrl) {
        return chapterLinksMap.get(websiteUrl);
    }
}