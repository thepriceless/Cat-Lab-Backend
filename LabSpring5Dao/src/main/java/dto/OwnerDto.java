package dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class OwnerDto {
    @Getter @Setter
    private Long OwnerId;
    @Getter @Setter
    private String Name;
    @Getter @Setter
    private LocalDate Birthday;
    @Getter @Setter
    private List<Long> Pets;
}
