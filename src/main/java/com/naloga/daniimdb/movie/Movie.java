package com.naloga.daniimdb.movie;

import com.naloga.daniimdb.actor.Actor;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Table(name = "MOVIE")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imdbID;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int releaseYear;

    @Column(nullable = false)
    private String description;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<MoviePicture> pictures;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "actor_movie",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id"))
    private Set<Actor> actors = new HashSet<>();

    public Movie(Long imdbId, String title, int releaseYear, String description) {
        this.imdbID = imdbId;
        this.title = title;
        this.releaseYear = releaseYear;
        this.description = description;
    }

    public Movie(String title, int releaseYear, String description) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.description = description;
    }

    @Override
    public int hashCode() {
        return Objects.hash(imdbID, title, releaseYear, description);
    }
}
