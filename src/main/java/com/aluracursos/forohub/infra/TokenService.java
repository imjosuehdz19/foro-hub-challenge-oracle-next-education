package com.aluracursos.forohub.infra;
import com.auth0.jwt.JWT;
import com.aluracursos.forohub.domain.usuario.Usuario;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String apiSecret;

    public String generarToken(Usuario usuario){
        try {
            Algorithm algorithm = Algorithm.HMAC256(apiSecret);
            return JWT.create()
                    .withIssuer("Foro Hub")
                    .withSubject(usuario.getLogin())
                    .withClaim("id", usuario.getId())
                    .withExpiresAt(generarFechaDeExpiracion())
                    .sign(algorithm);
        }catch (JWTCreationException exception){
            throw new RuntimeException("Error al generar el token JWT", exception);
        }
    }

    private Instant generarFechaDeExpiracion(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

    public String getSubject(String token){
        if (token == null){
            throw new RuntimeException("Token nulo.");
        }

        try {
            Algorithm algorithm = Algorithm.HMAC256(apiSecret);
            return JWT.require(algorithm)
                    .withIssuer("Foro Hub")
                    .build()
                    .verify(token)
                    .getSubject();
        }catch (JWTVerificationException exception){
            throw new RuntimeException("Token JWT inválido o expirado");
        }
    }
}
