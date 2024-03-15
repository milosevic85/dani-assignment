package com.naloga.daniimdb.actor.services;

import com.naloga.daniimdb.actor.Actor;
import com.naloga.daniimdb.actor.repository.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
    @Cacheable("actors")
    public List<Actor> getAllActors() {
        return actorRepository.findAll();
    }

    // list actors with pagination support
    @Cacheable("actors")
    public Page<Actor> getActors(Pageable pageable) {
        return actorRepository.findAll(pageable);
    }

    // search for actors by first or last name
    @Cacheable("actors")
    public List<Actor> searchActorsByName(String firstname, String lastName) {
        return actorRepository.findByFirstNameAndLastName(firstname, lastName);
    }

    // extra: get actor by id
    @Cacheable("actors")
    public Optional<Actor> getActorById(Long id) {
        return actorRepository.findById(id);
    }

    // extra: showing the usage of one filter: find all actors with age above
    @Cacheable("actorsByAge")
    public List<Actor> getActorsWithAgeAbove(int age) {
        return actorRepository.findActorsWithAgeAbove(age);
    }

    public void saveActor(Actor actor) {
        actorRepository.save(actor);
    }

    public void saveAllActors(List<Actor> actors) {
        actorRepository.saveAll(actors);
    }

    // CRUD OPERATIONS as in task by SRC:
    // create actor
    public Actor createActor(Actor actor) {
        return actorRepository.save(actor);
    }

    // update actor
    public Actor updateActor(Long id, Actor updatedActor) {
        Optional<Actor> existingActorOptional = actorRepository.findById(id);

        if (existingActorOptional.isPresent()) {
            Actor existingActor = existingActorOptional.get();

            existingActor.setFirstName(updatedActor.getFirstName());
            existingActor.setLastName(updatedActor.getLastName());
            existingActor.setBornDate(updatedActor.getBornDate());

            return actorRepository.save(existingActor);
        }
        return null;
    }

    // delete actor by id
    public void deleteActor(Long id) {
        actorRepository.deleteById(id);
    }
}
