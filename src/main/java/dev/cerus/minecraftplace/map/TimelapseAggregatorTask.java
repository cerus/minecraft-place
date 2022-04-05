package dev.cerus.minecraftplace.map;

import dev.cerus.minecraftplace.MinecraftPlacePlugin;
import dev.cerus.minecraftplace.reddit.canvas.Canvas;
import dev.cerus.minecraftplace.reddit.worker.CanvasUpdateWorker;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;

public class TimelapseAggregatorTask implements Runnable {

    private final MinecraftPlacePlugin plugin;
    private final TimelapseImageController timelapseImageController;
    private final CanvasUpdateWorker canvasUpdateWorker;

    public TimelapseAggregatorTask(final MinecraftPlacePlugin plugin,
                                   final TimelapseImageController timelapseImageController,
                                   final CanvasUpdateWorker canvasUpdateWorker) {
        this.plugin = plugin;
        this.timelapseImageController = timelapseImageController;
        this.canvasUpdateWorker = canvasUpdateWorker;
    }

    @Override
    public void run() {
        try {
            final int skip = Math.max(1, this.plugin.getConfig().getInt("timelapse.skip"));
            final BufferedImage image = ImageIO.read(new URL(this.timelapseImageController.next(skip)));

            // Draw image timestamp
            if (this.plugin.getConfig().getBoolean("timelapse.show-time")) {
                this.drawTime(image);
            }

            for (final Canvas canvas : this.plugin.getCanvasMap().values()) {
                final BufferedImage subimage = image.getSubimage(canvas.getX(), canvas.getY(), canvas.getWidth(), canvas.getHeight());
                this.canvasUpdateWorker.processDirect(canvas, subimage);
            }
        } catch (final IOException ignored) {
            this.plugin.getLogger().warning("Failed to load timelapse image");
        }
    }

    private void drawTime(final BufferedImage image) {
        final Graphics2D graphics = image.createGraphics();
        final int middleX = image.getWidth() / 2;
        final int middleY = image.getHeight() / 2;

        final SimpleDateFormat format = new SimpleDateFormat("MMM d HH:mm zzz");
        final String dateString = format.format(new Date(this.timelapseImageController.timestamp()));
        final Rectangle2D stringBounds = graphics.getFontMetrics().getStringBounds(dateString, graphics);
        graphics.setColor(Color.WHITE);
        graphics.fillRect(
                (int) (middleX - stringBounds.getWidth() / 2 - 4),
                (int) (middleY - stringBounds.getHeight() / 2 - 4),
                (int) stringBounds.getWidth() + 8,
                (int) stringBounds.getHeight() + 8
        );
        graphics.setColor(Color.BLACK);
        graphics.drawRect(
                (int) (middleX - stringBounds.getWidth() / 2 - 4),
                (int) (middleY - stringBounds.getHeight() / 2 - 4),
                (int) stringBounds.getWidth() + 8,
                (int) stringBounds.getHeight() + 8
        );
        graphics.drawString(dateString,
                (int) (middleX - stringBounds.getWidth() / 2),
                (int) (middleY - stringBounds.getHeight() / 2 + graphics.getFontMetrics().getAscent()));
        graphics.dispose();
    }

}
