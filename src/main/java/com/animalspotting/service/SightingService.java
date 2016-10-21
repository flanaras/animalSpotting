package com.animalspotting.service;

import com.animalspotting.domain.Animal;
import com.animalspotting.domain.Sighting;
import com.animalspotting.domain.Subscription;
import com.animalspotting.repository.SightingRepository;
import com.animalspotting.repository.SubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

@Service
@Transactional
public class SightingService {

    @Inject
    private SightingRepository sightingRepository;

    @Inject
    private SubscriptionRepository subscriptionRepository;

    @Inject
    private MailService mailService;

    private final Logger log = LoggerFactory.getLogger(SightingService.class);

    public Sighting save(Sighting sighting) {
        Animal animal = sighting.getAnimal();

        List<Subscription> subscriptions = subscriptionRepository.findByAnimal(animal);

        for (Subscription subscription : subscriptions) {
            log.info("");
            log.info(subscription.getUser().getEmail());
            mailService.sendEmail(subscription.getUser().getEmail(), "hello", "bla blah... a " + subscription.getAnimal().getName() + " was sighted ", false, false);
        }

        return sightingRepository.save(sighting);
    }

    public Sighting findOne(Long id) {
        return sightingRepository.findOne(id);
    }

    public void delete(Long id) {
        sightingRepository.delete(id);
    }

    public Page<Sighting> findAll(Pageable pageable) {
        return sightingRepository.findAll(pageable);
    }
}
