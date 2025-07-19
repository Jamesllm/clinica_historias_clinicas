package estructuras;

import clases.Paciente;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de un árbol AVL para almacenar objetos Paciente
 * El árbol se balancea automáticamente para mantener una altura logarítmica
 */
public class ArbolAVLPaciente {
    
    /**
     * Clase interna que representa un nodo del árbol AVL
     */
    public static class NodoAVL {
        Paciente paciente;
        int altura;
        NodoAVL izq, der;
        
        NodoAVL(Paciente p) {
            paciente = p;
            altura = 1;
        }
    }
    
    private NodoAVL raiz;
    
    public ArbolAVLPaciente() {
        raiz = null;
    }
    
    /**
     * Inserta un paciente en el árbol AVL
     * @param paciente El paciente a insertar
     */
    public void insertar(Paciente paciente) {
        raiz = insertar(raiz, paciente);
    }
    
    /**
     * Método recursivo para insertar un paciente
     * @param nodo El nodo actual
     * @param paciente El paciente a insertar
     * @return El nodo raíz del subárbol
     */
    private NodoAVL insertar(NodoAVL nodo, Paciente paciente) {
        if (nodo == null)
            return new NodoAVL(paciente);
        
        // Comparar por DNI
        int comparacion = paciente.getDni().compareTo(nodo.paciente.getDni());
        
        if (comparacion < 0)
            nodo.izq = insertar(nodo.izq, paciente);
        else if (comparacion > 0)
            nodo.der = insertar(nodo.der, paciente);
        else
            return nodo; // DNI duplicado
        
        // Actualizar altura del nodo actual
        nodo.altura = 1 + Math.max(altura(nodo.izq), altura(nodo.der));
        
        // Obtener el factor de balance
        int balance = getBalance(nodo);
        
        // Caso Izquierda-Izquierda
        if (balance > 1 && paciente.getDni().compareTo(nodo.izq.paciente.getDni()) < 0)
            return rotacionDerecha(nodo);
        
        // Caso Derecha-Derecha
        if (balance < -1 && paciente.getDni().compareTo(nodo.der.paciente.getDni()) > 0)
            return rotacionIzquierda(nodo);
        
        // Caso Izquierda-Derecha
        if (balance > 1 && paciente.getDni().compareTo(nodo.izq.paciente.getDni()) > 0) {
            nodo.izq = rotacionIzquierda(nodo.izq);
            return rotacionDerecha(nodo);
        }
        
        // Caso Derecha-Izquierda
        if (balance < -1 && paciente.getDni().compareTo(nodo.der.paciente.getDni()) < 0) {
            nodo.der = rotacionDerecha(nodo.der);
            return rotacionIzquierda(nodo);
        }
        
        return nodo;
    }
    
    /**
     * Rotación derecha
     * @param y El nodo a rotar
     * @return El nuevo nodo raíz
     */
    private NodoAVL rotacionDerecha(NodoAVL y) {
        NodoAVL x = y.izq;
        NodoAVL T2 = x.der;
        
        x.der = y;
        y.izq = T2;
        
        y.altura = Math.max(altura(y.izq), altura(y.der)) + 1;
        x.altura = Math.max(altura(x.izq), altura(x.der)) + 1;
        
        return x;
    }
    
    /**
     * Rotación izquierda
     * @param x El nodo a rotar
     * @return El nuevo nodo raíz
     */
    private NodoAVL rotacionIzquierda(NodoAVL x) {
        NodoAVL y = x.der;
        NodoAVL T2 = y.izq;
        
        y.izq = x;
        x.der = T2;
        
        x.altura = Math.max(altura(x.izq), altura(x.der)) + 1;
        y.altura = Math.max(altura(y.izq), altura(y.der)) + 1;
        
        return y;
    }
    
    /**
     * Obtiene la altura de un nodo
     * @param nodo El nodo
     * @return La altura del nodo
     */
    private int altura(NodoAVL nodo) {
        if (nodo == null) return 0;
        return nodo.altura;
    }
    
