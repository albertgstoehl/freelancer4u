package ch.zhaw.freelancer4u.service;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ch.zhaw.freelancer4u.model.Job;
import ch.zhaw.freelancer4u.model.JobsState;
import ch.zhaw.freelancer4u.repository.JobRepository;
@Service
public class JobService {
    @Autowired
    JobRepository jobRepository;

    public Optional<Job> assignJob(String jobId, String freelancerId) {
        // 1. Prüfen ob Job existiert
        Optional<Job> jobOptional = jobRepository.findById(jobId);
        if (jobOptional.isEmpty()) {
            return Optional.empty();
        }

        Job job = jobOptional.get();
        
        // 2. Prüfen ob Job im Zustand NEW ist
        if (job.getJobState() != JobsState.NEW) {
            return Optional.empty();
        }

        // 3. Job zuweisen
        job.setJobState(JobsState.ASSIGNED);
        job.setFreelancerId(freelancerId);

        // 4. Änderungen speichern
        try {
            Job updatedJob = jobRepository.save(job);
        // 5. Aktualisierter Job zurückgeben
            return Optional.of(updatedJob);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}