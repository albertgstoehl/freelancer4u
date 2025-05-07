package ch.zhaw.freelancer4u.tools;

import java.util.List;
import java.util.Optional;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import ch.zhaw.freelancer4u.model.Company;
import ch.zhaw.freelancer4u.model.Job;
import ch.zhaw.freelancer4u.model.JobCreateDTO;
import ch.zhaw.freelancer4u.model.JobType;
import ch.zhaw.freelancer4u.repository.CompanyRepository;
import ch.zhaw.freelancer4u.repository.JobRepository;
import ch.zhaw.freelancer4u.service.CompanyService;

@Component
public class FreelancerTools {
    private final JobRepository jobRepository;
    private final CompanyService companyService;
    private final CompanyRepository companyRepository;

    public FreelancerTools(JobRepository jobRepository, CompanyService companyService, CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.companyService = companyService;
        this.companyRepository = companyRepository;
    }

    @Tool(description = "Information about the jobs in the database.")
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    @Tool(description = "Information about the companies in the database.")
    public List<Company> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    @Tool(description = "Gets an existing company by name or creates a new one if it doesn't exist. Provide company name and an email for creation.")
    public Company getOrCreateCompany(String companyName, String companyEmail) {
        Optional<Company> existingCompany = companyRepository.findAll().stream()
                .filter(c -> c.getName().equalsIgnoreCase(companyName))
                .findFirst();

        if (existingCompany.isPresent()) {
            return existingCompany.get();
        } else {
            companyService.createCompany(companyName, companyEmail);
            return companyRepository.findAll().stream()
                .filter(c -> c.getName().equalsIgnoreCase(companyName) && c.getEmail().equalsIgnoreCase(companyEmail))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Failed to create or find company after creation: " + companyName));
        }
    }

    @Tool(description = "Creates a new job with the given details and associates it with a company (creates company if not existing). Specify job title, description, job type (CONSULTING, IMPLEMENTATION, FULL_PROJECT, SUPPORT), earnings, company name, and company email.")
    public Job createJobForCompany(String jobTitle, String jobDescription, String jobType, double earnings, String companyName, String companyEmail) {
        Company company = getOrCreateCompany(companyName, companyEmail);
        
        JobType type;
        try {
            type = JobType.valueOf(jobType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid job type: " + jobType + ". Valid types are: CONSULTING, IMPLEMENTATION, FULL_PROJECT, SUPPORT.");
        }

        JobCreateDTO jobCreateDTO = new JobCreateDTO();
        jobCreateDTO.setTitle(jobTitle);
        jobCreateDTO.setDescription(jobDescription);
        jobCreateDTO.setJobType(type);
        jobCreateDTO.setEarnings(earnings);
        jobCreateDTO.setCompanyId(company.getId());

        Job newJob = new Job(jobCreateDTO);
        return jobRepository.save(newJob);
    }
}
