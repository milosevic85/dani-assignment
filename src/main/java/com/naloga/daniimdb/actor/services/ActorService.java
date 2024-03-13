package com.naloga.daniimdb.actor.services;

import com.naloga.daniimdb.actor.Actor;
import com.naloga.daniimdb.actor.repository.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Service
public class ActorService {

    private final ActorRepository actorRepository;

    @Autowired
    public ActorService(ActorRepository actorRepository) {
        this.actorRepository = actorRepository;
    }

    // list all actors
    public List<Actor> getAllActors() {
        return actorRepository.findAll();
    }

    // list actors with pagination support
    public Page<Actor> getActors(Pageable pageable) {
        return actorRepository.findAll(pageable);
    }

    // search for actors by first or last name
    public List<Actor> searchActorsByName(String firstname, String lastName) {
        return actorRepository.findByFirstNameAndLastName(firstname, lastName);
    }

    // extra: get actor by id
    public Optional<Actor> getActorById(Long id) {
        return actorRepository.findById(id);
    }

    // CRUD OPERATIONS as in task by SRC:
    // create actor
    public Actor createActor(Actor actor) {
        return actorRepository.save(actor);
    }

    // update actor
    public Actor updateActor(Long id, Actor updatedActor) {
        if (actorRepository.existsById(id)) {
            updatedActor.setId(id);

            return actorRepository.save(updatedActor);
        }
        return null;
    }

    // delete actor by id
    public void deleteActor(Long id) {
        actorRepository.deleteById(id);
    }
}
