package ch.zhaw.freelancer4u.controller;

import ch.zhaw.freelancer4u.model.Job;
import ch.zhaw.freelancer4u.model.JobStateChangeDTO;
import ch.zhaw.freelancer4u.model.JobStateAggregationDTO;
import ch.zhaw.freelancer4u.service.JobService;
import ch.zhaw.freelancer4u.repository.JobRepository;
import ch.zhaw.freelancer4u.service.CompanyService;
import ch.zhaw.freelancer4u.service.UserService;
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
    private final UserService userService;

    public JobServiceController(JobService jobService, JobRepository jobRepository, CompanyService companyService, UserService userService) {
        this.jobService = jobService;
        this.jobRepository = jobRepository;
        this.companyService = companyService;
        this.userService = userService;
    }

    /**
     * PUT /api/service/assignjob
     * Weist einen Job einem Freelancer zu (Admin only)
     * @param changeS DTO mit Job- und Freelancer-ID
     * @return ResponseEntity mit aktualisiertem Job oder FORBIDDEN/BAD_REQUEST
     */
    @PutMapping("/assignjob")
    public ResponseEntity<Job> assignJob(@RequestBody JobStateChangeDTO changes) {
        if (!userService.userHasRole("admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        String freelancerId = changes.getFreelancerId();
        String jobId = changes.getJobId();
        Optional<Job> job = jobService.assignJob(jobId, freelancerId);
        if (job.isPresent()) {
            return new ResponseEntity<>(job.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * PUT /api/service/completejob
     * Schliesst einen Job ab (Admin only)
     * @param changes DTO mit Job-ID (und optional Freelancer-ID)
     * @return ResponseEntity mit aktualisiertem Job oder FORBIDDEN/BAD_REQUEST
     */
    @PutMapping("/completejob")
    public ResponseEntity<Job> completeJob(@RequestBody JobStateChangeDTO changes) {
        if (!userService.userHasRole("admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        String jobId = changes.getJobId();
        String freelancerId = changes.getFreelancerId();

        if (freelancerId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Optional<Job> job = jobService.completeJob(jobId, freelancerId);

        if (job.isPresent()) {
            return new ResponseEntity<>(job.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/jobdashboard")
    public ResponseEntity<List<JobStateAggregationDTO>> getJobStateAggregation(@RequestParam String company) {
        if (!companyService.companyExists(company)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<JobStateAggregationDTO> result = jobRepository.getJobStateAggregation(company);
        return ResponseEntity.ok(result);
    }

    // ### New Endpoints for Freelancers (Task 2.b & 2.c) ###

    /**
     * PUT /api/service/me/assignjob
     * Assigns a job to the currently authenticated user.
     * @param jobId The ID of the job to assign.
     * @return ResponseEntity with the updated Job or appropriate error status.
     */
    @PutMapping("/me/assignjob")
    public ResponseEntity<Job> assignToMe(@RequestParam String jobId) {
        String userEmail = userService.getEmail();
        if (userEmail == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Optional<Job> job = jobService.assignJob(jobId, userEmail);
        if (job.isPresent()) {
            return new ResponseEntity<>(job.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * PUT /api/service/me/completejob
     * Completes a job assigned to the currently authenticated user.
     * @param jobId The ID of the job to complete.
     * @return ResponseEntity with the updated Job or appropriate error status.
     */
    @PutMapping("/me/completejob")
    public ResponseEntity<Job> completeMyJob(@RequestParam String jobId) {
        String userEmail = userService.getEmail();
        if (userEmail == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Optional<Job> job = jobService.completeJob(jobId, userEmail);

        if (job.isPresent()) {
            return new ResponseEntity<>(job.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
} 