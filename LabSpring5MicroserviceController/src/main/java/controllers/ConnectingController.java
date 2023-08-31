package controllers;


import entities.securitymodels.User;
import exceptions.CatNotFoundException;
import exceptions.CatsRelationshipException;
import exceptions.OwnerCatRelationshipException;
import exceptions.OwnerNotFoundException;
import messaging.RabbitMessagingClient;
import messaging.RabbitMessagingConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import records.CatFriendCat;
import records.CatOwnerLink;
import records.Id;
import utils.IAuthenticationFacade;
import wrappers.request.*;

@ComponentScan(basePackages = {"records"})
@RestController
public class ConnectingController {
    private IAuthenticationFacade authenticationFacade;
    private final RabbitMessagingClient messagingClient;

    @Autowired
    public ConnectingController(IAuthenticationFacade facade, RabbitMessagingClient client) {
        this.authenticationFacade = facade;
        this.messagingClient = client;
    }

    @PostMapping("/cats/own")
    ResponseEntity<String> wireCatToOwner(@RequestBody CatOwnerLink body)
        throws CatNotFoundException, OwnerCatRelationshipException {

        Authentication authentication = authenticationFacade.getAuthentication();
        User loggedUser = (User) authentication.getPrincipal();

        messagingClient.sendWithoutResponse(
            RabbitMessagingConfig.outcomeToCatsRoutingName,
            new CatIdAndOwnerIdAndUsernameWrapper(body.catId(), body.ownerId(), loggedUser.getUsername()),
            "wire-cat-to-owner");

        return ResponseEntity.ok(String.format("Now person %s owns cat %s", body.ownerId(), body.catId()));
    }

    @PostMapping("/cats/disown")
    ResponseEntity<String> unwireCatFromOwner(@RequestBody CatOwnerLink body)
        throws CatNotFoundException, OwnerCatRelationshipException {
        Authentication authentication = authenticationFacade.getAuthentication();
        User loggedUser = (User) authentication.getPrincipal();

        messagingClient.sendWithoutResponse(
            RabbitMessagingConfig.outcomeToCatsRoutingName,
            new CatIdAndOwnerIdAndUsernameWrapper(body.catId(), body.ownerId(), loggedUser.getUsername()),
            "unwire-cat-from-owner");

        return ResponseEntity.ok(String.format("Now person %s no more owns cat %s", body.ownerId(), body.catId()));
    }

    @PostMapping("cats/unmake-friends")
    ResponseEntity<String> unmakeFriends(@RequestBody CatFriendCat body)
        throws CatNotFoundException, CatsRelationshipException {
        Authentication authentication = authenticationFacade.getAuthentication();
        User loggedUser = (User) authentication.getPrincipal();

        messagingClient.sendWithoutResponse(
            RabbitMessagingConfig.outcomeToCatsRoutingName,
            new CatIdAndCatIdAndUsernameWrapper(body.catId1(), body.catId2(), loggedUser.getUsername()),
            "unmake-friends");

        return ResponseEntity.ok(String.format("Now cat %s and %s aren't friends anymore", body.catId1(), body.catId2()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/cats")
    ResponseEntity<String> deleteCat(@RequestBody Id id)
        throws CatNotFoundException, OwnerCatRelationshipException, CatsRelationshipException {

        messagingClient.sendWithoutResponse(
            RabbitMessagingConfig.outcomeToCatsRoutingName,
            new CatIdWrapper(id.id()),
            "delete-cat");

        return ResponseEntity.ok(String.format("Cat with id = %d has been successfully deleted", id.id()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/owners")
    ResponseEntity<String> deleteOwner(@RequestBody Id id)
        throws OwnerNotFoundException, OwnerCatRelationshipException, CatsRelationshipException {

        messagingClient.sendWithoutResponse(
            RabbitMessagingConfig.outcomeToOwnersRoutingName,
            new OwnerIdWrapper(id.id()),
            "delete-owner");

        return ResponseEntity.ok(String.format("Person with id = %d has been successfully deleted", id.id()));
    }
}
