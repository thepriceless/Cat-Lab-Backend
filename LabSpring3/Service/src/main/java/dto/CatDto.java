package dto;

import enums.ServiceCatColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
public class CatDto {
    @Getter
    private Long CatId;
    @Getter
    private String Name;
    @Getter
    private LocalDate Birthday;
    @Getter
    private String Species;
    @Getter
    private ServiceCatColor Color;
    @Getter
    private Long OwnerId;
    @Getter
    private List<Long> Friends;
}
