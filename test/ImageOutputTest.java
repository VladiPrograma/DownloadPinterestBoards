import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ImageOutputTest {
    @TempDir Path directory;

    @Test
    void preservesDottedNamesAndExcludesQueryAndFragment() {
        assertEquals("photo.large.jpg", PinterestBoards.imageFileName(
                "https://i.pinimg.com/originals/photo.large.jpg?width=300#preview"));
        assertEquals("photo", PinterestBoards.imageFileName("https://i.pinimg.com/originals/photo"));
        assertEquals("hello world.png", PinterestBoards.imageFileName(
                "https://i.pinimg.com/originals/hello%20world.png"));
    }

    @Test
    void createsMissingOutputDirectoryAndWritesBytes() throws Exception {
        Path output = directory.resolve("new").resolve("nested");
        byte[] bytes = {1, 2, 3};
        PinterestBoards.saveImage(output.toString(), "https://i.pinimg.com/originals/a.jpg?q=1", bytes);
        assertArrayEquals(bytes, Files.readAllBytes(output.resolve("a.jpg")));
    }

    @Test
    void rejectsDirectoryNamesAndBackslashTraversal() {
        for (String suffix : new String[]{"", ".", "..", "%2e%2e", "..%5coutside.jpg"}) {
            assertThrows(IllegalArgumentException.class, () -> PinterestBoards.imageFileName(
                    "https://i.pinimg.com/originals/" + suffix));
        }
    }

    @Test
    void replacesCharactersThatWindowsCannotUseInNames() {
        assertEquals("photo_large.jpg", PinterestBoards.imageFileName(
                "https://i.pinimg.com/originals/photo%3Alarge.jpg"));
    }
}
