package clases;

/**
 *
 * @author Llapapasca Montes
 * @author Izquierdo Castro
 */
public class Genero {
    private int idGenero;
    private String nombre;
    private boolean estado;

    public Genero(int idGenero, String nombre, boolean estado) {
        this.idGenero = idGenero;
        this.nombre = nombre;
        this.estado = estado;
    }

    public Genero(int idGenero, String nombre) {
        this(idGenero, nombre, true);
    }

    public int getIdGenero() {
        return idGenero;
    }

    public void setIdGenero(int idGenero) {
        this.idGenero = idGenero;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return nombre;
    }
} 