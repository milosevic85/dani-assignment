package com.naloga.daniimdb.actor.controller;

import com.naloga.daniimdb.actor.Actor;
import com.naloga.daniimdb.actor.services.ActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/actors")
public class ActorController {

    private final ActorService actorService;

    @Autowired
    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }

    // list all actors
    @GetMapping
    public List<Actor> getAllActors() {
        return actorService.getAllActors();
    }

    // list actors with pagination support
    @GetMapping("/pagination")
    public Page<Actor> getActors(Pageable pageable) {
        return actorService.getActors(pageable);
    }

    // search for actors by first and last name
    @GetMapping("/search")
    public List<Actor> searchActorsByName(@RequestParam(required = false) String firstName, @RequestParam(required = false) String lastName) {
        return actorService.searchActorsByName(firstName, lastName);
    }

    // get an actor by id
    @GetMapping("/{id}")
    public ResponseEntity<Actor> getActorById(@PathVariable Long id) {
        return actorService.getActorById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // filter actors by age
    @GetMapping("/age/{age}")
    public List<Actor> getActorsByAge(@PathVariable int age) {
        return actorService.getActorsWithAgeAbove(age);
    }

    // CRUD OPERATIONS - task .pdf by SRC:
    // create actor
    @PostMapping
    public ResponseEntity<Actor> createActor(@ModelAttribute Actor actor) {
        Actor createdActor = actorService.createActor(actor);
        return ResponseEntity.ok(createdActor);
    }

    // update actor
    @PutMapping("/update/{id}")
    public ResponseEntity<Actor> updateActor(@PathVariable Long id, @RequestBody Actor updatedActor) {
        Actor updated = actorService.updateActor(id, updatedActor);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    // delete actor by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActor(@PathVariable Long id) {
        actorService.deleteActor(id);

        return ResponseEntity.noContent().build();
    }
}
