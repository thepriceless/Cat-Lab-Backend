package messaging;

import dto.UserDto;
import exceptions.NotImplementedException;
import mappers.UserMapper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import services.IUserService;
import wrappers.request.UsernameWrapper;

@Component
@ComponentScan(basePackages = {
    "messaging",
    "services"
})
@RabbitListener(queues = "${queue.name.income}")
public class RabbitMessagingListener {
    private final IUserService service;

    @Autowired
    RabbitMessagingListener(IUserService service) {
        this.service = service;
    }

    @RabbitHandler
    public UserDto UserIdHandler(@Header("keyHeader") String keyHeader, @Payload UsernameWrapper requestBodyWrapper) {
        switch (keyHeader) {
            case "get-user":
                return UserMapper.userAsDto(service.findUserByUsername(requestBodyWrapper.getUsername()));
        }

        throw NotImplementedException.NoImplementation();
    }

    /*@RabbitHandler
    public Set<RoleDto> RolesHandler(@Header("keyHeader") String keyHeader, @Payload RolesSetWrapper requestBodyWrapper) {
        switch (keyHeader) {
            case "get-roles":
                return service.getRolesByIds(
                    requestBodyWrapper.getRoles().stream().map(Role::getId).collect(Collectors.toSet()))
                    .stream().map(RoleMapper::roleAsDto).collect(Collectors.toSet());
        }

        throw NotImplementedException.NoImplementation();
    }*/
}
