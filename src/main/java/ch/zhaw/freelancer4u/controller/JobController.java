package ch.zhaw.freelancer4u.controller;

import ch.zhaw.freelancer4u.model.Job;
import ch.zhaw.freelancer4u.model.JobCreateDTO;
import ch.zhaw.freelancer4u.model.JobType;
import ch.zhaw.freelancer4u.repository.JobRepository;
import ch.zhaw.freelancer4u.service.CompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/api")
public class JobController {
    @Autowired
    private final JobRepository jobRepository;
    private final CompanyService companyService;

    public JobController(JobRepository jobRepository, CompanyService companyService) {
        this.jobRepository = jobRepository;
        this.companyService = companyService;
    }

    /**
     * GET /api/job
     * Gibt die Liste aller Jobs zurück
     * @return ResponseEntity mit Array von Job-Objekten
     */
    @GetMapping("/job")
    public ResponseEntity<List<Job>> getAllJobs(
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) JobType type) {
        
        try {
            List<Job> allJobs;
            if (min != null && type != null) {
                allJobs = jobRepository.findByEarningsGreaterThanAndJobType(min, type);
            } else if (min != null) {
                allJobs = jobRepository.findByEarningsGreaterThan(min);
            } else if (type != null) {
                allJobs = jobRepository.findByJobType(type);
            } else {
                allJobs = jobRepository.findAll();
            }
            
            return new ResponseEntity<>(allJobs, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * POST /api/job
     * Erstellt einen neuen Job
     * @param jobCreateDTO DTO mit den Job-Informationen
     * @return ResponseEntity mit dem erstellten Job
     */
    @PostMapping("/job")
    public ResponseEntity<Job> createJob(@RequestBody JobCreateDTO jobCreateDTO) {
        try {
            if (!companyService.companyExists(jobCreateDTO.getCompanyId())) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Job job = new Job(jobCreateDTO);
            Job savedJob = jobRepository.save(job);
            return new ResponseEntity<>(savedJob, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * GET /api/job/{id}
     * Gibt einen spezifischen Job zurück
     * @param id Die ID des Jobs
     * @return ResponseEntity mit Job-Objekt oder 404 wenn nicht gefunden
     */
    @GetMapping("/job/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable String id) {
        return jobRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
} 