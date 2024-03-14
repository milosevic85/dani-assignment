package com.naloga.daniimdb.movie;

import com.naloga.daniimdb.actor.Actor;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
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

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private List<MoviePicture> pictures;

    @ManyToMany(mappedBy = "movies")
    private List<Actor> actors;

    public Movie(Long imdbId, String title, int releaseYear, String description) {
        this.imdbID = imdbId;
        this.title = title;
        this.releaseYear = releaseYear;
        this.description = description;
    }
}
