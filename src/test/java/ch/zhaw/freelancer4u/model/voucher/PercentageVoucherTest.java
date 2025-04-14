package ch.zhaw.freelancer4u.model.voucher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    
    @Test
    public void testPercentageGreaterThan50() {
        // Test creating voucher with percentage > 50
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            new PercentageVoucher(51.0);
        });
        
        assertEquals("Error: Discount value must less or equal 50.", exception.getMessage());
    }
    
    @Test
    public void testPercentageLessThanOrEqualToZero() {
        // Test creating voucher with percentage = 0
        RuntimeException exceptionZero = assertThrows(RuntimeException.class, () -> {
            new PercentageVoucher(0.0);
        });
        
        assertEquals("Error: Discount value must be greater zero.", exceptionZero.getMessage());
        
        // Test creating voucher with negative percentage
        RuntimeException exceptionNegative = assertThrows(RuntimeException.class, () -> {
            new PercentageVoucher(-10.0);
        });
        
        assertEquals("Error: Discount value must be greater zero.", exceptionNegative.getMessage());
    }
    
    @Test
    public void testTwoJobsWithMockito() {
        // Create a new voucher with 20% discount
        PercentageVoucher voucher = new PercentageVoucher(20.0);
        
        // Create mock jobs
        Job mockedJob1 = mock(Job.class);
        Job mockedJob2 = mock(Job.class);
        
        // Set up the mock behavior
        when(mockedJob1.getEarnings()).thenReturn(42.0);
        when(mockedJob2.getEarnings()).thenReturn(77.0);
        
        // Create list with the mocked jobs
        List<Job> jobs = Arrays.asList(mockedJob1, mockedJob2);
        
        // Calculate the discount
        double discount = voucher.getDiscount(jobs);
        
        // Expected: 20% of (42.0 + 77.0) = 20% of 119.0 = 23.8
        assertEquals(23.8, discount, 0.001);
    }
}
