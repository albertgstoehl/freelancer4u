package ch.zhaw.freelancer4u.model.voucher;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.zhaw.freelancer4u.model.Job;
import ch.zhaw.freelancer4u.model.JobType;

public class FiveBucksVoucherTest {
    
    @Test
    public void testEmpty() {
        // Test with empty job list
        Voucher voucher = new FiveBucksVoucher();
        List<Job> jobs = new ArrayList<>();
        
        double discount = voucher.getDiscount(jobs);
        
        assertEquals(0.0, discount);
    }
    
    @Test
    public void testTen() {
        // Test with one job having earnings of exactly 10.0
        Voucher voucher = new FiveBucksVoucher();
        Job job = new Job();
        job.setJobTitle("Test Job");
        job.setJobDescription("Description");
        job.setJobType(JobType.IT);
        job.setCompanyId("company123");
        job.setEarnings(10.0);
        List<Job> jobs = Arrays.asList(job);
        
        double discount = voucher.getDiscount(jobs);
        
        assertEquals(5.0, discount);
    }
    
    @Test
    public void testLessThanTen() {
        // Test with jobs having total earnings less than 10.0
        Voucher voucher = new FiveBucksVoucher();
        Job job1 = new Job();
        job1.setJobTitle("Test Job 1");
        job1.setJobDescription("Description 1");
        job1.setJobType(JobType.IT);
        job1.setCompanyId("company1");
        job1.setEarnings(4.0);
        
        Job job2 = new Job();
        job2.setJobTitle("Test Job 2");
        job2.setJobDescription("Description 2");
        job2.setJobType(JobType.ACCOUNTING);
        job2.setCompanyId("company2");
        job2.setEarnings(5.0);
        
        List<Job> jobs = Arrays.asList(job1, job2);
        
        double discount = voucher.getDiscount(jobs);
        
        assertEquals(0.0, discount);
    }
    
    @Test
    public void testMoreThanTen() {
        // Test with jobs having total earnings more than 10.0
        Voucher voucher = new FiveBucksVoucher();
        Job job1 = new Job();
        job1.setJobTitle("Test Job 1");
        job1.setJobDescription("Description 1");
        job1.setJobType(JobType.IT);
        job1.setCompanyId("company1");
        job1.setEarnings(7.0);
        
        Job job2 = new Job();
        job2.setJobTitle("Test Job 2");
        job2.setJobDescription("Description 2");
        job2.setJobType(JobType.HR);
        job2.setCompanyId("company2");
        job2.setEarnings(8.0);
        
        List<Job> jobs = Arrays.asList(job1, job2);
        
        double discount = voucher.getDiscount(jobs);
        
        assertEquals(5.0, discount);
    }
}
