package dto;

import enums.ServiceCatColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class CatDto {
    @Getter @Setter
    private Long CatId;
    @Getter @Setter
    private String Name;
    @Getter @Setter
    private LocalDate Birthday;
    @Getter @Setter
    private String Species;
    @Getter @Setter
    private ServiceCatColor Color;
    @Getter @Setter
    private Long OwnerId;
    @Getter @Setter
    private List<Long> Friends;
}
