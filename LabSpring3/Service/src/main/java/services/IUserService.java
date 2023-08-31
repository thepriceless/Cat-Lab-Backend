package services;

import controllermodels.UserControllerModel;
import entities.securitymodels.User;
import exceptions.UserAlreadyExistsException;

import java.util.List;

public interface IUserService {
    User findUserById(Long id);

    List<User> allUsers();

    void saveUserwithLinkedOwner(UserControllerModel userModel) throws UserAlreadyExistsException;

    boolean deleteUser(Long id);
}
