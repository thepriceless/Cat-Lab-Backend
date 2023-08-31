package controllers;

import controllermodels.OwnerControllerModel;
import dto.OwnerDto;
import entities.Owner;
import exceptions.OwnerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import services.IOwnerService;

import java.util.List;

@ComponentScan(basePackages = {"services.serviceImplementations"})
@RestController
public class OwnerController {
    private final IOwnerService service;

    @Autowired
    public OwnerController(IOwnerService service) {
        this.service = service;
    }

    @GetMapping("/owners")
    public List<OwnerDto> all() {
        return service.getAllOwners();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/owners")
    OwnerDto newOwner(@RequestBody OwnerControllerModel model) {
        return service.addOwner(model);
    }

    @GetMapping("/owners/{id}")
    public OwnerDto one(@PathVariable Long id) throws OwnerNotFoundException {
        return service.getOwnerById(id);
    }
}
