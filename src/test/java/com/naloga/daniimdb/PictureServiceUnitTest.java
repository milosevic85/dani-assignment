package com.naloga.daniimdb;

import com.naloga.daniimdb.movie.services.PictureService;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PictureServiceUnitTest {
    private final String testPicDir = "src/test/resources/pictures/";

    @Test
    void testSavePic() throws IOException {
        MockitoAnnotations.openMocks(this);

        String testPicFileName = "test-picture.jpg";
        String picFilePath = testPicDir + testPicFileName;

        byte[] b;
        try {
            b = Files.readAllBytes(Paths.get(picFilePath));
        } catch (IOException e) {
            throw new IOException("Failed to read test picture file!", e);
        }
        MultipartFile testPicFile = new MockMultipartFile("test-picture.jpg", testPicFileName, "image/jpeg", b);

        // I mock PictureService and call savePic method with the mock MultipartFile
        PictureService pictureService = new PictureService();
        pictureService.savePic(123L, testPicFile);

        // Verify that the picture file is saved with the correct name
        String savedPicFilePath = testPicDir + "123.jpg";

        // test println
        System.out.println("Checking if file exists at: " + savedPicFilePath);
        System.out.println("Does file exist? " + Files.exists(Paths.get(savedPicFilePath)));

        assertTrue(Files.exists(Paths.get(savedPicFilePath)), "File should be saved at " + savedPicFilePath);

        // Clean up after the test
        try {
            Files.deleteIfExists(Paths.get(savedPicFilePath));
        } catch (IOException e) {
            throw new IOException("Failed to delete saved test picture file", e);
        }
    }
}

