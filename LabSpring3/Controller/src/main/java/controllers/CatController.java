package controllers;

import controllermodels.CatControllerModel;
import dto.CatDto;
import entities.Cat;
import entities.securitymodels.User;
import exceptions.AccessDeniedException;
import exceptions.CatNotFoundException;
import exceptions.CatsRelationshipException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import request.records.CatFriendCat;
import services.serviceImplementations.CatService;
import utils.IAuthenticationFacade;

import java.security.Principal;
import java.util.List;


@ComponentScan(basePackages = {"services.serviceImplementations", "utils"})
@RestController
public class CatController {
    private final CatService service;

    private IAuthenticationFacade authenticationFacade;

    @Autowired
    public CatController(CatService service, IAuthenticationFacade facade) {
        this.service = service;
        this.authenticationFacade = facade;
    }

    @GetMapping("/start")
    public String start(){
        return "Hello";
    }

    @GetMapping("/cats")
    public List<CatDto> all() {
        Authentication authentication = authenticationFacade.getAuthentication();
        User loggedUser = (User) authentication.getPrincipal();

        return service.getAllCatsByUser(loggedUser.getUsername());
    }

    @GetMapping("/cats/color/{color}")
    public List<CatDto> allFilteredByColor(@PathVariable String color) {
        Authentication authentication = authenticationFacade.getAuthentication();
        User loggedUser = (User) authentication.getPrincipal();

        return service.getAllCatsByColor(color, loggedUser.getUsername());
    }

    @GetMapping("/cats/free")
    public List<CatDto> allFree() {
        return service.getFreeCats();
    }

    @GetMapping("/cats/{catId}")
    public CatDto one(@PathVariable Long catId) throws CatNotFoundException, AccessDeniedException {
        Authentication authentication = authenticationFacade.getAuthentication();
        User loggedUser = (User) authentication.getPrincipal();

        return service.getCatByIdWithUser(catId, loggedUser.getUsername());
    }

    @PostMapping("/cats")
    @PreAuthorize("hasRole('ADMIN')")
    CatDto newCat(@RequestBody CatControllerModel model) {
        return service.addCat(model);
    }

    @PostMapping("/cats/make-friends")
    ResponseEntity<String> makeFriends(@RequestBody CatFriendCat body) throws CatNotFoundException, CatsRelationshipException {
        Authentication authentication = authenticationFacade.getAuthentication();
        User loggedUser = (User) authentication.getPrincipal();

        service.makeFriendsWithUser(body.catId1(), body.catId2(), loggedUser.getUsername());
        return ResponseEntity.ok(String.format("Now cats %s and %s are friends", body.catId1(), body.catId2()));
    }
}
