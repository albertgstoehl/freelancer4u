package ch.zhaw.freelancer4u.repository;

import ch.zhaw.freelancer4u.model.Job;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends MongoRepository<Job, String> {
    // Hier können bei Bedarf spezifische Abfragemethoden hinzugefügt werden
} 