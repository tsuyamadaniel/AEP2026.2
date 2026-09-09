package com.aep.monitor.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

/**
 * Representa um medicamento cadastrado para um paciente.
 * Estrutura simples e homogênea, conforme exigido na 1ª entrega da AEP
 * (uma única coleção NoSQL, documentos sem aninhamento).
 */
@Document(collection = "medicamentos")
public class Medicamento {

    private static final String FORMATO_HORARIO = "^([01]\\d|2[0-3]):[0-5]\\d$";

    @Id
    private String id;

    @NotBlank(message = "Nome do paciente é obrigatório")
    private String nomePaciente;

    @NotBlank(message = "Nome do medicamento é obrigatório")
    private String nomeMedicamento;

    @NotBlank(message = "Dosagem é obrigatória")
    private String dosagem;

    @NotBlank(message = "Horário é obrigatório")
    @Pattern(regexp = FORMATO_HORARIO, message = "Horário deve estar no formato HH:mm (ex: 08:00)")
    private String horario;

    private boolean tomado;

    public Medicamento() {
    }

    public Medicamento(String nomePaciente, String nomeMedicamento, String dosagem, String horario) {
        this.nomePaciente = nomePaciente;
        this.nomeMedicamento = nomeMedicamento;
        this.dosagem = dosagem;
        this.horario = horario;
        this.tomado = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomePaciente() {
        return nomePaciente;
    }

    public void setNomePaciente(String nomePaciente) {
        this.nomePaciente = nomePaciente;
    }

    public String getNomeMedicamento() {
        return nomeMedicamento;
    }

    public void setNomeMedicamento(String nomeMedicamento) {
        this.nomeMedicamento = nomeMedicamento;
    }

    public String getDosagem() {
        return dosagem;
    }

    public void setDosagem(String dosagem) {
        this.dosagem = dosagem;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public boolean isTomado() {
        return tomado;
    }

    public void marcarComoTomado() {
        this.tomado = true;
    }

    public void desmarcarComoTomado() {
        this.tomado = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medicamento)) return false;
        Medicamento that = (Medicamento) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Medicamento{" +
                "id='" + id + '\'' +
                ", nomePaciente='" + nomePaciente + '\'' +
                ", nomeMedicamento='" + nomeMedicamento + '\'' +
                ", dosagem='" + dosagem + '\'' +
                ", horario='" + horario + '\'' +
                ", tomado=" + tomado +
                '}';
    }
}
