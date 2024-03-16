package com.naloga.daniimdb.movie;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naloga.daniimdb.SecurityConfiguration;
import com.naloga.daniimdb.movie.controller.MovieController;
import com.naloga.daniimdb.movie.services.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Profile("test")
@Import(SecurityConfiguration.class)
@WebMvcTest(MovieController.class)
public class MovieControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MovieService movieService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllMovies() throws Exception {
        Movie movie1 = new Movie(12L, "Test Movie1", 2022, "Description 1");
        Movie movie2 = new Movie(13L, "Test Movie2", 2012, "Description 2");

        MultipartFile picFile1 = new MockMultipartFile("test1.jpg", new byte[0]);
        MultipartFile picFile2 = new MockMultipartFile("test2.jpg", new byte[0]);
        movieService.createMovie(movie1, picFile1);
        movieService.createMovie(movie2, picFile2);

        List<Movie> movies = Arrays.asList(movie1, movie2);
        when(movieService.getAllMovies()).thenReturn(movies);

        mockMvc.perform(MockMvcRequestBuilders.get("/movies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Test Movie1"))
                .andExpect(jsonPath("$[1].title").value("Test Movie2"));
    }

    @Test
    void testGetAllMoviesWithPictures() throws Exception {
        Movie movie = new Movie(12L, "Test Movie", 2022, "Description 1");
        MultipartFile picFile = new MockMultipartFile("test.jpg", new byte[0]);
        movieService.createMovie(movie, picFile);
        Optional<Movie> optionalMovie = Optional.of(movie);

        when(movieService.getAllMoviesWithPictures(anyLong())).thenReturn(optionalMovie);

        mockMvc.perform(MockMvcRequestBuilders.get("/movies/with-pictures?imdbId=123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Test Movie"));
    }

    @Test
    void testGetMovieByImdbId() throws Exception {
        Movie movie = new Movie(12L, "Test Movie", 2022, "Description 1");
        MultipartFile picFile = new MockMultipartFile("test.jpg", new byte[0]);
        movieService.createMovie(movie, picFile);

        when(movieService.getMovieByImdbId(eq(12L))).thenReturn(Optional.of(movie));

        mockMvc.perform(MockMvcRequestBuilders.get("/movies/12"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Test Movie"));
    }

    @Test
    void testGetMovies() throws Exception {
        Movie movie1 = new Movie(12L, "Test Movie1", 2022, "Description 1");
        Movie movie2 = new Movie(13L, "Test Movie2", 2012, "Description 2");
        MultipartFile picFile1 = new MockMultipartFile("test1.jpg", new byte[0]);
        MultipartFile picFile2 = new MockMultipartFile("test2.jpg", new byte[0]);
        movieService.createMovie(movie1, picFile1);
        movieService.createMovie(movie2, picFile2);
        List<Movie> movies = Arrays.asList(movie1, movie2);
        PageImpl<Movie> page = new PageImpl<>(movies);

        when(movieService.getMovies(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/movies/pagination"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].title").value("Test Movie1"))
                .andExpect(jsonPath("$.content[1].title").value("Test Movie2"));;
    }

    @Test
    void testGetMoviePictureUrl() throws Exception {
//        Movie movie = new Movie(12L, "Test Movie", 2022, "Description 1");
//        MultipartFile picFile = new MockMultipartFile("test.jpg", new byte[0]);
//        movieService.createMovie(movie, picFile);

        when(movieService.generatePictureUrl(eq(12L))).thenReturn("http://example.com/test.jpg");

        mockMvc.perform(MockMvcRequestBuilders.get("/movies/12/picture-url"))
                .andExpect(status().isOk())
                .andExpect(content().string("http://example.com/test.jpg"));
    }

    // TODO: [milosevic85], strangely only GET, DELETE is allowed although I made in security configuration to permitAll at least for test - need to check
    @Test
    void testCreateMovie() throws Exception {
        Movie movie1 = new Movie(12L, "Test Movie1", 2022, "Description 1");
        MultipartFile picFile1 = new MockMultipartFile("test1.jpg", new byte[0]);
        movieService.createMovie(movie1, picFile1);

        MockMultipartFile file = new MockMultipartFile("picFile1", "test1.jpg", MediaType.IMAGE_JPEG_VALUE, "image content".getBytes());

        when(movieService.createMovie(any(Movie.class), any())).thenReturn(movie1);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.multipart("/movies")
                        .file(file)
                        .flashAttr("movie", movie1) // Use flashAttr instead of param for object binding
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Movie createdMovie = objectMapper.readValue(content, Movie.class);

        assertNotNull(createdMovie);
        assertEquals("Test Movie1", createdMovie.getTitle());
    }

    // TODO: [milosevic85], strangely only GET, DELETE is allowed although I made in security configuration to permitAll at least for test - need to check
    @Test
    void testUpdateMovie() throws Exception {
        Movie movie = new Movie(12L, "Test Movie", 2022, "Description 1");
        MultipartFile picFile = new MockMultipartFile("test.jpg", new byte[0]);
        movieService.createMovie(movie, picFile);

        Movie movieToUpdate = new Movie(12L, "Test Updated", 2012, "Description Updated");

        when(movieService.updateMovie(eq(12L), any(Movie.class))).thenReturn(movieToUpdate);

        mockMvc.perform(MockMvcRequestBuilders.put("/movies/12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieToUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Test Updated"));
    }

    @Test
    void testDeleteMovie() throws Exception {
        Movie movie = new Movie(12L, "Test Movie", 2022, "Description 1");
        MultipartFile picFile = new MockMultipartFile("test.jpg", new byte[0]);
        movieService.createMovie(movie, picFile);

        mockMvc.perform(MockMvcRequestBuilders.delete("/movies/12"))
                .andExpect(status().isNoContent());
    }
}
