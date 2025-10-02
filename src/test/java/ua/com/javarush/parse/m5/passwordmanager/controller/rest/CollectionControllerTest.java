package ua.com.javarush.parse.m5.passwordmanager.controller.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ua.com.javarush.parse.m5.passwordmanager.entity.Collection;
import ua.com.javarush.parse.m5.passwordmanager.security.JwtUtil;
import ua.com.javarush.parse.m5.passwordmanager.service.CollectionService;

@WebMvcTest(CollectionController.class)
@WithMockUser
class CollectionControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private CollectionService collectionService;

  @MockBean private JwtUtil jwtUtil;

  @MockBean private UserDetailsService userDetailsService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  void create_shouldReturnCreatedCollection() throws Exception {
    Collection collection = new Collection();
    collection.setId(1L);
    collection.setName("Test Collection");

    when(collectionService.save(any(Collection.class))).thenReturn(collection);

    mockMvc
        .perform(
            post("/api/v1/collections/create")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(collection)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.name").value("Test Collection"));
  }

  @Test
  void getAll_shouldReturnListOfCollections() throws Exception {
    Collection collection1 = new Collection();
    collection1.setId(1L);
    collection1.setName("Test Collection 1");

    Collection collection2 = new Collection();
    collection2.setId(2L);
    collection2.setName("Test Collection 2");

    when(collectionService.findAll()).thenReturn(List.of(collection1, collection2));

    mockMvc
        .perform(get("/api/v1/collections/all"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(2))
        .andExpect(jsonPath("$[0].name").value("Test Collection 1"))
        .andExpect(jsonPath("$[1].name").value("Test Collection 2"));
  }

  @Test
  void findById_whenCollectionExists_shouldReturnCollection() throws Exception {
    Collection collection = new Collection();
    collection.setId(1L);
    collection.setName("Test Collection");

    when(collectionService.findById(1L)).thenReturn(Optional.of(collection));

    mockMvc
        .perform(get("/api/v1/collections/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.name").value("Test Collection"));
  }

  @Test
  void findById_whenCollectionDoesNotExist_shouldReturnNotFound() throws Exception {
    when(collectionService.findById(1L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/v1/collections/1")).andExpect(status().isNotFound());
  }

  @Test
  void update_whenCollectionExists_shouldReturnUpdatedCollection() throws Exception {
    Collection updatedCollection = new Collection();
    updatedCollection.setId(1L);
    updatedCollection.setName("Updated Collection");

    when(collectionService.update(eq(1L), any(Collection.class)))
        .thenReturn(Optional.of(updatedCollection));

    mockMvc
        .perform(
            put("/api/v1/collections/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCollection)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.name").value("Updated Collection"));
  }

  @Test
  void update_whenCollectionDoesNotExist_shouldReturnNotFound() throws Exception {
    Collection updatedCollection = new Collection();
    updatedCollection.setId(1L);
    updatedCollection.setName("Updated Collection");

    when(collectionService.update(eq(1L), any(Collection.class))).thenReturn(Optional.empty());

    mockMvc
        .perform(
            put("/api/v1/collections/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCollection)))
        .andExpect(status().isNotFound());
  }

  @Test
  void delete_shouldReturnNoContent() throws Exception {
    mockMvc.perform(delete("/api/v1/collections/1").with(csrf())).andExpect(status().isNoContent());
  }
}
