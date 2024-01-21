<<<<<<< HEAD
package com.example.Sale;public class ImageControllerTest {
}
=======
package com.example.Sale;

import com.example.Sale.controllers.ImageController;
import com.example.Sale.models.Image;
import com.example.Sale.repositories.ImageRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ImageControllerTest {
    private MockMvc mockMvc;

    @Mock
    private ImageRepository imageRepository;

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        ImageController imageController = new ImageController(imageRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(imageController).build();
    }

    @Test
    void getImageById_ImageExists_ReturnsImage() throws Exception {
        // Arrange
        byte[] fakeImageBytes = new byte[]{1,2,3,4}; // Replace with actual image bytes
        String contentType = "image/png";
        Image fakeImage = new Image(); // Assume Image class has appropriate getters and setters
        fakeImage.setId(1L);
        fakeImage.setContentType(contentType);
        fakeImage.setBytes(fakeImageBytes);

        when(imageRepository.findById(1L)).thenReturn(Optional.of(fakeImage));

        // Act & Assert
        mockMvc.perform(get("/images/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.parseMediaType(contentType)))
                .andExpect(content().bytes(fakeImageBytes));
    }

    @Test
    void getImageById_ImageDoesNotExist_ReturnsNotFound() throws Exception {
        // Arrange
        when(imageRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/images/99"))
                .andExpect(status().isNotFound());
    }


}

>>>>>>> 5271124 (Initial commit)
