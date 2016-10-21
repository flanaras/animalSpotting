package com.animalspotting.web.rest;

import com.animalspotting.AnimalSpottingApp;

import com.animalspotting.domain.Sighting;
import com.animalspotting.domain.User;
import com.animalspotting.domain.Animal;
import com.animalspotting.repository.SightingRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the SightingResource REST controller.
 *
 * @see SightingResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AnimalSpottingApp.class)
public class SightingResourceIntTest {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Float DEFAULT_LONGITUDE = 1F;
    private static final Float UPDATED_LONGITUDE = 2F;

    private static final Float DEFAULT_LATITUDE = 1F;
    private static final Float UPDATED_LATITUDE = 2F;

    private static final Integer DEFAULT_COUNT = 1;
    private static final Integer UPDATED_COUNT = 2;

    @Inject
    private SightingRepository sightingRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restSightingMockMvc;

    private Sighting sighting;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SightingResource sightingResource = new SightingResource();
        ReflectionTestUtils.setField(sightingResource, "sightingRepository", sightingRepository);
        this.restSightingMockMvc = MockMvcBuilders.standaloneSetup(sightingResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sighting createEntity(EntityManager em) {
        Sighting sighting = new Sighting()
                .date(DEFAULT_DATE)
                .longitude(DEFAULT_LONGITUDE)
                .latitude(DEFAULT_LATITUDE)
                .count(DEFAULT_COUNT);
        // Add required entity
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        sighting.setUser(user);
        // Add required entity
        Animal animal = AnimalResourceIntTest.createEntity(em);
        em.persist(animal);
        em.flush();
        sighting.setAnimal(animal);
        return sighting;
    }

    @Before
    public void initTest() {
        sighting = createEntity(em);
    }

    @Test
    @Transactional
    public void createSighting() throws Exception {
        int databaseSizeBeforeCreate = sightingRepository.findAll().size();

        // Create the Sighting

        restSightingMockMvc.perform(post("/api/sightings")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(sighting)))
                .andExpect(status().isCreated());

        // Validate the Sighting in the database
        List<Sighting> sightings = sightingRepository.findAll();
        assertThat(sightings).hasSize(databaseSizeBeforeCreate + 1);
        Sighting testSighting = sightings.get(sightings.size() - 1);
        assertThat(testSighting.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testSighting.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testSighting.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testSighting.getCount()).isEqualTo(DEFAULT_COUNT);
    }

    @Test
    @Transactional
    public void checkLongitudeIsRequired() throws Exception {
        int databaseSizeBeforeTest = sightingRepository.findAll().size();
        // set the field null
        sighting.setLongitude(null);

        // Create the Sighting, which fails.

        restSightingMockMvc.perform(post("/api/sightings")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(sighting)))
                .andExpect(status().isBadRequest());

        List<Sighting> sightings = sightingRepository.findAll();
        assertThat(sightings).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLatitudeIsRequired() throws Exception {
        int databaseSizeBeforeTest = sightingRepository.findAll().size();
        // set the field null
        sighting.setLatitude(null);

        // Create the Sighting, which fails.

        restSightingMockMvc.perform(post("/api/sightings")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(sighting)))
                .andExpect(status().isBadRequest());

        List<Sighting> sightings = sightingRepository.findAll();
        assertThat(sightings).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSightings() throws Exception {
        // Initialize the database
        sightingRepository.saveAndFlush(sighting);

        // Get all the sightings
        restSightingMockMvc.perform(get("/api/sightings?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(sighting.getId().intValue())))
                .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
                .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
                .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
                .andExpect(jsonPath("$.[*].count").value(hasItem(DEFAULT_COUNT)));
    }

    @Test
    @Transactional
    public void getSighting() throws Exception {
        // Initialize the database
        sightingRepository.saveAndFlush(sighting);

        // Get the sighting
        restSightingMockMvc.perform(get("/api/sightings/{id}", sighting.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(sighting.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.doubleValue()))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.doubleValue()))
            .andExpect(jsonPath("$.count").value(DEFAULT_COUNT));
    }

    @Test
    @Transactional
    public void getNonExistingSighting() throws Exception {
        // Get the sighting
        restSightingMockMvc.perform(get("/api/sightings/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSighting() throws Exception {
        // Initialize the database
        sightingRepository.saveAndFlush(sighting);
        int databaseSizeBeforeUpdate = sightingRepository.findAll().size();

        // Update the sighting
        Sighting updatedSighting = sightingRepository.findOne(sighting.getId());
        updatedSighting
                .date(UPDATED_DATE)
                .longitude(UPDATED_LONGITUDE)
                .latitude(UPDATED_LATITUDE)
                .count(UPDATED_COUNT);

        restSightingMockMvc.perform(put("/api/sightings")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedSighting)))
                .andExpect(status().isOk());

        // Validate the Sighting in the database
        List<Sighting> sightings = sightingRepository.findAll();
        assertThat(sightings).hasSize(databaseSizeBeforeUpdate);
        Sighting testSighting = sightings.get(sightings.size() - 1);
        assertThat(testSighting.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testSighting.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testSighting.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testSighting.getCount()).isEqualTo(UPDATED_COUNT);
    }

    @Test
    @Transactional
    public void deleteSighting() throws Exception {
        // Initialize the database
        sightingRepository.saveAndFlush(sighting);
        int databaseSizeBeforeDelete = sightingRepository.findAll().size();

        // Get the sighting
        restSightingMockMvc.perform(delete("/api/sightings/{id}", sighting.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Sighting> sightings = sightingRepository.findAll();
        assertThat(sightings).hasSize(databaseSizeBeforeDelete - 1);
    }
}
