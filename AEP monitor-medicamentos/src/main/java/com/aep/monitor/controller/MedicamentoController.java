package com.aep.monitor.controller;

import com.aep.monitor.dto.AtualizarHorarioRequest;
import com.aep.monitor.model.Medicamento;
import com.aep.monitor.repository.MedicamentoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/medicamentos")
public class MedicamentoController {

    private final MedicamentoRepository repository;

    public MedicamentoController(MedicamentoRepository repository) {
        this.repository = repository;
    }

    @Operation(summary = "Cadastrar um novo medicamento")
    @ApiResponse(responseCode = "201", description = "Medicamento cadastrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @PostMapping
    public ResponseEntity<Medicamento> cadastrar(@Valid @RequestBody Medicamento medicamento) {
        medicamento.setId(null);
        Medicamento salvo = repository.save(medicamento);
        return ResponseEntity.created(URI.create("/medicamentos/" + salvo.getId())).body(salvo);
    }

    @Operation(summary = "Listar todos os medicamentos cadastrados")
    @ApiResponse(responseCode = "200", description = "Lista de medicamentos")
    @GetMapping
    public ResponseEntity<List<Medicamento>> listarTodos() {
        return ResponseEntity.ok(repository.findAll());
    }

    @Operation(summary = "Buscar um medicamento por id")
    @ApiResponse(responseCode = "200", description = "Medicamento encontrado")
    @ApiResponse(responseCode = "404", description = "Medicamento não encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<Medicamento> buscarPorId(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Atualizar o horário de um medicamento")
    @ApiResponse(responseCode = "200", description = "Horário atualizado")
    @ApiResponse(responseCode = "404", description = "Medicamento não encontrado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @PatchMapping("/{id}/horario")
    public ResponseEntity<Medicamento> atualizarHorario(@PathVariable String id,
                                                          @Valid @RequestBody AtualizarHorarioRequest request) {
        Optional<Medicamento> existente = repository.findById(id);
        if (existente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Medicamento medicamento = existente.get();
        medicamento.setHorario(request.getHorario());
        return ResponseEntity.ok(repository.save(medicamento));
    }

    @Operation(summary = "Marcar um medicamento como tomado")
    @ApiResponse(responseCode = "200", description = "Medicamento marcado como tomado")
    @ApiResponse(responseCode = "404", description = "Medicamento não encontrado")
    @PatchMapping("/{id}/tomado")
    public ResponseEntity<Medicamento> marcarComoTomado(@PathVariable String id) {
        Optional<Medicamento> existente = repository.findById(id);
        if (existente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Medicamento medicamento = existente.get();
        medicamento.marcarComoTomado();
        return ResponseEntity.ok(repository.save(medicamento));
    }

    @Operation(summary = "Remover um medicamento")
    @ApiResponse(responseCode = "204", description = "Medicamento removido")
    @ApiResponse(responseCode = "404", description = "Medicamento não encontrado")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable String id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
