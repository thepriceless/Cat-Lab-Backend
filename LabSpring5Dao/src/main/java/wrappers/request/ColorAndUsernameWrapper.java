package wrappers.request;

import entities.CatColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ColorAndUsernameWrapper {
    @Getter
    String color;
    @Getter
    String username;
}
