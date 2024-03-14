package com.naloga.daniimdb.actor;

import com.naloga.daniimdb.actor.repository.ActorRepository;
import com.naloga.daniimdb.actor.services.ActorService;
import com.naloga.daniimdb.movie.Movie;
import com.naloga.daniimdb.movie.repository.MovieRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.List;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@ActiveProfiles("test")
public class ActorServiceUnitTest {

    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private MovieRepository movieRepository;

    private ActorService actorService;

    @BeforeEach
    void setUp() {
        actorService = new ActorService(actorRepository);
    }

    // or I can use manually later on hibernate-create or hibernate-validate in spring.jpa.hibernate.ddl-auto= at application.properties
    @BeforeEach
    void resetDatabase() {
        actorRepository.deleteAll();
        movieRepository.deleteAll();
    }

    @Test
    void testGetAllActors() {
        Long firstId = Long.valueOf(1);
        Long secondId = Long.valueOf(2);

        Actor no1 = new Actor(firstId, "First", "Actor", "01.04.1998");
        Actor no2 = new Actor(secondId, "Second", "Actor", "02.05.1981");

        actorService.createActor(no1);
        actorService.createActor(no2);

        // I am retrieving all actors from the H2 database
        List<Actor> allActors = actorService.getAllActors();

        // I expect to be 2 actors in the db
        assertEquals(2, allActors.size());
    }
    @Test
    void testSearchActorsByName() {
        Long firstId = Long.valueOf(1);
        Long secondId = Long.valueOf(2);

        String firstName = "Jernej";
        String lastName = "Sugman";

        Actor no1 = new Actor(firstId, "First", "Actor", "01.01.1998");
        Actor no2 = new Actor(secondId, "Jernej", "Sugman", "23.12.1968");

        actorRepository.save(no1);
        actorRepository.save(no2);

        // I find just Jernej Sugman
        List<Actor> actualActors = actorService.searchActorsByName(firstName, lastName);
        assertEquals(1, actualActors.size());
        assertEquals(firstName, actualActors.get(0).getFirstName());
        assertEquals(lastName, actualActors.get(0).getLastName());
        assertEquals("23.12.1968", actualActors.get(0).getBornDate());
    }

    @Test
    void testGetActorsWithPagination() {
        Long actorId = Long.valueOf(1);

        List<Movie> moviesSugman = Arrays.asList(
                new Movie(12132L, "Nasa mala klinika", 2000, "Komedija"),
                new Movie(12133L, "Preseren", 2005, "Podobitev slavne osebnosti"),
                new Movie(12134L, "Predmestje", 2010, "Drama")
        );

        movieRepository.saveAll(moviesSugman);

        Actor actor = new Actor(actorId, "Jernej", "Sugman", "23.12.1968", moviesSugman);

        actorRepository.save(actor);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Actor> actorsPage = actorService.getActors(pageable);
        List<Actor> actors = actorsPage.getContent();

        assertEquals(1, actors.size());
        assertEquals("Jernej", actors.get(0).getFirstName());
        assertEquals("Sugman", actors.get(0).getLastName());
        assertEquals("23.12.1968", actors.get(0).getBornDate());
        assertEquals(moviesSugman.size(), actors.get(0).getMovies().size());
    }

    // I check the values under correct id's for instance
    @Test
    void testGetActorsById() {
        List<Actor> actorsToSave = Arrays.asList(
                new Actor(Long.valueOf(1), "Al", "Bundy", "07.11.1948", null),
                new Actor(Long.valueOf(2), "Al", "Pacino", "25.04.1940", null),
                new Actor(Long.valueOf(3), "Joe", "Pesci", "09.02.1943", null),
                new Actor(Long.valueOf(4), "Matjaz", "Javsnik", "19.11.1969", null),
                new Actor(Long.valueOf(5), "Jernej", "Sugman", "23.12.1968", null),
                new Actor(Long.valueOf(6), "Boris", "Cavazza", "02.02.1939", null)
        );

        actorRepository.saveAll(actorsToSave);

        if(actorRepository.existsById(Long.valueOf(2)) && actorRepository.findById(Long.valueOf(4)).get().getFirstName().equals("Al")) {
            assertEquals("Pacino", actorRepository.findById(Long.valueOf(2)).get().getLastName());
        }
        if(actorRepository.existsById(Long.valueOf(4)) && actorRepository.findById(Long.valueOf(4)).get().getFirstName().equals("Matjaz")) {
            assertEquals("Javsnik", actorRepository.findById(Long.valueOf(4)).get().getLastName());
        }
        if(actorRepository.existsById(Long.valueOf(6)) && actorRepository.findById(Long.valueOf(4)).get().getFirstName().equals("Boris")) {
            assertEquals("Cavazza", actorRepository.findById(Long.valueOf(6)).get().getLastName());
        }
    }

    // Now I am testing CRUD operations:
    @Test
    void testCreateActor() {
        Long actorId = Long.valueOf(1);
        Actor actorBeingCreated = new Actor(actorId, "Sylvester", "Stallone", "06.07.1946");

        Actor createdActor = actorService.createActor(actorBeingCreated);

        assertNotNull(createdActor.getId());
        assertEquals("Sylvester", createdActor.getFirstName());
        assertEquals("Stallone", createdActor.getLastName());
        assertEquals("06.07.1946", createdActor.getBornDate());
    }

    @Test
    void testUpdateActor() {
        // Create three actors
        Actor actor1 = new Actor(Long.valueOf(1), "Al", "Bundy", "07.11.1948");
        Actor actor2 = new Actor(Long.valueOf(2), "Al", "Pacino", "25.04.1940");
        Actor actor3 = new Actor(Long.valueOf(3), "Joe", "Peschi", "09.02.1943");

        actorService.createActor(actor1);
        actorService.createActor(actor2);
        actorService.createActor(actor3);

        // I am testing value for update of typemistake at actor Peschi should be Pesci
        actor3.setLastName("Pesci");

        actorService.updateActor(actor3.getId(), actor3);

        Optional<Actor> updatedActor = actorService.getActorById(actor3.getId());

        assertNotNull(updatedActor);
        assertTrue(actorRepository.findById(updatedActor.get().getId()).isPresent());
        assertEquals("Pesci", updatedActor.get().getLastName());
    }

    @Test
    void testDeleteActor() {
        Long actorId = Long.valueOf(1);
        Actor actorBeingCreated = new Actor(actorId, "Sylvester", "Stallone", "06.07.1946");

        actorService.createActor(actorBeingCreated);

        actorService.deleteActor(actorId);

        assertFalse(actorRepository.findById(actorId).isPresent());
    }
}
