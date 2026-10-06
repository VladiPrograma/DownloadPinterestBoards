import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class NetworkReadTest {
    @Test
    void configuresFiniteTimeoutsAndClosesSuccessfulReads() throws Exception {
        AtomicBoolean closed = new AtomicBoolean();
        byte[] bytes = {0, 1, -1};
        InputStream input = new ByteArrayInputStream(bytes) {
            public void close() { closed.set(true); }
        };
        assertArrayEquals(bytes, PinterestBoards.readUrl(url(input)));
        assertTrue(closed.get());
    }

    @Test
    void closesTheStreamWhenReadingFails() throws Exception {
        AtomicBoolean closed = new AtomicBoolean();
        InputStream input = new InputStream() {
            public int read() throws IOException { throw new IOException("broken response"); }
            public void close() { closed.set(true); }
        };
        assertThrows(IOException.class, () -> PinterestBoards.readUrl(url(input)));
        assertTrue(closed.get());
    }

    private URL url(InputStream input) throws Exception {
        return new URL(null, "test://image", new URLStreamHandler() {
            protected URLConnection openConnection(URL url) {
                return new URLConnection(url) {
                    public void connect() { }
                    public InputStream getInputStream() {
                        assertTrue(getConnectTimeout() > 0);
                        assertTrue(getReadTimeout() > 0);
                        return input;
                    }
                };
            }
        });
    }
}
