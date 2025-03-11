package ch.zhaw.freelancer4u.controller;

import ch.zhaw.freelancer4u.model.Job;
import ch.zhaw.freelancer4u.model.JobCreateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ch.zhaw.freelancer4u.service.JobService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class JobController {

    private final JobService jobService;

    @Autowired
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    /**
     * GET /api/job
     * Gibt die Liste aller Jobs zurück
     * @return ResponseEntity mit Array von Job-Objekten
     */
    @GetMapping("/job")
    public ResponseEntity<List<Job>> getAllJobs() {
        List<Job> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

    /**
     * POST /api/job
     * Erstellt einen neuen Job
     * @param jobCreateDTO DTO mit den Job-Informationen
     * @return ResponseEntity mit dem erstellten Job
     */
    @PostMapping("/job")
    public ResponseEntity<Job> createJob(@RequestBody JobCreateDTO jobCreateDTO) {
        Job createdJob = jobService.createJob(jobCreateDTO);
        return ResponseEntity.ok(createdJob);
    }
} 