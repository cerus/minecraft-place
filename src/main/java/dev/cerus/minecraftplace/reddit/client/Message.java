package dev.cerus.minecraftplace.reddit.client;

import com.google.gson.JsonObject;

/**
 * Represents an incoming websocket message.
 * <p>
 * <p>
 * id is the operation id,
 * <p>
 * type is the message type.
 * <p>
 * rawPayload is the message payload. Not always present.
 */
public class Message {

    private final int id;
    private final String type;
    private final JsonObject rawPayload;

    public Message(final JsonObject object) {
        this.id = object.get("id").getAsInt();
        this.type = object.get("type").getAsString();
        this.rawPayload = object.has("payload") ? object.get("payload").getAsJsonObject() : null;
    }

    /**
     * Convenience method for retrieving a json object from the payload. The path is delimited by a dot.
     * <p>
     * Example:
     * <pre>
     * // Payload {data:{some_obj:{abc:{def:"123"}}}}
     * message.get("data.some_obj.abc").get("def").getString() // -> 123
     * </pre>
     *
     * @param path The path of the object to retrieve
     *
     * @return The object at the specified path
     */
    public JsonObject get(final String path) {
        final String[] pathItems = path.split("\\.");
        JsonObject current = this.rawPayload;
        for (final String item : pathItems) {
            current = current.getAsJsonObject(item);
        }
        return current;
    }

    /**
     * Get the type of the serialized object
     *
     * @return The type name of the serialized object
     */
    public String getTypeName() {
        return this.get("data.subscribe.data").get("__typename").getAsString();
    }

    public int getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }

    public JsonObject getRawPayload() {
        return this.rawPayload;
    }

}
