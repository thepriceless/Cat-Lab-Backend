package controllers;


import entities.securitymodels.User;
import exceptions.CatNotFoundException;
import exceptions.CatsRelationshipException;
import exceptions.OwnerCatRelationshipException;
import exceptions.OwnerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import request.records.CatFriendCat;
import request.records.Id;
import request.records.CatOwnerLink;
import services.serviceImplementations.ConnectingService;
import utils.IAuthenticationFacade;

@ComponentScan(basePackages = {"services.serviceImplementations"})
@RestController
public class ConnectingController {
    private final ConnectingService service;
    private IAuthenticationFacade authenticationFacade;

    @Autowired
    public ConnectingController(ConnectingService service, IAuthenticationFacade facade) {
        this.service = service;
        this.authenticationFacade = facade;
    }

    @PostMapping("/cats/own")
    ResponseEntity<String> wireCatToOwner(@RequestBody CatOwnerLink body)
        throws CatNotFoundException, OwnerCatRelationshipException {
        Authentication authentication = authenticationFacade.getAuthentication();
        User loggedUser = (User) authentication.getPrincipal();

        service.wireCatToOwnerWithUser(body.catId(), body.ownerId(), loggedUser.getUsername());
        return ResponseEntity.ok(String.format("Now person %s owns cat %s", body.ownerId(), body.catId()));
    }

    @PostMapping("/cats/disown")
    ResponseEntity<String> unwireCatFromOwner(@RequestBody CatOwnerLink body)
        throws CatNotFoundException, OwnerCatRelationshipException {
        Authentication authentication = authenticationFacade.getAuthentication();
        User loggedUser = (User) authentication.getPrincipal();

        service.unwireCatFromOwnerWithUser(body.catId(), body.ownerId(), loggedUser.getUsername());
        return ResponseEntity.ok(String.format("Now person %s no more owns cat %s", body.ownerId(), body.catId()));
    }

    @PostMapping("cats/unmake-friends")
    ResponseEntity<String> unmakeFriends(@RequestBody CatFriendCat body)
        throws CatNotFoundException, CatsRelationshipException {
        Authentication authentication = authenticationFacade.getAuthentication();
        User loggedUser = (User) authentication.getPrincipal();

        service.unmakeFriendsWithUser(body.catId1(), body.catId2(), loggedUser.getUsername());
        return ResponseEntity.ok(String.format("Now cat %s and %s aren't friends anymore", body.catId1(), body.catId2()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/cats")
    ResponseEntity<String> deleteCat(@RequestBody Id id)
        throws CatNotFoundException, OwnerCatRelationshipException, CatsRelationshipException {

        service.deleteCat(id.id());
        return ResponseEntity.ok(String.format("Cat with id = %d has been successfully deleted", id.id()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/owners")
    ResponseEntity<String> deleteOwner(@RequestBody Id id)
        throws OwnerNotFoundException, OwnerCatRelationshipException, CatsRelationshipException {

        service.deleteOwner(id.id());
        return ResponseEntity.ok(String.format("Person with id = %d has been successfully deleted", id.id()));
    }
}
