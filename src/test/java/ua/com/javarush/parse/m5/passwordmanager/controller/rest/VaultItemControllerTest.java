package ua.com.javarush.parse.m5.passwordmanager.controller.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf; // <--- ВАЖНЫЙ ИМПОРТ
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ua.com.javarush.parse.m5.passwordmanager.entity.VaultItem;
import ua.com.javarush.parse.m5.passwordmanager.security.JwtUtil;
import ua.com.javarush.parse.m5.passwordmanager.service.VaultItemService;

@WebMvcTest(VaultItemController.class)
@Import(VaultItemControllerTest.TestConfig.class)
class VaultItemControllerTest {

  @TestConfiguration
  static class TestConfig {
    @Bean
    public VaultItemService vaultItemService() {
      return mock(VaultItemService.class);
    }

    @Bean
    public JwtUtil jwtUtil() {
      return mock(JwtUtil.class);
    }
  }

  @Autowired private MockMvc mockMvc;

  @Autowired private VaultItemService service;

  @Autowired private ObjectMapper objectMapper;

  @Test
  @WithMockUser
  void whenPost_thenCreateItem() throws Exception {
    VaultItem item = new VaultItem();
    item.setId(1L);
    when(service.save(any(VaultItem.class))).thenReturn(item);

    mockMvc
        .perform(
            post("/api/v1/vault/create")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1L));
  }

  @Test
  @WithMockUser
  void whenGetById_thenReturnsItem() throws Exception {
    VaultItem item = new VaultItem();
    item.setId(1L);
    when(service.findById(1L)).thenReturn(Optional.of(item));

    mockMvc
        .perform(get("/api/v1/vault/{id}", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L));
  }

  @Test
  @WithMockUser
  void whenGetByIdNotFound_thenReturns404() throws Exception {
    when(service.findById(1L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/v1/vault/{id}", 1L)).andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser
  void whenPut_thenUpdatesItem() throws Exception {
    VaultItem item = new VaultItem();
    item.setId(1L);
    when(service.update(anyLong(), any(VaultItem.class))).thenReturn(Optional.of(item));

    mockMvc
        .perform(
            put("/api/v1/vault/{id}", 1L)
                .with(csrf()) // <--- ДОБАВЛЕНО
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(item)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L));
  }

  @Test
  @WithMockUser
  void whenPutNotFound_thenReturns404() throws Exception {
    when(service.update(anyLong(), any(VaultItem.class))).thenReturn(Optional.empty());

    mockMvc
        .perform(
            put("/api/v1/vault/{id}", 1L)
                .with(csrf()) // <--- ДОБАВЛЕНО
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new VaultItem())))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser
  void whenGetByLogin_thenReturnsItems() throws Exception {
    VaultItem item = new VaultItem();
    item.setLogin("testuser");
    when(service.findByLogin("testuser")).thenReturn(List.of(item));

    mockMvc
        .perform(get("/api/v1/vault/login/{login}", "testuser"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].login").value("testuser"));
  }

  @Test
  @WithMockUser
  void whenGetByLoginNotFound_thenReturnsEmptyList() throws Exception {
    when(service.findByLogin("testuser")).thenReturn(Collections.emptyList());

    mockMvc
        .perform(get("/api/v1/vault/login/{login}", "testuser"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));
  }

  @Test
  @WithMockUser
  void whenGetByResource_thenReturnsItems() throws Exception {
    VaultItem item = new VaultItem();
    item.setResource("testresource");
    when(service.findByResource("testresource")).thenReturn(List.of(item));

    mockMvc
        .perform(get("/api/v1/vault/resource").param("resource", "testresource"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].resource").value("testresource"));
  }

  @Test
  @WithMockUser
  void whenGetByResourceNotFound_thenReturnsEmptyList() throws Exception {
    when(service.findByResource("testresource")).thenReturn(Collections.emptyList());

    mockMvc
        .perform(get("/api/v1/vault/resource").param("resource", "testresource"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));
  }

  @Test
  @WithMockUser
  void whenGetAll_thenReturnsAllItems() throws Exception {
    VaultItem item1 = new VaultItem();
    VaultItem item2 = new VaultItem();
    when(service.findAll()).thenReturn(List.of(item1, item2));

    mockMvc
        .perform(get("/api/v1/vault/all"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(2));
  }

  @Test
  @WithMockUser
  void whenDelete_thenReturnsNoContent() throws Exception {
    doNothing().when(service).deleteById(1L);
    mockMvc
        .perform(delete("/api/v1/vault/{id}", 1L).with(csrf())) // <--- ДОБАВЛЕНО
        .andExpect(status().isNoContent());
  }
}
