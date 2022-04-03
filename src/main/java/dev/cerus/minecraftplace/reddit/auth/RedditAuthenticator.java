package dev.cerus.minecraftplace.reddit.auth;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class RedditAuthenticator {

    private RedditAuthenticator() {
        throw new UnsupportedOperationException();
    }

    public static Token authenticate(final String user, final String pass, final String clientId, final String clientSecret) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) new URL("https://www.reddit.com/api/v1/access_token" +
                "?grant_type=password&username=" + user + "&password=" + pass).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8)));
        connection.setRequestProperty("User-Agent", "github.com/cerus/minecraft-place");
        connection.setDoInput(true);

        InputStream in;
        try {
            in = connection.getInputStream();
        } catch (final Exception e) {
            in = connection.getErrorStream();
        }

        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        final byte[] buf = new byte[512];
        int read;
        while ((read = in.read(buf)) != -1) {
            bout.write(buf, 0, read);
        }

        final JsonObject responseObj = JsonParser.parseString(bout.toString()).getAsJsonObject();
        if (responseObj.has("error")) {
            throw new IllegalStateException("Failed to authenticate");
        }

        return new Token(
                System.currentTimeMillis() + (responseObj.get("expires_in").getAsLong() * 1000),
                responseObj.get("access_token").getAsString()
        );
    }

    public record Token(long expiresTimestamp, String token) {

        public boolean isValid() {
            return System.currentTimeMillis() < this.expiresTimestamp();
        }

    }

}
