package com.aep.monitor.dto;

import jakarta.validation.constraints.NotBlank;

public class AtualizarHorarioRequest {

    @NotBlank(message = "Horário é obrigatório")
    private String horario;

    public AtualizarHorarioRequest() {
    }

    public AtualizarHorarioRequest(String horario) {
        this.horario = horario;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }
}
