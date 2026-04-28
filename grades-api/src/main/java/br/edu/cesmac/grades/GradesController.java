package br.edu.cesmac.grades;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/info")
public class GradesController {

    @GetMapping
    public Map<String, Object> info(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "service", "grades-api",
                "descricao", "Notas e médias (mock).",
                "usuarioToken", jwt.getSubject(),
                "exemploNotas", List.of(
                        Map.of("alunoId", 1, "disciplina", "CS101", "nota", 8.5),
                        Map.of("alunoId", 2, "disciplina", "CS101", "nota", 9.0)));
    }
}
