package ch.zhaw.freelancer4u.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Data Transfer Object für die Erstellung eines Jobs
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Document
public class JobCreateDTO {
    private String title;
    private String description;
    private JobType jobType;
    private Double earnings;
    private String companyId;

    /**
     * Konstruktor für JobCreateDTO
     * @param title Titel des Jobs
     * @param description Beschreibung des Jobs
     * @param jobType Typ des Jobs
     * @param earnings Verdienst für den Job
     * @param companyId ID des Unternehmens
     */
    public JobCreateDTO(String title, String description, JobType jobType, Double earnings, String companyId) {
        this.title = title;
        this.description = description;
        this.jobType = jobType;
        this.earnings = earnings;
        this.companyId = companyId;
    }
} 