package dev.cerus.minecraftplace.map;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TimelapseImageController {

    private static final String RPLACE_DOT_SPACE = "https://rplace.space/combined/";

    private final List<String> images = new ArrayList<>();
    private final List<Long> timestamps = new ArrayList<>();
    private int index;

    public void loadFromRPlaceDotSpace() throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) new URL(RPLACE_DOT_SPACE).openConnection();
        connection.setRequestMethod("GET");
        connection.setDoInput(true);

        InputStream in;
        try {
            in = connection.getInputStream();
        } catch (final IOException ignored) {
            in = connection.getErrorStream();
        }

        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        final byte[] buf = new byte[512];
        int read;
        while ((read = in.read(buf)) != -1) {
            bout.write(buf, 0, read);
        }

        final String html = bout.toString();
        int index = 0;
        while (true) {
            final int startIndex = html.indexOf("<a href=\"", index);
            if (startIndex == -1) {
                break;
            }

            final String anchor = html.substring(
                    startIndex,
                    html.indexOf("</a>", startIndex)
            );
            final String anchorText = anchor.substring(anchor.indexOf("\">") + 2);

            if (anchorText.matches("\\d+\\.png")) {
                this.images.add(RPLACE_DOT_SPACE + anchorText);
                this.timestamps.add(Long.parseLong(anchorText.split("\\.")[0]) * 1000L);
            }

            index = startIndex + 1;
        }
    }

    public void put(final Collection<String> collection) {
        this.images.addAll(collection);
    }

    public String next(final int skip) {
        if (this.index + skip >= this.images.size()) {
            this.index = 0;
        }
        final String url = this.images.get(this.index);
        this.index += skip;
        return url;
    }

    public long timestamp() {
        return this.timestamps.get(this.index);
    }

}
