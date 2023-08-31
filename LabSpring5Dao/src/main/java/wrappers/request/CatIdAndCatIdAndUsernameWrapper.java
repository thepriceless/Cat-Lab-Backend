package wrappers.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class CatIdAndCatIdAndUsernameWrapper {
    @Getter
    private Long catId1;
    @Getter
    private Long catId2;
    @Getter
    private String username;
}
