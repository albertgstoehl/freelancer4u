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

    public Map<String, Object> completJob(String jobId, String freelancerId) {
        Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Job not found"));

        if (job.getJobState() != JobsState.ASSIGNED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Job is not in ASSIGNED state");
        }

        if (!job.getFreelancerId().equals(freelancerId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Job is not assigned to this freelancer");
        }

        job.setJobState(JobsState.DONE);
        jobRepository.save(job);

        Map<String, Object> response = new HashMap<>();
        response.put("jobState", "DONE");
        response.put("companyId", job.getCompanyId());
        response.put("freelancerId", job.getFreelancerId());
        return response;
    }
}