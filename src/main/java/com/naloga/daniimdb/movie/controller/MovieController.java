package com.naloga.daniimdb.movie.controller;


import com.naloga.daniimdb.movie.Movie;
import com.naloga.daniimdb.movie.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService){
        this.movieService = movieService;
    }

    // list all movies
    @GetMapping
    public List<Movie> getAllMovies() {
        return movieService.getAllMovies();
    }

    // list movies with pagination support
    @GetMapping("/pagination")
    public Page<Movie> getMovies(Pageable pageable) {
        return movieService.getMovies(pageable);
    }

    // I search movies by title for instance
    @GetMapping("/search")
    public List<Movie> searchMoviesByTitle(@RequestParam String title) {
        return movieService.searchMoviesByTitle(title);
    }

    // added extra check for imdbId
    @GetMapping("/{imdbId}")
    public ResponseEntity<Movie> getMovieByImdbId(@PathVariable long imdbId) {
        return movieService.getMovieByImdbId(imdbId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Task: I need to add CRUD operations
    // create a new movie
    @PostMapping
    public ResponseEntity<Movie> createMovie(@RequestBody Movie movie) {
        Movie createdMovie = movieService.createMovie(movie);

        return ResponseEntity.ok(createdMovie);
    }

    // update existing movie
    @PutMapping("/{imdbId}")
    public ResponseEntity<Movie> updateMovie(@PathVariable long imdbId, @RequestBody Movie updatedMovie) {
        Movie updated = movieService.updateMovie(imdbId, updatedMovie);

        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    // delete a movie by imdbId
    @DeleteMapping("/{imdbId}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long imdbId) {
        movieService.deleteMovie(imdbId);

        return ResponseEntity.noContent().build();
    }
}
