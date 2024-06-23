package br.com.waitomo.services;

import br.com.waitomo.dtos.TaskDTO;
import br.com.waitomo.dtos.TaskSearchDTO;
import br.com.waitomo.dtos.UserDTO;
import br.com.waitomo.models.TaskModel;
import br.com.waitomo.models.UserModel;
import br.com.waitomo.repositories.TaskRepository;
import br.com.waitomo.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    //ver create na hora do retorno,tá voltando null o id,e o resto do user
    public TaskDTO createTask(TaskDTO taskDTO){
        // Verificando se user_id existe
        UserModel userIdModel = userRepository.findById(taskDTO.getUser_id().getId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        // Criar TaskModel manualmente e atribuir o UserModel
        TaskModel taskModel = new TaskModel();
        taskModel.setTitle(taskDTO.getTitle());
        taskModel.setDescription(taskDTO.getDescription());
        taskModel.setUserModel(userIdModel);

        // Salvar TaskModel no banco de dados
        TaskModel savedTaskModel = taskRepository.save(taskModel);

        // Mapear o TaskModel salvo de volta para TaskDTO e retornar
        return mapTaskModelToDTO(savedTaskModel);
    }

    public List<TaskDTO> findAllTasks(Long id) {
        List<TaskModel> taskModels = taskRepository.findTasksByUserId(id);
        List<TaskDTO> taskDTOs = new ArrayList<>();

        for (TaskModel taskModel : taskModels) {
            TaskDTO taskDTO = modelMapper.map(taskModel, TaskDTO.class);

            // Mapear o UserModel para UserDTO
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

        // Se TaskModel tiver um UserModel não nulo, mapeie-o para UserDTO
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

        // Mantém o user_id existente na tarefa ao atualizar apenas o título e a descrição
        UserModel existingUser = taskModel.getUserModel();

        // Mapeia os campos atualizados do DTO para a entidade
        taskModel.setTitle(taskDTO.getTitle());
        taskModel.setDescription(taskDTO.getDescription());

        // Define o user_id existente na tarefa
        taskModel.setUserModel(existingUser);

        // Salva a tarefa atualizada no repositório
        TaskModel updatedTaskModel = taskRepository.save(taskModel);

        // Mapeia a tarefa atualizada para DTO, incluindo o user_id
        TaskDTO updatedTaskDTO = modelMapper.map(updatedTaskModel, TaskDTO.class);
        updatedTaskDTO.setUser_id(modelMapper.map(existingUser, UserDTO.class));

        // Retorna o DTO atualizado
        return updatedTaskDTO;
    }

    public void deleteTaskById(Long id){
        TaskModel taskModel = taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada"));
        taskRepository.deleteById(id);
    }

    //método para mapear os dados do task no create
    private TaskDTO mapTaskModelToDTO(TaskModel taskModel) {
        //criar uma nova instancia do taskDTO
        //settar os atributos com base nos getters do TaskModel,famoso mapear model para dto
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(taskModel.getId());
        taskDTO.setTitle(taskModel.getTitle());
        taskDTO.setDescription(taskModel.getDescription());

        // Certifique-se de mapear corretamente o UserModel para UserDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setId(taskModel.getUserModel().getId());
        userDTO.setUsername(taskModel.getUserModel().getUsername());
        userDTO.setEmail(taskModel.getUserModel().getEmail());
        taskDTO.setUser_id(userDTO);

        return taskDTO;
    }
}
