package wrappers.request;

import entities.securitymodels.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
public class RolesSetWrapper {
    @Getter
    public Set<Role> roles;
}
