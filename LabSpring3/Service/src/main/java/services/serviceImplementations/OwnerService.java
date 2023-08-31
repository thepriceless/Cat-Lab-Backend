package services.serviceImplementations;


import controllermodels.OwnerControllerModel;
import dto.OwnerDto;
import entities.Owner;
import exceptions.OwnerNotFoundException;
import mappers.OwnerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import repositories.OwnerRepository;
import services.IOwnerService;


import java.util.List;
import java.util.stream.Collectors;

@Component
public class OwnerService implements IOwnerService {
    private final OwnerRepository repository;

    @Autowired
    public OwnerService(OwnerRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<OwnerDto> getAllOwners() {
        return repository.findAll().stream().map(OwnerMapper::ownerAsDto).collect(Collectors.toList());
    }

    @Override
    public OwnerDto addOwner(@NonNull OwnerControllerModel ownerModel) {
        Owner newOwner = new Owner(ownerModel.getName(), ownerModel.getBirthday());

        repository.save(newOwner);

        return OwnerMapper.ownerAsDto(newOwner);
    }

    @Override
    public OwnerDto getOwnerById(@NonNull Long id) throws OwnerNotFoundException {
        Owner target = repository.findById(id).orElseThrow(() -> OwnerNotFoundException.NoOwnerById(id));

        return OwnerMapper.ownerAsDto(target);
    }
}
