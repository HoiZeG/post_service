package faang.school.postservice.dto.post;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private long id;
    @NotBlank(message = "Пост должен содержать текст.")
    private String content;
    @Min(value = 0, message = "Id автора должно быть положительным.")
    @NotNull(message = "Id автора не может быть пустым.")
    private Long authorId;
    @Min(value = 0, message = "Id проекта должно быть положительным.")
    @NotNull(message = "Id проекта не может быть пустым.")
    private Long projectId;
    boolean published;

    LocalDateTime publishedAt;

    LocalDateTime scheduledAt;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;
}
