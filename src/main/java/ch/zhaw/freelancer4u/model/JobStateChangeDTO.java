package ch.zhaw.freelancer4u.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO für die Änderung des Job-Status
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobStateChangeDTO {
    private String jobId;
    private String freelancerId;
} 