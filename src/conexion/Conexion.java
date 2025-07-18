package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static Conexion instancia;
    private Connection conexion;
    
// Local
    private final String URL = "jdbc:postgresql://localhost:5432/historias_clinicas";
    private final String USUARIO = "postgres";
    private final String CLAVE = "Peru123...";

// Publica    
//    private final String URL = "jdbc:postgresql://dpg-d1sihke3jp1c73al14t0-a.oregon-postgres.render.com/historias_clinicas_p3h4";
//    private final String USUARIO = "historias_clinicas_p3h4_user";
//    private final String CLAVE = "GNlvIv83jSCtsN1ktS4zN988h2FFLvc2";

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
