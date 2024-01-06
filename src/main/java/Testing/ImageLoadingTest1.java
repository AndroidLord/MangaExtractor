package Testing;

import Util.Credentials;
import Util.WebDriverProvider;
import org.openqa.selenium.*;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetSocketAddress;

import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ImageLoadingTest1 {

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
    public static BufferedImage loadImage(String imageUrl, WebDriver driver, Proxy proxy) throws IOException {
        // Check if the imageUrl is valid
        URL url = new URL(imageUrl);

        // Try to load the image
        try {
            driver.get(imageUrl);

            // Capture a screenshot of the entire web page
            File screenshot = ((ChromeDriver) driver).getScreenshotAs(OutputType.FILE);

            // Load the screenshot as an image
            BufferedImage fullImage = ImageIO.read(screenshot);

            // Close the screenshot file
            screenshot.delete();

            return fullImage;

        } catch (IOException e) {
            // Log the exception
            System.err.println("Failed to load image: " + e.getMessage());

            // Rethrow the exception
            throw e;
        }
    }

    public static void main(String[] args) throws IOException {

        String pageUrl = "https://mn2.mkklcdnv6temp.com/img/tab_39/04/44/13/sn995770/chapter_241/2-o.jpg"; // Replace with the actual URL of the HTML page you want to scrape.
        WebDriver webDriver = WebDriverProvider.getInstance().getDriver();
        webDriver.get(pageUrl);
       // webDriver.manage().addCookie();
        // Capture a screenshot and save it to a file.
        File screenshotFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
        try {
            FileHandler.copy(screenshotFile, new File("C:\\\\Users\\\\shubh\\\\Pictures\\\\funny\\\\anti Horny Exilier_files\\\\screenshot.png"));
            System.out.println("Screenshot saved successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }

//
//// Locate the image elements using the findElements() method.
//        List<WebElement> imageElements = webDriver.findElements(By.tagName("img"));
//
//// Loop through the image elements to extract image URLs.
//        for (WebElement imageElement : imageElements) {
//            String imgUrl = imageElement.getAttribute("src");
//
//            System.out.println(imgUrl);
//            // Download the image.
//            try {
//                URL imageUrl = new URL(imgUrl);
//                URLConnection connection = imageUrl.openConnection();
//                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
//                InputStream inputStream = connection.getInputStream();
//
//                // Specify the file path where you want to save the image.
//                String filePath = "C:\\Users\\shubh\\Pictures\\funny\\anti Horny Exilier_files\\image.jpg";
//                OutputStream outputStream = new FileOutputStream(filePath);
//
//                // Read the image data and save it to a file.
//                byte[] buffer = new byte[1024];
//                int bytesRead;
//                while ((bytesRead = inputStream.read(buffer)) != -1) {
//                    outputStream.write(buffer, 0, bytesRead);
//                }
//
//                // Close streams.
//                inputStream.close();
//                outputStream.close();
//
//                System.out.println("Image downloaded successfully to: " + filePath);
//            } catch (IOException e) {
//                e.printStackTrace();
//                System.out.println("Exiting and Image null");
//                System.exit(1);
//            }
//
//
//        }

//        String imageUrl = "https://chapmanganato.com/manga-kf988340/chapter-139";
//       WebDriver webDriver = WebDriverProvider.getInstance().getDriver();
//        webDriver.get(imageUrl);
//        // Use an explicit wait to wait for the image element to be present.
//        WebDriverWait wait = new WebDriverWait(webDriver, 10); // Adjust the timeout as needed.
//        WebElement imageElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("img")));
//
//        String imgUrl = imageElement.getAttribute("src");
//
//        // Create a new URL object using the image URL.
//        URL urlurl = new URL(imgUrl);
//// Open a connection to the image using the openConnection() method.
//        InputStream inputStream = urlurl.openConnection().getInputStream();
//
//        // Read the image data from the connection using the getInputStream() method.
//        byte[] imageData = inputStream.readAllBytes();
//
//        // Save the image data to a file using the FileOutputStream() method.
//        FileOutputStream fileOutputStream = new FileOutputStream("image.png");
//        fileOutputStream.write(imageData);
//        fileOutputStream.close();

        //System.out.println(loadImage(imageUrl,webDriver)==null?"Got Image":"Image is Null");


    }

}
