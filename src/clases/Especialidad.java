package clases;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Llapapasca Montes
 * @author Izquierdo Castro
 */
public class Especialidad {

    private int idEspecialidad;
    private String nombre;
    private List<Usuario> usuarios;

    public Especialidad(int idEspecialidad, String nombre) {
        this.idEspecialidad = idEspecialidad;
        this.nombre = nombre;
        this.usuarios = new ArrayList<>();
    }

    public void asignarUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarios;
    }

    public int getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(int idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
