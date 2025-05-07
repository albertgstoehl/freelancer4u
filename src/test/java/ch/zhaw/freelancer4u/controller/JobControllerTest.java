package ch.zhaw.freelancer4u.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.zhaw.freelancer4u.model.Job;
import ch.zhaw.freelancer4u.model.JobCreateDTO;
import ch.zhaw.freelancer4u.model.JobType;
import ch.zhaw.freelancer4u.model.JobsState;
import ch.zhaw.freelancer4u.repository.JobRepository;
import ch.zhaw.freelancer4u.security.TestSecurityConfig;
import ch.zhaw.freelancer4u.service.CompanyService;
import ch.zhaw.freelancer4u.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest(classes = TestSecurityConfig.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class JobControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private CompanyService companyService;

    @Mock
    private UserService userService;

    @MockitoBean(answers = Answers.RETURNS_DEEP_STUBS)
    private OpenAiChatModel chatModel;

    @InjectMocks
    private JobController jobController;

    @Autowired
    private ObjectMapper objectMapper;

    private JobCreateDTO jobCreateDTO;
    private Job testJob;
    private String testJobId = "123e4567-e89b-12d3-a456-426614174000";
    private static final String TEST_TITLE = "Mocked AI Title"; // Für die AI Mock-Antwort

    @BeforeEach
    public void setup() {
        org.mockito.MockitoAnnotations.openMocks(this);
        
        // Setup MockMvc with our controller and mocks
        mockMvc = MockMvcBuilders.standaloneSetup(jobController).build();
        
        // Create JobCreateDTO with all required fields
        jobCreateDTO = new JobCreateDTO();
        jobCreateDTO.setTitle("Test Job Title"); // Added missing title field
        jobCreateDTO.setDescription("Test Job Description");
        jobCreateDTO.setJobType(JobType.IMPLEMENT); // Changed from SOFTWARE_DEVELOPMENT to IMPLEMENT
        jobCreateDTO.setEarnings(150.0);
        jobCreateDTO.setCompanyId("company123");

        testJob = new Job(jobCreateDTO);
        testJob.setId(testJobId);
        testJob.setJobState(JobsState.NEW);
    }

    @BeforeEach
    void setupMockAiResponse() {
        when(chatModel.call(any(Prompt.class))
            .getResult()
            .getOutput()
            .getText()).thenReturn(TEST_TITLE);
    }

    /**
     * Test sequence 1: Create a new job (POST)
     */
    @Test
    public void testCreateJob() throws Exception {
        // Setup mocks
        when(userService.userHasRole("admin")).thenReturn(true);
        when(companyService.companyExists(any())).thenReturn(true);
        jobCreateDTO.setTitle(TEST_TITLE); // Sicherstellen, dass das DTO den gemockten Titel hat
        Job jobWithMockedTitle = new Job(jobCreateDTO);
        jobWithMockedTitle.setId(testJobId);
        jobWithMockedTitle.setJobState(JobsState.NEW);

        when(jobRepository.save(any(Job.class))).thenReturn(jobWithMockedTitle);

        // Perform test
        mockMvc
            .perform(
                post("/api/job")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, TestSecurityConfig.ADMIN)
                    .content(objectMapper.writeValueAsString(jobCreateDTO))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(testJobId)))
            .andExpect(jsonPath("$.title", is(TEST_TITLE)))
            .andExpect(
                jsonPath("$.description", is(jobCreateDTO.getDescription()))
            )
            .andExpect(
                jsonPath("$.jobType", is(jobCreateDTO.getJobType().toString()))
            )
            .andExpect(jsonPath("$.jobState", is("NEW")));
    }

    /**
     * Test sequence 2: Get the job and verify (GET)
     */
    @Test
    public void testGetJob() throws Exception {
        // Setup mocks
        when(jobRepository.findById(testJobId)).thenReturn(
            Optional.of(testJob)
        );

        // Perform test
        mockMvc
            .perform(
                get("/api/job/" + testJobId).header(
                    HttpHeaders.AUTHORIZATION,
                    TestSecurityConfig.USER
                )
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(testJobId)))
            .andExpect(jsonPath("$.description", is(testJob.getDescription())))
            .andExpect(
                jsonPath("$.jobType", is(testJob.getJobType().toString()))
            )
            .andExpect(jsonPath("$.earnings", is(testJob.getEarnings())));
    }

    /**
     * Test sequence 3: Delete the job (DELETE)
     */
    @Test
    public void testDeleteJob() throws Exception {
        // Setup mocks
        when(userService.userHasRole("admin")).thenReturn(true);

        // Perform test
        mockMvc
            .perform(delete("/api/job/" + testJobId)
                    .header(HttpHeaders.AUTHORIZATION, TestSecurityConfig.ADMIN))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", is("DELETED")));
    }

    /**
     * Test sequence 4: Verify job doesn't exist (GET with NOT_FOUND)
     */
    @Test
    public void testJobNoLongerExists() throws Exception {
        // Setup mocks
        when(jobRepository.findById(testJobId)).thenReturn(Optional.empty());

        // Perform test
        mockMvc
            .perform(get("/api/job/" + testJobId)
                    .header(HttpHeaders.AUTHORIZATION, TestSecurityConfig.USER))
            .andExpect(status().isNotFound());
    }
}
