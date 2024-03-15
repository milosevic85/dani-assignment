package com.naloga.daniimdb.actor;

import com.naloga.daniimdb.movie.Movie;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Table(name = "ACTOR")
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String bornDate;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "ACTOR_MOVIE",
            joinColumns = @JoinColumn(name = "ACTOR_ID"),
            inverseJoinColumns = @JoinColumn(name = "MOVIE_ID")
    )
    private Set<Movie> movies = new HashSet<>();

    // I could use immutability to be better and more robust for multi-threading in future etc., something like that instead of constructors:
//    public Actor withFirstName(String firstName) {
//        return new Actor(this.id, firstName, this.lastName, this.bornDate, this.movies);
//    }
//
//    public Actor withLastName(String lastName) {
//        return new Actor(this.id, this.firstName, lastName, this.bornDate, this.movies);
//    }
//
//    public Actor withBornDate(String bornDate) {
//        return new Actor(this.id, this.firstName, this.lastName, bornDate, this.movies);
//    }

    public Actor(Long id, String firstName, String lastName, String bornDate, List<Movie> movies) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bornDate = bornDate;
        this.movies = (Set<Movie>) movies;
    }
    public Actor(Long id, String firstName, String lastName, String bornDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bornDate = bornDate;
    }

    public Actor(String firstName, String lastName, String bornDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.bornDate = bornDate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, bornDate);
    }
}