    /**
     * Calcula el factor de balance de un nodo
     * @param nodo El nodo
     * @return El factor de balance
     */
    private int getBalance(NodoAVL nodo) {
        if (nodo == null) return 0;
        return altura(nodo.izq) - altura(nodo.der);
    }
    
    /**
     * Busca un paciente por DNI
     * @param dni El DNI a buscar
     * @return El paciente encontrado o null si no existe
     */
    public Paciente buscar(String dni) {
        return buscar(raiz, dni);
    }
    
    /**
     * Método recursivo para buscar un paciente
     * @param nodo El nodo actual
     * @param dni El DNI a buscar
     * @return El paciente encontrado o null
     */
    private Paciente buscar(NodoAVL nodo, String dni) {
        if (nodo == null)
            return null;
        
        int comparacion = dni.compareTo(nodo.paciente.getDni());
        
        if (comparacion == 0)
            return nodo.paciente;
        else if (comparacion < 0)
            return buscar(nodo.izq, dni);
        else
            return buscar(nodo.der, dni);
    }
    
    /**
     * Verifica si el árbol está vacío
     * @return true si está vacío, false en caso contrario
     */
    public boolean estaVacio() {
        return raiz == null;
    }
    
    /**
     * Obtiene todos los pacientes ordenados por DNI (recorrido in-order)
     * @return Lista de pacientes ordenados
     */
    public List<Paciente> obtenerPacientesOrdenados() {
        List<Paciente> pacientes = new ArrayList<>();
        recorridoInOrder(raiz, pacientes);
        return pacientes;
    }
    
    /**
     * Recorrido in-order que almacena los pacientes en una lista
     * @param nodo El nodo actual
     * @param pacientes La lista donde almacenar los pacientes
     */
    private void recorridoInOrder(NodoAVL nodo, List<Paciente> pacientes) {
        if (nodo != null) {
            recorridoInOrder(nodo.izq, pacientes);
            pacientes.add(nodo.paciente);
            recorridoInOrder(nodo.der, pacientes);
        }
    }
    
    /**
     * Recorrido in-order que imprime los pacientes en consola
     * @param nodo El nodo actual
     */
    public void recorridoInOrder(NodoAVL nodo) {
        if (nodo != null) {
            recorridoInOrder(nodo.izq);
            System.out.print(nodo.paciente.getDni() + " - " + 
                           nodo.paciente.getNombre() + " " + 
                           nodo.paciente.getApellidoPaterno() + " | ");
            recorridoInOrder(nodo.der);
        }
    }
    
    /**
     * Imprime el árbol en orden
     */
    public void imprimirArbol() {
        System.out.println("Recorrido in-order del árbol AVL:");
        recorridoInOrder(raiz);
        System.out.println();
    }
    
    /**
     * Obtiene la altura del árbol
     * @return La altura del árbol
     */
    public int obtenerAltura() {
        return altura(raiz);
    }
    
    /**
     * Carga pacientes desde una lista enlazada
     * @param listaPaciente La lista de pacientes
     */
    public void cargarDesdeLista(ListaPaciente listaPaciente) {
        raiz = null; // Limpiar el árbol
        ListaPaciente.NodoPaciente actual = listaPaciente.getCabeza();
        
        while (actual != null) {
            insertar(actual.paciente);
            actual = actual.siguiente;
        }
    }
    
    /**
     * Obtiene el número total de nodos en el árbol
     * @return El número de nodos
     */
    public int obtenerCantidadNodos() {
        return contarNodos(raiz);
    }
    
    /**
     * Método recursivo para contar nodos
     * @param nodo El nodo actual
     * @return El número de nodos en el subárbol
     */
    private int contarNodos(NodoAVL nodo) {
        if (nodo == null) return 0;
        return 1 + contarNodos(nodo.izq) + contarNodos(nodo.der);
    }
}
