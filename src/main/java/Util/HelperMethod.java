package Util;

import java.net.MalformedURLException;
import java.net.URL;

public class HelperMethod {



    public static String checkUrl(String url) throws MalformedURLException {

        String baseUrl = new URL(ChapterManager.getInstance().getCurrentChapter()).getHost();

        if (!url.startsWith("http://") && !url.startsWith("https://")) {

            if(url.startsWith(baseUrl))
                return "http://" + url;

            else
                return baseUrl+url;

        } else {
            return url;
        }
    }


}
