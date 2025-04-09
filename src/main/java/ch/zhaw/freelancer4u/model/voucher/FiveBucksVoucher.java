package ch.zhaw.freelancer4u.model.voucher;

import java.util.List;

import ch.zhaw.freelancer4u.model.Job;

/**
 * A voucher that gives a fixed discount of 5.0 
 * when the sum of job earnings is at least 10.0
 */
public class FiveBucksVoucher implements Voucher {

    /**
     * Calculate the discount amount based on the provided jobs
     * Returns 5.0 if the sum of earnings is >= 10.0, otherwise returns 0.0
     * 
     * @param jobs List of jobs to calculate discount for
     * @return The discount amount as a double value
     */
    @Override
    public double getDiscount(List<Job> jobs) {
        double totalEarnings = jobs.stream().mapToDouble(Job::getEarnings).sum();
        return totalEarnings >= 10.0 ? 5.0 : 0.0;
    }
}
