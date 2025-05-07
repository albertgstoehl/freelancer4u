package ch.zhaw.freelancer4u.controller;

import ch.zhaw.freelancer4u.model.Job;
import ch.zhaw.freelancer4u.model.JobCreateDTO;
import ch.zhaw.freelancer4u.model.JobType;
import ch.zhaw.freelancer4u.repository.JobRepository;
import ch.zhaw.freelancer4u.service.CompanyService;
import ch.zhaw.freelancer4u.service.UserService;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class JobController {
    @Autowired
    JobRepository jobRepository;
    @Autowired
    CompanyService companyService;
    @Autowired
    UserService userService;

    @Autowired
    OpenAiChatModel chatModel;

    /**
     * GET /api/job
     * Gibt die Liste aller Jobs zurück, optional gefiltert und paginiert.
     * @param min Optional: minimaler Verdienst
     * @param type Optional: Job-Typ
     * @param pageNumber Optional: Seitennummer (1-basiert)
     * @param pageSize Optional: Anzahl Elemente pro Seite
     * @return ResponseEntity mit Page<Job>-Objekt
     */
    @GetMapping("/job")
    public ResponseEntity<Page<Job>> getAllJobs(
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) JobType type,
            @RequestParam(required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "5") Integer pageSize) {

        Page<Job> allJobs;

        int effectivePageNumber = Math.max(0, pageNumber - 1);
        pageSize = Math.max(1, pageSize);

        if (min == null && type == null) {
            allJobs = jobRepository.findAll(PageRequest.of(effectivePageNumber, pageSize));
        } else {
            if (min != null && type != null) {
                allJobs = jobRepository.findByJobTypeAndEarningsGreaterThan(type, min,
                        PageRequest.of(effectivePageNumber, pageSize));
            } else if (min != null) {
                allJobs = jobRepository.findByEarningsGreaterThan(min,
                        PageRequest.of(effectivePageNumber, pageSize));
            } else {
                allJobs = jobRepository.findByJobType(type, PageRequest.of(effectivePageNumber, pageSize));
            }
        }

        return new ResponseEntity<>(allJobs, HttpStatus.OK);
    }

    /**
     * POST /api/job
     * Erstellt einen neuen Job
     * @param jobCreateDTO DTO mit den Job-Informationen
     * @return ResponseEntity mit dem erstellten Job
     */
    @PostMapping("/job")
    public ResponseEntity<Job> createJob(@RequestBody JobCreateDTO jobCreateDTO) {
        // Check if user has admin role
        if (!userService.userHasRole("admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            if (!companyService.companyExists(jobCreateDTO.getCompanyId())) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            var promptString = "Der Titel lautet bisher: '" + jobCreateDTO.getTitle() + "'. Falls nötig, verbessere den Titel anhand der folgenden Beschreibung: " + jobCreateDTO.getDescription() + ". Gib nur den neuen Titel zurück.";
            var generatedTitleResponse = chatModel.call(new Prompt(promptString));
            String newTitle = (String) generatedTitleResponse.getResult().getOutput().getText();
            
            jobCreateDTO.setTitle(newTitle);

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
        Optional<Job> optJob = jobRepository.findById(id);
        if (optJob.isPresent()) {
            return new ResponseEntity<>(optJob.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * DELETE /api/job/{id}
     * Löscht einen spezifischen Job (nur für Admin-Rolle)
     * @param id Die ID des Jobs
     * @return ResponseEntity mit Statuscode 200 mit Body "DELETED" wenn gelöscht, oder 403 wenn keine Admin-Rolle
     */
    @DeleteMapping("/job/{id}")
    public ResponseEntity<String> deleteJobById(@PathVariable String id) {
        if (!userService.userHasRole("admin")) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        jobRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body("DELETED");
    }
} 