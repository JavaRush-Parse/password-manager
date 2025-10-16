package ua.com.javarush.parse.m5.passwordmanager.dto;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> implements Serializable {
  private List<T> content;
  private int currentPage;
  private int totalPages;
  private long totalElements;
  private int pageSize;
  private boolean first;
  private boolean last;
  private boolean empty;

  public static <T> PageResponse<T> of(Page<T> page) {
    return PageResponse.<T>builder()
        .content(page.getContent())
        .currentPage(page.getNumber())
        .totalPages(page.getTotalPages())
        .totalElements(page.getTotalElements())
        .pageSize(page.getSize())
        .first(page.isFirst())
        .last(page.isLast())
        .empty(page.isEmpty())
        .build();
  }
}
