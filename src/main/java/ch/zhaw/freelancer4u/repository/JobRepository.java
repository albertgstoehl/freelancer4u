package ch.zhaw.freelancer4u.repository;

import ch.zhaw.freelancer4u.model.Job;
import ch.zhaw.freelancer4u.model.JobType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobRepository extends MongoRepository<Job, String> {
    List<Job> findByEarningsGreaterThan(Double earnings);
    List<Job> findByJobType(JobType type);
    List<Job> findByEarningsGreaterThanAndJobType(Double earnings, JobType jobType);
} 