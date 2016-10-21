package com.animalspotting.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.animalspotting.domain.Sighting;

import com.animalspotting.repository.SightingRepository;
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
 * REST controller for managing Sighting.
 */
@RestController
@RequestMapping("/api")
public class SightingResource {

    private final Logger log = LoggerFactory.getLogger(SightingResource.class);
        
    @Inject
    private SightingRepository sightingRepository;

    /**
     * POST  /sightings : Create a new sighting.
     *
     * @param sighting the sighting to create
     * @return the ResponseEntity with status 201 (Created) and with body the new sighting, or with status 400 (Bad Request) if the sighting has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/sightings",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Sighting> createSighting(@Valid @RequestBody Sighting sighting) throws URISyntaxException {
        log.debug("REST request to save Sighting : {}", sighting);
        if (sighting.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("sighting", "idexists", "A new sighting cannot already have an ID")).body(null);
        }
        Sighting result = sightingRepository.save(sighting);
        return ResponseEntity.created(new URI("/api/sightings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("sighting", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /sightings : Updates an existing sighting.
     *
     * @param sighting the sighting to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated sighting,
     * or with status 400 (Bad Request) if the sighting is not valid,
     * or with status 500 (Internal Server Error) if the sighting couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/sightings",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Sighting> updateSighting(@Valid @RequestBody Sighting sighting) throws URISyntaxException {
        log.debug("REST request to update Sighting : {}", sighting);
        if (sighting.getId() == null) {
            return createSighting(sighting);
        }
        Sighting result = sightingRepository.save(sighting);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("sighting", sighting.getId().toString()))
            .body(result);
    }

    /**
     * GET  /sightings : get all the sightings.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of sightings in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/sightings",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Sighting>> getAllSightings(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Sightings");
        Page<Sighting> page = sightingRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/sightings");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /sightings/:id : get the "id" sighting.
     *
     * @param id the id of the sighting to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the sighting, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/sightings/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Sighting> getSighting(@PathVariable Long id) {
        log.debug("REST request to get Sighting : {}", id);
        Sighting sighting = sightingRepository.findOne(id);
        return Optional.ofNullable(sighting)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /sightings/:id : delete the "id" sighting.
     *
     * @param id the id of the sighting to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/sightings/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSighting(@PathVariable Long id) {
        log.debug("REST request to delete Sighting : {}", id);
        sightingRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("sighting", id.toString())).build();
    }

}
