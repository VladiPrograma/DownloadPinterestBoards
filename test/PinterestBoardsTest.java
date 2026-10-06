import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PinterestBoardsTest {
    private static final String IMAGE = "https://i.pinimg.com/originals/ab/cd/image.jpg";

    @Test
    void extractsUniqueOriginalImagesFromHtmlAndJson() {
        String html = "<img src=\"" + IMAGE + "\"><script>{\"url\":\"" + IMAGE
                + "\"}</script><img src='https://i.pinimg.com/originals/second.png'>";
        assertEquals(List.of(IMAGE, "https://i.pinimg.com/originals/second.png"),
                PinterestBoards.getElementPos(html, "originals"));
    }

    @Test
    void ignoresUnquotedAndTruncatedMatches() {
        for (String html : List.of("originals", "originals at the end", "\"" + IMAGE,
                "<p>originals</p>", "")) {
            assertTrue(PinterestBoards.getElementPos(html, "originals").isEmpty());
        }
    }

    @Test
    void handlesEmptyPatternAndUppercasePattern() {
        assertTrue(PinterestBoards.getElementPos(IMAGE, "").isEmpty());
        assertEquals(List.of(IMAGE), PinterestBoards.getElementPos("\"" + IMAGE + "\"", "ORIGINALS"));
    }

    @Test
    void excludesNonImageHostsAndThumbnailPaths() {
        String html = "\"https://evil.example/pinimg/originals/a.jpg\" "
                + "\"https://pinimg.com.evil.example/originals/a.jpg\" "
                + "\"https://i.pinimg.com/736x/a.jpg\" \"not a pinimg originals URL\"";
        assertTrue(PinterestBoards.getElementPos(html, "originals").isEmpty());
    }

    @Test
    void readsEscapedJsonUrls() {
        assertEquals(List.of(IMAGE), PinterestBoards.getElementPos(
                "\"" + IMAGE.replace("/", "\\/") + "\"", "originals"));
    }
}
