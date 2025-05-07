package ch.zhaw.freelancer4u.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import ch.zhaw.freelancer4u.model.Job;
import ch.zhaw.freelancer4u.model.JobCreateDTO;
import ch.zhaw.freelancer4u.model.JobType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class CreateRandomJobsControllerTest {

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec responseSpec;

    @InjectMocks
    private CreateRandomJobsController createRandomJobsController;

    @Test
    public void testRandomJob_Success() {
        // Arrange
        JobCreateDTO jobCreateDTO = new JobCreateDTO();
        jobCreateDTO.setTitle("title");
        jobCreateDTO.setDescription("desc1");
        jobCreateDTO.setJobType(JobType.IMPLEMENT);
        jobCreateDTO.setEarnings(50.0);
        jobCreateDTO.setCompanyId("");
        var job = new Job(jobCreateDTO);

        when(chatClient.prompt("Create a random job")).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.entity(Job.class)).thenReturn(job);

        // Act
        ResponseEntity<Job> response = createRandomJobsController.randomJob();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(job, response.getBody());
    }

    // You might want to add a test for exception handling if that's a possibility
    @Test
    public void testRandomJob_ExceptionThrown() {
        // Arrange
        when(chatClient.prompt("Create a random job")).thenThrow(new RuntimeException("Chat service unavailable"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> createRandomJobsController.randomJob());
    }

    @Test
    public void testRandomJobs_Success() {
        // Arrange
        JobCreateDTO dto1 = new JobCreateDTO();
        dto1.setTitle("Job 1");
        dto1.setDescription("Description 1");
        dto1.setJobType(JobType.OTHER);
        dto1.setEarnings(100.0);
        dto1.setCompanyId("");
        Job job1 = new Job(dto1);

        JobCreateDTO dto2 = new JobCreateDTO();
        dto2.setTitle("Job 2");
        dto2.setDescription("Description 2");
        dto2.setJobType(JobType.IMPLEMENT);
        dto2.setEarnings(200.0);
        dto2.setCompanyId("");
        Job job2 = new Job(dto2);

        JobCreateDTO dto3 = new JobCreateDTO();
        dto3.setTitle("Job 3");
        dto3.setDescription("Description 3");
        dto3.setJobType(JobType.TEST);
        dto3.setEarnings(150.0);
        dto3.setCompanyId("");
        Job job3 = new Job(dto3);

        List<Job> expectedJobs = List.of(job1, job2, job3);

        ParameterizedTypeReference<List<Job>> typeRef = new ParameterizedTypeReference<List<Job>>() {
        };

        when(chatClient.prompt("Create three random jobs")).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.entity(typeRef)).thenReturn(expectedJobs);

        // Act
        ResponseEntity<List<Job>> response = createRandomJobsController.randomJobs();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().size());
        assertEquals(expectedJobs, response.getBody());
    }

    @Test
    public void testRandomJobs_EmptyList() {
        // Arrange
        List<Job> emptyList = Collections.emptyList();
        ParameterizedTypeReference<List<Job>> typeRef = new ParameterizedTypeReference<List<Job>>() {
        };

        when(chatClient.prompt("Create three random jobs")).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(responseSpec);
        when(responseSpec.entity(typeRef)).thenReturn(emptyList);

        // Act
        ResponseEntity<List<Job>> response = createRandomJobsController.randomJobs();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    public void testRandomJobs_ExceptionThrown() {
        // Arrange
        when(chatClient.prompt("Create three random jobs")).thenThrow(new RuntimeException("Chat service unavailable"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> createRandomJobsController.randomJobs());
    }
}