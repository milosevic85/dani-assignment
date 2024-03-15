package com.naloga.daniimdb.movie.repository;

import com.naloga.daniimdb.movie.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    // list all movies
    List<Movie> findAll();

    // list movies with pagination support
    Page<Movie> findAll(Pageable pageable);

    //search of movie
    List<Movie> findByTitleContainingIgnoreCase(String title);

    // to find all movies with pictures included
    @Query("SELECT DISTINCT m FROM Movie m LEFT JOIN FETCH m.pictures p WHERE m.imdbID = :imdbId")
    Optional<Movie> findByImdbIDWithPictures(@Param("imdbId") Long imdbId);
}
