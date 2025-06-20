package clinica_historias;

import com.formdev.flatlaf.FlatLightLaf;

/**
 *
 * @author Llapapasca Montes
 */
public class Clinica_historias {

    public static void main(String[] args) {
        // Cambiar tema al entorno
        FlatLightLaf.setup();
        
        // Ejemplo de uso de la clase Conexion
        conexion.Conexion conexionDB = conexion.Conexion.getInstance();
        if (conexionDB.getConexion() != null) {
            System.out.println("Conexión exitosa a la base de datos PostgreSQL.");
            
            // Despues de obtener la conexion se habre la app
            Login login = new Login(conexionDB);
            login.setVisible(true);
            
        } else {
            System.out.println("No se pudo conectar a la base de datos.");
        }
    }

}
