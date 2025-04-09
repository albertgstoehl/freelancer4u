package ch.zhaw.freelancer4u.model.voucher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.zhaw.freelancer4u.model.Job;
import ch.zhaw.freelancer4u.model.JobType;

public class PercentageVoucherTest {
    
    @Test
    public void testEmpty() {
        // Test with empty job list
        PercentageVoucher voucher = new PercentageVoucher(10.0);
        List<Job> jobs = new ArrayList<>();
        
        double discount = voucher.getDiscount(jobs);
        
        assertEquals(0.0, discount);
    }
    
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 5, 20, 49, 50})
    public void testSingleJobWithDifferentPercentages(int percentage) {
        // Test with one job having earnings of exactly 50.0 and different percentages
        PercentageVoucher voucher = new PercentageVoucher(percentage);
        Job job = createJob("Test Job", 50.0);
        List<Job> jobs = Arrays.asList(job);
        
        double discount = voucher.getDiscount(jobs);
        
        assertEquals(percentage * 50.0 / 100.0, discount);
    }
    
    @Test
    public void testTwoJobsWithFortyTwoPercentDiscount() {
        // Test with two jobs and 42% discount
        PercentageVoucher voucher = new PercentageVoucher(42.0);
        Job job1 = createJob("Test Job 1", 42.0);
        Job job2 = createJob("Test Job 2", 77.0);
        
        List<Job> jobs = Arrays.asList(job1, job2);
        
        double discount = voucher.getDiscount(jobs);
        
        assertEquals(49.98, discount, 0.001); // Using delta for floating point comparison
    }
    
    private Job createJob(String title, double earnings) {
        Job job = new Job();
        job.setTitle(title);
        job.setDescription("Description");
        job.setJobType(JobType.OTHER);
        job.setCompanyId("company123");
        job.setEarnings(earnings);
        return job;
    }
}
