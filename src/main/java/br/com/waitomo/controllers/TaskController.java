package br.com.waitomo.controllers;

import br.com.waitomo.dtos.TaskDTO;
import br.com.waitomo.dtos.TaskSearchDTO;
import br.com.waitomo.services.TaskService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody @Valid TaskDTO taskDTO, UriComponentsBuilder uriBuilder) {
        TaskDTO taskCreate = taskService.createTask(taskDTO);
        URI pathTask = uriBuilder.path("/tasks/{id}").buildAndExpand(taskCreate.getId()).toUri();
        return ResponseEntity.created(pathTask).body(taskCreate);
    }

    @GetMapping("/findAll/{id}")
    public ResponseEntity<List<TaskDTO>> findAllTasks(@PathVariable Long id){
        List<TaskDTO> tasks = taskService.findAllTasks(id);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> findTaskById(@PathVariable @NonNull Long id){
       TaskDTO task = taskService.findTaskById(id);
       return ResponseEntity.ok(task);
    }

    @PostMapping("/search")
    public ResponseEntity<List<TaskDTO>> findTaskSearch(@RequestBody @Valid TaskSearchDTO taskSearchDTO){
        List<TaskDTO> taskDTOList = taskService.findTaskSearch(taskSearchDTO.getSearch(),taskSearchDTO.getUser_id().getId());
        return ResponseEntity.ok(taskDTOList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTaskById(@PathVariable Long id, @RequestBody @Valid TaskDTO taskDTO){
        TaskDTO taskUpdate = taskService.updateTaskById(id,taskDTO);
        return ResponseEntity.ok(taskUpdate);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable Long id){
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }
}
