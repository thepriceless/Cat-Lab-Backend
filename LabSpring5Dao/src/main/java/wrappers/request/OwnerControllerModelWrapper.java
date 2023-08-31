package wrappers.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
public class OwnerControllerModelWrapper {
    @Getter @Setter
    private String name;
    @Getter @Setter
    private LocalDate birthday;
}
