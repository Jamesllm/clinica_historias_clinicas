package estructuras;

import clases.Genero;
import java.sql.*;
import conexion.Conexion;

public class ArregloGenero {
    private Genero[] generos;
    private int contador;
    private final int CAPACIDAD = 5;

    public ArregloGenero() {
        generos = new Genero[CAPACIDAD];
        contador = 0;
    }

    // Insertar un nuevo género
    public boolean insertar(Genero genero) {
        if (contador < CAPACIDAD) {
            generos[contador++] = genero;
            return true;
        }
        return false;
    }

    // Eliminación lógica (marcar como inactivo)
    public boolean eliminarLogico(int idGenero) {
        for (int i = 0; i < contador; i++) {
            if (generos[i] != null && generos[i].getIdGenero() == idGenero) {
                generos[i].setEstado(false);
                return true;
            }
        }
        return false;
    }

    // Ordenar por nombre (burbuja)
    public void ordenarPorNombre() {
        for (int i = 0; i < contador - 1; i++) {
            for (int j = 0; j < contador - i - 1; j++) {
                if (generos[j].getNombre().compareToIgnoreCase(generos[j + 1].getNombre()) > 0) {
                    Genero temp = generos[j];
                    generos[j] = generos[j + 1];
                    generos[j + 1] = temp;
                }
            }
        }
    }

    // Búsqueda lineal por nombre
    public Genero buscarPorNombre(String nombre) {
        for (int i = 0; i < contador; i++) {
            if (generos[i] != null && generos[i].getNombre().equalsIgnoreCase(nombre)) {
                return generos[i];
            }
        }
        return null;
    }

    // Cargar géneros desde la base de datos
    public void cargarDesdeBD() {
        try {
            Connection conn = Conexion.getInstance().getConexion();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT idGenero, nombre, estado FROM personas.genero WHERE estado = true");
            contador = 0;
            while (rs.next() && contador < CAPACIDAD) {
                int id = rs.getInt("idGenero");
                String nombre = rs.getString("nombre");
                boolean estado = rs.getBoolean("estado");
                generos[contador++] = new Genero(id, nombre, estado);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Guardar todos los géneros en la base de datos 
    public void guardarEnBD() {
        try {
            Connection conn = Conexion.getInstance().getConexion();
            for (int i = 0; i < contador; i++) {
                Genero g = generos[i];
                PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO personas.genero (idGenero, nombre, estado) VALUES (?, ?, ?) ON CONFLICT (idGenero) DO UPDATE SET nombre = EXCLUDED.nombre, estado = EXCLUDED.estado");
                ps.setInt(1, g.getIdGenero());
                ps.setString(2, g.getNombre());
                ps.setBoolean(3, g.isEstado());
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Getters
    public Genero[] getGeneros() {
        return generos;
    }
    public int getCount() {
        return contador;
    }
} 