package ch.zhaw.freelancer4u.model.voucher;

import java.util.List;

import ch.zhaw.freelancer4u.model.Job;
import ch.zhaw.freelancer4u.model.JobType;

/**
 * A voucher that gives a 50% discount on jobs of a specific type
 * when there are at least two jobs of that type
 */
public class TwoForOneVoucher implements Voucher {
    private final JobType targetJobType;
    
    /**
     * Creates a new TwoForOneVoucher for the specified job type
     * 
     * @param jobType The job type for which the discount applies
     */
    public TwoForOneVoucher(JobType jobType) {
        this.targetJobType = jobType;
    }
    
    /**
     * Calculates a 50% discount on the total earnings of jobs matching the target type,
     * but only if there are at least two jobs of that type
     * 
     * @param jobs List of jobs to calculate discount for
     * @return 50% of the sum of earnings for matching jobs if 2+ exist, otherwise 0.0
     */
    @Override
    public double getDiscount(List<Job> jobs) {
        if (jobs == null || jobs.isEmpty()) {
            return 0.0;
        }
        
        List<Job> matchingJobs = jobs.stream()
            .filter(job -> job.getJobType() == targetJobType)
            .toList();
            
        if (matchingJobs.size() >= 2) {
            double totalMatchingEarnings = matchingJobs.stream()
                .mapToDouble(Job::getEarnings)
                .sum();
                
            return totalMatchingEarnings * 0.5; // 50% discount
        }
        
        return 0.0; // No discount if less than 2 matching jobs
    }
}
