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
    private Long id;

    @Column(nullable = false, unique = true)
    private String imdbID;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int releaseYear;

    @Column(nullable = false)
    private String description;

    @ManyToMany(mappedBy = "movies")
    private List<Actor> actors;

}
