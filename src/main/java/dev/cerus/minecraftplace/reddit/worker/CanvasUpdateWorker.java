package dev.cerus.minecraftplace.reddit.worker;

import dev.cerus.minecraftplace.reddit.canvas.Canvas;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

/**
 * Downloads canvas updates and puts them onto their respective canvas.
 */
public class CanvasUpdateWorker implements Runnable {

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final List<QueueItem> queue = new CopyOnWriteArrayList<>();

    /**
     * Start the worker
     */
    public void start() {
        this.start(50);
    }

    /**
     * Start the worker
     *
     * @param delay The task delay in millis. Default is 50
     */
    public void start(final long delay) {
        this.executorService.scheduleAtFixedRate(this, 0, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * Queue a canvas update.
     *
     * @param canvas   The canvas to update
     * @param imageUrl The url to the canvas update
     */
    public void queue(final Canvas canvas, final String imageUrl) {
        this.queue.add(new QueueItem(canvas, imageUrl));
    }

    public void processDirect(final Canvas canvas, final BufferedImage image) {
        this.process(canvas, image, true);
    }

    @Override
    public void run() {
        // Process all queued items
        while (!this.queue.isEmpty()) {
            final QueueItem item = this.queue.remove(0);
            this.process(item);
        }
    }

    /**
     * Process a queued update.
     *
     * @param item The queued update
     */
    private void process(final QueueItem item) {
        try {
            // Get the image
            final BufferedImage image = ImageIO.read(new URL(item.imageUrl));
            this.process(item.canvas, image, false);
        } catch (final IOException ignored) {
        }
    }

    private void process(final Canvas canvas, final BufferedImage image, final boolean force) {
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                // Get pixel and abort if transparent
                final Color color = new Color(image.getRGB(x, y), true);
                if (color.getAlpha() != 255) {
                    continue;
                }
                // Update canvas
                canvas.setPixel(x, y, color, force);
            }
        }
    }

    /**
     * Get the amount of items in the queue
     *
     * @return The queue size
     */
    public int queueSize() {
        return this.queue.size();
    }

    /**
     * Stop the worker
     */
    public void stop() {
        this.executorService.shutdown();
    }

    private record QueueItem(Canvas canvas, String imageUrl) {}

}
