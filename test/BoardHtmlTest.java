import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class BoardHtmlTest {
    @Test
    void preservesAttributesWhitespaceAndUtf8AndClosesStream() throws Exception {
        String image = "https://i.pinimg.com/originals/image.jpg";
        String html = "<img alt='café con leche' src='" + image + "'>\n";
        AtomicBoolean closed = new AtomicBoolean();
        String result = PinterestBoards.webToString(url(html, closed));
        assertEquals(html, result);
        assertEquals(List.of(image), PinterestBoards.getElementPos(result, "originals"));
        assertTrue(closed.get());
    }

    @Test
    void acceptsEmptyResponseAndClosesStream() throws Exception {
        AtomicBoolean closed = new AtomicBoolean();
        assertEquals("", PinterestBoards.webToString(url("", closed)));
        assertTrue(closed.get());
    }

    private URL url(String body, AtomicBoolean closed) throws Exception {
        return new URL(null, "test://board", new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL url) {
                return new URLConnection(url) {
                    public void connect() { }
                    public InputStream getInputStream() {
                        return new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)) {
                            public void close() { closed.set(true); }
                        };
                    }
                };
            }
        });
    }
}
