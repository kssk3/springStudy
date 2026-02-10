package com.todoapp.pressentation.dto.request;

import com.todoapp.implement.validator.NullOrNotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTodoRequest {

    @NullOrNotBlank(message = "제목은 공백만 올 수 없습니다.")
    @Size(min = 1, max = 100, message = "제목은 1자 이상 100자 이하여야합니다.")
    private String title;

    @Size(max = 500, message = "설명은 500자 이하여야 합니다.")
    private String description;
}
