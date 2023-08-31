package dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @Getter
    private Long id;
    @Getter
    private String username;
    @Getter
    private String password;
    @Getter
    private Set<Long> rolesId;
}
