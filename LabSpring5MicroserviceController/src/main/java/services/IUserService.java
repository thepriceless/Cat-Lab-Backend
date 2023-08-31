package services;

import controllermodels.UserControllerModel;
import dto.RoleDto;
import dto.UserDto;
import entities.securitymodels.Role;
import entities.securitymodels.User;
import exceptions.UserAlreadyExistsException;

import java.util.List;
import java.util.Set;

public interface IUserService {
    User findUserById(Long id);
    public User findUserByUsername(String username);
    List<User> allUsers();
    UserDto saveUserWithLinkedOwner(UserControllerModel userModel) throws UserAlreadyExistsException;
    public Set<Role> getRolesByIds(Set<Long> ids);
    boolean deleteUser(Long id);
}
