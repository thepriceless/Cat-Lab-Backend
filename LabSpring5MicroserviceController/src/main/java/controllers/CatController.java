package controllers;

import controllermodels.CatControllerModel;
import dto.CatDto;
import entities.Cat;
import entities.securitymodels.User;
import exceptions.AccessDeniedException;
import exceptions.CatNotFoundException;
import exceptions.CatsRelationshipException;
import messaging.RabbitMessagingClient;
import messaging.RabbitMessagingConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import records.CatFriendCat;
import utils.IAuthenticationFacade;
import wrappers.request.*;

import java.util.List;

@ComponentScan(basePackages = {"services", "utils"})
@RestController
public class CatController {
    private IAuthenticationFacade authenticationFacade;
    private final RabbitMessagingClient messagingClient;

    @Autowired
    public CatController(IAuthenticationFacade facade, RabbitMessagingClient client) {
        this.authenticationFacade = facade;
        this.messagingClient = client;
    }

    @GetMapping("/start")
    public String start(){
        return "Hello";
    }

    @GetMapping("/cats")
    public List<CatDto> all() {
        Authentication authentication = authenticationFacade.getAuthentication();
        User loggedUser = (User) authentication.getPrincipal();

        return messagingClient.sendAndReceive(
            RabbitMessagingConfig.outcomeToCatsRoutingName,
            new UsernameWrapper(loggedUser.getUsername()),
            List.class,
            "get-all-cats");
    }

    @GetMapping("/cats/color/{color}")
    public List<CatDto> allFilteredByColor(@PathVariable String color) {
        Authentication authentication = authenticationFacade.getAuthentication();
        User loggedUser = (User) authentication.getPrincipal();

        return messagingClient.sendAndReceive(
            RabbitMessagingConfig.outcomeToCatsRoutingName,
            new ColorAndUsernameWrapper(color, loggedUser.getUsername()),
            List.class,
            "get-all-cats-color");
    }

    @GetMapping("/cats/free")
    public List<CatDto> allFree() {

        return messagingClient.sendAndReceive(
            RabbitMessagingConfig.outcomeToCatsRoutingName,
            new EmptyWrapper(""),
            List.class,
            "get-all-cats-free");
    }

    @GetMapping("/cats/{catId}")
    public CatDto one(@PathVariable Long catId) throws CatNotFoundException, AccessDeniedException {
        Authentication authentication = authenticationFacade.getAuthentication();
        User loggedUser = (User) authentication.getPrincipal();

        return messagingClient.sendAndReceive(
            RabbitMessagingConfig.outcomeToCatsRoutingName,
            new CatIdAndUsernameWrapper(catId, loggedUser.getUsername()),
            CatDto.class,
            "get-one-cat");
    }

    @PostMapping("/cats")
    @PreAuthorize("hasRole('ADMIN')")
    CatDto newCat(@RequestBody CatControllerModel model) {

        return messagingClient.sendAndReceive(
            RabbitMessagingConfig.outcomeToCatsRoutingName,
            new CatControllerModelWrapper(model.getName(), model.getColor(), model.getBirthday(), model.getSpecies()),
            CatDto.class,
            "create-cat");
    }

    @PostMapping("/cats/make-friends")
    ResponseEntity<String> makeFriends(@RequestBody CatFriendCat body) throws CatNotFoundException, CatsRelationshipException {
        Authentication authentication = authenticationFacade.getAuthentication();
        User loggedUser = (User) authentication.getPrincipal();

        messagingClient.sendWithoutResponse(
            RabbitMessagingConfig.outcomeToCatsRoutingName,
            new CatIdAndCatIdAndUsernameWrapper(body.catId1(), body.catId2(), loggedUser.getUsername()),
            "make-friends");

        return ResponseEntity.ok(String.format("Now cats %s and %s are friends", body.catId1(), body.catId2()));
    }
}
