package entities;


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
@Table(name = "Cat")
public class Cat {

    public Cat(String name, LocalDate birthday, String species, CatColor color){
        this.name = name;
        this.birthday = birthday;
        this.species = species;
        this.color = color;
        owner = null;
    }
    @Id
    @GeneratedValue
    @Getter @Setter
    private Long catId;
    @Getter @Setter
    private String name;
    @Getter @Setter
    private LocalDate birthday;
    @Getter @Setter
    private String species;
    @Getter @Setter
    @Enumerated(value = EnumType.STRING)
    private CatColor color;
    @ManyToOne @JoinColumn(name = "ownerId")
    @Getter @Setter
    private Owner owner;
    @ManyToMany
    private List<Cat> friends = new ArrayList<>();

    public List<Cat> getFriends(){ return Collections.unmodifiableList(friends); }

    public void addFriend(Cat friend) { friends.add(friend); }
    public void removeFriend(Cat friend) { friends.remove(friend); }
    public boolean hasFriend(Cat potentialFriend) {return friends.contains(potentialFriend);}
}