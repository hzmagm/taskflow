package com.sbl.taskflow;

import com.sbl.taskflow.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.testcontainers.junit.jupiter.Container;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.mysql.MySQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class TaskIntegrationTest {

    @Container
    @ServiceConnection
    static MySQLContainer mysql = new MySQLContainer("mysql:8.0");

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
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

        mockMvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON).content(taskJson)).andExpect(status().isCreated()).andExpect(jsonPath("$.id").exists()).andExpect(jsonPath("$.title").value("Nail the interview")).andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldFetchAllTasks() throws Exception {
        String taskJson = """
                {
                    "title": "Review Java 21 features",
                    "description": "Integration test description",
                    "status": "PENDING"\s
                }
                """;
        mockMvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON).content(taskJson)).andExpect(status().isCreated());
        mockMvc.perform(get("/api/tasks")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].title").value("Review Java 21 features"));
    }
}