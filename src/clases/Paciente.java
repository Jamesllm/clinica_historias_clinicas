package clases;

import java.util.Date;

/**
 *
 * @author Llapapasca Montes
 * @author Izquierdo Castro
 */
public class Paciente extends Persona {

    private Date fechaEntrada;
    private Date fechaSalida;
    private HistorialMedico historial = new HistorialMedico();

    public Paciente(Date fechaEntrada, Date fechaSalida, String DNI, String nombre, String apellidoPaterno, String apellidoMaterno, Date fechaNacimiento, String genero, String direccion, String telefono) {
        super(DNI, nombre, apellidoPaterno, apellidoMaterno, fechaNacimiento, genero, direccion, telefono);
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
    }

    public void registrarIngreso() {
    }

    public void registrarSalida() {
    }

    public Date getFechaEntrada() {
        return fechaEntrada;
    }

    public void setFechaEntrada(Date fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    public Date getFechaSalida() {
        return fechaSalida;
    }

    public void setFechaSalida(Date fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public HistorialMedico getHistorial() {
        return historial;
    }

    public void setHistorial(HistorialMedico historial) {
        this.historial = historial;
    }

}
