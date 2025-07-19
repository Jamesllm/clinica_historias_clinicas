# Sistema de Historias Clínicas - Clínica

Este sistema permite registrar pacientes, gestionar usuarios (personal de salud), registrar historias clínicas, emitir comprobantes y mantener una jerarquía clínica con estructuras de datos avanzadas.

## 🏥 Funcionalidades Principales

- **Gestión de Pacientes**: Registro, actualización y búsqueda de pacientes
- **Atención Médica**: Sistema de cola para atención de pacientes
- **Consultas Médicas**: Registro de diagnósticos y tratamientos
- **Comprobantes de Pago**: Gestión de pagos con estructura de pila
- **Búsqueda Avanzada**: Implementación de árboles ABB y AVL para búsqueda eficiente
- **Autenticación**: Sistema de login para personal médico

## 🛠️ Tecnologías Utilizadas

- **Java (POO)**: Programación orientada a objetos
- **PostgreSQL**: Base de datos relacional con schemas
- **Swing**: Interfaz gráfica de usuario
- **Estructuras de Datos**: Implementación de estructuras avanzadas
- **PlantUML**: Diagrama de clases del sistema

## 📊 Estructuras de Datos Implementadas

### 1. **Arreglo Unidimensional** - `ArregloGenero`
- **Propósito**: Almacenar géneros disponibles en el sistema
- **Operaciones**: Carga desde BD, consulta por ID
- **Complejidad**: O(1) para acceso directo

### 2. **Lista Enlazada Simple** - `ListaPaciente`
- **Propósito**: Gestión de pacientes del sistema
- **Operaciones**: Insertar, actualizar, eliminar, buscar, cargar desde BD
- **Complejidad**: O(n) para búsqueda, O(1) para inserción al inicio

### 3. **Lista Enlazada Simple** - `ListaConsultaMedica`
- **Propósito**: Historial de consultas médicas
- **Operaciones**: Insertar, consultar, cargar desde BD
- **Complejidad**: O(n) para búsqueda, O(1) para inserción

### 4. **Cola Dinámica** - `ColaDinamicaPaciente`
- **Propósito**: Sistema de atención de pacientes (FIFO)
- **Operaciones**: Enqueue, Dequeue, Peek, cargar desde BD
- **Complejidad**: O(1) para enqueue/dequeue
- **Aplicación**: Control de turnos de atención médica

### 5. **Pila Dinámica** - `PilaDinamicaComprobante`
- **Propósito**: Gestión de comprobantes de pago (LIFO)
- **Operaciones**: Push, Pop, Peek, cargar desde BD
- **Complejidad**: O(1) para push/pop
- **Aplicación**: Control de pagos y facturación

### 6. **Árbol Binario de Búsqueda (ABB)** - `ArbolABBPaciente`
- **Propósito**: Búsqueda eficiente de pacientes por DNI
- **Operaciones**: Insertar, buscar, recorrido in-order
- **Complejidad**: O(log n) promedio, O(n) peor caso
- **Aplicación**: Búsqueda rápida de pacientes

### 7. **Árbol AVL** - `ArbolAVLPaciente` ⭐ **NUEVO**
- **Propósito**: Búsqueda ultra-eficiente de pacientes por DNI
- **Operaciones**: Insertar, buscar, recorrido in-order, balanceo automático
- **Complejidad**: O(log n) garantizado en todos los casos
- **Características**: 
  - Balanceo automático mediante rotaciones
  - Altura máxima logarítmica
  - Implementa las 4 rotaciones: LL, RR, LR, RL
- **Aplicación**: Búsqueda optimizada de pacientes

## 🗂️ Clases del Sistema

### **Clases de Entidad**
- `Paciente`: Datos personales y médicos del paciente
- `Usuario`: Personal médico del sistema
- `Especialidad`: Especialidades médicas
- `Genero`: Géneros disponibles
- `ConsultaMedica`: Registro de consultas médicas
- `ComprobantePago`: Comprobantes de pago

### **Clases de Vista**
- `Login`: Autenticación de usuarios
- `Aplicacion`: Ventana principal del sistema
- `JD_Atender`: Diálogo para atención de pacientes

### **Clases de Conexión**
- `Conexion`: Gestión de conexión a PostgreSQL
- `LoginService`: Servicio de autenticación

## 🗄️ Base de Datos

### **Script SQL**
Ver archivo `src/conexion/base.sql`

### **Esquemas**
- **public**: Tablas principales del sistema
- **clinica**: Esquema específico para datos clínicos

## 🚀 Instalación y Ejecución

### **Requisitos**
- Java JDK 8 o superior
- PostgreSQL 12 o superior
- NetBeans IDE (recomendado)

### **Pasos de Instalación**

1. **Configurar Base de Datos**
   ```sql
   -- Crear base de datos
   CREATE DATABASE clinica_historias;
   
   -- Ejecutar script de creación
   \i src/conexion/base.sql
   ```

2. **Configurar Conexión**
   - Editar `src/conexion/Conexion.java`
   - Actualizar credenciales de PostgreSQL

3. **Compilar y Ejecutar**
   ```bash
   # Compilar desde NetBeans
   # O desde línea de comandos
   javac -cp "lib/*" src/**/*.java
   
   # Ejecutar
   java -cp "lib/*:src" vista.Login
   ```

## 📈 Ventajas del Sistema

### **Eficiencia en Búsquedas**
- **ABB**: Búsqueda O(log n) promedio
- **AVL**: Búsqueda O(log n) garantizada
- **Comparación**: AVL mantiene mejor balance que ABB

### **Gestión de Memoria**
- **Cola**: Gestión eficiente de turnos
- **Pila**: Control de pagos LIFO
- **Listas**: Flexibilidad en inserción/eliminación

### **Escalabilidad**
- Estructuras dinámicas que crecen según necesidad
- Balanceo automático en árboles AVL
- Persistencia en base de datos

## 👨‍💻 Autor
**Ronal Llapapasca Montes**  
*Universidad Tecnológica del Perú*  
*Algoritmos y Estructuras de Datos - Ciclo I 2025*

## 📝 Notas Técnicas

- **Patrón MVC**: Separación de lógica, vista y datos
- **POO**: Herencia, encapsulamiento, polimorfismo
- **Persistencia**: Conexión directa a PostgreSQL
- **Interfaz**: Swing para GUI moderna

## 🔄 Versiones

- **v1.0**: Implementación básica con ABB
- **v1.1**: Agregado árbol AVL para búsquedas optimizadas
- **v1.2**: Limpieza de código y documentación completa
