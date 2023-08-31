package wrappers.request;

import dto.CatDto;
import enums.ServiceCatColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class CatWrapper {
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
