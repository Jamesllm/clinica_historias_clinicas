package estructuras;

import clases.Paciente;
import java.util.ArrayList;
import java.util.List;

public class ArbolBBPaciente {
    public static class Nodo {
        public Paciente paciente;
        public Nodo izquierdo, derecho;

        public Nodo(Paciente paciente) {
            this.paciente = paciente;
            this.izquierdo = null;
            this.derecho = null;
        }
    }

    private Nodo raiz;

    public ArbolBBPaciente() {
        raiz = null;
    }

    // Insertar paciente por DNI
    public void insertar(Paciente paciente) {
        raiz = insertarRec(raiz, paciente);
    }

    private Nodo insertarRec(Nodo actual, Paciente paciente) {
        if (actual == null) {
            return new Nodo(paciente);
        }
        int cmp = paciente.getDni().compareTo(actual.paciente.getDni());
        if (cmp < 0) {
            actual.izquierdo = insertarRec(actual.izquierdo, paciente);
        } else if (cmp > 0) {
            actual.derecho = insertarRec(actual.derecho, paciente);
        }
        // Si es igual, no inserta duplicados
        return actual;
    }

    // Buscar paciente por DNI
    public Paciente buscar(String dni) {
        return buscarRec(raiz, dni);
    }

    private Paciente buscarRec(Nodo actual, String dni) {
        if (actual == null) {
            return null;
        }
        int cmp = dni.compareTo(actual.paciente.getDni());
        if (cmp == 0) {
            return actual.paciente;
        } else if (cmp < 0) {
            return buscarRec(actual.izquierdo, dni);
        } else {
            return buscarRec(actual.derecho, dni);
        }
    }

    // Recorrido in-order (ordenado por DNI)
    public List<Paciente> obtenerPacientesOrdenados() {
        List<Paciente> pacientes = new ArrayList<>();
        inOrderRec(raiz, pacientes);
        return pacientes;
    }

    private void inOrderRec(Nodo actual, List<Paciente> pacientes) {
        if (actual != null) {
            inOrderRec(actual.izquierdo, pacientes);
            pacientes.add(actual.paciente);
            inOrderRec(actual.derecho, pacientes);
        }
    }

    // Cargar pacientes desde una lista enlazada
    public void cargarDesdeLista(ListaPaciente listaPaciente) {
        raiz = null; // Limpiar el árbol
        ListaPaciente.NodoPaciente actual = listaPaciente.getCabeza();
        while (actual != null) {
            insertar(actual.paciente);
            actual = actual.siguiente;
        }
    }

    // Verificar si el árbol está vacío
    public boolean estaVacio() {
        return raiz == null;
    }

    // Obtener altura del árbol
    public int obtenerAltura() {
        return obtenerAlturaRec(raiz);
    }

    private int obtenerAlturaRec(Nodo actual) {
        if (actual == null) {
            return 0;
        }
        int alturaIzq = obtenerAlturaRec(actual.izquierdo);
        int alturaDer = obtenerAlturaRec(actual.derecho);
        return Math.max(alturaIzq, alturaDer) + 1;
    }

    // Contar nodos
    public int contarNodos() {
        return contarNodosRec(raiz);
    }

    private int contarNodosRec(Nodo actual) {
        if (actual == null) {
            return 0;
        }
        return 1 + contarNodosRec(actual.izquierdo) + contarNodosRec(actual.derecho);
    }
}
