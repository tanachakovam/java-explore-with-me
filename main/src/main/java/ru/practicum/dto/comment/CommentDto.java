package ru.practicum.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;


import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {
    private Long id;
    private String text;
    private String author;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
}
