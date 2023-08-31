package controllers;

import controllermodels.UserControllerModel;
import exceptions.RegistrationException;
import exceptions.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import services.IUserService;

@Controller
@ComponentScan(basePackages = {"services"})
public class RegistrationController {
    private final IUserService userService;

    @Autowired
    public RegistrationController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registration")
    public ResponseEntity<String> registerUserAccount(@RequestBody UserControllerModel model) throws RegistrationException, UserAlreadyExistsException {
        if (!model.getPassword().equals(model.getMatchingPassword())) {
            throw RegistrationException.PasswordDontMatch();
        }

        userService.saveUserwithLinkedOwner(model);

        return ResponseEntity.ok("Successful registration");
    }
}
