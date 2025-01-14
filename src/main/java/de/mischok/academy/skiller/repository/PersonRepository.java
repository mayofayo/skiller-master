package de.mischok.academy.skiller.repository;

import de.mischok.academy.skiller.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    @Query("SELECT p FROM Person p WHERE p.comment IS NOT NULL AND TRIM(p.comment) <> ''")
    List<Person> findAllPersonsWithComments();

    @Query("SELECT p FROM Person p WHERE p.birthday >= '1980-01-01 00:00:00' AND p.birthday < '1990-01-01 00:00:00'")
    List<Person> findAllPersonsBornInTheEighties();

}
