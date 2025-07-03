package estructuras;

import clases.Paciente;
import java.sql.*;
import java.util.Date;
import conexion.Conexion;

public class ColaDinamicaPaciente {
    public static class Nodo {
        public Paciente paciente;
        public Nodo siguiente;
        public Nodo(Paciente paciente) {
            this.paciente = paciente;
            this.siguiente = null;
        }
    }

    private Nodo frente;
    private Nodo finalCola;

    public ColaDinamicaPaciente() {
        frente = null;
        finalCola = null;
    }

    public void enqueue(Paciente paciente) {
        Nodo nuevo = new Nodo(paciente);
        if (finalCola == null) {
            frente = nuevo;
            finalCola = nuevo;
        } else {
            finalCola.siguiente = nuevo;
            finalCola = nuevo;
        }
    }

    public Paciente dequeue() {
        if (isEmpty()) {
            throw new RuntimeException("Cola vacía.");
        }
        Paciente paciente = frente.paciente;
        frente = frente.siguiente;
        if (frente == null) {
            finalCola = null;
        }
        return paciente;
    }

    public Paciente peek() {
        if (isEmpty()) {
            throw new RuntimeException("Cola vacía.");
        }
        return frente.paciente;
    }

    public boolean isEmpty() {
        return frente == null;
    }

    public void recorrer() {
        System.out.println("Contenido de la cola de pacientes:");
        Nodo actual = frente;
        while (actual != null) {
            System.out.println(actual.paciente.getDni() + " - " + actual.paciente.getNombre());
            actual = actual.siguiente;
        }
    }

    // Cargar pacientes desde la base de datos
    public void cargarDesdeBD() {
        try {
            frente = null;
            finalCola = null;
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
                enqueue(paciente);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 