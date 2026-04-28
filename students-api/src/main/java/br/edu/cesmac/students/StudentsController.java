package br.edu.cesmac.students;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/info")
public class StudentsController {

    @GetMapping
    public Map<String, Object> info(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "service", "students-api",
                "descricao", "Cadastro e consulta de alunos (mock, sem banco).",
                "usuarioToken", jwt.getSubject(),
                "exemploAlunos", List.of(
                        Map.of("id", 1, "nome", "Maria Silva"),
                        Map.of("id", 2, "nome", "João Santos")));
    }
}
