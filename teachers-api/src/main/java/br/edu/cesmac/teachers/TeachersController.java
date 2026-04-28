package br.edu.cesmac.teachers;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/info")
public class TeachersController {

    @GetMapping
    public Map<String, Object> info(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "service", "teachers-api",
                "descricao", "Professores e alocação (mock).",
                "usuarioToken", jwt.getSubject(),
                "exemploProfessores", List.of(
                        Map.of("id", 1, "nome", "Prof. Ana Costa"),
                        Map.of("id", 2, "nome", "Prof. Carlos Lima")));
    }
}
