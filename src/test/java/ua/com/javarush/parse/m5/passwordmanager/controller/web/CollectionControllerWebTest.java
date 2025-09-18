package ua.com.javarush.parse.m5.passwordmanager.controller.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import ua.com.javarush.parse.m5.passwordmanager.entity.Collection;
import ua.com.javarush.parse.m5.passwordmanager.service.CollectionService;

@WebMvcTest(CollectionControllerWeb.class)
@Import(CollectionControllerWebTest.TestConfig.class)
class CollectionControllerWebTest {

  @TestConfiguration
  static class TestConfig {
    @Bean
    public CollectionService collectionService() {
      return mock(CollectionService.class);
    }
  }

  @Autowired private MockMvc mockMvc;

  @Autowired private CollectionService collectionService;

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
    Collection collection = new Collection();
    collection.setName("Test Collection");
    when(collectionService.save(any(Collection.class))).thenReturn(collection);

    mockMvc
        .perform(
            post("/collections/save")
                .param("name", "Test Collection")
                .param("description", "Test Description")
                .param("color", "#FFFFFF")
                .param("icon", "test-icon"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"));
  }

  @Test
  void showEditForm_shouldReturnEditCollectionView() throws Exception {
    Collection existingCollection = new Collection();
    existingCollection.setId(1L);
    existingCollection.setName("Existing Collection");
    existingCollection.setDescription("Existing Description");
    existingCollection.setColor("#000000");
    existingCollection.setIcon("existing-icon");

    CollectionControllerWeb.CollectionForm form = new CollectionControllerWeb.CollectionForm();
    form.setId(existingCollection.getId());
    form.setName(existingCollection.getName());
    form.setDescription(existingCollection.getDescription());
    form.setColor(existingCollection.getColor());
    form.setIcon(existingCollection.getIcon());

    when(collectionService.findById(anyLong())).thenReturn(Optional.of(existingCollection));

    mockMvc
        .perform(get("/collections/edit/1"))
        .andExpect(status().isOk())
        .andExpect(view().name("edit-collection"))
        .andExpect(model().attributeExists("collection"))
        .andExpect(model().attribute("collection", form));
  }

  @Test
  void showEditForm_shouldRedirectToHomeIfNotFound() throws Exception {
    when(collectionService.findById(anyLong())).thenReturn(Optional.empty());

    mockMvc
        .perform(get("/collections/edit/1"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"));
  }

  @Test
  void update_shouldRedirectToCollections() throws Exception {
    Collection updatedCollection = new Collection();
    updatedCollection.setId(1L);
    updatedCollection.setName("Updated Collection");
    when(collectionService.update(anyLong(), any(Collection.class)))
        .thenReturn(Optional.of(updatedCollection));

    mockMvc
        .perform(
            post("/collections/update/1")
                .param("id", "1")
                .param("name", "Updated Collection")
                .param("description", "Updated Description")
                .param("color", "#AAAAAA")
                .param("icon", "updated-icon"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/collections"));
  }

  @Test
  void delete_shouldRedirectToCollections() throws Exception {
    mockMvc
        .perform(post("/collections/delete/1"))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/collections"));
  }
}
