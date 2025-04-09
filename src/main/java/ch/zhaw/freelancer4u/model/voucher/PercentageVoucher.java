package ch.zhaw.freelancer4u.model.voucher;

import java.util.List;

import ch.zhaw.freelancer4u.model.Job;

public class PercentageVoucher implements Voucher {
    private final double percentage;
    
    public PercentageVoucher(double percentage) {
        this.percentage = percentage;
    }
    
    @Override
    public double getDiscount(List<Job> jobs) {
        if (jobs == null || jobs.isEmpty()) {
            return 0.0;
        }
        
        double totalEarnings = jobs.stream()
            .mapToDouble(Job::getEarnings)
            .sum();
            
        return (percentage / 100.0) * totalEarnings;
    }
}
