package com.naloga.daniimdb.actor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.naloga.daniimdb.SecurityConfiguration;
import com.naloga.daniimdb.actor.repository.ActorRepository;
import com.naloga.daniimdb.actor.services.ActorService;
import com.naloga.daniimdb.movie.Movie;
import com.naloga.daniimdb.movie.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Profile("test")
@Import(SecurityConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ActorControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ActorService actorService;


    @MockBean
    private ActorRepository actorRepository;
    @MockBean
    private MovieRepository movieRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllActors() throws Exception {
        Actor actor1 = new Actor(1L, "Janez", "Novak", "01.01.1990");
        Actor actor2 = new Actor(2L, "Mojca", "Novak", "02.02.1995");
        List<Actor> actors = Arrays.asList(actor1, actor2);

        actorService.saveAllActors(actors);
        when(actorService.getAllActors()).thenReturn(actors);

        mockMvc.perform(MockMvcRequestBuilders.get("/actors"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value("Janez"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].firstName").value("Mojca"));
    }

    @Test
    void testGetActorsWithPagination() throws Exception {
        List<Movie> moviesSugman = Arrays.asList(
                new Movie(12132L, "Nasa mala klinika", 2000, "Komedija"),
                new Movie(12133L, "Preseren", 2005, "Podobitev slavne osebnosti"),
                new Movie(12134L, "Predmestje", 2010, "Drama")
        );
        movieRepository.saveAll(moviesSugman);

        List<Movie> moviesNovak = Arrays.asList(
                new Movie(12232L, "The bee", 2011, "Komedija")
        );
        movieRepository.saveAll(moviesNovak);

        List<Actor> actors = Arrays.asList(
                new Actor(1L, "Jernej", "Sugman", "23.12.1968", moviesSugman),
                new Actor(2L, "Janez", "Novak", "21.11.1933", moviesNovak)
        );
        actorRepository.saveAll(actors);

        // I mock the service to return the page of actors
        Pageable pageable = PageRequest.of(0, 10);
        Page<Actor> actorsPage = new PageImpl<>(actors, pageable, actors.size());
        when(actorService.getActors(pageable)).thenReturn(actorsPage);
        List<Actor> actors2 = actorsPage.getContent();

        // bugfixed: my actors2 had to be serialized into JSON, otherwise I had wrong content
        ObjectMapper objectMapper = new ObjectMapper();
        String expectedContent = objectMapper.writeValueAsString(actors2);
        System.out.println("Serialized JSON: " + expectedContent);

        // I mock the service to return the page of actors
        when(actorService.getActors(any(Pageable.class))).thenReturn(actorsPage);

        // now I perform the request
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/actors/pagination"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // capturing response content
        String responseContent = result.getResponse().getContentAsString();

        // Extract the 'content' array from the actual response content
        JsonNode actualJson = new ObjectMapper().readTree(responseContent);
        String actualContent = actualJson.get("content").toString();

        // then I tested to see the content
        System.out.println("Response Content: " + actualContent);

        // for instance I assert that the response content is not empty
        assertFalse(actualContent.isEmpty(), "Response content is empty");

        // check if response content matches the expected content
        assertEquals(expectedContent, actualContent, "Response content does not match expected content");
    }

    @Test
    void testSearchActorsByName() throws Exception {
        Actor actor1 = new Actor(1L, "Janez", "Novak", "01.01.1990");
        List<Actor> actors = Arrays.asList(actor1);

        when(actorService.searchActorsByName("Janez", "Novak")).thenReturn(actors);

        mockMvc.perform(MockMvcRequestBuilders.get("/actors/search")
                        .param("firstName", "Janez")
                        .param("lastName", "Novak"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value("Janez"));
    }

    @Test
    void testGetActorById() throws Exception {
        Actor actor = new Actor(1L, "Janez", "Novak", "01.01.1990");

        when(actorService.getActorById(1L)).thenReturn(Optional.of(actor));

        mockMvc.perform(MockMvcRequestBuilders.get("/actors/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Janez"));
    }

    @Test
    void testGetActorsByAge() throws Exception {
        Actor actor1 = new Actor(1L, "Janez", "Novak", "01.01.1990");
        Actor actor2 = new Actor(2L, "Mojca", "Novak", "02.02.1995");
        List<Actor> actors = Arrays.asList(actor1, actor2);

        actorRepository.saveAll(actors);

        when(actorService.getActorsWithAgeAbove(30)).thenReturn(actors);

        mockMvc.perform(MockMvcRequestBuilders.get("/actors/age/30"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value("Janez"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].firstName").value("Mojca"));
    }

    @Test
    void testCreateActor() throws Exception {
        Actor actor = new Actor(1L, "Janez", "Novak", "01.01.1990");

        // Mock the service to return the created actor
        when(actorService.createActor(any(Actor.class))).thenReturn(actor);

        // Perform the request to create an actor
        mockMvc.perform(MockMvcRequestBuilders.post("/actors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actor)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Janez"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Novak"));
    }

    @Test
    void testUpdateActor() throws Exception {
        Actor actor = new Actor(1L, "Janez", "Novak", "01.01.1990");
        Actor updatedActor = new Actor(1L, "Updated", "Actor", "01.01.1990");
        actorService.createActor(actor);

        // Mock the service to return the updated actor
        when(actorService.updateActor(any(Long.class), any(Actor.class))).thenReturn(updatedActor);

        // Perform the request to update an actor
        mockMvc.perform(MockMvcRequestBuilders.put("/actors/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedActor)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Updated"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Actor"));
    }
    @Test
    void testDeleteActor() throws Exception {
        Long actorId = Long.valueOf(1);
        Actor actorBeingCreated = new Actor(actorId, "Sylvester", "Stallone", "06.07.1946");
        actorService.createActor(actorBeingCreated);
        mockMvc.perform(MockMvcRequestBuilders.delete("/actors/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
