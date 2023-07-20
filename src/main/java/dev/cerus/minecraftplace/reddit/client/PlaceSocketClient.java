package dev.cerus.minecraftplace.reddit.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

/**
 * This is a websocket client for the r/place websocket.
 * <p>
 * All the messages were "reverse engineered" by looking at the r/place
 * network tab in the Chrome dev tools.
 */
public class PlaceSocketClient extends WebSocketClient {

    private static final String SOCKET_URL = "wss://gql-realtime-2.reddit.com/query";
    private static final String MSG_STOP = "{\"id\":\"%d\",\"type\":\"stop\"}";
    private static final String MSG_AUTH = "{\"type\":\"connection_init\",\"payload\":{\"Authorization\":\"Bearer %s\"}}";
    private static final String MSG_CONFIG = "{\"id\":\"%d\",\"type\":\"start\",\"payload\":{\"variables\":{\"input\":{\"channel\":{\"teamOwner\":\"GARLICBREAD\",\"category\":\"CONFIG\"}}},\"extensions\":{},\"operationName\":\"configuration\",\"query\":\"subscription configuration($input: SubscribeInput!) {\\n  subscribe(input: $input) {\\n    id\\n    ... on BasicMessage {\\n      data {\\n        __typename\\n        ... on ConfigurationMessageData {\\n          colorPalette {\\n            colors {\\n              hex\\n              index\\n              __typename\\n            }\\n            __typename\\n          }\\n          canvasConfigurations {\\n            index\\n            dx\\n            dy\\n            __typename\\n          }\\n          activeZone {\\n            topLeft {\\n              x\\n              y\\n              __typename\\n            }\\n            bottomRight {\\n              x\\n              y\\n              __typename\\n            }\\n            __typename\\n          }\\n          canvasWidth\\n          canvasHeight\\n          adminConfiguration {\\n            maxAllowedCircles\\n            maxUsersPerAdminBan\\n            __typename\\n          }\\n          __typename\\n        }\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\"}}";
    private static final String MSG_SUB_CANVAS = "{\"id\":\"%d\",\"type\":\"start\",\"payload\":{\"variables\":{\"input\":{\"channel\":{\"teamOwner\":\"GARLICBREAD\",\"category\":\"CANVAS\",\"tag\":\"%d\"}}},\"extensions\":{},\"operationName\":\"replace\",\"query\":\"subscription replace($input: SubscribeInput!) {\\n  subscribe(input: $input) {\\n    id\\n    ... on BasicMessage {\\n      data {\\n        __typename\\n        ... on FullFrameMessageData {\\n          __typename\\n          name\\n          timestamp\\n        }\\n        ... on DiffFrameMessageData {\\n          __typename\\n          name\\n          currentTimestamp\\n          previousTimestamp\\n        }\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\"}}";

    private final Set<Consumer<String>> rawMessageHandlers = new HashSet<>();
    private final Set<Consumer<Message>> messageHandlers = new HashSet<>();
    private final Set<Consumer<Exception>> errorHandlers = new HashSet<>();
    private final Set<Runnable> openHandlers = new HashSet<>();
    private final Set<Runnable> closeHandlers = new HashSet<>();

    public PlaceSocketClient() {
        super(
                URI.create(SOCKET_URL),
                Map.of("Origin", "https://garlic-bread.reddit.com") // Required or else we get 401
        );
    }

    /**
     * Handles 'open' events
     *
     * @param runnable The handler
     */
    public void handleOpen(final Runnable runnable) {
        this.openHandlers.add(runnable);
    }

    /**
     * Handles 'close' events
     *
     * @param runnable The handler
     */
    public void handleClose(final Runnable runnable) {
        this.closeHandlers.add(runnable);
    }

    /**
     * Handles raw incoming messages
     *
     * @param handler The handler
     */
    public void handleRawMessage(final Consumer<String> handler) {
        this.rawMessageHandlers.add(handler);
    }

    /**
     * Handles processed incoming messages
     *
     * @param handler The handler
     */
    public void handleMessage(final Consumer<Message> handler) {
        this.messageHandlers.add(handler);
    }

    /**
     * Handles errors
     *
     * @param handler The handler
     */
    public void handleError(final Consumer<Exception> handler) {
        this.errorHandlers.add(handler);
    }

    /**
     * Connect, authenticate and request config
     *
     * @param token The token to authenticate with
     *
     * @throws InterruptedException When interrupted
     */
    public void start(final String token) throws InterruptedException {
        this.connectBlocking();
        this.auth(token);
        this.requestConfig();
    }

    /**
     * Authenticate ourselves
     *
     * @param token The token to authenticate with
     */
    public void auth(final String token) {
        this.send(String.format(MSG_AUTH, token));
    }

    /**
     * Request the configuration (colors and canvases)
     */
    public void requestConfig() {
        this.send(String.format(MSG_CONFIG, 1));
    }

    /**
     * Subscribe to canvas updates
     *
     * @param opId   The arbitrary operation id
     * @param canvas The canvas id
     */
    public void subscribeCanvas(final int opId, final int canvas) {
        this.send(String.format(MSG_SUB_CANVAS, opId, canvas));
    }

    /**
     * Stop an operation
     *
     * @param opId The operation id
     */
    public void stopOperation(final int opId) {
        this.send(String.format(MSG_STOP, opId));
    }

    @Override
    public void onOpen(final ServerHandshake serverHandshake) {
        this.openHandlers.forEach(Runnable::run);
    }

    @Override
    public void onMessage(final String s) {
        this.rawMessageHandlers.forEach(handler -> handler.accept(s));

        // Process message
        final JsonElement parsedMsg = JsonParser.parseString(s);
        if (parsedMsg.isJsonObject()) {
            final JsonObject parsedMsgObj = parsedMsg.getAsJsonObject();
            if (parsedMsgObj.has("id")
                && parsedMsgObj.has("type")) {
                final Message message = new Message(parsedMsgObj);
                this.messageHandlers.forEach(handler -> handler.accept(message));
            }
        }
    }

    @Override
    public void onClose(final int i, final String s, final boolean b) {
        this.closeHandlers.forEach(Runnable::run);
    }

    @Override
    public void onError(final Exception e) {
        this.errorHandlers.forEach(handler -> handler.accept(e));
    }

}
