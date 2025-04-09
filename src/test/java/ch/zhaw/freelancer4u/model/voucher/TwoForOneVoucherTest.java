package ch.zhaw.freelancer4u.model.voucher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;

import ch.zhaw.freelancer4u.model.Job;
import ch.zhaw.freelancer4u.model.JobType;

public class TwoForOneVoucherTest {
    
    @Test
    public void testDifferentTypes() {
        // Test with two jobs of different types
        TwoForOneVoucher voucher = new TwoForOneVoucher(JobType.IMPLEMENT);
        
        Job job1 = createJob("Implement Feature", 100.0, JobType.IMPLEMENT);
        Job job2 = createJob("Review Code", 50.0, JobType.REVIEW);
        
        List<Job> jobs = Arrays.asList(job1, job2);
        
        double discount = voucher.getDiscount(jobs);
        
        assertEquals(0.0, discount);
    }
    
    @Test
    public void testTwoSameType() {
        // Test with two jobs of the same type
        TwoForOneVoucher voucher = new TwoForOneVoucher(JobType.TEST);
        
        Job job1 = createJob("Test Feature 1", 77.0, JobType.TEST);
        Job job2 = createJob("Test Feature 2", 33.0, JobType.TEST);
        
        List<Job> jobs = Arrays.asList(job1, job2);
        
        double discount = voucher.getDiscount(jobs);
        
        assertEquals(55.0, discount);
    }
    
    @Test
    public void testThreeSameType() {
        // Test with three jobs of the same type
        TwoForOneVoucher voucher = new TwoForOneVoucher(JobType.REVIEW);
        
        Job job1 = createJob("Review Code 1", 77.0, JobType.REVIEW);
        Job job2 = createJob("Review Code 2", 33.0, JobType.REVIEW);
        Job job3 = createJob("Review Code 3", 99.0, JobType.REVIEW);
        
        List<Job> jobs = Arrays.asList(job1, job2, job3);
        
        double discount = voucher.getDiscount(jobs);
        
        assertEquals(104.5, discount);
    }
    
    @Test
    public void testMixedTypes() {
        // Test with mixed job types
        TwoForOneVoucher voucher = new TwoForOneVoucher(JobType.REVIEW);
        
        Job job1 = createJob("Review Code 1", 77.0, JobType.REVIEW);
        Job job2 = createJob("Review Code 2", 33.0, JobType.REVIEW);
        Job job3 = createJob("Test Feature", 99.0, JobType.TEST);
        
        List<Job> jobs = Arrays.asList(job1, job2, job3);
        
        double discount = voucher.getDiscount(jobs);
        
        assertEquals(55.0, discount);
    }
    
    @ParameterizedTest
    @CsvSource({ "0,0", "1,0", "2,77", "3,115.5", "4,154" })
    public void testDifferentCounts(ArgumentsAccessor args) {
        int count = args.getInteger(0);
        double expectedDiscount = args.getDouble(1);
        
        TwoForOneVoucher voucher = new TwoForOneVoucher(JobType.DESIGN);
        
        List<Job> jobs = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            jobs.add(createJob("Design Task " + i, 77.0, JobType.DESIGN));
        }
        
        double discount = voucher.getDiscount(jobs);
        
        assertEquals(expectedDiscount, discount, 0.001);
    }
    
    private Job createJob(String title, double earnings, JobType jobType) {
        Job job = new Job();
        job.setTitle(title);
        job.setDescription("Description");
        job.setJobType(jobType);
        job.setCompanyId("company123");
        job.setEarnings(earnings);
        return job;
    }
}
