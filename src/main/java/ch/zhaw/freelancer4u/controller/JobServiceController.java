package ch.zhaw.freelancer4u.controller;

import ch.zhaw.freelancer4u.model.Job;
import ch.zhaw.freelancer4u.model.JobStateChangeDTO;
import ch.zhaw.freelancer4u.model.JobStateAggregationDTO;
import ch.zhaw.freelancer4u.service.JobService;
import ch.zhaw.freelancer4u.repository.JobRepository;
import ch.zhaw.freelancer4u.service.CompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/api/service")
public class JobServiceController {
    
    private final JobService jobService;
    private final JobRepository jobRepository;
    private final CompanyService companyService;

    public JobServiceController(JobService jobService, JobRepository jobRepository, CompanyService companyService) {
        this.jobService = jobService;
        this.jobRepository = jobRepository;
        this.companyService = companyService;
    }

    /**
     * PUT /api/service/assignjob
     * Weist einen Job einem Freelancer zu
     * @param changeS DTO mit Job- und Freelancer-ID
     * @return ResponseEntity mit aktualisiertem Job oder BAD_REQUEST
     */
    @PutMapping("/assignjob")
    public ResponseEntity<Job> assignJob(@RequestBody JobStateChangeDTO changeS) {
        String freelancerId = changeS.getFreelancerId();
        String jobId = changeS.getJobId();
        Optional<Job> job = jobService.assignJob(jobId, freelancerId);
        if (job.isPresent()) {
            return new ResponseEntity<>(job.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/completejob")
    public ResponseEntity<Map<String, Object>> completeJob(@RequestBody JobStateChangeDTO jobStateChangeDTO) {
        Map<String, Object> result = jobService.completJob(
            jobStateChangeDTO.getJobId(),
            jobStateChangeDTO.getFreelancerId()
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/jobdashboard")
    public ResponseEntity<List<JobStateAggregationDTO>> getJobStateAggregation(@RequestParam String company) {
        if (!companyService.companyExists(company)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<JobStateAggregationDTO> result = jobRepository.getJobStateAggregation(company);
        return ResponseEntity.ok(result);
    }
} 