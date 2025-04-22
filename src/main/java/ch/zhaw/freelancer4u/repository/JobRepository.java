package ch.zhaw.freelancer4u.repository;

import ch.zhaw.freelancer4u.model.Job;
import ch.zhaw.freelancer4u.model.JobType;
import ch.zhaw.freelancer4u.model.JobStateAggregationDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobRepository extends MongoRepository<Job, String> {
    Page<Job> findByEarningsGreaterThan(Double earnings, Pageable pageable);
    Page<Job> findByJobType(JobType jobType, Pageable pageable);
    Page<Job> findByJobTypeAndEarningsGreaterThan(JobType jobType, Double earnings, Pageable pageable);
    Page<Job> findAll(Pageable pageable);

    @Aggregation({
        "{'$match': {'companyId': ?0}}",
        "{'$group': {'_id': '$jobState', 'jobIds': {'$push': '$_id'}, 'count': {'$count': {}}}}"
    })
    List<JobStateAggregationDTO> getJobStateAggregation(String companyId);
} 