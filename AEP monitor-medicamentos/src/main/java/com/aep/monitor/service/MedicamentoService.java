package com.aep.monitor.service;

import com.aep.monitor.exception.MedicamentoNaoEncontradoException;
import com.aep.monitor.model.Medicamento;
import com.aep.monitor.repository.MedicamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Concentra as regras de negócio do domínio "medicamento", deixando o
 * controller responsável apenas por mapear rotas HTTP para chamadas de
 * serviço. Isso facilita testar a lógica isoladamente (sem subir o
 * contexto web) e evita duplicar a checagem de "existe/não existe" em
 * cada endpoint do controller.
 */
@Service
public class MedicamentoService {

    private final MedicamentoRepository repository;

    public MedicamentoService(MedicamentoRepository repository) {
        this.repository = repository;
    }

    public Medicamento cadastrar(Medicamento medicamento) {
        medicamento.setId(null);
        return repository.save(medicamento);
    }

    public List<Medicamento> listarTodos() {
        return repository.findAll();
    }

    public List<Medicamento> listarPorPaciente(String nomePaciente) {
        return repository.findByNomePaciente(nomePaciente);
    }

    public Medicamento buscarPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new MedicamentoNaoEncontradoException(id));
    }

    public Medicamento atualizarHorario(String id, String novoHorario) {
        Medicamento medicamento = buscarPorId(id);
        medicamento.setHorario(novoHorario);
        return repository.save(medicamento);
    }

    public Medicamento marcarComoTomado(String id) {
        Medicamento medicamento = buscarPorId(id);
        medicamento.marcarComoTomado();
        return repository.save(medicamento);
    }

    public void remover(String id) {
        if (!repository.existsById(id)) {
            throw new MedicamentoNaoEncontradoException(id);
        }
        repository.deleteById(id);
    }
}
