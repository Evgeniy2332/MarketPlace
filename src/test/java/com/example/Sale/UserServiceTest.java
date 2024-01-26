package com.example.Sale;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.example.Sale.models.Product;
import com.example.Sale.models.User;
import com.example.Sale.repositories.ProductRepository;
import com.example.Sale.repositories.UserRepository;
import com.example.Sale.service.UserService;
import org.checkerframework.checker.units.qual.C;
import org.junit.After;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import static com.codeborne.selenide.Selenide.$x;
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
        // Инициализация моков перед каждым тестом
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUsers() {
        // Arrange:Подготовка данных для теста
        User userToSave = new User();
        userToSave.setEmail("test@example.com");
        userToSave.setPassword("password123"); // Предполагаемый простой пароль для примера

        // Mock behavior: Установка поведения моков
        when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(userToSave);

        // Выполнение тестируемого действия
        User createdUser = userService.createUsers(userToSave);

        // Проверка результата
        verify(userRepository).save(userToSave);
        // Можно добавить дополнительные проверки в соответствии с конкретными требованиями
        // Например, проверить, соответствует ли возвращенный пользователь ожидаемому пользователю

        // Предполагается, что в классе User есть метод для получения закодированного пароля
        Assertions.assertEquals("encodedPassword", createdUser.getPassword());
    }

    @Test
    public void testSaveProducts() throws Exception {
        //Подготовка тестовых данных
        Product product = new Product();
        product.setTitle("Test Product");

        MockMultipartFile mockFile = new MockMultipartFile("files", "test.jpg", "image/jpeg", "some_image_content".getBytes());

        // Выполнение запроса
        mockMvc.perform(MockMvcRequestBuilders.multipart("/product/create")
                        .file(mockFile)
                        .param("product", "testProductJSONString"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Проверка что продукт сохранен
        verify(productRepository, times(1)).save(product);
    }




    @Test
    public void testAutorizationAndCreateProduct(){


         Selenide.open("localhost:8080");//открываем страницу сайта


         $x("//*[@class='login-button']").click();//кликаем на вход в личный кабинет


    }
    @After
    public void amyTest() {

        Selenide.closeWebDriver();
    }
}


