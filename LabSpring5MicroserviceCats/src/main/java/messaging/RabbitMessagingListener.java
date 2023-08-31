package messaging;

import controllermodels.CatControllerModel;
import dto.CatDto;
import exceptions.NotImplementedException;
import mappers.CatMapper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import services.ICatService;
import wrappers.request.*;

import java.util.List;

@Component
@ComponentScan(basePackages = {
    "messaging",
    "services"
})
@RabbitListener(queues = "${queue.name.with-cats}")
public class RabbitMessagingListener {
    private final ICatService service;

    @Autowired
    RabbitMessagingListener(ICatService service) {
        this.service = service;
    }

    @RabbitHandler
    public List<CatDto> UsernameHandler(@Header("keyHeader") String keyHeader, @Payload UsernameWrapper requestBodyWrapper) {
        System.out.println(keyHeader);
        switch (keyHeader) {
            case "get-all-cats":
                return service.getAllCatsByUser(requestBodyWrapper.getUsername());
        }

        throw NotImplementedException.NoImplementation();
    }

    @RabbitHandler
    public List<CatDto> ColorAndUsernameHandler(@Header("keyHeader") String keyHeader, @Payload ColorAndUsernameWrapper requestBodyWrapper) {
        switch (keyHeader) {
            case "get-all-cats-color":
                return service.getAllCatsByColor(requestBodyWrapper.getColor(), requestBodyWrapper.getUsername());
        }

        throw NotImplementedException.NoImplementation();
    }

    @RabbitHandler
    public List<CatDto> EmptyBodyHandler(@Header("keyHeader") String keyHeader, @Payload EmptyWrapper requestBodyWrapper) {
        switch (keyHeader) {
            case "get-all-cats-free":
                return service.getFreeCats();
        }

        throw NotImplementedException.NoImplementation();
    }

    @RabbitHandler
    public CatDto CatIdAndUsernameHandler(@Header("keyHeader") String keyHeader, @Payload CatIdAndUsernameWrapper requestBodyWrapper) {
        switch (keyHeader) {
            case "get-one-cat":
                return service.getCatByIdWithUser(requestBodyWrapper.getCatId(), requestBodyWrapper.getUsername());
        }

        throw NotImplementedException.NoImplementation();
    }

    @RabbitHandler
    public CatDto CatControllerModelHandler(@Header("keyHeader") String keyHeader, @Payload CatControllerModelWrapper requestBodyWrapper) {
        switch (keyHeader) {
            case "create-cat":
                return service.addCat(new CatControllerModel(
                    requestBodyWrapper.getName(),
                    requestBodyWrapper.getColor(),
                    requestBodyWrapper.getBirthday(),
                    requestBodyWrapper.getSpecies()));
        }

        throw NotImplementedException.NoImplementation();
    }

    @RabbitHandler
    public CatDto CatIdHandler(@Header("keyHeader") String keyHeader, @Payload CatIdWrapper requestBodyWrapper) {
        switch (keyHeader) {
            case "delete-cat":
                service.deleteCat(requestBodyWrapper.getId());
                return new CatDto();

            case "get-cat-by-id-mock":
                return service.getCatById(requestBodyWrapper.getId());
        }

        throw NotImplementedException.NoImplementation();
    }

    @RabbitHandler
    public CatDto CatWrapperHandler(@Header("keyHeader") String keyHeader, @Payload CatWrapper requestBodyWrapper) {
        switch (keyHeader) {
            case "save-cat":
                return service.saveCat(new CatDto(
                    requestBodyWrapper.getCatId(),
                    requestBodyWrapper.getName(),
                    requestBodyWrapper.getBirthday(),
                    requestBodyWrapper.getSpecies(),
                    requestBodyWrapper.getColor(),
                    requestBodyWrapper.getOwnerId(),
                    requestBodyWrapper.getFriends()));
        }

        throw NotImplementedException.NoImplementation();
    }

    @RabbitHandler
    public void CatIdAndCatIdAndUsernameHandler(@Header("keyHeader") String keyHeader, @Payload CatIdAndCatIdAndUsernameWrapper requestBodyWrapper) {
        switch (keyHeader) {
            case "make-friends":
                service.makeFriendsWithUser(
                    requestBodyWrapper.getCatId1(),
                    requestBodyWrapper.getCatId2(),
                    requestBodyWrapper.getUsername());

            case "unmake-friends":
                service.unmakeFriendsWithUser(
                    requestBodyWrapper.getCatId1(),
                    requestBodyWrapper.getCatId2(),
                    requestBodyWrapper.getUsername());
        }

        throw NotImplementedException.NoImplementation();
    }

    @RabbitHandler
    public void CatIdAndOwnerIdAndUsernameHandler(@Header("keyHeader") String keyHeader, @Payload CatIdAndOwnerIdAndUsernameWrapper requestBodyWrapper) {
        switch (keyHeader) {
            case "wire-cat-to-owner":
                service.wireCatToOwnerWithUser(
                    requestBodyWrapper.getCatId(),
                    requestBodyWrapper.getOwnerId(),
                    requestBodyWrapper.getUsername());

            case "unwire-cat-from-owner":
                service.unwireCatFromOwnerWithUser(
                    requestBodyWrapper.getCatId(),
                    requestBodyWrapper.getOwnerId(),
                    requestBodyWrapper.getUsername());
        }

        throw NotImplementedException.NoImplementation();
    }
}
