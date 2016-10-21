package com.animalspotting.web.rest;

import com.animalspotting.AnimalSpottingApp;

import com.animalspotting.domain.Animal;
import com.animalspotting.repository.AnimalRepository;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the AnimalResource REST controller.
 *
 * @see AnimalResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AnimalSpottingApp.class)
public class AnimalResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    private static final String DEFAULT_PICTURE_URL = "AAAAA";
    private static final String UPDATED_PICTURE_URL = "BBBBB";

    @Inject
    private AnimalRepository animalRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restAnimalMockMvc;

    private Animal animal;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AnimalResource animalResource = new AnimalResource();
        ReflectionTestUtils.setField(animalResource, "animalRepository", animalRepository);
        this.restAnimalMockMvc = MockMvcBuilders.standaloneSetup(animalResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Animal createEntity(EntityManager em) {
        Animal animal = new Animal()
                .name(DEFAULT_NAME)
                .pictureURL(DEFAULT_PICTURE_URL);
        return animal;
    }

    @Before
    public void initTest() {
        animal = createEntity(em);
    }

    @Test
    @Transactional
    public void createAnimal() throws Exception {
        int databaseSizeBeforeCreate = animalRepository.findAll().size();

        // Create the Animal

        restAnimalMockMvc.perform(post("/api/animals")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(animal)))
                .andExpect(status().isCreated());

        // Validate the Animal in the database
        List<Animal> animals = animalRepository.findAll();
        assertThat(animals).hasSize(databaseSizeBeforeCreate + 1);
        Animal testAnimal = animals.get(animals.size() - 1);
        assertThat(testAnimal.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testAnimal.getPictureURL()).isEqualTo(DEFAULT_PICTURE_URL);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = animalRepository.findAll().size();
        // set the field null
        animal.setName(null);

        // Create the Animal, which fails.

        restAnimalMockMvc.perform(post("/api/animals")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(animal)))
                .andExpect(status().isBadRequest());

        List<Animal> animals = animalRepository.findAll();
        assertThat(animals).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAnimals() throws Exception {
        // Initialize the database
        animalRepository.saveAndFlush(animal);

        // Get all the animals
        restAnimalMockMvc.perform(get("/api/animals?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(animal.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].pictureURL").value(hasItem(DEFAULT_PICTURE_URL.toString())));
    }

    @Test
    @Transactional
    public void getAnimal() throws Exception {
        // Initialize the database
        animalRepository.saveAndFlush(animal);

        // Get the animal
        restAnimalMockMvc.perform(get("/api/animals/{id}", animal.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(animal.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.pictureURL").value(DEFAULT_PICTURE_URL.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAnimal() throws Exception {
        // Get the animal
        restAnimalMockMvc.perform(get("/api/animals/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAnimal() throws Exception {
        // Initialize the database
        animalRepository.saveAndFlush(animal);
        int databaseSizeBeforeUpdate = animalRepository.findAll().size();

        // Update the animal
        Animal updatedAnimal = animalRepository.findOne(animal.getId());
        updatedAnimal
                .name(UPDATED_NAME)
                .pictureURL(UPDATED_PICTURE_URL);

        restAnimalMockMvc.perform(put("/api/animals")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedAnimal)))
                .andExpect(status().isOk());

        // Validate the Animal in the database
        List<Animal> animals = animalRepository.findAll();
        assertThat(animals).hasSize(databaseSizeBeforeUpdate);
        Animal testAnimal = animals.get(animals.size() - 1);
        assertThat(testAnimal.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testAnimal.getPictureURL()).isEqualTo(UPDATED_PICTURE_URL);
    }

    @Test
    @Transactional
    public void deleteAnimal() throws Exception {
        // Initialize the database
        animalRepository.saveAndFlush(animal);
        int databaseSizeBeforeDelete = animalRepository.findAll().size();

        // Get the animal
        restAnimalMockMvc.perform(delete("/api/animals/{id}", animal.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Animal> animals = animalRepository.findAll();
        assertThat(animals).hasSize(databaseSizeBeforeDelete - 1);
    }
}
