package ch.zhaw.freelancer4u.controller;

import ch.zhaw.freelancer4u.model.Job;
import ch.zhaw.freelancer4u.model.JobStateChangeDTO;
import ch.zhaw.freelancer4u.service.JobService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/service")
public class JobServiceController {
    
    private final JobService jobService;

    public JobServiceController(JobService jobService) {
        this.jobService = jobService;
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
} 