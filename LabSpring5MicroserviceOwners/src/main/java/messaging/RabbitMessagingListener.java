package messaging;

import dto.OwnerDto;
import exceptions.NotImplementedException;
import mappers.OwnerMapper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import services.IOwnerService;
import wrappers.request.EmptyWrapper;
import wrappers.request.OwnerControllerModelWrapper;
import wrappers.request.OwnerIdWrapper;
import wrappers.request.OwnerWrapper;

import java.util.List;

@Component
@ComponentScan(basePackages = {
    "messaging",
    "services"
})
@RabbitListener(queues = "${queue.name.with-owners}")
public class RabbitMessagingListener {
    private final IOwnerService service;

    @Autowired
    RabbitMessagingListener(IOwnerService service) {
        this.service = service;
    }

    @RabbitHandler
    public OwnerDto OwnerHandler(@Header("keyHeader") String keyHeader, @Payload OwnerControllerModelWrapper requestBodyWrapper) {
        switch (keyHeader) {
            case "create-owner":
                return service.addOwner(OwnerMapper.ownerWrapperAsModel(requestBodyWrapper));
        }

        throw NotImplementedException.NoImplementation();
    }

    @RabbitHandler
    public List<OwnerDto> EmptyBodyHandler(@Header("keyHeader") String keyHeader, @Payload EmptyWrapper requestBodyWrapper) {
        switch (keyHeader) {
            case "get-all-owners":
                return service.getAllOwners();
        }

        throw NotImplementedException.NoImplementation();
    }

    @RabbitHandler
    public OwnerDto OwnerHandler(@Header("keyHeader") String keyHeader, @Payload OwnerWrapper requestBodyWrapper) {
        switch (keyHeader) {
            case "save-owner":
                return service.saveOwner(new OwnerDto(
                    requestBodyWrapper.getOwnerId(),
                    requestBodyWrapper.getName(),
                    requestBodyWrapper.getBirthday(),
                    requestBodyWrapper.getPets()));
        }

        throw NotImplementedException.NoImplementation();
    }

    @RabbitHandler
    public OwnerDto OwnerControllerModelHandler(@Header("keyHeader") String keyHeader, @Payload OwnerIdWrapper requestBodyWrapper) {
        switch (keyHeader) {
            case "get-one-owner":
                return service.getOwnerDtoById(requestBodyWrapper.getOwnerId());

            case "delete-owner":
                service.deleteOwner(requestBodyWrapper.getOwnerId());
                return new OwnerDto();
        }

        throw NotImplementedException.NoImplementation();
    }
}
