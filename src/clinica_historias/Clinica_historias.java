package clinica_historias;

/**
 *
 * @author Llapapasca Montes
 */
public class Clinica_historias {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Ejemplo de uso de la clase Conexion
        conexion.Conexion conexionDB = conexion.Conexion.getInstance();
        if (conexionDB.getConexion() != null) {
            System.out.println("Conexión exitosa a la base de datos PostgreSQL.");
        } else {
            System.out.println("No se pudo conectar a la base de datos.");
        }
    }
    
}
