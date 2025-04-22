package ch.zhaw.freelancer4u.service;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ch.zhaw.freelancer4u.model.Job;
import ch.zhaw.freelancer4u.model.JobsState;
import ch.zhaw.freelancer4u.repository.JobRepository;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;
import java.util.HashMap;

@Service
public class JobService {
    @Autowired
    JobRepository jobRepository;

    public Optional<Job> assignJob(String jobId, String freelancerId) {
        // 1. Prüfen ob Job existiert
        Optional<Job> jobOptional = jobRepository.findById(jobId);
        if (jobOptional.isEmpty()) {
            return Optional.empty(); // Job not found
        }

        Job job = jobOptional.get();
        
        // 2. Prüfen ob Job im Zustand NEW ist
        if (job.getJobState() != JobsState.NEW) {
            return Optional.empty(); // Job not in correct state
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
            // Log exception maybe?
            return Optional.empty(); // Error during save
        }
    }

    public Optional<Job> completeJob(String jobId, String freelancerId) {
        Optional<Job> jobOptional = jobRepository.findById(jobId);
        if (jobOptional.isEmpty()) {
            // Job not found
            return Optional.empty();
        }

        Job job = jobOptional.get();

        if (job.getJobState() != JobsState.ASSIGNED) {
            // Job is not in ASSIGNED state
            return Optional.empty();
        }

        if (freelancerId == null || job.getFreelancerId() == null || !job.getFreelancerId().equals(freelancerId)) {
            // Job is not assigned to this freelancer, or freelancerId provided is null
            // Note: The check `job.getFreelancerId().equals(freelancerId)` implies freelancerId should not be null.
            // If an admin *can* complete a job without providing the correct freelancerId, this logic needs changing.
            // Assuming for now it must match.
            return Optional.empty();
        }

        job.setJobState(JobsState.DONE);

        try {
            Job savedJob = jobRepository.save(job); // Save the updated job
            return Optional.of(savedJob); // Return the updated job wrapped in Optional
        } catch (Exception e) {
            // Log exception?
            return Optional.empty(); // Error during save
        }
    }
}