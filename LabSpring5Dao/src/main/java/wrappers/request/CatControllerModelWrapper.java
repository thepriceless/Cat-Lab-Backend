package wrappers.request;

import entities.CatColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
public class CatControllerModelWrapper {
    @Getter
    private String name;
    @Getter
    private CatColor color;
    @Getter
    private LocalDate birthday;
    @Getter
    private String species;
}
