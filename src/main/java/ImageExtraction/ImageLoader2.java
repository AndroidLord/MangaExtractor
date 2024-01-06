package ImageExtraction;

import Util.WebDriverProvider;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ImageLoader2 {


    public static BufferedImage loadImage(String imageUrl, WebDriver driver) {

        driver.get(imageUrl);

        // Capture the screenshot of the entire web page
        File screenshot = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);
        BufferedImage fullImage = null;
        // Load the screenshot as an image
        try {
            fullImage = ImageIO.read(screenshot);

         return fullImage;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return fullImage;
    }



}
