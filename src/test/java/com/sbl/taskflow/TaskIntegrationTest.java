package com.sbl.taskflow;

import com.sbl.taskflow.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class TaskIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TaskRepository taskRepository;

    // 2. Clean the database before every test
    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    void connectionEstablished() {
        // Simple test to ensure the container starts and context loads
        assert postgres.isCreated();
        assert postgres.isRunning();
    }

    @Test
    void shouldCreateTaskSuccessfully() throws Exception {
        // Simulate the JSON payload from a frontend or postman
        String taskJson = """
                {
                    "title": "Nail the interview",
                    "description": "Build a Spring Boot project with Docker and K8s",
                    "status": "PENDING"
                }
                """;

        // Perform the POST request and assert the results
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Nail the interview"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldFetchAllTasks() throws Exception {
        // Arrange: Add a task to the DB first
        String taskJson = """
                {
                    "title": "Review Java 21 features"
                }
                """;
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskJson));

        // Act & Assert: Fetch tasks and ensure there is 1 in the list
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Review Java 21 features"));
    }
}