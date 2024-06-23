package br.com.waitomo.services;

import br.com.waitomo.dtos.TokenResponseApi;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Service;
import com.auth0.jwt.exceptions.JWTCreationException;

@Service
public class TokenService {
    public TokenResponseApi createToken(){
        TokenResponseApi tokenResponseApi = new TokenResponseApi();
        try{
            Algorithm algorithm = Algorithm.HMAC256("WaitomoHiper12çCorporation");
            tokenResponseApi.setMessage("Token criado com sucesso!");
            tokenResponseApi.setStatus(200);
            tokenResponseApi.setToken(JWT.create().withIssuer("ListaDeTarefas").sign(algorithm));

            return tokenResponseApi;
        }catch (JWTCreationException e){
            tokenResponseApi.setMessage("Acesso negado: ");
            tokenResponseApi.setStatus(403);
            tokenResponseApi.setToken("");

            return tokenResponseApi;
        }
    }
}
