package mappers;

import dto.RoleDto;
import entities.securitymodels.Role;

import java.util.HashSet;

public class RoleMapper {
    public static RoleDto roleAsDto(Role role) {
        return new RoleDto(
            role.getId(),
            role.getName(),
            new HashSet<>());
    }

    public static Role roleDtoAsRole(RoleDto dto) {
        return new Role(dto.getId(), dto.getName());
    }
}
