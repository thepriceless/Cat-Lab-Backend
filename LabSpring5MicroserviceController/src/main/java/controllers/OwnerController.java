package controllers;

import controllermodels.OwnerControllerModel;
import dto.OwnerDto;
import exceptions.OwnerNotFoundException;
import messaging.RabbitMessagingClient;
import messaging.RabbitMessagingConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import wrappers.request.EmptyWrapper;
import wrappers.request.OwnerControllerModelWrapper;
import wrappers.request.OwnerIdWrapper;
import java.util.List;

@ComponentScan(basePackages = {})
@RestController
public class OwnerController {
    private final RabbitMessagingClient messagingClient;
    @Autowired
    public OwnerController(RabbitMessagingClient client) {
        this.messagingClient = client;
    }

    @GetMapping("/owners")
    public List<OwnerDto> all() {

        return messagingClient.sendAndReceive(
            RabbitMessagingConfig.outcomeToOwnersRoutingName,
            new EmptyWrapper(""),
            List.class,
            "get-all-owners");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/owners")
    OwnerDto newOwner(@RequestBody OwnerControllerModel model) {

        return messagingClient.sendAndReceive(
            RabbitMessagingConfig.outcomeToOwnersRoutingName,
            new OwnerControllerModelWrapper(model.getName(), model.getBirthday()),
            OwnerDto.class,
            "create-owner");
    }

    @GetMapping("/owners/{id}")
    public OwnerDto one(@PathVariable Long id) throws OwnerNotFoundException {

        return messagingClient.sendAndReceive(
            RabbitMessagingConfig.outcomeToOwnersRoutingName,
            new OwnerIdWrapper(id),
            OwnerDto.class,
            "get-one-owner");
    }
}
