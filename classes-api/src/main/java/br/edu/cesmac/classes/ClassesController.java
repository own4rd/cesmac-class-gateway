package br.edu.cesmac.classes;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/info")
public class ClassesController {

    @GetMapping
    public Map<String, Object> info(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "service", "classes-api",
                "descricao", "Turmas e horários (mock).",
                "usuarioToken", jwt.getSubject(),
                "exemploTurmas", List.of(
                        Map.of("turma", "ADS-2025.1-M", "disciplina", "Programação Web"),
                        Map.of("turma", "ADS-2025.1-N", "disciplina", "Banco de Dados")));
    }
}
