package com.naloga.daniimdb.actor.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.naloga.daniimdb.actor.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Long> {

    // list all actors
    List<Actor> findAll();

    // list actors with pagination support
    Page<Actor> findAll(Pageable pageable);

    // extra: search by first and last name
    List<Actor> findByFirstNameAndLastName(String firstName, String lastName);

}
