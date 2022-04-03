package dev.cerus.minecraftplace;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.cerus.maps.plugin.map.MapScreenRegistry;
import dev.cerus.minecraftplace.auth.RedditAuthenticator;
import dev.cerus.minecraftplace.canvas.Canvas;
import dev.cerus.minecraftplace.canvas.Palette;
import dev.cerus.minecraftplace.client.PlaceSocketClient;
import dev.cerus.minecraftplace.image.CanvasUpdateWorker;
import dev.cerus.minecraftplace.listener.JoinListener;
import dev.cerus.minecraftplace.task.MapUpdateTask;
import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MinecraftPlacePlugin extends JavaPlugin {

    private final Set<Integer> previousOperations = new HashSet<>();
    private final Map<Integer, Canvas> canvasMap = new HashMap<>();
    private final CanvasUpdateWorker canvasUpdateWorker = new CanvasUpdateWorker();
    private PlaceSocketClient client;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.canvasUpdateWorker.start();

        // Attempt to authenticate ourselves
        final RedditAuthenticator.Token token;
        try {
            token = RedditAuthenticator.authenticate(
                    this.getConfig().getString("reddit.username"),
                    this.getConfig().getString("reddit.password"),
                    this.getConfig().getString("reddit.clientid"),
                    this.getConfig().getString("reddit.clientsecret")
            );
        } catch (final IOException e) {
            this.getLogger().severe("Failed to authenticate");
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        // Attempt to connect to the Reddit API
        try {
            this.startClient(token);
        } catch (final InterruptedException e) {
            this.getLogger().severe("Failed to start client");
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        // Register listener
        this.getServer().getPluginManager().registerEvents(new JoinListener(), this);

        // Enable fast graphics and start update task
        Bukkit.getScheduler().runTaskLater(this, () ->
                MapScreenRegistry.getScreens().stream()
                        .filter(screen -> screen.getWidth() == 16)
                        .filter(screen -> screen.getHeight() == 8)
                        .forEach(screen -> screen.useFastGraphics(true)), 9 * 20);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new MapUpdateTask(this), 10 * 20, 20);
    }

    @Override
    public void onDisable() {
        this.canvasUpdateWorker.stop();
        if (this.client != null) {
            this.client.close();
        }
    }

    private void startClient(final RedditAuthenticator.Token token) throws InterruptedException {
        // Flow:
        // 1. Connect to Reddit web socket
        // 2. Authenticate & request configuration
        // 3. Socket responds with config, parse configuration and subscribe to canvas updates
        // 4. Server will now start sending updates

        this.client = new PlaceSocketClient();
        this.client.handleMessage(message -> {
            if (!message.getType().equals("data")) {
                // Discard messages that are not of the 'data' type
                return;
            }

            if (message.getTypeName().equals("DiffFrameMessageData")
                    || message.getTypeName().equals("FullFrameMessageData")) {
                // These messages contain canvas updates (an url pointing to an image of the canvas)
                // These images show either the full or only a few updates tiles.
                final String imageUrl = message.get("data.subscribe.data").get("name").getAsString();
                this.canvasUpdateWorker.queue(this.canvasMap.get(message.getId() - 10), imageUrl);
            } else if (message.getTypeName().equals("ConfigurationMessageData")) {
                // This message contains color palette info and canvas info

                final JsonObject dataObj = message.get("data.subscribe.data");

                // Create palette and parse colors
                final Palette palette = new Palette();
                final JsonArray colorsArr = dataObj.getAsJsonObject("colorPalette").getAsJsonArray("colors");
                for (final JsonElement element : colorsArr) {
                    final JsonObject colorObj = element.getAsJsonObject();
                    palette.set(
                            colorObj.get("index").getAsInt(),
                            new Color(Integer.parseInt(colorObj.get("hex").getAsString().substring(1), 16), false)
                    );
                }

                // Declare canvas bounds
                final int canvasWidth = dataObj.get("canvasWidth").getAsInt();
                final int canvasHeight = dataObj.get("canvasHeight").getAsInt();

                // Parse canvases
                final JsonArray canvasArr = dataObj.getAsJsonArray("canvasConfigurations");
                for (final JsonElement element : canvasArr) {
                    final JsonObject canvasObj = element.getAsJsonObject();
                    final Canvas canvas = new Canvas(
                            palette,
                            canvasObj.get("dx").getAsInt(),
                            canvasObj.get("dy").getAsInt(),
                            canvasWidth,
                            canvasHeight
                    );
                    this.canvasMap.put(canvasObj.get("index").getAsInt(), canvas);
                }

                // It is possible to receive this message multiple times. If that happens
                // we unsubscribe from previous updates
                this.previousOperations.forEach(opId -> this.client.stopOperation(opId));
                this.previousOperations.clear();

                // Subscribe to canvas updates. The operation id is arbitrary and only
                // necessary to unsubscribe later on.
                this.canvasMap.forEach((canvasId, canvas) -> {
                    this.client.subscribeCanvas(10 + canvasId, canvasId);
                    this.previousOperations.add(10 + canvasId);
                });
            }
        });
        this.client.start(token.token());
    }

    public Map<Integer, Canvas> getCanvasMap() {
        return this.canvasMap;
    }

}
