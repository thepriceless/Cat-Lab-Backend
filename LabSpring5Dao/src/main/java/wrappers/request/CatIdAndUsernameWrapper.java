package wrappers.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class CatIdAndUsernameWrapper {
    @Getter
    private Long catId;
    @Getter
    private String username;
}
