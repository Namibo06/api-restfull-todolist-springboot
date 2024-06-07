package br.com.waitomo.repositories;

import br.com.waitomo.models.TaskModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskModel,Long> {
    @Query(value = "SELECT * FROM tb_task WHERE user_id = ?1",nativeQuery = true)
    List<TaskModel> findTasksByUserId(Long user_id);

    @Query(value = "SELECT * FROM tb_task WHERE user_id = :user_id AND title LIKE %:search%",nativeQuery = true)
    List<TaskModel> findTasksSearchQuery(@Param("search") String search,@Param("user_id") Long user_id);
}
