package controllermodels;

import entities.CatColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
public class CatControllerModel {
    @Getter @Setter
    private String name;
    @Getter @Setter
    private CatColor color;
    @Getter @Setter
    private LocalDate birthday;
    @Getter @Setter
    private String species;
}
