package wrappers.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class CatIdAndOwnerIdWrapper {
    @Getter
    private Long catId;
    @Getter
    private Long ownerId;
}
