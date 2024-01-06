package Util;

import java.util.HashMap;
import java.util.Map;

public class WebsiteChecker {


        // Define a HashMap as a class-level variable
        private Map<String, String> scrapingRules = new HashMap<>();
        private Map<String, String> scrapeWebAttribute = new HashMap<>();


        public WebsiteChecker() {

            scrapingRules.put("ww6.mangakakalot.tv","div.vung-doc img");
            scrapingRules.put("kunmanga.com","div.page-break.no-gaps img");
            scrapingRules.put("asuracomics.com","#readerarea img");

            scrapeWebAttribute.put("ww6.mangakakalot.tv","data-src");
            scrapeWebAttribute.put("kunmanga.com","src");
            scrapeWebAttribute.put("asuracomics.com","src");

        }


        // Method to retrieve scraping rules for a website
        public String getWebsiteElement(String websiteUrl) {

            if(scrapingRules.containsKey(websiteUrl)) {

                System.out.println(" !Found! ");
                System.out.println("Website Found: "+websiteUrl);
                System.out.println("Element Used: " + scrapingRules.get(websiteUrl));

                return scrapingRules.get(websiteUrl);
            }
            else
                System.out.println(" Not Found ");
            return null;
        }


    // Method to retrieve scraping rules for a website
    public String getWebsiteAttribute(String websiteUrl) {

        if(scrapeWebAttribute.containsKey(websiteUrl)) {

            return scrapeWebAttribute.get(websiteUrl);
        }
        else
            System.out.println("Attribute Absent");

        return null;
    }


}



