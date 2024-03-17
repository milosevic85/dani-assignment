package com.naloga.daniimdb.movie.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naloga.daniimdb.movie.Movie;
import com.naloga.daniimdb.movie.services.MovieService;
import com.naloga.daniimdb.movie.services.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;
    private final PictureService pictureService;
    private final ObjectMapper objectMapper;

    @Autowired
    public MovieController(MovieService movieService, PictureService pictureService, ObjectMapper objectMapper) {
        this.movieService = movieService;
        this.pictureService = pictureService;
        this.objectMapper = objectMapper;
    }

    // list all movies
    @GetMapping
    public List<Movie> getAllMovies() {
        return movieService.getAllMovies();
    }

    // list all movies with their pictures
    @GetMapping("/with-pictures")
    public Optional<Movie> getAllMoviesWithPictures(@RequestParam Long imdbId) {
        return movieService.getAllMoviesWithPictures(imdbId);
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
    public ResponseEntity<Movie> getMovieByImdbId(@PathVariable Long imdbId) {
        return movieService.getMovieByImdbId(imdbId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // generate a picture url for a given imdbId
    @GetMapping("/{imdbId}/picture-url")
    public ResponseEntity<String> getMoviePictureUrl(@PathVariable Long imdbId) {
        String pictureUrl = movieService.generatePictureUrl(imdbId);
        return ResponseEntity.ok(pictureUrl);
    }

    // Task: I need to add CRUD operations

    // create a new movie
    // bugfixed: Movie movie with annotation @ModelAttribute
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Movie> createMovie(@RequestParam("picFile") MultipartFile picFile,
                                             @ModelAttribute Movie movie) {

        try {
            PictureService.savePic(movie.getImdbID(), picFile);

            Movie createdMovie = movieService.createMovie(movie, picFile);
            return ResponseEntity.ok(createdMovie);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // update existing movie
    @PutMapping("/update/{imdbId}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long imdbId, @RequestBody Movie updatedMovie) {
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
