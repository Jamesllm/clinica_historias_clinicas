package clases;

/**
 *
 * @author Llapapasca Montes
 * @author Izquierdo Castro
 */
public class Medicamento {

    private int idMedicamento;
    private String nombre;
    private String presentacion;

    public Medicamento(int idMedicamento, String nombre, String presentacion) {
        this.idMedicamento = idMedicamento;
        this.nombre = nombre;
        this.presentacion = presentacion;
    }

    public void actualizarStock() {
        // Implementación vacía
    }

    public void consultarPresentacion() {
        // Implementación vacía
    }

    // Getters y setters
    public int getIdMedicamento() {
        return idMedicamento;
    }

    public void setIdMedicamento(int idMedicamento) {
        this.idMedicamento = idMedicamento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }
}
