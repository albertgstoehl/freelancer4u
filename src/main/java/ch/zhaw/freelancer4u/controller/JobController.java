package ch.zhaw.freelancer4u.controller;

import ch.zhaw.freelancer4u.model.Job;
import ch.zhaw.freelancer4u.model.JobCreateDTO;
import ch.zhaw.freelancer4u.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class JobController {

    private final JobRepository jobRepository;

    @Autowired
    public JobController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    /**
     * GET /api/job
     * Gibt die Liste aller Jobs zurück
     * @return ResponseEntity mit Array von Job-Objekten
     */
    @GetMapping("/job")
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(jobRepository.findAll());
    }

    /**
     * POST /api/job
     * Erstellt einen neuen Job
     * @param jobCreateDTO DTO mit den Job-Informationen
     * @return ResponseEntity mit dem erstellten Job
     */
    @PostMapping("/job")
    public ResponseEntity<Job> createJob(@RequestBody JobCreateDTO jobCreateDTO) {
        Job job = new Job(jobCreateDTO);
        return ResponseEntity.ok(jobRepository.save(job));
    }
} 