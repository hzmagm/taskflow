package com.sbl.taskflow.service;

import com.sbl.taskflow.entity.Task;
import com.sbl.taskflow.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public Task createTask(Task task){
        return taskRepository.save(task);
    }
    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }
    public Task getTaskById(Long id){
        return taskRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Task not found with id : "+ id));
    }
    public Task updateTask(Long id,Task taskDetails){
        Task task=getTaskById(id);
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        return taskRepository.save(task);
    }
    public void deleteTask(Long id){
        Task task = getTaskById(id);
        taskRepository.delete(task);
    }
}
