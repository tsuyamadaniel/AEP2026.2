package com.aep.monitor.controller;

import com.aep.monitor.model.Medicamento;
import com.aep.monitor.repository.MedicamentoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MedicamentoController.class)
class MedicamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MedicamentoRepository repository;

    private Medicamento medicamentoValido() {
        Medicamento m = new Medicamento("Maria", "Dipirona", "500mg", "08:00");
        m.setId("1");
        return m;
    }

    @Test
    void deveCadastrarMedicamentoComDadosValidos() throws Exception {
        Medicamento medicamento = new Medicamento("Maria", "Dipirona", "500mg", "08:00");
        Medicamento salvo = medicamentoValido();
        when(repository.save(any(Medicamento.class))).thenReturn(salvo);

        mockMvc.perform(post("/medicamentos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(medicamento)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.nomePaciente").value("Maria"));
    }

    @Test
    void deveRetornarBadRequestAoCadastrarComDadosInvalidos() throws Exception {
        Medicamento invalido = new Medicamento("", "", "", "");

        mockMvc.perform(post("/medicamentos")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());

        verify(repository, never()).save(any());
    }

    @Test
    void deveListarTodosOsMedicamentos() throws Exception {
        when(repository.findAll()).thenReturn(List.of(medicamentoValido()));

        mockMvc.perform(get("/medicamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"));
    }

    @Test
    void deveBuscarMedicamentoPorId() throws Exception {
        when(repository.findById("1")).thenReturn(Optional.of(medicamentoValido()));

        mockMvc.perform(get("/medicamentos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    void deveRetornarNotFoundQuandoBuscarIdInexistente() throws Exception {
        when(repository.findById("999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/medicamentos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveAtualizarHorarioDeMedicamentoExistente() throws Exception {
        Medicamento existente = medicamentoValido();
        when(repository.findById("1")).thenReturn(Optional.of(existente));
        when(repository.save(any(Medicamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(patch("/medicamentos/1/horario")
                        .contentType("application/json")
                        .content("{\"horario\":\"12:00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.horario").value("12:00"));
    }

    @Test
    void deveRetornarBadRequestAoAtualizarHorarioVazio() throws Exception {
        mockMvc.perform(patch("/medicamentos/1/horario")
                        .contentType("application/json")
                        .content("{\"horario\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornarNotFoundAoAtualizarHorarioDeIdInexistente() throws Exception {
        when(repository.findById("999")).thenReturn(Optional.empty());

        mockMvc.perform(patch("/medicamentos/999/horario")
                        .contentType("application/json")
                        .content("{\"horario\":\"12:00\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveMarcarMedicamentoComoTomado() throws Exception {
        Medicamento existente = medicamentoValido();
        when(repository.findById("1")).thenReturn(Optional.of(existente));
        when(repository.save(any(Medicamento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(patch("/medicamentos/1/tomado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tomado").value(true));
    }

    @Test
    void deveRetornarNotFoundAoMarcarComoTomadoIdInexistente() throws Exception {
        when(repository.findById("999")).thenReturn(Optional.empty());

        mockMvc.perform(patch("/medicamentos/999/tomado"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deveRemoverMedicamentoExistente() throws Exception {
        when(repository.existsById("1")).thenReturn(true);

        mockMvc.perform(delete("/medicamentos/1"))
                .andExpect(status().isNoContent());

        verify(repository).deleteById("1");
    }

    @Test
    void deveRetornarNotFoundAoRemoverIdInexistente() throws Exception {
        when(repository.existsById("999")).thenReturn(false);

        mockMvc.perform(delete("/medicamentos/999"))
                .andExpect(status().isNotFound());

        verify(repository, never()).deleteById(eq("999"));
    }
}
