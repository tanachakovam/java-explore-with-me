package ru.practicum.dto.compilation;

import lombok.Builder;
import lombok.Data;


import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Builder
public class NewCompilationDto {
    private List<Long> events;
    private Boolean pinned;
    @NotBlank
    private String title;
}
