package com.naloga.daniimdb.movie;

import com.naloga.daniimdb.movie.repository.MovieRepository;
import com.naloga.daniimdb.movie.services.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MovieServiceUnitTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllMovies() {
        List<Movie> movies = new ArrayList<>();
        movies.add(new Movie(1L, "Movie 1", 2022, "Description 1"));
        movies.add(new Movie(2L, "Movie 2", 2023, "Description 2"));
        movieService.saveAllMovies(movies);

        when(movieRepository.findAll()).thenReturn(movies);

        List<Movie> retrievedMovies = movieService.getAllMovies();

        assertEquals(2, retrievedMovies.size());
        verify(movieRepository, times(1)).findAll();
    }

    @Test
    void testGetMovies() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Movie> movies = new ArrayList<>();
        movies.add(new Movie(1L, "Movie 1", 2022, "Description 1"));
        movies.add(new Movie(2L, "Movie 2", 2023, "Description 2"));
        Page<Movie> moviePage = new PageImpl<>(movies, pageRequest, movies.size());

        movieService.saveAllMovies(movies);
        when(movieRepository.findAll(pageRequest)).thenReturn(moviePage);

        Page<Movie> retrievedMovies = movieService.getMovies(pageRequest);

        assertEquals(2, retrievedMovies.getTotalElements());
        verify(movieRepository, times(1)).findAll(pageRequest);
    }

    @Test
    void testSearchMoviesByTitle() {
        List<Movie> movies = new ArrayList<>();
        movies.add(new Movie(1L, "Movie 1", 2022, "Description 1"));
        movies.add(new Movie(2L, "Movie 2", 2023, "Description 2"));

        movieService.saveAllMovies(movies);
        when(movieRepository.findByTitleContainingIgnoreCase("Movie")).thenReturn(movies);

        List<Movie> retrievedMovies = movieService.searchMoviesByTitle("Movie");

        assertEquals(2, retrievedMovies.size());
        verify(movieRepository, times(1)).findByTitleContainingIgnoreCase("Movie");
    }

    @Test
    void testGeneratePictureUrl() {
        String expectedUrl = "/pictures/123.jpg";

        String generatedUrl = movieService.generatePictureUrl(123L);

        assertEquals(expectedUrl, generatedUrl);
    }

    @Test
    void testGetMovieByImdbId() {
        Movie movie = new Movie(12L, "Movie 1", 2022, "Description 1");
        movie.setImdbID(Long.valueOf(12));
        MultipartFile picFile = new MockMultipartFile("test.jpg", new byte[0]);
        movieService.createMovie(movie, picFile);

        when(movieService.getMovieByImdbId(Long.valueOf(12))).thenReturn(Optional.of(movie));

        Optional<Movie> retrievedMovie = movieService.getMovieByImdbId(Long.valueOf(12));

        assertTrue(retrievedMovie.isPresent(), "Retrieved movie should be present");
        assertEquals(movie, retrievedMovie.orElse(null), "Retrieved movie should match the expected movie");

        verify(movieRepository, times(1)).findById(Long.valueOf(12));
    }

    @Test
    void testCreateMovie() {
        Movie movie = new Movie(1L, "Movie 1", 2022, "Description 1");
        MultipartFile picFile = new MockMultipartFile("test.jpg", new byte[0]);

        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        Movie createdMovie = movieService.createMovie(movie, picFile);

        assertNotNull(createdMovie, "Created movie should not be null");
        assertEquals(movie.getImdbID(), createdMovie.getImdbID(), "Movie IDs should match");
        assertEquals(movie.getTitle(), createdMovie.getTitle(), "Movie titles should match");
        assertEquals(movie.getReleaseYear(), createdMovie.getReleaseYear(), "Movie years should match");
        assertEquals(movie.getDescription(), createdMovie.getDescription(), "Movie descriptions should match");

        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    void testUpdateMovie() {
        Movie originalMovie = new Movie(1L, "Already existing Movie", 2022, "Some movie that exists already in table");
        MultipartFile picFile = new MockMultipartFile("test.jpg", new byte[0]);
        movieService.createMovie(originalMovie, picFile);

        // I update the movie on ID = 1 with new value
        Movie updatedMovie = new Movie(1L, "Updated Movie 1", 2023, "Updated Description 1");
        when(movieRepository.existsById(1L)).thenReturn(true);
        when(movieRepository.save(updatedMovie)).thenReturn(updatedMovie);
        Movie updated = movieService.updateMovie(1L, updatedMovie);
        assertEquals(updatedMovie, updated);

        // I see that original movie is not equal to the updated one after my change = OK
        assertNotEquals(originalMovie, updated);

        verify(movieRepository, times(1)).save(updatedMovie);
    }

    @Test
    void testDeleteMovie() {
        long imdbId = 1L;
        Movie movie = new Movie(imdbId, "Movie 1", 2022, "Description 1");
        MultipartFile picFile = new MockMultipartFile("test.jpg", new byte[0]);
        movieService.createMovie(movie, picFile);
        movieService.deleteMovie(imdbId);

        verify(movieRepository, times(1)).deleteById(imdbId);
    }
}
