package ch.zhaw.freelancer4u.model.voucher;

import java.util.List;

import ch.zhaw.freelancer4u.model.Job;

/**
 * Interface for all types of discount vouchers
 */
public interface Voucher {
    /**
     * Calculate the discount amount based on the provided jobs
     * 
     * @param jobs List of jobs to calculate discount for
     * @return The discount amount as a double value
     */
    double getDiscount(List<Job> jobs);
}
