package ImageExtraction;
import Util.WebDriverProvider;
import org.openqa.selenium.WebDriver;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

import static ImageExtraction.ImageLoader1.loadImage;

public class ImageHub {

    public static List<BufferedImage> getImages(List<String> imageUrls) {

        int numThreads = Math.min(imageUrls.size(), Runtime.getRuntime().availableProcessors());

        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        CompletionService<IndexedBufferedImage> completionService = new ExecutorCompletionService<>(executorService);

        List<IndexedBufferedImage> indexedImages = new ArrayList<>();

        // Submit tasks with original index
        for (int i = 0; i < imageUrls.size(); i++) {
            final int index = i;
            completionService.submit(() -> {
                BufferedImage image = loadImage(imageUrls.get(index));
                return new IndexedBufferedImage(index, image);
            });
        }

        // Retrieve results in original order
        for (int i = 0; i < imageUrls.size(); i++) {
            try {
                Future<IndexedBufferedImage> future = completionService.take();
                IndexedBufferedImage indexedImage = future.get();
                indexedImages.add(indexedImage);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        // Sort indexed images by their original index
        indexedImages.sort(Comparator.comparingInt(IndexedBufferedImage::getIndex));

        // Extract the images in the correct order
        List<BufferedImage> imageList = new ArrayList<>();
        for (IndexedBufferedImage indexedImage : indexedImages) {
            imageList.add(indexedImage.getImage());
        }

        // Shutdown the executor service
        executorService.shutdown();

        return imageList;
    }

    static class IndexedBufferedImage {
        private final int index;
        private final BufferedImage image;

        public IndexedBufferedImage(int index, BufferedImage image) {
            this.index = index;
            this.image = image;
        }

        public int getIndex() {
            return index;
        }

        public BufferedImage getImage() {
            return image;
        }
    }

}
