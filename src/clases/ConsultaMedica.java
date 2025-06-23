package clases;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConsultaMedica {

    private int idConsultaMedica;
    private String diagnostico;
    private String tratamiento;
    private Date fechaRegistro;
    private Paciente paciente;
    private Usuario usuario;
    private List<Medicamento> medicamentos;
    private ComprobantePago comprobantePago;

    public ConsultaMedica(int idConsultaMedica, String diagnostico, String tratamiento, Date fechaRegistro, Paciente paciente, Usuario usuario, ComprobantePago comprobantePago) {
        this.idConsultaMedica = idConsultaMedica;
        this.diagnostico = diagnostico;
        this.tratamiento = tratamiento;
        this.fechaRegistro = fechaRegistro;
        this.paciente = paciente;
        this.usuario = usuario;
        this.comprobantePago = comprobantePago;
        this.medicamentos = new ArrayList<>();
    }

    public void registrarConsulta() {
        // Implementación vacía
    }

    public void actualizarDiagnostico(String nuevoDiagnostico) {
        this.diagnostico = nuevoDiagnostico;
    }

    public void agregarTratamiento(String nuevoTratamiento) {
        this.tratamiento = nuevoTratamiento;
    }

    public void agregarMedicamento(Medicamento medicamento) {
        medicamentos.add(medicamento);
    }

    // Getters y setters
    public int getIdConsultaMedica() {
        return idConsultaMedica;
    }

    public void setIdConsultaMedica(int idConsultaMedica) {
        this.idConsultaMedica = idConsultaMedica;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Medicamento> getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(List<Medicamento> medicamentos) {
        this.medicamentos = medicamentos;
    }

    public ComprobantePago getComprobantePago() {
        return comprobantePago;
    }

    public void setComprobantePago(ComprobantePago comprobantePago) {
        this.comprobantePago = comprobantePago;
    }
}
