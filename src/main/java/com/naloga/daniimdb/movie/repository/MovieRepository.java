package com.naloga.daniimdb.movie.repository;

import com.naloga.daniimdb.movie.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    // list all movies
    List<Movie> findAll();

    // list movies with pagination support
    Page<Movie> findAll(Pageable pageable);

    //search of movie
    List<Movie> findByTitleContainingIgnoreCase(String title);

}
