package ua.com.javarush.parse.m5.passwordmanager.controller.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ua.com.javarush.parse.m5.passwordmanager.entity.Collection;
import ua.com.javarush.parse.m5.passwordmanager.security.JwtUtil;
import ua.com.javarush.parse.m5.passwordmanager.service.CollectionService;

@WebMvcTest(CollectionControllerWeb.class)
@WithMockUser
class CollectionControllerWebTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private CollectionService collectionService;

  @MockBean private JwtUtil jwtUtil;

  @MockBean private UserDetailsService userDetailsService;

  @Test
  void showCreateForm_shouldReturnCreateCollectionView() throws Exception {
    mockMvc
        .perform(get("/collections/create"))
        .andExpect(status().isOk())
        .andExpect(view().name("create-collection"))
        .andExpect(model().attributeExists("collection"));
  }

  @Test
  void save_shouldRedirectToHome() throws Exception {
    mockMvc
        .perform(post("/collections/save").with(csrf()).param("name", "Test Collection"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"));
  }

  @Test
  void saveNewItem_withHxRequest_shouldReturnEmptyString() throws Exception {
    mockMvc
        .perform(
            post("/collections/save")
                .with(csrf())
                .header("HX-Request", "true")
                .param("name", "Test Collection"))
        .andExpect(status().isOk())
        .andExpect(content().string(""));
  }

  @Test
  void showCreateFormModal_withHxRequest_shouldReturnModalFragment() throws Exception {
    mockMvc
        .perform(get("/collections/create-modal").header("HX-Request", "true"))
        .andExpect(status().isOk())
        .andExpect(view().name("component/create-collection-modal :: modal"))
        .andExpect(model().attributeExists("collection"));
  }

  @Test
  void showEditForm_whenCollectionExists_shouldReturnEditCollectionView() throws Exception {
    Collection collection = new Collection();
    collection.setId(1L);
    collection.setName("Test Collection");

    when(collectionService.findById(1L)).thenReturn(Optional.of(collection));

    mockMvc
        .perform(get("/collections/edit/1"))
        .andExpect(status().isOk())
        .andExpect(view().name("edit-collection"))
        .andExpect(model().attributeExists("collection"));
  }

  @Test
  void showEditForm_whenCollectionDoesNotExist_shouldRedirectToHome() throws Exception {
    when(collectionService.findById(1L)).thenReturn(Optional.empty());

    mockMvc
        .perform(get("/collections/edit/1"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"));
  }

  @Test
  void update_shouldRedirectToCollections() throws Exception {
    mockMvc
        .perform(post("/collections/update/1").with(csrf()).param("name", "Updated Collection"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/collections"));
  }

  @Test
  void delete_shouldRedirectToCollections() throws Exception {
    mockMvc
        .perform(post("/collections/delete/1").with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/collections"));
  }

  @Test
  void saveNewItem_withHxRequest_whenServiceThrowsException_shouldReturnError() throws Exception {
    when(collectionService.save(any(Collection.class)))
        .thenThrow(new RuntimeException("Test Exception"));

    mockMvc
        .perform(
            post("/collections/save")
                .with(csrf())
                .header("HX-Request", "true")
                .param("name", "Test Collection"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().string("Error saving collection: Test Exception"));
  }
}
