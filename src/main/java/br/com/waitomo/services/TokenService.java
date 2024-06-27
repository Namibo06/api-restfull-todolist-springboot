package br.com.waitomo.services;

import br.com.waitomo.dtos.TokenResponseApi;
import br.com.waitomo.repositories.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.auth0.jwt.exceptions.JWTCreationException;

import java.util.Date;

@Service
public class TokenService {
    @Autowired
    UserService userService;

    public String createToken(){
        try{

            Algorithm algorithm = Algorithm.HMAC256("WaitomoHiper12çCorporation");
            Date now = new Date();
            Date expirationDate = new Date(now.getTime() + 3600 * 1000); //valido por uma hora

            return JWT.create()
                    .withIssuer("ListaDeTarefas")
                    .withIssuedAt(now)
                    .withExpiresAt(expirationDate)
                    .sign(algorithm);

        }catch (JWTCreationException e){
            throw new RuntimeException("Não foi possivel criar o token");
        }
    }

}
