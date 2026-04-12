package org.example.poll_craft.model.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePollRequest {

    @NotBlank(message = "Название опроса обязательно")
    @Size(max = 255)
    private String title;

    @Size(max = 5000)
    private String description;

    @NotEmpty(message = "Должен быть хотя бы один вопрос")
    private List<QuestionDto> questions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionDto {

        @NotBlank(message = "Текст вопроса обязателен")
        private String text;

        @NotNull(message = "Тип вопроса обязателен")
        private String type;

        private Boolean required = true;

        private List<OptionDto> options;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class OptionDto {
            private String text;
        }

    }

}