package clases;

import java.util.Date;

/**
 *
 * @author Llapapasca Montes
 * @author Izquierdo Castro
 */
public class Usuario extends Persona {

    private int idUsuario;
    private Especialidad especialidad;

    public Usuario(int idUsuario, String turno, Especialidad especialidad, String dni, String nombre, String apellidoPaterno, String apellidoMaterno, Date fechaNacimiento, String genero, String direccion, String telefono) {
        super(dni, nombre, apellidoPaterno, apellidoMaterno, fechaNacimiento, genero, direccion, telefono);
        this.idUsuario = idUsuario;
        this.especialidad = especialidad;
    }

    public void asignarTurno() {
    }

    public void cambiarEspecialidad(Especialidad nuevaEspecialidad) {
        this.especialidad = nuevaEspecialidad;
    }

    public String getNombreCompleto() {
        return this.getNombre() + " " + this.getApellidoPaterno() + " " + this.getApellidoMaterno();
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

}
