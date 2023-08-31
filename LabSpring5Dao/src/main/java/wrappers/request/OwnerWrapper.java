package wrappers.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class OwnerWrapper {
    @Getter
    private Long OwnerId;
    @Getter
    private String Name;
    @Getter
    private LocalDate Birthday;
    @Getter
    private List<Long> Pets;
}
