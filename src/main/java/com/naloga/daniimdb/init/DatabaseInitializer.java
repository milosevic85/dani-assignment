package com.naloga.daniimdb.init;

import com.naloga.daniimdb.actor.Actor;
import com.naloga.daniimdb.actor.repository.ActorRepository;
import com.naloga.daniimdb.actor.services.ActorService;
import com.naloga.daniimdb.movie.Movie;
import com.naloga.daniimdb.movie.repository.MovieRepository;
import com.naloga.daniimdb.movie.services.MovieService;
import jakarta.annotation.PostConstruct;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class DatabaseInitializer {

    private final ActorRepository actorRepository;
    private final MovieRepository movieRepository;

    private final MovieService movieService;
    private final ActorService actorService;

    @Autowired
    public DatabaseInitializer(ActorRepository actorRepository, MovieRepository movieRepository, MovieService movieService, ActorService actorService) {
        this.actorRepository = actorRepository;
        this.movieRepository = movieRepository;
        this.movieService = movieService;
        this.actorService = actorService;
    }

    @Transactional
    @PostConstruct
    public void initializeDatabase() {
        saveActorsAndMovies();
        associateActorsWithMovies();
        saveAdditionalActorsAndMovies();
    }

    protected void saveActorsAndMovies() {
        List<Actor> actors = Arrays.asList(
                new Actor("Tom", "Hanks", "09.07.1956"),
                new Actor("Leonardo", "DiCaprio", "11.11.1974"),
                new Actor("Brad", "Pitt", "18.12.1963"),
                new Actor("Johnny", "Depp", "09.06.1963"),
                new Actor("Will", "Smith", "25.09.1968")
        );

        List<Movie> movies = Arrays.asList(
                new Movie("Forrest Gump", 1994, "Drama"),
                new Movie("The Green Mile", 1999, "Drama"),
                new Movie("Cast Away", 2000, "Drama"),
                new Movie("Pirates of the Caribbean", 2003, "Adventure"),
                new Movie("The Pursuit of Happyness", 2006, "Drama"),
                new Movie("Fight Club", 1999, "Drama"),
                new Movie("Inglourious Basterds", 2009, "War"),
                new Movie("Edward Scissorhands", 1990, "Fantasy"),
                new Movie("Charlie and the Chocolate Factory", 2005, "Adventure"),
                new Movie("Alice in Wonderland", 2010, "Adventure"),
                new Movie("Men in Black", 1997, "Action"),
                new Movie("Independence Day", 1996, "Action"),
                new Movie("I Am Legend", 2007, "Drama")
        );

        // Save actors and movies
        actorService.saveAllActors(actors);
        movieService.saveAllMovies(movies);
    }

    protected void associateActorsWithMovies() {
        List<Actor> actors = actorRepository.findAll();
        List<Movie> movies = movieRepository.findAll();

        for (Actor actor : actors) {
            for (Movie movie : movies) {
                if (isActorInMovie(actor, movie)) {
                    actor.getMovies().add(movie);
                    movie.getActors().add(actor); // bugfixed: I must update the movie's actors as well for this to work
                }
            }
            actorRepository.save(actor);
        }
    }

    private boolean isActorInMovie(Actor actor, Movie movie) {
        switch (actor.getFirstName() + " " + actor.getLastName()) {
            case "Tom Hanks":
                return Arrays.asList("Forrest Gump", "The Green Mile", "Cast Away").contains(movie.getTitle());
            case "Leonardo DiCaprio":
                return Arrays.asList("The Green Mile", "Inception", "Titanic").contains(movie.getTitle());
            case "Brad Pitt":
                return Arrays.asList("Fight Club", "Inglourious Basterds", "Se7en").contains(movie.getTitle());
            case "Johnny Depp":
                return Arrays.asList("Pirates of the Caribbean", "Edward Scissorhands", "Charlie and the Chocolate Factory").contains(movie.getTitle());
            case "Will Smith":
                return Arrays.asList("Men in Black", "Independence Day", "I Am Legend").contains(movie.getTitle());
            default:
                return false;
        }
    }

    protected void saveAdditionalActorsAndMovies() {
        // Additional actors and movies to be saved
        Actor additionalActor = new Actor("Meryl", "Streep", "22.06.1949");
        actorService.saveActor(additionalActor);

        Movie additionalMovie = new Movie("The Devil Wears Prada", 2006, "Comedy");
        movieService.saveMovie(additionalMovie);
    }
}