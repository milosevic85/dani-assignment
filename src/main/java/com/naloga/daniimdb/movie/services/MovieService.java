package com.naloga.daniimdb.movie.services;

import com.naloga.daniimdb.movie.Movie;
import com.naloga.daniimdb.movie.repository.MovieRepository;
import com.naloga.daniimdb.util.PictureUrlGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository){
        this.movieRepository = movieRepository;
    }

    // list all movies
    @Cacheable("movies")
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    // list all movies with their pictures
    @Cacheable("movies")
    public Optional<Movie> getAllMoviesWithPictures(Long imdbId) {
        return movieRepository.findByImdbIDWithPictures(imdbId);
    }

    // list movies with pagination support
    public Page<Movie> getMovies(Pageable pageable){
        return movieRepository.findAll(pageable);
    }

    // search movies by title and I will ignore case
    public List<Movie> searchMoviesByTitle(String title){
        return movieRepository.findByTitleContainingIgnoreCase(title);
    }

    // generate a picture URL for a movie by its imdbId
    @Cacheable("pictureUrls")
    public String generatePictureUrl(Long imdbId) {
        return PictureUrlGenerator.generatePictureUrl(imdbId);
    }

    // extra I can add search for imdbID
    public Optional<Movie> getMovieByImdbId(Long imdbId){
        return movieRepository.findById(imdbId);
    }

    // CRUD OPERATIONS according to task from SRC
    // create a movie
    public Movie createMovie(Movie movie, MultipartFile picFile) {
        try {
            // I save the picture associated with the movie
            PictureService.savePic(movie.getImdbID(), picFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movieRepository.save(movie);
    }

    // update an existing movie
    public Movie updateMovie(Long imdbId, Movie updatedMovie) {
        if (movieRepository.existsById(imdbId)) {
            updatedMovie.setImdbID(imdbId);

            return movieRepository.save(updatedMovie);
        }
        return null;
    }

    // delete movie by imdbId
    public void deleteMovie(long imdbId) {
        movieRepository.deleteById(imdbId);
    }
}
