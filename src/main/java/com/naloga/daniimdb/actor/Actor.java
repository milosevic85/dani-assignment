package com.naloga.daniimdb.actor;

import com.naloga.daniimdb.movie.Movie;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
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

    // Each actor can be associated with many movies, actor of course played in other movies also, I had to be careful here
    @ManyToMany
    @JoinTable(
            name = "movie_actor",
            joinColumns = @JoinColumn(name = "actor_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id")
    )
    private List<Movie> movies;

    // I could use immutability I know to be better and more robust for multi-threading in future etc., something like that for instance:
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
        this.movies = movies;
    }
    public Actor(Long id, String firstName, String lastName, String bornDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bornDate = bornDate;
    }
}
