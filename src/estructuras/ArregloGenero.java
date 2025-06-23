package estructuras;

import clases.Genero;
import java.sql.*;
import conexion.Conexion;

public class ArregloGenero {
    private Genero[] generos;
    private int count;
    private final int CAPACIDAD = 5;

    public ArregloGenero() {
        generos = new Genero[CAPACIDAD];
        count = 0;
    }

    // Insertar un nuevo género
    public boolean insertar(Genero genero) {
        if (count < CAPACIDAD) {
            generos[count++] = genero;
            return true;
        }
        return false;
    }

    // Eliminación lógica (marcar como inactivo)
    public boolean eliminarLogico(int idGenero) {
        for (int i = 0; i < count; i++) {
            if (generos[i] != null && generos[i].getIdGenero() == idGenero) {
                generos[i].setEstado(false);
                return true;
            }
        }
        return false;
    }

    // Ordenar por nombre (burbuja)
    public void ordenarPorNombre() {
        for (int i = 0; i < count - 1; i++) {
            for (int j = 0; j < count - i - 1; j++) {
                if (generos[j].getNombre().compareToIgnoreCase(generos[j + 1].getNombre()) > 0) {
                    Genero temp = generos[j];
                    generos[j] = generos[j + 1];
                    generos[j + 1] = temp;
                }
            }
        }
    }

    // Búsqueda binaria por nombre (debe estar ordenado)
    public Genero buscarPorNombre(String nombre) {
        int left = 0, right = count - 1;
        while (left <= right) {
            int mid = (left + right) / 2;
            int cmp = generos[mid].getNombre().compareToIgnoreCase(nombre);
            if (cmp == 0) return generos[mid];
            if (cmp < 0) left = mid + 1;
            else right = mid - 1;
        }
        return null;
    }

    // Cargar géneros desde la base de datos
    public void cargarDesdeBD() {
        try {
            Connection conn = Conexion.getInstance().getConexion();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT idGenero, nombre, estado FROM personas.genero WHERE estado = true");
            count = 0;
            while (rs.next() && count < CAPACIDAD) {
                int id = rs.getInt("idGenero");
                String nombre = rs.getString("nombre");
                boolean estado = rs.getBoolean("estado");
                generos[count++] = new Genero(id, nombre, estado);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Guardar todos los géneros en la base de datos (ejemplo simple)
    public void guardarEnBD() {
        try {
            Connection conn = Conexion.getInstance().getConexion();
            for (int i = 0; i < count; i++) {
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
        return count;
    }
} 