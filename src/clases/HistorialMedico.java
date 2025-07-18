package clases;

import java.util.ArrayList;
import java.util.List;

public class HistorialMedico {
    private List<String> consultas;
    private List<String> diagnosticos;
    private List<String> medicamentos;

    public HistorialMedico() {
        this.consultas = new ArrayList<>();
        this.diagnosticos = new ArrayList<>();
        this.medicamentos = new ArrayList<>();
    }

    public void agregarConsulta(String consulta) {
        consultas.add(consulta);
    }

    public void agregarDiagnostico(String diagnostico) {
        diagnosticos.add(diagnostico);
    }

    public void agregarMedicamento(String medicamento) {
        medicamentos.add(medicamento);
    }

    public List<String> getConsultas() {
        return consultas;
    }

    public List<String> getDiagnosticos() {
        return diagnosticos;
    }

    public List<String> getMedicamentos() {
        return medicamentos;
    }

    @Override
    public String toString() {
        return "Consultas: " + consultas +
               "\nDiagnósticos: " + diagnosticos +
               "\nMedicamentos: " + medicamentos;
    }
}