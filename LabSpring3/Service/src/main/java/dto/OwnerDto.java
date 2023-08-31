package dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
public class OwnerDto {
    @Getter
    private Long OwnerId;
    @Getter
    private String Name;
    @Getter
    private LocalDate Birthday;
    @Getter
    private List<Long> Pets;
}
