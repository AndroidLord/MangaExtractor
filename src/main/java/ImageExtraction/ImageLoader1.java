package ImageExtraction;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import javax.imageio.ImageIO;

import jdk.jshell.execution.Util;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ImageLoader1 {


    // download Images
    public static BufferedImage loadImage(String imageUrl) {
        try {

            if(imageUrl==null || imageUrl.isEmpty())
                System.err.println("\nImage is Null or Empty\n");

            URL url = new URL(imageUrl);
            BufferedImage bufferedImage = ImageIO.read(url);
            return bufferedImage;
//            ImageIcon imageIcon = new ImageIcon(image);
//            imageLabel.setIcon(imageIcon);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            // Replace the URL with the image URL you want to load
//            new ImageLoaderGUI("https://manhuaga.com/wp-content/uploads/WP-manga/data/manga_64bd34d1afeb5/7e65197c618c8f93fdc3d401dda335b6/001.png");
//        });
//    }
}
