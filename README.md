# Sistema de Historias Clínicas - Clínica

Este sistema permite registrar pacientes, gestionar usuarios (personal de salud), registrar historias clínicas, emitir comprobantes y mantener una jerarquía clínica con estructuras de datos avanzadas.

## Tecnologías
- Java (POO)
- PostgreSQL (con schemas)
- Estructuras de datos (pila, lista, cola, árboles)
- PlantUML (diagrama de clases)

## Estructuras implementadas

- Arreglo unidimensional: géneros
- Lista enlazada: pacientes, historial de consultas
- Pila dinámica: comprobantes de pago (falta implementar)
- Cola dinámica: atención de pacientes
- Árbol Binario de Búsqueda (ABB): búsqueda eficiente de pacientes por DNI

## Diagrama de Clases
Ver archivo `diagrama_uml.puml`




## Script SQL
Ver archivo `clinica_pg.sql`

## Ejecutar
1. Crear BD PostgreSQL y correr `clinica_pg.sql`
2. Compilar clases Java
3. Ejecutar `Main`

## Autor
Ronal Llapapasca
