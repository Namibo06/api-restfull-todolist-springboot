package br.com.waitomo.repositories;

import br.com.waitomo.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserModel,Long> {
    @Query(value = "SELECT id, username, email, password FROM tb_user WHERE email = ?1 AND password = ?2",nativeQuery = true)
    UserModel findByEmailAndPassword(String email,String password);
}
