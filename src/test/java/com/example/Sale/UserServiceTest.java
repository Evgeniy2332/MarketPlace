package com.example.Sale;

import com.example.Sale.models.Product;
import com.example.Sale.models.User;
import com.example.Sale.repositories.ProductRepository;
import com.example.Sale.repositories.UserRepository;
import com.example.Sale.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SaleApplication.class)
@AutoConfigureMockMvc
public class UserServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testCreateUsers() {
        // Arrange
        User userToSave = new User();
        userToSave.setEmail("test@example.com");
        userToSave.setPassword("password123"); // Assuming a plain password for the example

        // Mock behavior
        when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(userToSave);

        // Act
        User createdUser = userService.createUsers(userToSave);

        // Assert
        verify(userRepository).save(userToSave);
        // You can add more assertions based on your specific requirements
        // For example, check if the returned user matches the expected user.

        // Assuming you have a getter method for the encoded password in the User class
        Assertions.assertEquals("encodedPassword", createdUser.getPassword());
    }

    @Test
    public void testSaveProducts() throws Exception {
        // Prepare test data
        Product product = new Product();
        product.setTitle("Test Product");

        MockMultipartFile mockFile = new MockMultipartFile("files", "test.jpg", "image/jpeg", "some_image_content".getBytes());

        // Perform the request
        mockMvc.perform(MockMvcRequestBuilders.multipart("/your-endpoint")
                        .file(mockFile)
                        .param("product", "testProductJSONString"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify that the product is saved
        verify(productRepository, times(1)).save(product);
    }
}


