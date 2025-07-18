package estructuras;

import clases.ComprobantePago;
import java.sql.*;
import java.util.Date;
import conexion.Conexion;

/**
 * TAD PilaDinamicaComprobante
 * 
 * Especificación informal:
 *   Permite apilar comprobantes de pago, desapilar, ver el tope, mostrar la pila y cargar desde la base de datos.
 * 
 * Especificación formal:
 *   - push(e): agrega un comprobante al tope de la pila.
 *   - pop(): elimina y retorna el comprobante en el tope de la pila.
 *   - peek(): retorna el comprobante en el tope sin eliminarlo.
 *   - mostrar(): muestra todos los comprobantes en la pila.
 *   - cargarDesdeBD(): carga comprobantes desde la base de datos.
 */
public class PilaDinamicaComprobante {
    public static class Nodo {
        public ComprobantePago comprobante;
        public Nodo siguiente;
        public Nodo(ComprobantePago comprobante) {
            this.comprobante = comprobante;
            this.siguiente = null;
        }
    }

    public Nodo tope;

    public PilaDinamicaComprobante() {
        tope = null;
    }

    public void push(ComprobantePago comprobante) {
        Nodo nuevo = new Nodo(comprobante);
        nuevo.siguiente = tope;
        tope = nuevo;
    }

    public ComprobantePago pop() {
        if (isEmpty()) {
            throw new RuntimeException("Pila vacía. No se puede eliminar.");
        }
        ComprobantePago comprobante = tope.comprobante;
        tope = tope.siguiente;
        return comprobante;
    }

    public ComprobantePago peek() {
        if (isEmpty()) {
            throw new RuntimeException("Pila vacía.");
        }
        return tope.comprobante;
    }

    public boolean isEmpty() {
        return tope == null;
    }

    public void mostrar() {
        System.out.println("Contenido de la pila de comprobantes:");
        Nodo actual = tope;
        while (actual != null) {
            System.out.println("ID: " + actual.comprobante.getIdComprobantePago() + " - Forma de pago: " + actual.comprobante.getPago());
            actual = actual.siguiente;
        }
    }

    // Cargar comprobantes desde la base de datos
    public void cargarDesdeBD() {
        try {
            tope = null;
            Connection conn = Conexion.getInstance().getConexion();
            Statement stmt = conn.createStatement();
            String sql = "SELECT idComprobantePago, fechaEmision, formaPago FROM pagos.comprobantePago";
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int idComprobantePago = rs.getInt("idComprobantePago");
                Date fechaEmision = rs.getDate("fechaEmision");
                String pago = rs.getString("formaPago");
                ComprobantePago comprobante = new ComprobantePago(idComprobantePago, fechaEmision, pago);
                push(comprobante);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 