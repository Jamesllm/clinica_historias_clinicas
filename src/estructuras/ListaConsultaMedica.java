package estructuras;

import clases.ConsultaMedica;
import clases.Paciente;
import clases.Usuario;
import java.sql.*;
import conexion.Conexion;

public class ListaConsultaMedica {
    public static class NodoConsulta {
        public ConsultaMedica consulta;
        public NodoConsulta siguiente;
        public NodoConsulta(ConsultaMedica consulta) {
            this.consulta = consulta;
            this.siguiente = null;
        }
    }

    public NodoConsulta cabeza;

    public ListaConsultaMedica() {
        cabeza = null;
    }

    public void agregar(ConsultaMedica consulta) {
        NodoConsulta nuevo = new NodoConsulta(consulta);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            NodoConsulta actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevo;
        }
    }

    // Guardar una consulta médica en la base de datos
    public int guardarEnBD(ConsultaMedica consulta) throws SQLException {
        Connection conn = Conexion.getInstance().getConexion();
        // Obtener idPaciente desde la BD usando el DNI
        int idPaciente = -1;
        PreparedStatement psPaciente = conn.prepareStatement(
            "SELECT pa.idPaciente FROM personas.paciente pa INNER JOIN personas.persona p ON pa.idPersona = p.idPersona WHERE p.dni = ?"
        );
        psPaciente.setString(1, consulta.getPaciente().getDni());
        ResultSet rsPaciente = psPaciente.executeQuery();
        if (rsPaciente.next()) {
            idPaciente = rsPaciente.getInt("idPaciente");
        }
        rsPaciente.close();
        psPaciente.close();

        if (idPaciente == -1) {
            throw new SQLException("No se encontró el ID del paciente.");
        }

        // Insertar la consulta médica
        PreparedStatement psConsulta = conn.prepareStatement(
            "INSERT INTO clinica.consultaMedica (idPaciente, idUsuario, diagnostico, tratamiento, fechaRegistro) VALUES (?, ?, ?, ?, ?) RETURNING idConsulta"
        );
        psConsulta.setInt(1, idPaciente);
        psConsulta.setInt(2, consulta.getUsuario().getIdUsuario());
        psConsulta.setString(3, consulta.getDiagnostico());
        psConsulta.setString(4, consulta.getTratamiento());
        psConsulta.setDate(5, new java.sql.Date(consulta.getFechaRegistro().getTime()));
        ResultSet rsConsulta = psConsulta.executeQuery();
        int idConsulta = -1;
        if (rsConsulta.next()) {
            idConsulta = rsConsulta.getInt("idConsulta");
        }
        rsConsulta.close();
        psConsulta.close();

        return idConsulta;
    }

    // Cargar todas las consultas médicas desde la base de datos
    public void cargarDesdeBD() {
        cabeza = null;
        try {
            Connection conn = Conexion.getInstance().getConexion();
            Statement stmt = conn.createStatement();
            String sql = "SELECT c.idConsulta, c.diagnostico, c.tratamiento, c.fechaRegistro, " +
                         "p.dni, p.nombre, p.apellidoPaterno, p.apellidoMaterno, p.fechaNacimiento, p.direccion, p.telefono, g.nombre as genero, " +
                         "pa.fechaEntrada, pa.fechaSalida, " +
                         "u.idUsuario, u.turno, e.nombre as especialidad, up.dni as usuarioDni, up.nombre as usuarioNombre, up.apellidoPaterno as usuarioApellidoPaterno, up.apellidoMaterno as usuarioApellidoMaterno, up.fechaNacimiento as usuarioFechaNacimiento, up.direccion as usuarioDireccion, up.telefono as usuarioTelefono, ug.nombre as usuarioGenero " +
                         "FROM clinica.consultaMedica c " +
                         "INNER JOIN personas.paciente pa ON c.idPaciente = pa.idPaciente " +
                         "INNER JOIN personas.persona p ON pa.idPersona = p.idPersona " +
                         "INNER JOIN personas.genero g ON p.idGenero = g.idGenero " +
                         "INNER JOIN personas.usuario u ON c.idUsuario = u.idUsuario " +
                         "INNER JOIN personas.persona up ON u.idPersona = up.idPersona " +
                         "INNER JOIN personas.genero ug ON up.idGenero = ug.idGenero " +
                         "INNER JOIN clinica.especialidad e ON u.idEspecialidad = e.idEspecialidad ";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                // Paciente
                clases.Paciente paciente = new clases.Paciente(
                    rs.getTimestamp("fechaEntrada"),
                    rs.getTimestamp("fechaSalida"),
                    rs.getString("dni"),
                    rs.getString("nombre"),
                    rs.getString("apellidoPaterno"),
                    rs.getString("apellidoMaterno"),
                    rs.getDate("fechaNacimiento"),
                    rs.getString("genero"),
                    rs.getString("direccion"),
                    rs.getString("telefono")
                );
                // Usuario
                clases.Especialidad especialidad = new clases.Especialidad(0, rs.getString("especialidad"));
                clases.Usuario usuario = new clases.Usuario(
                    rs.getInt("idUsuario"),
                    rs.getString("turno"),
                    especialidad,
                    rs.getString("usuarioDni"),
                    rs.getString("usuarioNombre"),
                    rs.getString("usuarioApellidoPaterno"),
                    rs.getString("usuarioApellidoMaterno"),
                    rs.getDate("usuarioFechaNacimiento"),
                    rs.getString("usuarioGenero"),
                    rs.getString("usuarioDireccion"),
                    rs.getString("usuarioTelefono")
                );
                // ConsultaMedica
                clases.ConsultaMedica consulta = new clases.ConsultaMedica(
                    rs.getInt("idConsulta"),
                    rs.getString("diagnostico"),
                    rs.getString("tratamiento"),
                    rs.getDate("fechaRegistro"),
                    paciente,
                    usuario,
                    null // comprobantePago (puedes cargarlo si lo necesitas)
                );
                agregar(consulta);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 