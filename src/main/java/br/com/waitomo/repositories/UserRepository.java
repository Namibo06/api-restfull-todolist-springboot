package br.com.waitomo.repositories;

import br.com.waitomo.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserModel,Long> {
    UserDetails findByEmail(String email);

    @Query(value = "SELECT token FROM railway.tb_user WHERE email = ?1",nativeQuery = true)
    UserModel findByEmailUpdateToken(String email);
}
