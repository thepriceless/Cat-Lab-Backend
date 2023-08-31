package repositories;


import entities.Cat;
import entities.CatColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatRepository extends JpaRepository<Cat, Long> {
    List<Cat> findByOwnerIsNull();
    List<Cat> findByOwner_OwnerId(Long id);
    List<Cat> findByColorEqualsAndOwner_OwnerId(CatColor colorName, Long id);
}
