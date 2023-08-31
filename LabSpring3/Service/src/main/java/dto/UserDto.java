package dto;

import entities.Owner;
import entities.securitymodels.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
