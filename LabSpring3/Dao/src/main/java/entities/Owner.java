package entities;

import entities.securitymodels.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Entity
@NoArgsConstructor
@Table(name = "Owner")
public class Owner {
    public Owner(String name, LocalDate birthday){
        pets = new ArrayList<>();

        this.name = name;
        this.birthday = birthday;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter
    private Long ownerId;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private LocalDate birthday;
    @OneToMany(mappedBy = "owner")
    private List<Cat> pets;
    public List<Cat> getPets() { return Collections.unmodifiableList(pets); }
    public void addCat(Cat newCat) {
        pets.add(newCat);
    }
    public void removeCat(Cat cat) {pets.remove(cat);}
    public boolean ownsCat(Cat cat) {return pets.contains(cat);}
}
