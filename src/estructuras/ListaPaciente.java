package estructuras;

import clases.Paciente;
import java.sql.*;
import java.util.Date;
import conexion.Conexion;
import clases.Genero;
import estructuras.ArregloGenero;

public class ListaPaciente {
    public static class NodoPaciente {
        public Paciente paciente;
        public NodoPaciente siguiente;
        public NodoPaciente(Paciente paciente) {
            this.paciente = paciente;
            this.siguiente = null;
        }
    }

    private NodoPaciente cabeza;
    private ArregloGenero arregloGenero = new ArregloGenero(); // Para buscar idGenero

    public ListaPaciente() {
        cabeza = null;
    }

    public NodoPaciente getCabeza() {
        return cabeza;
    }

    // Agregar al final
    public void agregar(Paciente paciente) {
        NodoPaciente nuevo = new NodoPaciente(paciente);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            NodoPaciente actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevo;
        }
    }

    // Eliminar por DNI (eliminación lógica: no se borra de la BD, solo de la lista)
    public boolean eliminarPorDni(String dni) {
        NodoPaciente actual = cabeza, anterior = null;
        while (actual != null) {
            if (actual.paciente.getDni().equals(dni)) {
                if (anterior == null) {
                    cabeza = actual.siguiente;
                } else {
                    anterior.siguiente = actual.siguiente;
                }
                return true;
            }
            anterior = actual;
            actual = actual.siguiente;
        }
        return false;
    }

    // Recorrer y mostrar
    public void mostrar() {
        NodoPaciente actual = cabeza;
        while (actual != null) {
            System.out.println(actual.paciente.getDni() + " - " + actual.paciente.getNombre());
            actual = actual.siguiente;
        }
    }

    // Cargar pacientes desde la base de datos
    public void cargarDesdeBD() {
        try {
            cabeza = null; // Limpiar la lista antes de cargar
            arregloGenero.cargarDesdeBD();
            Connection conn = Conexion.getInstance().getConexion();
            Statement stmt = conn.createStatement();
            
            String sql = "SELECT p.dni, p.nombre, p.apellidoPaterno, p.apellidoMaterno, p.fechaNacimiento, genero.nombre as genero, p.direccion, p.telefono, pa.fechaEntrada, pa.fechaSalida\n" +
                "FROM personas.persona p \n" +
                "INNER JOIN personas.paciente pa ON p.idPersona = pa.idPersona\n" +
                "INNER JOIN personas.genero genero ON p.idGenero = genero.idGenero";
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                String dni = rs.getString("dni");
                String nombre = rs.getString("nombre");
                String apellidoPaterno = rs.getString("apellidoPaterno");
                String apellidoMaterno = rs.getString("apellidoMaterno");
                Date fechaNacimiento = rs.getDate("fechaNacimiento");
                String genero = rs.getString("genero");
                String direccion = rs.getString("direccion");
                String telefono = rs.getString("telefono");
                Date fechaEntrada = rs.getTimestamp("fechaEntrada");
                Date fechaSalida = rs.getTimestamp("fechaSalida");
                Paciente paciente = new Paciente(fechaEntrada, fechaSalida, dni, nombre, apellidoPaterno, apellidoMaterno, fechaNacimiento, genero, direccion, telefono);
                agregar(paciente);
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Guardar un paciente en la base de datos (ejemplo simple)
    public void guardarEnBD(Paciente paciente) {
        try {
            arregloGenero.cargarDesdeBD();
            Connection conn = Conexion.getInstance().getConexion();
            // Obtener idGenero a partir del nombre
            System.out.println("DEBUG - Guardando paciente en BD:");
            System.out.println("  - Género recibido: '" + paciente.getGenero() + "'");
            Genero generoObj = arregloGenero.buscarPorNombre(paciente.getGenero());
            System.out.println("  - Género encontrado: " + (generoObj != null ? generoObj.getNombre() + " (ID: " + generoObj.getIdGenero() + ")" : "NULL"));
            int idGenero = (generoObj != null) ? generoObj.getIdGenero() : 1; // Por defecto 1 si no se encuentra
            System.out.println("  - ID Género final: " + idGenero);
            
            // Insertar en persona
            PreparedStatement psPersona = conn.prepareStatement(
                "INSERT INTO personas.persona (dni, nombre, apellidoPaterno, apellidoMaterno, fechaNacimiento, direccion, telefono, idGenero) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING idPersona");
            psPersona.setString(1, paciente.getDni());
            psPersona.setString(2, paciente.getNombre());
            psPersona.setString(3, paciente.getApellidoPaterno());
            psPersona.setString(4, paciente.getApellidoMaterno());
            psPersona.setDate(5, new java.sql.Date(paciente.getFechaNacimiento().getTime()));
            psPersona.setString(6, paciente.getDireccion());
            psPersona.setString(7, paciente.getTelefono());
            psPersona.setInt(8, idGenero);
            ResultSet rs = psPersona.executeQuery();
            
            int idPersona = -1;
            if (rs.next()) {
                idPersona = rs.getInt(1);
            }
            rs.close();
            psPersona.close();
            // Insertar en paciente
            PreparedStatement psPaciente = conn.prepareStatement(
                "INSERT INTO personas.paciente (idPersona, fechaEntrada, fechaSalida) VALUES (?, ?, ?)");
            
            psPaciente.setInt(1, idPersona);
            psPaciente.setTimestamp(2, new java.sql.Timestamp(paciente.getFechaEntrada().getTime()));
            psPaciente.setTimestamp(3, new java.sql.Timestamp(paciente.getFechaSalida().getTime()));
            psPaciente.executeUpdate();
            psPaciente.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Actualizar un paciente en la base de datos por DNI
    public void actualizarEnBD(Paciente paciente) {
        try {
            arregloGenero.cargarDesdeBD();
            Connection conn = Conexion.getInstance().getConexion();
            // Obtener idGenero a partir del nombre
            System.out.println("DEBUG - Actualizando paciente en BD:");
            System.out.println("  - Género recibido: '" + paciente.getGenero() + "'");
            Genero generoObj = arregloGenero.buscarPorNombre(paciente.getGenero());
            System.out.println("  - Género encontrado: " + (generoObj != null ? generoObj.getNombre() + " (ID: " + generoObj.getIdGenero() + ")" : "NULL"));
            int idGenero = (generoObj != null) ? generoObj.getIdGenero() : 1;
            System.out.println("  - ID Género final: " + idGenero);
            
            // Actualizar en persona
            PreparedStatement psPersona = conn.prepareStatement(
                "UPDATE personas.persona SET nombre=?, apellidoPaterno=?, apellidoMaterno=?, fechaNacimiento=?, direccion=?, telefono=?, idGenero=? WHERE dni=?");
            psPersona.setString(1, paciente.getNombre());
            psPersona.setString(2, paciente.getApellidoPaterno());
            psPersona.setString(3, paciente.getApellidoMaterno());
            psPersona.setDate(4, new java.sql.Date(paciente.getFechaNacimiento().getTime()));
            psPersona.setString(5, paciente.getDireccion());
            psPersona.setString(6, paciente.getTelefono());
            psPersona.setInt(7, idGenero);
            psPersona.setString(8, paciente.getDni());
            psPersona.executeUpdate();
            psPersona.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 