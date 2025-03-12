package ch.zhaw.freelancer4u.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Klasse zur Repräsentation eines Jobs
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Document
public class Job {
    @Id
    private String id;
    private String title;
    private String description;
    private JobType jobType;
    private JobsState jobState = JobsState.NEW;
    private double earnings;
    private String companyId;
    @Field(write = Field.Write.ALWAYS)
    private String freelancerId = null;

    /**
     * Konstruktor für Job mit allen Attributen aus dem DTO
     * @param jobCreateDTO DTO mit den Job-Informationen
     */
    public Job(JobCreateDTO jobCreateDTO) {
        this.title = jobCreateDTO.getTitle();
        this.description = jobCreateDTO.getDescription();
        this.jobType = jobCreateDTO.getJobType();
        this.earnings = jobCreateDTO.getEarnings();
        this.companyId = jobCreateDTO.getCompanyId();
    }
} 