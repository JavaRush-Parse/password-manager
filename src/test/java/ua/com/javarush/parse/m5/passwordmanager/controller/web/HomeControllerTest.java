package ua.com.javarush.parse.m5.passwordmanager.controller.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import ua.com.javarush.parse.m5.passwordmanager.service.VaultItemService;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
@Import(HomeControllerTest.TestConfig.class)
class HomeControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public VaultItemService vaultItemService() {
            return mock(VaultItemService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VaultItemService service;

    @Test
    void whenHome_thenReturnsHomeViewWithVaultItems() throws Exception {
        when(service.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("vaultItems"));
    }
}
