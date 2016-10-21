package com.animalspotting.repository;

import com.animalspotting.domain.Sighting;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Sighting entity.
 */
@SuppressWarnings("unused")
public interface SightingRepository extends JpaRepository<Sighting,Long> {

    @Query("select sighting from Sighting sighting where sighting.user.login = ?#{principal.username}")
    List<Sighting> findByUserIsCurrentUser();

}
