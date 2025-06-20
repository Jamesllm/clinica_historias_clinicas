package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static Conexion instancia;
    private Connection conexion;
    
    private final String URL = "jdbc:postgresql://localhost:5432/historias_clinicas";
    private final String USUARIO = "postgres";
    private final String CLAVE = "Peru123...";

    private Conexion() {
        try {
            Class.forName("org.postgresql.Driver");
            conexion = DriverManager.getConnection(URL, USUARIO, CLAVE);
            System.out.println("Conexión establecida exitosamente");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el driver de PostgreSQL");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Conexion getInstance() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    public Connection getConexion() {
        return conexion;
    }
    
    /**
     * Cierra la conexión a la base de datos
     */
    public void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println("Conexión cerrada exitosamente");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Verifica si la conexión está activa
     */
    public boolean isConexionActiva() {
        try {
            return conexion != null && !conexion.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
