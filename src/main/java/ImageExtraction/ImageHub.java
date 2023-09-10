package ImageExtraction;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ImageHub {


    public static List<BufferedImage> getImages(List<String> imageUrls) {

        int numThreads = Math.min(imageUrls.size(), Runtime.getRuntime().availableProcessors()); // Choose an appropriate number of threads

        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CompletionService<BufferedImage> completionService = new ExecutorCompletionService<>(executorService);

        // Submit image fetching tasks
        for (String imageUrl : imageUrls) {
            completionService.submit(() -> ImageLoader1.loadImage(imageUrl));
        }

        List<BufferedImage> imageList = new ArrayList<>();

        // Wait for all tasks to complete and add images in the original order
        for (int i = 0; i < imageUrls.size(); i++) {
            try {
                Future<BufferedImage> future = completionService.take();
                BufferedImage image = future.get();  // This will block until the image is retrieved
                imageList.add(image);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        // Shutdown the executor service
        executorService.shutdown();

        return imageList;
    }
}
