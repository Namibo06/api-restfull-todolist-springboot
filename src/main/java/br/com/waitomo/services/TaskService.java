package br.com.waitomo.services;

import br.com.waitomo.dtos.TaskDTO;
import br.com.waitomo.dtos.UserDTO;
import br.com.waitomo.models.TaskModel;
import br.com.waitomo.models.UserModel;
import br.com.waitomo.repositories.TaskRepository;
import br.com.waitomo.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public TaskDTO createTask(TaskDTO taskDTO){
        UserModel userIdModel = userRepository.findById(taskDTO.getUser_id().getId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        TaskModel taskModel = new TaskModel();
        taskModel.setTitle(taskDTO.getTitle());
        taskModel.setDescription(taskDTO.getDescription());
        taskModel.setUserModel(userIdModel);

        TaskModel savedTaskModel = taskRepository.save(taskModel);

        return mapTaskModelToDTO(savedTaskModel);
    }

    public List<TaskDTO> findAllTasks(Long id) {
        List<TaskModel> taskModels = taskRepository.findTasksByUserId(id);
        List<TaskDTO> taskDTOs = new ArrayList<>();

        for (TaskModel taskModel : taskModels) {
            TaskDTO taskDTO = modelMapper.map(taskModel, TaskDTO.class);

            if (taskModel.getUserModel() != null) {
                UserDTO userDTO = modelMapper.map(taskModel.getUserModel(), UserDTO.class);
                taskDTO.setUser_id(userDTO);
            }
            taskDTOs.add(taskDTO);
        }
        return taskDTOs;
    }

    public TaskDTO findTaskById(Long id){
        TaskModel taskModel = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada"));

        TaskDTO taskDTO = modelMapper.map(taskModel, TaskDTO.class);

        if (taskModel.getUserModel() != null) {
            UserDTO userDTO = modelMapper.map(taskModel.getUserModel(), UserDTO.class);
            taskDTO.setUser_id(userDTO);
        }

        return taskDTO;
    }

    public List<TaskDTO> findTaskSearch(String search, Long userId) {
        List<TaskModel> tasks = taskRepository.findTasksSearchQuery(search, userId);
        return tasks.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private TaskDTO convertToDTO(TaskModel taskModel) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(taskModel.getId());
        taskDTO.setTitle(taskModel.getTitle());
        taskDTO.setDescription(taskModel.getDescription());

        UserModel userModel = taskModel.getUserModel();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userModel.getId());
        userDTO.setUsername(userModel.getUsername());
        userDTO.setEmail(userModel.getEmail());

        taskDTO.setUser_id(userDTO);

        return taskDTO;
    }

    public TaskDTO updateTaskById(Long id,TaskDTO taskDTO){
        TaskModel taskModel = taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada"));
        UserModel existingUser = taskModel.getUserModel();

        taskModel.setTitle(taskDTO.getTitle());
        taskModel.setDescription(taskDTO.getDescription());
        taskModel.setUserModel(existingUser);
        TaskModel updatedTaskModel = taskRepository.save(taskModel);

        TaskDTO updatedTaskDTO = modelMapper.map(updatedTaskModel, TaskDTO.class);
        updatedTaskDTO.setUser_id(modelMapper.map(existingUser, UserDTO.class));

        return updatedTaskDTO;
    }

    public void deleteTaskById(Long id){
        TaskModel taskModel = taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada"));
        taskRepository.deleteById(id);
    }

    private TaskDTO mapTaskModelToDTO(TaskModel taskModel) {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(taskModel.getId());
        taskDTO.setTitle(taskModel.getTitle());
        taskDTO.setDescription(taskModel.getDescription());

        UserDTO userDTO = new UserDTO();
        userDTO.setId(taskModel.getUserModel().getId());
        userDTO.setUsername(taskModel.getUserModel().getUsername());
        userDTO.setEmail(taskModel.getUserModel().getEmail());
        taskDTO.setUser_id(userDTO);

        return taskDTO;
    }
}
