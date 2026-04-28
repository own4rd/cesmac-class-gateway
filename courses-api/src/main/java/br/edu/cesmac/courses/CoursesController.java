package br.edu.cesmac.courses;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/info")
public class CoursesController {

    @GetMapping
    public Map<String, Object> info(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "service", "courses-api",
                "descricao", "Disciplinas e ementas (mock).",
                "usuarioToken", jwt.getSubject(),
                "exemploCursos", List.of(
                        Map.of("codigo", "CS101", "nome", "Programação I"),
                        Map.of("codigo", "CS102", "nome", "Estruturas de Dados")));
    }
}
