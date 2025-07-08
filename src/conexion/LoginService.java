package conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Clases
import clases.Usuario;
import clases.Especialidad;

public class LoginService {

    public static Usuario autenticarUsuario(Connection conn, String dni, String contrasena) throws Exception {
        String sql = "SELECT u.idUsuario, u.turno, u.idEspecialidad, u.contrasena, p.dni, p.nombre, p.apellidoPaterno, p.apellidoMaterno, p.fechaNacimiento, p.direccion, p.telefono, g.nombre as genero, e.nombre as especialidad "
                + "FROM personas.usuario u "
                + "INNER JOIN personas.persona p ON u.idPersona = p.idPersona "
                + "INNER JOIN personas.genero g ON p.idGenero = g.idGenero "
                + "INNER JOIN clinica.especialidad e ON u.idEspecialidad = e.idEspecialidad "
                + "WHERE p.dni = ? AND u.contrasena = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, dni);
        ps.setString(2, contrasena);
        ResultSet rs = ps.executeQuery();
        Usuario usuario = null;
        if (rs.next()) {
            Especialidad especialidad = new Especialidad(rs.getInt("idEspecialidad"), rs.getString("especialidad"));
            usuario = new Usuario(
                    rs.getInt("idUsuario"),
                    rs.getString("turno"),
                    especialidad,
                    rs.getString("dni"),
                    rs.getString("nombre"),
                    rs.getString("apellidoPaterno"),
                    rs.getString("apellidoMaterno"),
                    rs.getDate("fechaNacimiento"),
                    rs.getString("genero"),
                    rs.getString("direccion"),
                    rs.getString("telefono")
            );
        }
        rs.close();
        ps.close();
        return usuario;
    }
}
