package de.mischok.academy.skiller.service;

import de.mischok.academy.skiller.domain.Person;
import de.mischok.academy.skiller.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    public List<Person> getAllPersonsWithComment() {
        return personRepository.findAllPersonsWithComments();
    }


    // das war die erste Funktion, die ich bearbeitet habe. Habe hier mal ausprobiert, Examples zu verwenden; danach habe ich der Einfachheit halber zu SQL-Queries gewechselt.
    public List<Person> getAllPersonsFromCountries(String...countries) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("country", ExampleMatcher.GenericPropertyMatcher::exact)
                .withIgnorePaths("id", "firstName", "lastName", "email", "university", "comment", "birthday");

        List<Person> persons = new java.util.ArrayList<>();

        for (String country : countries) {
            Person person = new Person();
            person.setCountry(country);
            persons.addAll(personRepository.findAll(Example.of(person, matcher)));
        }
        return persons;
    }

    public List<String> getAllCountries() {
        List<String> countries = new java.util.ArrayList<>();
        for (Person person : getAllPersons()) {
            if(!countries.contains(person.getCountry())) {
                countries.add(person.getCountry());
            }
        }
        countries.remove(null);
        return countries;
    }

    public List<Person> getAllPersonsBornInTheEighties() {
        return personRepository.findAllPersonsBornInTheEighties();
    }

    public Map<String, Long> getPersonCountByCountry() {
        Map<String, Long> countries = new java.util.HashMap<>();
        for (Person person : getAllPersons()) {
            if(!countries.containsKey(person.getCountry())) {
                countries.put(person.getCountry(), 1L);
            } else {
                countries.put(person.getCountry(), countries.get(person.getCountry()) + 1L);
            }
        }
        countries.remove(null);
        return countries;
    }

    public void deleteAll() {
        personRepository.deleteAll();
    }
}
