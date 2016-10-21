package com.animalspotting.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.animalspotting.domain.Animal;

import com.animalspotting.repository.AnimalRepository;
import com.animalspotting.web.rest.util.HeaderUtil;
import com.animalspotting.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Animal.
 */
@RestController
@RequestMapping("/api")
public class AnimalResource {

    private final Logger log = LoggerFactory.getLogger(AnimalResource.class);
        
    @Inject
    private AnimalRepository animalRepository;

    /**
     * POST  /animals : Create a new animal.
     *
     * @param animal the animal to create
     * @return the ResponseEntity with status 201 (Created) and with body the new animal, or with status 400 (Bad Request) if the animal has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/animals",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Animal> createAnimal(@Valid @RequestBody Animal animal) throws URISyntaxException {
        log.debug("REST request to save Animal : {}", animal);
        if (animal.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("animal", "idexists", "A new animal cannot already have an ID")).body(null);
        }
        Animal result = animalRepository.save(animal);
        return ResponseEntity.created(new URI("/api/animals/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("animal", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /animals : Updates an existing animal.
     *
     * @param animal the animal to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated animal,
     * or with status 400 (Bad Request) if the animal is not valid,
     * or with status 500 (Internal Server Error) if the animal couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/animals",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Animal> updateAnimal(@Valid @RequestBody Animal animal) throws URISyntaxException {
        log.debug("REST request to update Animal : {}", animal);
        if (animal.getId() == null) {
            return createAnimal(animal);
        }
        Animal result = animalRepository.save(animal);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("animal", animal.getId().toString()))
            .body(result);
    }

    /**
     * GET  /animals : get all the animals.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of animals in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/animals",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Animal>> getAllAnimals(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Animals");
        Page<Animal> page = animalRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/animals");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /animals/:id : get the "id" animal.
     *
     * @param id the id of the animal to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the animal, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/animals/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Animal> getAnimal(@PathVariable Long id) {
        log.debug("REST request to get Animal : {}", id);
        Animal animal = animalRepository.findOne(id);
        return Optional.ofNullable(animal)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /animals/:id : delete the "id" animal.
     *
     * @param id the id of the animal to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/animals/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteAnimal(@PathVariable Long id) {
        log.debug("REST request to delete Animal : {}", id);
        animalRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("animal", id.toString())).build();
    }

}
