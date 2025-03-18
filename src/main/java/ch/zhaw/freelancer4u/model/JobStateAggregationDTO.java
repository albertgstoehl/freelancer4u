package ch.zhaw.freelancer4u.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class JobStateAggregationDTO {
    private String id;
    private List<String> jobIds;
    private String count;
} 