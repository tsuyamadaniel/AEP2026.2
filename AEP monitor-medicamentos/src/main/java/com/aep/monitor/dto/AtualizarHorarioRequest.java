package com.aep.monitor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AtualizarHorarioRequest {

    @NotBlank(message = "Horário é obrigatório")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Horário deve estar no formato HH:mm (ex: 08:00)")
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
