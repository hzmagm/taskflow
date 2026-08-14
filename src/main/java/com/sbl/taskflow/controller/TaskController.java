package com.sbl.taskflow.controller;

import com.sbl.taskflow.entity.Task;
import com.sbl.taskflow.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(taskService.createTask(task));
    }
    @GetMapping("/AllTasks/{id}")
    public ResponseEntity<List<Task>> getAllTasks(@PathVariable Long id){
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/taskbyid/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long Id){
        return ResponseEntity.ok(taskService.getTaskById(Id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long Id){
        taskService.deleteTask(Id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long Id,@RequestBody Task task){
        return ResponseEntity.ok(taskService.updateTask(Id,task));
    }

}
