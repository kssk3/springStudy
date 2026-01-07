package test;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TodoCreateRequest {

    private String title;
    private String description;

}
